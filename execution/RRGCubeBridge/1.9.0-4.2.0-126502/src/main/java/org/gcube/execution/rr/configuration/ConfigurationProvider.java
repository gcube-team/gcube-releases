package org.gcube.execution.rr.configuration;

import java.util.List;

public interface ConfigurationProvider {
	public List<String> getGHNContextStartScopes();
	public List<String> getGHNContextScopes();
	public boolean isClientMode();
}
