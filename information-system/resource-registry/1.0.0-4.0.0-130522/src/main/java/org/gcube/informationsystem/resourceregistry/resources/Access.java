package org.gcube.informationsystem.resourceregistry.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.gcube.informationsystem.resourceregistry.AccessRESTPath;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.Query;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lucio lelii (lucio.lelii@isti.cnr.it)
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@Path(AccessRESTPath.ACCESS_PATH_PART)
public class Access {

	private static Logger logger = LoggerFactory.getLogger(Access.class);

	@Inject
	Query queryManager;

	@Inject
	EntityManagement entityManager;

	@Inject
	SchemaManagement entityRegistrationManager;

	@GET
	public String query(@QueryParam(AccessRESTPath.QUERY_PARAM) String query,
			@QueryParam(AccessRESTPath.FETCH_PLAN_PARAM) String fetchPlan)
			throws InvalidQueryException {
		logger.trace("Requested query (fetch plan {}):\n{}", fetchPlan, query);
		return queryManager.execute(query, fetchPlan);
	}

	@GET
	@Path(AccessRESTPath.FACET_PATH_PART + "/{"
			+ AccessRESTPath.FACET_ID_PATH_PARAM + "}")
	public String getFacet(
			@PathParam(AccessRESTPath.FACET_ID_PATH_PARAM) String facetId)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.trace("Requested Facet with id {}", facetId);
		return entityManager.readFacet(facetId);
	}

	@GET
	@Path(AccessRESTPath.FACET_PATH_PART + "/"
			+ AccessRESTPath.SCHEMA_PATH_PART + "/{"
			+ AccessRESTPath.FACET_TYPE_PATH_PARAM + "}")
	public String getFacetSchema(
			@PathParam(AccessRESTPath.FACET_TYPE_PATH_PARAM) String facetType)
			throws SchemaNotFoundException {
		logger.trace("Requested Facet Schema for type {}", facetType);
		return entityRegistrationManager.getFacetSchema(facetType);
	}

	@GET
	@Path(AccessRESTPath.RESOURCE_PATH_PART + "/{"
			+ AccessRESTPath.RESOURCE_ID_PATH_PARAM + "}")
	public String getResource(
			@PathParam(AccessRESTPath.RESOURCE_ID_PATH_PARAM) String resourceId)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.trace("Requested Resource with id {}", resourceId);
		return entityManager.readResource(resourceId);
	}

	@GET
	@Path(AccessRESTPath.RESOURCE_PATH_PART + "/"
			+ AccessRESTPath.SCHEMA_PATH_PART + "/{"
			+ AccessRESTPath.RESOURCE_TYPE_PATH_PARAM + "}")
	public String getResourceSchema(
			@PathParam(AccessRESTPath.RESOURCE_TYPE_PATH_PARAM) String resourceType)
			throws SchemaNotFoundException {
		logger.trace("Requested Resource Schema for type {}", resourceType);
		return entityRegistrationManager.getResourceSchema(resourceType);
	}

}
