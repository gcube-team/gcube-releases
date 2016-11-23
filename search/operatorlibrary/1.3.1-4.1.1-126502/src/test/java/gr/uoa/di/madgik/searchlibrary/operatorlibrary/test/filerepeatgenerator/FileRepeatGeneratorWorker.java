package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.filerepeatgenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRepeatGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(FileRepeatGeneratorWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private RecordWriter<Record> writer = null;
	private URI outLocator = null;
	private int count = 0;
	private String fileName = null;
	private int id;
	private long timeout;
	private TimeUnit timeUnit;
	private Float threshold;
	private File outFile = null;
	
	/**
	 * Used to synchronize writer retrieval
	 */
	private Object synchWriter=null;
	
	public FileRepeatGeneratorWorker(int count, String fileName, int id, long timeout, TimeUnit timeUnit, Float threshold, File outFile, Object synchWriter) throws Exception {
	    this.count = count;
	    this.fileName = fileName;
	    this.id = id;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	    this.threshold = threshold != null ? threshold : RecordWriter.DefaultThreshold;
	    this.outFile = outFile;

	    if(this.threshold < 0.0f || this.threshold > 1.0f)
	    	throw new Exception("Invalid threshold value");
	 
//	    TCPConnectionManager.Init(new TCPConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//	    TCPWriterProxy producerProxy = new TCPWriterProxy();
	    LocalWriterProxy producerProxy = new LocalWriterProxy();
	    RecordDefinition[] defs;
	    FileFieldDefinition fileFieldDef = new FileFieldDefinition("ThisIsTheField");
	    fileFieldDef.setTransportDirective(TransportDirective.Partial);
	    fileFieldDef.setChunkSize(10240);
	    defs = new RecordDefinition[]{new GenericRecordDefinition((new FieldDefinition[] {fileFieldDef}))};
	    
	    if(threshold == null) {
	    	RecordWriter<Record> baseWriter = new RecordWriter<Record>(producerProxy, defs, 100, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
	    	writer = baseWriter;
	    	//writer = new TimeoutBasedRecordWriter<Record>(baseWriter);
	    	//((TimeoutBasedRecordWriter<Record>)writer).setTimeOut(timeout, timeUnit);
	    	//writer = new HeartbeatRecordWriter<Record>(writer);
	    	
	    }else {
	    	RecordWriter<Record> baseWriter = new RecordWriter<Record>(producerProxy, defs, 100, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor/*, this.threshold*/);
	    	writer = baseWriter;
	    	//writer = new HeartbeatRecordWriter<Record>(new ThresholdRecordWriter<Record>(writer));
	    	//((ThresholdRecordWriter<Record>)writer).setCapacityFillFactor(0.7f);
	    }
		
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
		this.setName("FileRepeatGeneratorWorker #" + id);
		int rc = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		Random rnd = new Random(47);
		BufferedWriter out = null;
		int thresholdRecs = 0;
		Object thresholdSync = null;
		
		if(outFile != null) {
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
			
			while(rc < count) {
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
	
				Record outRec;
				outRec = new GenericRecord();
				FileField outField = new FileField(new File(fileName));
				outRec.setFields(new Field[]{outField});
			
				
				try {
					long startpout=System.currentTimeMillis();
					
					if(!writer.put(outRec, 60, TimeUnit.SECONDS)) {
						logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
						continue;
					}
//					if(threshold == null) {
//						try {
//							resultWriter.write(outRec);
//						}catch(GRS2WriterException e) {
//
//						}
////						if(!writer.put(outRec,this.timeout, this.timeUnit))
////						{
////							logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords() + " time elapsed = " + (System.currentTimeMillis() - startpout));
////							continue;
////							//System.out.println("Could not write record " + rc + ". Skipping. "+writer.availableRecords()+" - "+(System.currentTimeMillis()-startpout));
////						}
//					}else {
//						synchronized(thresholdSync) {
//							if(!( writer.availableRecords() > thresholdRecs))
//								System.out.println("WRITER WILL NOT WAIT FOR THRESHOLD CONDITION");
//							while(writer.getStatus() == Status.Open && writer.availableRecords() > thresholdRecs) {
//								System.out.println("WRITER WAITING FOR THRESHOLD CONDITION");
//								thresholdSync.wait();
//								System.out.println("WRITER NOTIFIED OF THRESHOLD CONDITION");
//							}
//						}
//						if(writer.availableRecords() < writer.getCapacity()) {
//							if(!writer.put(outRec)) {
//								logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
//								continue;
//								//System.out.println("Could not write record " + rc + ". Skipping. "+writer.availableRecords()+" - "+(System.currentTimeMillis()-startpout));
//							}
//						}else {
//							if(!writer.put(outRec, this.timeout, this.timeUnit)) {
//								logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
//								continue;
//								//System.out.println("Could not write record " + rc + ". Skipping. "+writer.availableRecords()+" - "+(System.currentTimeMillis()-startpout));
//							}
//						}
						
				//	System.out.println("WRITER: AVAILABLE RECORDS: " + writer.availableRecords());
						
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
						out.write(((FileField)outRec.getField(0)).getPayload().getPath());
						out.newLine();
					}catch(Exception e) {
						logger.warn("Could not persist record to output file", e);
					}
//					try {
//						TimeUnit.MILLISECONDS.sleep(4000);
//					}catch(InterruptedException e) {}
				}
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
					logger.warn("Could not close output file", e);
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
