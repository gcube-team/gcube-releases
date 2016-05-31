package org.gcube.resources.federation.fhnmanager.is;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;

import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;

import cz.cesnet.cloud.occi.api.exception.CommunicationException;

public interface ISProxyInterface {

	public Set<VMProvider> getVMProviders();
		
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

	public void dumpNodes();

	public void updateIs() throws CommunicationException, UnknownHostException;


		
}
