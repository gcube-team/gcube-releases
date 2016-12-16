/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.util.Iterator;
import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.relation.IsParentOf;
import org.gcube.informationsystem.resourceregistry.api.ContextManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.resources.utils.HeaderUtility;
import org.gcube.informationsystem.resourceregistry.resources.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextManagementImpl implements ContextManagement {

	private static Logger logger = LoggerFactory
			.getLogger(ContextManagementImpl.class);

	protected Vertex checkContext(OrientGraph orientGraph, UUID parentContext,
			String contextName) throws ContextNotFoundException,
			ContextException {

		Vertex parent = null;

		if (parentContext != null) {

			parent = getContext(orientGraph, parentContext);

			// TODO Rewrite using Gremlin
			String select = "SELECT FROM (TRAVERSE out(" + IsParentOf.NAME
					+ ") FROM " + parent.getId() + " MAXDEPTH 1) WHERE "
					+ Context.NAME_PROPERTY + "=\"" + contextName + "\" AND "
					+ Context.HEADER_PROPERTY + "." + Header.UUID_PROPERTY
					+ "<>\"" + parentContext.toString() + "\"";

			logger.trace(select);

			String message = "A context with the same name (" + contextName
					+ ") has been already created as child of "
					+ parentContext.toString() + "(name="
					+ parent.getProperty(Context.NAME_PROPERTY).toString()
					+ ")";

			logger.trace("Checking if {} -> {}", message, select);

			OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(
					select);
			Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery)
					.execute();

			if (vertexes != null && vertexes.iterator().hasNext()) {
				throw new ContextException(message);
			}

		} else {
			// TODO Rewrite using Gremlin
			String select = "SELECT FROM "
					+ org.gcube.informationsystem.model.entity.Context.NAME
					+ " WHERE " + Context.NAME_PROPERTY + " = \"" + contextName
					+ "\"" + " AND in(\"" + IsParentOf.NAME + "\").size() = 0";

			OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(
					select);
			Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery)
					.execute();

			if (vertexes != null && vertexes.iterator().hasNext()) {
				throw new ContextException(
						"A root context with the same name (" + contextName
								+ ") already exist");
			}

		}

		return parent;

	}

	public Vertex getContext(OrientGraph orientGraph, UUID context)
			throws ContextNotFoundException {
		try {
			return Utility.getElementByUUID(orientGraph, Context.NAME, context, Vertex.class);
		} catch (ResourceRegistryException e) {
			throw new ContextNotFoundException(e.getMessage());
		}
	}

	@Override
	public String create(UUID parentContext, String name)
			throws ContextCreationException, InternalException {

		OrientGraph orientGraph = null;
		UUID uuid = UUID.randomUUID();

		try {
			logger.info(
					"Trying to create {} with name {} and parent {} UUID {}",
					Context.NAME, name, Context.NAME, parentContext);

			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			Vertex parent;
			try {
				parent = checkContext(orientGraph, parentContext, name);
			} catch (ContextException e) {
				throw new ContextCreationException(e.getMessage());
			}

			SecurityContext.createSecurityContext(orientGraph, uuid);

			OrientVertex context = orientGraph.addVertex("class:"
					+ Context.NAME);
			context.setProperty(Context.NAME_PROPERTY, name);
			context.save();

			HeaderUtility.addHeader(context, uuid);

			if (parentContext != null) {
				OrientEdge edge = orientGraph.addEdge(null, parent, context,
						IsParentOf.NAME);
				HeaderUtility.addHeader(edge, null);
				edge.save();
			}

			SecurityContext.addToSecurityContext(orientGraph, context, uuid);
			
			logger.trace("Creating {}", Utility.toJsonString(context, true));

			orientGraph.commit();

			Vertex readContext = getContext(orientGraph, uuid);
			logger.info("Context created {}",
					Utility.toJsonString((OrientVertex) readContext, true));
			return Utility.toJsonString((OrientVertex) readContext, false);

		} catch (ContextCreationException e) {
			throw e;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
				SecurityContext.deleteSecurityContext(orientGraph, uuid, true);
			}
			throw new InternalException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}

	}

	@Override
	public String read(UUID contextUUID) throws ContextNotFoundException,
			ContextException {
		OrientGraph orientGraph = SecurityContextMapper
				.getSecurityContextFactory(
						SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
						PermissionMode.READER).getTx();
		Vertex context = getContext(orientGraph, contextUUID);
		return Utility.toJsonString((OrientVertex) context, false);
	}

	@Override
	public String rename(UUID contextUUID, String newName)
			throws ContextNotFoundException, ContextException {

		OrientGraph orientGraph = null;

		try {
			logger.info("Trying to rename {} with UUID {} to {}", Context.NAME,
					contextUUID, newName);

			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			Vertex context = getContext(orientGraph, contextUUID);

			UUID parentUUID = null;

			Iterable<Edge> edges = context.getEdges(Direction.IN,
					IsParentOf.NAME);
			if (edges != null && edges.iterator().hasNext()) {
				Iterator<Edge> iteratorEdge = edges.iterator();
				Edge edge = iteratorEdge.next();

				if (iteratorEdge.hasNext()) {
					throw new ContextException("");
				}

				Vertex parent = edge.getVertex(Direction.OUT);
				parentUUID = UUID.fromString((String) parent
						.getProperty(Context.HEADER_PROPERTY + "."
								+ Header.UUID_PROPERTY));
			}

			checkContext(orientGraph, parentUUID, newName);
			context.setProperty(Context.NAME_PROPERTY, newName);

			orientGraph.commit();

			String contextJsonString = Utility.toJsonString(context, true);
			logger.info("Context renamed {}", contextJsonString);

			return Utility.toJsonString((OrientVertex) context, false);
		} catch (ContextException ce) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw ce;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public String move(UUID newParentUUID, UUID contextToMoveUUID)
			throws ContextNotFoundException, ContextException {

		OrientGraph orientGraph = null;
		try {
			logger.info(
					"Trying to move {} with UUID {} as child of {} with UUID {}",
					Context.NAME, contextToMoveUUID, Context.NAME, newParentUUID);

			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();
			Vertex context = getContext(orientGraph, contextToMoveUUID);

			logger.trace("Context to move {}",
					Utility.toJsonString(context, true));

			checkContext(orientGraph, newParentUUID,
					context.getProperty(Context.NAME_PROPERTY).toString());

			// Removing the old parent relationship if any
			Iterable<Edge> edges = context.getEdges(Direction.IN,
					IsParentOf.NAME);
			if (edges != null && edges.iterator().hasNext()) {
				Iterator<Edge> edgeIterator = edges.iterator();
				Edge edge = edgeIterator.next();
				logger.trace("Removing {} {}", Edge.class.getSimpleName(), edge);
				edge.remove();
			}

			if (newParentUUID != null) {
				Vertex parent = getContext(orientGraph, newParentUUID);
				logger.trace("New Parent Context {}",
						Utility.toJsonString(parent, true));
				OrientEdge edge = orientGraph.addEdge(null, parent, context,
						IsParentOf.NAME);
				HeaderUtility.addHeader(edge, null);
				edge.save();
			}

			orientGraph.commit();

			context = getContext(orientGraph, contextToMoveUUID);
			String contextJsonString = Utility.toJsonString(context, true);
			logger.info("Context moved {}", contextJsonString);

			return Utility.toJsonString((OrientVertex) context, false);

		} catch (ContextException ce) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw ce;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

	@Override
	public boolean delete(UUID uuid) throws ContextNotFoundException,
			ContextException {

		OrientGraph orientGraph = null;

		try {
			logger.info("Trying to remove {} with UUID {}", Context.NAME, uuid);

			orientGraph = SecurityContextMapper.getSecurityContextFactory(
					SecurityContextMapper.ADMIN_SECURITY_CONTEXT_UUID,
					PermissionMode.WRITER).getTx();

			Vertex context = getContext(orientGraph, uuid);
			logger.trace("Context to be delete {}",
					Utility.toJsonString(context, true));

			Iterable<Edge> edges = context.getEdges(Direction.OUT,
					IsParentOf.NAME);
			if (edges != null && edges.iterator().hasNext()) {
				throw new ContextException(
						"Only context with no children can be deleted");
			}

			SecurityContext.deleteSecurityContext(orientGraph, uuid, false);

			context.remove();

			orientGraph.commit();
			logger.info("{} with UUID {} successfully removed", Context.NAME,
					uuid);
			return true;

		} catch (ContextException ce) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw ce;
		} catch (Exception e) {
			if (orientGraph != null) {
				orientGraph.rollback();
			}
			throw new ContextException(e.getMessage());
		} finally {
			if (orientGraph != null) {
				orientGraph.shutdown();
			}
		}
	}

}
