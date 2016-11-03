package gr.uoa.di.madgik.grs.store.record;

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
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.store.buffer.CacheBufferStore;

public class CacheRecordStore implements IRecordStore { 
	
	private static final int KeepInMemoryDef = 1000;
	private static final MemoryStoreEvictionPolicy EvictionPolicyDef = MemoryStoreEvictionPolicy.FIFO;
	private int keepInMemory = KeepInMemoryDef;
	private MemoryStoreEvictionPolicy evictionPolicy = EvictionPolicyDef;
	
	private Cache cache = null;
	private Cache orderedMappingCache = null;
//	private Hashtable<Long, Long> mappingOrdered=null;
	private boolean enableOrder=false;
	private long recordCount=0;
	private CacheManager manager = CacheBufferStore.manager;
	private UUID id = UUID.randomUUID();
	private String name = "gRSRecordStoreCache" + id;
	private String orderedName = "gRSRecordStoreCacheAux" + id;
	
	public CacheRecordStore(int keepInMemory, MemoryStoreEvictionPolicy evictionPolicy) {
		this.cache = new Cache(this.name, keepInMemory, evictionPolicy, true, null, true, 0, 0, false, 600, null);
		this.cache.setName(this.name);
		this.manager.addCache(cache);
	//	if(this.enableOrder) this.mappingOrdered=new Hashtable<Long, Long>();
		if(this.enableOrder)
			this.orderedMappingCache = new Cache(this.orderedName, keepInMemory, evictionPolicy, true, null, true, 0, 0, false, 600, null);
		this.recordCount = 0;
		this.keepInMemory = keepInMemory;
		this.evictionPolicy = evictionPolicy;
	}
	
	public CacheRecordStore(int keepInMemory) {
		this(keepInMemory, CacheRecordStore.EvictionPolicyDef);
	}
	
	public CacheRecordStore() {
		this(CacheRecordStore.KeepInMemoryDef, CacheRecordStore.EvictionPolicyDef);
	}
	
	public void enableOrder(boolean enableOrder) {
		if(this.enableOrder == true && enableOrder == true) {
			if(enableOrder == true) {
				this.orderedMappingCache.dispose();
				this.manager.removeCache(this.orderedName);
			}
			this.orderedMappingCache = new Cache(this.orderedName, this.keepInMemory, this.evictionPolicy, true, null, true, 0, 0, false, 600, null);
			this.orderedMappingCache.setName(this.orderedName);
			this.manager.addCache(this.orderedMappingCache);
		}
		this.enableOrder = enableOrder;
		
		if(this.enableOrder == false && this.orderedMappingCache != null) {
			this.orderedMappingCache.dispose();
			this.orderedMappingCache = null;
			this.manager.removeCache(this.orderedName);
		}
	}

	public long getRecordCount() {
		return cache.getSize();
	}

	public void persist(Record record) throws GRS2RecordStoreException {
		
		try {
			if(this.cache.isElementInMemory(record.getID())) //avoid checking on disk
					return;
			
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			DataOutput out = new DataOutputStream(bOut);
			out.writeUTF(record.getClass().getName());
			record.deflate(out);
			this.cache.putQuiet(new Element(record.getID(), bOut.toByteArray()));
		//	this.mappingSimple.put(record.getID(), offset);
		//	if(this.enableOrder) this.mappingOrdered.put(recordCount, record.getID());
			if(this.enableOrder)
				this.orderedMappingCache.putQuiet(new Element(((Long)recordCount).toString(), ((Long)record.getID()).toString()));
			this.recordCount+=1;
		}catch (Exception e) {
			throw new GRS2RecordStoreAccessException("Could not persist record", e);
		}
		
	}

	public Record retrieve(long recordID, boolean reset) throws GRS2RecordStoreException {
		try {
			Element element;
			if((element = this.cache.getQuiet(recordID)) == null)
				return null;
			
			byte[] bIn = (byte[])element.getObjectValue();
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(bIn));
			String recordType = in.readUTF();
			Record record = (Record)Class.forName(recordType).newInstance();
			record.inflate(in,reset);
			return record;
		}catch(Exception e) {
			throw new GRS2RecordStoreAccessException("Could not retrieve record", e);
		}
	
	}

	public Record retrieveByIndex(long recordIndex, boolean reset) throws GRS2RecordStoreException {
		try
		{
			if(!this.enableOrder) throw new GRS2RecordStoreAccessException("this operation is only available when ordering is enabled");
			
			Element element = null;
			if((element = this.orderedMappingCache.getQuiet(((Long)recordIndex).toString())) == null)
				return null;
			long recId = (Long)element.getValue();
			return this.retrieve(recId, reset);
		} catch (Exception e)
		{
			throw new GRS2RecordStoreAccessException("Could not persist record", e);
		}
	}

	public void dispose() throws GRS2RecordStoreException {
		try
		{
//			if(this.mappingOrdered!=null)
//			{
//				this.mappingOrdered.clear();
//				this.mappingOrdered=null;
//			}
			if(this.orderedMappingCache != null) {
				this.orderedMappingCache.dispose();
				this.orderedMappingCache = null;
				this.manager.removeCache(this.orderedName);
			}
			this.cache.dispose();
			this.cache = null;
			this.manager.removeCache(this.name);
		} catch (Exception e)
		{
			throw new GRS2RecordStoreException("Could not dispose persistency manager resources", e);
		}
		
	}
}
