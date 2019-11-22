package org.gcube.data.analysis.tabulardata.operation.view.maps;

public class CachedObject<T> {

	
	//1 minute TTL
	private static final long lifeTime=3*60*1000;
	
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
