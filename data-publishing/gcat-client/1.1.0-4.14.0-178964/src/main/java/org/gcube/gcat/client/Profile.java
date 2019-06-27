package org.gcube.gcat.client;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.gcube.gcat.api.GCatConstants;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Profile extends GCatClient implements org.gcube.gcat.api.interfaces.Profile<String,Void> {

	public Profile() throws MalformedURLException {
		super(PROFILES);
	}
	
	public Profile(URL enforcedServiceURL) throws MalformedURLException {
		super(enforcedServiceURL, PROFILES);
	}
	
	@Override
	public String list() throws WebApplicationException {
		return super.list(null);
	}
	
	protected String createOrUpdate(String name, String xml) {
		try {
			initRequest();
			gxhttpStringRequest.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
			gxhttpStringRequest.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
			gxhttpStringRequest.path(name);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.put(xml);
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	@Override
	public String create(String name, String xml) {
		return createOrUpdate(name, xml);
	}

	@Override
	public String read(String name) {
		return read(name, false);
	}
	
	public String read(String name, boolean asJSON) {
		try {
			initRequest();
			gxhttpStringRequest.path(name);
			if(asJSON) {
				gxhttpStringRequest.header(HttpHeaders.ACCEPT, GCatConstants.APPLICATION_JSON_CHARSET_UTF_8);
			} else {
				gxhttpStringRequest.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
			}
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	@Override
	public String update(String name, String xml) {
		return createOrUpdate(name, xml);
	}
	
	@Override
	public Void delete(String name) {
		super.delete(null, name);
		return null;
	}

}
