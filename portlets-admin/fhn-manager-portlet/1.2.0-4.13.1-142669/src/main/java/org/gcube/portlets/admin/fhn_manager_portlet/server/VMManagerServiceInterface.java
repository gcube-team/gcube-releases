package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.rmi.RemoteException;
import java.util.List;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMRequirement;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;

public interface VMManagerServiceInterface {

	public List<ServiceProfile> getServiceProfiles() throws RemoteException,ServiceException;	
	public List<VMTemplate> getVMTemplates(String serviceProfileId,String vmProviderId)throws RemoteException,ServiceException;
	public List<VMTemplate> getVMTemplatesByRequirement(VMRequirement requirements) throws RemoteException,ServiceException;
	public List<VMProvider> getVMProviders(String serviceProfileId,String vmTemplateId)throws RemoteException,ServiceException;
	public List<RemoteNode> getNodes(String serviceProfileId,String vmProviderId)throws RemoteException,ServiceException;
	
	// Actions
	
	public RemoteNode createNode(String serviceProfileId,String vmTemplateId,String vmProviderId)throws RemoteException,ServiceException;
	public void startNode(String remoteNodeId)throws RemoteException,ServiceException;
	public void stopNode(String remoteNodeId)throws RemoteException,ServiceException;
	public void destroyNode(String remoteNodeId)throws RemoteException,ServiceException;
	
	public DescribedResource describeResource(ObjectType type,String id)throws RemoteException,ServiceException;
	
	
	// By Id
	
	public RemoteNode getNodeById(String id) throws RemoteException,ServiceException;
	public VMProvider getProviderById(String id) throws RemoteException,ServiceException;
	
}
