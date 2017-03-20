package gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.CompareTokens;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.comparator.ComparisonMode;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.ComparisonMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to populate the created ResultSet with records after the locator to the result has been sent
 * 
 * @author UoA
 */
public class OfflineSortWorker<T extends Record> implements SortWorker {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(OfflineSortWorker.class.getName());
	/**
	 * The writer to use
	 */
	private IRecordWriter<Record> writer = null;
	/**
	 * The locator of the created writer that will be used by clients
	 */
	URI outLocator = null;
	
	/**
	 * The reader to use
	 */
	private RandomReader<T> reader = null;
	/**
	 * The {@link Field} name of the key to base the sorting on
	 */
	private String keyFieldName=null;
	/**
	 * The sorting order 
	 */
	private short order=0;
	/**
	 * Statistics
	 */
	private StatsContainer stats;
	/**
	 * The comparison method
	 */
	private ComparisonMethod method = null;
	/**
	 * The timeout that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the keep-top operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the keep-top operation
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Constructs an {@link OfflineSortWorker} 
	 * 
	 * @param reader The {@link RandomReader} from which input will be read
	 * @param writer The {@link IRecordWriter} to which output will be written
	 * @param keyFieldName The {@link Field} name of the key based on which the sorting should be performed
	 * @param order The order of the sorting. This should be one of {@link CompareTokens#ASCENDING_ORDER} and {@link CompareTokens#DESCENDING_ORDER}
	 * @param method The method of comparison. This should be one of {@link ComparisonMethod#FULL_COMPARISON}, {@link ComparisonMethod#DETECT_MODE} and {@link ComparisonMethod#PROVIDED_MODE}
	 * @param mode The mode of comparison. This should be one of {@link ComparisonMode#COMPARE_STRINGS}, {@link ComparisonMode#COMPARE_LONGINTS}, {@link ComparisonMode#COMPARE_DOUBLES} and {@link ComparisonMode#COMPARE_DATES}
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout to be used
	 * @param stats statistics
	 */
	public OfflineSortWorker(RandomReader<T> reader, IRecordWriter<Record> writer, String key, short order, ComparisonMethod method, ComparisonMode mode, long timeout, TimeUnit timeUnit, StatsContainer stats) throws Exception {
	    this.reader = reader;
	    reader.setWindowSize(1);
	    this.writer = writer;
		this.keyFieldName = key;
		this.order = order;
		this.stats = stats;
		this.method = method;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	
		logger.info("Comparison method: " + this.method);
		if(method == ComparisonMethod.PROVIDED_MODE) {
			if(mode == null) {
				mode = ComparisonMode.COMPARE_STRINGS;
				CompareTokens.setMode(ComparisonMode.COMPARE_STRINGS);
				logger.warn("Missing mode. Assuming string comparison.");
			}
			else
				CompareTokens.setMode(mode);
			logger.info("Comparison mode: " + CompareTokens.getMode());
		}	
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void run(){
		long start=Calendar.getInstance().getTimeInMillis();
		int count=0;
		try{
			List<SortArrayElement> sortVector=new ArrayList<SortArrayElement>(1000);
			List<SortArrayElement> appendVector=new ArrayList<SortArrayElement>();
		//	buffer=new DiskBuffer();
			
			long now=Calendar.getInstance().getTimeInMillis();
			int rc=0;

			while(true) {
				
				if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
					break;
				
				T record = reader.get(timeout, timeUnit);
				if(record == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer has timed out");
					break;
				}
				
				rc+=1;
				String value = null;
				try{
					if(rc==1) {
						logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
						stats.timeToFirstInput(Calendar.getInstance().getTimeInMillis()-now);
					}
					
					try{
						Field key = record.getField(this.keyFieldName);
						if(key instanceof StringField)
							value = ((StringField)key).getPayload();
					}catch(Exception e){
						logger.warn("Could not extract sort key from record #" + rc + ". Continuing");
					}
					if(value==null)
						appendVector.add(new SortArrayElement(reader.currentRecord(),null));
					else {
						sortVector.add(new SortArrayElement(reader.currentRecord(), value));

						if(method == ComparisonMethod.DETECT_MODE)
							CompareTokens.updateMode(value);

					}
				}catch(Exception e){
					logger.error("Could not retrieve the record. Continuing", e);
				}

				if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Stopping.");
					break;
				}
			}
			
			logger.info("retrieving of results took "+(Calendar.getInstance().getTimeInMillis()-now));
			now=Calendar.getInstance().getTimeInMillis();
	
			if(writer.getStatus() == Status.Open) {
				if(method == ComparisonMethod.DETECT_MODE || method == ComparisonMethod.PROVIDED_MODE) {
					if(order == CompareTokens.ASCENDING_ORDER) 
						Collections.sort(sortVector,new SortAscendingComparator(CompareTokens.getMode()));
					else 
						Collections.sort(sortVector,new SortDescendingComparator(CompareTokens.getMode()));
				}
				else {
					if(order == CompareTokens.ASCENDING_ORDER) 
						Collections.sort(sortVector,new SortAscendingComparator());
					else 
						Collections.sort(sortVector,new SortDescendingComparator());
				}
			}
			logger.info("SORT: sorting vector of "+sortVector.size()+" elements took "+(Calendar.getInstance().getTimeInMillis()-now));
			now=Calendar.getInstance().getTimeInMillis();
			long firststop = 0;
			
			rc = 0;
			Iterator<SortArrayElement> it = sortVector.iterator();
			while( it.hasNext() ) {
				try {
					count++;
					reader.seek(-reader.currentRecord()+it.next().index-1);
					T rec = reader.get();
					if(rec == null)
						throw new Exception("Could not retrieve stored record");
					if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
						logger.info("Consumer side stopped consumption. Stopping.");
						break;
					}
					if(!writer.put(rec, timeout, timeUnit) ) {
						if(writer.getStatus() == IBuffer.Status.Open)
							logger.warn("Consumer has timed out");
						break;
					}
					rc++;
					if(rc == 1)
						firststop = Calendar.getInstance().getTimeInMillis();
				}catch(Exception e) {
					logger.warn("Could not write record " + count, e);
				}
			}
			
			it = appendVector.iterator();
			while( it.hasNext() ) {
				try {
					count++;
					reader.seek(it.next().index);
					T rec = reader.get();
					if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
						logger.info("Consumer side stopped consumption. Stopping.");
						break;
					}
					if(!writer.put(rec, timeout, timeUnit) ) {
						if(writer.getStatus() == IBuffer.Status.Open)
							logger.warn("Consumer has timed out");
						break;
					}
					rc++;
					if(rc == 1)
						firststop = Calendar.getInstance().getTimeInMillis();
				}catch(Exception e) {
					logger.warn("Could not write record" + count, e);
				}
			}	
			
			logger.info("SORT: writing results took "+(Calendar.getInstance().getTimeInMillis()-now));
			long closestop=Calendar.getInstance().getTimeInMillis();
			
			stats.timeToComplete(closestop-start);
			stats.timeToFirst(firststop-start);
			stats.producedResults(rc);
			stats.productionRate(((float)rc/(float)(closestop-start))*1000);
			logger.info("SORT OPERATOR:" +
					"Produced first result in "+(firststop-start)+" milliseconds\n" +
					"Produced last result in "+(closestop-start)+" milliseconds\n" +
					"Produced " + rc + " results\n" + 
					"Production rate was "+(((float)rc/(float)(closestop-start))*1000)+" records per second");
		}catch(Exception e){
			logger.error("Error while background sorting. Closing", e);
		}finally {
			try{
				try { 
					reader.close();
					writer.close();
				}catch(Exception ee) { }
			}catch(Exception ee){
				logger.error("Error while closing. ", ee);
			}
		}
	}
}
