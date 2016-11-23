package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.seqgenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.http.HTTPWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

import java.net.URI;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeqGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(SeqGeneratorWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer = null;
	private URI outLocator = null;
	private int count = 0;
	private boolean RS = false;
	private boolean singleField = false;
	private long timeout = 0;
	private TimeUnit timeUnit = null;
	
	/**
	 * Append a random string to a field of some of the records
	 */
	private boolean noise = false;
	/**
	 * Which field to append noise to
	 */
	private int noiseField = 0;
	String[] fieldNames = null;
	int id;
	Integer seed = null;
	/**
	 * Used to synchronize writer retrieval
	 */
	private Object synchWriter=null;
	
	public SeqGeneratorWorker(int count, boolean RS, ProxyType proxyType, boolean singleField, boolean noise, int noiseField, String[] fieldNames,
			int id, Integer seed, long timeout, TimeUnit timeUnit, Object synchWriter) throws Exception {
	    this.count = count;
	    this.RS = RS;
	    this.singleField = singleField;
	    this.fieldNames = fieldNames;
	    this.noise = noise;
	    this.noiseField = noiseField;
	    this.id = id;
	    this.seed = seed;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	 
	    RecordDefinition[] defs=null;
	    if(this.singleField == false) {
	    	FieldDefinition[] fieldDefs = new StringFieldDefinition[fieldNames.length];
	    	for(int i = 0; i < fieldNames.length; i++)
	    	fieldDefs[i] = new StringFieldDefinition(fieldNames[i]);
	    //	stringFieldDef.setCompress(true);
	    	defs = new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)};
	    }else
	    	defs = new RecordDefinition[]{new GenericRecordDefinition(new FieldDefinition[]{new StringFieldDefinition()})};
	    
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
	    	break;
	    }
	    
	    writer = new RecordWriter(producerProxy, defs, 100,
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
		outLocator = writer.getLocator();
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
		Random rnd = null;
		if(seed != null)
			rnd = new Random(seed);
		else
			rnd = new Random(Calendar.getInstance().getTimeInMillis());
		int rc = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		
		synchronized(this.synchWriter){
			this.synchWriter.notify();
		}
		
		int eventsEmitted = 0;
		boolean finalEmitted = false;
		
		try {
			while(rc < count) {
				if(rc==1)
					logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
			
				StringBuilder record = new StringBuilder();
				Hashtable<String, String> attrs = new Hashtable<String, String>();
				String docId =  ((Integer)this.id).toString() + ":" + ((Integer)rc).toString();
				String colId = "TestCol";
				String rank = ((Float)rnd.nextFloat()).toString();
				if(RS == true) {
					record.append("<RSRecord ");
					record.append("DocID=\"" + attrs.get("DocID") + "\" ");
					record.append("CollID=\"" + attrs.get("CollID") + "\"");
					record.append("\" RankID=\"" + attrs.get("RankID"));
					record.append("\">");
				}
				else
					record.append("<record>");
			
				Field[] fields = null;
				if(!singleField)
					fields = new Field[this.fieldNames.length];
				for(int f = 0; f < fieldNames.length; f++) {
					record.append("<" + fieldNames[f] + ">");
					int rndLength = rnd.nextInt(50) + 1;
					record.append(fieldNames[f]);
					String payload = "";
					payload += fieldNames[f] + rc;
					if(noise == true && f == noiseField && rnd.nextInt(100) > 60)
						payload += ("_" + rnd.nextInt(4000));
					record.append(payload);
					record.append("</" + fieldNames[f] + ">");
					if(!singleField)
						fields[f] = new StringField(payload);
				}
				if(RS == false)
					record.append("</record>");
				
				Record outRec = null;
				
				if(!singleField) {
					outRec = new GenericRecord();
					outRec.setFields(fields);
				}else
				{
					outRec = new GenericRecord();
					outRec.setFields(new Field[]{new StringField(record.toString())});
				}
	
				if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Sequence generator #" + id + " stopping prematurely");
					System.out.println("Consumer side stopped consumption. Sequence generator #" + id + " stopping prematurely");
					break;
				}
				
				if(eventsEmitted < 0) {
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
						System.out.println("Consumer side stopped consumption. Sequence generator #" + id + " stopping prematurely");
						break;
					}
				}
				
				rc++;
			}
			logger.info("Data generation took "+(Calendar.getInstance().getTimeInMillis()-now));
		}catch(Exception e) {
			logger.error("Error while generating sequence", e);
		}finally {
			try {
				writer.close();
			}catch(Exception e) { }
		}

	}
}
