package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.filersgenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
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
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort.OfflineSortWorker;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.FloatGenerator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRSGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(OfflineSortWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private RecordWriter writer = null;
	private URI outLocator = null;
	private int count = 0;
	private int id;
	private long timeout;
	private TimeUnit timeUnit;
	private Float threshold;
	private String path = null;
	private File outFile = null;
	
	/**
	 * Used to synchronize writer retrieval
	 */
	private Object synchWriter=null;
	
	public FileRSGeneratorWorker(String path, int id, ProxyType proxyType, long timeout, TimeUnit timeUnit, Float threshold, File outFile, Object synchWriter) throws Exception {
	    this.path = path;
	    this.id = id;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	    this.threshold = threshold != null ? threshold : RecordWriter.DefaultThreshold;
	    this.outFile = outFile;

	    if(this.threshold < 0.0f || this.threshold > 1.0f)
	    	throw new Exception("Invalid threshold value");
	    
	    IWriterProxy producerProxy = null;
	    FileFieldDefinition fileFieldDef = new FileFieldDefinition();
	    fileFieldDef.setTransportDirective(TransportDirective.Partial);
	    RecordDefinition[] defs = new RecordDefinition[]{new GenericRecordDefinition((new FieldDefinition[] {fileFieldDef}))};

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
	    if(threshold == null)
	    	writer = new RecordWriter<GenericRecord>(producerProxy, defs, 100, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
//	    }else {
//	    	writer = new ThresholdRecordWriter<GenericRecord>(producerProxy, defs, 100, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, this.threshold);
//	    	((ThresholdRecordWriter<GenericRecord>)writer).setCapacityFillFactor(0.7f);
//	    }
//	    
	    
	   
	   
	//    TCPConnectionManager.Init(new TCPConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
	//    TCPConnectionManager.RegisterEntry(new Grs2TCPServerConnManagerEntry());
	//    TCPServerProxy producerProxy = new TCPServerProxy(new TCPConfig(false, false, ProtocolType.ClientPull), null);
		
		outLocator = producerProxy.getLocator();
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
		this.setName("FileRSGeneratorWorker #" + id);
		FloatGenerator fg = new FloatGenerator(null);
		int rc = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		Random rnd = new Random(47);
		BufferedWriter out = null;
		int thresholdRecs = 0;
		Object thresholdSync = null;
		
		if(out != null) {
			try {
				out = new BufferedWriter(new FileWriter(this.outFile));
			} catch (IOException e) {
				logger.warn("Could not open output file", e);
			}
		}
		
		synchronized(this.synchWriter){
			this.synchWriter.notify();
		}
		
		try {

//TODO uncomment when threshold notification is implemented
//			if(threshold != null) {
//				thresholdRecs = (int)Math.ceil(writer.getCapacity()*writer.getThreshold());
//				thresholdSync = writer.getThresholdNotificationObject();
//			}
			
	//		RecordWriter writer = null;
			File dir = new File(this.path);
			File rsFiles[] = dir.listFiles();
			
			if(rsFiles == null) {
				logger.error("Error while listing directory files");
				return;
			}
			
			for(File f: rsFiles) {
			
			//	System.out.println(((BlockingRecordStreamWriter)writer).RemainingNumberOfItemsRequested());
				if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					//logger.info("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
					System.out.println("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
					break;
				}
		
			
				if(rc==1) {
					System.out.println("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
					//logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
				}
				
				Record outRec = new GenericRecord();
				FileField outField = new FileField(f);
				outRec.setFields(new Field[]{outField});

				try {
					long startpout=System.currentTimeMillis();
					
					try {
						writer.put(outRec);
					}catch(GRS2WriterException e) {
						logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords() + " time elapsed = " + (System.currentTimeMillis() - startpout));
						continue;
					}
						
				}catch(GRS2WriterException e) {
					logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords(), e);
					continue;
					//System.out.println("Could not write record " + rc + ". Skipping")
				}catch(Exception e) {
					logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords(), e);
					continue;
					//System.out.println("Could not write record " + rc + ". Skipping");
				}
				rc++;
				
				if(out != null) {
					try {
						out.write(f.getPath());
						out.newLine();
					}catch(Exception e) {
						logger.warn("Could not persist record to output file", e);
					}
				}
//					try {
//						TimeUnit.MILLISECONDS.sleep(4000);
//					}catch(InterruptedException e) {}
			//	System.out.println("Writer wrote rec #" + rc);
			//	System.out.println("Writer: remaining items: " + ((BlockingRecordStreamWriter)writer).RemainingNumberOfItemsRequested());
				if(rc == count)
					break;
//				try {
//					TimeUnit.MILLISECONDS.sleep(rnd.nextInt(300));
//				}catch(InterruptedException e) 	{ }
			}
		//	System.out.println(((BlockingRecordStreamWriter)writer).RemainingNumberOfItemsRequested());
	
			writer.close();
			if(out != null) {
				try {
				out.close();
				}catch(Exception e) {
					logger.warn("Could nnot close output file", e);
				}
			}
		}catch(GRS2WriterException e) {
			logger.warn("Could not close writer");
		}
		long closestop = Calendar.getInstance().getTimeInMillis();
		logger.info("Data generation took "+(closestop-now));
		logger.info("Produced " + rc + " records");
		logger.info("Production rate was " + ((float)rc/(float)(closestop-now))*1000);
	//	System.out.println("Data generation took "+(closestop-now));
	//	System.out.println("Produced " + rc + " records");
	//	System.out.println("Production rate was " + ((float)rc/(float)(closestop-now))*1000);
	//	System.out.flush();
	//	System.err.flush();
	}

}
