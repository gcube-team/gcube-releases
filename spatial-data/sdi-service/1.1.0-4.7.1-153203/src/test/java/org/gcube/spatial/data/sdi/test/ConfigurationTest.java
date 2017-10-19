package org.gcube.spatial.data.sdi.test;

import java.net.URL;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.GISManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.GeoNetworkManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.SDIManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.ThreddsManagerImpl;

public class ConfigurationTest {

	public static void main(String[] args) {
		TokenSetter.set("/gcube/devNext");
		
		URL propertiesURL=ConfigurationTest.class.getResource("/WEB-INF/config.properties");
		
		LocalConfiguration.init(propertiesURL);
		
		SDIManagerImpl sdi=new SDIManagerImpl(new GeoNetworkManagerImpl(), new ThreddsManagerImpl(), new GISManagerImpl());
		
		System.out.println(sdi.getContextConfiguration());
	}

}
