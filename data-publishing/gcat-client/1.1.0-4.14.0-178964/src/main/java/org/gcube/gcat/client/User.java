package org.gcube.gcat.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.WebServiceException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class User extends GCatClient implements org.gcube.gcat.api.interfaces.User<String,Void> {

	public User() throws MalformedURLException {
		super(USERS);
	}
	
	public User(URL enforcedServiceURL) throws MalformedURLException {
		super(enforcedServiceURL, USERS);
	}
	
	@Override
	public String list() {
		return super.list(null);
	}
	
	@Override
	public String create(String json) throws WebServiceException {
		return super.create(json);
	}

	@Override
	public String read(String username) {
		return super.read(username);
	}

	@Override
	public String update(String username, String json) {
		return super.update(json, username);
	}

	@Override
	public Void delete(String username) {
		super.delete(null, username);
		return null;
	}


	
}
