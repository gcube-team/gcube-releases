package org.gcube.gcat.client;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.gcube.gcat.api.GCatConstants;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Profile extends GCatClient implements org.gcube.gcat.api.interfaces.Profile<String,Void> {

	public Profile() throws MalformedURLException {
		super(PROFILES);
	}

	@Override
	public String list() throws WebApplicationException {
		return super.list(null);
	}
	
	@Override
	public String create(String name, String xml) {
		try {
			initRequest();
			HttpURLConnection httpURLConnection = gxhttpStringRequest.put(xml);
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@Override
	public String read(String name) {
		try {
			initRequest();
			gxhttpStringRequest.path(name);
			gxhttpStringRequest.header("Accept", MediaType.APPLICATION_XML);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	public String read(String name, boolean asJSON) {
		if(!asJSON) {
			return read(name);
		}
		gxhttpStringRequest.header("Accept", GCatConstants.APPLICATION_JSON_CHARSET_UTF_8);
		return super.read(name);
	}
	
	@Override
	public String update(String name, String xml) {
		return super.update(xml, name);
	}
	
	@Override
	public Void delete(String name) {
		super.delete(null, name);
		return null;
	}

}
