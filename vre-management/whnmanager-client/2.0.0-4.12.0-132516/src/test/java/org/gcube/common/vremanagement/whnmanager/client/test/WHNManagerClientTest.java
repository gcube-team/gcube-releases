package org.gcube.common.vremanagement.whnmanager.client.test;


import static org.gcube.common.vremanagement.whnmanager.client.plugins.AbstractPlugin.whnmanager;
import static org.junit.Assert.assertTrue;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
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
		ScopeProvider.instance.set("/gcube/devNext");
		//SecurityTokenProvider.instance.set("dd476d92-22cd-4144-9f77-46e6414133ac|98187548");
		proxy = whnmanager().at("dlib29.isti.cnr.it", 8080).build();
		Assert.assertNotNull(proxy);
	}
	
    @Test
	public void addScopeToGHN() throws Exception{
		assertTrue(proxy.addToContext("/gcube/devNext/NextNext"));
	}

	@Test
	public void removeScopeFromGhn() throws Exception{
		 assertTrue(proxy.removeFromContext("/gcube/devNext/NextNext"));
	}

}
