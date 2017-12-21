package org.gcube.informationsystem.resourceregistry.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.relation.IsParentOf;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.AdminSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextUtility {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextUtility.class);
	
	private Map<String,UUID> contextUUIDs;
	private Map<UUID,SecurityContext> contexts;
	
	private static ContextUtility contextUtility;
	
	public static ContextUtility getInstance() {
		if(contextUtility == null) {
			contextUtility = new ContextUtility();
		}
		return contextUtility;
	}
	
	private ContextUtility() {
		contextUUIDs = new HashMap<>();
		contexts = new HashMap<>();
	}
	
	private static final InheritableThreadLocal<Boolean> hierarchicMode = new InheritableThreadLocal<Boolean>() {
		
		@Override
		protected Boolean initialValue() {
			return false;
		}
		
	};
	
	public static InheritableThreadLocal<Boolean> getHierarchicMode() {
		return hierarchicMode;
	}
	
	private static String getCurrentContextFullName() {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry = null;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch(Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}
	
	public static SecurityContext getCurrentSecurityContext() throws ResourceRegistryException {
		String fullName = getCurrentContextFullName();
		if(fullName == null) {
			throw new ContextException("Null Token and Scope. Please set your token first.");
		}
		return ContextUtility.getInstance().getSecurityContextByFullName(fullName);
	}
	
	public static AdminSecurityContext getAdminSecurityContext() throws ResourceRegistryException {
		AdminSecurityContext adminSecurityContext = (AdminSecurityContext) ContextUtility.getInstance()
				.getSecurityContextByUUID(DatabaseEnvironment.ADMIN_SECURITY_CONTEXT_UUID);
		return adminSecurityContext;
	}
	
	public synchronized void removeFromCache(UUID uuid, boolean fullNameOnly) throws ResourceRegistryException {
		for(String fullName : contextUUIDs.keySet()) {
			UUID uuidKey = contextUUIDs.get(fullName);
			if(uuidKey.compareTo(uuid) == 0) {
				contextUUIDs.remove(fullName);
				if(!fullNameOnly) {
					contexts.remove(uuid);
				}
				return;
			}
		}
	}
	
	public synchronized void addSecurityContext(SecurityContext securityContext) {
		contexts.put(securityContext.getUUID(), securityContext);
	}
	
	public synchronized void addSecurityContext(String fullname, SecurityContext securityContext) {
		contextUUIDs.put(fullname, securityContext.getUUID());
		contexts.put(securityContext.getUUID(), securityContext);
	}
	
	private synchronized SecurityContext getSecurityContextByFullName(String fullName) throws ContextException {
		try {
			SecurityContext securityContext = null;
			
			logger.trace("Trying to get {} for {}", SecurityContext.class.getSimpleName(), fullName);
			UUID uuid = contextUUIDs.get(fullName);
			
			if(uuid == null) {
				logger.trace("{} for {} is not in cache. Going to get it", SecurityContext.class.getSimpleName(),
						fullName);
				
				Vertex contextVertex = getContextVertexByFullName(fullName);
				
				uuid = Utility.getUUID(contextVertex);
				
				securityContext = getSecurityContextByUUID(uuid, contextVertex);
				
				addSecurityContext(fullName, securityContext);
			} else {
				securityContext = contexts.get(uuid);
			}
			
			return securityContext;
			
		} catch(ContextException e) {
			throw e;
		} catch(Exception e) {
			throw new ContextException("Unable to restrive Context UUID from current Context", e);
		}
	}
	
	protected SecurityContext getSecurityContextByUUID(UUID uuid) throws ResourceRegistryException {
		return getSecurityContextByUUID(uuid, null);
	}
	
	private Vertex getContextVertexByUUID(UUID uuid) throws ResourceRegistryException {
		return Utility.getElementByUUID(getAdminSecurityContext().getGraph(PermissionMode.READER), Context.NAME, uuid,
				Vertex.class);
	}
	
	private SecurityContext getSecurityContextByUUID(UUID uuid, Vertex contextVertex) throws ResourceRegistryException {
		SecurityContext securityContext = contexts.get(uuid);
		if(securityContext == null) {
			
			securityContext = new SecurityContext(uuid);
			
			try {
				if(contextVertex == null) {
					contextVertex = getContextVertexByUUID(uuid);
				}
				Vertex parentVertex = contextVertex.getVertices(Direction.IN, IsParentOf.NAME).iterator().next();
				
				if(parentVertex != null) {
					UUID parentUUID = Utility.getUUID(parentVertex);
					securityContext.setParentSecurityContext(getSecurityContextByUUID(parentUUID, parentVertex));
				}
				
			} catch(NoSuchElementException e) {
				// No parent
			}
			
			contexts.put(uuid, securityContext);
		}
		
		return securityContext;
	}
	
	private Vertex getContextVertexByFullName(String fullName) throws ResourceRegistryException {
		
		logger.trace("Going to get {} {} from full name '{}'", Context.NAME, Vertex.class.getSimpleName(), fullName);
		
		ScopeBean scopeBean = new ScopeBean(fullName);
		String name = scopeBean.name();
		
		// TODO Rewrite the query using Gremlin
		// Please note that this query works because all the scope parts has a
		// different name
		String select = "SELECT FROM " + Context.class.getSimpleName() + " WHERE " + Context.NAME_PROPERTY + " = \""
				+ name + "\"";
		;
		OSQLSynchQuery<Vertex> osqlSynchQuery = new OSQLSynchQuery<Vertex>(select);
		
		Iterable<Vertex> vertexes = getAdminSecurityContext().getGraph(PermissionMode.READER).command(osqlSynchQuery)
				.execute();
		
		if(vertexes == null || !vertexes.iterator().hasNext()) {
			throw new ContextNotFoundException("Error retrieving context with name " + fullName);
		}
		
		Iterator<Vertex> iterator = vertexes.iterator();
		Vertex context = iterator.next();
		
		logger.trace("Context Representing Vertex : {}", Utility.toJsonString(context, true));
		
		if(iterator.hasNext()) {
			throw new ContextNotFoundException("Found more than one context with name " + name
					+ "but required the one with path" + fullName + ". Please Reimplement the query");
		}
		
		return context;
	}
	
}
