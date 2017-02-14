/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.context;

import java.util.Iterator;
import java.util.UUID;

import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole.ALLOW_MODES;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class SecurityContext {

	private static Logger logger = LoggerFactory
			.getLogger(SecurityContext.class);

	public static final String DEFAULT_WRITER_ROLE = "writer";
	public static final String DEFAULT_READER_ROLE = "reader";

	public static void addToSecurityContext(OrientGraph orientGraph,
			Vertex vertex, UUID context) {
		OSecurity oSecurity = orientGraph.getRawGraph().getMetadata()
				.getSecurity();
		SecurityContext.addToSecurityContext(oSecurity, vertex, context);
	}

	public static void addToSecurityContext(OSecurity oSecurity, Vertex vertex,
			UUID context) {
		OrientVertex orientVertex = (OrientVertex) vertex;

		SecurityContext.allowSecurityContextRoles(oSecurity,
				orientVertex.getRecord(), context);
		orientVertex.save();
		
		Iterable<Edge> iterable = vertex.getEdges(Direction.BOTH);
		Iterator<Edge> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			OrientEdge edge = (OrientEdge) iterator.next();
			SecurityContext.allowSecurityContextRoles(oSecurity,
					edge.getRecord(), context);
			edge.save();
		}
	}

	public static void addToSecurityContext(OrientGraph orientGraph, Edge edge,
			UUID context) {
		OSecurity oSecurity = orientGraph.getRawGraph().getMetadata()
				.getSecurity();
		SecurityContext.addToSecurityContext(oSecurity, edge, context);
	}
	
	public static void addToSecurityContext(OSecurity oSecurity, Edge edge,
			UUID context) {
		OrientEdge orientEdge = (OrientEdge) edge;
		SecurityContext.allowSecurityContextRoles(oSecurity,
				orientEdge.getRecord(), context);
	}

	protected static void allowSecurityContextRoles(OSecurity oSecurity,
			ODocument oDocument, UUID context) {
		oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_ALL,
				SecurityContextMapper.getSecurityRoleOrUserName(
						SecurityContextMapper.PermissionMode.WRITER,
						SecurityContextMapper.SecurityType.ROLE, context));

		oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_READ,
				SecurityContextMapper.getSecurityRoleOrUserName(
						SecurityContextMapper.PermissionMode.READER,
						SecurityContextMapper.SecurityType.ROLE, context));

		oDocument.save();

		// oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_ALL,
		// DEFAULT_WRITER_ROLE);
		// oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_READ,
		// DEFAULT_READER_ROLE);

	}

	public static void createSecurityContext(OrientGraph orientGraph,
			UUID context) {
		
		ODatabaseDocumentTx oDatabaseDocumentTx = orientGraph.getRawGraph();
		OSecurity oSecurity = oDatabaseDocumentTx.getMetadata().getSecurity();

		ORole writer = oSecurity.getRole(DEFAULT_WRITER_ROLE);
		ORole reader = oSecurity.getRole(DEFAULT_READER_ROLE);

		String writeRoleName = SecurityContextMapper.getSecurityRoleOrUserName(
					SecurityContextMapper.PermissionMode.WRITER,
					SecurityContextMapper.SecurityType.ROLE, context);
		ORole writerRole = oSecurity.createRole(writeRoleName,
				writer, ALLOW_MODES.DENY_ALL_BUT);
		writerRole.save();
		logger.trace("{} created", writerRole);
		
		
		String readerRoleName = SecurityContextMapper.getSecurityRoleOrUserName(
					SecurityContextMapper.PermissionMode.READER,
					SecurityContextMapper.SecurityType.ROLE, context);
		ORole readerRole = oSecurity.createRole(readerRoleName,
				reader, ALLOW_MODES.DENY_ALL_BUT);
		readerRole.save();
		logger.trace("{} created", readerRole);

		
		String writerUserName = SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.WRITER,
				SecurityContextMapper.SecurityType.USER, context);
		OUser writerUser = oSecurity.createUser(writerUserName,
				DatabaseEnvironment.DEFAULT_PASSWORDS
					.get(SecurityContextMapper.PermissionMode.WRITER),
				writerRole);
		writerUser.save();
		logger.trace("{} created", writerUser);
		
		
		String readerUserName =  SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.READER,
				SecurityContextMapper.SecurityType.USER, context);
		OUser readerUser = oSecurity.createUser(readerUserName,
				DatabaseEnvironment.DEFAULT_PASSWORDS
					.get(SecurityContextMapper.PermissionMode.READER),
				readerRole);
		readerUser.save();
		logger.trace("{} created", readerUser);
		
		oDatabaseDocumentTx.commit();
		
		logger.trace(
				"Security Context (roles and users) with UUID {} successfully created",
				context.toString());
	}

	public static void deleteSecurityContext(OrientGraph orientGraph,
			UUID context, boolean commit) {

		logger.trace(
				"Going to remove Security Context (roles and users) with UUID {}",
				context.toString());
		ODatabaseDocumentTx oDatabaseDocumentTx = orientGraph.getRawGraph();
		OSecurity oSecurity = oDatabaseDocumentTx.getMetadata().getSecurity();

		String user = SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.READER,
				SecurityContextMapper.SecurityType.USER, context);
		boolean dropped = oSecurity.dropUser(user);
		if (dropped) {
			logger.trace("{} successfully dropped", user);
		} else {
			logger.error("{} was not dropped successfully", user);
		}

		user = SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.WRITER,
				SecurityContextMapper.SecurityType.USER, context);
		dropped = oSecurity.dropUser(user);
		if (dropped) {
			logger.trace("{} successfully dropped", user);
		} else {
			logger.error("{} was not dropped successfully", user);
		}

		String role = SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.READER,
				SecurityContextMapper.SecurityType.ROLE, context);
		dropped = oSecurity.dropRole(role);
		if (dropped) {
			logger.trace("{} successfully dropped", role);
		} else {
			logger.error("{} was not dropped successfully", role);
		}

		role = SecurityContextMapper.getSecurityRoleOrUserName(
				SecurityContextMapper.PermissionMode.WRITER,
				SecurityContextMapper.SecurityType.ROLE, context);
		dropped = oSecurity.dropRole(role);
		if (dropped) {
			logger.trace("{} successfully dropped", role);
		} else {
			logger.error("{} was not dropped successfully", role);
		}

		if(commit){
			oDatabaseDocumentTx.commit();
		}

		logger.trace(
				"Security Context (roles and users) with UUID {} successfully removed",
				context.toString());
	}
}
