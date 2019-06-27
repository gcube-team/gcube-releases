package org.gcube.informationsystem.resourceregistry.context.security;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OSecurity;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AdminSecurityContext extends SecurityContext {
	
	private static Logger logger = LoggerFactory.getLogger(SecurityContext.class);
	
	public AdminSecurityContext() throws ResourceRegistryException {
		super(DatabaseEnvironment.ADMIN_SECURITY_CONTEXT_UUID, false);
	}
	
	@Override
	public void create() {
		throw new RuntimeException("Cannot use this method for Admin Context");
	}
	
	@Override
	protected ORole getSuperRole(OSecurity oSecurity, PermissionMode permissionMode) {
		return oSecurity.getRole(DatabaseEnvironment.DEFAULT_ADMIN_ROLE);
	}
	
	@Override
	protected ORole addExtraRules(ORole role, PermissionMode permissionMode) {
		logger.trace("Adding extra rules for {}", role.getName());
		switch(permissionMode) {
			case WRITER:
				role.addRule(ORule.ResourceGeneric.BYPASS_RESTRICTED, null, ORole.PERMISSION_ALL);
				break;
			
			case READER:
				role.addRule(ORule.ResourceGeneric.BYPASS_RESTRICTED, null, ORole.PERMISSION_READ);
				break;
			
			default:
				break;
		}
		return role;
	}
	
}
