/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class RelationManagement<R extends Relation> extends
		ERManagement<R, Edge> {

	private static Logger logger = LoggerFactory
			.getLogger(RelationManagement.class);

	protected final Class<? extends Entity> targetEntityClass;

	protected RelationManagement(Class<R> relationClass) {
		super(relationClass);

		this.ignoreKeys.add(Relation.HEADER_PROPERTY);
		this.ignoreKeys.add(Relation.TARGET_PROPERTY);
		this.ignoreKeys.add(Relation.SOURCE_PROPERTY);
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_IN.toLowerCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_OUT.toLowerCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_IN.toUpperCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_OUT.toUpperCase());

		if (ConsistsOf.class.isAssignableFrom(relationClass)) {
			this.baseType = ConsistsOf.NAME;
			this.targetEntityClass = Facet.class;
		} else if (IsRelatedTo.class.isAssignableFrom(relationClass)) {
			this.baseType = IsRelatedTo.NAME;
			this.targetEntityClass = Resource.class;
		} else {
			this.baseType = Relation.NAME;
			this.targetEntityClass = Resource.class;
		}
	}

	protected RelationManagement(Class<R> relationClass, OrientGraph orientGraph) {
		this(relationClass);
		this.orientGraph = orientGraph;
	}

	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeAsJson().toString();
	}

	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		JSONObject relation = serializeSelfOnly();

		Vertex target = element.getVertex(Direction.IN);
		EntityManagement entityManagement = EntityManagement
				.getEntityManagement(orientGraph, target);

		try {
			relation.put(Relation.TARGET_PROPERTY,
					entityManagement.serializeAsJson());
		} catch (JSONException e) {
			throw new ResourceRegistryException(e);
		}

		return relation;
	}

	protected Map<String, JSONObject> fullSerialize(
			Map<String, JSONObject> visitedSourceResources)
			throws ResourceRegistryException {
		Vertex source = element.getVertex(Direction.OUT);

		String id = source.getId().toString();

		JSONObject sourceResource = visitedSourceResources.get(id);
		if (sourceResource == null) {
			ResourceManagement resourceManagement = (ResourceManagement) EntityManagement
					.getEntityManagement(orientGraph, source);
			if (this instanceof IsRelatedToManagement) {
				sourceResource = resourceManagement.serializeAsJson();
			} else if (this instanceof ConsistsOfManagement) {
				sourceResource = resourceManagement.serializeSelfOnly();
			} else {
				String error = String.format("{%s is not a %s nor a %s. "
						+ "This is really strange and should not occur. "
						+ "Please Investigate it.", this,
						IsRelatedToManagement.class.getSimpleName(),
						ConsistsOfManagement.class.getSimpleName());
				throw new ResourceRegistryException(error);
			}
		}

		if (this instanceof IsRelatedToManagement) {
			sourceResource = ResourceManagement.addIsRelatedTo(sourceResource,
					serializeAsJson());
		} else if (this instanceof ConsistsOfManagement) {
			sourceResource = ResourceManagement.addConsistsOf(sourceResource,
					serializeAsJson());
		} else {
			String error = String.format("{%s is not a %s nor a %s. "
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.", this,
					IsRelatedToManagement.class.getSimpleName(),
					ConsistsOfManagement.class.getSimpleName());
			throw new ResourceRegistryException(error);
		}

		visitedSourceResources.put(id, sourceResource);

		return visitedSourceResources;
	}

	public Edge reallyCreate(UUID sourceUUID, UUID targetUUID)
			throws ResourceRegistryException {
		ResourceManagement srmSource = new ResourceManagement(orientGraph);
		srmSource.setUUID(sourceUUID);
		Vertex source = srmSource.getElement();

		EntityManagement entityManagement = getEntityManagement();
		entityManagement.setUUID(targetUUID);
		Vertex target = (Vertex) entityManagement.getElement();

		return reallyCreate(source, target);

	}

	protected Edge reallyCreate(Vertex source, Vertex target)
			throws ResourceRegistryException {

		EntityManagement sourceEntityManagement = EntityManagement
				.getEntityManagement(orientGraph, source);
		EntityManagement targetEntityManagement = EntityManagement
				.getEntityManagement(orientGraph, target);
		if (!(sourceEntityManagement instanceof ResourceManagement)) {
			String error = String.format(
					"Any type of %s can have only a %s as %s. "
							+ "Cannot instatiate %s beetween %s -> %s ",
					Relation.NAME, Resource.NAME, Relation.SOURCE_PROPERTY,
					erType, sourceEntityManagement.serialize(),
					targetEntityManagement.serialize());
			throw new ResourceRegistryException(error);
		}

		if (this instanceof IsRelatedToManagement) {
			if (!(targetEntityManagement instanceof ResourceManagement)) {
				String error = String.format("A %s can have only a %s as %s. "
						+ "Cannot instatiate %s beetween %s -> %s ", baseType,
						Resource.NAME, Relation.TARGET_PROPERTY, erType,
						sourceEntityManagement.serialize(),
						targetEntityManagement.serialize());
				throw new ResourceRegistryException(error);
			}
		} else if (this instanceof ConsistsOfManagement) {
			if (!(targetEntityManagement instanceof FacetManagement)) {
				String error = String.format("A %s can have only a %s as %s. "
						+ "Cannot instatiate %s beetween %s -> %s ", baseType,
						Facet.NAME, Relation.TARGET_PROPERTY, erType,
						sourceEntityManagement.serialize(),
						targetEntityManagement.serialize());
				throw new ResourceRegistryException(error);
			}
		} else {
			String error = String.format("{%s is not a %s nor a %s. "
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.", this,
					IsRelatedToManagement.class.getSimpleName(),
					ConsistsOfManagement.class.getSimpleName());
			throw new ResourceRegistryException(error);
		}

		logger.trace("Creating {} beetween {} -> {}", erType,
				sourceEntityManagement.serialize(),
				targetEntityManagement.serialize());

		element = orientGraph.addEdge(null, source, target, erType);

		ERManagement.updateProperties(element, jsonNode, ignoreKeys,
				ignoreStartWithKeys);

		HeaderUtility.addHeader(element, null);
		ContextUtility.addToActualContext(orientGraph, element);

		((OrientEdge) element).save();

		logger.info("{} successfully created", erType);

		return element;
	}

	public Edge reallyCreate(Vertex source) throws ResourceRegistryException {
		Vertex target = null;
		EntityManagement entityManagement = getEntityManagement();

		if (!jsonNode.has(Relation.TARGET_PROPERTY)) {
			throw new ResourceRegistryException(
					"Error while creating relation. No target definition found");
		}
		entityManagement.setJSON(jsonNode.get(Relation.TARGET_PROPERTY));
		try {
			target = (Vertex) entityManagement.getElement();
		} catch (Exception e) {
			target = entityManagement.reallyCreate();
		}
		return reallyCreate(source, target);
	}

	public Edge reallyCreate(UUID sourceUUID) throws ResourceRegistryException {
		ResourceManagement srmSource = new ResourceManagement(orientGraph);
		srmSource.setUUID(sourceUUID);
		Vertex source = srmSource.getElement();
		return reallyCreate(source);
	}

	@Override
	public Edge reallyUpdate() throws ResourceRegistryException {

		logger.debug("Trying to update {} : {}", erType, jsonNode);

		Edge edge = getElement();
		ERManagement.updateProperties(edge, jsonNode, ignoreKeys,
				ignoreStartWithKeys);

		if (ConsistsOf.class.isAssignableFrom(erTypeClass)) {
			JsonNode target = jsonNode.get(Relation.TARGET_PROPERTY);
			if (target != null) {
				FacetManagement fm = new FacetManagement(orientGraph);
				fm.setJSON(target);
				fm.reallyUpdate();
			}
		}

		logger.info("{} {} successfully updated", erType, jsonNode);

		return edge;

	}

	@Override
	public boolean reallyAddToContext() throws ContextException,
			ResourceRegistryException {
		getElement();

		AddConstraint addConstraint = AddConstraint.unpropagate;

		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(
					PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if (propagationConstraint.getAddConstraint() != null) {
				addConstraint = propagationConstraint.getAddConstraint();
			}
		} catch (Exception e) {
			logger.warn("Error while getting {} from {}. Assuming {}. "
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.",
					Relation.PROPAGATION_CONSTRAINT,
					Utility.toJsonString(element, true), addConstraint);
		}

		Vertex target = element.getVertex(Direction.IN);

		switch (addConstraint) {
		case propagate:
			/*
			 * The relation must be added only in the case the target vertex
			 * must be added. Otherwise we have a relation which point to an
			 * entity outside of the context.
			 */
			ContextUtility.addToActualContext(orientGraph, getElement());
			EntityManagement entityManagement = EntityManagement
					.getEntityManagement(orientGraph, target);
			entityManagement.reallyAddToContext();
			break;

		case unpropagate:
			break;

		default:
			break;
		}

		return true;
	}

	protected boolean removeFromContextTargetVertex(Vertex target)
			throws ResourceRegistryException {
		EntityManagement entityManagement = EntityManagement
				.getEntityManagement(orientGraph, target);
		if (entityManagement != null) {
			entityManagement.reallyRemoveFromContext();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean reallyRemoveFromContext() throws ContextException,
			ResourceRegistryException {
		getElement();

		RemoveConstraint removeConstraint = RemoveConstraint.keep;

		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(
					PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if (propagationConstraint.getRemoveConstraint() != null) {
				removeConstraint = propagationConstraint.getRemoveConstraint();
			}
		} catch (Exception e) {
			logger.warn("Error while getting {} from {}. Assuming {}. "
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.",
					Relation.PROPAGATION_CONSTRAINT,
					Utility.toJsonString(element, true), removeConstraint);
		}

		Vertex target = element.getVertex(Direction.IN);

		/*
		 * In any removeConstraint value the relation MUSt be removed from
		 * context to avoid to have edge having a source outside of the context.
		 */
		ContextUtility.removeFromActualContext(orientGraph, element);

		switch (removeConstraint) {
		case cascade:
			removeFromContextTargetVertex(target);
			break;

		case cascadeWhenOrphan:
			Iterable<Edge> iterable = target.getEdges(Direction.IN);
			Iterator<Edge> iterator = iterable.iterator();
			if (iterator.hasNext()) {
				logger.trace(
						"{} point to {} which is not orphan. Giving {} directive, it will be not remove from current context.",
						element, target, removeConstraint);
			} else {
				removeFromContextTargetVertex(target);
			}
			break;

		case keep:
			break;

		default:
			break;
		}

		return true;
	}

	protected EntityManagement getEntityManagement()
			throws ResourceRegistryException {
		EntityManagement entityManagement;
		if (ConsistsOf.class.isAssignableFrom(erTypeClass)) {
			entityManagement = new FacetManagement(orientGraph);
		} else if (IsRelatedTo.class.isAssignableFrom(erTypeClass)) {
			entityManagement = new ResourceManagement(orientGraph);
		} else {
			String error = String.format("{%s is not a %s nor a %s. "
					+ "This is really strange ad should not occur. "
					+ "Please Investigate it.", erTypeClass, ConsistsOf.NAME,
					IsRelatedTo.NAME);
			throw new ResourceRegistryException(error);
		}
		return entityManagement;
	}

	@SuppressWarnings("unchecked")
	public static RelationManagement getRelationManagement(
			OrientGraph orientGraph, Edge edge)
			throws ResourceRegistryException {
		OrientEdgeType orientEdgeType = ((OrientEdge) edge).getType();
		RelationManagement relationManagement = null;
		if (orientEdgeType.isSubClassOf(ConsistsOf.NAME)) {
			relationManagement = new ConsistsOfManagement(orientGraph);
		} else if (orientEdgeType.isSubClassOf(IsRelatedTo.NAME)) {
			relationManagement = new IsRelatedToManagement(orientGraph);
		} else {
			String error = String.format("{%s is not a %s nor a %s. "
					+ "This is really strange ad should not occur. "
					+ "Please Investigate it.", edge, ConsistsOf.NAME,
					IsRelatedTo.NAME);
			throw new ResourceRegistryException(error);
		}
		relationManagement.setElement(edge);
		return relationManagement;
	}

	protected boolean deleteTargetVertex(Vertex target)
			throws ResourceRegistryException {
		EntityManagement entityManagement = EntityManagement
				.getEntityManagement(orientGraph, target);
		if (entityManagement != null) {
			entityManagement.reallyDelete();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean reallyDelete() throws RelationNotFoundException,
			ResourceRegistryException {
		logger.debug(
				"Going to remove {} with UUID {}. Related {}s will be detached.",
				baseType, uuid, targetEntityClass.getSimpleName());

		getElement();

		RemoveConstraint removeConstraint = RemoveConstraint.keep;

		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(
					PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if (propagationConstraint.getRemoveConstraint() != null) {
				removeConstraint = propagationConstraint.getRemoveConstraint();
			}
		} catch (Exception e) {
			logger.warn("Error while getting {} from {}. Assuming {}. "
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.",
					Relation.PROPAGATION_CONSTRAINT,
					Utility.toJsonString(element, true), removeConstraint);
		}

		Vertex target = element.getVertex(Direction.IN);
		element.remove();

		switch (removeConstraint) {
			case cascade:
				deleteTargetVertex(target);
				break;
	
			case cascadeWhenOrphan:
				Iterable<Edge> iterable = target.getEdges(Direction.IN);
				Iterator<Edge> iterator = iterable.iterator();
				if (iterator.hasNext()) {
					logger.trace(
							"{} point to {} which is not orphan. Giving {} directive, it will be keep.",
							element, target, removeConstraint);
				} else {
					deleteTargetVertex(target);
				}
				break;
	
			case keep:
				break;
	
			default:
				break;
		}

		return true;
	}

	public String create(UUID sourceUUID, UUID targetUUID)
			throws ResourceRegistryException {
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.WRITER);

			element = reallyCreate(sourceUUID, targetUUID);

			orientGraph.commit();

			return serialize();

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

	@SuppressWarnings("unchecked")
	protected Collection<JSONObject> serializeEdges(Iterable<Edge> edges,
			boolean postFilterPolymorphic) throws ResourceRegistryException {
		Map<String, JSONObject> visitedSourceResources = new HashMap<>();
		for (Edge edge : edges) {
			if (postFilterPolymorphic && edge.getLabel().compareTo(erType) != 0) {
				continue;
			}
			RelationManagement relationManagement = getRelationManagement(
					orientGraph, edge);
			visitedSourceResources = relationManagement
					.fullSerialize(visitedSourceResources);
		}
		return visitedSourceResources.values();
	}

	protected String serializeJSONObjectList(Collection<JSONObject> list) {
		JSONArray jsonArray = new JSONArray(list);
		return jsonArray.toString();
	}

	@Override
	public String reallyGetAll(boolean polymorphic)
			throws ResourceRegistryException {
		Iterable<Edge> edges = orientGraph.getEdgesOfClass(erType, polymorphic);
		Collection<JSONObject> collection = serializeEdges(edges, false);
		return serializeJSONObjectList(collection);
	}

	public String reallyGetAllFrom(UUID uuid, Direction direction,
			boolean polymorphic) throws ResourceRegistryException {
		EntityManagement entityManagement = null;
		try {
			entityManagement = (EntityManagement) ERManagement
					.getERManagementFromUUID(orientGraph, uuid);
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceRegistryException(String.format(
					"Provided UUID %s does not belogn to any %s",
					uuid.toString(), Entity.NAME));
		}

		Vertex vertex = (Vertex) entityManagement.getElement();

		List<JSONObject> list = new ArrayList<>();
		Iterable<Edge> edges = vertex.getEdges(direction, erType);
		list.addAll(serializeEdges(edges, !polymorphic));

		return serializeJSONObjectList(list);

	}

	public String allFrom(UUID uuid, Direction direction, boolean polymorphic)
			throws ResourceRegistryException {
		try {
			orientGraph = ContextUtility
					.getActualSecurityContextGraph(PermissionMode.READER);

			return reallyGetAllFrom(uuid, direction, polymorphic);
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

}
