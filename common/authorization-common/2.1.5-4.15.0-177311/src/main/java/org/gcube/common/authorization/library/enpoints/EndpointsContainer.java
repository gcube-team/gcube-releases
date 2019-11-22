package org.gcube.common.authorization.library.enpoints;

import java.util.Map;

public class EndpointsContainer {

	private Map<String, AuthorizationEndpoint> endpoints;
	private String defaultInfrastructure;
	
	protected EndpointsContainer(Map<String, AuthorizationEndpoint> endpoints,
			String defaultInfrastructure) {
		super();
		this.endpoints = endpoints;
		this.defaultInfrastructure = defaultInfrastructure;
	}
	public Map<String, AuthorizationEndpoint> getEndpoints() {
		return endpoints;
	}
	public String getDefaultInfrastructure() {
		return defaultInfrastructure;
	}

}
