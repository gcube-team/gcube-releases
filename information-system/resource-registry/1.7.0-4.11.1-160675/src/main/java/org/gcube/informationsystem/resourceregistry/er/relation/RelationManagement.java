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
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("rawtypes")
public abstract class RelationManagement<R extends Relation, S extends EntityManagement, T extends EntityManagement>
		extends ERManagement<R,Edge> {
	
	protected final Class<? extends Entity> targetEntityClass;
	
	protected S sourceEntityManagement;
	protected T targetEntityManagement;
	
	protected RelationManagement(AccessType accessType) {
		super(accessType);
		
		this.ignoreKeys.add(Relation.HEADER_PROPERTY);
		this.ignoreKeys.add(Relation.TARGET_PROPERTY);
		this.ignoreKeys.add(Relation.SOURCE_PROPERTY);
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_IN.toLowerCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_OUT.toLowerCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_IN.toUpperCase());
		this.ignoreKeys.add(OrientBaseGraph.CONNECTION_OUT.toUpperCase());
		
		switch(accessType) {
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
		
		sourceEntityManagement = null;
		targetEntityManagement = null;
		
	}
	
	protected RelationManagement(AccessType accessType, SecurityContext workingContext, OrientGraph orientGraph) {
		this(accessType);
		this.orientGraph = orientGraph;
		setWorkingContext(workingContext);
	}
	
	/*
	 * Needed for ResourceManagement.serializeAsJson() function to check that
	 * sourceEntityManagement is the same of the instance is creating this
	 * RelationManagement. TODO Look for a workaround
	 */
	public S giveMeSourceEntityManagementAsIs() throws ResourceRegistryException {
		return sourceEntityManagement;
	}
	
	@SuppressWarnings("unchecked")
	public S getSourceEntityManagement() throws ResourceRegistryException {
		if(sourceEntityManagement == null) {
			Vertex source = getElement().getVertex(Direction.OUT);
			sourceEntityManagement = newSourceEntityManagement();
			sourceEntityManagement.setElement(source);
		}
		sourceEntityManagement.setReload(reload);
		return sourceEntityManagement;
	}
	
	@SuppressWarnings("unchecked")
	public T getTargetEntityManagement() throws ResourceRegistryException {
		if(targetEntityManagement == null) {
			Vertex target = getElement().getVertex(Direction.IN);
			targetEntityManagement = newTargetEntityManagement();
			targetEntityManagement.setElement(target);
		}
		targetEntityManagement.setReload(reload);
		return targetEntityManagement;
	}
	
	public void setSourceEntityManagement(S sourceEntityManagement) {
		this.sourceEntityManagement = sourceEntityManagement;
	}
	
	public void setTargetEntityManagement(T targetEntityManagement) {
		this.targetEntityManagement = targetEntityManagement;
	}
	
	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeAsJson().toString();
	}
	
	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		return serializeAsJson(true, true);
	}
	
	public JSONObject serializeAsJson(boolean includeSource, boolean includeTarget) throws ResourceRegistryException {
		JSONObject relation = serializeSelfOnly();
		
		try {
			if(includeSource) {
				EntityManagement sourceEntityManagement = getSourceEntityManagement();
				relation.put(Relation.SOURCE_PROPERTY, sourceEntityManagement.serializeSelfOnly());
			}
			
			if(includeTarget) {
				EntityManagement targetEntityManagement = getTargetEntityManagement();
				relation.put(Relation.TARGET_PROPERTY, targetEntityManagement.serializeAsJson());
			}
			
		} catch(ResourceRegistryException e) {
			logger.error("Unable to correctly serialize {}. {}", element, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE, e);
			throw e;
		} catch(Exception e) {
			logger.error("Unable to correctly serialize {}. {}", element, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE, e);
			throw new ResourceRegistryException(e);
		}
		
		return relation;
	}
	
	protected Map<String,JSONObject> fullSerialize(Map<String,JSONObject> visitedSourceResources)
			throws ResourceRegistryException {
		
		Vertex source = getElement().getVertex(Direction.OUT);
		
		String id = source.getId().toString();
		
		JSONObject sourceResource = visitedSourceResources.get(id);
		ResourceManagement resourceManagement = null;
		
		if(sourceResource == null) {
			resourceManagement = (ResourceManagement) ERManagementUtility.getEntityManagement(getWorkingContext(),
					orientGraph, source);
			if(this instanceof IsRelatedToManagement) {
				sourceResource = resourceManagement.serializeAsJson();
			} else if(this instanceof ConsistsOfManagement) {
				sourceResource = resourceManagement.serializeSelfOnly();
			} else {
				String error = String.format("{%s is not a %s nor a %s. %s", this,
						IsRelatedToManagement.class.getSimpleName(), ConsistsOfManagement.class.getSimpleName(),
						Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				throw new ResourceRegistryException(error);
			}
		}
		
		if(this instanceof IsRelatedToManagement) {
			sourceResource = ResourceManagement.addIsRelatedTo(sourceResource, serializeAsJson());
		} else if(this instanceof ConsistsOfManagement) {
			sourceResource = ResourceManagement.addConsistsOf(sourceResource, serializeAsJson());
		} else {
			String error = String.format("{%s is not a %s nor a %s. %s", this,
					IsRelatedToManagement.class.getSimpleName(), ConsistsOfManagement.class.getSimpleName(),
					Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			throw new ResourceRegistryException(error);
		}
		
		visitedSourceResources.put(id, sourceResource);
		
		return visitedSourceResources;
	}
	
	@Override
	protected Edge reallyCreate() throws ResourceRegistryException {
		
		if(sourceEntityManagement == null) {
			
			if(!jsonNode.has(Relation.SOURCE_PROPERTY)) {
				throw new ResourceRegistryException("Error while creating relation. No source definition found");
			}
			
			UUID sourceUUID = org.gcube.informationsystem.impl.utils.Utility
					.getUUIDFromJsonNode(jsonNode.get(Relation.SOURCE_PROPERTY));
			
			sourceEntityManagement = newSourceEntityManagement();
			sourceEntityManagement.setUUID(sourceUUID);
		}
		
		if(targetEntityManagement == null) {
			targetEntityManagement = newTargetEntityManagement();
			
			if(!jsonNode.has(Relation.TARGET_PROPERTY)) {
				throw new ResourceRegistryException("Error while creating " + erType + ". No target definition found");
			}
			
			try {
				targetEntityManagement.setJSON(jsonNode.get(Relation.TARGET_PROPERTY));
			} catch(SchemaException e) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append("A ");
				errorMessage.append(erType);
				errorMessage.append(" can be only created beetween ");
				errorMessage.append(sourceEntityManagement.getAccessType().getName());
				errorMessage.append(" and ");
				errorMessage.append(targetEntityManagement.getAccessType().getName());
				throw new ResourceRegistryException(errorMessage.toString(), e);
			}
			
			try {
				targetEntityManagement.getElement();
			} catch(Exception e) {
				targetEntityManagement.internalCreate();
			}
		}
		
		logger.trace("Creating {} beetween {} -> {}", erType, getSourceEntityManagement().serialize(),
				getTargetEntityManagement().serialize());
		
		Vertex source = (Vertex) getSourceEntityManagement().getElement();
		Vertex target = (Vertex) getTargetEntityManagement().getElement();
		
		element = orientGraph.addEdge(null, source, target, erType);
		
		ERManagement.updateProperties(oClass, element, jsonNode, ignoreKeys, ignoreStartWithKeys);
		
		logger.info("{} successfully created", erType);
		
		return element;
	}
	
	protected abstract S newSourceEntityManagement() throws ResourceRegistryException;
	
	protected abstract T newTargetEntityManagement() throws ResourceRegistryException;
	
	@Override
	protected Edge reallyUpdate() throws ResourceRegistryException {
		
		logger.debug("Trying to update {} : {}", erType, jsonNode);
		
		Edge edge = getElement();
		ERManagement.updateProperties(oClass, edge, jsonNode, ignoreKeys, ignoreStartWithKeys);
		
		if(accessType.compareTo(AccessType.CONSISTS_OF) == 0) {
			JsonNode target = jsonNode.get(Relation.TARGET_PROPERTY);
			if(target != null) {
				FacetManagement fm = new FacetManagement(getWorkingContext(), orientGraph);
				fm.setJSON(target);
				fm.internalUpdate();
			}
		}
		
		logger.info("{} {} successfully updated", erType, jsonNode);
		
		return edge;
		
	}
	
	@Override
	protected boolean reallyAddToContext() throws ContextException, ResourceRegistryException {
		getElement();
		
		AddConstraint addConstraint = AddConstraint.unpropagate;
		
		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if(propagationConstraint.getAddConstraint() != null) {
				addConstraint = propagationConstraint.getAddConstraint();
			} else {
				String error = String.format("%s.%s in %s is null. %s", Relation.PROPAGATION_CONSTRAINT,
						PropagationConstraint.ADD_PROPERTY, Utility.toJsonString(element, true),
						Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				logger.error(error);
				throw new ResourceRegistryException(error);
			}
		} catch(Exception e) {
			String error = String.format("Error while getting %s from %s while performing AddToContext. %s",
					Relation.PROPAGATION_CONSTRAINT, Utility.toJsonString(element, true),
					Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			logger.warn(error);
			throw new ResourceRegistryException(error, e);
		}
		
		switch(addConstraint) {
			case propagate:
				/*
				 * The relation must be added only in the case the target vertex must be added.
				 * Otherwise we have a relation which point to an entity outside of the context.
				 */
				getTargetEntityManagement().internalAddToContext();
				
				getWorkingContext().addElement(getElement(), orientGraph);
				
				break;
			
			case unpropagate:
				break;
			
			default:
				break;
		}
		
		return true;
	}
	
	public boolean forcedAddToContext() throws ContextException, ResourceRegistryException {
		
		getElement();
		
		/* Adding source to Context */
		getSourceEntityManagement().internalAddToContext();
		
		/* Adding target to Context */
		getTargetEntityManagement().internalAddToContext();
		
		getWorkingContext().addElement(getElement(), orientGraph);
		
		return true;
	}
	
	@Override
	protected boolean reallyRemoveFromContext() throws ContextException, ResourceRegistryException {
		getElement();
		
		RemoveConstraint removeConstraint = RemoveConstraint.keep;
		
		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if(propagationConstraint.getRemoveConstraint() != null) {
				removeConstraint = propagationConstraint.getRemoveConstraint();
			} else {
				String error = String.format("%s.%s in %s is null. %s", Relation.PROPAGATION_CONSTRAINT,
						PropagationConstraint.REMOVE_PROPERTY, Utility.toJsonString(element, true),
						Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				logger.error(error);
				throw new ResourceRegistryException(error);
			}
		} catch(Exception e) {
			String error = String.format("Error while getting %s from %s while performing RemoveFromContext. %s",
					Relation.PROPAGATION_CONSTRAINT, Utility.toJsonString(element, true),
					Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			logger.error(error);
			throw new ResourceRegistryException(error, e);
			
		}
		
		/*
		 * In any removeConstraint value the relation MUST be removed from context to
		 * avoid to have edge having a source outside of the context.
		 */
		getWorkingContext().removeElement(getElement(), orientGraph);
		
		switch(removeConstraint) {
			case cascade:
				getTargetEntityManagement().internalRemoveFromContext();
				break;
			
			case cascadeWhenOrphan:
				Vertex target = (Vertex) getTargetEntityManagement().getElement();
				
				Iterable<Edge> iterable = target.getEdges(Direction.IN);
				Iterator<Edge> iterator = iterable.iterator();
				int count = 0;
				OrientEdge edge = null;
				while(iterator.hasNext()) {
					edge = (OrientEdge) iterator.next();
					OrientEdge thisOrientEdge = (OrientEdge) getElement();
					if(edge.compareTo(thisOrientEdge) != 0) {
						if(thisOrientEdge.getOutVertex().compareTo(edge.getOutVertex()) != 0) {
							count++;
							break;
						}
						/*
						 * else{ ContextUtility.removeFromActualContext(orientGraph, edge); }
						 */
					}
				}
				
				if(count > 0) {
					logger.trace(
							"{} point to {} which is not orphan ({} exists). Giving {} directive, it will be not remove from current context.",
							element, target, edge, removeConstraint);
				} else {
					getTargetEntityManagement().internalRemoveFromContext();
				}
				break;
			
			case keep:
				break;
			
			default:
				break;
		}
		
		return true;
	}
	
	@Override
	protected boolean reallyDelete() throws RelationNotFoundException, ResourceRegistryException {
		logger.debug("Going to remove {} with UUID {}. Related {}s will be detached.", accessType.getName(), uuid,
				targetEntityClass.getSimpleName());
		
		getElement();
		
		RemoveConstraint removeConstraint = RemoveConstraint.keep;
		
		try {
			PropagationConstraint propagationConstraint = Utility.getEmbedded(PropagationConstraint.class, element,
					Relation.PROPAGATION_CONSTRAINT);
			if(propagationConstraint.getRemoveConstraint() != null) {
				removeConstraint = propagationConstraint.getRemoveConstraint();
			} else {
				String error = String.format("%s.%s in %s is null. %s", Relation.PROPAGATION_CONSTRAINT,
						PropagationConstraint.REMOVE_PROPERTY, Utility.toJsonString(element, true),
						Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				logger.error(error);
				throw new ResourceRegistryException(error);
			}
		} catch(Exception e) {
			logger.warn("Error while getting {} from {}. Assuming {}. {}", Relation.PROPAGATION_CONSTRAINT,
					Utility.toJsonString(element, true), removeConstraint, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
		}
		
		Vertex target = (Vertex) getTargetEntityManagement().getElement();
		element.remove();
		
		switch(removeConstraint) {
			case cascade:
				getTargetEntityManagement().internalDelete();
				break;
			
			case cascadeWhenOrphan:
				Iterable<Edge> iterable = target.getEdges(Direction.IN);
				Iterator<Edge> iterator = iterable.iterator();
				if(iterator.hasNext()) {
					logger.trace("{} point to {} which is not orphan. Giving {} directive, it will be keep.", element,
							target, removeConstraint);
				} else {
					getTargetEntityManagement().internalDelete();
				}
				break;
			
			case keep:
				break;
			
			default:
				break;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected Collection<JSONObject> serializeEdges(Iterable<Edge> edges, boolean postFilterPolymorphic)
			throws ResourceRegistryException {
		Map<String,JSONObject> visitedSourceResources = new HashMap<>();
		for(Edge edge : edges) {
			if(postFilterPolymorphic && edge.getLabel().compareTo(erType) != 0) {
				continue;
			}
			RelationManagement relationManagement = ERManagementUtility.getRelationManagement(getWorkingContext(),
					orientGraph, edge);
			visitedSourceResources = relationManagement.fullSerialize(visitedSourceResources);
		}
		return visitedSourceResources.values();
	}
	
	protected String serializeJSONObjectList(Collection<JSONObject> list) {
		JSONArray jsonArray = new JSONArray(list);
		return jsonArray.toString();
	}
	
	@Override
	public String reallyGetAll(boolean polymorphic) throws ResourceRegistryException {
		Iterable<Edge> edges = orientGraph.getEdgesOfClass(erType, polymorphic);
		Collection<JSONObject> collection = serializeEdges(edges, false);
		return serializeJSONObjectList(collection);
	}
	
	public String reallyGetAllFrom(UUID uuid, Direction direction, boolean polymorphic)
			throws ResourceRegistryException {
		EntityManagement entityManagement = null;
		try {
			entityManagement = (EntityManagement) ERManagementUtility.getERManagementFromUUID(getWorkingContext(),
					orientGraph, uuid);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new ResourceRegistryException(
					String.format("Provided UUID %s does not belogn to any %s", uuid.toString(), Entity.NAME));
		}
		
		Vertex vertex = (Vertex) entityManagement.getElement();
		
		List<JSONObject> list = new ArrayList<>();
		Iterable<Edge> edges = vertex.getEdges(direction, erType);
		list.addAll(serializeEdges(edges, !polymorphic));
		
		return serializeJSONObjectList(list);
		
	}
	
	public String allFrom(UUID uuid, Direction direction, boolean polymorphic) throws ResourceRegistryException {
		try {
			orientGraph = getWorkingContext().getGraph(PermissionMode.READER);
			
			return reallyGetAllFrom(uuid, direction, polymorphic);
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
	
	@Override
	public boolean addToContext() throws ERNotFoundException, ContextException {
		logger.debug("Going to add {} with UUID {} to actual Context", accessType.getName(), uuid);
		
		try {
			orientGraph = ContextUtility.getAdminSecurityContext().getGraph(PermissionMode.WRITER);
			
			boolean added = forcedAddToContext();
			
			orientGraph.commit();
			logger.info("{} with UUID {} successfully added to actual Context", accessType.getName(), uuid);
			
			return added;
		} catch(Exception e) {
			logger.error("Unable to add {} with UUID {} to actual Context", accessType.getName(), uuid, e);
			if(orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e);
		} finally {
			if(orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}
	
}
