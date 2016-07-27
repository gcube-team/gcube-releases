package org.gcube.informationsystem.resourceregistry.resources;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.gcube.informationsystem.model.orientdb.impl.relation.ConsistOf;
import org.gcube.informationsystem.model.relation.RelatedTo;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lucio lelii (lucio.lelii@isti.cnr.it)
 * 
 */

@Path("entity")
public class EntityManager {

	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);

	@Inject
	EntityManagement entityManager;

	/* Facets Methods */

	@Path("facet/{facetType}")
	@PUT
	public String createFacet(@PathParam("facetType") String facetType,
			String jsonRepresentation) throws EntityException,
			ResourceRegistryException {
		logger.trace("requested facet creation for type {} with json {} ",
				facetType, jsonRepresentation);
		return entityManager.createFacet(facetType, jsonRepresentation);
	}

	@Path("facet/{facetId}")
	@POST
	public String updateFacet(@PathParam("facetId") String facetId,
			String jsonRepresentation) throws FacetNotFoundException,
			ResourceRegistryException {
		logger.trace("requested facet update for id {} with json", facetId,
				jsonRepresentation);
		return entityManager.updateFacet(facetId, jsonRepresentation);
	}

	@Path("facet/{facetId}")
	@DELETE
	public boolean deleteFacet(@PathParam("facetId") String facetId)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.trace("requested facet deletion for id {}", facetId);
		return entityManager.deleteFacet(facetId);
	}

	/* Resources Methods */

	@Path("resource/{resourceType}")
	@PUT
	public String createResource(
			@PathParam("resourceType") String resourceType,
			String jsonRepresentation) throws FacetNotFoundException,
			ResourceRegistryException {
		logger.trace("requested resource creation for type {} with json {}",
				resourceType, jsonRepresentation);
		return entityManager.createResource(resourceType, jsonRepresentation);
	}

	@Path("resource/{resourceId}")
	@DELETE
	public boolean deleteResource(@PathParam("resourceId") String resourceId)
			throws ResourceNotFoundException, Exception {
		logger.trace("requested resource deletion for id {}", resourceId);
		return entityManager.deleteResource(resourceId);
	}

	/* Relations Methods */

	@Path("consistOf/source/{resourceId}/target/{facetId}")
	@PUT
	public String attachFacet(@PathParam("resourceId") String resourceUUID,
			@PathParam("facetId") String facetUUID,
			@QueryParam("consistOfType") String consistOfType,
			@QueryParam("jsonProperties") String jsonProperties)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		logger.trace(
				"requested to attach resource {} to facet {} ({} Type {}) with properties {}",
				resourceUUID, facetUUID, ConsistOf.class.getSimpleName(),
				consistOfType, jsonProperties);
		return entityManager.attachFacet(resourceUUID, facetUUID,
				consistOfType, jsonProperties);
	}

	@Path("consistOf/{consistOfId}")
	@DELETE
	public boolean detachFacet(@PathParam("consistOfId") String consistOfUUID)
			throws ResourceRegistryException {
		logger.trace("requested to detach {}", consistOfUUID);
		return entityManager.detachFacet(consistOfUUID);
	}

	@Path("relatedTo/source/{sourceResourceId}/target/{targetResourceId}")
	@PUT
	public String attachResource(
			@PathParam("sourceResourceId") String sourceResourceUuid,
			@PathParam("targetResourceId") String targetResourceUuid,
			@QueryParam("relatedToType") String relatedToType,
			@QueryParam("jsonProperties") String jsonProperties)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.trace(
				"requested to attach source resource {} and target resource {} ({} Type {}) with properties {}",
				sourceResourceUuid, targetResourceUuid,
				RelatedTo.class.getSimpleName(), relatedToType, jsonProperties);
		return entityManager.attachResource(sourceResourceUuid,
				targetResourceUuid, relatedToType, jsonProperties);
	}

	@Path("relatedTo/{relatedToId}")
	@DELETE
	public boolean detachResource(
			@PathParam("relatedToId") String relatedToUUID)
			throws ResourceRegistryException {
		logger.trace("requested to detach {}", relatedToUUID);
		return entityManager.detachResource(relatedToUUID);
	}

}
