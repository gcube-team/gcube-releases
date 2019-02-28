package org.gcube.spatial.data.sdi.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.GISManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.GeoNetworkManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.RoleManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.SDIManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.ThreddsManagerImpl;
import org.gcube.spatial.data.sdi.test.factories.ThreddsManagerFactory;

public class ConfigurationTest {

	public static void main(String[] args) throws MalformedURLException {
		TokenSetter.set("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
//		TokenSetter.set("/gcube/devNext/NextNext");
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		
		SDIManagerImpl sdi=new SDIManagerImpl(new GeoNetworkManagerImpl(new RoleManagerImpl()), new ThreddsManagerFactory().provide(), new GISManagerImpl());
		
		System.out.println(sdi.getContextConfiguration());
		System.out.println(sdi.getContextConfiguration().getGeonetworkConfiguration().get(0).getAccessibleCredentials());
	}

}
