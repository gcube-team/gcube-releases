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
		return context+clientId+qualifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result
				+ ((qualifier == null) ? 0 : qualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorizationId other = (AuthorizationId) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		return true;
	}
	
	
}
