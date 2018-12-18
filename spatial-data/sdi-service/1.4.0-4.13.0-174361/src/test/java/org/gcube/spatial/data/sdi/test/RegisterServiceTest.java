package org.gcube.spatial.data.sdi.test;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.GISManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.GeoNetworkManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.RoleManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.SDIManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.ThreddsManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.model.ParameterType;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;
import org.gcube.spatial.data.sdi.test.factories.ThreddsManagerFactory;

import ch.qos.logback.core.util.SystemInfo;

public class RegisterServiceTest {

	
	public static void main(String[] args) throws MalformedURLException, ServiceRegistrationException {
		TokenSetter.set("/d4science.research-infrastructures.eu/gCubeApps/AlienAndInvasiveSpecies");
		
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		
		SDIManagerImpl sdi=new SDIManagerImpl(new GeoNetworkManagerImpl(new RoleManagerImpl()), new ThreddsManagerFactory().provide(), new GISManagerImpl());
		
		
		System.out.println(sdi.registerService(getGNDefinition()));
		
		
		
		
	}
	
	
	private static GeoNetworkServiceDefinition getGNDefinition() {
		GeoNetworkServiceDefinition def=new GeoNetworkServiceDefinition();
		def.setAdminPassword("gCube@Gn321_sdi");
		def.setDescription("GeoNetwork v3 Serving Demo VREs");
		def.setHostname("geonetwork-spatialdata.d4science.org");
		def.setMajorVersion((short)3);
		def.setMinorVersion((short)2);
		def.setReleaseVersion((short)1);
		def.setName("GeoNetwork 3 Alien");
		def.setPriority(10);
		ParameterType[] params=new ParameterType[] {
			new ParameterType("suffixes",""),
			//devNext Manually configured
//			new ParameterType("scope1","devNext"),
//			new ParameterType("scopeUser1","devNext_context"),
//			new ParameterType("scopePwd1","123456"),
//			new ParameterType("ckanUser1","devNext_ckan"),
//			new ParameterType("scopePwd1","987456"),
//			new ParameterType("mngUser1","devNext_manager"),
//			new ParameterType("mngPwd1","741852"),
//			new ParameterType("default1","1"),
//			new ParameterType("public1","1"),
//			new ParameterType("confidential1","1"),
//			new ParameterType("private1","1"),
			//NextNext
//			new ParameterType("scope2","NextNext"),
//			new ParameterType("scopeUser2","nextNext_context"),
//			new ParameterType("scopePwd2","456789"),
//			new ParameterType("ckanUser2","nextNext_ckan"),
//			new ParameterType("scopePwd2","123789"),
//			new ParameterType("mngUser2","nextNext_manager"),
//			new ParameterType("mngPwd2","963852"),
//			new ParameterType("default2","1"),
//			new ParameterType("public2","1"),
//			new ParameterType("confidential2","1"),
//			new ParameterType("private2","1"),
		};
		def.setProperties(new ArrayList<ParameterType>(Arrays.asList(params)));
		return def;
	}
}
