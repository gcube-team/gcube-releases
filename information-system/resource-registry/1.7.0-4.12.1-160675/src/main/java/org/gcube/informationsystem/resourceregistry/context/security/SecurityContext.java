/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.context.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.gcube.informationsystem.resourceregistry.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole.ALLOW_MODES;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SecurityContext {
	
	private static Logger logger = LoggerFactory.getLogger(SecurityContext.class);
	
	protected static final String DEFAULT_WRITER_ROLE = "writer";
	protected static final String DEFAULT_READER_ROLE = "reader";
	
	public static final String H = "H";
	
	protected final boolean hierarchic;
	
	public enum SecurityType {
		ROLE("Role"), USER("User");
		
		private final String name;
		
		private SecurityType(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public enum PermissionMode {
		READER("Reader"), WRITER("Writer");
		
		private final String name;
		
		private PermissionMode(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	protected final UUID context;
	
	protected final Map<Boolean,Map<PermissionMode,OrientGraphFactory>> factoryMap;
	
	protected SecurityContext parentSecurityContext;
	
	protected Set<SecurityContext> children;
	
	protected boolean isHierarchicMode() {
		return hierarchic && ContextUtility.getHierarchicMode().get();
	}
	
	public void setParentSecurityContext(SecurityContext parentSecurityContext) {
		if(this.parentSecurityContext!=null) {
			this.parentSecurityContext.getChildren().remove(this);
		}
		
		this.parentSecurityContext = parentSecurityContext;
		if(parentSecurityContext!=null) {
			this.parentSecurityContext.addChild(this);
		}
	}
	
	public SecurityContext getParentSecurityContext() {
		return parentSecurityContext;
	}
	
	private void addChild(SecurityContext child) {
		this.children.add(child);
	}
	
	public Set<SecurityContext> getChildren(){
		return this.children;
	}
	
	protected OrientGraph getAdminOrientGraph() throws ResourceRegistryException {
		return ContextUtility.getAdminSecurityContext().getGraph(PermissionMode.WRITER);
	}
	
	/**
	 * @return a set containing all children and recursively
	 * all children.  
	 */
	private Set<SecurityContext> getAllChildren(){
		Set<SecurityContext> allChildren = new HashSet<>();
		allChildren.add(this);
		for(SecurityContext securityContext : getChildren()) {
			allChildren.addAll(securityContext.getAllChildren());
		}
		return allChildren;
	}
	
	/**
	 * @return 
	 */
	private Set<SecurityContext> getAllParents(){
		Set<SecurityContext> allParents = new HashSet<>();
		SecurityContext parent = getParentSecurityContext();
		while(parent!=null) {
			allParents.add(parent);
			parent = parent.getParentSecurityContext();
		}
		return allParents;
	}
	
	
	/**
	 * Use to change the parent not to set the first time
	 * 
	 * @param newParentSecurityContext
	 * @param orientGraph
	 * @throws ResourceRegistryException 
	 */
	public void changeParentSecurityContext(SecurityContext newParentSecurityContext, OrientGraph orientGraph) throws ResourceRegistryException {
		if(!hierarchic) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("Cannot change parent ");
			errorMessage.append(SecurityContext.class.getSimpleName());
			errorMessage.append(" to non hierarchic ");
			errorMessage.append(SecurityContext.class.getSimpleName());
			errorMessage.append(". ");
			errorMessage.append(Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE);
			final String error = errorMessage.toString();
			logger.error(error);
			throw new RuntimeException(error);
		}
		
		OSecurity oSecurity = getOSecurity(orientGraph);
		
		Set<SecurityContext> allChildren = getAllChildren();
		
		Set<SecurityContext> oldParents = getAllParents();
		
		Set<SecurityContext> newParents = new HashSet<>();
		if(newParentSecurityContext!=null) {
			newParents = newParentSecurityContext.getAllParents();
		}
		
		/* 
		 * From old parents I remove the new parents so that oldParents
		 * contains only the parents where I have to remove all 
		 * HReaderRole-UUID e HWriterRole-UUID of allChildren by using 
		 * removeHierarchicRoleFromParent() function
		 * 
		 */
		oldParents.removeAll(newParents);
		removeChildrenHRolesFromParents(oSecurity, oldParents, allChildren);
		
		setParentSecurityContext(newParentSecurityContext);
		
		if(newParentSecurityContext!=null){
			for(PermissionMode permissionMode : PermissionMode.values()) {
				for(SecurityContext child : allChildren) {
					String roleName = child.getSecurityRoleOrUserName(permissionMode, SecurityType.ROLE, true);
					ORole role = oSecurity.getRole(roleName);
					getParentSecurityContext().addHierarchicRoleToParent(oSecurity, permissionMode, role);
				}
			}
		}
		
	}
	
	protected SecurityContext(UUID context, boolean hierarchic) throws ResourceRegistryException {
		this.context = context;
		this.factoryMap = new HashMap<>();
		this.hierarchic = hierarchic;
		this.children = new HashSet<>(); 
	}
	
	public SecurityContext(UUID context) throws ResourceRegistryException {
		this(context, true);
	}
	
	private synchronized OrientGraphFactory getFactory(PermissionMode permissionMode, boolean recreate) {
		OrientGraphFactory factory = null;
		
		Boolean h = hierarchic && isHierarchicMode();
		
		Map<PermissionMode,OrientGraphFactory> factories = factoryMap.get(h);
		if(factories == null) {
			factories = new HashMap<>();
		} else {
			if(recreate) {
				factories.remove(permissionMode);
			}
		}
		
		factory = factories.get(permissionMode);
		
		if(factory == null) {
			
			String username = getSecurityRoleOrUserName(permissionMode, SecurityType.USER, h);
			String password = DatabaseEnvironment.DEFAULT_PASSWORDS.get(permissionMode);
			
			factory = new OrientGraphFactory(DatabaseEnvironment.DB_URI, username, password).setupPool(1, 10);
			factory.setConnectionStrategy(DatabaseEnvironment.CONNECTION_STRATEGY_PARAMETER.toString());
			
			factories.put(permissionMode, factory);
		}
		
		return factory;
	}
	
	public UUID getUUID() {
		return context;
	}
	
	public String getSecurityRoleOrUserName(PermissionMode permissionMode, SecurityType securityType,
			boolean hierarchic) {
		StringBuilder stringBuilder = new StringBuilder();
		if(hierarchic) {
			stringBuilder.append(H);
		}
		stringBuilder.append(permissionMode);
		stringBuilder.append(securityType);
		stringBuilder.append("_");
		stringBuilder.append(context.toString());
		return stringBuilder.toString();
	}
	
	private OSecurity getOSecurity(OrientGraph orientGraph) {
		ODatabaseDocumentTx oDatabaseDocumentTx = orientGraph.getRawGraph();
		return oDatabaseDocumentTx.getMetadata().getSecurity();
	}
	
	public void addElement(Element element) throws ResourceRegistryException {
		addElement(element, getAdminOrientGraph());
	}
	
	protected void allow(OSecurity oSecurity, ODocument oDocument, boolean hierarchic) {
		String writerRoleName = getSecurityRoleOrUserName(PermissionMode.WRITER, SecurityType.ROLE, hierarchic);
		oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_ALL, writerRoleName);
		String readerRoleName = getSecurityRoleOrUserName(PermissionMode.READER, SecurityType.ROLE, hierarchic);
		oSecurity.allowRole(oDocument, ORestrictedOperation.ALLOW_READ, readerRoleName);
	}
	
	public void addElement(Element element, OrientGraph orientGraph) {
		OrientElement orientElement = (OrientElement) element;
		ODocument oDocument = orientElement.getRecord();
		OSecurity oSecurity = getOSecurity(orientGraph);
		allow(oSecurity, oDocument, false);
		if(hierarchic) {
			allow(oSecurity, oDocument, true);
		}
		oDocument.save();
		orientElement.save();
	}
	
	public void removeElement(Element element) throws ResourceRegistryException {
		removeElement(element, getAdminOrientGraph());
	}
	
	protected void deny(OSecurity oSecurity, ODocument oDocument, boolean hierarchic) {
		// The element could be created in such a context so the writerUser for the
		// context is allowed by default because it was the creator
		String writerUserName = getSecurityRoleOrUserName(PermissionMode.WRITER, SecurityType.USER, hierarchic);
		oSecurity.denyUser(oDocument, ORestrictedOperation.ALLOW_ALL, writerUserName);
		String readerUserName = getSecurityRoleOrUserName(PermissionMode.WRITER, SecurityType.USER, hierarchic);
		oSecurity.denyUser(oDocument, ORestrictedOperation.ALLOW_READ, readerUserName);
		
		String writerRoleName = getSecurityRoleOrUserName(PermissionMode.WRITER, SecurityType.ROLE, hierarchic);
		oSecurity.denyRole(oDocument, ORestrictedOperation.ALLOW_ALL, writerRoleName);
		String readerRoleName = getSecurityRoleOrUserName(PermissionMode.READER, SecurityType.ROLE, hierarchic);
		oSecurity.denyRole(oDocument, ORestrictedOperation.ALLOW_READ, readerRoleName);
		
	}
	
	public void removeElement(Element element, OrientGraph orientGraph) {
		OrientElement orientElement = (OrientElement) element;
		ODocument oDocument = orientElement.getRecord();
		OSecurity oSecurity = getOSecurity(orientGraph);
		deny(oSecurity, oDocument, false);
		if(hierarchic) {
			deny(oSecurity, oDocument, true);
		}
		oDocument.save();
		orientElement.save();
	}
	
	protected boolean allowed(final ORole role, final ODocument oDocument) {
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				ContextUtility.getHierarchicMode().set(false);
				OrientGraphNoTx orientGraphNoTx = getGraphNoTx(PermissionMode.READER);
				try {
					OrientElement element = orientGraphNoTx.getElement(oDocument.getIdentity());
					if(element == null) {
						return false;
					}
					return true;
				} catch(Exception e) {
					return false;
				} finally {
					orientGraphNoTx.shutdown();
				}
			}
			
		};
		
		Future<Boolean> result = executor.submit(callable);
		try {
			return result.get();
		} catch(Exception e) {
			return false;
		}
	}
	
	public void create() throws ResourceRegistryException {
		OrientGraph orientGraph = getAdminOrientGraph();
		create(orientGraph);
		orientGraph.commit();
		orientGraph.shutdown();
	}
	
	protected ORole addExtraRules(ORole role, PermissionMode permissionMode) {
		return role;
	}
	
	protected ORole getSuperRole(OSecurity oSecurity, PermissionMode permissionMode) {
		String superRoleName = permissionMode.name().toLowerCase();
		return oSecurity.getRole(superRoleName);
	}
	
	protected void addHierarchicRoleToParent(OSecurity oSecurity, PermissionMode permissionMode, ORole role) {
		String userName = getSecurityRoleOrUserName(permissionMode, SecurityType.USER, true);
		OUser user = oSecurity.getUser(userName);
		user.addRole(role);
		user.save();
		
		if(getParentSecurityContext() != null) {
			getParentSecurityContext().addHierarchicRoleToParent(oSecurity, permissionMode, role);
		}
	}
	
	protected void createRolesAndUsers(OSecurity oSecurity) {
		boolean[] booleanArray;
		if(hierarchic) {
			booleanArray = new boolean[] {false, true};
		} else {
			booleanArray = new boolean[] {false};
		}
		
		for(boolean hierarchic : booleanArray) {
			for(PermissionMode permissionMode : PermissionMode.values()) {
				ORole superRole = getSuperRole(oSecurity, permissionMode);
				
				String roleName = getSecurityRoleOrUserName(permissionMode, SecurityType.ROLE, hierarchic);
				ORole role = oSecurity.createRole(roleName, superRole, ALLOW_MODES.DENY_ALL_BUT);
				addExtraRules(role, permissionMode);
				role.save();
				logger.trace("{} created", role);
				
				if(hierarchic && getParentSecurityContext() != null) {
					getParentSecurityContext().addHierarchicRoleToParent(oSecurity, permissionMode, role);
				}
				
				String userName = getSecurityRoleOrUserName(permissionMode, SecurityType.USER, hierarchic);
				OUser user = oSecurity.createUser(userName, DatabaseEnvironment.DEFAULT_PASSWORDS.get(permissionMode),
						role);
				user.save();
				logger.trace("{} created", user);
			}
		}
		
	}
	
	public void create(OrientGraph orientGraph) {
		OSecurity oSecurity = getOSecurity(orientGraph);
		
		createRolesAndUsers(oSecurity);
		
		logger.trace("Security Context (roles and users) with UUID {} successfully created", context.toString());
	}
	
	private void drop(OSecurity oSecurity, String name, SecurityType securityType) {
		boolean dropped = false;
		switch(securityType) {
			case ROLE:
				dropped = oSecurity.dropRole(name);
				break;
			
			case USER:
				dropped = oSecurity.dropUser(name);
				break;
			
			default:
				break;
		}
		if(dropped) {
			logger.trace("{} successfully dropped", name);
		} else {
			logger.error("{} was not dropped successfully", name);
		}
	}
	
	public void delete() throws ResourceRegistryException {
		OrientGraph orientGraph = getAdminOrientGraph();
		delete(orientGraph);
		orientGraph.commit();
		orientGraph.shutdown();
	}
	
	protected void removeChildrenHRolesFromParents(OSecurity oSecurity) {
		Set<SecurityContext> parents = getAllParents();
		Set<SecurityContext> allChildren = getAllChildren();
		removeChildrenHRolesFromParents(oSecurity, parents, allChildren);
	}
	
	protected void removeChildrenHRolesFromParents(OSecurity oSecurity, Set<SecurityContext> parents, Set<SecurityContext> children) {
		for(SecurityContext parent : parents) {
			parent.removeChildrenHRolesFromMyHUsers(oSecurity, children);
		}
	}
	
	protected void removeChildrenHRolesFromMyHUsers(OSecurity oSecurity, Set<SecurityContext> children) {
		for(PermissionMode permissionMode : PermissionMode.values()) {
			String userName = getSecurityRoleOrUserName(permissionMode, SecurityType.USER, true);
			OUser user = oSecurity.getUser(userName);
			for(SecurityContext child : children) {
				String roleName = child.getSecurityRoleOrUserName(permissionMode, SecurityType.ROLE, true);
				logger.debug("Going to remove {} from {}", roleName, userName);
				boolean removed = user.removeRole(roleName);
				logger.trace("{} {} removed from {}", roleName, removed ? "successfully" : "NOT", userName);
			}
			user.save();
		}
		
	}
	
	protected void removeHierarchicRoleFromMyHUser(OSecurity oSecurity, PermissionMode permissionMode, String roleName) {
		String userName = getSecurityRoleOrUserName(permissionMode, SecurityType.USER, true);
		OUser user = oSecurity.getUser(userName);
		logger.debug("Going to remove {} from {}", roleName, userName);
		boolean removed = user.removeRole(roleName);
		logger.trace("{} {} removed from {}", roleName, removed ? "successfully" : "NOT", userName);
		user.save();
	}
	
	protected void deleteRolesAndUsers(OSecurity oSecurity) {
		boolean[] booleanArray;
		if(hierarchic) {
			booleanArray = new boolean[] {false, true};
		} else {
			booleanArray = new boolean[] {false};
		}
		for(boolean hierarchic : booleanArray) {
			if(hierarchic) {
				removeChildrenHRolesFromParents(oSecurity);
			}
			for(PermissionMode permissionMode : PermissionMode.values()) {
				for(SecurityType securityType : SecurityType.values()) {
					String name = getSecurityRoleOrUserName(permissionMode, securityType, hierarchic);
					drop(oSecurity, name, securityType);
				}
			}
		}
	}
	
	public void delete(OrientGraph orientGraph) {
		OSecurity oSecurity = getOSecurity(orientGraph);
		
		logger.trace("Going to remove Security Context (roles and users) with UUID {}", context.toString());
		
		deleteRolesAndUsers(oSecurity);
		
		logger.trace("Security Context (roles and users) with UUID {} successfully removed", context.toString());
		
	}
	
	public OrientGraph getGraph(PermissionMode permissionMode) {
		OrientGraphFactory factory = getFactory(permissionMode, false);
		OrientGraph orientGraph = factory.getTx();
		if(orientGraph.isClosed()) {
			factory = getFactory(permissionMode, true);
			orientGraph = factory.getTx();
		}
		return orientGraph;
	}
	
	public OrientGraphNoTx getGraphNoTx(PermissionMode permissionMode) {
		OrientGraphFactory factory = getFactory(permissionMode, false);
		OrientGraphNoTx orientGraphNoTx = factory.getNoTx();
		if(orientGraphNoTx.isClosed()) {
			factory = getFactory(permissionMode, true);
			orientGraphNoTx = factory.getNoTx();
		}
		return orientGraphNoTx;
	}
	
	public ODatabaseDocumentTx getDatabaseDocumentTx(PermissionMode permissionMode) {
		OrientGraphFactory factory = getFactory(permissionMode, false);
		ODatabaseDocumentTx databaseDocumentTx = factory.getDatabase();
		if(databaseDocumentTx.isClosed()) {
			factory = getFactory(permissionMode, true);
			databaseDocumentTx = factory.getDatabase();
		}
		return databaseDocumentTx;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", Context.NAME, getUUID().toString());
	}
}
