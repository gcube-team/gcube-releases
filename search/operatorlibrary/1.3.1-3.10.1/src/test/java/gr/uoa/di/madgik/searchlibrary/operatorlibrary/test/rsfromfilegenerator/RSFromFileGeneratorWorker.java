package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.rsfromfilegenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.mirror.HTTPWriterMirror;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;

public class RSFromFileGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(RSFromFileGeneratorWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private RecordWriter<Record> writer = null;
	private URI outLocator = null;
	private boolean onlyFinalEvent = false;
	private int id;
	private long timeout;
	private ProxyType proxyType;
	private TimeUnit timeUnit;
	private Float threshold;
	private String inFile;
	
	/**
	 * Used to synchronize writer retrieval
	 */
	private Object synchWriter=null;
	
	public RSFromFileGeneratorWorker(String inFile, boolean onlyFinalEvent, int id, ProxyType proxyType, long timeout, TimeUnit timeUnit, Float threshold, Object synchWriter) throws Exception {
	    this.inFile = inFile;
	    this.onlyFinalEvent = onlyFinalEvent;
	    this.id = id;
	    this.proxyType = proxyType;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	    this.threshold = threshold;

	    if(this.threshold != null && (this.threshold < 0.0f || this.threshold > 1.0f))
	    	throw new Exception("Invalid threshold value");
	
		this.synchWriter = synchWriter;
	}
	
	/**
	 * retrieves the locator of the writer this thread is populating
	 * 
	 * @return the locator of the writer
	 */
	public URI getLocator(){
		return outLocator;
	}
	
	public void run() {
		this.setName("RSFromFileWorker #" + id);
		int rc = 0;
		int eventsEmitted = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		boolean finalEmitted = false;
		
		try {
			IWriterProxy producerProxy = null;
			switch(proxyType)
			{
			case Local:
				producerProxy = new LocalWriterProxy();
				break;
			case TCP:
				producerProxy = new TCPWriterProxy();
				break;
			case HTTP:
				producerProxy = new HTTPWriterProxy();
			}
			
			BufferedReader tmpReader = new BufferedReader(new FileReader(new File(inFile)));
			int count = 0;
			String line = null;
			while((line = tmpReader.readLine()) != null) count++;
			tmpReader.close();
			
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(inFile)));
			
			line = null;
			while((line = fileReader.readLine()) != null) {
				String[] fieldEntries = line.split(" ");
				Map<String, String> fieldValues = new LinkedHashMap<String, String>();
				for(String fieldEntry : fieldEntries) {
					String[] fv = fieldEntry.split("=");
					if(fv.length != 2)
						throw new Exception("Invalid field format: " + fieldEntry);
					fieldValues.put(fv[0], fv[1]);
				}
				if(rc == 0) {
					RecordDefinition[] defs = null;
					FieldDefinition[] fieldDefs = new StringFieldDefinition[fieldValues.size()];
			    	int i = 0;
					for(Map.Entry<String, String> fieldValue : fieldValues.entrySet()) {
			    		fieldDefs[i] = new StringFieldDefinition(fieldValue.getKey());
			    		i++;
					}
			    	defs = new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)};
				    
				    if(threshold == null) {
				    	RecordWriter<Record> baseWriter = new RecordWriter<Record>(producerProxy, defs, 50, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
				    	writer = baseWriter;
				    	
				    }else {
				    	RecordWriter<Record> baseWriter = new RecordWriter<Record>(producerProxy, defs, 50, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor/*, this.threshold*/);
				    	writer = baseWriter;
				    }
					
					outLocator = producerProxy.getLocator();
					
					synchronized(this.synchWriter){
						this.synchWriter.notify();
					}
				}
				
				if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
					System.out.println("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
					break;
				}
				
				if(rc==1) {
					System.out.println("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
					//logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
				}
				
				Record outRec = new GenericRecord();
				Field[] outFields = new Field[fieldValues.size()];
				int i = 0;
				for(Map.Entry<String, String> fieldValue : fieldValues.entrySet()) {
					String outPayload = fieldValue.getValue();
					outPayload = outPayload.replaceAll("%nl", "\n");
					outPayload = outPayload.replace("%%nl", "%nl");
					outFields[i++] = new StringField(outPayload);
				}
				outRec.setFields(outFields);
				
				if(!onlyFinalEvent || eventsEmitted < 10) {
					if(rc % 100 == 0 && rc > 0) {
						writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+rc));
						eventsEmitted++;
					}
				}else if(!finalEmitted) {
					writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNOFINAL_EVENT, ""+count));
					finalEmitted = true;
				}
				
				if(!writer.put(outRec, timeout, timeUnit)) {
					if(writer.getStatus() == Status.Open) {
						logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
						rc++;
						continue;
					}else {
						System.out.println("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
						break;
					}
				}
				rc++;
			}
		}catch(Exception e) {
			logger.warn("Error while generating random records",e );
		}finally {
			try { writer.close(); } 
			catch(Exception e) { }
		}
		long closestop = Calendar.getInstance().getTimeInMillis();
		logger.info("Data generation took "+(closestop-now));
		logger.info("Produced " + rc + " records");
		logger.info("Production rate was " + ((float)rc/(float)(closestop-now))*1000);
	}
}
