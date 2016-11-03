package org.gcube.execution.rr.configuration.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.execution.rr.configuration.ConfigurationProvider;

public class ConfigurationProviderServiceImpl implements ConfigurationProvider {

	public List<String> getGHNContextStartScopes() {
		
		List<String> scopes = new ArrayList<String>();
		for (GCUBEScope scope : GHNContext.getContext().getStartScopes()){
			scopes.add(scope.toString());
		}
		return scopes;
	}

	public List<String> getGHNContextScopes() {
		List<String> scopes = new ArrayList<String>();
		for (GCUBEScope scope : GHNContext.getContext().getGHN().getScopes().values()){
			scopes.add(scope.toString());
		}
		
		return scopes;
	}

	
	public boolean isClientMode() {
		return GHNContext.getContext().isClientMode();
		
	}
}
