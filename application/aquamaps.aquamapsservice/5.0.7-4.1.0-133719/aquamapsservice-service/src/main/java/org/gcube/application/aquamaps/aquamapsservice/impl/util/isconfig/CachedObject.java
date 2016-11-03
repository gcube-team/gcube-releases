package org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig;

public class CachedObject<T> {

	
	//Milliseconds
	private static final long lifeTime=2*60*1000;
	
	private long timestamp=System.currentTimeMillis();
	private T theObject=null;
	
	public CachedObject(T theObject) {
		this.theObject=theObject;
	}
	
	public T getTheObject() {
		return theObject;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public static long getLifetime() {
		return lifeTime;
	}
	
	public boolean isvalid(){
		return System.currentTimeMillis()-timestamp<lifeTime;
	}
	
}
