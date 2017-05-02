package org.gcube.informationsystem.publisher.cache;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistryCache  extends LinkedHashMap<String,List<URI>>{

	private final int capacity;
	private long accessCount = 0;
	private long hitCount = 0;
	
	public RegistryCache(int capacity){
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}
	
	public List<URI> get(String key){
		accessCount++;
		if (containsKey(key)){
		  hitCount++;
		}
		List<URI> value = super.get(key);
		return value;
	}
	
	protected boolean removeEldestEntry(Map.Entry eldest){
		return size() > capacity;
	}
	
	public long getAccessCount(){
		return accessCount;
	}
	
	public long getHitCount(){
		return hitCount;
	}
}
