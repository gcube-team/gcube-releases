package org.gcube.resources.federation.fhnmanager.is;

import java.net.UnknownHostException;

import java.util.Set;

import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;

import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;

public interface ISProxyInterface {

		
	public ServiceProfile getServiceProfileById(String serviceProfileId);
	
	public NodeTemplate getNodeTemplate(String serviceProfileId, String vmProviderId);
	
	public Set<NodeTemplate> getVMProviderNodeTemplates(String vmProviderId);
	
	public NodeTemplate getNodeTemplateById(String nodeTemplateId);
	
	public void addNode(Node node);
	
	public Set<Node> findNodes(String serviceProfileId, String vmProviderId);
	
	public Set<VMProvider> findVMProvidersbyServiceProfile(String serviceProfileId);
	
	public VMProvider findVMProviderbyId(String vmProviderId);
	
	public Set<ServiceProfile> getAllServiceProfiles();

	public Node getNodeById(String nodeId);

	public void deleteNode(Node node);

	public VMProvider getVMProviderById(String vmProviderid);

	public void updateIs() throws CommunicationException, UnknownHostException; //to delete from interface

	public Set<VMProvider> getAllVMProviders();

	public Set<Node> getAllNodes();

	public void deleteHostingNode(String hostname);

}
