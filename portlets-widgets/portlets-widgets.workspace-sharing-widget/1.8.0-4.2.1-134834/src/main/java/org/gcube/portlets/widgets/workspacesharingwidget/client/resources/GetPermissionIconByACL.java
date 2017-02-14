/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client.resources;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 25, 2014
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

}
