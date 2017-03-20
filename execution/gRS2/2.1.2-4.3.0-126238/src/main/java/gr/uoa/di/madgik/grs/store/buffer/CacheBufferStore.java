package gr.uoa.di.madgik.grs.store.buffer;

import gr.uoa.di.madgik.grs.buffer.QueueBuffer;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.FifoMultiplex;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.FirstAvailableMultiplex;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.IMultiplex;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.TimeStruct;
import gr.uoa.di.madgik.grs.store.record.FileRecordStore;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ehcache.CacheManager;

/**
 * Implementation of the {@link IBufferStore} which utilizes {@link FileRecordStore} instances to store 
 * the {@link Record}s accessed through the incoming locators
 * 
 * @author gpapanikos
 *
 */
public class CacheBufferStore extends Thread implements IBufferStore
{
	private static Logger logger=Logger.getLogger(CacheBufferStore.class.getName());
	/**
	 * The default {@link IBufferStore.MultiplexType} currently set to {@link IBufferStore.MultiplexType#FIFO}
	 */
	public static final MultiplexType DefaultMultiplexType=MultiplexType.FIFO;
	private MultiplexType multiplex=CacheBufferStore.DefaultMultiplexType;
	private URI[] locators=new URI[0];
	private long readerTimeout=ForwardReader.DefaultIteratorTimeout;
	private TimeUnit readerTimeoutTimeUnit=ForwardReader.DefaultIteratorTimeUnit;
	private TimeStruct inactivity=new TimeStruct(QueueBuffer.DefaultInactivityTimeout, QueueBuffer.DefaultInactivityTimeUnit);
	
	private boolean doneInit=false;
	private long lastActivityTime=System.currentTimeMillis();
	private final Object modifyNotification=new Object();
	
	private ArrayList<BufferStoreEntry> entries=null;
	private IMultiplex algo=null;
	
	private ArrayList<BufferStoreReader> readers=new ArrayList<BufferStoreReader>();

	private String key=null;
	
	public static CacheManager manager = new CacheManager();

	
	/**
	 * Create new instance
	 */
	public CacheBufferStore()
	{
		this.doneInit=false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#associateStoreReader(gr.uoa.di.madgik.grs.store.buffer.BufferStoreReader)
	 */
	public void associateStoreReader(BufferStoreReader reader)
	{
		this.readers.add(reader);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#setKey(java.lang.String)
	 */
	public void setKey(String key)
	{
		this.key=key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getKey()
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getModificationObject()
	 */
	public Object getModificationObject()
	{
		return this.modifyNotification;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getEntries()
	 */
	public ArrayList<BufferStoreEntry> getEntries()
	{
		return this.entries;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getMultiplexType()
	 */
	public MultiplexType getMultiplexType()
	{
		return this.multiplex;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#setMultiplexType(gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType)
	 */
	public void setMultiplexType(MultiplexType multiplex) throws GRS2BufferStoreInvalidOperationException
	{
		if(this.doneInit) throw new GRS2BufferStoreInvalidOperationException("Already performed initialization");
		this.multiplex=multiplex;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getReaderTimeout()
	 */
	public long getReaderTimeout()
	{
		return this.readerTimeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#setReaderTimeout(long)
	 */
	public void setReaderTimeout(long timeout)
	{
		this.readerTimeout=timeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getReaderTimeoutTimeUnit()
	 */
	public TimeUnit getReaderTimeoutTimeUnit()
	{
		return this.readerTimeoutTimeUnit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#setReaderTimeoutTimeUnit(java.util.concurrent.TimeUnit)
	 */
	public void setReaderTimeoutTimeUnit(TimeUnit unit)
	{
		this.readerTimeoutTimeUnit=unit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getInactivityTimeout()
	 */
	public long getInactivityTimeout()
	{
		return this.inactivity.timeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getInactivityTimeUnit()
	 */
	public TimeUnit getInactivityTimeUnit()
	{
		return this.inactivity.unit;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getLocators()
	 */
	public URI[] getLocators()
	{
		return this.locators;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#getLastActivityTime()
	 */
	public long getLastActivityTime()
	{
		return this.lastActivityTime;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#markActivity()
	 */
	public void markActivity()
	{
		this.lastActivityTime=System.currentTimeMillis();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#setLocators(java.net.URI[])
	 */
	public void setLocators(URI[] locators) throws GRS2BufferStoreInvalidOperationException
	{
		if(this.doneInit) throw new GRS2BufferStoreInvalidOperationException("Already performed initialization");
		this.locators=locators;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#initialize()
	 */
	public void initialize() throws GRS2BufferStoreInvalidOperationException
	{
		try
		{
			this.doneInit=true;
			this.entries=new ArrayList<BufferStoreEntry>(this.locators.length);
			for(URI locator : this.locators)
			{
				BufferStoreEntry entry=new BufferStoreEntry(locator);
				entry.setReaderTimeout(this.readerTimeout);
				entry.setReaderTimeoutTimeUnit(this.readerTimeoutTimeUnit);
				entry.initialize();
				this.entries.add(entry);
			}
			for(BufferStoreEntry entry: this.entries)
			{
				TimeStruct str=new TimeStruct(entry.getReaderTimeout(), entry.getReaderTimeoutTimeUnit());
				if(this.inactivity.compareTo(str)>0) this.inactivity=str;
			}
			
		}catch(Exception ex)
		{
			this.dispose();
			throw new GRS2BufferStoreInvalidOperationException("Could not initialize provided readers", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Starts the execution in a daemon background thread
	 * </p>
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#store()
	 */
	public void store()
	{
		this.setName("File buffer store");
		this.setDaemon(true);
		this.start();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.IBufferStore#dispose()
	 */
	public void dispose()
	{
		this.locators=null;
		for(BufferStoreReader reader : this.readers) { try{ reader.dispose(); }catch(Exception ex){} }
		this.readers.clear();
		this.readers=null;
		if(this.algo!=null) { try{ this.algo.dispose(); }catch(Exception ex){} }
		this.algo=null;
		for(BufferStoreEntry entry : this.entries) { try{entry.dispose();} catch(Exception ex){} }
		this.entries.clear();
		this.entries=null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * In the context of the execution thread initiated by {@link CacheBufferStore#store()} procedure, the thread loads in its
	 * thread of execution the defined {@link IMultiplex} implementation and forwards the execution to it. The multiplexing 
	 * implementations used are {@link FifoMultiplex} and {@link FirstAvailableMultiplex} depending on the value of
	 * {@link CacheBufferStore#getMultiplexType()}
	 * </p>
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		try
		{
			switch(this.multiplex)
			{
				case FIFO: { this.algo=new FifoMultiplex(); break; }
				case FirstAvailable: { this.algo=new FirstAvailableMultiplex(); break; }
				default: throw new GRS2BufferStoreInvalidArgumentException("non recognizable multiplexing value "+this.multiplex.toString());
			}
			this.algo.setEntries(this.entries);
			this.algo.setBufferStore(this);
			this.algo.setModificationNotify(this.modifyNotification);
			this.algo.multiplex();
		}catch(Exception ex)
		{
			this.dispose();
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Could not complete buffer storing. Disposing",ex);
		}
	}
}
