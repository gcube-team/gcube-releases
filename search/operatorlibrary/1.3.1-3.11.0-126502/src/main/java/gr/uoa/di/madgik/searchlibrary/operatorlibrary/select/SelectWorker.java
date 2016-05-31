package gr.uoa.di.madgik.searchlibrary.operatorlibrary.select;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectWorker<T extends Record> extends Thread {

	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(SelectWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer = null;
	
	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;
	/**
	 * The logical expression
	 */
	private String logicalExpressions = null;

	/**
	 * Statistics
	 */
	private StatsContainer stats = null;
	
	/**
	 * A mask array to be used for column filter/rearrange 
	 */
	private Integer[] mask;

	/**
	 * The timeout that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the filter operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the filter operation
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Creates a new {@link SelectWorker} which will perform the background filter operation
	 * 
	 * @param reader The reader to consume record from
	 * @param writer The writer which will be used for authoring
	 * @param logicalExpressions The name of the {@link Field} containing the payload on which the filtering will be applied
	 * @param stats Statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout to be used
	 */
	public SelectWorker(IRecordReader<T> reader, IRecordWriter<Record> writer, String logicalExpressions, StatsContainer stats, long timeout, TimeUnit timeUnit) {
	    this.reader = reader;
	    this.writer = writer;
	    this.logicalExpressions = logicalExpressions;
	    this.stats = stats;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	}
	
	/**
	 * Creates a new {@link SelectWorker} which will perform the background filter operation
	 * 
	 * @param reader The reader to consume record from
	 * @param writer The writer which will be used for authoring
	 * @param logicalExpressions The name of the {@link Field} containing the payload on which the filtering will be applied
	 * @param mask filter mask to be applied
	 * @param stats Statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout to be used
	 *
	 */
	public SelectWorker(IRecordReader<T> reader, IRecordWriter<Record> writer, String logicalExpressions, Integer[] mask, StatsContainer stats, long timeout, TimeUnit timeUnit) {
		this(reader, writer, logicalExpressions, stats, timeout, timeUnit);
		this.mask = mask;
	}

	
	/**
	 * Performs the filter operation
	 */
	public void run() {
		Thread.currentThread().setName(SelectWorker.class.getName());
		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = 0, firstOutputStop = start;
		int rc = 0;
		
		Map<String, String> bindings = new HashMap<String, String>(); 
		Set<String> refs = new HashSet<String>();
		Binder binder = null;
		if (logicalExpressions != null) {
			binder = new Binder(logicalExpressions, "\\[(\\w+)\\]");
		
			refs = binder.getSubstitutions();
		}
		
		try {

		while(true) {
			try{
				if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
					break;
				
				T rec = reader.get(timeout, timeUnit);
				if(rec == null) {
					if(reader.getStatus() == Status.Open)
						logger.warn("Producer has timed out");
					break;
				}
				
				if(rc==0)
					firstInputStop = Calendar.getInstance().getTimeInMillis();
				
				if(writer.getStatus() == Status.Close || writer.getStatus() == Status.Dispose ) {
					logger.info("Consumer side stopped consumption. Stopping.");
					break;
				}
				
				bindings.clear();
				for (String bind : refs) {
					try{
						Field key = rec.getField(bind);
						if (key == null) {
							if (bind.matches("\\d+"))
								key = rec.getField(Integer.parseInt(bind));
							if (key == null)
								throw new Exception("Referenced field: " + bind + " is null");
						}
						if(key instanceof StringField)
							bindings.put(bind, ((StringField)key).getPayload());
						else
							throw new Exception("Refence: " + key.getFieldDefinition().getName() + " is not a String field");
					}catch(Exception e){
						logger.warn("Could not extract payload from record #" + rc + ". Continuing", e);
						continue;
					}	
				}
				
				if (binder != null && !Evaluator.jsEvaluator(binder.substitute(bindings)))
					continue;
				
				// Rearrange fields in record based on filterMask
				if (mask != null) {
					Field fields[] = new Field[mask.length];
					for (int i = 0; i < mask.length; i++) {
						fields[i] = rec.getField(mask[i]);
					}
					rec.setFields(fields);
				}
				
				if(!writer.importRecord(rec, timeout, timeUnit)){
					if(writer.getStatus() == Status.Open)
						logger.warn("Consumer has timed out");
					break;
				}
		
				rc++;
				if(rc==1)
					firstOutputStop = Calendar.getInstance().getTimeInMillis();
			}catch(Exception e) {
				logger.error("Could not retrieve the record. Continuing", e); 
				}
			}
		}catch(Exception e) {
			logger.error("Error during background transformation. Closing", e);
		}finally {
			try{
				writer.close();
				reader.close();
			}catch(Exception ee){ }
		}
		
		long closeStop = Calendar.getInstance().getTimeInMillis();
		
		stats.timeToComplete(closeStop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float)rc/(float)(closeStop - start))*1000);
		logger.info("SELECT OPERATOR:Produced first result in "+(firstOutputStop - start)+" milliseconds\n" +
				"Produced last result in "+(closeStop - start)+" milliseconds\n" +
				"Produced " + rc + " results\n" + 
				"Production rate was "+(((float)rc/(float)(closeStop - start))*1000)+" records per second");
	}
	
}
