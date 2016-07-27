/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.util.Iterator;
import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.orientdb.impl.entity.Context;
import org.gcube.informationsystem.model.relation.ParentOf;
import org.gcube.informationsystem.resourceregistry.api.ContextManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContext;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.resources.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.resources.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class ContextManagementImpl implements ContextManagement {

	private static Logger logger = LoggerFactory.getLogger(ContextManagementImpl.class);

	protected Vertex checkContext(OrientGraph orientGraph,
			String parentContextUUID, String contextName)
			throws ContextNotFoundException, ContextException {

		Vertex parent = null;
		
		if (parentContextUUID != null) {
			
			parent = getContext(orientGraph, parentContextUUID);
			
			// TODO Rewrite using Gremlin
			String select = "SELECT FROM (TRAVERSE out(\"ParentOf\") FROM " +  
					parent.getId() + " MAXDEPTH 1) WHERE " + 
					Context.NAME_PROPERTY + "=\"" + contextName + "\" AND "+ 
					Context.HEADER_PROPERTY + "." + Header.UUID_PROPERTY + 
					"<>\""+ parentContextUUID + "\""; 
			
			logger.trace(select);

			String message = "A context with the same name (" + contextName + 
					") has been already created as child of " +	
					parentContextUUID + "(name=" + 
					parent.getProperty(Context.NAME_PROPERTY).toString() + ")";
			
			logger.trace("Checking if {} -> {}", message, select);
			
			OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(select);
			Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery).execute();
			
			if(vertexes!=null && vertexes.iterator().hasNext()) {
				throw new ContextException(message);
			}


		} else {
			// TODO Rewrite using Gremlin
			String select = "SELECT FROM " + Context.class.getSimpleName() + " WHERE "
					+ Context.NAME_PROPERTY + " = \"" + contextName + "\""
					+ " AND in(\"" + ParentOf.class.getSimpleName() + "\").size() = 0";

			OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(select);
			Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery).execute();

			if (vertexes != null && vertexes.iterator().hasNext()) {
				throw new ContextException("A root context with the same name (" + 
						contextName + ") already exist");
			}

		}

		return parent;
		
	}
	
	public Vertex getContext(OrientGraph orientGraph, String contextUUID) throws ContextNotFoundException {
		try {
			return Utility.getEntityByUUID(orientGraph, org.gcube.informationsystem.model.entity.Context.NAME, contextUUID);
		} catch (ResourceRegistryException e) {
			throw new ContextNotFoundException(e.getMessage());
		}
	}
	
	@Override
	public String create(String parentContextUUID, String name)
			throws ContextCreationException {

		if (parentContextUUID != null && parentContextUUID.compareTo("") == 0) {
			parentContextUUID = null;
		}

		OrientGraph orientGraph = SecurityContextMapper.getSecurityContextFactory(null, PermissionMode.WRITER).getTx();

		Vertex parent;
		try {
			parent = checkContext(orientGraph, parentContextUUID, name);
		} catch (ContextException e) {
			throw new ContextCreationException(e.getMessage());
		}

		try {
			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString();

			SecurityContext.createSecurityContext(orientGraph, uuidString);
			
			FramedGraphFactory framedGraphFactory = new FramedGraphFactory();
			FramedGraph<OrientGraph> framedGraph = framedGraphFactory.create(orientGraph);
			
			Context context = framedGraph.addVertex("class:"+Context.class.getSimpleName(), Context.class);
			context.setName(name);
			
			HeaderUtility.addHeader(context, uuid);
			
			if (parentContextUUID != null) {
				orientGraph.addEdge(null, parent, context.asVertex(), ParentOf.class.getSimpleName());
			}
			
			SecurityContext.addToSecurityContext(orientGraph, context.asVertex(), uuidString);
			
			logger.trace("Creating {}", Utility.vertexToJsonString(context.asVertex()));
			
			orientGraph.commit();
			orientGraph.shutdown();

			return uuidString;

		} catch (Exception e) {
			orientGraph.rollback();
			throw new ContextCreationException(e.getMessage());
		}

	}

	@Override
	public String read(String contextUUID) 
			throws ContextNotFoundException, ContextException {
		
		OrientGraph orientGraph = SecurityContextMapper.getSecurityContextFactory(null, PermissionMode.READER).getTx();
		Vertex context = getContext(orientGraph, contextUUID);
		try {
			return Utility.vertexToJsonString(context, false);
		} catch (JSONException e) {
			throw new ContextException(e.getCause());
		}
		
	}
	
	@Override
	public String rename(String contextUUID, String newName)
			throws ContextNotFoundException, ContextException {
		
		OrientGraph orientGraph = SecurityContextMapper.getSecurityContextFactory(null, PermissionMode.WRITER).getTx();
		
		Vertex context = getContext(orientGraph, contextUUID);
		
		String parentUUID = null;
		
		Iterable<Edge> edges = context.getEdges(Direction.IN, ParentOf.class.getSimpleName());
		if (edges != null && edges.iterator().hasNext()) {
			Iterator<Edge> iteratorEdge = edges.iterator();
			Edge edge = iteratorEdge.next();
			
			if(iteratorEdge.hasNext()){
				throw new ContextException("");
			}
			
			Vertex parent = edge.getVertex(Direction.OUT);
			parentUUID = parent.getProperty(Context.HEADER_PROPERTY + "." + Header.UUID_PROPERTY).toString();
		}
		
		
		checkContext(orientGraph, parentUUID, newName);
		context.setProperty(Context.NAME_PROPERTY, newName);
		
		
		orientGraph.commit();
		orientGraph.shutdown();
		
		return contextUUID;
	}

	
	@Override
	public String move(String newParentUUID, String contextToMoveUUID)
			throws ContextNotFoundException, ContextException{
		
		OrientGraph orientGraph = SecurityContextMapper.getSecurityContextFactory(null, PermissionMode.WRITER).getTx();
		Vertex context = getContext(orientGraph, contextToMoveUUID);
		
		logger.trace("Context to move {}", Utility.vertexToJsonString(context));
		
		checkContext(orientGraph, newParentUUID, context.getProperty(Context.NAME_PROPERTY).toString());
		
		// Removing the old parent relationship if any
		Iterable<Edge> edges = context.getEdges(Direction.IN, ParentOf.class.getSimpleName());
		if (edges != null && edges.iterator().hasNext()) {
			Iterator<Edge> edgeIterator = edges.iterator();
			Edge edge = edgeIterator.next();
			logger.trace("Removing {} {}", Edge.class.getSimpleName(), edge);
			edge.remove();
		}
		
		if(newParentUUID!=null){
			Vertex parent = getContext(orientGraph, newParentUUID);
			logger.trace("New Parent Context {}", Utility.vertexToJsonString(parent));
			orientGraph.addEdge(null, parent, context, ParentOf.class.getSimpleName());
		}
		
		orientGraph.commit();
		orientGraph.shutdown();
		
		return contextToMoveUUID;
	}
	
	@Override
	public String delete(String uuid) throws ContextNotFoundException, ContextException {
		
		OrientGraph orientGraph = SecurityContextMapper.getSecurityContextFactory(null, PermissionMode.WRITER).getTx();
		Vertex context = getContext(orientGraph, uuid);
		logger.trace("Context to be delete {}", Utility.vertexToJsonString(context));
		
		Iterable<Edge> edges = context.getEdges(Direction.OUT, ParentOf.class.getSimpleName());
		if (edges != null && edges.iterator().hasNext()) {
			throw new ContextException("Only context with no children can be deleted");
		}
		
		SecurityContext.deleteSecurityContext(orientGraph, uuid);
		
		context.remove();

		orientGraph.commit();
		orientGraph.shutdown();
		
		return uuid;
	}



}
