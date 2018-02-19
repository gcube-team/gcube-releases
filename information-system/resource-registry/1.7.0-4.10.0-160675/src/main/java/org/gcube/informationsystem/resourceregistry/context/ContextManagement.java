package org.gcube.informationsystem.resourceregistry.context;

import java.util.Iterator;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.relation.IsParentOf;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.EntityManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextManagement extends EntityManagement<Context> {
	
	private static Logger logger = LoggerFactory.getLogger(ContextManagement.class);
	
	protected String name;
	
	private void init() {
		this.ignoreStartWithKeys.add(Context.PARENT_PROPERTY);
		this.ignoreStartWithKeys.add(Context.CHILDREN_PROPERTY);
		this.erType = Context.NAME;
	}
	
	public ContextManagement() {
		super(AccessType.CONTEXT);
		init();
	}
	
	public ContextManagement(OrientGraph orientGraph) throws ResourceRegistryException {
		this();
		this.orientGraph = orientGraph;
		getWorkingContext();
	}
	
	public String getName() {
		if(name == null) {
			if(element == null) {
				if(jsonNode != null) {
					name = jsonNode.get(Context.NAME_PROPERTY).asText();
				}
			} else {
				name = element.getProperty(Context.NAME_PROPERTY);
			}
		}
		return name;
	}
	
	protected SecurityContext getWorkingContext() throws ResourceRegistryException {
		if(workingContext == null) {
			workingContext = ContextUtility.getInstance()
					.getSecurityContextByUUID(DatabaseEnvironment.CONTEXT_SECURITY_CONTEXT_UUID);
		}
		return workingContext;
	}
	
	@Override
	protected ContextNotFoundException getSpecificElementNotFoundException(ERNotFoundException e) {
		return new ContextNotFoundException(e.getMessage(), e.getCause());
	}
	
	@Override
	protected EntityAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(String message) {
		return new EntityAvailableInAnotherContextException(message);
	}
	
	@Override
	protected ContextAlreadyPresentException getSpecificERAlreadyPresentException(String message) {
		return new ContextAlreadyPresentException(message);
	}
	
	protected void checkContext(ContextManagement parentContext)
			throws ContextNotFoundException, ContextAlreadyPresentException, ResourceRegistryException {
		
		if(parentContext != null) {
			String parentId = parentContext.getElement().getId().toString();
			
			// TODO Rewrite using Gremlin
			String select = "SELECT FROM (TRAVERSE out(" + IsParentOf.NAME + ") FROM " + parentId
					+ " MAXDEPTH 1) WHERE " + Context.NAME_PROPERTY + "=\"" + getName() + "\" AND "
					+ Context.HEADER_PROPERTY + "." + Header.UUID_PROPERTY + "<>\"" + parentContext.uuid + "\"";
			
			logger.trace(select);
			
			StringBuilder message = new StringBuilder();
			message.append("A context with name (");
			message.append(getName());
			message.append(") has been already created as child of ");
			message.append(parentContext.serializeSelfOnly().toString());
			
			logger.trace("Checking if {} -> {}", message, select);
			
			OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(select);
			Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery).execute();
			
			if(vertexes != null && vertexes.iterator().hasNext()) {
				throw new ContextAlreadyPresentException(message.toString());
			}
			
		} else {
			// TODO Rewrite using Gremlin
			String select = "SELECT FROM " + org.gcube.informationsystem.model.entity.Context.NAME + " WHERE "
					+ Context.NAME_PROPERTY + " = \"" + getName() + "\"" + " AND in(\"" + IsParentOf.NAME
					+ "\").size() = 0";
			
			OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(select);
			Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery).execute();
			
			if(vertexes != null && vertexes.iterator().hasNext()) {
				throw new ContextAlreadyPresentException(
						"A root context with the same name (" + this.getName() + ") already exist");
			}
			
		}
		
	}
	
	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeAsJson().toString();
	}
	
	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		
		JSONObject context = serializeSelfOnly();
		
		int count = 0;
		Iterable<Edge> parents = getElement().getEdges(Direction.IN);
		for(Edge edge : parents) {
			if(++count > 1) {
				throw new ContextException("A " + Context.NAME + " can not have more than one parent");
			}
			try {
				IsParentOfManagement isParentOfManagement = new IsParentOfManagement(orientGraph);
				isParentOfManagement.setElement(edge);
				JSONObject isParentOf = isParentOfManagement.serializeAsJson(true, false);
				context.putOpt(Context.PARENT_PROPERTY, isParentOf);
			} catch(JSONException e) {
				logger.error("Unable to correctly serialize {}. {}", edge, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				throw new ContextException("");
			}
		}
		
		Iterable<Edge> childrenEdges = getElement().getEdges(Direction.OUT);
		for(Edge edge : childrenEdges) {
			
			IsParentOfManagement isParentOfManagement = new IsParentOfManagement(orientGraph);
			isParentOfManagement.setElement(edge);
			try {
				JSONObject isParentOf = isParentOfManagement.serializeAsJson();
				context = addRelation(context, isParentOf, Context.CHILDREN_PROPERTY);
			} catch(ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. {}", edge, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				throw e;
			} catch(Exception e) {
				logger.error("Unable to correctly serialize {}. {}", edge, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				throw new ResourceRegistryException(e);
			}
		}
		
		return context;
	}
	
	@Override
	protected Vertex reallyCreate() throws ERAlreadyPresentException, ResourceRegistryException {
		SecurityContext securityContext = null;
		SecurityContext parentSecurityContext = null;
		
		try {
			JsonNode isParentOfJsonNode = jsonNode.get(Context.PARENT_PROPERTY);
			
			if(isParentOfJsonNode != null && !(isParentOfJsonNode instanceof NullNode)) {
				
				JsonNode parentJsonNode = isParentOfJsonNode.get(Relation.SOURCE_PROPERTY);
				ContextManagement parentContextManagement = new ContextManagement(orientGraph);
				parentContextManagement.setJSON(parentJsonNode);
				UUID parentUUID = parentContextManagement.uuid;
				parentSecurityContext = ContextUtility.getInstance().getSecurityContextByUUID(parentUUID);
				
				
				checkContext(parentContextManagement);
				if(uuid == null) {
					uuid = UUID.randomUUID();
				}
				
				createVertex();
				
				IsParentOfManagement isParentOfManagement = new IsParentOfManagement(orientGraph);
				isParentOfManagement.setJSON(isParentOfJsonNode);
				isParentOfManagement.setSourceEntityManagement(parentContextManagement);
				isParentOfManagement.setTargetEntityManagement(this);
				
				isParentOfManagement.internalCreate();
				
			} else {
				checkContext(null);
				createVertex();
			}
			
			securityContext = new SecurityContext(uuid);
			securityContext.setParentSecurityContext(parentSecurityContext);
			securityContext.create(orientGraph);
			
			ContextUtility.getInstance().addSecurityContext(securityContext);
			
			return getElement();
		} catch(Exception e) {
			orientGraph.rollback();
			if(securityContext != null) {
				securityContext.delete(orientGraph);
				if(parentSecurityContext!=null && securityContext!=null) {
					parentSecurityContext.getChildren().remove(securityContext);
				}
				ContextUtility.getInstance().removeFromCache(uuid, false);
			}
			throw e;
		}
	}
	
	@Override
	protected Vertex reallyUpdate() throws ERNotFoundException, ResourceRegistryException {
		
		boolean parentChanged = false;
		boolean nameChanged = false;
		
		Vertex parent = null;
		boolean found = false;
		
		Iterable<Vertex> iterable = getElement().getVertices(Direction.IN, IsParentOf.NAME);
		for(Vertex p : iterable) {
			if(found) {
				String message = String.format("{} has more than one parent. {}", Context.NAME,
						Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
				throw new ResourceRegistryException(message.toString());
			}
			parent = p;
			found = true;
		}
		
		ContextManagement actualParentContextManagement = null;
		if(parent != null) {
			actualParentContextManagement = new ContextManagement(orientGraph);
			actualParentContextManagement.setElement(parent);
		}
		
		ContextManagement newParentContextManagement = actualParentContextManagement;
		
		JsonNode isParentOfJsonNode = jsonNode.get(Context.PARENT_PROPERTY);
		JsonNode parentContextJsonNode = null;
		if(isParentOfJsonNode != null && !(isParentOfJsonNode instanceof NullNode)) {
			parentContextJsonNode = isParentOfJsonNode.get(Relation.SOURCE_PROPERTY);
		}
		
		if(parentContextJsonNode != null && !(parentContextJsonNode instanceof NullNode)) {
			UUID parentUUID = org.gcube.informationsystem.impl.utils.Utility.getUUIDFromJsonNode(parentContextJsonNode);
			if(actualParentContextManagement != null) {
				if(parentUUID.compareTo(actualParentContextManagement.uuid) != 0) {
					parentChanged = true;
				}
			} else {
				parentChanged = true;
			}
			
			if(parentChanged) {
				newParentContextManagement = new ContextManagement(orientGraph);
				newParentContextManagement.setJSON(parentContextJsonNode);
			}
		} else {
			if(actualParentContextManagement != null) {
				parentChanged = true;
				newParentContextManagement = null;
			}
			
		}
		
		String oldName = getElement().getProperty(Context.NAME_PROPERTY);
		String newName = jsonNode.get(Context.NAME_PROPERTY).asText();
		if(oldName.compareTo(newName) != 0) {
			nameChanged = true;
			name = newName;
		}
		
		if(parentChanged || nameChanged) {
			checkContext(newParentContextManagement);
		}
		
		if(parentChanged) {
			move(newParentContextManagement, false);
		}
		
		element = (Vertex) ERManagement.updateProperties(oClass, getElement(), jsonNode, ignoreKeys,
				ignoreStartWithKeys);
		
		ContextUtility.getInstance().removeFromCache(uuid, true);
		
		return element;
	}
	
	private void move(ContextManagement newParentContextManagement, boolean check)
			throws ContextNotFoundException, ContextAlreadyPresentException, ResourceRegistryException {
		if(check) {
			checkContext(newParentContextManagement);
		}
		
		SecurityContext newParentSecurityContext = null;
		
		// Removing the old parent relationship if any
		Iterable<Edge> edges = getElement().getEdges(Direction.IN, IsParentOf.NAME);
		if(edges != null && edges.iterator().hasNext()) {
			Iterator<Edge> edgeIterator = edges.iterator();
			Edge edge = edgeIterator.next();
			IsParentOfManagement isParentOfManagement = new IsParentOfManagement();
			isParentOfManagement.setElement(edge);
			isParentOfManagement.internalDelete();
			
			if(edgeIterator.hasNext()) {
				throw new ContextException(
						"Seems that the Context has more than one Parent. " + Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			}
		}
		
		if(newParentContextManagement != null) {
			JsonNode isParentOfJsonNode = jsonNode.get(Context.PARENT_PROPERTY);
			IsParentOfManagement isParentOfManagement = new IsParentOfManagement(orientGraph);
			isParentOfManagement.setJSON(isParentOfJsonNode);
			isParentOfManagement.setSourceEntityManagement(newParentContextManagement);
			isParentOfManagement.setTargetEntityManagement(this);
			isParentOfManagement.internalCreate();
			newParentSecurityContext = ContextUtility.getInstance().getSecurityContextByUUID(newParentContextManagement.uuid);
		}
		
		SecurityContext thisSecurityContext = ContextUtility.getInstance().getSecurityContextByUUID(uuid);
		thisSecurityContext.changeParentSecurityContext(newParentSecurityContext, orientGraph);
	}
	
	@Override
	protected boolean reallyDelete() throws ERNotFoundException, ResourceRegistryException {
		Iterable<Edge> iterable = getElement().getEdges(Direction.OUT);
		Iterator<Edge> iterator = iterable.iterator();
		while(iterator.hasNext()) {
			throw new ContextException("Cannot remove a " + Context.NAME + " having children");
		}
		
		element.remove();
		
		ContextUtility contextUtility = ContextUtility.getInstance();
		SecurityContext securityContext = contextUtility.getSecurityContextByUUID(uuid);
		securityContext.delete(orientGraph);
		
		contextUtility.removeFromCache(uuid, false);
		
		return true;
		
	}
	
	@Override
	public String reallyGetAll(boolean polymorphic) throws ResourceRegistryException {
		JSONArray jsonArray = new JSONArray();
		Iterable<Vertex> iterable = orientGraph.getVerticesOfClass(erType, polymorphic);
		for(Vertex vertex : iterable) {
			ContextManagement contextManagement = new ContextManagement();
			contextManagement.setElement(vertex);
			try {
				JSONObject jsonObject = contextManagement.serializeAsJson();
				jsonArray.put(jsonObject);
			} catch(ResourceRegistryException e) {
				logger.error("Unable to correctly serialize {}. It will be excluded from results. {}",
						vertex.toString(), Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			}
		}
		return jsonArray.toString();
	}
	
}
