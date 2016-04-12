package org.gcube.spatial.data.geonetwork.utils;

import java.io.IOException;
import java.util.Properties;

import org.gcube.spatial.data.geonetwork.GeoNetwork;

public class RuntimeParameters {

	
	
	private Properties props=new Properties();
	
	public RuntimeParameters() throws IOException {
		props.load(GeoNetwork.class.getResourceAsStream("query.properties"));
	}
	
	public Properties getProps() {
		return props;
	}
	
}
