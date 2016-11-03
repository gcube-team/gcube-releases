package org.gcube.resources.federation.fhnmanager.is;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructure;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructureTemplate;
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

	public void updateIs() throws CommunicationException, UnknownHostException; 

	public Set<VMProvider> getAllVMProviders();

	public Set<Node> getAllNodes();

	public void deleteHostingNode(String hostname);

	public Node getNodeByURI(String UriId);

	public String getNodeNameById(String Id);

	public Set<NodeTemplate> getAllNodeTemplates();

	public OccopusInfrastructureTemplate returnInfraTemplate(String infraid);

	public OccopusInfrastructure returnInfra(OccopusInfrastructureTemplate a);

	OccopusInfrastructure getInfrastructureById(String infraId);

	void addInfra(OccopusInfrastructure infra);

	void deleteInfrastructure(String infra);

	Set<OccopusInfrastructure> getAllInfrastructures();

	public void updateInfra(OccopusInfrastructure a);

	String setFedCloudOccopusAuth() throws Exception;

	String setD4ScienceOccopusAuth() throws Exception;

	List<OccopusInfrastructure> getAllInfrastructuresList();


}
