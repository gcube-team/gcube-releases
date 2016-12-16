package org.gcube.resources.federation.fhnmanager.service.rest;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.api.exception.FHNManagerException;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructure;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructureTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.impl.FHNManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RestFHNManagerImpl implements FHNManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestFHNManagerImpl.class);

	private FHNManager service = new FHNManagerImpl();

	@GET
	@Path("/nodes")
	@Produces("text/xml")
	public Set<Node> findNodes(@QueryParam("serviceProfileId") String serviceProfileId,
			@QueryParam("vmProviderId") String vmProviderId) throws FHNManagerException, UnknownHostException {

		LOGGER.debug("Finding Node with serviceProfile=" + serviceProfileId + " and vmProviderId=" + vmProviderId);
		return service.findNodes(serviceProfileId, vmProviderId);
	}

	@GET
	@Path("/nodes/create")
	@Produces("text/xml")
	public Node createNode(@QueryParam("vmProviderId") String vmProviderId,
			@QueryParam("serviceProfileId") String serviceProfileId,
			@QueryParam("resourceTemplateId") String resourceTemplateId) throws FHNManagerException {

		LOGGER.debug("Creating Node with serviceProfile=" + serviceProfileId + ", VMProviderId=" + vmProviderId
				+ " and ResourceTemplateId=" + resourceTemplateId);
		return service.createNode(vmProviderId, serviceProfileId, resourceTemplateId);
	}

	@GET
	@Path("/vmproviders")
	@Produces("text/xml")
	public Set<VMProvider> findVMProviders(@QueryParam("serviceProfileId") String serviceProfileId)
			throws FHNManagerException {

		LOGGER.debug("Finding VMProviders with serviceProfile=" + serviceProfileId);

		return service.findVMProviders(serviceProfileId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.resources.federation.fhnmanager.service.rest.test#getVMProvider
	 * (java.lang.String)
	 */
	@GET
	@Path("/vmproviders/{vmProviderId}")
	@Produces("text/xml")
	public VMProvider getVMProviderbyId(@PathParam("vmProviderId") String vmProviderId) throws FHNManagerException {
		return service.getVMProviderbyId(vmProviderId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.resources.federation.fhnmanager.service.rest.test#
	 * allServiceProfiles()
	 */
	@GET
	@Path("/serviceprofiles")
	@Produces("text/xml")
	public Set<ServiceProfile> allServiceProfiles() throws FHNManagerException {
		// TODO Auto-generated method stub
		return service.allServiceProfiles();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.resources.federation.fhnmanager.service.rest.test#
	 * findResourceTemplate(java.lang.String, java.lang.String)
	 */
	@GET
	@Path("/resourceTemplate")
	@Produces("text/xml")
	public Collection<ResourceTemplate> findResourceTemplate(@QueryParam("vmProviderId") String vmProviderId)
			throws FHNManagerException {
		LOGGER.debug("Finding ResourceTemplate with vmProviderId=" + vmProviderId);
		return service.findResourceTemplate(vmProviderId);
	}

	// /* (non-Javadoc)
	// * @see
	// org.gcube.resources.federation.fhnmanager.service.rest.test#getNode(java.lang.String,
	// java.lang.String)
	// */
	// @GET @Path("/nodes/{nodeId: .*}")
	// @Produces("text/xml")
	// public Node getNodeById(@PathParam("nodeId") String nodeId) throws
	// FHNManagerException{
	// LOGGER.debug("Finding Node with Id=" +nodeId);
	// return service.getNodeById(nodeId);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.resources.federation.fhnmanager.service.rest.test#getNode(java.
	 * lang.String, java.lang.String)
	 */
	@GET
	@Path("/nodes/{nodeId}")
	@Produces("text/xml")
	public Node getNodeById(@PathParam("nodeId") String nodeId) throws FHNManagerException {
		LOGGER.debug("Finding Node with Id=" + nodeId);
		return service.getNodeById(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.resources.federation.fhnmanager.service.rest.test#startNode(
	 * java.lang.String, java.lang.String)
	 */
	@GET
	@Path("/nodes/start")
	@Produces("text/xml")
	public void startNode(@QueryParam("nodeId") String nodeId) throws FHNManagerException {
		// TODO Auto-generated method stub
		service.startNode(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.resources.federation.fhnmanager.service.rest.test#stopNode(java
	 * .lang.String, java.lang.String)
	 */
	@GET
	@Path("/nodes/stop")
	@Produces("text/xml")
	public void stopNode(@QueryParam("nodeId") String nodeId) throws FHNManagerException {
		// TODO Auto-generated method stub
		service.stopNode(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.resources.federation.fhnmanager.service.rest.test#deleteNode(
	 * java.lang.String, java.lang.String)
	 */
	@GET
	@Path("/nodes/delete")
	@Produces("text/xml")
	public void deleteNode(@QueryParam("nodeId") String nodeId) throws FHNManagerException {
		// TODO Auto-generated method stub
		service.deleteNode(nodeId);
	}

	@GET
	@Path("/infrastructures/create")
	@Produces("text/xml")
	public OccopusInfrastructure createInfrastructureByTemplate(
			@QueryParam("infrastructureTemplateId") String infrastructureTemplateId) {
		// TODO Auto-generated method stub
		LOGGER.debug("Creating Occopus Infrastructure with infrastructureTemplateId = " + infrastructureTemplateId);
		return service.createInfrastructureByTemplate(infrastructureTemplateId);

	}

	@GET
	@Path("/infrastructures/delete")
	@Produces("text/xml")
	public void destroyInfrastructure(@QueryParam("infrastructureId") String infrastructureId) {
		LOGGER.debug("Destroying Occopus Infrastructure  with id = " + infrastructureId);
		service.destroyInfrastructure(infrastructureId);

	}

	@GET
	@Path("/infrastructures/{infraId}")
	@Produces("text/xml")
	public OccopusInfrastructure getInfrastructureById(@PathParam("infraId") String infraId)
			throws FHNManagerException {
		LOGGER.debug("Finding Infra with Id=" + infraId);
		return service.getInfrastructureById(infraId);
	}

	@GET
	@Path("/infrastructures")
	@Produces("text/xml")
	public Set<OccopusInfrastructure> getAllInfrastructures() throws FHNManagerException {
		LOGGER.debug("Finding Infrastructures:");
		return service.getAllInfrastructures();
	}

}
