/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.entity;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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

}
