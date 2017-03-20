package gr.uoa.di.madgik.grs.store.buffer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.store.event.EventStoreFactory;
import gr.uoa.di.madgik.grs.store.event.GRS2EventStoreException;
import gr.uoa.di.madgik.grs.store.event.IEventStore;
import gr.uoa.di.madgik.grs.store.record.GRS2RecordStoreException;
import gr.uoa.di.madgik.grs.store.record.IRecordStore;
import gr.uoa.di.madgik.grs.store.record.RecordStoreFactory;

/**
 * This entry holds information per incoming locator to be used by {@link IBufferStore} implementations. This information 
 * includes the locator that is managed, the reader that is used to iterate over the {@link Record}s accessible through the 
 * locator, the {@link IRecordStore} that persists the retrieved {@link Record}s, the {@link IEventStore} that persists the 
 * received {@link BufferEvent}s as well as a persisted location holding the {@link RecordDefinition}s that are needed to 
 * be available for the {@link Record}s to be reused
 * 
 * @author gpapanikos
 *
 */
public class BufferStoreEntry
{
	/**
	 * The status of the entry
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum EntryStatus
	{
		/**
		 * The entry is still active and the reader is used to retrieve more {@link Record}s
		 */
		Open,
		/**
		 * The entry is closed and no more {@link Record}s are expected
		 */
		Close
	}
	
	private URI locator=null;
	private long readerTimeout=ForwardReader.DefaultIteratorTimeout;
	private TimeUnit readerTimeoutTimeUnit=ForwardReader.DefaultIteratorTimeUnit;

	private ForwardReader<Record> reader=null;
	private IRecordStore recordManager=null;
	private IEventStore eventManager=null;
	private File bufferDefinitions=null;
	
	private EntryStatus status=EntryStatus.Open;

	/**
	 * Create a new instance
	 * 
	 * @param locator the locator pointing to the incoming {@link IBuffer} 
	 */
	public BufferStoreEntry(URI locator)
	{
		this.locator=locator;
		this.status=EntryStatus.Open;
	}
	
	/**
	 * Retrieves the status of the entry
	 * 
	 * @return the status
	 */
	public EntryStatus getStatus()
	{
		return this.status;
	}
	
	/**
	 * Sets the status of the entry
	 * 
	 * @param status the status
	 */
	public void setStatus(EntryStatus status)
	{
		this.status=status;
	}

	/**
	 * Retrieves the reader used to iterate over the {@link Record}s of the incoming locator
	 * 
	 * @return the reader
	 */
	public ForwardReader<Record> getReader()
	{
		return reader;
	}

	/**
	 * Retrieves the {@link IRecordStore} manager that is used to persist incoming {@link Record}s
	 * 
	 * @return the used manager
	 */
	public IRecordStore getRecordManager()
	{
		return recordManager;
	}

	/**
	 * Retrieves the {@link IEventStore} manager that is used to persist incoming {@link BufferEvent}s
	 * 
	 * @return the used manager
	 */
	public IEventStore getEventManager()
	{
		return eventManager;
	}

	/**
	 * Retrieves the timeout set to be used by the reader of the incoming locator. This value is to be used in conjunction
	 * with the {@link BufferStoreEntry#getReaderTimeoutTimeUnit()}
	 * 
	 * @return the timeout
	 */
	public long getReaderTimeout()
	{
		return this.readerTimeout;
	}

	/**
	 * Sets the timeout set to be used by the reader of the incoming locator. This value is to be used in conjunction
	 * with the {@link BufferStoreEntry#setReaderTimeoutTimeUnit(TimeUnit)}
	 * 
	 * @param timeout the timeout
	 */
	public void setReaderTimeout(long timeout)
	{
		this.readerTimeout=timeout;
	}

	/**
	 * Retrieves the timeout time unit set to be used by the reader of the incoming locator. This value is to be used in conjunction
	 * with the {@link BufferStoreEntry#getReaderTimeout()}
	 * 
	 * @return the time unit
	 */
	public TimeUnit getReaderTimeoutTimeUnit()
	{
		return this.readerTimeoutTimeUnit;
	}

	/**
	 * Sets the timeout time unit set to be used by the reader of the incoming locator. This value is to be used in conjunction
	 * with the {@link BufferStoreEntry#setReaderTimeout(long)}
	 * 
	 * @param unit the time unit
	 */
	public void setReaderTimeoutTimeUnit(TimeUnit unit)
	{
		this.readerTimeoutTimeUnit=unit;
	}
	
	/**
	 * After all configuration values have been set, this method initializes the {@link IRecordStore}, the {@link ForwardReader},
	 * and persists the reader available {@link RecordDefinition}s 
	 * 
	 * @throws GRS2RecordStoreException the state of the {@link IRecordStore} does not allow for this operation to be completed
	 * @throws GRS2ReaderException the state of the {@link ForwardReader} does not allow for this operation to be completed
	 * @throws GRS2BufferStoreAccessException the state of the {@link IBufferStore} does not allow for this operation to be completed
	 * @throws GRS2EventStoreException the state of the {@link IEventStore} does not allow for this operation to be completed
	 */
	public void initialize() throws GRS2RecordStoreException, GRS2ReaderException, GRS2BufferStoreAccessException, GRS2EventStoreException
	{
		this.recordManager=RecordStoreFactory.getManager();
		this.recordManager.enableOrder(true);
		this.eventManager=EventStoreFactory.getManager();
		this.reader = new ForwardReader<Record>(locator);
		this.reader.setIteratorTimeout(this.readerTimeout);
		this.reader.setIteratorTimeout(this.readerTimeout);
		this.persistBufferDefinitions();
	}
	
	/**
	 * Disposes all state kept as well as the initialized {@link ForwardReader#close()}, {@link IRecordStore#dispose()}
	 * and removes the persisted {@link RecordDefinition}s
	 */
	public void dispose()
	{
		this.locator=null;
		if(this.reader!=null) try{this.reader.close();}catch(Exception ex){}
		if(this.recordManager!=null) try{this.recordManager.dispose();}catch(Exception ex){}
		if(this.eventManager!=null) try{this.eventManager.dispose();}catch(Exception ex){}
		if(this.bufferDefinitions!=null) try{this.bufferDefinitions.delete();}catch(Exception ex){}
		this.status=EntryStatus.Close;
	}
	
	/**
	 * Retrieves the {@link RecordDefinition}s from the persisted location it has stored them at initialization
	 * 
	 * @return the {@link RecordDefinition}s of the incoming locator
	 * @throws GRS2BufferStoreAccessException the state of the {@link IBufferStore} does not allow for this operation to be completed
	 */
	public RecordDefinition[] getDefinitions() throws GRS2BufferStoreAccessException
	{
		DataInputStream din=null;
		try
		{
			din=new DataInputStream(new BufferedInputStream(new FileInputStream(this.bufferDefinitions)));
			int len=din.readInt();
			RecordDefinition[] defs=new RecordDefinition[len];
			for(int i=0;i<len;i+=1)
			{
				String recordDefType=din.readUTF();
				RecordDefinition def=(RecordDefinition)Class.forName(recordDefType).newInstance();
				def.inflate(din);
				defs[i]=def;
			}
			return defs;
		}catch(Exception ex)
		{
			throw new GRS2BufferStoreAccessException("Could not initialize buffer definitions file", ex);
		}
		finally
		{
			if(din!=null)
			{
				try{din.close();}catch(Exception ex){}
			}
		}
	}
	
	private void persistBufferDefinitions() throws GRS2BufferStoreAccessException
	{
		DataOutputStream dout=null;
		try
		{
			this.bufferDefinitions=File.createTempFile(UUID.randomUUID().toString(), null);
			this.bufferDefinitions.deleteOnExit();
			dout=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.bufferDefinitions)));
			RecordDefinition []defs=this.reader.getRecordDefinitions();
			dout.writeInt(defs.length);
			for(RecordDefinition def : defs)
			{
				dout.writeUTF(def.getClass().getName());
				def.deflate(dout);
			}
		}catch(Exception ex)
		{
			throw new GRS2BufferStoreAccessException("Could not initialize buffer definitions file", ex);
		}
		finally
		{
			if(dout!=null)
			{
				try{dout.flush();}catch(Exception ex){}
				try{dout.close();}catch(Exception ex){}
			}
		}
	}
}
