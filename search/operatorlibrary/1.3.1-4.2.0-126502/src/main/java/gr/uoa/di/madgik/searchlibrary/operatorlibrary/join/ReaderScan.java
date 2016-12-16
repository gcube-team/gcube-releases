package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.security.MessageDigest;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to iterate over an input result set
 * retrieve the records it contains and persist them in a temporary file buffer and offer them for join
 * 
 * @author UoA
 */
public class ReaderScan extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ReaderScan.class.getName());
	/**
	 * The element scanner
	 */
	ScanElement scan=null;
	
	/**
	 * A unique identifier for this operation
	 */
	private String uid = null;
	
	/**
	 * Creates a new {@link ReaderScan}
	 * 
	 * @param scan Info on the input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * and the buffer file
	 * @param uid A unique identifier for this operation
	 */
	public ReaderScan(ScanElement scan, String uid){
		this.scan=scan;
		this.uid = uid;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		int rc = 0;
		try{
			BooleanHolder stopNotifier = null;
			long timeout = 0;
			TimeUnit timeUnit = null;
			synchronized (scan.getSynchThis()){
				scan.setActive(true);
				timeout = scan.getTimeout();
				timeUnit = scan.getTimeUnit();
				scan.getSynchThis().notify();
				stopNotifier = scan.getStopNotifier();
			}
		
			RandomReader<Record> reader = scan.getReader();
			MessageDigest algorithm=MessageDigest.getInstance("SHA-1");
			long currentRecordIndex = 0;
			while(true){
				try{
					if(stopNotifier.get() == true) {
						logger.info(this.uid + ": Reader stopping after being notified");
						break;
					}
					Record record = null;
					synchronized(scan.getSynchThis()) {
						if(reader.getStatus() == Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
							break;
						
						//reader.seek(currentRecordIndex-reader.currentRecord());
						record = reader.get(timeout, timeUnit);
						if(record == null) {
							if(reader.getStatus() == Status.Open) 
								logger.warn(this.uid + ": Producer has timed out");
							break;
						}
						rc++;
						currentRecordIndex = reader.currentRecord();
						scan.getEventHandler().increaseReadRecordCount(scan.getInputID());
						BufferEvent ev = reader.receive();
						if(ev!=null) scan.getEventQueue().add(new EventEntry(ev, (int)scan.getInputID()));
					}
					
					String value=null;
					try{
						value = ((StringField)record.getField(scan.getKey())).getPayload();
					}catch(Exception e){
						logger.warn(this.uid + ": Could not extract value from record ", e);
					}
					if(value!=null){
					//	FilePosition pos=scan.getFileBuf().persist(rec);
						algorithm.reset();
//						algorithm.update((new String(tmp.getRecordAttributes(ResultElementGeneric.RECORD_ID_NAME)[0].getAttrValue()+tmp.getRecordAttributes(ResultElementGeneric.RECORD_COLLECTION_NAME)[0].getAttrValue()+value)).getBytes());
						algorithm.update((new String(value)).getBytes());
						byte []digest=algorithm.digest();
						Vector<Long> vec=new Vector<Long>();
						synchronized (scan.getSynchThis()){
							vec.add(currentRecordIndex);
							scan.setCounter(scan.getCounter() + 1);
							scan.getSynchThis().notifyAll();
						}
						scan.getQueue().put(new JoinElement(vec,new HashKey(digest),scan.getInputID()));
					}
				}catch(Exception e){
					logger.error(this.uid + ": Could not retrieve the record. Continuing", e);
				}
			}
		}catch(Exception e){
			logger.error(this.uid + ": Could not scan entire reader. Closing", e);
		}finally {
			logger.trace(this.uid + " read " + rc + " records");
			synchronized (scan.getSynchThis()){
				scan.setActive(false);
				scan.setFinished(true);
				//scan.getSynchThis().notifyAll();
			}
//			try{
//				scan.getFileBuf().close();
//			}catch(Exception e){
//				logger.error("Could not close file buf.", e);
//			}
		}
	}
}
