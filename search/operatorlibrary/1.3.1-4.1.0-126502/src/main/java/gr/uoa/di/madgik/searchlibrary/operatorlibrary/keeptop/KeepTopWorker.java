package gr.uoa.di.madgik.searchlibrary.operatorlibrary.keeptop;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort.OfflineSortWorker;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepTopWorker<T extends Record> extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(OfflineSortWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<T> writer = null;
	/**
	 * The number of records to keep
	 */
	private int count = 0;
	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;
	/**
	 * Statistics
	 */
	private StatsContainer stats = null;
	
	/**
	 * The timeout that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the keep-top operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the keep-top operation
	 */
	private TimeUnit timeUnit;

	/**
	 * Creates a new {@link KeepTopWorker} which will perform the background keep-top operation
	 * 
	 * @param reader The reader to consume record from
	 * @param writer The writer which will be used for authoring
	 * @param count The number of records to keep
	 * @param stats Statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout to be used
	 */
	public KeepTopWorker(IRecordReader<T> reader, IRecordWriter<T> writer, int count, StatsContainer stats, long timeout, TimeUnit timeUnit) {
	    this.reader = reader;
	    this.count = count;
		this.stats = stats;
		this.writer = writer;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Performs the keep-top operation
	 */
	public void run() {
		int rc = 0;
		long firststop = 0;
		long start = Calendar.getInstance().getTimeInMillis();
	
		while(rc < this.count) {
			try{
				if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
					break;
				
				T rec = reader.get(timeout, timeUnit);
				if(rec == null) {
					if(reader.getStatus() == Status.Open) 
						logger.warn("Producer has timed out");
					break;
				}
				
				if(rc==0) {
					logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-start));
					stats.timeToFirstInput(Calendar.getInstance().getTimeInMillis()-start);
				}
				
				if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Stopping.");
					break;
				}
				
				if(!writer.importRecord(rec, timeout, timeUnit) ) {
					if(writer.getStatus() == IBuffer.Status.Open)
						logger.warn("Consumer has timed out");
					break;
				}
				rc++;
				if(rc==1 )
					firststop = Calendar.getInstance().getTimeInMillis();
			}catch(Exception e) {
				logger.error("Could not retrieve the record. Continuing"); 
			}
		}
		try {
			reader.close();
			writer.close();
		}catch(Exception e) {
			logger.warn("Could not close reader or writer", e);
		}

		long closestop = Calendar.getInstance().getTimeInMillis();
		stats.timeToComplete(closestop-start);
		stats.timeToFirst(firststop-start);
		stats.producedResults(rc);
		stats.productionRate(((float)rc/(float)(closestop-start))*1000);
		logger.info("KEEPTOP OPERATOR:Produced first result in "+(firststop-start)+" milliseconds\n" +
				"Produced last result in "+(closestop-start)+" milliseconds\n" +
				"Produced " + rc + " results\n" + 
				"Production rate was "+(((float)rc/(float)(closestop-start))*1000)+" records per second");

	}
}
