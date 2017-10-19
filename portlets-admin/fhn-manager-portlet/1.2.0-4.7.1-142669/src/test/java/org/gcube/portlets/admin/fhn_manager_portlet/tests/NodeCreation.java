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
		ScopeProvider.instance.set("/gcube/preprod/preVRE");
		client=new RemoteServiceImpl();
//		client=new RemoteServiceImpl("http://fedcloud.res.eng.it:80/fhn-manager-service/rest");
	}
	
	@Test
	public void createNode() throws RemoteException, ServiceException{
		/**
		 *  CREATE_OBJECT [parameters : {VM_PROVIDER_ID=4-2, OBJECT_TYPE=REMOTE_NODE, 
		 *  VM_TEMPLATE_ID=http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#medium, SERVICE_PROFILE_ID=2-1}]

		 * */
		String serviceProfile="06ca2f9c-cb42-433a-9e91-864186043833";
		String template="http://fedcloud.egi.eu/occi/compute/flavour/1.0#large";
		String provider="2a6baa9b-eb0b-49bf-841e-e67f97511693";
		
		
		
		System.out.println(client.createNode(serviceProfile, template, provider));
	}
	
}
