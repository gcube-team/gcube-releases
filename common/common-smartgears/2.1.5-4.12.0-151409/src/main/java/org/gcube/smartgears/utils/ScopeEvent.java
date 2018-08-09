package org.gcube.smartgears.utils;

import java.util.Collection;

public class ScopeEvent {

	private Collection<String> scopes;

	public ScopeEvent(Collection<String> scopes) {
		super();
		this.scopes = scopes;
	}

	public Collection<String> getScopes() {
		return scopes;
	}
}
