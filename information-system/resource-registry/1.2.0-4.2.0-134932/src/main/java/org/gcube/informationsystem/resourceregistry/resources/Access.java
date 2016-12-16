package org.gcube.informationsystem.resourceregistry.resources;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.Query;
import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.resources.impl.EntityManagementImpl;
import org.gcube.informationsystem.resourceregistry.resources.impl.QueryImpl;
import org.gcube.informationsystem.resourceregistry.resources.impl.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 */
@Path(AccessPath.ACCESS_PATH_PART)
public class Access {

	private static Logger logger = LoggerFactory.getLogger(Access.class);

	public static final String ID_PATH_PARAM = "id";
	public static final String TYPE_PATH_PARAM = "type";
	
	protected Query queryManager = new QueryImpl();
	protected EntityManagement entityManager = new EntityManagementImpl();
	protected SchemaManagement schemaManager = new SchemaManagementImpl();

	/**
	 * It allows to query Entities and Relations in the current Context.<br />
	 * It accepts idempotent query only..
	 * <br /><br />
	 * For query syntax please refer to<br /> 
	 * <a href="https://orientdb.com/docs/last/SQL-Syntax.html" target="_blank">
	 * 		https://orientdb.com/docs/last/SQL-Syntax.html
	 * </a>
	 * <br /><br />
	 * e.g. GET /resource-registry/access?query=SELECT FROM V
	 * @param query Defines the query to send to the backend.
	 * @param limit Defines the number of results you want returned, 
	 * defaults to all results.
	 * @param fetchPlan Defines the fetching strategy you want to use. See 
	 * <a href="https://orientdb.com/docs/last/Fetching-Strategies.html" 
	 * 	target="_blank">
	 * 		https://orientdb.com/docs/last/Fetching-Strategies.html
	 * </a>
	 * @return The JSON representation of the result
	 * @throws InvalidQueryException if the query is invalid or no idempotent
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String query(@QueryParam(AccessPath.QUERY_PARAM) String query,
			@QueryParam(AccessPath.LIMIT_PARAM) int limit,
			@QueryParam(AccessPath.FETCH_PLAN_PARAM) String fetchPlan)
			throws InvalidQueryException {
		logger.info("Requested query (fetch plan {}, limit : {}):\n{}", fetchPlan, limit, query);
		return queryManager.query(query, limit, fetchPlan);
	}

	/**
	 * e.g. GET /resource-registry/access/facet/instance/4d28077b-566d-4132-b073-f4edaf61dcb9
	 * @param facetId
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@GET 
	@Path(AccessPath.FACET_PATH_PART + "/" + AccessPath.INSTANCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFacet(@PathParam(ID_PATH_PARAM) String facetId)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.info("Requested Facet with id {}", facetId);
		return entityManager.readFacet(UUID.fromString(facetId));
	}

	/**
	 * e.g. GET /resource-registry/access/facet/schema/ContactFacet
	 * @param facetType
	 * @return
	 * @throws SchemaNotFoundException
	 */
	@GET 
	@Path(AccessPath.FACET_PATH_PART + "/" + AccessPath.SCHEMA_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFacetSchema(@PathParam(TYPE_PATH_PARAM) String facetType)
			throws SchemaNotFoundException {
		logger.info("Requested Facet Schema for type {}", facetType);
		return schemaManager.getFacetSchema(facetType);
	}

	/**
	 * e.g. GET /resource-registry/access/resource/instance/cc132a2c-d0b0-45a8-92fa-7451f6a44b6d
	 * @param resourceId
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ResourceRegistryException
	 */
	@GET
	@Path(AccessPath.RESOURCE_PATH_PART + "/" + AccessPath.INSTANCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getResource(@PathParam(ID_PATH_PARAM) String resourceId)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.info("Requested Resource with id {}", resourceId);
		return entityManager.readResource(UUID.fromString(resourceId));
	}

	/**
	 * e.g. GET /resource-registry/access/resource/schema/HostingNode
	 * @param resourceType
	 * @return
	 * @throws SchemaNotFoundException
	 */
	@GET
	@Path(AccessPath.RESOURCE_PATH_PART + "/" + AccessPath.SCHEMA_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getResourceSchema(@PathParam(TYPE_PATH_PARAM) String resourceType)
			throws SchemaNotFoundException {
		logger.info("Requested Resource Schema for type {}", resourceType);
		return schemaManager.getResourceSchema(resourceType);
	}
	
}