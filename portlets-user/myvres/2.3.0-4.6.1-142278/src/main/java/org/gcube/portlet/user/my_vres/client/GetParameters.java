package org.gcube.portlet.user.my_vres.client;

public class GetParameters {
	String redirectURI;
	String state;
	String context;
	String clientId;
	
	
	public GetParameters(String redirectURI, String state, String context, String clientId) {
		super();
		this.redirectURI = redirectURI;
		this.state = state;
		this.context = context;
		this.clientId = clientId;
	}

	public String getRedirectURI() {
		return redirectURI;
	}

	public String getState() {
		return state;
	}

	public String getContext() {
		return context;
	}

	public String getClientId() {
		return clientId;
	}

	@Override
	public String toString() {
		return "GetParameters [redirectURI=" + redirectURI + ", state=" + state + ", context=" + context + ", clientId="
				+ clientId + "]";
	}


}

