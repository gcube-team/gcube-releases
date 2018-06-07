package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNodeStatus;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMRequirement;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;

public class MockRemote implements VMManagerServiceInterface {

	static ArrayList<ServiceProfile> profiles=new ArrayList<ServiceProfile>(Arrays.asList(new ServiceProfile[]{
			new ServiceProfile("1", "0.1", "dummy Service ", new Date()),
			new ServiceProfile("2", "0.1", "dummy Service 2", new Date()),
	}));
	
	static ArrayList<VMTemplate> templates=new ArrayList<VMTemplate>(Arrays.asList(new VMTemplate[]{
			new VMTemplate("1", "Normal", 2, 1024d,"1"),
			new VMTemplate("2", "Big", 4, 2048d,"1"),
			new VMTemplate("3", "Huge", 8, 4096d,"1"),
	}));
	
	static ArrayList<VMProvider> providers=new ArrayList<VMProvider>(Arrays.asList(new VMProvider[]{
			new VMProvider("simple provider", "http://acme.org", "1")	
		}));
	
	static ArrayList<RemoteNode> nodes=new ArrayList<RemoteNode>();
	
	
	@Override
	public List<ServiceProfile> getServiceProfiles() throws RemoteException,
			ServiceException {
		return profiles; 
	}

	@Override
	public List<VMTemplate> getVMTemplates(String serviceProfileId,
			String vmProviderId) throws RemoteException, ServiceException {
		return templates;
	}

	@Override
	public List<VMTemplate> getVMTemplatesByRequirement(
			VMRequirement requirements) throws RemoteException,
			ServiceException {
		return templates;
	}

	@Override
	public List<VMProvider> getVMProviders(String serviceProfileId,
			String vmTemplateId) throws RemoteException, ServiceException {
		return providers;
	}

	@Override
	public List<RemoteNode> getNodes(String serviceProfileId,
			String vmProviderId) throws RemoteException, ServiceException {
		return nodes;
	}

	@Override
	public RemoteNode createNode(String serviceProfileId, String vmTemplateId,
			String vmProviderId) throws RemoteException, ServiceException {
		RemoteNode toAdd=new RemoteNode();
		toAdd.setServiceProfileId(serviceProfileId);
		toAdd.setStatus(RemoteNodeStatus.active);
		toAdd.setVmProviderId(vmProviderId);
		toAdd.setVmTemplateId(vmTemplateId);
		nodes.add(toAdd);
		return toAdd;
	}

	@Override
	public void startNode(String remoteNodeId) throws RemoteException,
			ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopNode(String remoteNodeId) throws RemoteException,
			ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyNode(String remoteNodeId) throws RemoteException,
			ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public DescribedResource describeResource(ObjectType type, String id)
			throws RemoteException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteNode getNodeById(String id) throws RemoteException,
			ServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public VMProvider getProviderById(String id) throws RemoteException,
			ServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
