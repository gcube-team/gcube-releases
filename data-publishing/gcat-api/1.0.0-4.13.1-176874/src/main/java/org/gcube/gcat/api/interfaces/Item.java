package org.gcube.gcat.api.interfaces;

import javax.xml.ws.WebServiceException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface Item<C,D> extends CRUD<C,D> {
	
	String ITEMS = "items";

	public String list(int limit, int offset) throws WebServiceException;
	
	public D delete(String name, boolean purge) throws WebServiceException;
	
	public D purge(String name) throws WebServiceException;
}
