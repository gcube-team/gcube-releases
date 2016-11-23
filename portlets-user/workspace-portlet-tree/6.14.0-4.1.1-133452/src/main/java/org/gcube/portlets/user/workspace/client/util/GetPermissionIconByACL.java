/**
 * 
 */
package org.gcube.portlets.user.workspace.client.util;

import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL.USER_TYPE;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 17, 2014
 * 
 */
public class GetPermissionIconByACL {

	public static AbstractImagePrototype getImage(WorkspaceACL acl) {

		if(acl==null || acl.getId()==null)
			return null;
		
		String id = acl.getId();
		
		if (id.compareTo("ADMINISTRATOR") == 0) {
			return Resources.getIconAdministrator();
		} else if (id.compareTo("READ_ONLY") == 0) {
			return Resources.getIconReadOnly();
		} else if (id.compareTo("WRITE_OWNER") == 0) {
			return Resources.getIconWriteOwn();
		} else if (id.compareTo("WRITE_ALL") == 0) {
			return Resources.getIconWriteAll();
		}

		return null;
	}
	
	public static AbstractImagePrototype getImage(WorkspaceACL.USER_TYPE userType) {

		if(userType==null)
			return null;
		
		if (userType.equals(USER_TYPE.ADMINISTRATOR)) {
			return Resources.getIconAdministrator();
		} else if (userType.equals(USER_TYPE.GROUP)) {
			return Resources.getIconUsers();
		}

		return null;
	}

}
