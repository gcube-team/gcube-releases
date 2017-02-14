package org.gcube.portlets.user.geoexplorer.server.datafetcher;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */

public interface FetchingBuffer<T extends FetchingElement> {

	public abstract void add(T e) throws Exception;

	public abstract List<T> getList() throws Exception;
	
	public abstract int size() throws Exception;
	
	public abstract List<T> getList(int startIndex, int offset) throws Exception;

}