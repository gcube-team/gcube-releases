package org.gcube.contentmanager.storageserver.startup;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigurationTest {

	static String scope="/d4science.research-infrastructures.eu";
	static String user=null;
	static String password=null;
	static String serviceClass="DataTransformation";
	static String serviceName="DataTransformationService";
	static Configuration c=null;
	
	@BeforeClass
	public static void init(){
		c=new Configuration(scope, user, password, true);
	}
	
	@Test
	public void serverAccess() {
		List <ServiceEndpoint> se=c.getStorageServiceEndpoint();
		Assert.assertNotNull(c.getServerAccess(se));
	}
	
	
	@Test
	public void getDTSHostsTest(){
		List<String> hosts=c.retrieveDTSHosts();
		for (String host:hosts){
			System.out.println("host: "+host);
		}
	}
	

}
