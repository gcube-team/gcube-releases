package org.gcube.portlets.admin.fhn_manager_portlet.tests;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.server.RemoteServiceImpl;
import org.gcube.portlets.admin.fhn_manager_portlet.server.VMManagerServiceInterface;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;
import org.junit.Before;
import org.junit.Test;

public class ModelCompatibility {

	VMManagerServiceInterface client;
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		client=new RemoteServiceImpl();
//		client=new RemoteServiceImpl("http://fedcloud.res.eng.it:80/fhn-manager-service/rest");
	}
	
	@Test
	public void printIps() throws RemoteException, ServiceException, UnknownHostException{
		for(RemoteNode node:client.getNodes(null, null)){
			InetAddress address = InetAddress.getByName(node.getHost()); 
			System.out.println(node.getHost()+" : "+address.getHostAddress()); 
		}
	}
	
	
	@Test
	public void translateServiceProfile() throws RemoteException, ServiceException{
		System.out.println(client.getServiceProfiles());
	}
	
	@Test
	public void translateVMProvider() throws RemoteException, ServiceException{
		System.out.println(client.getVMProviders(null, null));
	}
	
	
	@Test
	public void translateVMTemplate() throws RemoteException, ServiceException{
		System.out.println(client.getVMTemplates(null, null));
	}
	
	@Test
	public void translateRemoteNode() throws RemoteException, ServiceException{
		System.out.println(client.getNodes(null, null));
	}
	
	@Test 
	public void describeServiceProfiles() throws RemoteException, ServiceException{
		System.out.println("********************************SERVICE PROFILES**********************************");
		print(client.getServiceProfiles());
	}
	
	@Test 
	public void describeVMProvider() throws RemoteException, ServiceException{
		System.out.println("********************************VM Provider**********************************");
		print(client.getVMProviders(null,null));
	}
	
	
	@Test 
	public void describeVMTemplates() throws RemoteException, ServiceException{
		System.out.println("********************************VM TEMPLATES**********************************");
		print(client.getVMTemplates(null, null));
	}
	
	
	@Test 
	public void describeRemoteNodes() throws RemoteException, ServiceException{
		System.out.println("********************************REMOTE NODES**********************************");
		print(client.getNodes(null, null));
	}
	
	private <T extends Storable> void print(Collection<T> toPrint) throws RemoteException, ServiceException{
		for(T s: toPrint)
		System.out.println(client.describeResource(s.getType(), s.getKey()));
	}
	
}
