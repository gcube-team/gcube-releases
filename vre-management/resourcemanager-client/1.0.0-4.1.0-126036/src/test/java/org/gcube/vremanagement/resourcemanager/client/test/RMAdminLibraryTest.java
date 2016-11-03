package org.gcube.vremanagement.resourcemanager.client.test;


import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.resourcemanager.client.RMAdminLibrary;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;

public class RMAdminLibraryTest {

	public static RMAdminLibrary library=null;
	public static String rmHost="node13.d.d4science.research-infrastructures.eu";
	
//	@Before
	public void initialize(){
		ScopeProvider.instance.set("/gcube/devsec");
		library=Proxies.adminService().at("node13.d.d4science.research-infrastructures.eu", 8080).withTimeout(1, TimeUnit.MINUTES).build();
	}

//	@Test
	public void cleanSoftwareStateTest() throws ResourcesCreationException, InvalidScopeException{
		library.cleanSoftwareState();
	}
	

}
