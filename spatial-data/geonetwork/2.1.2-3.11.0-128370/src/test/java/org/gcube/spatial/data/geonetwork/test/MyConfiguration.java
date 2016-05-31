package org.gcube.spatial.data.geonetwork.test;

import java.util.HashMap;
import java.util.Map;

import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;

public class MyConfiguration implements Configuration {

	private static final Map<LoginLevel,String> pwds=new HashMap<LoginLevel, String>();
	private static final Map<LoginLevel,String> usrs=new HashMap<LoginLevel, String>();
	static {
		pwds.put(LoginLevel.DEFAULT, "admin");
		usrs.put(LoginLevel.DEFAULT, "admin");
	}
	
	@Override
	public String getGeoNetworkEndpoint() {
		return "http://geoserver-dev3.d4science-ii.research-infrastructures.eu:8080/geonetwork";
	}

	@Override
	public Map<LoginLevel, String> getGeoNetworkUsers() {
		return usrs;
	}

	@Override
	public Map<LoginLevel, String> getGeoNetworkPasswords() {
		return pwds;
	}

	@Override
	public int getScopeGroup() {
		return 2;
	}

}
