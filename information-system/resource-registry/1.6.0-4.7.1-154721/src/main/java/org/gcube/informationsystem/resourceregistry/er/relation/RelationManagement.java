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
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
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
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
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

	protected RelationManagement(AccessType accessType) {
		super(accessType);

		this.ignoreKeys.add(Relation.HEADER_PROPERTY);
		this.ignoreKeys.add(Relation.TARGET_PROPERTY);
		this.ignoreKeys.add(Relation.SOURCE_PROPERTY);
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_IN.toLowerCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_OUT.toLowerCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_IN.toUpperCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_OUT.toUpperCase());

		switch (accessType) {
			case CONSISTS_OF:
				this.targetEntityClass = Facet.class;
				break;
			
			case IS_RELATED_TO:
				this.targetEntityClass = Resource.class;
				break;
				
			default:
				this.targetEntityClass = Resource.class;
				break;
		}

	}

	protected RelationManagement(AccessType accessType, OrientGraph orientGraph) {
		this(accessType);
		this.orientGraph = orientGraph;
	}

	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeAsJson().toString();
	}

	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		JSONObject relation = serializeSelfOnly();

		try {
			Vertex source = element.getVertex(Direction.OUT);
			EntityManagement sourceEntityManagement = EntityManagement
					.getEntityManagement(orientGraph, source);
			relation.put(Relation.SOURCE_PROPERTY, sourceEntityManagement.serializeSelfOnly());
			
			Vertex target = element.getVertex(Direction.IN);
			EntityManagement targetEntityManagement = EntityManagement
					.getEntityManagement(orientGraph, target);
			relation.put(Relation.TARGET_PROPERTY,
					targetEntityManagement.serializeAsJson());
			
		} catch (ResourceRegistryException e) {
			logger.error("Unable to correctly serialize {}. This is really strange and should not occur.", element, e);
			throw e;
		} catch (Exception e) {
			logger.error("Unable to correctly serialize {}. This is really strange and should not occur.", element, e);
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

	private Edge reallyCreate(UUID sourceUUID, UUID targetUUID)
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
						+ "Cannot instatiate %s beetween %s -> %s ", accessType.getName(),
						Resource.NAME, Relation.TARGET_PROPERTY, erType,
						sourceEntityManagement.serialize(),
						targetEntityManagement.serialize());
				throw new ResourceRegistryException(error);
			}
		} else if (this instanceof ConsistsOfManagement) {
			if (!(targetEntityManagement instanceof FacetManagement)) {
				String error = String.format("A %s can have only a %s as %s. "
						+ "Cannot instatiate %s beetween %s -> %s ", accessType.getName(),
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

		ERManagement.updateProperties(oClass, element, jsonNode, ignoreKeys,
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
	public Edge reallyCreate() throws ResourceRegistryException {
		if(!jsonNode.has(Relation.SOURCE_PROPERTY)){
			throw new ResourceRegistryException(
					"Error while creating relation. No source definition found");
		}
		
		UUID sourceUUID = org.gcube.informationsystem.impl.utils.Utility
				.getUUIDFromJsonNode(jsonNode.get(Relation.SOURCE_PROPERTY));
		
		return reallyCreate(sourceUUID);
	}
	
	
	@Override
	public Edge reallyUpdate() throws ResourceRegistryException {

		logger.debug("Trying to update {} : {}", erType, jsonNode);

		Edge edge = getElement();
		ERManagement.updateProperties(oClass, edge, jsonNode, ignoreKeys,
				ignoreStartWithKeys);

		if (accessType.compareTo(AccessType.CONSISTS_OF)==0) {
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
			}else {
				String error = String.format("%s.%s in %s is null"
						+ "This is really strange and should not occur. "
						+ "Please Investigate it.",
						Relation.PROPAGATION_CONSTRAINT,
						PropagationConstraint.ADD_PROPERTY,
						Utility.toJsonString(element, true));
				logger.error(error);
				throw new ResourceRegistryException(error);
			}
		} catch (Exception e) {
			String error = String.format("Error while getting %s from %s while performing AddToContext."
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.",
					Relation.PROPAGATION_CONSTRAINT,
					Utility.toJsonString(element, true));
			logger.warn(error);
			throw new ResourceRegistryException(error, e);
		}

		Vertex target = element.getVertex(Direction.IN);

		switch (addConstraint) {
			case propagate:
				/*
				 * The relation must be added only in the case the target vertex
				 * must be added. Otherwise we have a relation which point to an
				 * entity outside of the context.
				 */
				EntityManagement entityManagement = EntityManagement
						.getEntityManagement(orientGraph, target);
				entityManagement.reallyAddToContext();
				ContextUtility.addToActualContext(orientGraph, getElement());
				
				break;
	
			case unpropagate:
				break;
	
			default:
				break;
		}

		return true;
	}

	public boolean forcedAddToContext() throws ContextException,
		ResourceRegistryException {
		
		getElement();
		
		/* Adding source to Context */
		Vertex source = element.getVertex(Direction.OUT);
		EntityManagement entityManagement = EntityManagement
				.getEntityManagement(orientGraph, source);
		entityManagement.reallyAddToContext();
		
		/* Adding target to Context */
		Vertex target = element.getVertex(Direction.IN);
		entityManagement = EntityManagement
				.getEntityManagement(orientGraph, target);
		entityManagement.reallyAddToContext();
		
		ContextUtility.addToActualContext(orientGraph, getElement());
		
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
			}else{
				String error = String.format("%s.%s in %s is null"
						+ "This is really strange and should not occur. "
						+ "Please Investigate it.",
						Relation.PROPAGATION_CONSTRAINT,
						PropagationConstraint.REMOVE_PROPERTY,
						Utility.toJsonString(element, true));
				logger.error(error);
				throw new ResourceRegistryException(error);
			}
		} catch (Exception e) {
			String error = String.format("Error while getting %s from %s while performing RemoveFromContext."
					+ "This is really strange and should not occur. "
					+ "Please Investigate it.",
					Relation.PROPAGATION_CONSTRAINT,
					Utility.toJsonString(element, true));
			logger.error(error);
			throw new ResourceRegistryException(error, e);
			
		}

		Vertex target = element.getVertex(Direction.IN);

		/*
		 * In any removeConstraint value the relation MUST be removed from
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
				int count = 0;
				OrientEdge edge = null;
				while (iterator.hasNext()) {
					edge = (OrientEdge) iterator.next();
					OrientEdge thisOrientEdge = (OrientEdge) element;
					if(edge.compareTo(thisOrientEdge)!=0){
						if(thisOrientEdge.getOutVertex().compareTo(edge.getOutVertex())!=0){
							count++;
							break;
						}
						/*
						else{
							ContextUtility.removeFromActualContext(orientGraph, edge);
						}
						*/
					}
				}
				
				if (count>0) {
					logger.trace(
							"{} point to {} which is not orphan ({} exists). Giving {} directive, it will be not remove from current context.",
							element, target, edge, removeConstraint);
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
		
		switch (accessType) {
			case CONSISTS_OF:
				entityManagement = new FacetManagement(orientGraph);
				break;
			case IS_RELATED_TO:
				entityManagement = new ResourceManagement(orientGraph);
				break;
			default:
				String error = String.format("{%s is not a %s nor a %s. "
						+ "This is really strange ad should not occur. "
						+ "Please Investigate it.", accessType.getName(), ConsistsOf.NAME,
						IsRelatedTo.NAME);
				throw new ResourceRegistryException(error);
		}
		
		return entityManagement;
	}

	@SuppressWarnings("unchecked")
	public static RelationManagement getRelationManagement(
			OrientGraph orientGraph, Edge edge)
			throws ResourceRegistryException {
		
		if(orientGraph==null){
			throw new ResourceRegistryException(OrientGraph.class.getSimpleName() + "instance is null. This is really strage please contact the administrator.");
		}
		
		if(edge==null){
			throw new ResourceRegistryException(Edge.class.getSimpleName() + "instance is null. This is really strage please contact the administrator.");
		}
		
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
				accessType.getName(), uuid, targetEntityClass.getSimpleName());

		getElement();

		RemoveConstraint removeConstraint = RemoveConstraint.keep;

		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(
					PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if (propagationConstraint.getRemoveConstraint() != null) {
				removeConstraint = propagationConstraint.getRemoveConstraint();
			}else{
				String error = String.format("%s.%s in %s is null"
						+ "This is really strange and should not occur. "
						+ "Please Investigate it.",
						Relation.PROPAGATION_CONSTRAINT,
						PropagationConstraint.REMOVE_PROPERTY,
						Utility.toJsonString(element, true));
				logger.error(error);
				throw new ResourceRegistryException(error);
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

	@SuppressWarnings("unused")
	private String create(UUID sourceUUID, UUID targetUUID)
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

	@Override
	public boolean addToContext() throws ContextException {
		logger.debug("Going to add {} with UUID {} to actual Context",
				accessType.getName(), uuid);

		try {
			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			boolean added = forcedAddToContext();

			orientGraph.commit();
			logger.info("{} with UUID {} successfully added to actual Context",
					accessType.getName(), uuid);

			return added;
		} catch (Exception e) {
			logger.error("Unable to add {} with UUID {} to actual Context",
					accessType.getName(), uuid, e);
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e);
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
}
