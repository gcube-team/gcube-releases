package org.gcube.gcat.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.xml.ws.WebServiceException;

import org.gcube.gcat.api.GCatConstants;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Group extends GCatClient implements org.gcube.gcat.api.interfaces.Group<String,Void> {
	
	public Group() throws MalformedURLException {
		super(GROUPS);
	}
	
	public Group(URL enforcedServiceURL) throws MalformedURLException {
		super(enforcedServiceURL, GROUPS);
	}
	
	@Override
	public String list(int limit, int offset) throws WebApplicationException {
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put(GCatConstants.LIMIT_PARAMETER, String.valueOf(limit));
		queryParams.put(GCatConstants.OFFSET_PARAMETER, String.valueOf(offset));
		return super.list(queryParams);
	}
	
	@Override
	public String create(String json) {
		return super.create(json);
	}

	@Override
	public String read(String name) {
		return super.read(name);
	}

	@Override
	public String update(String name, String json) {
		return super.update(json, name);
	}

	@Override
	public String patch(String name, String json) {
		return super.patch(json, name);
	}
	
	@Override
	public Void delete(String name) {
		super.delete(false, name);
		return null;
	}

	@Override
	public Void delete(String name, boolean purge) throws WebServiceException {
		super.delete(purge, name);
		return null;
	}

	@Override
	public Void purge(String name) throws WebServiceException {
		super.delete(true, name);
		return null;
	}
	
}
