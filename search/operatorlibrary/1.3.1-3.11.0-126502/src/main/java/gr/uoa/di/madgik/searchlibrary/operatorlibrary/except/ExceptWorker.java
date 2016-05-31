package gr.uoa.di.madgik.searchlibrary.operatorlibrary.except;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to perform the set difference operation
 * 
 * @author UoA
 */
public class ExceptWorker extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ExceptWorker.class.getName());
	/**
	 * The iterator on the left input
	 */
	private IRecordReader<Record> leftReader=null;
	/**
	 * The iterator on the right input
	 */
	private IRecordReader<Record> rightReader=null;
	/**
	 * The name of the {@link Field} originating from the left {@link IRecordReaderReader} and containing the key to base the operation on
	 */
	private String leftKeyFieldName = null;
	/**
	 * The name of the {@link Field} originating from the right {@link IRecordReader} and containing the key to base the operation on
	 */
	private String rightKeyFieldName = null;
	/**
	 * The writer to use
	 */
	private IRecordWriter<Record> writer=null;
	/**
	 * The timeout to be used both by the {@link IRecordReader}s and the {@link IRecordWriter}
	 */
	private long timeout;
	/**
	 * The time unit of the timeout that will be used
	 */
	private TimeUnit timeUnit = null;
	/**
	 * The number of produced elements
	 */
	private int count=0;
	/**
	 * Used for timing
	 */
	private long firststop=0;
	/**
	 * statistics
	 */
	private StatsContainer stats;
	

	/**
	 * Creates a new {@link ExceptWorker}
	 * 
	 * @param writer The {@link IRecordWriter} to write records to
	 * @param leftReader The reader for the left input
	 * @param rightReader The reader for the right input
	 * @param leftKeyFieldName The name of the {@link Field} originating from the left {@link IRecordReader} and containing the key to base the operation on
	 * @param rightKeyFieldName The name of the {@link Field} originating from the right {@link IRecordReader} and containing the key to base the operation on
	 * @param timeout The timeout to be used both by the {@link IRecordReader}s and the {@link IRecordWriter}
	 * @param timeUnit The time unit of the timeout that will be used
	 * @param stats statistics
	 */
	public ExceptWorker(IRecordWriter<Record> writer, IRecordReader<Record> leftReader, IRecordReader<Record> rightReader, String leftKeyFieldName, String rightKeyFieldName, 
		 long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.leftReader = leftReader;
		this.rightReader = rightReader;
		this.leftKeyFieldName = leftKeyFieldName;
		this.rightKeyFieldName = rightKeyFieldName;
		this.writer = writer;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats = stats;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		long start=Calendar.getInstance().getTimeInMillis();
		try{
			
			Set<String> exceptSet = new HashSet<String>();
			
			while(true){
				try{
					Record record = null;
		
					if(rightReader.getStatus() == Status.Dispose || (rightReader.getStatus()==Status.Close && rightReader.availableRecords()==0))
						break;
					
					record = rightReader.get(timeout, timeUnit);
					if(record == null) {
						if(rightReader.getStatus() == Status.Open) 
							logger.warn("Producer has timed out");
						break;
					}
					
					StringField keyField = null;
					try{
						keyField = (StringField)record.getField(rightKeyFieldName);
					}catch(Exception e){
						logger.warn("Could not extract value from record ", e);
					}
					
					if(keyField != null)
						exceptSet.add(keyField.getPayload());
				}catch(Exception e){
					logger.error("Could not retrieve the record. Continuing", e);
				}
			}
			
			while(true) {
				try {
					Record record = null;
					
					if(leftReader.getStatus() == Status.Dispose || (leftReader.getStatus()==Status.Close && leftReader.availableRecords()==0))
						break;
					
					record = leftReader.get(timeout, timeUnit);
					if(record == null) {
						if(leftReader.getStatus() == Status.Open) 
							logger.warn("Producer has timed out");
						break;
					}
					
					StringField keyField = null;
					try{
						keyField = (StringField)record.getField(leftKeyFieldName);
					}catch(Exception e){
						logger.warn("Could not extract value from record ", e);
					}
					
					if(keyField == null || !exceptSet.contains(keyField.getPayload())) {
						if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
							logger.info("Consumer side stopped consumption. Stopping.");
							break;
						}
						if(!writer.importRecord(record, timeout, timeUnit) ) {
							if(writer.getStatus() == IBuffer.Status.Open)
								logger.warn("Consumer has timed out");
							break;
						}
						this.count++;
					}
				}catch(Exception e){
					logger.error("Could not retrieve the record. Continuing", e);
				}
			}
		
			try { writer.close(); } catch(Exception e) { }
			try { leftReader.close(); } catch(Exception e) { }
			try { rightReader.close(); } catch(Exception e) { }
			long closestop=Calendar.getInstance().getTimeInMillis();
			logger.info("EXCEPT OPERATOR:");
			logger.info("Time to Complete: " + (closestop - start));
			logger.info("Time to First: " + (firststop - start));
			logger.info("Production Rate: " + (((float)this.count/(float)(closestop-start))*1000) + " records per second");
			logger.info("Produced Results: " + this.count);
			stats.timeToComplete(closestop-start);
			stats.timeToFirst(firststop-start);
			stats.productionRate((((float)this.count/(float)(closestop-start))*1000));
			stats.producedResults(this.count);
			
		}catch(Exception e){
			logger.error("Error while background joining. Closing", e);
			try { writer.close(); } catch(Exception ex) { }
		}
	}
}
