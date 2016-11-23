package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to perform the join operation
 * 
 * @author UoA
 */
public class JoinWorker extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(JoinWorker.class.getName());
	/**
	 * The iterator on the left input
	 */
	private RandomReader<Record> leftReader=null;
	/**
	 * The iterator on the right input
	 */
	private RandomReader<Record> rightReader=null;
	/**
	 * The name of the {@link Field} originating from the left {@link RandomReader} and containing the key to base the join on
	 */
	private String leftKeyFieldName = null;
	/**
	 * The name of the {@link Field} originating from the right {@link RandomReader} and containing the key to base the join on
	 */
	private String rightKeyFieldName = null;
	/**
	 * The indices of the join keys in the {@link RecordDefinition} of the producer
	 */
	private int[] keyIndices;
	/**
	 * The writer to use
	 */
	private IRecordWriter<Record> writer=null;
	/**
	 * The record generation policy that should be followed
	 */
	private RecordGenerationPolicy recordGenerationPolicy = null;
	/**
	 * The resolver used to get suitable producer record definition indices
	 */
	private DefinitionIndexResolver defResolver = null;
	/**
	 * The timeout to be used both by the {@link RandomReader}s and the {@link IRecordWriter}
	 */
	private long timeout;
	/**
	 * The time unit of the timeout that will be used
	 */
	private TimeUnit timeUnit = null;
	/**
	 * Used for synchronization
	 */
	private Object synchThis=null;
	/**
	 * The number of joined elements
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
	 * A unique identifier for this operation
	 */
	private String uid = null;
	
	/**
	 * The class whose constructor is going to be used to create new records based on the input record definitions.
	 * Initialized the first time a pair of results is retrieved and used in all subsequent operations
	 */
	private Class<?> recordClass = null;
	

	/**
	 * Creates a new {@link JoinWorker}
	 * 
	 * @param writer The {@link IRecordWriter} to write records to
	 * @param leftReader The reader for the left input
	 * @param rightReader The reader for the right input
	 * @param leftKeyFieldName The name of the {@link Field} originating from the left {@link RandomReader} and containing the key to base the join on
	 * @param rightKeyFieldName The name of the {@link Field} originating from the right {@link RandomReader} and containing the key to base the join on
	 * @param keyIndex The index of the join key in the {@link RecordDefinition} of the producer
	 * @param recordGenerationPolicy The record generation policy that should be used
	 * @param timeout The timeout to be used both by the {@link RandomReader}s and the {@link IRecordWriter}
	 * @param timeUnit The time unit of the timeout that will be used
	 * @param stats statistics
	 * @param uid A unique identifier for this operation
	 */
	public JoinWorker(IRecordWriter<Record> writer, RandomReader<Record> leftReader, RandomReader<Record> rightReader, String leftKeyFieldName, String rightKeyFieldName, 
			DefinitionIndexResolver defResolver, int[] keyIndices, RecordGenerationPolicy recordGenerationPolicy, long timeout, TimeUnit timeUnit, StatsContainer stats, String uid){
		this.leftReader = leftReader;
		this.rightReader = rightReader;
		this.leftKeyFieldName = leftKeyFieldName;
		this.rightKeyFieldName = rightKeyFieldName;
		this.writer = writer;
		this.defResolver = defResolver;
		this.keyIndices = keyIndices;
		this.recordGenerationPolicy = recordGenerationPolicy;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.synchThis = new Object();
		this.stats = stats;
		this.uid = uid;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		long start=Calendar.getInstance().getTimeInMillis();
		try{
			leftReader.setWindowSize(1);
			rightReader.setWindowSize(1);
			BooleanHolder stopNotifier = new BooleanHolder();
		//	Queue<JoinElement> queue=new LinkedList<JoinElement>();
			BlockingQueue<JoinElement> queue = new ArrayBlockingQueue<JoinElement>(Math.min(leftReader.getCapacity(), rightReader.getCapacity())-1);
			Queue<EventEntry> events = new ConcurrentLinkedQueue<EventEntry>();
			EventHandler<Record> eventHandler = new EventHandler<Record>(writer, events, 2, 100);
			
			ScanElement scan1=new ScanElement(leftReader, leftKeyFieldName, queue, events, eventHandler, timeout, timeUnit, synchThis, (short)0, stopNotifier);
			ReaderScan scanT1=new ReaderScan(scan1, this.uid);
			scanT1.start();
			ScanElement scan2=new ScanElement(rightReader, rightKeyFieldName, queue, events, eventHandler, timeout, timeUnit, synchThis, (short)1, stopNotifier);
			ReaderScan scanT2=new ReaderScan(scan2, this.uid);
			scanT2.start();
			
			HashJoin join1=new HashJoin();
			HashJoin join2=new HashJoin();
			JoinElement tmp=null;
			boolean stop=false;
			
//			synchronized(this.synchThis) {
//				while(!scan1.isActive())
//					this.synchThis.wait();
//			}
//			synchronized(this.synchThis) {
//				while(!scan2.isActive())
//					this.synchThis.wait();
//			}
			
			long now=Calendar.getInstance().getTimeInMillis();
			while(true){
				synchronized (this.synchThis) {
					if(scan1.hasFinished() && scan2.hasFinished()){
						stop=false;
						break;
					}
					//when scan1 hasn't added any results in the common queue there is no need to continue with scan2
					if(scan1.hasFinished() && scan1.getCounter()==0){
						stop=true;
						break;
					}
					//when scan2 hasn't added any results in the common queue there is no need to continue with scan1
					if(scan2.hasFinished() && scan2.getCounter()==0){
						stop=true;
						break;
					}
				//	if(tmp==null){
				//		this.synchThis.wait(2000);
				//	}
				}
				tmp=queue.poll(500, TimeUnit.MILLISECONDS);
				if(tmp==null) 
					continue;
				eventHandler.propagateEvents();
				if(this.count==0) 
					stats.timeToFirstInput(Calendar.getInstance().getTimeInMillis()-now);
				this.checkJoin(tmp, join1, join2, leftReader, rightReader, synchThis, queue, eventHandler, stopNotifier);
			}
			if(!stop){
				while(!queue.isEmpty()){
					tmp = queue.poll();
					eventHandler.propagateEvents();
					this.checkJoin(tmp, join1, join2, leftReader, rightReader, synchThis, queue, eventHandler, stopNotifier);
				}
			}
		//	logger.info("Join tmp file1 writing time was "+wbuf1.wtime);
		//	logger.info("Join tmp file2 writing time was "+wbuf2.wtime);
		//	logger.info("Join tmp file1 reading time was "+rbuf1.rtime);
		//	logger.info("Join tmp file2 reading time was "+rbuf2.rtime);
		//	rbuf1.clear();
		//	rbuf2.clear();
		//	writer.Flush();
			eventHandler.sendPendingFinalEvents(count);
			try { writer.close(); } catch(Exception e) { }
			try { scan1.getReader().close(); } catch(Exception e) { }
			try { scan2.getReader().close(); } catch(Exception e) { }
			long closestop=Calendar.getInstance().getTimeInMillis();
			logger.info("JOIN OPERATOR " + this.uid + ":\n" + 
					"Time to Complete: " + (closestop - start) + "\n" +
					"Time to First: " + (firststop - start) + "\n" +
					"Production Rate: " + (((float)this.count/(float)(closestop-start))*1000) + " records per second\n" +
					"Produced Results: " + this.count +"\n");
			stats.timeToComplete(closestop-start);
			stats.timeToFirst(firststop-start);
			stats.productionRate((((float)this.count/(float)(closestop-start))*1000));
			stats.producedResults(this.count);
			
		}catch(Exception e){
			logger.error("Error while background joining for " + this.uid + ". Closing", e);
			try { leftReader.close(); } catch(Exception ex) { }
			try { rightReader.close(); } catch(Exception ex) { }
			try { writer.close(); } catch(Exception ex) { }
		}
	}
	
	/**
	 * Checks if the provided elements must be joined and places it in the respective hash table
	 * 
	 * @param tmp The element to check
	 * @param join1 The Hash table for the left input
	 * @param join2 The Hash table for the right input
	 * @param leftReader The left input reader
	 * @param rightReader The right input reader
	 * @param synch An object used to synchronize readers
	 * @param queue The record queue from which records are retrieved
	 * @param eventHandler The event handler used to propagate events
	 * @param stopNotifier An object used to signify the end of processing
	 */
	public void checkJoin(JoinElement tmp,HashJoin join1,HashJoin join2,RandomReader<Record> leftReader, RandomReader<Record> rightReader, Object synch, 
			BlockingQueue<JoinElement> queue, EventHandler eventHandler, BooleanHolder stopNotifier){
		try{
			JoinElement inHash=null;
			if(tmp.getCollectionID()==0){
				join1.add(tmp);
				//System.out.println("Join 1 added " + tmp.getValue().getKey());
				inHash=join2.lookup(tmp);		
				//if(inHash == null) {
				//	System.out.println("join2.lookup is null");
				//}
				//else
				//	System.out.println("join2.lookup not null");
				
				if(inHash!=null){
					try{
						//Retrieve records
//						Vector<Recordo> r1 = new Vector<Record>();
//						Vector<Record> r2 = new Vector<Record>();
//						for(Long pos : tmp.getRecordIndices()) {
//							leftReader.seek(-leftReader.currentRecord() + pos -1);
//							Record rec = leftReader.get();
//							if(rec == null)
//								throw new Exception("Could not retrieve stored record");
//							r1.add(rec);
//						}
//						for(Long pos : inHash.getRecordIndices()) {
//							rightReader.seek(-rightReader.currentRecord() + pos -1);
//							Record rec = rightReader.get();
//							if(rec == null)
//								throw new Exception("Could not retrieve stored record");
//							r2.add(rec);
//						}
						
Outer:					for(int i=0;i<tmp.getRecordIndices().size();i+=1){
							for(int q=0;q<inHash.getRecordIndices().size();q+=1){
								if(this.count==0) 
									firststop=Calendar.getInstance().getTimeInMillis();
								this.count+=1;
								if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
									logger.info("Consumer side of " + this.uid + " stopped consumption. Stopping.");
									queue.clear();
									stopNotifier.set(true);
									break Outer;
								}
								
								synchronized(synch) {
									long leftCurr = leftReader.currentRecord();
									long rightCurr = rightReader.currentRecord();
									leftReader.seek(-leftReader.currentRecord() + tmp.getRecordIndices().get(i) -1);
									Record rec1 = leftReader.get();
									rightReader.seek(-rightReader.currentRecord() + inHash.getRecordIndices().get(q) -1);
									Record rec2 = rightReader.get();
									leftReader.seek(-leftReader.currentRecord() + leftCurr);
									rightReader.seek(-rightReader.currentRecord() + rightCurr);
									Record outRec = getMergedFieldRecord(rec1, rec2);
									outRec.setDefinitionIndex(defResolver.resolveIndex(rec1.getDefinitionIndex(), rec2.getDefinitionIndex()));
							
							//		StringField key1 = (StringField)rec1.getField(leftKeyFieldName);
							//		StringField key2 = (StringField)rec2.getField(rightKeyFieldName);
									StringField outKey = (StringField)outRec.getField(keyIndices[defResolver.resolveIndex(rec1.getDefinitionIndex(), rec2.getDefinitionIndex())]);
									if(outKey != null) {
								//		outKey.setPayload(key1.getPayload() + key2.getPayload());
								//		rec1.unbind(); rec1.makeAvailable();
								//		rec2.unbind(); rec2.makeAvailable();
										rec1.hide();
										rec2.hide();
										
										if(!writer.put(outRec, timeout, timeUnit) ) {
											if(writer.getStatus() == IBuffer.Status.Open)
												logger.warn("Consumer of " + this.uid + " has timed out");
											break;
										}
										eventHandler.increaseProducedRecordCount();
									}
								}
								//Record rec1 = r1.get(i);
								//Record rec2 = r2.get(q);
								

							
							//	writer.addResults(ResultElementGeneric.merge(r1.get(i),r2.get(q)));
							
								//System.out.println("merging: " + ((StringField)r1.get(i).GetFieldByIndex(0)).GetStringPayload() + " + " + ((StringField)r2.get(q).GetFieldByIndex(0)).GetStringPayload());
								//field.SetStringPayload(((StringField)r1.get(i).GetFieldByIndex(0)).GetStringPayload() + ((StringField)r2.get(q).GetFieldByIndex(0)).GetStringPayload());
							//	field.SetStringPayload(mergeXML(r1.get(i).GetGCubeTextPayload(), r2.get(q).GetGCubeTextPayload()));
							
			
							}
						}
						//r1=null;
						//r2=null;
					}catch(Exception e){
						logger.error(this.uid + " could not merge the records. Continuing", e);
					}
				}
			}
			else {
				join2.add(tmp);
				//System.out.println("Join 2 added " + tmp.getValue().getKey());
				inHash=join1.lookup(tmp);
				//if(inHash == null) {
				//	System.out.println("join2.lookup is null");
				//}
				//else
				//	System.out.println("join2.lookup not null");
				if(inHash!=null){
					try{
//						//Retrieve records
//						Vector<Record> r1 = new Vector<Record>();
//						Vector<Record> r2 = new Vector<Record>();
//						for(Long pos : inHash.getRecordIndices()) {
//							leftReader.seek(-leftReader.currentRecord() + pos -1);
//							Record rec = leftReader.get();
//							if(rec == null)
//								throw new Exception("Could not retrieve stored record");
//							r1.add(rec);
//						}
//						for(Long pos : tmp.getRecordIndices()) {
//							rightReader.seek(-rightReader.currentRecord() + pos -1);
//							Record rec = rightReader.get();
//							if(rec == null)
//								throw new Exception("Could not retrieve stored record");
//							r2.add(rec);
//						}
Outer:					for(int i=0;i<inHash.getRecordIndices().size();i+=1){
							for(int q=0;q<tmp.getRecordIndices().size();q+=1){
								if(this.count==0) 
									firststop=Calendar.getInstance().getTimeInMillis();
								this.count+=1;
								if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
									logger.info("Consumer side of " + this.uid + " stopped consumption. Notifying readers to stop.");
									queue.clear();
									stopNotifier.set(true);
									break Outer;
								}
								
								synchronized(synch) {
									long leftCurr = leftReader.currentRecord();
									long rightCurr = rightReader.currentRecord();
									leftReader.seek(-leftReader.currentRecord() + inHash.getRecordIndices().get(i) -1);
									Record rec1 = leftReader.get();
									rightReader.seek(-rightReader.currentRecord() + tmp.getRecordIndices().get(q) -1);
									Record rec2 = rightReader.get();
									leftReader.seek(-leftReader.currentRecord() + leftCurr);
									rightReader.seek(-rightReader.currentRecord() + rightCurr);
									
									//Record rec1 = r1.get(i);
									//Record rec2 = r2.get(q);
									//System.out.println("merging: " + ((StringField)r1.get(i).GetFieldByIndex(0)).GetStringPayload() + " + " + ((StringField)r2.get(q).GetFieldByIndex(0)).GetStringPayload());
								//	field.SetStringPayload(((StringField)r1.get(i).GetFieldByIndex(0)).GetStringPayload() + ((StringField)r2.get(q).GetFieldByIndex(0)).GetStringPayload());
								//	field.SetStringPayload(mergeXML(((StringField)r1.get(i).GetFieldByIndex(0)).GetStringPayload(), ((StringField)r2.get(q).GetFieldByIndex(0)).GetStringPayload()));
									
									Record outRec = getMergedFieldRecord(rec1, rec2);
									outRec.setDefinitionIndex(defResolver.resolveIndex(rec1.getDefinitionIndex(), rec2.getDefinitionIndex()));
								//	StringField key1 = (StringField)rec1.getField(leftKeyFieldName);
								//	StringField key2 = (StringField)rec2.getField(rightKeyFieldName);
									StringField outKey = (StringField)outRec.getField(keyIndices[defResolver.resolveIndex(rec1.getDefinitionIndex(), rec2.getDefinitionIndex())]);
									if(outKey != null) {
											//outKey.setPayload(key1.getPayload() + key2.getPayload());
									//	rec1.makeAvailable(); rec1.unbind();
									//	rec2.makeAvailable(); rec2.unbind();
										rec1.hide();
										rec2.hide();
										
										if(!writer.put(outRec, timeout, timeUnit) ) {
											if(writer.getStatus() == IBuffer.Status.Open)
												logger.warn("Consumer of " + this.uid + " has timed out");
											break;
										}
										eventHandler.increaseProducedRecordCount();
									}
								}
							}
						}
				//		r1=null;
				//		r2=null;
					}catch(Exception e){
						logger.error(this.uid + " could not merge the records. Continuing", e);
					}
				}
			}
		}catch(Exception e){
			logger.error("Could not check join of " + this.uid + ". Continuing", e);
		}
	}
	
	
	private Record getMergedFieldRecord(Record rec1, Record rec2) throws Exception {
		int generatedRecordLength;
		if(recordGenerationPolicy == RecordGenerationPolicy.Concatenate)
			generatedRecordLength = rec1.getFields().length + rec2.getFields().length - 1;
		else if(recordGenerationPolicy == RecordGenerationPolicy.KeepLeft)
			generatedRecordLength = rec1.getFields().length;
		else
			generatedRecordLength = rec2.getFields().length;
		Field[] outFields = new Field[generatedRecordLength];
		int outF = 0;
		if(recordGenerationPolicy == RecordGenerationPolicy.Concatenate || recordGenerationPolicy == RecordGenerationPolicy.KeepLeft) {
			for(int f = 0; f < rec1.getFields().length; f++, outF++)
				outFields[outF] = rec1.getField(f);
		}
		if(recordGenerationPolicy != RecordGenerationPolicy.KeepLeft) {
			for(int f = 0; f < rec2.getFields().length; f++, outF++) {
				Field currField = rec2.getField(f);
				if(recordGenerationPolicy == RecordGenerationPolicy.Concatenate) {
					if(!currField.getFieldDefinition().getName().equals(rightKeyFieldName))
						outFields[outF] = currField;
					else
						outF--;
				}else //keep right
					outFields[outF] = currField;
			}
		}
		if(this.recordClass == null)
			this.recordClass = Class.forName(rec1.getClass().getName());
		Record outRec = (Record)this.recordClass.newInstance();
		outRec.setFields(outFields);
		return outRec;
	}
//	public String mergeXML(String element1, String element2) throws Exception {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		factory.setNamespaceAware(true);
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document document = builder.parse(new InputSource(new StringReader(element1)));
//		Element root = document.getDocumentElement();
//		
//		Document newDoc = builder.newDocument();
//		Element newRoot = newDoc.createElementNS(root.getNamespaceURI(), root.getNodeName());
//		newDoc.appendChild(newRoot);
//		
//		NodeList list = document.getElementsByTagNameNS(root.getNamespaceURI(), root.getNodeName()); 
//		Element element = (Element)list.item(0); 
//		list = element.getChildNodes();
//		for(int i = 0 ; i < list.getLength(); i++) {
//			Node dup = newDoc.importNode(list.item(i), true);
//			newDoc.getDocumentElement().appendChild(dup);
//		}
//		
//		document = builder.parse(new InputSource(new StringReader(element2)));
//		root = document.getDocumentElement();
//		
//		list = document.getElementsByTagNameNS(root.getNamespaceURI(), root.getNodeName()); 
//		element = (Element)list.item(0); 
//		list = element.getChildNodes();
//		for(int i = 0 ; i < list.getLength(); i++) {
//			Node dup = newDoc.importNode(list.item(i), true);
//			newDoc.getDocumentElement().appendChild(dup);
//		}
//		
//        TransformerFactory transfac = TransformerFactory.newInstance();
//        Transformer trans = transfac.newTransformer();
//        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//        trans.setOutputProperty(OutputKeys.INDENT, "no");
//
//        //create string from xml tree
//        StringWriter sw = new StringWriter();
//        StreamResult result = new StreamResult(sw);
//        DOMSource source = new DOMSource(newDoc);
//        trans.transform(source, result);
//        return sw.toString();
//
//	}
}
