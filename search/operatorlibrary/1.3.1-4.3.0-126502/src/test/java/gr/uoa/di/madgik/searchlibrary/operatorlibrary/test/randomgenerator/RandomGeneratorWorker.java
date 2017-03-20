package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.randomgenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
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
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.FloatGenerator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(RandomGeneratorWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private RecordWriter<Record> writer = null;
	private URI outLocator = null;
	private int count = 0;
	private boolean RS = false;
	private boolean singleField = false;
	private boolean onlyFinalEvent = false;
	private int id;
	private long timeout;
	private TimeUnit timeUnit;
	private Float threshold;
	private Generator<? extends Object>[] fieldGenerators;
	private String[] fieldNames = null;
	private File outFile = null;
	
	/**
	 * Used to synchronize writer retrieval
	 */
	private Object synchWriter=null;

	public RandomGeneratorWorker(int count, boolean RS, ProxyType proxyType, String[] fieldNames, Generator<? extends Object>[] fieldGenerators, boolean singleField, boolean onlyFinalEvent, 
			int id, long timeout, TimeUnit timeUnit, Float threshold, int bufferCapacity, File outFile, Object synchWriter) throws Exception {
	    this.count = count;
	    this.RS = RS;
	    this.fieldNames = fieldNames;
	    this.fieldGenerators = fieldGenerators;
	    this.singleField = singleField;
	    this.onlyFinalEvent = onlyFinalEvent;
	    this.id = id;
	    this.timeout = timeout;
	    this.timeUnit = timeUnit;
	    this.threshold = threshold != null ? threshold : RecordWriter.DefaultThreshold;
	    this.outFile = outFile;

	    if(this.threshold < 0.0f || this.threshold > 1.0f)
	    	throw new Exception("Invalid threshold value");

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
	    
	    RecordDefinition[] defs=null;
	    if(singleField == false) {
	    	FieldDefinition[] fieldDefs = new StringFieldDefinition[fieldNames.length];
	    	for(int i = 0; i < fieldNames.length; i++)
	    	fieldDefs[i] = new StringFieldDefinition(fieldNames[i]);
	    //	stringFieldDef.setCompress(true);
	    	defs = new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)};
	    }else
	    	defs = new RecordDefinition[]{new GenericRecordDefinition(new FieldDefinition[]{new StringFieldDefinition()})};
	    if(threshold == null) {
	    	RecordWriter<Record> baseWriter = new RecordWriter<Record>(producerProxy, defs, bufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
	    	writer = baseWriter;
	    	//writer = new TimeoutBasedRecordWriter<Record>(baseWriter);
	    	//((TimeoutBasedRecordWriter<Record>)writer).setTimeOut(timeout, timeUnit);
	    	//writer = new HeartbeatRecordWriter<Record>(writer);
	    	
	    }else {
	    	RecordWriter<Record> baseWriter = new RecordWriter<Record>(producerProxy, defs, bufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor/*, this.threshold*/);
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
		long closestop;
		long productionTime = -1;
		float productionRate = -1.0f;
		this.setName("RandomGeneratorWorker #" + id);
		FloatGenerator fg = new FloatGenerator(null);
		int rc = 0;
		int eventsEmitted = 0;
		long start = Calendar.getInstance().getTimeInMillis();
		boolean finalEmitted = false;
	//	Random rnd = new Random(47);
		BufferedWriter out = null;
	//	int thresholdRecs = 0;
	//	Object thresholdSync = null;
		
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
			if(fieldGenerators.length != fieldNames.length) {
				logger.error("Field name/type length mismatch");
				writer.close();
				return;
			}

//TODO uncomment when threshold notification is implemented
//			if(threshold != null) {
//				thresholdRecs = (int)Math.ceil(writer.getCapacity()*writer.getThreshold());
//				thresholdSync = writer.getThresholdNotificationObject();
//			}
			
	//		RecordWriter writer = null;
			
			while(rc < count) {
				if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
					System.out.println("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
					break;
				}
		
			
				if(rc==1) {
					System.out.println("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-start));
					//logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-start));
				}
			
				StringBuilder record = new StringBuilder();
				Hashtable<String, String> attrs = new Hashtable<String, String>();
				attrs.put("DocID", ((Integer)this.id).toString() + ":" + ((Integer)rc).toString());
				attrs.put("CollID", "TestCol");
				attrs.put("RankID", fg.next().toString());
	
				if(RS == true) {
					record.append("<RSRecord ");
					record.append("DocID=\""+ attrs.get("DocID") + "\" ");
					record.append("CollID=\"" + attrs.get("CollID") + "\" ");
					record.append("RankID=\"" + attrs.get("RankID") + "\" ");
					record.append("TestAttr=\"" + "foo" + "\"");
					record.append(">");
				}
				else
					record.append("<record>");
				
				Field[] fields = null;	
				if(!singleField)
					fields = new Field[this.fieldGenerators.length];
				for(int f = 0; f < fieldNames.length; f++) {
						record.append("<" + fieldNames[f] + ">");
					//	record.append(fieldNames[f]);
					String payload = fieldGenerators[f].next().toString();
					record.append(payload);
					record.append("</" + fieldNames[f] + ">");
					if(!singleField)
						fields[f] = new StringField(payload);
				}
				
				if(RS == true)
					record.append("</RSRecord>");
				else
					record.append("</record>");
				
				Record outRec=null;
				if(!singleField) {
					outRec = new GenericRecord();
					outRec.setFields(fields);
				}else
				{
					outRec = new GenericRecord();
					outRec.setFields(new Field[]{new StringField(record.toString())});
				}
				try {
	//				long startpout=System.currentTimeMillis();
		
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
					System.out.println("Could not write record " + rc + ". Skipping. Available Record = " + writer.availableRecords());
					continue;
				}catch(Exception e) {
					logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords(), e);
					System.out.println("Could not write record " + rc + ". Skipping. Available Record = " + writer.availableRecords());
					continue;
				}
				
				if(out != null) {
					if(singleField) {
						try {
							//out.write(((GCubeXMLRecord)outRec).downgrade());
							//out.newLine();
							
						}catch(Exception e) {
							logger.warn("Could not persist record to output file", e);
						}
					}else {
						try {
							for(int f = 0; f < fieldNames.length; f++)
								out.write(((StringField)outRec.getField(f)).getPayload() + " ");
							out.newLine();
						}catch(Exception e) {
							logger.warn("Could not persist record to output file", e);
						}
					}
//					try {
//						TimeUnit.MILLISECONDS.sleep(4000);
//					}catch(InterruptedException e) {}
				}
			//	System.out.println("Writer wrote rec #" + rc);
				if(rc == count)
					break;
//				try {
//					TimeUnit.MILLISECONDS.sleep(rnd.nextInt(300));
//				}catch(InterruptedException e) 	{ }
			}
	
		}catch(Exception e) {
			logger.warn("Error while generating random records",e );
		}finally {
			try { 
				closestop = Calendar.getInstance().getTimeInMillis();
				productionTime = closestop-start;
				productionRate = ((float)rc/(float)(closestop-start))*1000;
				BufferEvent productionTimeEvent = new KeyValueEvent("productionTime", Long.toString(productionTime));
				BufferEvent productionRateEvent = new KeyValueEvent("productionRate", Float.toString(productionRate));
				writer.emit(productionTimeEvent);
				writer.emit(productionRateEvent);
				writer.close(); 
			} 
			catch(Exception e) { }
			
			if(out != null) {
				try {
				out.close();
				}catch(Exception e) {
					logger.warn("Could not close output file", e);
				}
			}
		}
		logger.info("Data generation took "+productionTime);
		logger.info("Produced " + rc + " records");
		logger.info("Production rate was " + productionRate);
	//	System.out.println("Data generation took "+(closestop-start));
	//	System.out.println("Produced " + rc + " records");
	//	System.out.println("Production rate was " + ((float)rc/(float)(closestop-start))*1000);
	//	System.out.flush();
	//	System.err.flush();
	}

}
