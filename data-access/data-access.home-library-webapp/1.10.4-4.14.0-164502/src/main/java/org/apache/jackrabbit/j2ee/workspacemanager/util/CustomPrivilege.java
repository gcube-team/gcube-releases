package org.apache.jackrabbit.j2ee.workspacemanager.util;

import javax.jcr.security.Privilege;

public interface CustomPrivilege extends Privilege{

	public static final String NO_LIMIT 		= "hl:noOwnershipLimit";
	public static final String REMOVE_ROOT 		= "hl:removeSharedRoot";
	public static final String WRITE_ALL 		= "hl:writeAll";
	public static final String READ 			= "jcr:read";
	public static final String WRITE 			= "jcr:write";	
	public static final String ADMINISTRATOR 	= "jcr:all";
	
}
