package org.gcube.application.aquamaps.publisher.impl.datageneration;

public interface ObjectManager<T> {

	public T generate() throws Exception;
	
	public T update(T obj) throws Exception;
	
	public void destroy(T obj) throws Exception;
}
