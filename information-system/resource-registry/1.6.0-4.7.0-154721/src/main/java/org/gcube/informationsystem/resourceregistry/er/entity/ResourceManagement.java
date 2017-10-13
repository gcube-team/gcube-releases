/**
 * 
 */
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
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger logger = LoggerFactory
			.getLogger(ResourceManagement.class);

	public ResourceManagement() {
		super(AccessType.RESOURCE);
	}

	public ResourceManagement(OrientGraph orientGraph) {
		super(AccessType.RESOURCE, orientGraph);
	}

	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeAsJson().toString();
	}

	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		
		JSONObject sourceResource = serializeSelfOnly();

		/*
		 * Cannot get ConsistsOf edge only because is not polymorphic for a 
		 * com.tinkerpop.blueprints.Vertex
		 * vertex.getEdges(Direction.OUT, ConsistsOf.NAME);
		 * TODO Looks for a different query
		 */
		
		Iterable<Edge> edges = getElement().getEdges(Direction.OUT);
		for (Edge edge : edges) {
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = RelationManagement
					.getRelationManagement(orientGraph, edge);
			if (relationManagement instanceof ConsistsOfManagement) {
				try {
					JSONObject consistsOf = relationManagement
							.serializeAsJson();
					sourceResource = addConsistsOf(sourceResource, consistsOf);
				}catch (ResourceRegistryException e) {
					logger.error("Unable to correctly serialize {}. This is really strange and should not occur.", edge);
					throw e;
				}catch (Exception e) {
					logger.error("Unable to correctly serialize {}. This is really strange and should not occur.", edge);
					throw new ResourceRegistryException(e);
				}
				
			}
			/*
			 * This comment is just to show that IsRelatedTo is not serialized
			 * by default as design choice and not because forget
			 * 
			 * else if(orientEdgeType.isSubClassOf(IsRelatedTo.NAME)){
			 * 	 JSONObject isRelatedTo = relationManagement
						.serializeAsJson();
			 * 	 sourceResource = addIsRelatedTo(sourceResource, isRelatedTo);
			 * }
			 */
		}

		return sourceResource;
	}

	protected static JSONObject addRelation(JSONObject sourceResource,
			JSONObject relation, AccessType accessType)
			throws ResourceRegistryException {
		String arrayKey = accessType.lowerCaseFirstCharacter();
		JSONArray relationArray = null;

		try {
			if (sourceResource.has(arrayKey)) {
				relationArray = sourceResource.getJSONArray(arrayKey);
			} else {
				relationArray = new JSONArray();
			}

			relationArray.put(relation);
			sourceResource.putOpt(arrayKey, relationArray);
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		}

		return sourceResource;
	}

	public static JSONObject addConsistsOf(JSONObject sourceResource,
			JSONObject consistsOf) throws ResourceRegistryException {
		return addRelation(sourceResource, consistsOf, AccessType.CONSISTS_OF);
	}

	public static JSONObject addIsRelatedTo(JSONObject sourceResource,
			JSONObject isRelatedTo) throws ResourceRegistryException {
		return addRelation(sourceResource, isRelatedTo, AccessType.IS_RELATED_TO);
	}

	@Override
	public Vertex reallyCreate() throws ResourceAlreadyPresentException,
			ResourceRegistryException {
		
		createVertex();

		String property = AccessType.CONSISTS_OF.lowerCaseFirstCharacter();
		if (jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for (JsonNode consistOfJsonNode : jsonNodeArray) {
				ConsistsOfManagement com = new ConsistsOfManagement(orientGraph);
				com.setJSON(consistOfJsonNode);
				com.reallyCreate(element);
			}
		}

		property = AccessType.IS_RELATED_TO.lowerCaseFirstCharacter();
		if (jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for (JsonNode relationJsonNode : jsonNodeArray) {
				IsRelatedToManagement irtm = new IsRelatedToManagement(
						orientGraph);
				irtm.setJSON(relationJsonNode);
				irtm.reallyCreate(element);
			}
		}

		return element;
	}

	@Override
	public Vertex reallyUpdate() throws ResourceNotFoundException, ResourceRegistryException {

		getElement();

		String property = AccessType.CONSISTS_OF.lowerCaseFirstCharacter();
		if (jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for (JsonNode relationJsonNode : jsonNodeArray) {
				ConsistsOfManagement com = new ConsistsOfManagement(orientGraph);
				com.setJSON(relationJsonNode);
				com.reallyCreateOrUdate();
			}
		}

		property = AccessType.IS_RELATED_TO.lowerCaseFirstCharacter();
		if (jsonNode.has(property)) {
			JsonNode jsonNodeArray = jsonNode.get(property);
			for (JsonNode relationJsonNode : jsonNodeArray) {
				IsRelatedToManagement irtm = new IsRelatedToManagement(
						orientGraph);
				irtm.setJSON(relationJsonNode);
				irtm.reallyUpdate();
			}
		}

		((OrientVertex) element).save();

		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean reallyDelete() throws ResourceNotFoundException,
			ResourceRegistryException {
		// internalDeleteResource(orientGraph, uuid, null);

		getElement();

		Iterable<Edge> iterable = element.getEdges(Direction.OUT);
		Iterator<Edge> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			Edge edge = iterator.next();
			OrientEdgeType orientEdgeType = ((OrientEdge) edge).getType();
			@SuppressWarnings("rawtypes")
			RelationManagement relationManagement = null;
			if (orientEdgeType.isSubClassOf(IsRelatedTo.NAME)) {
				relationManagement = new IsRelatedToManagement(orientGraph);
			} else if (orientEdgeType.isSubClassOf(ConsistsOf.NAME)) {
				relationManagement = new ConsistsOfManagement(orientGraph);
			} else {
				logger.warn("{} is not a {} nor a {}. "
						+ "This is really strange ad should not occur. "
						+ "Please Investigate it.",
						Utility.toJsonString(edge, true), IsRelatedTo.NAME,
						ConsistsOf.NAME);
			}
			if (relationManagement != null) {
				relationManagement.setElement(edge);
				relationManagement.reallyDelete();
			}

		}

		element.remove();

		return true;
	}
	
	
	public String all(boolean polymorphic, Map<String, String> constraint) throws ResourceRegistryException {
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			return reallyGetAll(polymorphic, constraint);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	public String reallyGetAll(boolean polymorphic, Map<String, String> constraint) throws ResourceRegistryException{
		JSONArray jsonArray = new JSONArray();
		
		String relationType = constraint.get(AccessPath.RELATION_TYPE_PATH_PART);
		constraint.remove(AccessPath.RELATION_TYPE_PATH_PART);
		String facetType = constraint.get(AccessPath.FACET_TYPE_PATH_PART);
		constraint.remove(AccessPath.FACET_TYPE_PATH_PART);
		
		/* 
		 * SELECT FROM (TRAVERSE inE('isIdentifiedBy'), outV('EService') 
		 * FROM (SELECT FROM SoftwareFacet WHERE group='VREManagement' AND name='SmartExecutor')) 
		 * 
		 * WHERE @class='EService'  // Only is not polymorphic
		 */
		
		boolean first = true;
		
		StringBuilder selectStringBuilder = new StringBuilder("SELECT FROM (TRAVERSE inE('");
		selectStringBuilder.append(relationType);
		selectStringBuilder.append("'), outV('");
		selectStringBuilder.append(erType);
		selectStringBuilder.append("') FROM (SELECT FROM ");
		selectStringBuilder.append(facetType);
		for(String key : constraint.keySet()){
			if(first){
				selectStringBuilder.append(" WHERE ");
				first = false;
			}else{
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

		if(!polymorphic){
			selectStringBuilder.append(" WHERE @class='");
			selectStringBuilder.append(erType);
			selectStringBuilder.append("'");
		}

		String select = selectStringBuilder.toString();
		logger.trace(select);

		
		OSQLSynchQuery<Element> osqlSynchQuery = new OSQLSynchQuery<Element>(
				select);
		Iterable<Element> elements = orientGraph.command(osqlSynchQuery)
				.execute();
		
		for(Element element : elements){
			
			if(polymorphic){
				OrientVertexType orientVertexType = null;
				try {
					OrientElement orientElement = ((OrientElement) element);
					if(orientElement instanceof OrientEdge){
						continue;
					}
					orientVertexType = ((OrientVertex) orientElement).getType();
				}catch (Exception e) {
					String error = String.format("Unable to detect type of %s. This is really strage please contact the administrator.", element.toString()); 
					logger.error(error, e);
					throw new ResourceRegistryException(error);
				}

				if(orientVertexType.getName().compareTo(erType)!=0){
					if(!orientVertexType.isSubClassOf(erType) ) {
						continue;
					}
				}
				
				
			}
			
			Vertex vertex = (Vertex) element;
			
			@SuppressWarnings("rawtypes")
			EntityManagement entityManagement = EntityManagement.getEntityManagement(orientGraph, vertex);
			try {
				JSONObject jsonObject =  entityManagement.serializeAsJson();
				jsonArray.put(jsonObject);
			}catch (ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. It will be excluded from results. This is really strange and should not occur.", vertex.toString());
			}
		}
		
		
		return jsonArray.toString();
	}


}
