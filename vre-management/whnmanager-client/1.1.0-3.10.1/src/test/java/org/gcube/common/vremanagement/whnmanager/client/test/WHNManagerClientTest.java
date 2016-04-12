package org.gcube.common.vremanagement.whnmanager.client.test;


import static org.gcube.common.vremanagement.whnmanager.client.plugins.AbstractPlugin.whnmanager;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resourcemanagement.whnmanager.api.types.AddScopeInputParams;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WHNManagerClientTest {
	
	Logger logger = LoggerFactory.getLogger(WHNManagerClientTest.class);
	WHNManagerProxy proxy;
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube");
		proxy = whnmanager().build();
		Assert.assertNotNull(proxy);
	}
	
//	@Test
	public void addScopeToGHN() throws Exception{
		logger.trace("executing getTabularDatamanager");
		AddScopeInputParams params= new AddScopeInputParams("/gcube/devsec/devVRE", "");
		assertTrue(proxy.addScope(params));
	}

	@Test
	public void removeScopeFromGhn() throws Exception{
		 assertTrue(proxy.removeScope("/gcube/devsec/devVRE"));
	}

}
