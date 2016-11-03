/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole.ALLOW_MODES;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class SecurityContext {

	private static Logger logger = LoggerFactory
			.getLogger(SecurityContext.class);

	public static final String DEFAULT_WRITER_ROLE = "writer";
	public static final String DEFAULT_READER_ROLE = "reader";

	public static void addToSecurityContext(OrientGraph orientGraph,
			Vertex vertex, String contextID) {
		OSecurity oSecurity = orientGraph.getRawGraph().getMetadata()
				.getSecurity();
		SecurityContext.addToSecurityContext(oSecurity, vertex, contextID);
	}

	public static void addToSecurityContext(OSecurity oSecurity, Vertex vertex,
			String contextID) {
		OrientVertex orientVertex = (OrientVertex) vertex;

		SecurityContext.allowSecurityContextRoles(oSecurity,
				orientVertex.getRecord(), contextID);

		Iterable<Edge> iterable = vertex.getEdges(Direction.BOTH);
		Iterator<Edge> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			OrientEdge edge = (OrientEdge) iterator.next();
			SecurityContext.allowSecurityContextRoles(oSecurity,
					edge.getRecord(), contextID);
		}
	}

	public static void addToSecurityContext(OrientGraph orientGraph, Edge edge,
			String contextID) {
		OSecurity oSecurity = orientGraph.getRawGraph().getMetadata()
				.getSecurity();
		SecurityContext.addToSecurityContext(oSecurity, edge, contextID);
	}

	public static void addToSecurityContext(OSecurity oSecurity, Edge edge,
			String contextID) {
		OrientEdge orientEdge = (OrientEdge) edge;
		SecurityContext.allowSecurityContextRoles(oSecurity,
				orientEdge.getRecord(), contextID);
	}

	protected static void allowSecurityContextRoles(OSecurity oSecurity,
			ODocument oDocument, String contextID) {
		oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_ALL,
				SecurityContextMapper.getSecurityRoleOrUserName(
						SecurityContextMapper.PermissionMode.WRITER,
						SecurityContextMapper.SecurityType.ROLE, contextID));

		oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_READ,
				SecurityContextMapper.getSecurityRoleOrUserName(
						SecurityContextMapper.PermissionMode.READER,
						SecurityContextMapper.SecurityType.ROLE, contextID));

		// oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_ALL,
		// DEFAULT_WRITER_ROLE);
		// oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_READ,
		// DEFAULT_READER_ROLE);

	}

	public static void createSecurityContext(OrientGraph orientGraph,
			String contextID, boolean commit) {
		OSecurity oSecurity = orientGraph.getRawGraph().getMetadata()
				.getSecurity();

		ORole writer = oSecurity.getRole(DEFAULT_WRITER_ROLE);
		ORole reader = oSecurity.getRole(DEFAULT_READER_ROLE);

		ORole writerRole = oSecurity.createRole(SecurityContextMapper
				.getSecurityRoleOrUserName(
						SecurityContextMapper.PermissionMode.WRITER,
						SecurityContextMapper.SecurityType.ROLE, contextID),
				writer, ALLOW_MODES.DENY_ALL_BUT);

		ORole readerRole = oSecurity.createRole(SecurityContextMapper
				.getSecurityRoleOrUserName(
						SecurityContextMapper.PermissionMode.READER,
						SecurityContextMapper.SecurityType.ROLE, contextID),
				reader, ALLOW_MODES.DENY_ALL_BUT);

		oSecurity.createUser(SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.WRITER,
				SecurityContextMapper.SecurityType.USER, contextID),
				DatabaseEnvironment.DEFAULT_PASSWORDS
						.get(SecurityContextMapper.PermissionMode.WRITER),
				writerRole);
		oSecurity.createUser(SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.READER,
				SecurityContextMapper.SecurityType.USER, contextID),
				DatabaseEnvironment.DEFAULT_PASSWORDS
						.get(SecurityContextMapper.PermissionMode.READER),
				readerRole);

		if (commit) {
			orientGraph.commit();
		}

		logger.trace(
				"Security Context (roles and users) with UUID {} successfully created",
				contextID);
	}

	public static void deleteSecurityContext(OrientGraph orientGraph,
			String contextID, boolean commit) {

		logger.trace(
				"Going to remove Security Context (roles and users) with UUID {}",
				contextID);

		OSecurity oSecurity = orientGraph.getRawGraph().getMetadata()
				.getSecurity();

		oSecurity.dropUser(SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.READER,
				SecurityContextMapper.SecurityType.USER, contextID));
		oSecurity.dropUser(SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.WRITER,
				SecurityContextMapper.SecurityType.USER, contextID));
		oSecurity.dropRole(SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.READER,
				SecurityContextMapper.SecurityType.ROLE, contextID));
		oSecurity.dropRole(SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.WRITER,
				SecurityContextMapper.SecurityType.ROLE, contextID));

		if (commit) {
			orientGraph.commit();
		}

		logger.trace(
				"Security Context (roles and users) with UUID {} successfully removed",
				contextID);
	}
}
