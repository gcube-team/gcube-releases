package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class EntityManagement<E extends Entity> extends ERManagement<E,Vertex> {
	
	/**
	 * Provide a cache edge-internal-id -> RelationManagement 
	 * this avoid to recreate the relationManagement of already visited edges
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String,RelationManagement> relationManagements;
	
	protected EntityManagement(AccessType accessType) {
		super(accessType);
		
		this.ignoreKeys.add(Entity.HEADER_PROPERTY);
		
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX.toLowerCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_IN_PREFIX.toUpperCase());
		this.ignoreStartWithKeys.add(OrientVertex.CONNECTION_OUT_PREFIX.toUpperCase());
		
		this.relationManagements = new HashMap<>();
		
	}
	
	protected EntityManagement(AccessType accessType, SecurityContext workingContext, OrientGraph orientGraph) {
		this(accessType);
		this.orientGraph = orientGraph;
		setWorkingContext(workingContext);
	}
	
	@SuppressWarnings("rawtypes")
	/*
	 * It works perfectly in case of any kind of update. In case of use from create
	 * the cache does not work by using the ID because until commit the edge has a
	 * fake id starting with - (minus) sign. This not imply any collateral effect
	 * but a better solution is a desiderata.
	 */
	protected RelationManagement getRelationManagement(Edge edge) throws ResourceRegistryException {
		String id = edge.getId().toString();
		RelationManagement relationManagement = relationManagements.get(id);
		if(relationManagement == null) {
			relationManagement = ERManagementUtility.getRelationManagement(getWorkingContext(), orientGraph, edge);
			relationManagements.put(id, relationManagement);
		}
		return relationManagement;
	}
	
	protected void addToRelationManagement(@SuppressWarnings("rawtypes") RelationManagement relationManagement)
			throws ResourceRegistryException {
		Element elem = relationManagement.getElement();
		String id = elem.getId().toString();
		if(relationManagements.get(id) != null && relationManagements.get(id) != relationManagement) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("Two different instance of ");
			errorMessage.append(relationManagement.getClass().getSimpleName());
			errorMessage.append(" point to the same ");
			errorMessage.append(elem.getClass().getSimpleName());
			errorMessage.append(". ");
			errorMessage.append(Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			throw new ResourceRegistryException(errorMessage.toString());
		}
		relationManagements.put(id, relationManagement);
	}
	
	protected static JSONObject addRelation(JSONObject sourceResource, JSONObject relation, String arrayKey)
			throws ResourceRegistryException {
		JSONArray relationArray = null;
		try {
			if(sourceResource.has(arrayKey)) {
				relationArray = sourceResource.getJSONArray(arrayKey);
			} else {
				relationArray = new JSONArray();
			}
			
			relationArray.put(relation);
			sourceResource.putOpt(arrayKey, relationArray);
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		}
		return sourceResource;
	}
	
	protected Vertex createVertex() throws EntityAlreadyPresentException, ResourceRegistryException {
		
		logger.trace("Going to create {} for {} ({}) using {}", Vertex.class.getSimpleName(), accessType.getName(),
				erType, jsonNode);
		
		try {
			
			if(oClass.isAbstract()) {
				String error = String.format(
						"Trying to create an instance of %s of type %s which is abstract. The operation will be aborted.",
						accessType.getName(), erType);
				throw new ResourceRegistryException(error);
			}
			
			Vertex vertexEntity = orientGraph.addVertex("class:" + erType);
			
			try {
				if(uuid != null) {
					Vertex v = getElement();
					if(v != null) {
						String error = String.format("A %s with UUID %s already exist", erType, uuid.toString());
						throw getSpecificERAlreadyPresentException(error);
					}
				}
				
			} catch(ERNotFoundException e) {
				try {
					Element el = ERManagementUtility.getAnyElementByUUID(uuid);
					String error = String.format("UUID %s is already used by another %s. This is not allowed.",
							uuid.toString(), (el instanceof Vertex) ? Entity.NAME : Relation.NAME);
					throw getSpecificERAvailableInAnotherContextException(error);
					
				} catch(ERNotFoundException e1) {
					// OK the UUID is not already used.
				}
			} catch(ERAvailableInAnotherContextException e) {
				throw e;
			}
			
			this.element = vertexEntity;
			
			if(accessType == AccessType.RESOURCE) {
				// Facet and relation are created in calling method
			} else {
				ERManagement.updateProperties(oClass, element, jsonNode, ignoreKeys, ignoreStartWithKeys);
			}
			
			logger.info("Created {} is {}", Vertex.class.getSimpleName(),
					Utility.toJsonString((OrientVertex) element, true));
			
			return element;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			logger.trace("Error while creating {} for {} ({}) using {}", Vertex.class.getSimpleName(),
					accessType.getName(), erType, jsonNode, e);
			throw new ResourceRegistryException("Error Creating " + erType + " with " + jsonNode, e.getCause());
		}
	}
	
	@Override
	protected boolean reallyAddToContext() throws ContextException, ResourceRegistryException {
		
		getWorkingContext().addElement(getElement(), orientGraph);
		
		Iterable<Edge> edges = getElement().getEdges(Direction.OUT);
		
		for(Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = getRelationManagement(edge);
			relationManagement.internalAddToContext();
		}
		
		return true;
	}
	
	@Override
	protected boolean reallyRemoveFromContext() throws ContextException, ResourceRegistryException {
		
		Iterable<Edge> edges = getElement().getEdges(Direction.OUT);
		
		for(Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = getRelationManagement(edge);
			relationManagement.internalRemoveFromContext();
		}
		
		getWorkingContext().removeElement(getElement(), orientGraph);
		
		return true;
	}
	
	@Override
	public String reallyGetAll(boolean polymorphic) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		Iterable<Vertex> iterable = orientGraph.getVerticesOfClass(erType, polymorphic);
		for(Vertex vertex : iterable) {
			@SuppressWarnings("rawtypes")
			EntityManagement entityManagement = ERManagementUtility.getEntityManagement(getWorkingContext(),
					orientGraph, vertex);
			try {
				JSONObject jsonObject = entityManagement.serializeAsJson();
				jsonArray.put(jsonObject);
			} catch(ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. It will be excluded from results. {}",
						vertex.toString(), Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			}
		}
		return jsonArray.toString();
	}
	
}
