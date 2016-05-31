package org.gcube.spatial.data.geonetwork.configuration;

import java.util.Map;

import org.gcube.spatial.data.geonetwork.LoginLevel;

public interface Configuration {

	public String getGeoNetworkEndpoint();
	public Map<LoginLevel,String> getGeoNetworkUsers();
	public Map<LoginLevel,String> getGeoNetworkPasswords();
	public int getScopeGroup();
}
