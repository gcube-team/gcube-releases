package org.gcube.data.transfer.library.caches;

public class TTLContainer<T> {
	
	private long lastUsageTime=System.currentTimeMillis();
	private long creationTime=System.currentTimeMillis();
	
	private T theObject;
	public TTLContainer(T theObject) {			
		this.theObject = theObject;
	}
	
	
	private void update(){
		lastUsageTime=System.currentTimeMillis();			
	}
	
	public T getTheObject(){
		update();
		return theObject;
	}
	
	public long getLastUsageTime() {
		return lastUsageTime;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
}