package gr.uoa.di.madgik.grs.reader;

import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.IReaderProxy;
import gr.uoa.di.madgik.grs.proxy.ProxyFactory;
import gr.uoa.di.madgik.grs.proxy.mirror.GRS2ProxyMirrorException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.store.record.GRS2RecordStoreException;
import gr.uoa.di.madgik.grs.store.record.IRecordStore;
import gr.uoa.di.madgik.grs.store.record.RecordStoreFactory;
import gr.uoa.di.madgik.grs.utils.ProgressiveTimeoutGenerator;

/**
 * The {@link RandomReader} class provides access over an authored {@link IBuffer} in a random access fashion. The {@link Record}s
 * retrieved can be defined to be cast to the specific {@link Record} extending class. The random way of record access is enabled 
 * through the use of a {@link IRecordStore} that persists all the records that are retrieved in the forward fashion. This way
 * whenever a previous record than the current forward record, it is retrieved by the record store. The deserialization from the 
 * record store is not performed on a per record only fashion, but a window can be defined that will include a number of pre-stored 
 * records
 * 
 * @author gpapanikos
 *
 * @param <T> The type of {@link Record} specialization that is to be returned by the <code>get</code> operations
 */
public class RandomReader<T extends Record> implements IRecordReader<T>, Iterable<T>
{
	/**
	 * The default timeout to be used by the {@link RandomReaderIterator}s initialized by this reader when retrieving
	 * records or checking if more records are available. This value should be interpreted in conjunction with the
	 * value of {@link RandomReader#DefaultIteratorTimeUnit} 
	 */
	public static final int DefaultIteratorTimeout=10;
	/**
	 * The default time unit to be used by the {@link RandomReaderIterator}s initialized by this reader when retrieving
	 * records or checking if more records are available. This value should be interpreted in conjunction with the
	 * value of {@link RandomReader#DefaultIteratorTimeout} 
	 */
	public static final TimeUnit DefaultIteratorTimeUnit=TimeUnit.SECONDS;
	/**
	 * The default size of the window used to resume records that are already in the {@link IRecordStore} and need to be
	 * made available in memory. Currently set to 1
	 */
	public static final int DefaultWindowSize=1;
	private IBuffer buffer=null;
	private IReaderProxy proxy=null;
	private Object immediateNotificationObject=new Object();
	private long iteratorTimeout=ForwardReader.DefaultIteratorTimeout;
	private TimeUnit iteratorTimeUnit=ForwardReader.DefaultIteratorTimeUnit;
	private IRecordStore manager=null;
	
	private long lastRecordIndex=-1;
	private long currentRecordIndex=-1;
	private int windowSize=RandomReader.DefaultWindowSize;
	
	private Hashtable<Long, T> window=new Hashtable<Long, T>();
	private long windowBeginRecordIndex=-1;
	private long windowEndRecordIndex=-1;
	
	/**
	 * Creates a new reader accessing the {@link IBuffer} referenced by the provided locator
	 * 
	 * @param locator the locator identifying the {@link IBuffer} to consume
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public RandomReader(URI locator) throws GRS2ReaderException
	{
		try
		{
			this.proxy=ProxyFactory.getProxy(locator);
			this.buffer=this.proxy.getBuffer();
			this.manager=RecordStoreFactory.getManager();
			this.manager.enableOrder(false);
			this.immediateNotificationObject=this.buffer.getReaderImmediateNotificationObject();
			this.initCounters();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to initialize the reader", ex);
		}
	}
	
	/**
	 * Creates a new reader accessing the {@link IBuffer} referenced by the provided locator
	 * The {@link IBuffer} will have the specified capacity, if possible
	 * 
	 * @param locator the locator identifying the {@link IBuffer} to consume
	 * @param capacity a desired capacity for the {@link IBuffer} which will be consumed. Used as
	 * a hint to the underlying {@link IReaderProxy}, which might or might not honor it depending
	 * on its strategy
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public RandomReader(URI locator, int capacity) throws GRS2ReaderException
	{
		try
		{
			this.proxy=ProxyFactory.getProxy(locator);
			this.proxy.overrideBufferCapacity(capacity);
			this.buffer=this.proxy.getBuffer();
			this.manager=RecordStoreFactory.getManager();
			this.manager.enableOrder(false);
			this.immediateNotificationObject=this.buffer.getReaderImmediateNotificationObject();
			this.initCounters();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to initialize the reader", ex);
		}
	}
	
	/**
	 * Retrieves the {@link RecordDefinition}s that define the {@link Record}s that are accessible through the reader
	 * 
	 * @see IBuffer#getRecordDefinitions()
	 * 
	 * @return the {@link RecordDefinition}s as were provided by the corresponding writer
	 * @throws GRS2ReaderException the operation could not be completed
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
	 * @see {@link IRecordReader#getInactivityTimeout()}
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
	 * @see {@link IRecordReader#getInactivityTimeUnit()}
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
	 * Sets the window size that will be used whenever a pre-read record is requested. Other than just the requested record,
	 * an additional number of records will be resumed from the {@link IRecordStore} and made available 
	 * 
	 * @param windowSize the size of the window
	 */
	public void setWindowSize(int windowSize)
	{
		this.windowSize = windowSize;
	}

	/**
	 * Retrieves the window size that will be used whenever a pre-read record is requested. Other than just the requested record,
	 * an additional number of records will be resumed from the {@link IRecordStore} and made available 
	 * 
	 * @return the window size
	 */
	public int getWindowSize()
	{
		return this.windowSize;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The {@link Iterator}s which will use the timeout are instances of {@link RandomReaderIterator}.
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
	 * The {@link Iterator}s which use the timeout are instances of {@link RandomReaderIterator}.
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
	 * The {@link Iterator}s which will use the time unit are instances of {@link RandomReaderIterator}.
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
	 * The {@link Iterator}s which use the time unit are instances of {@link RandomReaderIterator}.
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
	 * @see {@link IRecordReader#getCapacity()}
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
	 * @see {@link IRecordReader#getConcurrentPartialCapacity()}
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
	 * @see {@link IRecordReader#getThreshold}
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
	 * @see {@link IRecordReader#getStatus()}
	 */
	public synchronized IBuffer.Status getStatus()
	{
		return this.buffer.getStatus();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecordStore#dispose()
	 * @see {@link IRecordReader#close()}
	 */
	public synchronized void close() throws GRS2ReaderException
	{
		try
		{
			if(this.buffer.getStatus()==Status.Dispose) return;
			this.buffer.close();
			this.buffer.dispose();
			this.manager.dispose();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to close reader", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#totalRecords()}
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
	 * @see {@link IRecordReader#currentRecord()}
	 */
	public long currentRecord() throws GRS2ReaderException {
		if(this.currentRecordIndex == -1)
			throw new GRS2ReaderException("no records retrieved");
		return this.currentRecordIndex;
	}
	
	/**
	 * The number of readily available for consumption {@link Record}s
	 * 
	 * @see IBuffer#availableRecords()
	 * 
	 * @return the number of readily available for consumption records
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public synchronized int availableRecords() throws GRS2ReaderException
	{
		try
		{
			if(this.serveFromWindow())
				return (int)(this.window.size() - this.currentRecordIndex + this.windowBeginRecordIndex);
			return this.buffer.availableRecords();
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve available record count", ex);
		}
	}

	/**
	 * Retrieves the next available {@link Record} from the {@link IBuffer}. An explicit cast is performed to the 
	 * type provided in the generic declaration of the reader. If the request can be served by the {@link IRecordStore}
	 * then only the local store is touched and not the {@link IBuffer}
	 * 
	 * @see IBuffer#get()
	 * 
	 * @return the retrieved {@link Record} or null if none was readily available
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	@SuppressWarnings("unchecked")
	public synchronized T get() throws GRS2ReaderException
	{
		try
		{
			T record=null;
			if(this.serveFromWindow())
			{
				record=this.window.get(this.currentRecordIndex);
				if(record==null) throw new GRS2RecordStoreException("Could not locate previously stored record");
				this.buffer.markSimulateActivity();
			}
			else
			{
				record=(T)this.buffer.get();
				this.persistRecord(record);
			}
			if(record!=null) this.increaseCounters();
			return record;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve record", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * If the request can be served by the {@link IRecordStore} then only the local store is touched and not the {@link IBuffer}
	 * 
	 * @see IBuffer#get()
	 * 
	 * @param timeout the timeout to wait for
	 * @param unit the unit of time to use to interpret the timeout value
	 * @return the {@link Record} retrieved, or null if the timeout expired without a {@link Record} becoming available
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	@SuppressWarnings("unchecked")
	public synchronized T get(long timeout, TimeUnit unit) throws GRS2ReaderException
	{
		try
		{
			T item=null;
			if(this.serveFromWindow())
			{
				item=this.window.get(this.currentRecordIndex);
				if(item==null) throw new GRS2RecordStoreException("Could not locate previously stored record");
				this.buffer.markSimulateActivity();
			}
			else
			{
				ProgressiveTimeoutGenerator ptf = new ProgressiveTimeoutGenerator(unit.toMillis(timeout));
				while (ptf.hasNext())
				{
					if(this.buffer.getStatus()==Status.Dispose) break;
					if(this.buffer.getStatus()==Status.Close && this.buffer.availableRecords()==0) break;
					item=(T)this.buffer.get();
					if(item!=null) break;
					synchronized (this.immediateNotificationObject)
					{
						try { this.immediateNotificationObject.wait(ptf.next()); } catch (InterruptedException e){ break; }
					}
				}
				this.persistRecord(item);
			}
			if(item!=null) this.increaseCounters();
			return item;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to retrieve record", ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * Seeks the number of {@link Record}s provided forward or backward in the list of {@link Record}s available depending on 
	 * whether the provided length is a positive or negative number. If the seek length is larger than the readily available 
	 * {@link Record}s, then the timeout and time unit available through {@link ForwardReader#getIteratorTimeout()} and 
	 * {@link ForwardReader#getIteratorTimeUnit()}. If the seek can be completely served by the local {@link IRecordStore} then
	 * the {@link IBuffer} is not used.  
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
			long newLen=0;
			long additionalRecs=0;
			if(len<0 && (this.currentRecordIndex+len)<0) newLen=(-1 * this.currentRecordIndex);
			else if(len>0 && this.currentRecordIndex+len<=this.lastRecordIndex) newLen=len;
			else if(len>0 && this.currentRecordIndex+len>this.lastRecordIndex)
			{
				additionalRecs=this.currentRecordIndex+len-this.lastRecordIndex;
				len=this.lastRecordIndex-this.currentRecordIndex;
				newLen=len;
			}
			else newLen=len;
			/*if(additionalRecs==0)*/ this.resetWindow(this.currentRecordIndex+newLen);
			for(int i=0;i<additionalRecs;i+=1)
			{
				T rec=this.get(this.getIteratorTimeout(), this.getIteratorTimeUnit());
				if(rec==null) break;
				newLen+=1;
			}
			
			if (this.currentRecordIndex + newLen-additionalRecs < 0) {
				throw new GRS2ReaderException("index out of bounds");
			}
			
			this.currentRecordIndex+=newLen-additionalRecs;
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
	 * The iterator returned is an instance of {@link RandomReaderIterator}
	 * </p>
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator()
	{
		return new RandomReaderIterator<T>(this);
	}
	
	/**
	 * The iterator returned is an instance of {@link RandomReaderIterator}
	 * 
	 * @return the list iterator instance
	 */
	public ListIterator<T> listIterator()
	{
		return new RandomReaderIterator<T>(this);
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

	//////////// for Iterator usage ////////////
	
	/**
	 * {@inheritDoc}
	 * 
	 * If the request can be served by the local {@link IRecordStore}
	 * then the {@link IBuffer} is not used
	 * 
	 * @see {@link IRecordReader#waitAvailable(long, TimeUnit)}
	 */
	public synchronized boolean waitAvailable(long timeout, TimeUnit unit) throws GRS2ReaderException
	{
		try
		{
			boolean item=false;
			if(this.serveFromWindow()) item=true;
			else
			{
				long expirationTime = System.currentTimeMillis() + unit.toMillis(timeout);
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
			}
			return item;
		}catch(GRS2Exception ex)
		{
			throw new GRS2ReaderException("unable to seek", ex);
		}
	}
	
	/**
	 * Checks if according to the {@link IRecordStore} and the current window a client can request a previous to the 
	 * current {@link Record} 
	 * 
	 * @return true if a previous record can be requested based on the store window
	 */
	protected synchronized boolean canCallPrevious()
	{
		return this.currentRecordIndex!=1;
	}
	
	/**
	 * Checks if according to the {@link IRecordStore} and the current window a client can request a next to the 
	 * current {@link Record} 
	 * 
	 * @return true if a next record can be requested based on the store window
	 */
	protected synchronized boolean canCallNext()
	{
		if(this.currentRecordIndex<this.lastRecordIndex) return true;
		return false;
	}

	//////////// for internal usage ////////////
	
	private void persistRecord(T record) throws GRS2RecordStoreException, GRS2RecordDefinitionException, GRS2ProxyMirrorException, GRS2BufferException
	{
		if(record==null) return;
//		long startTotal=System.currentTimeMillis();
		record.makeAvailable();
//		long stopAvail=System.currentTimeMillis();
		this.manager.persist(record);
//		System.out.println("persisted in total "+(System.currentTimeMillis()-startTotal)+" with make available in "+(stopAvail-startTotal));
	}
	
	@SuppressWarnings("unchecked")
	private T retrieveRecord(long recordIndex) throws GRS2RecordStoreException, GRS2ReaderInvalidArgumentException, GRS2RecordDefinitionException, GRS2BufferException
	{
		Record record=this.manager.retrieve(recordIndex,false);
		if(record==null) throw new GRS2ReaderInvalidArgumentException("provided record id #" + recordIndex + " not found in persistency manager");
		record.bind(this.buffer);
		return (T)record;
	}

	private void initCounters() throws GRS2BufferException
	{
		this.lastRecordIndex=0;
		this.currentRecordIndex=0;
		this.windowSize=this.buffer.getCapacity();
		this.windowBeginRecordIndex=-1;
		this.windowEndRecordIndex=-1;
	}
	
	private void resetWindow(long recordIndex) throws GRS2RecordDefinitionException, GRS2RecordStoreException, GRS2ReaderInvalidArgumentException, GRS2BufferException
	{
		if(recordIndex>=this.lastRecordIndex)
		{
			this.windowBeginRecordIndex=-1;
			this.windowEndRecordIndex=-1;
			this.window.clear();
		}
		else if (recordIndex<0)
		{
			this.windowBeginRecordIndex=-1;
			this.windowEndRecordIndex=-1;
			this.window.clear();
		}
		else if(windowBeginRecordIndex>=0 && windowEndRecordIndex>=0 && 
				windowBeginRecordIndex<=recordIndex && windowEndRecordIndex>=recordIndex)
		{
			//already in window range
		}
		else
		{
			int tmpWindowSize=this.windowSize;
			//the subtraction will be an int as it is smaller than the set window size so cast is safe
			if(recordIndex+this.windowSize>this.lastRecordIndex) tmpWindowSize=(int)(this.lastRecordIndex-recordIndex);
			this.window.clear();
			for(long i=0;i<tmpWindowSize;i+=1)
			{
				this.window.put(recordIndex+i, this.retrieveRecord(recordIndex+i));
			}
			this.windowBeginRecordIndex=recordIndex;
			this.windowEndRecordIndex=recordIndex+tmpWindowSize-1;
		}
	}
	
	private boolean serveFromWindow()
	{
		if(this.windowBeginRecordIndex>=0 && this.windowEndRecordIndex>=0 && 
				this.currentRecordIndex>=this.windowBeginRecordIndex && 
				this.currentRecordIndex<=this.windowEndRecordIndex) return true;
		return false;
	}
	
	private void increaseCounters() throws GRS2RecordDefinitionException, GRS2RecordStoreException, GRS2ReaderInvalidArgumentException, GRS2BufferException
	{
		this.currentRecordIndex+=1;
		if(this.lastRecordIndex<this.currentRecordIndex) this.lastRecordIndex=this.currentRecordIndex;
		if(this.windowBeginRecordIndex>=0 && this.windowEndRecordIndex>=0 && 
				this.currentRecordIndex>this.windowEndRecordIndex) this.resetWindow(this.currentRecordIndex);
	}
}
