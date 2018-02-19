package org.gcube.contentmanager.storageserver.store;

import com.mongodb.DBObject;

public class StorageStatusObject {
	
	private String consumer;
	
	private long volume;
	
	private int count;

	private String id;
	
	private DBObject dbo;
	
	public StorageStatusObject(String consumer, long volume, int count){
		this.consumer=consumer;
		this.volume=volume;
		this.count=count;
	}
	
	public StorageStatusObject(String id, String consumer, long volume, int count, DBObject obj){
		this.id=id;
		this.consumer=consumer;
		this.volume=volume;
		this.count=count;
		this.dbo=obj;
	}
	
	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DBObject getDbo() {
		return dbo;
	}

	public void setDbo(DBObject dbo) {
		this.dbo = dbo;
	}

	
}
