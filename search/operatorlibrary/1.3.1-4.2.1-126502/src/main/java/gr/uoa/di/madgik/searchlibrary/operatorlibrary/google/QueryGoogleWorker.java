package gr.uoa.di.madgik.searchlibrary.operatorlibrary.google;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

/**
 * Worker Thread that continues to update the produced {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} in the background
 * 
 * @author UoA
 */
public class QueryGoogleWorker extends Thread{
	/**
	 * The Logger this class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(QueryGoogleWorker.class.getName());
	/**
	 * The writer to use
	 */
	private IRecordWriter<Record> writer = null;
	/**
	 * Statistics
	 */
	private StatsContainer stats = null;
	/**
	 * The search string
	 */
	private String searchString=null;
	/**
	 * Number of results
	 */
	private int resNo;
	
	/**
	 * The timeout which will be used by the writer
	 */
	private long timeout;
	/**
	 * The time unit of the timeout used by the writer
	 */
	private TimeUnit timeUnit;

	/**
	 * Creates a new <code>QueryGoogleWorker</code>
	 * 
	 * @param searchString The search string to provide goggle with
	 * @param resNo number of results
	 * @param writer The {@link IRecordWriter} which will be used to produce results
	 * @param timeout The timeout which will be used by the writer
	 * @param timeUnit The time unit of the timeout
	 * @param stats statistics1
	 */
	public QueryGoogleWorker(String searchString, int resNo, IRecordWriter<Record> writer, long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.searchString=searchString;
		this.resNo = resNo;
		this.writer = writer;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats = stats;
	}

	/**
	 * Executes the actual search 
	 * 
	 * @param QueryString The query string
	 * @return The results
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private GoogleResultElement[] executeSearch(String query, int resNo) throws Exception
	{
		return GoogleResultsRetriever.getResults(query, resNo);
	}

	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		
		long startTime = Calendar.getInstance().getTimeInMillis();
		long firstOutput = 0;
		try{
			GoogleResultElement[] results = executeSearch(this.searchString, this.resNo);
			int i = 0;
			for(i=0;i<results.length;i+=1){
				if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Stopping.");
					break;
				}
				Record rec = new GenericRecord();
				Field[] fields = new Field[2];
				fields[0] = new StringField(results[i].getKey());
				fields[1] = new StringField(results[i].getPayload());
				rec.setFields(fields);
				
				if(!writer.put(rec, timeout, timeUnit) ) {
					if(writer.getStatus() == IBuffer.Status.Open)
						logger.warn("Consumer has timed out");
					break;
				}
				
				if(i == 1)
					firstOutput = Calendar.getInstance().getTimeInMillis();
			}
			long closeStop = Calendar.getInstance().getTimeInMillis();
			stats.timeToFirstInput(0);
			stats.timeToFirst(firstOutput - startTime);
			stats.timeToComplete(closeStop - startTime);
			stats.productionRate((float)i/(((float)(closeStop - startTime))*1000));
			logger.info("GOOGLE OPERATOR:" +
					"Produced first result in "+(firstOutput - startTime)+" milliseconds\n" +
					"Produced last result in "+(closeStop - startTime)+" milliseconds\n" +
					"Produced " + i + " results\n" + 
					"Production rate was "+(((float)i/(float)(closeStop - startTime))*1000)+" records per second");
			
		}catch(Exception e){
			logger.error("Error while background searching google. Closing",e);
		}
		finally {
			try {  writer.close(); } catch(Exception e) { }
		}
	}
}
