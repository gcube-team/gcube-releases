/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.utils;

import java.util.Iterator;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.informationsystem.model.orientdb.impl.embedded.Header;
import org.gcube.informationsystem.model.orientdb.impl.entity.Context;
import org.gcube.informationsystem.model.orientdb.impl.entity.Entity;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContext;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class ContextUtility {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextUtility.class);
	
	public static void addToActualContext(OrientGraph orientGraph, Vertex vertex)
			throws ContextException {
		String contextID = ContextUtility.getActualContextUUID();
		SecurityContext.addToSecurityContext(orientGraph, vertex, contextID);
	}

	public static void addToActualContext(OSecurity oSecurity, Vertex vertex)
			throws ContextException {
		String contextID = ContextUtility.getActualContextUUID();
		SecurityContext.addToSecurityContext(oSecurity, vertex, contextID);
	}

	public static void addToActualContext(OrientGraph orientGraph, Edge edge)
			throws ContextException {
		String contextID = ContextUtility.getActualContextUUID();
		SecurityContext.addToSecurityContext(orientGraph, edge, contextID);
	}

	public static void addToActualContext(OSecurity oSecurity, Edge edge)
			throws ContextException {
		String contextID = ContextUtility.getActualContextUUID();
		SecurityContext.addToSecurityContext(oSecurity, edge, contextID);
	}

	protected static String getContextUUIDFromContextVertex(Vertex vertex) {
		ODocument header = vertex.getProperty(Entity.HEADER_PROPERTY);
		String contextID = header.field(Header.UUID_PROPERTY);
		return contextID;
	}

	public static String getActualContextUUID() throws ContextException {
		try {
			String scope = ScopeProvider.instance.get();
			OrientGraphFactory factory = SecurityContextMapper.getSecurityContextFactory(null, PermissionMode.READER);
			Vertex context = ContextUtility.getContextVertexByFullName(
					factory.getTx(), scope);
			return getContextUUIDFromContextVertex(context);
		} catch (Exception e) {
			throw new ContextException(
					"Unable to restrive Context UUID from current Context");
		}
	}

	public static OrientGraph getActualSecurityContextGraph(
			PermissionMode permissionMode) throws Exception {
		try {
			String contextID = getActualContextUUID();
			OrientGraphFactory factory = SecurityContextMapper
					.getSecurityContextFactory(contextID, permissionMode);
			return factory.getTx();
		} catch (Exception e) {
			logger.error("Unable to retrieve context.", e);
			throw e;
		}
	}

	public static Vertex getContextVertexByFullName(OrientGraph orientGraph,
			String fullName) throws ContextNotFoundException {

		ScopeBean scopeBean = new ScopeBean(fullName);
		String name = scopeBean.name();

		// TODO Rewrite the previous query using Gremlin
		// Please note that this query works because all the scope parts has a
		// different name
		String select = "SELECT FROM " + Context.class.getSimpleName()
				+ " WHERE " + Context.NAME_PROPERTY + " = \"" + name + "\"";
		;
		OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(
				select);
		Iterable<Vertex> vertexes = orientGraph.command(osqlSynchQuery)
				.execute();

		if (vertexes == null || !vertexes.iterator().hasNext()) {
			throw new ContextNotFoundException(
					"Error retrieving context with name " + fullName);
		}

		Iterator<Vertex> iterator = vertexes.iterator();
		Vertex context = iterator.next();

		logger.trace("Context Representing Vertex : {}",
				Utility.vertexToJsonString(context));
		
		if (iterator.hasNext()) {
			throw new ContextNotFoundException(
					"Found more than one context with name " + name
							+ "but required the one with path" + fullName
							+ ". Please Reimplement the query");
		}

		return context;
	}

	public static String getActualSecurityRoleOrUserName(
			SecurityContextMapper.PermissionMode permissionMode,
			SecurityContextMapper.SecurityType securityType) {
		String scope = ScopeProvider.instance.get();
		return SecurityContextMapper.getSecurityRoleOrUserName(permissionMode,
				securityType, scope);
	}

}
