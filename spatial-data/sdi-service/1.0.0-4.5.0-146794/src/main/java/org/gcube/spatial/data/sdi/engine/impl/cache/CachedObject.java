package org.gcube.spatial.data.sdi.engine.impl.cache;

public class CachedObject<T> {

	
	
	
	private long lastUpdate=System.currentTimeMillis();
	
	
	private T theObject;
	
	
	
	
	public CachedObject(T theObject) {
		super();
		this.theObject = theObject;
	}

	public T getTheObject() {
		return theObject;
	}


	public boolean isValid(long TTL){
		return System.currentTimeMillis()-lastUpdate<TTL;
	}
	
	public void invalidate(){
		lastUpdate=0l;
	}
}
