package org.gcube.portlets.admin.fhn_manager_portlet.tests;

import java.rmi.RemoteException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.server.RemoteServiceImpl;
import org.gcube.portlets.admin.fhn_manager_portlet.server.VMManagerServiceInterface;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;
import org.junit.Before;
import org.junit.Test;

public class NodeCreation {

	VMManagerServiceInterface client;
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		client=new RemoteServiceImpl();
//		client=new RemoteServiceImpl("http://fedcloud.res.eng.it:80/fhn-manager-service/rest");
	}
	
	@Test
	public void createNode() throws RemoteException, ServiceException{
		/**
		 *  CREATE_OBJECT [parameters : {VM_PROVIDER_ID=4-2, OBJECT_TYPE=REMOTE_NODE, 
		 *  VM_TEMPLATE_ID=http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#medium, SERVICE_PROFILE_ID=2-1}]

		 * */
		String serviceProfile="2-1";
		String template="http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#medium";
		String provider="4-2";
		
		
		
		System.out.println(client.createNode(serviceProfile, template, provider));
	}
	
}
