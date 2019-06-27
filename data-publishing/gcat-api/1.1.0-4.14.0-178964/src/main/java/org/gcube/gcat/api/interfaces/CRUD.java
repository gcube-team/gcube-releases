package org.gcube.gcat.api.interfaces;

import javax.xml.ws.WebServiceException;

public interface CRUD<C,D> {
	
	public C create(String json) throws WebServiceException;
	
	public String read(String name) throws WebServiceException ;
	
	public String update(String name, String json) throws WebServiceException;
	
	public D delete(String name) throws WebServiceException;
}
