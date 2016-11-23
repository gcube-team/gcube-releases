package org.gcube.informationsystem.resourceregistry.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

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
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * @author Lucio Lelii (lucio.lelii@isti.cnr.it)
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
	 * e.g. GET /resource-registry/access?query=SELECT FROM V
	 * @param query
	 * @param fetchPlan
	 * @return
	 * @throws InvalidQueryException
	 */
	@GET
	public String query(@QueryParam(AccessPath.QUERY_PARAM) String query,
			@QueryParam(AccessPath.FETCH_PLAN_PARAM) String fetchPlan)
			throws InvalidQueryException {
		logger.info("Requested query (fetch plan {}):\n{}", fetchPlan, query);
		return queryManager.execute(query, fetchPlan);
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
	public String getFacet(@PathParam(ID_PATH_PARAM) String facetId)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.info("Requested Facet with id {}", facetId);
		return entityManager.readFacet(facetId);
	}

	/**
	 * e.g. GET /resource-registry/access/facet/schema/ContactFacet
	 * @param facetType
	 * @return
	 * @throws SchemaNotFoundException
	 */
	@GET 
	@Path(AccessPath.FACET_PATH_PART + "/" + AccessPath.SCHEMA_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
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
	@GET @Path(AccessPath.RESOURCE_PATH_PART + "/" + AccessPath.INSTANCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public String getResource(@PathParam(ID_PATH_PARAM) String resourceId)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.info("Requested Resource with id {}", resourceId);
		return entityManager.readResource(resourceId);
	}

	/**
	 * e.g. GET /resource-registry/access/resource/schema/HostingNode
	 * @param resourceType
	 * @return
	 * @throws SchemaNotFoundException
	 */
	@GET @Path(AccessPath.RESOURCE_PATH_PART + "/" + AccessPath.SCHEMA_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	public String getResourceSchema(@PathParam(TYPE_PATH_PARAM) String resourceType)
			throws SchemaNotFoundException {
		logger.info("Requested Resource Schema for type {}", resourceType);
		return schemaManager.getResourceSchema(resourceType);
	}
	
}