package org.gcube.data.publishing.gCatFeeder.model;

import java.util.HashMap;

public class CatalogueInstanceDescriptor {

	private String url=null;
	private String user=null;
	private String password=null;
	
	private String customToken=null;
	
	private HashMap<String,String> headersParameters=new HashMap<>();

	public CatalogueInstanceDescriptor() {
		super();
	}

	public String getUrl() {
		return url;
	}

	
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getCustomToken() {
		return customToken;
	}

	public HashMap<String, String> getHeadersParameters() {
		return headersParameters;
	}

	public CatalogueInstanceDescriptor setUser(String user) {
		this.user = user;
		return this;
	}

	public CatalogueInstanceDescriptor setPassword(String password) {
		this.password = password;
		return this;
	}

	public CatalogueInstanceDescriptor setCustomToken(String customToken) {
		this.customToken = customToken;
		return this;
	}
	
	public CatalogueInstanceDescriptor addHeaderParameter(String name,String value) {
		this.headersParameters.put(name, value);
		return this;
	}
	
	public CatalogueInstanceDescriptor setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public String toString() {
		return "CatalogueInstanceDescriptor [url=" + url + ", user=" + user + ", password=" + password
				+ ", customToken=" + customToken + ", headersParameters=" + headersParameters + "]";
	}
	
	
	
}
