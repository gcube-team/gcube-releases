package org.gcube.common.authorizationservice.persistence.entities;

import javax.persistence.Embeddable;

@Embeddable
public class AuthorizationId {
		
	String context;
	String clientId;
	String qualifier;
	
	protected AuthorizationId() {
		super();
	}

	public AuthorizationId(String context, String clientId, String qualifier) {
		super();
		this.context = context;
		this.clientId = clientId;
		this.qualifier = qualifier;
	}

	@Override
	public String toString() {
		return "AuthorizationId [context=" + context
				+ ", clientId=" + clientId + " qualifier ="+qualifier+"]";
	}
	
	
}
