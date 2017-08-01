package gr.uoa.di.madgik.grs.reader;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.IReaderProxy;
import gr.uoa.di.madgik.grs.proxy.ProxyFactory;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.utils.ProgressiveTimeoutGenerator;

import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * The {@link ForwardReader} class provides access over an authored {@link IBuffer} in a forward only fashion. The {@link Record}s
 * retrieved can be defined to be cast to the specific {@link Record} extending class
 * 
 * @author gpapanikos
 *
 * @param <T> The type of {@link Record} specialization that is to be returned by the <code>get</code> operations
 */
public class ForwardReader<T extends Record> implements IRecordReader<T>, Iterable<T>
{
	/**
	 * The default timeout to be used by the {@link ForwardReaderIterator}s initialized by this reader when retrieving
	 * records or checking if more records are available. This value should be interpreted in conjunction with the
	 * value of {@link ForwardReader#DefaultIteratorTimeUnit} 
	 */
	public static final int DefaultIteratorTimeout=10;
	/**
	 * The default time unit to be used by the {@link ForwardReaderIterator}s initialized by this reader when retrieving
	 * records or checking if more records are available. This value should be interpreted in conjunction with the
	 * value of {@link ForwardReader#DefaultIteratorTimeout} 
	 */
	public static final TimeUnit DefaultIteratorTimeUnit=TimeUnit.SECONDS;
	private IBuffer buffer=null;
	private IReaderProxy proxy=null;
	private Object immediateNotificationObject=new Object();
	private long iteratorTimeout=ForwardReader.DefaultIteratorTimeout;
	private TimeUnit iteratorTimeUnit=ForwardReader.DefaultIteratorTimeUnit;
	private long currentRecordIndex=-1;
	
	/**
	 * Creates a new reader accessing the {@link IBuffer} referenced by the provided locator
	 * 
	 * @param locator the locator identifying the {@link IBuffer} to consume
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public ForwardReader(URI locator) throws GRS2ReaderException
	{
		try
		{
			this.proxy=ProxyFactory.getProxy(locator);
			this.buffer=this.proxy.getBuffer();
			this.immediateNotificationObject=this.buffer.getReaderImmediateNotificationObject();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to initialize the reader", ex);
		}
	}
	
	/**
	 * Creates a new reader accessing the {@link IBuffer} referenced by the provided locator.
	 * The {@link IBuffer} will have the specified capacity, if possible
	 * 
	 * @param locator the locator identifying the {@link IBuffer} to consume
	 * @param capacity a desired capacity for the {@link IBuffer} which will be consumed. Used as
	 * a hint to the underlying {@link IReaderProxy}, which might or might not honor it depending
	 * on its strategy
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public ForwardReader(URI locator, int capacity) throws GRS2ReaderException
	{
		try
		{
			this.proxy=ProxyFactory.getProxy(locator);
			this.proxy.overrideBufferCapacity(capacity);
			this.buffer=this.proxy.getBuffer();
			this.immediateNotificationObject=this.buffer.getReaderImmediateNotificationObject();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to initialize the reader", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getRecordDefinitions()
	 */
	public RecordDefinition[] getRecordDefinitions() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.getRecordDefinitions();
			
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve the record definitions", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getInactivityTimeout()
	 */
	public long getInactivityTimeout() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.getInactivityTimeout();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve buffer's inactivity timeout", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getInactivityTimeUnit()
	 */
	public TimeUnit getInactivityTimeUnit() throws GRS2ReaderException
	{
		try
		{
		return this.buffer.getInactivityTimeUnit();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve buffer's inactivity time unit", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The {@link Iterator}s which will use the timeout are instances of {@link ForwardReaderIterator}.
	 * </p>
	 * 
	 * @see {@link IRecordReader#setIteratorTimeout(long)}
	 */
	public void setIteratorTimeout(long iteratorTimeout)
	{
		this.iteratorTimeout = iteratorTimeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The {@link Iterator}s which use the timeout are instances of {@link ForwardReaderIterator}.
	 * 
	 * @see {@link IRecordReader#getIteratorTimeout()}
	 */
	public long getIteratorTimeout()
	{
		return iteratorTimeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The {@link Iterator}s which will use the time unit are instances of {@link ForwardReaderIterator}.
	 * </p>
	 * 
	 * @see {@link IRecordReader#setIteratorTimeUnit(TimeUnit)}
	 */
	public void setIteratorTimeUnit(TimeUnit iteratorTimeUnit)
	{
		this.iteratorTimeUnit = iteratorTimeUnit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The {@link Iterator}s which use the time unit are instances of {@link ForwardReaderIterator}.
	 * </p>
	 * 
	 * @see {@link IRecordReader#getIteratorTimeUnit()}
	 */
	public TimeUnit getIteratorTimeUnit()
	{
		return iteratorTimeUnit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getCapacity()
	 */
	public int getCapacity() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.getCapacity();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve capacity", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getConcurrentPartialCapacity()
	 */
	public int getConcurrentPartialCapacity() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.getConcurrentPartialCapacity();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve concurrent partial capacity", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getThreshold()
	 */
//	public float getThreshold() throws GRS2ReaderException
//	{
//		try
//		{
//			return this.buffer.getNotificationThreshold();
//		}catch(GRS2Exception ex)
//		{
//			throw new GRS2ReaderException("unable to retrieve notification threshold", ex);
//		}
//	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getThresholdNotificationObject()
	 */
//	public Object getThresholdNotificationObject() throws GRS2ReaderException
//	{
//		try 
//		{
//			return this.buffer.getReaderThresholdNotificationObject();
//		}catch(GRS2Exception ex)
//		{
//			throw new GRS2ReaderException("unable to retrieve threshold notification object");
//		}
//	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#getStatus()
	 */
	public synchronized IBuffer.Status getStatus()
	{
		return this.buffer.getStatus();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#close()
	 */
	public synchronized void close() throws GRS2ReaderException
	{
		try
		{
			if(this.buffer.getStatus()==Status.Dispose) return;
			this.buffer.close();
			this.buffer.dispose();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to close reader", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#totalRecords()
	 */
	public synchronized long totalRecords() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.totalRecords();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve total record count", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#availableRecords()
	 */
	public synchronized int availableRecords() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.availableRecords();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve available record count", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#currentRecord()
	 */
	public long currentRecord() throws GRS2ReaderException {
		if(this.currentRecordIndex == -1)
			throw new GRS2ReaderException("no records retrieved");
		return this.currentRecordIndex;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#get()
	 */
	@SuppressWarnings("unchecked")
	public synchronized T get() throws GRS2ReaderException
	{
		try
		{
			T item=(T)this.buffer.get();
			if(item!=null)
				this.currentRecordIndex++;
			return item;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve record", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#get(long, TimeUnit)
	 */
	@SuppressWarnings("unchecked")
	public synchronized T get(long timeout, TimeUnit unit) throws GRS2ReaderException
	{
		try
		{
			ProgressiveTimeoutGenerator ptg = new ProgressiveTimeoutGenerator(unit.toMillis(timeout));
			Record item=null;
			while (true)
			{
				if(this.buffer.getStatus()==Status.Dispose) break;
				if(this.buffer.getStatus()==Status.Close && this.buffer.availableRecords()==0) break;
				item=this.buffer.get();
				if(item!=null || !ptg.hasNext()) break;
				synchronized (this.immediateNotificationObject)
				{
					try { this.immediateNotificationObject.wait(ptg.next()); } catch (InterruptedException e){ break; }
				}
			}
			if(item!=null)
				this.currentRecordIndex++;
			return (T)item;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve record", ex);
		}
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * Seeks the number of {@link Record}s provided forward in the list of {@link Record}s available. If the seek
	 * length is larger than the readily available {@link Record}s, then the timeout and time unit available through
	 * {@link ForwardReader#getIteratorTimeout()} and {@link ForwardReader#getIteratorTimeUnit()}  
	 * 
	 * @param len the number of {@link Record}}s to skip
	 * @return the number of {@link Record}s actually skipped
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public long seek(long len) throws GRS2ReaderException
	{
		try
		{
			if(len==0) return 0;
			else if (len<0) throw new GRS2ReaderInvalidArgumentException("seek length must be non negative");
			int newLen=0;
			for(long i=0;i<len;i+=1)
			{
				T rec=this.get(this.getIteratorTimeout(), this.getIteratorTimeUnit());
				if(rec==null) break;
				newLen+=1;
			}
			this.currentRecordIndex+=newLen;
			return newLen;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to seek", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The iterator returned is an instance of {@link ForwardReaderIterator}
	 * </p>
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator()
	{
		return new ForwardReaderIterator<T>(this);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#waitAvailable(long, TimeUnit)}
	 */
	public synchronized boolean waitAvailable(long timeout, TimeUnit unit) throws GRS2ReaderException
	{
		try
		{
			long expirationTime = System.currentTimeMillis() + unit.toMillis(timeout);
			boolean item=false;
			while (true)
			{
				if(this.buffer.getStatus()==Status.Dispose) break;
				if(this.buffer.getStatus()==Status.Close && this.buffer.availableRecords()==0) break;
				if(this.buffer.availableRecords()>0) item=true;
				if(item) break;
				long timeLeft = expirationTime - System.currentTimeMillis();
				if (timeLeft <= 0) break;
				synchronized (this.immediateNotificationObject)
				{
					try { this.immediateNotificationObject.wait(timeLeft); } catch (InterruptedException e){ break; }
				}
			}
			return item;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to seek", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#emit(BufferEvent)
	 */
	public synchronized void emit(BufferEvent event) throws GRS2ReaderException, GRS2ReaderInvalidArgumentException
	{
		if(event==null) throw new GRS2ReaderInvalidArgumentException("event cannot be null");
		try
		{
			event.setSource(EventSource.Reader);
			this.buffer.emit(event);
		} catch (GRS2BufferException e)
		{
			throw new GRS2ReaderException("unable to emit event", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordReader#receive()
	 */
	public synchronized BufferEvent receive() throws GRS2ReaderException
	{
		try
		{
			return this.buffer.receive(EventSource.Writer);
		} catch (GRS2BufferException e)
		{
			throw new GRS2ReaderException("unable to receive event", e);
		}
	}
}
