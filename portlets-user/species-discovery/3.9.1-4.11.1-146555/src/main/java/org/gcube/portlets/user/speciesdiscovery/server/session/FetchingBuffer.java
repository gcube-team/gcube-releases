package org.gcube.portlets.user.speciesdiscovery.server.session;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;

public interface FetchingBuffer<T extends FetchingElement> {

	public abstract void add(T e) throws Exception;

	public abstract List<T> getList() throws Exception;
	
	public abstract int size() throws Exception;
	
	public abstract List<T> getList(int startIndex, int offset) throws Exception;
	
	public abstract List<T> getList(Map<String,String> filterANDMap, int startIndex, int offset) throws Exception;

}