package gr.uoa.di.madgik.grs.store.event;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.UUID;

import gr.uoa.di.madgik.grs.events.BufferEvent;

/**
 * Implementation of the {@link IEventStore} using a {@link RandomAccessFile} over a local file as the persistency medium
 * 
 * @author gpapanikos
 *
 */
public class FileEventStore implements IEventStore 
{
	private File eventFile=null;
	private RandomAccessFile rand=null;
	private Hashtable<Long, Long> mappingOrdered=null;
	private long eventCount=0;
	
	/**
	 * Creates a new instance
	 * 
	 * @throws IOException
	 */
	public FileEventStore() throws IOException
	{
		this.eventFile=File.createTempFile(UUID.randomUUID().toString(), null);
		this.eventFile.deleteOnExit();
		this.rand=new RandomAccessFile(this.eventFile, "rw");
		this.mappingOrdered=new Hashtable<Long, Long>();
		this.eventCount=0;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.event.IEventStore#getEventCount()
	 */
	public long getEventCount()
	{
		return this.eventCount;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.event.IEventStore#dispose()
	 */
	public synchronized void dispose() throws GRS2EventStoreException
	{
		try
		{
			if(this.mappingOrdered!=null)
			{
				this.mappingOrdered.clear();
				this.mappingOrdered=null;
			}
			this.rand.close();
			this.eventFile.delete();
		} catch (Exception e)
		{
			throw new GRS2EventStoreException("Could not dispose persistency manager resources", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.event.IEventStore#persist(BufferEvent)
	 */
	public synchronized void persist(BufferEvent event) throws GRS2EventStoreException
	{
		try
		{
//			long start=System.currentTimeMillis();
			this.rand.seek(this.rand.length());
			long offset=this.rand.getFilePointer();
			this.rand.writeUTF(event.getClass().getName());
			event.deflate(this.rand);
			this.mappingOrdered.put(eventCount, offset);
			this.eventCount+=1;
//			System.out.println("record persist took "+(System.currentTimeMillis()-start));
		} catch (Exception e)
		{
			throw new GRS2EventStoreAccessException("Could not persist event", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.event.IEventStore#retrieveByIndex(long)
	 */
	public synchronized BufferEvent retrieveByIndex(long eventIndex) throws GRS2EventStoreException
	{
		try
		{
//			long start=System.currentTimeMillis();
			if(!this.mappingOrdered.containsKey(eventIndex)) return null;
			this.rand.seek(this.mappingOrdered.get(eventIndex));
			String eventType=this.rand.readUTF();
			BufferEvent event = (BufferEvent)Class.forName(eventType).newInstance();
			event.inflate(this.rand);
//			System.out.println("event retrieve took "+(System.currentTimeMillis()-start));
			return event;
		} catch (Exception e)
		{
			throw new GRS2EventStoreAccessException("Could not retrieve event", e);
		}
	}
}
