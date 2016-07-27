package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.Record;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Placeholder for input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
 * file buffer and join related structs
 * 
 * @author UoA
 */
public class ScanElement {
	/**
	 * The reader to use
	 */
	private ForwardReader<Record> reader=null;
	/**
	 * The Queue to populate
	 */
	private Queue<JoinElement> queue=null;
	/**
	 * The buffer to use - We don't use a buffer in this 
	 * implementation because we keep only 3 values(OID-ColID-rank) for each
	 * element and not the entire element	 
	 */
	private DiskBuffer fileBuf=null;
	/**
	 * Used for queue synchronization
	 */
	private Object synchThis=null;
	/**
	 * Whether this scanner is still active
	 */
	private boolean active=true;
	/**
	 * The sequence number of the collection this is scanning
	 */
	private short inputID=0;
	/**
	 * The id of the metadata input this is scanning - 0 if it is the content input
	 */
	private short metaInputID=0;
	/**
	 * A counter for the number of elements scanned 
	 */
	private long counter;
	/**
	 * The timeout used by the reader
	 */
	private long timeout;
	/**
	 * The time unit of the timeout used by the reader
	 */
	private TimeUnit timeUnit;
	
	/**
	 * Create a new {@link ScanElement}
	 * 
	 * @param reader Input reader
	 * @param queue The queue to place extacted records
	 * @param synchThis Used for queue synchronization
	 * @param inputID Used to separate the various {@link ScanElement} instances based on their collection
	 * @param metaInputID Used to separate the various {@link ScanElement} instances of a collection based on 
	 * which content/metadata collections they belong
	 */
	public ScanElement(ForwardReader<Record> reader,Queue<JoinElement> queue,Object synchThis,short inputID, short metaInputID, long timeout, TimeUnit timeUnit){
		this.reader=reader;
		this.queue=queue;
		this.synchThis=synchThis;
		this.active=true;
		this.inputID=inputID;
		this.metaInputID=metaInputID;
		this.counter=0;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	/**
	 * Retrieves the counter for this {@link ScanElement}
	 * 
	 * @return The counter
	 */
	public long getCounter() {
		return counter;
	}
	
	/**
	 * Sets the counter of this {@link ScanElement}
	 * 
	 * @param counter The counter
	 */
	public void setCounter(long counter) {
		this.counter = counter;
	}
	
	/**
	 * Retrieves the input id of this {@link ScanElement}
	 * 
	 * @return The input ID
	 */
	public short getInputID() {
		return inputID;
	}

	/**
	 * Sets the input id of this {@link ScanElement}
	 * 
	 * @param inputID The input id
	 */
	public void setInputID(short inputID) {
		this.inputID = inputID;
	}
	
	/**
	 * Retrieves the metadata input id of this {@link ScanElement}
	 * 
	 * @return The input ID
	 */
	public short getMetaInputID() {
		return metaInputID;
	}

	/**
	 * Sets the metadata input id of this {@link ScanElement}
	 * 
	 * @param inputID The input id
	 */
	public void setMetaInputID(short metaInputID) {
		this.metaInputID = metaInputID;
	}

	/**
	 * Checks if the current {@link ScanElement} is active
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets if the current {@link ScanElement} is active
	 * 
	 * @param active <code>true</code> if it is, <code>false</code> otherwise
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Retrieves the file buffer
	 * 
	 * @return The file buffer
	 */
	public DiskBuffer getFileBuf() {
		return fileBuf;
	}

	/**
	 * Sets the file buffer
	 * 
	 * @param fileBuf The file buffer
	 */
	public void setFileBuf(DiskBuffer fileBuf) {
		this.fileBuf = fileBuf;
	}

	/**
	 * Retrieves the {@link RSXMLIterator}
	 * 
	 * @return The iterator
	 */
	public ForwardReader<Record> getReader() {
		return reader;
	}

	/**
	 * Sets the {@link RSXMLIterator}
	 * 
	 * @param iter The iterator
	 */
	public void setReader(ForwardReader<Record> reader) {
		this.reader = reader;
	}
	
	/**
	 * Retrieves the queue used to place records
	 * 
	 * @return The queue
	 */
	public Queue<JoinElement> getQueue() {
		return queue;
	}

	/**
	 * Sets the queue used to place records
	 * 
	 * @param queue The queue
	 */
	public void setQueue(Queue<JoinElement> queue) {
		this.queue = queue;
	}

	/**
	 * Retrieves the object used to synchronize queue access
	 * 
	 * @return The object to use
	 */
	public Object getSynchThis() {
		return synchThis;
	}

	/**
	 * Sets the object used to synchronize queue access
	 * 
	 * @param synchThis The object to use
	 */
	public void setSynchThis(Object synchThis) {
		this.synchThis = synchThis;
	}
	
	/**
	 * Retrieves the timeout used by the reader
	 * 
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * Sets the timeout used by the reader
	 * 
	 * @param timeout the timeout
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Retrieves the time unit of the timeout used by the reader
	 * 
	 * @return the time unit of the timeout
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	
	/**
	 * Sets the time unit of the timeout used by the reader
	 * 
	 * @param timeUnit the unit of the timeout
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
}
