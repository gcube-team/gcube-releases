package gr.uoa.di.madgik.grs.buffer;

import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link QueueBuffer} is the main implementation of the {@link IBuffer} interface. The 
 * {@link QueueBuffer} implements the {@link IBuffer} interface through two underlying queues.
 * Upon initialization, two queues are constructed, each with the capacity dictated by 
 * {@link QueueBuffer#setCapacity(int)}. One of the queues, the <i>forward</i> one is
 * used by the writer whenever a new {@link Record} is created and inserted in the buffer. The
 * other queue, the <i>backward</i> one, is used to place items that have been read by the reader.
 * Whenever a {@link Record} is moved out of the head of the forward queue, it is placed in the 
 * end of the backward queue while records that overflow the head of the backward queue are
 * disposed. The backward buffer's capacity is increased at initialization time by the number
 * of concurrent partial record requests the {@link QueueBuffer#setConcurrentPartialCapacity(int)}
 * method has set. Therefore, when setting the two configuration values, one should make note that
 * the maximum number of {@link Record}s that will at any time be consuming memory resources will
 * be 2 * {@link QueueBuffer#getCapacity()} + {@link QueueBuffer#getConcurrentPartialCapacity()} 
 * 
 * @author gpapanikos
 *
 */
public class QueueBuffer implements IBuffer
{
	private static Logger logger=Logger.getLogger(QueueBuffer.class.getName());
	
	/**
	 * The default buffer capacity. This is the default value for the configuration parameter 
	 * set otherwise though {@link QueueBuffer#setCapacity(int)}
	 */
	public static int DefaultCapacity=50;
	/**
	 * The default value of concurrently served partial {@link Record}s. This is the default value 
	 * for the configuration parameter set otherwise though {@link QueueBuffer#setConcurrentPartialCapacity(int)}
	 */
	public static int DefaultConcurrentPartial=1;
	/**
	 * The default threshold value. This is the default value for the configuration parameter set 
	 * otherwise though {@link QueueBuffer#setNotificationThreshold(float)}
	 */
	public static float DefaultThreshold=0.5f;
	/**
	 * The default inactivity timeout value. This is the default value for the configuration parameter set 
	 * otherwise though {@link QueueBuffer#setInactivityTimeout(long)}
	 */
	public static long DefaultInactivityTimeout=100;
	/**
	 * The default inactivity timeout time unit value. This is the default value for the configuration parameter set 
	 * otherwise though {@link QueueBuffer#setInactivityTimeUnit(TimeUnit)}
	 */
	public static TimeUnit DefaultInactivityTimeUnit=TimeUnit.SECONDS;
	/**
	 * The default mirror buffer scaling factor over the set buffer capacity. This is the default value 
	 * for the configuration parameter set otherwise though {@link QueueBuffer#setMirrorBuffer(int)}
	 */
	public static float DefaultMirrorBufferFactor=0.5f;
	
	private ArrayBlockingQueue<Record> queueForward=null;
	private ArrayBlockingQueue<Record> queueBackward=null;
	
	private int capacity=QueueBuffer.DefaultCapacity;
	private int concurrentPartial=QueueBuffer.DefaultConcurrentPartial;
	private int mirrorBuffer=(int)Math.ceil(QueueBuffer.DefaultCapacity*QueueBuffer.DefaultMirrorBufferFactor);
	private float notificationThreshold=QueueBuffer.DefaultThreshold;
	private TransportDirective directive=TransportDirective.Full;
	private long inactivityTimeout=QueueBuffer.DefaultInactivityTimeout;
	private TimeUnit inactivityTimeUnit=QueueBuffer.DefaultInactivityTimeUnit;
	
	private IMirror mirror=null;
	
	private long totalRecords=0;
	private IBuffer.Status status=Status.Init;
	private String key=null;
	
	private final Object writerThresholdNotificationObject=new Object();
	private final Object readerThresholdNotificationObject=new Object();
	private final Object writerImmediateNotificationObject=new Object();
	private final Object readerImmediateNotificationObject=new Object();
	
	private RecordDefinition[] definitions=null;
	
	private long lastActivityTime=System.currentTimeMillis();
	private boolean simulateActivity=false;
	
	private LinkedList<BufferEvent> eventsFromReader=null;
	private LinkedList<BufferEvent> eventsFromWriter=null;
	
	private IBufferStore store=null;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setMirror(gr.uoa.di.madgik.grs.proxy.mirror.IMirror)
	 */
	public void setMirror(IMirror mirror)
	{
		this.mirror=mirror;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getMirror()
	 */
	public IMirror getMirror()
	{
		return mirror;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getLastActivityTime()
	 */
	public long getLastActivityTime()
	{
		return this.lastActivityTime;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#markSimulateActivity()
	 */
	public synchronized void markSimulateActivity()
	{
		this.lastActivityTime = System.currentTimeMillis();
		this.simulateActivity = true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getSimulateActivity()
	 */
	public synchronized boolean getSimulateActivity()
	{
		boolean status = this.simulateActivity;
		this.simulateActivity = false;
		return status;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getRecordDefinitions()
	 */
	public RecordDefinition[] getRecordDefinitions() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.definitions;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setRecordDefinitions(gr.uoa.di.madgik.grs.record.RecordDefinition[])
	 */
	public void setRecordDefinitions(RecordDefinition[] definitions) throws GRS2BufferInitializationException
	{
		if(this.status!=Status.Init) throw new GRS2BufferInitializationException("Record definitions can only be set before buffer initialization");
		this.definitions=definitions;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getTransportDirective()
	 */
	public TransportDirective getTransportDirective() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.directive;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * @throws GRS2BufferInvalidArgumentException if the provided directive is {@link gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective#Inherit}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setTransportDirective(gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective)
	 */
	public void setTransportDirective(TransportDirective directive) throws GRS2BufferInitializationException,GRS2BufferInvalidArgumentException
	{
		if(this.status!=Status.Init) throw new GRS2BufferInitializationException("Transport directive can only be set before buffer initialization");
		if(directive==TransportDirective.Inherit) throw new GRS2BufferInvalidArgumentException("Transport directive of buffer cannot have the value of "+directive.toString());
		this.directive=directive;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * In case the defined {@link gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective} is {@link gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective#Inherit}, the value is
	 * overridden to {@link gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective#Full}
	 * </p>
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#resolveTransportDirective()
	 */
	public TransportDirective resolveTransportDirective() throws GRS2BufferDisposedException
	{
		switch(this.getTransportDirective())
		{
			case Full:
			case Partial:
			{
				return this.getTransportDirective();
			}
			case Inherit:
			default:
			{
				return TransportDirective.Full;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getCapacity()
	 */
	public synchronized int getCapacity() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.capacity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * @throws GRS2BufferInvalidArgumentException if the provided capacity is less or equal to 0
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setCapacity(int)
	 */
	public synchronized void setCapacity(int capacity) throws GRS2BufferDisposedException,GRS2BufferInitializationException,GRS2BufferInvalidArgumentException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status!=Status.Init) throw new GRS2BufferInitializationException("Capacity can only be set before buffer initialization");
		if(capacity<=0) throw new GRS2BufferInvalidArgumentException("Capacity must be greater than 0");
		this.capacity=capacity;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getConcurrentPartialCapacity()
	 */
	public int getConcurrentPartialCapacity() throws GRS2BufferException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.concurrentPartial;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * @throws GRS2BufferInvalidArgumentException if the provided partial concurrency capacity is less or equal to 0
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setConcurrentPartialCapacity(int)
	 */
	public void setConcurrentPartialCapacity(int capacity) throws GRS2BufferDisposedException,GRS2BufferInitializationException,GRS2BufferInvalidArgumentException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status!=Status.Init) throw new GRS2BufferInitializationException("Capacity can only be set before buffer initialization");
		if(capacity<=0) throw new GRS2BufferInvalidArgumentException("Capacity must be greater than 0");
		this.concurrentPartial=capacity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setMirrorBuffer(int)
	 */
	public void setMirrorBuffer(int size) throws GRS2BufferDisposedException,GRS2BufferInitializationException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status!=Status.Init) throw new GRS2BufferInitializationException("Mirror buffer can only be set before buffer initialization");
		this.mirrorBuffer=size;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getMirrorBuffer()
	 */
	public int getMirrorBuffer() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.mirrorBuffer;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getNotificationThreshold()
	 */
//	public float getNotificationThreshold() throws GRS2BufferDisposedException
//	{
//		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
//		return this.notificationThreshold;
//	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setNotificationThreshold(float)
	 */
//	public void setNotificationThreshold(float threshold) throws GRS2BufferDisposedException, GRS2BufferInvalidArgumentException
//	{
//		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
//		if(this.notificationThreshold<0 || this.notificationThreshold>1) throw new GRS2BufferInvalidArgumentException("Threshold must be between 0 and 1");
//		this.notificationThreshold=threshold;
//	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getInactivityTimeUnit()
	 */
	public TimeUnit getInactivityTimeUnit() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.inactivityTimeUnit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setInactivityTimeUnit(java.util.concurrent.TimeUnit)
	 */
	public void setInactivityTimeUnit(TimeUnit unit) throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		this.inactivityTimeUnit=unit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getInactivityTimeout()
	 */
	public long getInactivityTimeout() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.inactivityTimeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setInactivityTimeout(long)
	 */
	public void setInactivityTimeout(long timeout) throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		this.inactivityTimeout=timeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getWriterThresholdNotificationObject()
	 */
//	public Object getWriterThresholdNotificationObject() throws GRS2BufferDisposedException
//	{
//		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
//		return this.writerThresholdNotificationObject;
//	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getReaderThresholdNotificationObject()
	 */
//	public Object getReaderThresholdNotificationObject() throws GRS2BufferDisposedException
//	{
//		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
//		return this.readerThresholdNotificationObject;
//	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getWriterImmediateNotificationObject()
	 */
	public Object getWriterImmediateNotificationObject() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.writerImmediateNotificationObject;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getReaderImmediateNotificationObject()
	 */
	public Object getReaderImmediateNotificationObject() throws GRS2BufferDisposedException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		return this.readerImmediateNotificationObject;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getKey()
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferInvalidArgumentException if the provided key is null or empty
	 * @throws GRS2BufferInvalidOperationException if the {@link QueueBuffer} has already been set with a key
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setKey(java.lang.String)
	 */
	public void setKey(String key) throws GRS2BufferInvalidArgumentException, GRS2BufferInvalidOperationException
	{
		if(key==null || key.trim().length()==0) throw new GRS2BufferInvalidArgumentException("Key cannot be null or emtpy");
		if(this.key!=null) throw new GRS2BufferInvalidOperationException("Buffer is already assigned with a key");
		this.key=key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method updates the buffer's last activity time
	 * </p>
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * @throws GRS2BufferInvalidArgumentException if the capacity or concurrency partial capacity set is less or 
	 * equal to 0, or the notification threshold is outside the lower and upper bounds of 0 and 1 respectively
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#initialize()
	 */
	public synchronized void initialize() throws GRS2BufferDisposedException, GRS2BufferInitializationException,GRS2BufferInvalidArgumentException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status!=Status.Init) throw new GRS2BufferInitializationException("Initialization can only be performed once");
		if(this.capacity<=0) throw new GRS2BufferInvalidArgumentException("Capacity must be greater than 0");
		if(this.concurrentPartial<=0) throw new GRS2BufferInvalidArgumentException("Concurrent partial capacity must be greater than 0");
		if(this.notificationThreshold<0 || this.notificationThreshold>1) throw new GRS2BufferInvalidArgumentException("Threshold must be between 0 and 1");
		this.queueForward=new ArrayBlockingQueue<Record>(this.capacity);
		this.queueBackward=new ArrayBlockingQueue<Record>(this.capacity+this.concurrentPartial);
		this.eventsFromReader=new LinkedList<BufferEvent>();
		this.eventsFromWriter=new LinkedList<BufferEvent>();
		this.status=Status.Open;
		this.lastActivityTime=System.currentTimeMillis();
		if(this.store!=null) this.store.markActivity();
		if(logger.isLoggable(Level.FINE))logger.log(Level.FINE, "Initialized QueueBuffer with capacity ("+this.capacity+"), threshold ("+this.notificationThreshold+")");
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#getStatus()
	 */
	public synchronized Status getStatus()
	{
		return this.status;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#availableRecords()
	 */
	public synchronized int availableRecords() throws GRS2BufferDisposedException,GRS2BufferInitializationException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status==Status.Init) throw new GRS2BufferInitializationException("Buffer not yet initialized");
		return this.queueForward.size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#totalRecords()
	 */
	public synchronized long totalRecords() throws GRS2BufferDisposedException,GRS2BufferInitializationException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status==Status.Init) throw new GRS2BufferInitializationException("Buffer not yet initialized");
		return this.totalRecords;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#setBufferStore(IBufferStore)
	 */
	public void setBufferStore(IBufferStore store) throws GRS2BufferException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status==Status.Init) throw new GRS2BufferInitializationException("Buffer not yet initialized");
		this.store=store;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method updates the buffer's last activity time <br/>
	 * After successfully adding the provided {@link Record} to the {@link QueueBuffer}'s forward buffer,
	 * the record's {@link Record#bind(IBuffer)} is invoked and the synchronization objects are used
	 * to notify any waiting blocked readers 
	 * </p>
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Open}
	 * @throws GRS2RecordDefinitionException if the provided {@link Record} has not been properly provided with a valid {@link RecordDefinition}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#put(gr.uoa.di.madgik.grs.record.Record)
	 */
	public synchronized boolean put(Record record) throws GRS2BufferException,GRS2RecordDefinitionException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status!=Status.Open) throw new GRS2BufferInitializationException("Records can be added only when buffer is in open state");
		boolean success=this.queueForward.offer(record);
		if(success)
		{
			this.totalRecords+=1;
			record.bind(this);
			this.notifyImmediateReaders();
			if(this.status==Status.Open && this.queueForward.size()>=1) this.notifyThresholdReaders();
		}
		this.lastActivityTime=System.currentTimeMillis();
		if(this.store!=null) this.store.markActivity();
		return success;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method updates the buffer's last activity time <br/>
	 * After successfully retrieving the provided {@link Record} from the {@link QueueBuffer}'s forward buffer,
	 * head and removes it from the queue, it adds it to the tail of the backward queue, removing and disposing
	 * any overflowing {@link Record}. Additionally, the synchronization objects are used
	 * to notify any waiting blocked writers 
	 * </p>
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Open}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#get()
	 */
	public synchronized Record get() throws GRS2BufferDisposedException,GRS2BufferInitializationException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status==Status.Init) throw new GRS2BufferInitializationException("Buffer not yet initialized");
		Record rec=this.queueForward.poll();
		if(rec!=null)
		{
			if(this.queueBackward.remainingCapacity()==0)
			{
				Record recBack=this.queueBackward.poll();
				if(recBack!=null && recBack.isBoundTo(this)) recBack.dispose();
			}
			this.queueBackward.add(rec);
			this.notifyImmediateWriters();
			if(this.status==Status.Open && this.queueForward.remainingCapacity() >= Math.ceil(this.capacity*(1-this.notificationThreshold)) )this.notifyThresholdWriters();
		}
		this.lastActivityTime=System.currentTimeMillis();
		if(this.store!=null) this.store.markActivity();
		return rec;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Both backward and forward queues are checked to locate the requested {@link Record}
	 * </p>
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is still in {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#locate(long)
	 */
	public synchronized Record locate(long recordIndex) throws GRS2BufferDisposedException, GRS2BufferInitializationException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status==Status.Init) throw new GRS2BufferInitializationException("Buffer not yet initialized");
		if(this.queueBackward!=null)
		{
			for(Record item : this.queueBackward) if(item.getID()==recordIndex) return item;
		}
		if(this.queueForward!=null)
		{
			for(Record item : this.queueForward) if(item.getID()==recordIndex) return item;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This method updates the buffer's last activity time <br/>
	 * Any blocking readers or writers are notified using the respective synchronization objects
	 * </p>
	 * 
	 * @throws GRS2BufferDisposedException if the buffer's {@link QueueBuffer#dispose()} has already been invoked
	 * @throws GRS2BufferInitializationException when the buffer's status is still in {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Init}
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#close()
	 */
	public synchronized void close() throws GRS2BufferDisposedException,GRS2BufferInitializationException
	{
		if(this.status==Status.Dispose) throw new GRS2BufferDisposedException("Buffer is disposed");
		if(this.status==Status.Init) throw new GRS2BufferInitializationException("Buffer not yet initialized");
		this.status=Status.Close;
		this.notifyImmediateWriters();
		this.notifyThresholdWriters();
		this.notifyImmediateReaders();
		this.notifyThresholdReaders();
		this.lastActivityTime=System.currentTimeMillis();
		if(this.store!=null) this.store.markActivity();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * If the buffer's status is already {@link IBuffer.Status#Dispose}, then the method returns immediately <br/>
	 * Any {@link Record} in either the backward or forward queues is disposed <br/>
	 * If a {@link IMirror} has been associated with the {@link QueueBuffer}, the {@link IMirror} is disposed <br/>
	 * If a registry key is associated with the {@link QueueBuffer}, the registry's entry is removed <br/>
	 * Any blocked readers or writers, are notified using the respective synchronization objects
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#dispose()
	 */
	public synchronized void dispose()
	{
		if(this.status==Status.Dispose) return;
		this.capacity=0;
		this.totalRecords=0;
		if(this.queueForward!=null)
		{
			for(Record rec : this.queueForward) 
				if(rec.isBoundTo(this)) rec.dispose();
			this.queueForward.clear();
		}
		this.queueForward=null;
		if(this.queueBackward!=null)
		{
			for(Record rec : this.queueBackward) 
				if(rec.isBoundTo(this)) rec.dispose();
			this.queueBackward.clear();
		}
		this.queueBackward=null;
		this.status=Status.Dispose;
		if(this.mirror!=null) this.mirror.dispose();
		GRSRegistry.Registry.remove(this.key);
		this.notifyImmediateWriters();
		this.notifyThresholdWriters();
		this.notifyImmediateReaders();
		this.notifyThresholdReaders();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferInvalidArgumentException if the provided event is null or the source is not recognizable
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#emit(gr.uoa.di.madgik.grs.events.BufferEvent)
	 */
	public synchronized void emit(BufferEvent event) throws GRS2BufferInvalidArgumentException
	{
		if(event==null) throw new GRS2BufferInvalidArgumentException("event cannot be null");
		switch(event.getSource())
		{
			case Reader: { this.eventsFromReader.offer(event); break; }
			case Writer: { this.eventsFromWriter.offer(event); break; }
			default: { throw new GRS2BufferInvalidArgumentException("Unrecogized source of event"); }
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GRS2BufferInvalidArgumentException if the provided source is not recognizable
	 * 
	 * @see gr.uoa.di.madgik.grs.buffer.IBuffer#receive(gr.uoa.di.madgik.grs.events.BufferEvent.EventSource)
	 */
	public synchronized BufferEvent receive(EventSource source) throws GRS2BufferInvalidArgumentException
	{
		switch(source)
		{
			case Reader: { return this.eventsFromReader.poll(); }
			case Writer: { return this.eventsFromWriter.poll(); }
			default: { throw new GRS2BufferInvalidArgumentException("Unrecogized source of event"); }
		}
	}
	
	private void notifyThresholdWriters()
	{
		synchronized (this.writerThresholdNotificationObject)
		{
			this.writerThresholdNotificationObject.notifyAll();
		}
	}
	
	private void notifyThresholdReaders()
	{
		synchronized (this.readerThresholdNotificationObject)
		{
			this.readerThresholdNotificationObject.notifyAll();
		}
	}
	
	private void notifyImmediateWriters()
	{
		synchronized (this.writerImmediateNotificationObject)
		{
			this.writerImmediateNotificationObject.notifyAll();
		}
	}
	
	private void notifyImmediateReaders()
	{
		synchronized (this.readerImmediateNotificationObject)
		{
			this.readerImmediateNotificationObject.notifyAll();
		}
	}
}
