package gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

public class TransformWorker<T extends Record> extends Thread {

	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(TransformWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer;
	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;
	/**
	 * The name of the field containing the payload which will be transformed
	 */
	private String payloadFieldName = null;
	/**
	 * The XSLT to be used for transformation
	 */
	private String xslt = null;
	
	/**
	 * Statistics
	 */
	private StatsContainer stats = null;
	
	/**
	 * The timeout that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the transform operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link IRecordWriter} and the {@link IRecordReader} involved in the transform operation
	 */
	private TimeUnit timeUnit;
	
	/**
	 * 
	 * @param reader The {@link IRecordReader} to consume records from
	 * @param writer The {@link IRecordWriter} which will be used to write the output
	 * @param payloadFieldName The name of the {@link Field} containing the payload on which the transformation will be applied
	 * @param xslt The XSLT to apply on each record field
	 * @param stats Statistics
	 * @param timeout The timeout which will be used both by the reader and the writer
	 * @param timeUnit The unit of the timeout which will be used
	 */
	public TransformWorker(IRecordReader<T> reader, IRecordWriter<Record> writer, String payloadFieldName, String xslt, StatsContainer stats, long timeout, TimeUnit timeUnit) {
	    this.reader = reader;
	    this.writer = writer;
	    this.payloadFieldName = payloadFieldName;
	    this.xslt = xslt;
	    this.stats = stats;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	}
	
	/**
	 * Performs the transform operation
	 */
	public void run() {
		int rc = 0;
		long firstInputStop = 0, firstOutputStop = Calendar.getInstance().getTimeInMillis();
		long start = Calendar.getInstance().getTimeInMillis();
		
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer tr = tf.newTransformer(new StreamSource(new ByteArrayInputStream(xslt.getBytes())));
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
					
					String transformed = null;
					if(payload != null) {
	 					StringWriter strWriter = new StringWriter();
						tr.transform(new StreamSource(new StringReader(payload)), new StreamResult(strWriter));
						transformed = strWriter.toString();
						((StringField)rec.getField(this.payloadFieldName)).setPayload(transformed);
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
					logger.error("Could not process record. Continuing", e); 
				}
			}
		}catch(Exception e) {
			logger.error("Error during background transformation. Closing", e);
		}finally{
			try{
				writer.close();
				reader.close();
			}catch(Exception ee){ }
		}
		long closestop = Calendar.getInstance().getTimeInMillis();
		stats.timeToComplete(closestop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float)rc/(float)(closestop - start))*1000);
		logger.info("TRANSFORM OPERATOR:" + "Produced first result in " + (firstOutputStop - start) + " milliseconds\n"  +
				"Produced last result in "+(closestop - start)+" milliseconds\n" +
				"Produced " + rc + " results\n" + 
				"Production rate was "+(((float)rc/(float)(closestop - start))*1000)+" records per second");
	}
}
