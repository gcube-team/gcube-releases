package org.gcube.spatial.data.geonetwork.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess.Version;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;

public class MyConfiguration implements Configuration {

	@Override
	public String getGeoNetworkEndpoint()
			throws MissingServiceEndpointException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScopeConfiguration getScopeConfiguration()
			throws MissingServiceEndpointException,
			MissingConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScopeConfiguration acquireConfiguration()
			throws MissingServiceEndpointException,
			MissingConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account getAdminAccount() throws MissingServiceEndpointException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ScopeConfiguration> getExistingConfigurations()
			throws MissingServiceEndpointException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ScopeConfiguration> getParentScopesConfiguration()
			throws MissingServiceEndpointException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createScopeConfiguration(ScopeConfiguration toCreate)
			throws MissingServiceEndpointException {
		// TODO Auto-generated method stub
		
	}
@Override
public Version getGeoNetworkVersion() throws MissingServiceEndpointException {
	// TODO Auto-generated method stub
	return null;
}
	
}
