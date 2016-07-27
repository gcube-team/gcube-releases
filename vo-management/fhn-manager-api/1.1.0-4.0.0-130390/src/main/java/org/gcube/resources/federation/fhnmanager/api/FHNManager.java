package org.gcube.resources.federation.fhnmanager.api;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.gcube.resources.federation.fhnmanager.api.exception.FHNManagerException;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;

public interface FHNManager {

	VMProvider getVMProviderbyId(String vmProviderid) throws FHNManagerException;
	
	Set<VMProvider> findVMProviders(String serviceProfileId) throws FHNManagerException;

	Set<ServiceProfile> allServiceProfiles() throws FHNManagerException;

	Collection<ResourceTemplate> findResourceTemplate(String vmProviderid) throws FHNManagerException;

	Node createNode(String vmProviderId, String serviceProfileId, String resourceTemplateId) throws FHNManagerException;

	Node getNodeById(String NodeId) throws FHNManagerException;

	Set<Node> findNodes(String vmProviderId, String serviceProfileId) throws FHNManagerException, UnknownHostException;

	void startNode(String NodeId) throws FHNManagerException;

	void stopNode(String NodeId) throws FHNManagerException;

	void deleteNode(String NodeId) throws FHNManagerException;

}
