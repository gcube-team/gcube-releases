package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.List;

public interface ResourceHandler<T> {

	public List<T> initialize() throws Exception;
	
	public void add(T resource) throws Exception;
	
	public void drop(String resourceId) throws Exception;
}
