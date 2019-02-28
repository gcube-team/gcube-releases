package org.gcube.gcat.api.interfaces;

import javax.xml.ws.WebServiceException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface Group<C,D> extends CRUD<C,D> {
	
	String GROUPS = "groups";

	public String list(int limit, int offset) throws WebServiceException;
	
	public String patch(String name, String json) throws WebServiceException;
	
	public D delete(String name, boolean purge) throws WebServiceException;
	
	public D purge(String name) throws WebServiceException;
	
}
