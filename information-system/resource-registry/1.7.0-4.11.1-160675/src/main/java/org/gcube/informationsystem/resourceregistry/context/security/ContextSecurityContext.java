package org.gcube.informationsystem.resourceregistry.context.security;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextSecurityContext extends SecurityContext {
	
	private static Logger logger = LoggerFactory.getLogger(SecurityContext.class);
	
	public ContextSecurityContext() throws ResourceRegistryException {
		super(DatabaseEnvironment.CONTEXT_SECURITY_CONTEXT_UUID, false);
	}
	
	@Override
	protected ORole addExtraRules(ORole role, PermissionMode permissionMode) {
		logger.trace("Adding extra rules for {}", role.getName());
		switch(permissionMode) {
			case WRITER:
				role.addRule(ORule.ResourceGeneric.CLUSTER, null, ORole.PERMISSION_ALL);
				role.addRule(ORule.ResourceGeneric.SYSTEM_CLUSTERS, null, ORole.PERMISSION_ALL);
				role.addRule(ORule.ResourceGeneric.CLASS, null, ORole.PERMISSION_ALL);
				break;
			
			case READER:
				role.addRule(ORule.ResourceGeneric.CLUSTER, null, ORole.PERMISSION_READ);
				role.addRule(ORule.ResourceGeneric.SYSTEM_CLUSTERS, null, ORole.PERMISSION_READ);
				role.addRule(ORule.ResourceGeneric.CLASS, null, ORole.PERMISSION_READ);
				break;
			
			default:
				break;
		}
		return role;
	}
	
}
