package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
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
	private RandomReader<Record> reader = null;
	/**
	 * The name of the {@link Field} containing key to use
	 */
	private String keyFieldName = null;
	/**
	 * The Queue to populate
	 */
	private BlockingQueue<JoinElement> queue = null;
	/**
	 * The event queue
	 */
	private Queue<EventEntry> events = null;
	/**
	 * The event handler used to propagate events
	 */
	private EventHandler eventHandler = null;
	/**
	 * The buffer to use
	 */
//	private DiskBuffer fileBuf = null;
	/**
	 * Used for queue synchronization
	 */
	private Object synchThis = null;
	/**
	 * Whether this scanner is still active
	 */
	private boolean active = true;
	/**
	 * Whether this scanner has finished reading
	 */
	private boolean finished = false;
	/**
	 * The id of the input this is scanning
	 */
	private short inputID = 0;
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
	
	private BooleanHolder stopNotifier = null;
	/**
	 * Create a new {@link ScanElement}
	 * 
	 * @param reader Reader of the input
	 * @param keyFieldName The name of the {@link Field} containing the key based on which the join is performed
	 * @param queue The queue to place extacted records
	 * @param events The queue used to propagate events
	 * @param eventHandler The event handler which is used to propagate events
	 * @param timeout The timeout that will be used by the {@link RandomReader}
	 * @param timeUnit The time unit of the timeout that will be used
	 * @param fileBuf The file buffer to use
	 * @param synchThis Used for queue synchronization
	 * @param inputID Used to separate the various {@link ScanElement} instances
	 */
	public ScanElement(RandomReader<Record> reader, String keyFieldName, BlockingQueue<JoinElement> queue, Queue<EventEntry> events, EventHandler eventHandler,
			long timeout, TimeUnit timeUnit, Object synchThis, short inputID, BooleanHolder stopNotifier){
		this.reader = reader;
		this.keyFieldName=keyFieldName;
		this.queue=queue;
		this.events = events;
		this.eventHandler = eventHandler;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
//		this.fileBuf=fileBuf;
		this.synchThis=synchThis;
		this.active=false;
		this.finished = false;
		this.inputID=inputID;
		this.counter=0;
		this.stopNotifier = stopNotifier;
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
	 * Checks if the current {@link ScanElement} has finished authoring
	 */
	public boolean hasFinished() {
		return finished;
	}
	
	/**
	 * Sets if the current {@link ScanElement} has finished authoring
	 * 
	 * @param finished <code>true</code> if is has, <code>false</code> otherwise
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	/**
	 * Retrieves the file buffer
	 * 
	 * @return The file buffer
	 */
//	public DiskBuffer getFileBuf() {
//		return fileBuf;
//	}

	/**
	 * Sets the file buffer
	 * 
	 * @param fileBuf The file buffer
	 */
//	public void setFileBuf(DiskBuffer fileBuf) {
//		this.fileBuf = fileBuf;
//	}

	/**
	 * Retrieves the {@link RandomReader}
	 * 
	 * @return The reader
	 */
	public RandomReader<Record> getReader() {
		return reader;
	}

	/**
	 * Sets the {@link RandomReader}
	 * 
	 * @param reader The reader
	 */
	public void setReader(RandomReader<Record> reader) {
		this.reader = reader;
	}

	/**
	 * Retrieves the key 
	 * 
	 * @return The key
	 */
	public String getKey() {
		return keyFieldName;
	}

	/**
	 * Sets the key
	 * 
	 * @param keyFieldName The name of the {@link Field} containing the key
	 */
	public void setKey(String keyFieldName) {
		this.keyFieldName = keyFieldName;
	}

	/**
	 * Retrieves the queue used to place records
	 * 
	 * @return The queue
	 */
	public BlockingQueue<JoinElement> getQueue() {
		return queue;
	}

	/**
	 * Sets the queue used to place records
	 * 
	 * @param queue The queue
	 */
	public void setQueue(BlockingQueue<JoinElement> queue) {
		this.queue = queue;
	}
	
	/**
	 * Retrieves the queue used to place events
	 * 
	 * @return The event queue
	 */
	public Queue<EventEntry> getEventQueue() {
		return events;
	}
	
	/**
	 * Sets the queue used to place events
	 * 
	 * @param events The queue
	 */
	public void setEventQueue(Queue<EventEntry> events) {
		this.events = events;
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
	 * Retrieves the event handler used to propagate events
	 * 
	 * @return the event handler
	 */
	public EventHandler getEventHandler() {
		return eventHandler;
	}
	
	/**
	 * Sets the event handler used to propagate events
	 * 
	 * @param eventHandler The event handler
	 */
	public void setEventHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
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
	 * Retrieves the synch object used to notify this reader of consumption halt
	 * 
	 * @return The stop synch object
	 */
	public BooleanHolder getStopNotifier() {
		return stopNotifier;
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
