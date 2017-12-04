package gr.uoa.di.madgik.grs.registry;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

import java.util.Collection;
import java.util.Hashtable;
import java.util.UUID;

/**
 * The {@link GRSRegistry} utility class is a statically initialized single instance per JVM that can uniquely register and reference
 * items registered to it using a unique identifier. The items that can be registered are {@link IBuffer} and {@link IBufferStore}
 * instances. In addition to instantiating the registry, during the static initialization, a {@link LifecycleManager} is also 
 * initialize and set to monitor the lifecycle properties of the registered items 
 * 
 * @author gpapanikos
 *
 */
public class GRSRegistry
{
	/**
	 * The registry instance used
	 */
	public static final GRSRegistry Registry=new GRSRegistry();
	private Hashtable<String, IBuffer> mapBuffer=new Hashtable<String, IBuffer>();
	private Hashtable<String, IBufferStore> mapStore=new Hashtable<String, IBufferStore>();
	private static final LifecycleManager lifecycle=new LifecycleManager();
	
	static
	{
		GRSRegistry.lifecycle.setName("lifecycle manager");
		GRSRegistry.lifecycle.setDaemon(true);
		GRSRegistry.lifecycle.start();
	}
	
	private GRSRegistry(){}
	
	/**
	 * Registers the provided {@link IBuffer} in the registry and assigns it with a unique id with which it can be referenced
	 * 
	 * @param buffer the {@link IBuffer} to register
	 * @return the key the {@link IBuffer} was assigned
	 */
	public synchronized String add(IBuffer buffer)
	{ 
		String key=null;
		key=UUID.randomUUID().toString();
		this.mapBuffer.put(key, buffer);
		return key;
	}
	
	/**
	 * Registers the provided {@link IBufferStore} in the registry and assigns it with a unique id with which it can be referenced
	 * 
	 * @param store the {@link IBufferStore} to register
	 * @return the key the {@link IBufferStore} was assigned
	 */
	public synchronized String add(IBufferStore store)
	{ 
		String key=null;
		key=UUID.randomUUID().toString();
		this.mapStore.put(key, store);
		return key;
	}
	
	/**
	 * Retrieves the {@link IBuffer} entry that is associated with the provided key
	 * 
	 * @param key the key of the entry to retrieve
	 * @return the {@link IBuffer} associated with the key or null if no {@link IBuffer} is associated with the key
	 */
	public synchronized IBuffer getBuffer(String key)
	{
		if(key==null) return null;
		if(!this.mapBuffer.containsKey(key)) return null;
		return this.mapBuffer.get(key);
	}
	
	/**
	 * Retrieves the {@link IBufferStore} entry that is associated with the provided key
	 * 
	 * @param key the key of the entry to retrieve
	 * @return the {@link IBufferStore} associated with the key or null if no {@link IBufferStore} is associated with the key
	 */
	public synchronized IBufferStore getStore(String key)
	{
		if(key==null) return null;
		if(!this.mapStore.containsKey(key)) return null;
		return this.mapStore.get(key);
	}
	
	/**
	 * Removes the entry with the specific key from the registry if found and calls
	 * {@link IBuffer#dispose()} or {@link IBufferStore#dispose()} depending on the nature
	 * of the respective entry
	 * 
	 * @param key the key of the entry to remove and dispose
	 */
	public synchronized void remove(String key)
	{
		if(key==null) return;
		if(this.mapBuffer.containsKey(key)) 
		{
			IBuffer buf=this.mapBuffer.remove(key);
			if(buf!=null) try{buf.dispose();}catch(Exception ex){}
		}
		else if(this.mapStore.containsKey(key))
		{
			IBufferStore store=this.mapStore.remove(key);
			if(store!=null) try{store.dispose();}catch(Exception ex){}
		}
	}
	
	/**
	 * Retrieves the available {@link IBuffer} entries stored in the registry 
	 * 
	 * @return the available {@link IBuffer} entries
	 */
	protected synchronized Collection<IBuffer> getBufferEntries()
	{
		return this.mapBuffer.values();
	}
	
	/**
	 * Retrieves the available {@link IBufferStore} entries stored in the registry 
	 * 
	 * @return the available {@link IBufferStore} entries
	 */
	protected synchronized Collection<IBufferStore> getStoreEntries()
	{
		return this.mapStore.values();
	}
}
