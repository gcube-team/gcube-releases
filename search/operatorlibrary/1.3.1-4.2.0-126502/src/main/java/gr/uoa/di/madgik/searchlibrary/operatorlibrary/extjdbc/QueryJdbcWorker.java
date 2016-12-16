package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker Thread that continues to update the produced {@link ResultSet} in the background
 * 
 * @author UoA
 */
public class QueryJdbcWorker extends Thread{
	/**
	 * The Logger the class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(QueryJdbcWorker.class.getName());
	/**
	 * The writer to use
	 */
	private IRecordWriter<Record> writer = null;
	/**
	 * The locator of the writer associated with the output
	 */
	private URI outLocator = null;
	/**
	 * The result set
	 */
	private ResultSet rs=null;
	/**
	 * The names of the table columns
	 */
	private String []columnNames=null;
	/**
	 * The document identifier
	 */
	private String UniqueDocId=null;
	/**
	 * statistics
	 */
	private StatsContainer stats;
	
	/**
	 * The timeout that will be used by the {@link IRecordWriter} involved in the jdbc operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by the {@link IRecordWriter} involved in the jdbc operation
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Creates a new <code>QueryJdbcWorker</code>
	 * 
	 * @param rs the db {@link ResultSet}
	 * @param columnNames The names of the colums in the db table 
	 * @param UniqueDocId A string that will make the ids of the produced records unique
	 * @param writer The writer to use
	 * @param timeout The timeout to be used both by the writer
	 * @param timeUnit The unit of the timeout to be used
	 * @param stats stats
	 */
	public QueryJdbcWorker(ResultSet rs, String []columnNames, String UniqueDocId, IRecordWriter<Record> writer, long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.rs=rs;
		this.columnNames=columnNames;
		this.UniqueDocId=UniqueDocId;
		this.stats=stats;
		this.writer = writer;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * retrieves the writer this thread is populating
	 * 
	 * @return the writer
	 */
	public URI getLocator(){
		return outLocator;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		long start=Calendar.getInstance().getTimeInMillis();
		try{
			
			stats.timeToFirstInput(0);
			long rowCounter=0;
			long firstInputStop = 0, firstOutputStop = 0;
			while(rs.next()){
				try{
					if(rowCounter == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();
					if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
						logger.info("Consumer side stopped consumption. Stopping.");
						break;
					}
		
					Record rec = new GenericRecord();
					Field[] fields = new Field[2];
					fields[0] = new StringField(UniqueDocId + Long.toString(rowCounter));
					fields[1] = new StringField(JdbcResultElement.rs2XML(columnNames,rs));
					rec.setFields(fields);

					if(!writer.put(rec, timeout, timeUnit) ) {
						if(writer.getStatus() == IBuffer.Status.Open)
							logger.warn("Consumer has timed out");
						break;
					}
					rowCounter++;
					if(rowCounter == 1)
						firstOutputStop = Calendar.getInstance().getTimeInMillis();
				}catch(Exception e){
					logger.error("Could not add record. Continuing", e);
				}
			}
			long closestop=Calendar.getInstance().getTimeInMillis();
			stats.timeToComplete(closestop - start);
			stats.timeToFirstInput(firstInputStop - start);
			stats.timeToFirst(firstOutputStop - start);
			stats.producedResults(rowCounter);
			stats.productionRate((((float)rowCounter/(float)(closestop-start))*1000));
			logger.info("JDBC OPERATOR:\nProduced first result in "+(firstOutputStop-start)+" milliseconds\n" +
					"Produced last result in "+(closestop-start)+" milliseconds\n" +
					"Production rate was "+(((float)rowCounter/(float)(closestop-start))*1000)+" records per second");
		}catch(Exception e){
			logger.error("Error while background populating. Closing",e);
		}finally {
			try{ writer.close(); }catch(Exception ee){ }
		}
	}
}
