package org.gcube.portlets.admin.fhn_manager_portlet.tests;

import java.rmi.RemoteException;

import org.gcube.portlets.admin.fhn_manager_portlet.server.Context;
import org.gcube.portlets.admin.fhn_manager_portlet.server.cache.Cache;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class CacheTest {

	public static Cache cache=null; 
	
	
	@BeforeClass
	public static void initCache(){
		long nodesTTL=30000;
		long templatesTTL=2*nodesTTL;
		long providersTTL=10*templatesTTL;
		long profilesTTL=10*providersTTL;
		cache=new Cache(new Context(nodesTTL,templatesTTL,providersTTL,profilesTTL));
	}
	
	@Before
	public void initContext(){
		TokenSetter.set("/gcube/devNext");
	}
	
	@Test
	public void getNodes() throws RemoteException, ServiceException{
		cache.getNodes(null,null);
	}

	@Test
	public void getTemplates() throws RemoteException, ServiceException{
		cache.getTemplates(null,null);
	}
	
	@Test 
	public void getProviders() throws RemoteException, ServiceException{
		cache.getProviders(null,null);
	}

	@Test
	public void variousGetsFromNodes() throws RemoteException, ServiceException{
		for(RemoteNode node:cache.getNodes(null,null)){
			cache.getNodeById(node.getId());
			cache.getTemplateById(node.getVmTemplateId());
			cache.getProviderById(node.getVmProviderId());
		}
	}
	
	
	@Test
	public void variousGetsFromTemplates() throws RemoteException, ServiceException{
		for(VMTemplate template:cache.getTemplates(null,null)){
			cache.getNodes(null,template.getId());
			cache.getProviderById(template.getProviderId());
		}
	}
}
