/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.RelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.model.relation.ConsistOf;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.resources.utils.ContextUtility;
import org.gcube.informationsystem.resourceregistry.resources.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.resources.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class EntityManagementImpl implements EntityManagement {

	private static Logger logger = LoggerFactory
			.getLogger(EntityManagementImpl.class);

	public Vertex getEntity(OrientGraph orientGraph, String uuid,
			String entityType, Class<? extends Entity> entityClass)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		try {
			if (entityType == null || entityType.compareTo("") == 0) {
				if (Facet.class.isAssignableFrom(entityClass)) {
					entityType = Facet.NAME;
				}
				if (Resource.class.isAssignableFrom(entityClass)) {
					entityType = Resource.NAME;
				}
			}
			return Utility.getEntityByUUID(orientGraph, entityType, uuid);
		} catch (ResourceRegistryException e) {
			if (Facet.class.isAssignableFrom(entityClass)) {
				throw new FacetNotFoundException(e.getMessage());
			}
			if (Resource.class.isAssignableFrom(entityClass)) {
				throw new ResourceNotFoundException(e.getMessage());
			}
			throw e;
		}

	}

	public Edge getRelation(
			OrientGraph orientGraph,
			String uuid,
			String relationType,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationClass)
			throws ResourceRegistryException {
		try {
			if (relationType == null || relationType.compareTo("") == 0) {
				if (RelatedTo.class.isAssignableFrom(relationClass)) {
					relationType = RelatedTo.NAME;
				}
				if (ConsistOf.class.isAssignableFrom(relationClass)) {
					relationType = ConsistOf.NAME;
				}
			}
			return Utility.getRelationByUUID(orientGraph, relationType, uuid);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}

	}

	public String createVertexEntity(String entityType,
			Class<? extends Entity> entity, String jsonRepresentation)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			SchemaManagementImpl schemaManagement = new SchemaManagementImpl();
			try {
				schemaManagement.getTypeSchema(entityType,
						entity.getSimpleName());
			} catch (SchemaNotFoundException e) {
				throw e;
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);

			OrientVertex entityVertex = orientGraph.addVertex("class:"
					+ entityType);

			if (Resource.class.isAssignableFrom(entity)) {
				// TODO
			} else {
				Iterator<Entry<String, JsonNode>> iterator = jsonNode.fields();
				while (iterator.hasNext()) {
					Entry<String, JsonNode> entry = iterator.next();
					JsonNode value = entry.getValue();
					entityVertex.setProperty(entry.getKey(), value.asText());
				}
			}

			HeaderUtility.addHeader(entityVertex, null);
			ContextUtility.addToActualContext(orientGraph, entityVertex);

			entityVertex.save();
			orientGraph.commit();

			return Utility
					.orientVertexToJsonString((OrientVertex) entityVertex);

		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException("Error Creating " + entityType
					+ " with " + jsonRepresentation, e.getCause());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public String createEdgeRelation(String sourceUUID,
			Class<? extends Entity> sourceClass, String targetUUID,
			Class<? extends Entity> targetClass, String relationType,
			String jsonProperties) throws FacetNotFoundException,
			ResourceNotFoundException, ResourceRegistryException {
		OrientGraph orientGraph = null;

		if (relationType == null || relationType.compareTo("") == 0) {
			throw new ResourceRegistryException(Relation.class.getSimpleName()
					+ "type cannot be empty or null");
		}

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			SchemaManagementImpl schemaManagement = new SchemaManagementImpl();
			try {
				schemaManagement.getTypeSchema(relationType, Relation.NAME);
			} catch (SchemaNotFoundException e) {
				throw e;
			}

			Vertex source = getEntity(orientGraph, sourceUUID, null,
					sourceClass);
			Vertex target = getEntity(orientGraph, targetUUID, null,
					targetClass);

			// TODO Check if in and out types are compatible with the relation
			// type as defined in relation type

			logger.trace("Creating {} ({}) beetween {} -> {}",
					Relation.class.getSimpleName(), relationType,
					Utility.vertexToJsonString(source),
					Utility.vertexToJsonString(target));

			Edge edge = orientGraph.addEdge(null, source, target, relationType);

			if (jsonProperties != null && jsonProperties.compareTo("") != 0) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNode = mapper.readTree(jsonProperties);

					Iterator<Entry<String, JsonNode>> iterator = jsonNode
							.fields();
					while (iterator.hasNext()) {
						Entry<String, JsonNode> entry = iterator.next();
						try {
							JsonNode value = entry.getValue();
							edge.setProperty(entry.getKey(), value.asText());
						} catch (Exception e) {
							throw new ResourceRegistryException(
									"Error while setting property"
											+ String.valueOf(entry), e);
						}
					}
				} catch (Exception e) {
					new ResourceRegistryException(
							"Error while setting Relation Properties", e);
				}
			}

			HeaderUtility.addHeader(edge, null);
			ContextUtility.addToActualContext(orientGraph, edge);

			((OrientEdge) edge).save();
			orientGraph.commit();

			return Utility.orientEdgeToJsonString((OrientEdge) edge);
		} catch (ResourceNotFoundException rnfe) {
			throw rnfe;
		} catch (Exception e) {
			throw new ResourceNotFoundException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public String createFacet(String facetType, String jsonRepresentation)
			throws ResourceRegistryException {
		return createVertexEntity(facetType, Facet.class, jsonRepresentation);
	}

	@Override
	public String readFacet(String uuid) throws FacetNotFoundException,
			ResourceRegistryException {
		return readFacet(uuid, Facet.NAME);
	}

	@Override
	public String readFacet(String uuid, String facetType)
			throws FacetNotFoundException, ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			Vertex facet = getEntity(orientGraph, uuid, facetType, Facet.class);

			return Utility.orientVertexToJsonString((OrientVertex) facet);
		} catch (FacetNotFoundException fnfe) {
			throw fnfe;
		} catch (Exception e) {
			throw new ResourceRegistryException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public String updateFacet(String uuid, String jsonRepresentation)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);

			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			// TODO get facetType from json
			Vertex facet = getEntity(orientGraph, uuid, Facet.NAME, Facet.class);

			Set<String> oldKeys = facet.getPropertyKeys();

			Iterator<Entry<String, JsonNode>> iterator = jsonNode.fields();
			while (iterator.hasNext()) {

				Entry<String, JsonNode> entry = iterator.next();
				String key = entry.getKey();

				if (key.startsWith("_")) {
					oldKeys.remove(key);
					continue;
				}

				if (key.compareTo(Facet.HEADER_PROPERTY) == 0) {
					oldKeys.remove(key);
					continue;
				}

				JsonNode value = entry.getValue();
				facet.setProperty(key, value.asText());

				oldKeys.remove(key);

			}

			for (String key : oldKeys) {
				if (key.startsWith("_")) {
					continue;
				}
				facet.removeProperty(key);
			}

			((OrientVertex) facet).save();
			orientGraph.commit();

			return Utility.orientVertexToJsonString((OrientVertex) facet);

		} catch (FacetNotFoundException fnfe) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException("Error Updating Facet",
					e.getCause());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public boolean deleteFacet(String uuid) throws FacetNotFoundException,
			ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex facet = getEntity(orientGraph, uuid, Facet.NAME, Facet.class);

			facet.remove();
			orientGraph.commit();
		} catch (FacetNotFoundException fnfe) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}

		return true;
	}

	@Override
	public String attachFacet(String resourceUUID, String facetUUID,
			String consistOfType, String jsonProperties)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		return createEdgeRelation(resourceUUID, Resource.class, facetUUID,
				Facet.class, consistOfType, jsonProperties);
	}

	@Override
	public boolean detachFacet(String consistOfUUID)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = getRelation(orientGraph, consistOfUUID, ConsistOf.NAME,
					ConsistOf.class);

			edge.remove();
			orientGraph.commit();
		} catch (FacetNotFoundException fnfe) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}

		return true;

	}

	@Override
	public String attachResource(String sourceResourceUuid,
			String targetResourceUuid, String relatedToType,
			String jsonProperties) throws ResourceNotFoundException,
			ResourceRegistryException {
		return createEdgeRelation(sourceResourceUuid, Resource.class,
				targetResourceUuid, Resource.class, relatedToType,
				jsonProperties);
	}

	@Override
	public boolean detachResource(String relatedToUUID)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = getRelation(orientGraph, relatedToUUID, RelatedTo.NAME,
					RelatedTo.class);

			edge.remove();
			orientGraph.commit();
		} catch (FacetNotFoundException fnfe) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}

		return true;
	}

	@Override
	public String createResource(String resourceType, String jsonRepresentation)
			throws ResourceRegistryException {
		return createVertexEntity(resourceType, Resource.class,
				jsonRepresentation);
	}

	@Override
	public String readResource(String uuid) throws ResourceNotFoundException {
		return readResource(uuid, Resource.NAME);
	}

	@Override
	public String readResource(String uuid, String resourceType)
			throws ResourceNotFoundException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			Vertex resource = getEntity(orientGraph, uuid, resourceType,
					Resource.class);

			// TODO get all attached facets

			return Utility.orientVertexToJsonString((OrientVertex) resource);
		} catch (ResourceNotFoundException rnfe) {
			throw rnfe;
		} catch (Exception e) {
			throw new ResourceNotFoundException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public boolean deleteResource(String uuid)
			throws ResourceNotFoundException, ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex resource = getEntity(orientGraph, uuid, null, Resource.class);

			// TODO remove attached facets if not managed from hooks
			resource.remove();

			orientGraph.commit();
		} catch (ResourceNotFoundException fnfe) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceNotFoundException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}

		return true;
	}

}
