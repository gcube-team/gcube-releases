package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to iterate over an input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
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
	 * The name of the {@link Field} containing the object id
	 */
	String objectIdFieldName = null;
	/**
	 * The name of the {@link Field} containing the colllection
	 */
	String collectionFieldName = null;
	/**
	 * The name of the {@link Field} containing the rank
	 */
	String rankFieldName = null;
	
	/**
	 * Creates a new {@link ReaderScan}
	 * 
	 * @param scan Info on the input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * and the buffer file
	 * @param objectIdFieldName The name of the {@link Field} containing the object id
	 * @param collectionFieldName The name of the {@link Field} containing the collection
	 * @param rankFieldName The name of the {@link Field} containing the rank
	 */
	public ReaderScan(ScanElement scan, String objectIdFieldName, String collectionFieldName, String rankFieldName){
		this.scan=scan;
		this.objectIdFieldName = objectIdFieldName;
		this.collectionFieldName = collectionFieldName;
		this.rankFieldName = rankFieldName;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			long timeout = 0;
			TimeUnit timeUnit = null;
			synchronized (scan.getSynchThis()){
				scan.setActive(true);
				scan.getSynchThis().notify();
				timeout = scan.getTimeout();
				timeUnit = scan.getTimeUnit();
			}
			ForwardReader<Record> reader = scan.getReader();
			long currentRecordIndex = 0;
			while(true){
				try{
					//reader.seek(currentRecordIndex-reader.currentRecord());
					Record tmp = reader.get(timeout, timeUnit);
					if(tmp == null) {
						if(reader.getStatus() == Status.Open) 
							logger.warn("Producer has timed out");
						break;
					}
					currentRecordIndex = reader.currentRecord();
					
					String value=null;
					double rank = 0.0;
					try{
						 value = ((StringField)tmp.getField(this.objectIdFieldName)).getPayload();
						 rank = Double.parseDouble(((StringField)tmp.getField(this.rankFieldName)).getPayload()); 
					}catch(Exception e){}
					if(value!=null){
						synchronized (scan.getSynchThis()){
							scan.setCounter(scan.getCounter() + 1);
							scan.getQueue().offer(new JoinElement(value,rank,scan.getInputID(),scan.getMetaInputID()));
							scan.getSynchThis().notifyAll();
						}
					}
				}catch(Exception e){
					logger.error("Could not retrieve the record. Continuing",e);
				}
			}
		}catch(Exception e){
			logger.error("Could not scan entire reader. Closing",e);
		}finally {
			synchronized (scan.getSynchThis()){
				scan.setActive(false);
				scan.getSynchThis().notifyAll();
			}
		}
	}
}
