package org.gcube.smartgears.utils.sweeper;

public class ContextBean {
	
	private String token;
	private String context;

	public ContextBean(String token, String context) {
		super();
		this.token = token;
		this.context = context;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getContext() {
		return context;
	}
	
}
