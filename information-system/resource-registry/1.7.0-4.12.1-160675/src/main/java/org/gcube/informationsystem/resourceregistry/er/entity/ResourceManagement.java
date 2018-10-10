package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceManagement extends EntityManagement<Resource> {
	
	public ResourceManagement() {
		super(AccessType.RESOURCE);
	}
	
	public ResourceManagement(SecurityContext workingContext, OrientGraph orientGraph) {
		super(AccessType.RESOURCE, workingContext, orientGraph);
	}
	
	@Override
	protected ResourceNotFoundException getSpecificElementNotFoundException(ERNotFoundException e) {
		return new ResourceNotFoundException(e.getMessage(), e.getCause());
	}
	
	@Override
	protected ResourceAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(
			String message) {
		return new ResourceAvailableInAnotherContextException(message);
	}
	
	@Override
	protected ResourceAlreadyPresentException getSpecificERAlreadyPresentException(String message) {
		return new ResourceAlreadyPresentException(message);
	}
	
	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeAsJson().toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		
		JSONObject sourceResource = serializeSelfOnly();
		
		/*
		 * Cannot get ConsistsOf edge only because is not polymorphic for a
		 * com.tinkerpop.blueprints.Vertex vertex.getEdges(Direction.OUT,
		 * ConsistsOf.NAME); TODO Looks for a different query
		 */
		
		Iterable<Edge> edges = getElement().getEdges(Direction.OUT);
		for(Edge edge : edges) {
			
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = getRelationManagement(edge);
			relationManagement.setReload(reload);
			
			if(relationManagement.giveMeSourceEntityManagementAsIs() == null) {
				relationManagement.setSourceEntityManagement(this);
			}
			
			if(relationManagement.giveMeSourceEntityManagementAsIs() != this) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append("SourceEntityManagement for ");
				errorMessage.append(relationManagement.getClass().getSimpleName());
				errorMessage.append(" is not the one expected. ");
				errorMessage.append(Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				throw new ResourceRegistryException(errorMessage.toString());
			}
			
			if(relationManagement instanceof ConsistsOfManagement) {
				try {
					JSONObject consistsOf = relationManagement.serializeAsJson(true, true);
					sourceResource = addConsistsOf(sourceResource, consistsOf);
				} catch(ResourceRegistryException e) {
					logger.error("Unable to correctly serialize {}. {}", edge, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
					throw e;
				} catch(Exception e) {
					logger.error("Unable to correctly serialize {}. {}", edge, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
					throw new ResourceRegistryException(e);
				}
				
			}
			/*
			 * This comment is just to show that IsRelatedTo is not serialized by default as
			 * design choice and not because forget
			 * 
			 * else if(orientEdgeType.isSubClassOf(IsRelatedTo.NAME)){ JSONObject
			 * isRelatedTo = relationManagement.serializeAsJson(true, true); sourceResource
			 * = addIsRelatedTo(sourceResource, isRelatedTo); }
			 */
		}
		
		return sourceResource;
	}
	
	public static JSONObject addConsistsOf(JSONObject sourceResource, JSONObject consistsOf)
			throws ResourceRegistryException {
		return addRelation(sourceResource, consistsOf, AccessType.CONSISTS_OF.lowerCaseFirstCharacter());
	}
	
	public static JSONObject addIsRelatedTo(JSONObject sourceResource, JSONObject isRelatedTo)
			throws ResourceRegistryException {
		return addRelation(sourceResource, isRelatedTo, AccessType.IS_RELATED_TO.lowerCaseFirstCharacter());
	}
	
	@Override
	protected Vertex reallyCreate() throws ResourceAlreadyPresentException, ResourceRegistryException {
		
		createVertex();
		
		String property = AccessType.CONSISTS_OF.lowerCaseFirstCharacter();
		if(jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for(JsonNode consistOfJsonNode : jsonNodeArray) {
				ConsistsOfManagement com = new ConsistsOfManagement(getWorkingContext(), orientGraph);
				com.setJSON(consistOfJsonNode);
				com.setSourceEntityManagement(this);
				com.internalCreate();
				addToRelationManagement(com);
			}
		}
		
		property = AccessType.IS_RELATED_TO.lowerCaseFirstCharacter();
		if(jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for(JsonNode relationJsonNode : jsonNodeArray) {
				IsRelatedToManagement irtm = new IsRelatedToManagement(getWorkingContext(), orientGraph);
				irtm.setJSON(relationJsonNode);
				irtm.setSourceEntityManagement(this);
				irtm.internalCreate();
				addToRelationManagement(irtm);
			}
		}
		
		return element;
	}
	
	@Override
	protected Vertex reallyUpdate() throws ResourceNotFoundException, ResourceRegistryException {
		
		getElement();
		
		String property = AccessType.CONSISTS_OF.lowerCaseFirstCharacter();
		if(jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for(JsonNode relationJsonNode : jsonNodeArray) {
				ConsistsOfManagement com = new ConsistsOfManagement(getWorkingContext(), orientGraph);
				com.setJSON(relationJsonNode);
				com.internalCreateOrUdate();
				addToRelationManagement(com);
			}
		}
		
		property = AccessType.IS_RELATED_TO.lowerCaseFirstCharacter();
		if(jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for(JsonNode relationJsonNode : jsonNodeArray) {
				IsRelatedToManagement irtm = new IsRelatedToManagement(getWorkingContext(), orientGraph);
				irtm.setJSON(relationJsonNode);
				irtm.internalUpdate();
				addToRelationManagement(irtm);
			}
		}
		
		return element;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean reallyDelete() throws ResourceNotFoundException, ResourceRegistryException {
		// internalDeleteResource(orientGraph, uuid, null);
		
		getElement();
		
		Iterable<Edge> iterable = element.getEdges(Direction.OUT);
		Iterator<Edge> iterator = iterable.iterator();
		while(iterator.hasNext()) {
			
			Edge edge = iterator.next();
			OrientEdgeType orientEdgeType = ((OrientEdge) edge).getType();
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = null;
			if(orientEdgeType.isSubClassOf(IsRelatedTo.NAME)) {
				relationManagement = new IsRelatedToManagement(getWorkingContext(), orientGraph);
			} else if(orientEdgeType.isSubClassOf(ConsistsOf.NAME)) {
				relationManagement = new ConsistsOfManagement(getWorkingContext(), orientGraph);
			} else {
				logger.warn("{} is not a {} nor a {}. {}", Utility.toJsonString(edge, true), IsRelatedTo.NAME,
						ConsistsOf.NAME, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			}
			if(relationManagement != null) {
				relationManagement.setElement(edge);
				relationManagement.internalDelete();
			}
			
		}
		
		element.remove();
		
		return true;
	}
	
	public String all(boolean polymorphic, Map<String,String> constraint) throws ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.READER);
			
			return reallyGetAll(polymorphic, constraint);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
	public String reallyGetAll(boolean polymorphic, Map<String,String> constraint) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		
		String relationType = constraint.get(AccessPath.RELATION_TYPE_PATH_PART);
		constraint.remove(AccessPath.RELATION_TYPE_PATH_PART);
		String facetType = constraint.get(AccessPath.FACET_TYPE_PATH_PART);
		constraint.remove(AccessPath.FACET_TYPE_PATH_PART);
		
		// TODO check types
		
		/*
		 * SELECT FROM (TRAVERSE inE('isIdentifiedBy'), outV('EService') FROM (SELECT
		 * FROM SoftwareFacet WHERE group='VREManagement' AND name='SmartExecutor'))
		 * 
		 * WHERE @class='EService' // Only is not polymorphic
		 */
		
		boolean first = true;
		
		StringBuilder selectStringBuilder = new StringBuilder("SELECT FROM (TRAVERSE inE('");
		selectStringBuilder.append(relationType);
		selectStringBuilder.append("'), outV('");
		selectStringBuilder.append(erType);
		selectStringBuilder.append("') FROM (SELECT FROM ");
		selectStringBuilder.append(facetType);
		for(String key : constraint.keySet()) {
			if(first) {
				selectStringBuilder.append(" WHERE ");
				first = false;
			} else {
				selectStringBuilder.append(" AND ");
			}
			selectStringBuilder.append(key);
			selectStringBuilder.append("=");
			String value = constraint.get(key).trim();
			selectStringBuilder.append("'");
			selectStringBuilder.append(value);
			selectStringBuilder.append("'");
		}
		selectStringBuilder.append(" ))");
		
		if(!polymorphic) {
			selectStringBuilder.append(" WHERE @class='");
			selectStringBuilder.append(erType);
			selectStringBuilder.append("'");
		}
		
		String select = selectStringBuilder.toString();
		logger.trace(select);
		
		OSQLSynchQuery<Element> osqlSynchQuery = new OSQLSynchQuery<Element>(select);
		Iterable<Element> elements = orientGraph.command(osqlSynchQuery).execute();
		
		for(Element element : elements) {
			
			if(polymorphic) {
				OrientVertexType orientVertexType = null;
				try {
					OrientElement orientElement = ((OrientElement) element);
					if(orientElement instanceof OrientEdge) {
						continue;
					}
					orientVertexType = ((OrientVertex) orientElement).getType();
				} catch(Exception e) {
					String error = String.format("Unable to detect type of %s. %s", element.toString(),
							Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
					logger.error(error, e);
					throw new ResourceRegistryException(error);
				}
				
				if(orientVertexType.getName().compareTo(erType) != 0) {
					if(!orientVertexType.isSubClassOf(erType)) {
						continue;
					}
				}
				
			}
			
			Vertex vertex = (Vertex) element;
			
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
