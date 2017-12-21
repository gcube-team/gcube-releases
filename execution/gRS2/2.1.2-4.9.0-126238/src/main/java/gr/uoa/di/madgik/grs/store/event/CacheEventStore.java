package gr.uoa.di.madgik.grs.store.event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.store.buffer.CacheBufferStore;

/**
 * Implementation of the {@link IEventStore} using an in-memory disk overflowable {@link Cache} as the persistency medium
 * 
 * @author gpapanikos
 *
 */
public class CacheEventStore implements IEventStore 
{
	private static final int KeepInMemoryDef = 1000;
	private static final MemoryStoreEvictionPolicy EvictionPolicyDef = MemoryStoreEvictionPolicy.FIFO;
	
	private CacheManager manager = CacheBufferStore.manager;
	private Cache cache = null;
//	private RandomAccessFile rand=null;
//	private Hashtable<Long, Long> mappingOrdered=null;
	private UUID id = UUID.randomUUID();
	private String name = "gRSEventStoreCache" + id;
	private long eventCount=0;
	
	public CacheEventStore(int keepInMemory, MemoryStoreEvictionPolicy evictionPolicy)
	{
		this.cache = new Cache(this.name, keepInMemory, evictionPolicy, true, null, true, 0, 0, false, 0, null);
		this.cache.setName(this.name);
		this.manager.addCache(cache);
	//	if(this.enableOrder) this.mappingOrdered=new Hashtable<Long, Long>();
		this.eventCount=0;
	}
	
	public CacheEventStore(int keepInMemory) {
		this(keepInMemory, CacheEventStore.EvictionPolicyDef);
	}
	
	public CacheEventStore() {
		this(CacheEventStore.KeepInMemoryDef, CacheEventStore.EvictionPolicyDef);
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
			this.cache.dispose();
			this.cache = null;
			this.manager.removeCache(this.name);
		
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
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			DataOutput out = new DataOutputStream(bOut);
			out.writeUTF(event.getClass().getName());
			event.deflate(out);
			this.cache.putQuiet(new Element(((Long)this.eventCount).toString(), bOut.toByteArray()));
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
			Element element;
			if((element = this.cache.getQuiet(eventIndex)) == null)
				return null;
			
			byte[] bIn = (byte[])element.getObjectValue();
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(bIn));
			String eventType = in.readUTF();
			BufferEvent event = (BufferEvent)Class.forName(eventType).newInstance();
			event.inflate(in);
			return event;
//			System.out.println("event retrieve took "+(System.currentTimeMillis()-start));
		} catch (Exception e)
		{
			throw new GRS2EventStoreAccessException("Could not retrieve event", e);
		}
	}
}
