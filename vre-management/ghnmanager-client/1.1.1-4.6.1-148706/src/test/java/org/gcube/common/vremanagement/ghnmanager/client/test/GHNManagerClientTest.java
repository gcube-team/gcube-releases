package org.gcube.common.vremanagement.ghnmanager.client.test;

import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.ghnmanager.client.GHNManagerLibrary;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.RIData;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ScopeRIParams;
import org.gcube.common.vremanagement.ghnmanager.client.proxies.Proxies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class GHNManagerClientTest {
	
	GHNManagerLibrary library=null;

	@Before
	public void initialize(){

		ScopeProvider.instance.set("/gcube/devsec");
		library = Proxies.service().withTimeout(1, TimeUnit.MINUTES).build();


	}
	/**
	 * @param args
	 */
	@Test
	public void activateRITest()throws Exception{

		RIData params = new RIData();			
		params.setClazz("VREManagement");
		params.setName("Deployer");
		System.out.println("Activating RI..");
		Assert.assertTrue(library.activateRI(params));	
		System.out.println("done");

	}

	/**
	 * @param args
	 */
	@Test
	public void shutdownTest()throws Exception{

		ShutdownOptions options = new ShutdownOptions();
		options.setRestart(true);
		options.setClean(false);								
		library.shutdown(options);

	}
	@Test
	public void addGHNtoScopeTest() throws Exception{
		AddScopeInputParams params = new AddScopeInputParams();
		params.setScope("/gcube/devsec/devVRE");
		params.setMap(""); //eventually, set here the new Service Map
		Assert.assertTrue(library.addScope(params));	

	}
	@Test
	public void addRItoScopeTest() throws Exception{
		ScopeRIParams params = new ScopeRIParams();			
		params.setClazz("VREManagement");
		params.setName("GHNManager");
		params.setScope("/gcube/devsec/devVRE");
		Assert.assertTrue(library.addRIToScope(params));
	}
	@Test
	public void deactivateRITest() throws Exception{
		RIData params = new RIData();			
		params.setClazz("VREManagement");
		params.setName("Deployer");
		Assert.assertTrue(library.deactivateRI(params));
	}

	@Test
	public void removeGHNFromScopeTest() throws Exception{

		Assert.assertTrue(library.removeScope("/gcube/devsec/devVRE"));
	}
	
	@Test
	public void removeRIFromScopeTest() throws Exception{
		ScopeRIParams params = new ScopeRIParams();			
		params.setClazz("VREManagement");
		params.setName("GHNManager");
		params.setScope("/gcube/devsec/devVRE");
		Assert.assertTrue(library.removeRIFromScope(params));
	}

}
