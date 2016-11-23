/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONStringer;
import org.codehaus.jettison.json.JSONWriter;
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
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
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
import com.tinkerpop.blueprints.Direction;
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
				if (IsRelatedTo.class.isAssignableFrom(relationClass)) {
					relationType = IsRelatedTo.NAME;
				}
				if (ConsistsOf.class.isAssignableFrom(relationClass)) {
					relationType = ConsistsOf.NAME;
				}
			}
			return Utility.getRelationByUUID(orientGraph, relationType, uuid);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}

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

	private static Header getHeader(JsonNode jsonNode)
			throws JsonParseException, JsonMappingException, IOException {
		if (jsonNode.has(Resource.HEADER_PROPERTY)) {
			JsonNode header = jsonNode.get(Resource.HEADER_PROPERTY);
			if (header.isNull()) {
				return null;
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(header.toString(), Header.class);
		}
		return null;
	}

	private static void checkEmbeddedType(JsonNode jsonNode) throws ResourceRegistryException {
		if(jsonNode.has(Entities.CLASS_PROPERTY)){
			// Complex type
			String type = getClassProperty(jsonNode);
			
			SchemaManagementImpl schemaManagement = new SchemaManagementImpl();
			try {
				schemaManagement.getTypeSchema(type, Embedded.NAME);
				
			} catch (SchemaNotFoundException e) {
				throw e;
			}
			
			Header header = null;
			try {
				header = getHeader(jsonNode);
			} catch (Exception e){
				logger.warn("An invalid Header has been provided. An embedded object cannot have an Header.  It will be ignored.");
				throw new ResourceRegistryException("An embedded object cannot have an Header");
			}
				
			if(header!=null){
				logger.warn("An embedded object cannot have an Header. It will be ignored.");
				throw new ResourceRegistryException("An embedded object cannot have an Header");
			}

		}
	}
	
	public static Object getObejctFromElement(JsonNode value) throws ResourceRegistryException {
		JsonNodeType jsonNodeType = value.getNodeType();
		switch (jsonNodeType) {
			case OBJECT:
				checkEmbeddedType(value);
				return null;
		
			case ARRAY:
				List<Object> array = new ArrayList<>();
				Iterator<JsonNode> arrayElement = value.elements();
				while(arrayElement.hasNext()){
					JsonNode arrayNode = arrayElement.next();
					Object objectNode = getObejctFromElement(arrayNode);
					if(objectNode!=null){
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
				if(value.isDouble() || value.isFloat()){
					return value.asDouble();
				}
				if(value.isBigInteger() || value.isShort() || value.isInt()){
					return value.asInt();
				}
				
				if(value.isLong()){
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
			Set<String> ignoreKeys) throws JsonProcessingException, IOException{
		
		Map<String, Object> map = new HashMap<>();
				
		Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();
		while (fields.hasNext()) {
			Entry<String, JsonNode> entry = fields.next();
			
			String key = entry.getKey();
			if (key.compareTo(Relation.HEADER_PROPERTY) == 0) {
				continue;
			}
			if (key.startsWith("@") || key.startsWith("_")) {
				continue;
			}
			if (ignoreKeys.contains(key)) {
				continue;
			}
			
			JsonNode value = entry.getValue();
			Object object = null;
			try {
				object = getObejctFromElement(value);
				if(object!=null){
					map.put(key, object);
				}
			} catch (ResourceRegistryException e) {
				logger.warn("An invalidy property has been provided. It will be ignored.");
			}
			
			
			
		}
		
		return map;
	}
	
	private void createRelation(OrientGraph orientGraph, Vertex resource, JsonNode relationArray,
			@SuppressWarnings("rawtypes") Class<? extends Relation> relation) throws FacetNotFoundException,
			ResourceNotFoundException, ResourceRegistryException {
		
		Iterator<JsonNode> iterator = relationArray.elements();
		while (iterator.hasNext()) {
			JsonNode node = iterator.next();

			/* Managing Target */
			JsonNode target = node.get(Relation.TARGET_PROPERTY);
			
			Header targetHeader = null;
			try {
				targetHeader = getHeader(target);
			} catch (IOException e) {
				new ResourceRegistryException(e);
			}

			Class<? extends Entity> targetClass = null;
			
			
			Vertex targetVertex = null;
			if (targetHeader == null) {
				if (ConsistsOf.class.isAssignableFrom(relation)) {
					targetVertex = createVertexEntity(
							orientGraph, getClassProperty(target), 
							Facet.class,
							target.toString(), true);
					targetClass = Facet.class;
					targetHeader = targetVertex.getProperty(Facet.HEADER_PROPERTY);
				} else {
					String error = String.format(
							"%s %s must already exist. The UUID must be provided in the %s of %s json respresentation", 
							Relation.TARGET_PROPERTY, Resource.NAME, 
							Header.NAME, IsRelatedTo.NAME);
					logger.error(error);
					throw new ResourceRegistryException(error);
				}
			}else {
				// The target Entity was already created we just need to create
				// the right relation
				
				if(ConsistsOf.class.isAssignableFrom(relation)) {
					targetClass = Facet.class;
				}else if(IsRelatedTo.class.isAssignableFrom(relation)){
					targetClass = Resource.class;
				}else{
					String error = String.format(
							"%s Unsupported %s creation", relation.toString(),
							Relation.NAME);
					logger.error(error);
					throw new ResourceRegistryException(error);
				}
				
				String targetUUID = targetHeader.getUUID().toString();
				String entityType = getClassProperty(target);
				
				targetVertex = getEntity(orientGraph, targetUUID, entityType, targetClass);
			}
			
			String relationType = getClassProperty(node);
			
			
			Set<String> ignoreKeys = new HashSet<>();
			ignoreKeys.add(Relation.TARGET_PROPERTY);
			ignoreKeys.add(Relation.SOURCE_PROPERTY);
			
			Map<String, Object> edgeProperties = null;
			try {
				edgeProperties = getPropertyMap(node, ignoreKeys);
			}catch(Exception e){
				String error = "Error while parsing json to get Relation properties";
				logger.error(error, e);
				throw new ResourceRegistryException(error, e);
			}
			
			createEdgeRelation(orientGraph, resource, targetVertex, 
					relationType, relation, edgeProperties, true);

		}
	}
	
	public Vertex createVertexEntity(String entityType,
			Class<? extends Entity> entity, String jsonRepresentation,
			boolean deferredCommit) throws ResourceRegistryException {
		OrientGraph orientGraph = ContextUtility
				.getActualSecurityContextGraph(PermissionMode.WRITER);
		return createVertexEntity(orientGraph, entityType, entity, jsonRepresentation, 
				deferredCommit);
	}
	
	public Vertex createVertexEntity(OrientGraph orientGraph, String entityType,
			Class<? extends Entity> entity, String jsonRepresentation,
			boolean deferredCommit) throws ResourceRegistryException {
		
		try {
			
			SchemaManagementImpl schemaManagement = new SchemaManagementImpl();
			try {
				schemaManagement.getTypeSchema(entityType,
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

			OrientVertex vertex = orientGraph.addVertex("class:"
					+ entityType);

			Header entityHeader = getHeader(jsonNode);
			if (entityHeader != null) {
				vertex.setProperty(Entity.HEADER_PROPERTY, entityHeader);
			} else {
				entityHeader = HeaderUtility.addHeader(vertex, null);
			}

			if (Resource.class.isAssignableFrom(entity)) {
				// Facet and relation are created in calling method
			} else {
				Iterator<Entry<String, JsonNode>> iterator = jsonNode.fields();
				while (iterator.hasNext()) {
					Entry<String, JsonNode> entry = iterator.next();
					if (entry.getKey().compareTo(Facet.HEADER_PROPERTY) == 0) {
						continue;
					}
					if (entry.getKey().startsWith("@")
							|| entry.getKey().startsWith("_")) {
						continue;
					}
					JsonNode value = entry.getValue();
					vertex.setProperty(entry.getKey(), value.asText());
				}
			}

			ContextUtility.addToActualContext(orientGraph, vertex);

			vertex.save();

			if (!deferredCommit) {
				orientGraph.commit();

				logger.trace("Created {} is {} orientVertexToJsonString",
						Vertex.class.getSimpleName(), Utility
								.toJsonString((OrientVertex) vertex, true));
			}

			return vertex;

		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException("Error Creating " + entityType
					+ " with " + jsonRepresentation, e.getCause());
		} finally {
			if (orientGraph != null && !deferredCommit) {
				orientGraph.shutdown();
			}
		}
	}

	public Edge createEdgeRelation(
			String sourceUUID, Class<? extends Entity> sourceClass, 
			String targetUUID, Class<? extends Entity> targetClass, 
			String relationType, @SuppressWarnings("rawtypes") Class<? extends Relation> relationBaseClass, 
			String jsonProperties)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		OrientGraph orientGraph = null;

		if (relationType == null || relationType.compareTo("") == 0) {
			throw new ResourceRegistryException(Relation.class.getSimpleName()
					+ "type cannot be empty or null");
		}

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Vertex source = getEntity(orientGraph, sourceUUID, null,
					sourceClass);
			Vertex target = getEntity(orientGraph, targetUUID, null,
					targetClass);
			
			Map<String, Object> edgeProperties = new HashMap<>();
			
			if (jsonProperties != null && jsonProperties.compareTo("") != 0) {
				try {
					Set<String> ignoreKeys = new HashSet<>();
					ignoreKeys.add(Relation.SOURCE_PROPERTY);
					ignoreKeys.add(Relation.TARGET_PROPERTY);
					
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNode = mapper.readTree(jsonProperties);
					
					edgeProperties = getPropertyMap(jsonNode, ignoreKeys);
					
				} catch (Exception e) {
					new ResourceRegistryException(
							"Error while setting Relation Properties", e);
				}
			}
			
			return createEdgeRelation(orientGraph, source, target, 
					relationType, relationBaseClass, edgeProperties, false);
			
		} catch(ResourceNotFoundException rnfe){
			throw rnfe;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}
		
	}
	
	public Edge createEdgeRelation(OrientGraph orientGraph, 
			Vertex source, Vertex target, 
			String relationType, @SuppressWarnings("rawtypes") Class<? extends Relation> relationBaseClass,
			Map<String, Object> edgeProperties,
			boolean deferredCommit) throws ResourceRegistryException {
		

		try {
			
			SchemaManagementImpl schemaManagement = new SchemaManagementImpl();
			try {
				schemaManagement.getTypeSchema(relationType, relationBaseClass.getSimpleName());
			} catch (SchemaNotFoundException e) {
				throw e;
			}
			
			if (relationType == null || relationType.compareTo("") == 0) {
				throw new ResourceRegistryException(Relation.class.getSimpleName()
						+ " Type cannot be empty or null");
			}
			
			// TODO Check the relation compatibility between source and target

			logger.trace("Creating {} ({}) beetween {} -> {}",
					Relation.class.getSimpleName(), relationType,
					Utility.toJsonString(source),
					Utility.toJsonString(target));

			Edge edge = orientGraph.addEdge(null, source, target, relationType);

			for(String key : edgeProperties.keySet()){
				try {
					edge.setProperty(key, edgeProperties.get(key));
				} catch (Exception e) {
					String error = String.format("Error while setting property %s : %s", key, edgeProperties.get(key).toString());
					logger.error(error);
					throw new ResourceRegistryException(error, e);
				}
			}
					

			HeaderUtility.addHeader(edge, null);
			ContextUtility.addToActualContext(orientGraph, edge);

			((OrientEdge) edge).save();

			if (!deferredCommit) {
				orientGraph.commit();
			}
			return edge;

		} catch (ResourceRegistryException rre) {
			if (orientGraph!=null) {
				orientGraph.rollback();
			}
			throw rre;
		} catch (Exception e) {
			if (orientGraph!=null) {
				orientGraph.rollback();
			}
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph!= null && !deferredCommit) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public String createFacet(String facetType, String jsonRepresentation)
			throws ResourceRegistryException {
		Vertex vertex = createVertexEntity(facetType, Facet.class, jsonRepresentation, false);
		return Utility.toJsonString((OrientVertex) vertex, false);
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

			logger.trace("{} of type {} with UUID {} is {}", Facet.NAME,
					facetType, uuid, Utility.toJsonString((OrientVertex) facet, true));

			return Utility.toJsonString((OrientVertex) facet, true);
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

			String entityType = getClassProperty(jsonNode);
			Vertex facet = getEntity(orientGraph, uuid, entityType, Facet.class);

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

			logger.trace("{} with UUID {} has been updated {}", Facet.NAME,
					uuid, Utility.toJsonString((OrientVertex) facet, true));

			return Utility.toJsonString((OrientVertex) facet, false);

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
		Edge edge = createEdgeRelation(resourceUUID, Resource.class, 
				facetUUID, Facet.class, 
				consistOfType, ConsistsOf.class,
				jsonProperties);
		return Utility.toJsonString((OrientEdge) edge, false);
	}

	@Override
	public boolean detachFacet(String consistOfUUID)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = getRelation(orientGraph, consistOfUUID,
					ConsistsOf.NAME, ConsistsOf.class);

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
		Edge edge = createEdgeRelation(sourceResourceUuid, Resource.class,
				targetResourceUuid, Resource.class, 
				relatedToType, IsRelatedTo.class,
				jsonProperties);
		return Utility.toJsonString((OrientEdge) edge, false);
	}

	@Override
	public boolean detachResource(String relatedToUUID)
			throws ResourceRegistryException {
		OrientGraph orientGraph = null;

		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			Edge edge = getRelation(orientGraph, relatedToUUID,
					IsRelatedTo.NAME, IsRelatedTo.class);

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

	private static String marshallResource(Vertex vertex) throws JSONException{
		JSONObject jsonObject = Utility.toJsonObject((OrientVertex) vertex, true);
		
		JSONArray consistsOfArray = new JSONArray();
		
		Iterable<Edge> edges = vertex.getEdges(Direction.OUT);
		
		for(Edge edge : edges){
			
			String edgeType = edge.getLabel();
			
			try {
				SchemaManagementImpl schemaManagement = new SchemaManagementImpl();
				schemaManagement.getTypeSchema(edgeType, ConsistsOf.NAME);
				
				JSONObject jsonObjectEdge = Utility.toJsonObject((OrientEdge) edge, true);
				Vertex facetVertex = edge.getVertex(Direction.IN);
				jsonObjectEdge.put(Relation.TARGET_PROPERTY, Utility.toJsonObject((OrientVertex) facetVertex, true));
				consistsOfArray.put(jsonObjectEdge);
				
			} catch (SchemaNotFoundException e) {
				// This not an ConsistsOf Edge. it will be skipped
			}
			
		}
		
		jsonObject.put(lowerCaseFirstCharacter(ConsistsOf.NAME), consistsOfArray);
		
		return jsonObject.toString();
	}
	
	
	@Override
	public String createResource(String resourceType, String jsonRepresentation)
			throws ResourceRegistryException {
		
		logger.trace("Trying to create {} : {}", resourceType,
					jsonRepresentation);
		
		OrientGraph orientGraph = null;
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);
			
			Vertex resource = createVertexEntity(orientGraph, resourceType, Resource.class, jsonRepresentation, true);
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(jsonRepresentation);
			
			String property = lowerCaseFirstCharacter(ConsistsOf.NAME);
			if (jsonNode.has(property)) {
				JsonNode jsonNodeArray = jsonNode.get(property);
				createRelation(orientGraph, resource, jsonNodeArray, ConsistsOf.class);
			}
	
			property = lowerCaseFirstCharacter(IsRelatedTo.NAME);
			if (jsonNode.has(property)) {
				JsonNode jsonNodeArray = jsonNode.get(property);
				createRelation(orientGraph, resource, jsonNodeArray, IsRelatedTo.class);
			}
			
			orientGraph.commit();
			
			return marshallResource(resource);
			
		} catch(ResourceRegistryException rre) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw rre;
		} catch(Exception e){
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

			logger.trace("{} of type {} with UUID {} is {}", Resource.NAME,
					resourceType, uuid, Utility.toJsonString((OrientVertex) resource, true));

			return marshallResource(resource);
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
