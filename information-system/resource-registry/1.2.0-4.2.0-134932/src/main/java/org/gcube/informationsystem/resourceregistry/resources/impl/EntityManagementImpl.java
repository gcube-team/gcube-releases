/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.resources.utils.ContextUtility;
import org.gcube.informationsystem.resourceregistry.resources.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.resources.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class EntityManagementImpl implements EntityManagement {

	public static final Set<String> RELATION_IGNORE_KEYS;
	public static final Set<String> RELATION_IGNORE_START_WITH_KEYS;

	public static final Set<String> ENTITY_IGNORE_KEYS;
	public static final Set<String> ENTITY_IGNORE_START_WITH_KEYS;

	public static final Set<String> EMBEDDED_IGNORE_KEYS;
	public static final Set<String> EMBEDDED_IGNORE_START_WITH_KEYS;

	public static final String AT = "@";
	public static final String UNDERSCORE = "_";

	static {
		RELATION_IGNORE_KEYS = new HashSet<String>();
		RELATION_IGNORE_KEYS.add(Relation.HEADER_PROPERTY);
		RELATION_IGNORE_KEYS.add(Relation.TARGET_PROPERTY);
		RELATION_IGNORE_KEYS.add(Relation.SOURCE_PROPERTY);
		RELATION_IGNORE_KEYS.add(OrientBaseGraph.CONNECTION_IN.toLowerCase());
		RELATION_IGNORE_KEYS.add(OrientBaseGraph.CONNECTION_OUT.toLowerCase());
		RELATION_IGNORE_KEYS.add(OrientBaseGraph.CONNECTION_IN.toUpperCase());
		RELATION_IGNORE_KEYS.add(OrientBaseGraph.CONNECTION_OUT.toUpperCase());

		RELATION_IGNORE_START_WITH_KEYS = new HashSet<String>();
		RELATION_IGNORE_START_WITH_KEYS.add(AT);
		RELATION_IGNORE_START_WITH_KEYS.add(UNDERSCORE);

		ENTITY_IGNORE_KEYS = new HashSet<String>();
		ENTITY_IGNORE_KEYS.add(Entity.HEADER_PROPERTY);

		ENTITY_IGNORE_START_WITH_KEYS = new HashSet<String>();
		ENTITY_IGNORE_START_WITH_KEYS.add(OrientVertex.CONNECTION_IN_PREFIX
				.toLowerCase());
		ENTITY_IGNORE_START_WITH_KEYS.add(OrientVertex.CONNECTION_OUT_PREFIX
				.toLowerCase());
		ENTITY_IGNORE_START_WITH_KEYS.add(OrientVertex.CONNECTION_IN_PREFIX
				.toUpperCase());
		ENTITY_IGNORE_START_WITH_KEYS.add(OrientVertex.CONNECTION_OUT_PREFIX
				.toUpperCase());
		ENTITY_IGNORE_START_WITH_KEYS.add(AT);
		ENTITY_IGNORE_START_WITH_KEYS.add(UNDERSCORE);

		EMBEDDED_IGNORE_KEYS = new HashSet<String>();

		EMBEDDED_IGNORE_START_WITH_KEYS = new HashSet<String>();
		ENTITY_IGNORE_START_WITH_KEYS.add(AT);
		ENTITY_IGNORE_START_WITH_KEYS.add(UNDERSCORE);

	}

	private static Logger logger = LoggerFactory
			.getLogger(EntityManagementImpl.class);

	protected Vertex getEntity(OrientGraph orientGraph, UUID uuid,
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
			return Utility.getElementByUUID(orientGraph, entityType, uuid,
					Vertex.class);
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

	protected Vertex getEntity(OrientGraph orientGraph, JsonNode jsonNode,
			UUID uuid, Class<? extends Entity> entityClass)
			throws JsonParseException, JsonMappingException, IOException,
			FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {

		String classProperty = getClassProperty(jsonNode);

		try {
			SchemaManagementImpl.getTypeSchema(orientGraph, classProperty,
					classProperty);
		} catch (SchemaNotFoundException e) {
			throw e;
		}

		Header header = HeaderUtility.getHeader(jsonNode, false);
		UUID resourceUUID = header.getUUID();

		Vertex vertex = getEntity(orientGraph, resourceUUID, classProperty,
				entityClass);

		return vertex;
	}

	public Edge getRelation(
			OrientGraph orientGraph,
			UUID uuid,
			String relationType,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationClass)
			throws ResourceRegistryException {
		try {
			if (relationType == null || relationType.compareTo("") == 0) {
				if (IsRelatedTo.class.isAssignableFrom(relationClass)) {
					relationType = IsRelatedTo.NAME;
				}
				if (ConsistsOf.class.isAssignableFrom(relationClass)) {
					relationType = ConsistsOf.NAME;
				}
			}
			return Utility.getElementByUUID(orientGraph, relationType, uuid,
					Edge.class);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}

	}

	protected Edge getRelation(
			OrientGraph orientGraph,
			JsonNode jsonNode,
			String relationType,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationClass)
			throws JsonParseException, JsonMappingException, IOException,
			ResourceRegistryException {

		if (relationType == null || relationType.compareTo("") == 0) {
			String error = String.format("Invalid %s type : %s",
					relationClass.getSimpleName(), relationType);
			throw new ResourceRegistryException(error);
		}

		String classProperty = getClassProperty(jsonNode);
		if (relationType.compareTo(classProperty) != 0) {
			try {
				SchemaManagementImpl.getTypeSchema(orientGraph, relationType,
						classProperty);
			} catch (SchemaNotFoundException e) {
				throw e;
			}

		}

		Header header = HeaderUtility.getHeader(jsonNode, false);
		UUID relationUUID = header.getUUID();

		Edge edge = getRelation(orientGraph, relationUUID, relationType,
				relationClass);

		return edge;
	}

	private static String lowerCaseFirstCharacter(String string) {
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	private static String getClassProperty(JsonNode jsonNode) {
		if (jsonNode.has(Entities.CLASS_PROPERTY)) {
			return jsonNode.get(Entities.CLASS_PROPERTY).asText();
		}
		return null;
	}

	private static ODocument getEmbeddedType(JsonNode jsonNode)
			throws ResourceRegistryException {
		if (jsonNode.has(Entities.CLASS_PROPERTY)) {
			// Complex type
			String type = getClassProperty(jsonNode);

			try {
				SchemaManagementImpl.getTypeSchema(type, Embedded.NAME);
			} catch (SchemaNotFoundException e) {
				throw e;
			}

			Header header = null;
			try {
				header = HeaderUtility.getHeader(jsonNode, false);
			} catch (Exception e) {
				logger.warn("An invalid Header has been provided. An embedded object cannot have an Header.  It will be ignored.");
				throw new ResourceRegistryException(
						"An embedded object cannot have an Header");
			}

			if (header != null) {
				logger.warn("An embedded object cannot have an Header. It will be ignored.");
				throw new ResourceRegistryException(
						"An embedded object cannot have an Header");
			}

			ODocument oDocument = new ODocument(type);
			return oDocument.fromJSON(jsonNode.toString());

		}
		return null;
	}

	public static Object getObjectFromElement(JsonNode value)
			throws ResourceRegistryException {
		JsonNodeType jsonNodeType = value.getNodeType();

		switch (jsonNodeType) {
		case OBJECT:
			return getEmbeddedType(value);

		case ARRAY:
			List<Object> array = new ArrayList<>();
			Iterator<JsonNode> arrayElement = value.elements();
			while (arrayElement.hasNext()) {
				JsonNode arrayNode = arrayElement.next();
				Object objectNode = getObjectFromElement(arrayNode);
				if (objectNode != null) {
					array.add(objectNode);
				}
			}
			return array;

		case BINARY:
			break;

		case BOOLEAN:
			return value.asBoolean();

		case NULL:
			break;

		case NUMBER:
			if (value.isDouble() || value.isFloat()) {
				return value.asDouble();
			}
			if (value.isBigInteger() || value.isShort() || value.isInt()) {
				return value.asInt();
			}

			if (value.isLong()) {
				return value.asLong();
			}
			break;

		case STRING:
			return value.asText();

		case MISSING:
			break;

		case POJO:
			break;

		default:
			break;
		}

		return null;
	}

	public static Map<String, Object> getPropertyMap(JsonNode jsonNode,
			Set<String> ignoreKeys, Set<String> ignoreStartWith)
			throws JsonProcessingException, IOException {

		Map<String, Object> map = new HashMap<>();

		if (ignoreKeys == null) {
			ignoreKeys = new HashSet<>();
		}

		if (ignoreStartWith == null) {
			ignoreStartWith = new HashSet<>();
		}

		Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();

		OUTER_WHILE: while (fields.hasNext()) {
			Entry<String, JsonNode> entry = fields.next();

			String key = entry.getKey();

			if (ignoreKeys.contains(key)) {
				continue;
			}

			for (String prefix : ignoreStartWith) {
				if (key.startsWith(prefix)) {
					continue OUTER_WHILE;
				}
			}

			JsonNode value = entry.getValue();
			Object object = null;
			try {
				object = getObjectFromElement(value);
				if (object != null) {
					map.put(key, object);
				}
			} catch (ResourceRegistryException e) {
				logger.warn("An invalidy property has been provided. It will be ignored.");
			}

		}

		return map;
	}

	private Map<String, Object> getVertexProperties(JsonNode node)
			throws ResourceRegistryException {
		Map<String, Object> vertexProperties = null;
		try {
			vertexProperties = getPropertyMap(node, ENTITY_IGNORE_KEYS,
					ENTITY_IGNORE_START_WITH_KEYS);
		} catch (Exception e) {
			String error = "Error while parsing json to get Relation properties";
			logger.error(error, e);
			throw new ResourceRegistryException(error, e);
		}
		return vertexProperties;
	}

	private Map<String, Object> getEdgeProperties(JsonNode node)
			throws ResourceRegistryException {
		Map<String, Object> edgeProperties = null;
		try {
			edgeProperties = getPropertyMap(node, RELATION_IGNORE_KEYS,
					RELATION_IGNORE_START_WITH_KEYS);
		} catch (Exception e) {
			String error = "Error while parsing json to get Relation properties";
			logger.error(error, e);
			throw new ResourceRegistryException(error, e);
		}
		return edgeProperties;
	}

	private Element updateProperties(Element element, JsonNode jsonNode)
			throws ResourceRegistryException {
		Set<String> ignoreKeys = null;
		Set<String> ignoreStartWithKeys = null;

		Set<String> oldKeys = element.getPropertyKeys();

		Map<String, Object> properties;
		if (element instanceof Vertex) {
			properties = getVertexProperties(jsonNode);
			ignoreKeys = ENTITY_IGNORE_KEYS;
			ignoreStartWithKeys = ENTITY_IGNORE_START_WITH_KEYS;
		} else if (element instanceof Edge) {
			properties = getEdgeProperties(jsonNode);
			ignoreKeys = RELATION_IGNORE_KEYS;
			ignoreStartWithKeys = RELATION_IGNORE_START_WITH_KEYS;
		} else {
			String error = String.format("Error while updating {} properties",
					element.toString());
			throw new ResourceRegistryException(error);
		}

		oldKeys.removeAll(properties.keySet());

		for (String key : properties.keySet()) {
			try {
				element.setProperty(key, properties.get(key));
			} catch (Exception e) {
				String error = String.format(
						"Error while setting property %s : %s", key, properties
								.get(key).toString());
				logger.error(error);
				throw new ResourceRegistryException(error, e);
			}
		}

		OUTER_FOR: for (String key : oldKeys) {

			if (ignoreKeys.contains(key)) {
				continue;
			}

			for (String prefix : ignoreStartWithKeys) {
				if (key.startsWith(prefix)) {
					continue OUTER_FOR;
				}
			}

			element.removeProperty(key);
		}

		return element;
	}

	private Vertex getOrCreateTargetVertex(OrientGraph orientGraph,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relation,
			JsonNode target) throws ResourceRegistryException {
		Header targetHeader = null;
		try {
			targetHeader = HeaderUtility.getHeader(target, false);
		} catch (IOException e) {
			throw new ResourceRegistryException(e);
		}

		Vertex targetVertex = null;
		if (targetHeader == null) {
			if (ConsistsOf.class.isAssignableFrom(relation)) {
				targetVertex = createVertexEntity(orientGraph,
						getClassProperty(target), Facet.class,
						target.toString());
				targetHeader = targetVertex.getProperty(Facet.HEADER_PROPERTY);
			} else {
				String error = String
						.format("%s %s must already exist. The UUID must be provided in the %s of %s json respresentation",
								Relation.TARGET_PROPERTY, Resource.NAME,
								Header.NAME, IsRelatedTo.NAME);
				logger.error(error);
				throw new ResourceRegistryException(error);
			}
		} else {
			// The target Entity was already created we just need to create
			// the right relation
			Class<? extends Entity> targetClass = null;

			if (ConsistsOf.class.isAssignableFrom(relation)) {
				targetClass = Facet.class;
			} else if (IsRelatedTo.class.isAssignableFrom(relation)) {
				targetClass = Resource.class;
			} else {
				String error = String.format("%s Unsupported %s creation",
						relation.toString(), Relation.NAME);
				logger.error(error);
				throw new ResourceRegistryException(error);
			}

			UUID targetUUID = targetHeader.getUUID();
			String entityType = getClassProperty(target);

			targetVertex = getEntity(orientGraph, targetUUID, entityType,
					targetClass);
		}

		return targetVertex;
	}

	private void createRelations(OrientGraph orientGraph, Vertex resource,
			JsonNode relationArray,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relation)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {

		Iterator<JsonNode> iterator = relationArray.elements();
		while (iterator.hasNext()) {
			JsonNode node = iterator.next();

			/* Managing Target */
			JsonNode target = node.get(Relation.TARGET_PROPERTY);

			Vertex targetVertex = getOrCreateTargetVertex(orientGraph,
					relation, target);

			String relationType = getClassProperty(node);

			Map<String, Object> edgeProperties = getEdgeProperties(node);

			createEdgeRelation(orientGraph, resource, targetVertex,
					relationType, relation, edgeProperties);

		}
	}

	private Vertex createVertexEntity(OrientGraph orientGraph,
			String entityType, Class<? extends Entity> entity,
			String jsonRepresentation) throws ResourceRegistryException {

		logger.trace("Going to create {} for {} ({}) using {}",
				Vertex.class.getSimpleName(),
				entity.getClass().getSimpleName(), entityType,
				jsonRepresentation);

		try {

			try {
				SchemaManagementImpl.getTypeSchema(orientGraph, entityType,
						entity.getSimpleName());
			} catch (SchemaNotFoundException e) {
				throw e;
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);

			String type = getClassProperty(jsonNode);
			if (type != null && type.compareTo(entityType) != 0) {
				String error = String
						.format("Declared resourceType does not match with json representation %s!=%s",
								entityType, type);
				logger.trace(error);
				throw new ResourceRegistryException(error);
			}

			OrientVertex vertex = orientGraph.addVertex("class:" + entityType);

			Header entityHeader = HeaderUtility.getHeader(jsonNode, true);
			if (entityHeader != null) {
				vertex.setProperty(Entity.HEADER_PROPERTY, entityHeader);
			} else {
				entityHeader = HeaderUtility.addHeader(vertex, null);
			}

			if (Resource.class.isAssignableFrom(entity)) {
				// Facet and relation are created in calling method
			} else {
				Map<String, Object> properties = getVertexProperties(jsonNode);

				for (String key : properties.keySet()) {
					try {
						vertex.setProperty(key, properties.get(key));
					} catch (Exception e) {
						String error = String.format(
								"Error while setting property %s : %s", key,
								properties.get(key).toString());
						logger.error(error);
						throw new ResourceRegistryException(error, e);
					}
				}

			}

			ContextUtility.addToActualContext(orientGraph, vertex);

			vertex.save();

			logger.info("Created {} is {}", Vertex.class.getSimpleName(),
					Utility.toJsonString((OrientVertex) vertex, true));

			return vertex;

		} catch (Exception e) {
			logger.trace("Error while creating {} for {} ({}) using {}",
					Vertex.class.getSimpleName(), entity.getClass()
							.getSimpleName(), entityType, jsonRepresentation, e);
			throw new ResourceRegistryException("Error Creating " + entityType
					+ " with " + jsonRepresentation, e.getCause());
		}
	}

	private Edge createEdgeRelation(
			OrientGraph orientGraph,
			UUID sourceUUID,
			Class<? extends Entity> sourceClass,
			UUID targetUUID,
			Class<? extends Entity> targetClass,
			String relationType,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationBaseClass,
			String jsonProperties) throws FacetNotFoundException,
			ResourceNotFoundException, ResourceRegistryException {

		logger.trace("Trying to create {} with {}", relationType,
				jsonProperties);

		try {
			if (relationType == null || relationType.compareTo("") == 0) {
				throw new ResourceRegistryException(
						Relation.class.getSimpleName()
								+ "type cannot be empty or null");
			}

			Vertex source = getEntity(orientGraph, sourceUUID, null,
					sourceClass);
			Vertex target = getEntity(orientGraph, targetUUID, null,
					targetClass);

			Map<String, Object> edgeProperties = new HashMap<>();

			if (jsonProperties != null && jsonProperties.compareTo("") != 0) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNode = mapper.readTree(jsonProperties);

					edgeProperties = getEdgeProperties(jsonNode);

				} catch (Exception e) {
					throw new ResourceRegistryException(
							"Error while setting Relation Properties", e);
				}
			}

			return createEdgeRelation(orientGraph, source, target,
					relationType, relationBaseClass, edgeProperties);

		} catch (ResourceRegistryException e) {
			logger.trace("Error Creating {} with {}", relationType,
					jsonProperties, e);
			throw e;
		} catch (Exception e) {
			logger.trace("Error Creating {} with {}", relationType,
					jsonProperties, e);
			throw new ResourceRegistryException(e);
		}

	}

	private Edge createEdgeRelation(
			OrientGraph orientGraph,
			Vertex source,
			Vertex target,
			String relationType,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationBaseClass,
			Map<String, Object> edgeProperties)
			throws ResourceRegistryException {

		logger.debug("Trying to create {} with properties {}", relationType,
				edgeProperties);

		if (relationType == null || relationType.compareTo("") == 0) {
			throw new ResourceRegistryException(Relation.class.getSimpleName()
					+ " Type cannot be empty or null");
		}

		try {
			SchemaManagementImpl.getTypeSchema(orientGraph, relationType,
					relationBaseClass.getSimpleName());
		} catch (SchemaNotFoundException e) {
			throw e;
		}

		// TODO Check the relation compatibility between source and target

		logger.trace("Creating {} ({}) beetween {} -> {}",
				Relation.class.getSimpleName(), relationType,
				Utility.toJsonString(source, true),
				Utility.toJsonString(target, true));

		Edge edge = orientGraph.addEdge(null, source, target, relationType);

		for (String key : edgeProperties.keySet()) {
			try {
				edge.setProperty(key, edgeProperties.get(key));
			} catch (Exception e) {
				String error = String.format(
						"Error while setting property %s : %s", key,
						edgeProperties.get(key).toString());
				logger.error(error);
				throw new ResourceRegistryException(error, e);
			}
		}

		HeaderUtility.addHeader(edge, null);
		ContextUtility.addToActualContext(orientGraph, edge);

		((OrientEdge) edge).save();

		logger.info("{} with properties {} successfully created", relationType,
				edgeProperties);

		return edge;

	}

	/**
	 * @param orientGraph
	 * @param relationJsonNode
	 * @param class1
	 * @throws ResourceRegistryException
	 */
	private Edge updateRelation(
			OrientGraph orientGraph,
			JsonNode jsonNode,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationClass)
			throws ResourceRegistryException {

		logger.debug("Trying to update {} : {}", relationClass.getSimpleName(),
				jsonNode);

		try {

			String relationType = getClassProperty(jsonNode);

			if (relationType == null || relationType.compareTo("") == 0) {
				throw new ResourceRegistryException(
						Relation.class.getSimpleName()
								+ " Type cannot be empty or null");
			}

			try {
				SchemaManagementImpl.getTypeSchema(orientGraph, relationType,
						relationClass.getSimpleName());
			} catch (SchemaNotFoundException e) {
				throw e;
			}

			UUID uuid = org.gcube.informationsystem.impl.utils.Utility
					.getUUIDFromJsonNode(jsonNode);

			Edge edge = Utility.getElementByUUID(orientGraph, relationType,
					uuid, Edge.class);

			edge = (Edge) updateProperties(edge, jsonNode);
			((OrientEdge) edge).save();

			JsonNode target = jsonNode.get(Relation.TARGET_PROPERTY);
			if (target != null) {
				UUID targetUUID = org.gcube.informationsystem.impl.utils.Utility
						.getUUIDFromJsonNode(target);
				updateFacet(orientGraph, targetUUID, target);
			}

			logger.info("{} {} successfully updated", relationType, jsonNode);

			return edge;

		} catch (ResourceRegistryException rre) {
			logger.error("Error Updating {} {} ", relationClass, jsonNode, rre);
			throw rre;
		} catch (Exception e) {
			logger.error("Error Creating {} {}", relationClass, jsonNode, e);
			throw new ResourceRegistryException(e);
		}

	}

	@Override
	public String createFacet(String facetType, String jsonRepresentation)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex vertex = createVertexEntity(orientGraph, facetType,
					Facet.class, jsonRepresentation);
			return Utility.toJsonString((OrientVertex) vertex, false);
		} catch (ResourceRegistryException e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
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
	}

	@Override
	public String readFacet(UUID uuid) throws FacetNotFoundException,
			ResourceRegistryException {
		return readFacet(uuid, Facet.NAME);
	}

	@Override
	public String readFacet(UUID uuid, String facetType)
			throws FacetNotFoundException, ResourceRegistryException {

		logger.debug("Going to read {} ({}) with UUID {}", Facet.NAME,
				facetType, uuid);

		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			Vertex facet = getEntity(orientGraph, uuid, facetType, Facet.class);

			logger.info("{} of type {} with UUID {} is {}", Facet.NAME,
					facetType, uuid,
					Utility.toJsonString((OrientVertex) facet, true));

			return Utility.toJsonString((OrientVertex) facet, true);
		} catch (FacetNotFoundException fnfe) {
			logger.error("Unable to read {} ({}) with UUID {}", Facet.NAME,
					facetType, uuid, fnfe);
			throw fnfe;
		} catch (Exception e) {
			logger.error("Unable to read {} ({}) with UUID {}", Facet.NAME,
					facetType, uuid, e);
			throw new ResourceRegistryException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	protected Vertex updateFacet(OrientGraph orientGraph, UUID uuid,
			JsonNode jsonNode) throws ResourceRegistryException {
		logger.debug("Trying to update {} with UUID {} usign {}", Facet.NAME,
				uuid, jsonNode);

		String entityType = getClassProperty(jsonNode);
		Vertex facet = getEntity(orientGraph, uuid, entityType, Facet.class);

		facet = (Vertex) updateProperties(facet, jsonNode);

		((OrientVertex) facet).save();

		return facet;
	}

	@Override
	public String updateFacet(UUID uuid, String jsonRepresentation)
			throws ResourceRegistryException {

		logger.debug("Trying to update {} with UUID {} usign {}", Facet.NAME,
				uuid, jsonRepresentation);

		OrientGraph orientGraph = null;

		try {

			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);
			Vertex facet = updateFacet(orientGraph, uuid, jsonNode);

			orientGraph.commit();

			logger.info("{} with UUID {} has been updated {}", Facet.NAME,
					uuid, Utility.toJsonString((OrientVertex) facet, true));

			return Utility.toJsonString((OrientVertex) facet, false);

		} catch (ResourceRegistryException e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
		} catch (Exception e) {
			logger.debug("Unable to update {} with UUID {} usign {}",
					Facet.NAME, uuid, jsonRepresentation, e);
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
	public boolean deleteFacet(UUID uuid) throws FacetNotFoundException,
			ResourceRegistryException {

		logger.debug("Going to delete {} with UUID {}", Facet.NAME, uuid);

		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex facet = getEntity(orientGraph, uuid, Facet.NAME, Facet.class);

			facet.remove();
			orientGraph.commit();

			logger.info("{} with UUID {} was successfully deleted.",
					Facet.NAME, uuid);

		} catch (FacetNotFoundException fnfe) {
			logger.error("Unable to delete {} with UUID {}", Facet.NAME, uuid,
					fnfe);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			logger.error("Unable to delete {} with UUID {}", Facet.NAME, uuid,
					e);
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
	public String attachFacet(UUID resourceUUID, UUID facetUUID,
			String consistOfType, String jsonProperties)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		return createEdgeRelation(resourceUUID, Resource.class, facetUUID,
				Facet.class, consistOfType, ConsistsOf.class, jsonProperties);
	}

	@Override
	public boolean detachFacet(UUID consistsOfUUID)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		logger.debug(
				"Going to remove {} {} with UUID {}. {} will be detached from its {}.",
				ConsistsOf.NAME, Relation.NAME, consistsOfUUID, Facet.NAME,
				Resource.NAME);

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = getRelation(orientGraph, consistsOfUUID,
					ConsistsOf.NAME, ConsistsOf.class);

			edge.remove();
			orientGraph.commit();

			logger.info(
					"{} {} with UUID {} successfully removed. {} has been detached from its {}.",
					ConsistsOf.NAME, Relation.NAME, consistsOfUUID, Facet.NAME,
					Resource.NAME);

		} catch (FacetNotFoundException fnfe) {
			logger.error(
					"Unable to remove {} {} with UUID {}. {} has not been detached from its {}.",
					ConsistsOf.NAME, Relation.NAME, consistsOfUUID, Facet.NAME,
					Resource.NAME);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw fnfe;
		} catch (Exception e) {
			logger.error(
					"Unable to remove {} {} with UUID {}. {} has not been detached from its {}.",
					ConsistsOf.NAME, Relation.NAME, consistsOfUUID, Facet.NAME,
					Resource.NAME);
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

	private String createEdgeRelation(
			UUID sourceUUID,
			Class<? extends Entity> sourceClass,
			UUID targetUUID,
			Class<? extends Entity> targetClass,
			String relationType,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relationClass,
			String jsonProperties) throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {

			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = createEdgeRelation(orientGraph, sourceUUID,
					sourceClass, targetUUID, targetClass, relationType,
					relationClass, jsonProperties);

			orientGraph.commit();

			return Utility.toJsonString((OrientEdge) edge, false);

		} catch (ResourceRegistryException e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw e;
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
	}

	@Override
	public String attachResource(UUID sourceResourceUuid,
			UUID targetResourceUuid, String isRelatedToType,
			String jsonProperties) throws ResourceNotFoundException,
			ResourceRegistryException {
		return createEdgeRelation(sourceResourceUuid, Resource.class,
				targetResourceUuid, Resource.class, isRelatedToType,
				IsRelatedTo.class, jsonProperties);
	}

	@Override
	public boolean detachResource(UUID isRelatedToUUID)
			throws ResourceRegistryException {

		logger.debug(
				"Going to remove {} {} with UUID {}. Related {}s will be detached.",
				IsRelatedTo.NAME, Relation.NAME, isRelatedToUUID, Resource.NAME);

		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = getRelation(orientGraph, isRelatedToUUID,
					IsRelatedTo.NAME, IsRelatedTo.class);

			edge.remove();
			orientGraph.commit();

			logger.info(
					"{} {} with UUID {} successfully removed. Related {}s were detached.",
					IsRelatedTo.NAME, Relation.NAME, isRelatedToUUID,
					Resource.NAME);

		} catch (ResourceRegistryException rre) {
			logger.error(
					"Unable to remove {} {} with UUID. Related {}s will not be detached.",
					IsRelatedTo.NAME, Relation.NAME, isRelatedToUUID,
					Resource.NAME);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw rre;
		} catch (Exception e) {
			logger.error(
					"Unable to remove {} {} with UUID {}. Related {}s will not be detached.",
					IsRelatedTo.NAME, Relation.NAME, isRelatedToUUID,
					Resource.NAME);
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

	private static String marshallResource(OrientGraph orientGraph,
			Vertex vertex) throws JSONException {
		JSONObject jsonObject = Utility.toJsonObject((OrientVertex) vertex,
				true);

		JSONArray consistsOfArray = new JSONArray();

		Iterable<Edge> edges = vertex.getEdges(Direction.OUT);

		for (Edge edge : edges) {

			String edgeType = edge.getLabel();

			try {
				SchemaManagementImpl.getTypeSchema(orientGraph, edgeType,
						ConsistsOf.NAME);
			} catch (SchemaNotFoundException e) {
				// This not an ConsistsOf Edge. it will be skipped
				continue;
			}

			JSONObject jsonObjectEdge = Utility.toJsonObject((OrientEdge) edge,
					true);
			Vertex facetVertex = edge.getVertex(Direction.IN);

			jsonObjectEdge.put(Relation.TARGET_PROPERTY,
					Utility.toJsonObject((OrientVertex) facetVertex, true));
			consistsOfArray.put(jsonObjectEdge);

		}

		jsonObject.put(lowerCaseFirstCharacter(ConsistsOf.NAME),
				consistsOfArray);

		return jsonObject.toString();
	}

	@Override
	public String createResource(String resourceType, String jsonRepresentation)
			throws ResourceRegistryException {

		logger.debug("Trying to create {} using {}", resourceType,
				jsonRepresentation);

		OrientGraph orientGraph = null;
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex resource = createVertexEntity(orientGraph, resourceType,
					Resource.class, jsonRepresentation);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);

			String property = lowerCaseFirstCharacter(ConsistsOf.NAME);
			if (jsonNode.has(property)) {
				JsonNode jsonNodeArray = jsonNode.get(property);
				createRelations(orientGraph, resource, jsonNodeArray,
						ConsistsOf.class);
			}

			property = lowerCaseFirstCharacter(IsRelatedTo.NAME);
			if (jsonNode.has(property)) {
				JsonNode jsonNodeArray = jsonNode.get(property);
				createRelations(orientGraph, resource, jsonNodeArray,
						IsRelatedTo.class);
			}

			orientGraph.commit();

			String resourceString = marshallResource(orientGraph, resource);

			logger.info("{} ({}) successfully created {}", Resource.NAME,
					resourceType, resourceString);

			return resourceString;

		} catch (ResourceRegistryException rre) {
			logger.error("Unable to create {} ({}) using {}", Resource.NAME,
					resourceType, jsonRepresentation, rre);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw rre;
		} catch (Exception e) {
			logger.error("Unable to create {} ({}) using {}", Resource.NAME,
					resourceType, jsonRepresentation, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}

	}

	@Override
	public String readResource(UUID uuid) throws ResourceNotFoundException {
		return readResource(uuid, Resource.NAME);
	}

	@Override
	public String readResource(UUID uuid, String resourceType)
			throws ResourceNotFoundException {

		logger.debug("Going to read {} ({}) with UUID {}", Resource.NAME,
				resourceType, uuid);

		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			Vertex resource = getEntity(orientGraph, uuid, resourceType,
					Resource.class);

			logger.info("{} of type {} with UUID {} is {}", Resource.NAME,
					resourceType, uuid,
					Utility.toJsonString((OrientVertex) resource, true));

			return marshallResource(orientGraph, resource);
		} catch (ResourceNotFoundException rnfe) {
			logger.error("Unable to read {} ({}) with UUID {}", Resource.NAME,
					resourceType, uuid, rnfe);
			throw rnfe;
		} catch (Exception e) {
			logger.error("Unable to read {} ({}) with UUID {}", Resource.NAME,
					resourceType, uuid, e);
			throw new ResourceNotFoundException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public String updateResource(UUID resourceUUID, String jsonRepresentation)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.debug("Trying to update {} using {}", resourceUUID,
				jsonRepresentation);

		OrientGraph orientGraph = null;
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);

			Vertex resource = getEntity(orientGraph, jsonNode, resourceUUID,
					Resource.class);

			String property = lowerCaseFirstCharacter(ConsistsOf.NAME);
			if (jsonNode.has(property)) {
				JsonNode jsonNodeArray = jsonNode.get(property);
				for (JsonNode relationJsonNode : jsonNodeArray) {
					updateRelation(orientGraph, relationJsonNode,
							ConsistsOf.class);
				}

			}

			((OrientVertex) resource).save();
			orientGraph.commit();

			String resourceString = marshallResource(orientGraph, resource);

			logger.info("{} with UUID {} has been updated {}", Resource.NAME,
					resourceUUID,
					Utility.toJsonString((OrientVertex) resource, true));

			return resourceString;

		} catch (ResourceRegistryException rre) {
			logger.debug("Unable to update {} with UUID {} usign {}",
					Resource.NAME, resourceUUID, jsonRepresentation, rre);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw rre;
		} catch (Exception e) {
			logger.debug("Unable to update {} with UUID {} usign {}",
					Resource.NAME, resourceUUID, jsonRepresentation, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public boolean deleteResource(UUID uuid) throws ResourceNotFoundException,
			ResourceRegistryException {

		logger.debug("Going to delete {} with UUID {}", Resource.NAME, uuid);

		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex resource = getEntity(orientGraph, uuid, null, Resource.class);

			// TODO remove attached facets if not managed from hooks

			resource.remove();

			orientGraph.commit();

			logger.info("{} with UUID {} was successfully deleted.",
					Resource.NAME, uuid);

			return true;
		} catch (ResourceNotFoundException rnfe) {
			logger.error("Unable to delete {} with UUID {}", Resource.NAME,
					uuid, rnfe);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw rnfe;
		} catch (Exception e) {
			logger.error("Unable to delete {} with UUID {}", Resource.NAME,
					uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceNotFoundException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	protected <E extends Entity> boolean addEntityToContext(Class<E> clz,
			UUID uuid) throws FacetNotFoundException,
			ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {

		logger.debug("Going to add {} with UUID {} to actual Context",
				clz.getSimpleName(), uuid);

		OrientGraph orientGraph = null;

		try {
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			Vertex vertex = getEntity(orientGraph, uuid, null, clz);

			UUID contextUUID = ContextUtility.addToActualContext(orientGraph,
					vertex);

			if (Resource.class.isAssignableFrom(clz)) {
				Iterable<Vertex> facets = vertex.getVertices(Direction.OUT,
						ConsistsOf.NAME);
				for (Vertex facet : facets) {
					ContextUtility.addToActualContext(orientGraph, facet);
				}
			}

			orientGraph.commit();
			logger.info(
					"{} with UUID {} successfully added to actual Context with UUID {}",
					clz.getSimpleName(), uuid, contextUUID);
			return true;
		} catch (Exception e) {
			logger.error(
					"Unable to add {} with UUID {} successfully added to actual Context",
					clz.getSimpleName(), uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public boolean addResourceToContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		return addEntityToContext(Resource.class, uuid);
	}

	public boolean addFacetToContext(UUID uuid) throws FacetNotFoundException,
			ContextNotFoundException, ResourceRegistryException {
		return addEntityToContext(Facet.class, uuid);
	}

}
