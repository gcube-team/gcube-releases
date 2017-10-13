package org.gcube.common.authorization.library.enpoints;

import java.util.Map;

public class EndpointsContainer {

	private Map<Integer, AuthorizationEndpoint> endpoints;
	private String defaultInfrastructure;
	protected EndpointsContainer(Map<Integer, AuthorizationEndpoint> endpoints,
			String defaultInfrastructure) {
		super();
		this.endpoints = endpoints;
		this.defaultInfrastructure = defaultInfrastructure;
	}
	public Map<Integer, AuthorizationEndpoint> getEndpoints() {
		return endpoints;
	}
	public String getDefaultInfrastructure() {
		return defaultInfrastructure;
	}

}
