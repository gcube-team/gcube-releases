package gr.uoa.di.madgik.searchlibrary.operatorlibrary.filter;

import java.io.StringReader;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

public class FilterWorker<T extends Record> extends Thread {

	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(FilterWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer = null;
	
	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;
	/**
	 * The name of the field containing the payload which will be filtered
	 */
	private String payloadFieldName = null;
	/**
	 * The xPath to use
	 */
	private String xPath = null;
	/**
	 * Statistics
	 */
	private StatsContainer stats = null;
	
	/**
	 * The timeout that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the filter operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the filter operation
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Creates a new {@link FilterWorker} which will perform the background filter operation
	 * 
	 * @param reader The reader to consume record from
	 * @param writer The writer which will be used for authoring
	 * @param payloadFieldName The name of the {@link Field} containing the payload on which the filtering will be applied
	 * @param count The number of records to keep
	 * @param stats Statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout to be used
	 */
	public FilterWorker(IRecordReader<T> reader, IRecordWriter<Record> writer, String payloadFieldName, String xPath, StatsContainer stats, long timeout, TimeUnit timeUnit) {
	    this.reader = reader;
	    this.writer = writer;
	    this.payloadFieldName = payloadFieldName;
	    this.xPath = xPath;
	    this.stats = stats;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	}
	
	/**
	 * Performs the filter operation
	 */
	public void run() {
		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = 0, firstOutputStop = 0;
		int rc = 0;
		
		try {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath;
		XPathExpression expr;
		try {
			xpath = xpf.newXPath();
		    expr = xpath.compile(xPath);
		}catch(Exception ee) {
			logger.error("Could not compile XPath expression.", ee);
			try { writer.close(); } catch(Exception e) { }
			return;
		}

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
				
				String payload = null;
				try{
					Field key = rec.getField(this.payloadFieldName);
					if(key instanceof StringField)
						payload = ((StringField)key).getPayload();
				}catch(Exception e){
					logger.warn("Could not extract payload from record #" + rc + ". Continuing");
					continue;
				}	
				
				String filtered = null;
				if(payload != null) {
					filtered = expr.evaluate(new InputSource(new StringReader(payload)));
					((StringField)rec.getField(this.payloadFieldName)).setPayload(filtered);
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
		logger.info("FILTER OPERATOR:Produced first result in "+(firstOutputStop - start)+" milliseconds\n" +
				"Produced last result in "+(closeStop - start)+" milliseconds\n" +
				"Produced " + rc + " results\n" + 
				"Production rate was "+(((float)rc/(float)(closeStop - start))*1000)+" records per second");

	}
}
