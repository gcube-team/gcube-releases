package org.gcube.common.dbinterface.persistence;

public abstract class PersistencyCallback<T> {

	public void onObjectLoaded(T obj){};
	
	public void onObjectStored(T obj){};
	
	public void onBeforeStore(T obj){};
	
	public void onObjectUpdated(T obj){};
	
	public void onObjectDeleted(T obj){};
}
