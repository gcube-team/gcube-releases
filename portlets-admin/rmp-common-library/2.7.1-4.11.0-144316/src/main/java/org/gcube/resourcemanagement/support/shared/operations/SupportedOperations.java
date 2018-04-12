/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: SupportedOperations.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.operations;

import org.gcube.resourcemanagement.support.shared.types.UserGroup;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum SupportedOperations {
	GHN_SHUTDOWN(UserGroup.ADMIN),
	GHN_RESTART(UserGroup.ADMIN),
	GHN_CLEAN_RESTART(UserGroup.ADMIN),
	GHN_DELETE(UserGroup.ADMIN),
	GHN_FORCE_DELETE(UserGroup.SUPERUSER, UserGroup.ADMIN),

	GENERIC_RESOURCE_CREATE(UserGroup.ADMIN),
	GENERIC_RESOURCE_EDIT(UserGroup.ADMIN),
	GENERIC_RESOURCE_DELETE(UserGroup.ADMIN),
	GENERIC_RESOURCE_FORCE_DELETE(UserGroup.SUPERUSER, UserGroup.ADMIN),
	
	RUNTIME_RESOURCE_DELETE(UserGroup.ADMIN),
	RUNTIME_RESOURCE_FORCE_DELETE(UserGroup.SUPERUSER, UserGroup.ADMIN),

	SERVICE_CREATE(UserGroup.ADMIN),
	SERVICE_DEPLOY(UserGroup.ADMIN),
	SERVICE_GET_REPORT(UserGroup.ADMIN, UserGroup.DEBUG),
	SERVICE_GET_RESOURCE_BY_ID(UserGroup.ADMIN, UserGroup.DEBUG, UserGroup.USER),

	RUNNING_INSTANCE_UNDEPLOY(UserGroup.ADMIN),

	COLLECTION_DELETE(UserGroup.ADMIN),
	COLLECTION_FORCE_DELETE(UserGroup.SUPERUSER, UserGroup.ADMIN),

	VIEW_DELETE(UserGroup.ADMIN),
	VIEW_FORCE_DELETE(UserGroup.SUPERUSER, UserGroup.ADMIN),

	ADD_TO_SCOPE(UserGroup.ADMIN),

	SWEEP_GHN(UserGroup.ADMIN),

	CREATE_MENU_SHOW(UserGroup.ADMIN, UserGroup.DEBUG),
	INFRASTRUCTURE_UPGRADE(UserGroup.ADMIN, UserGroup.DEBUG);

	private UserGroup[] permissions = null;

	private SupportedOperations(final UserGroup...permissions) {
		this.permissions = permissions;
	}

	public UserGroup[] getPermissions() {
		return this.permissions;
	}

	/**
	 * States if a group is allowed to execute an operation.
	 * @param permission
	 * @return
	 */
	public boolean isAllowed(final UserGroup permission) {
		if (this.getPermissions() == null
				|| this.getPermissions().length == 0
				|| permission == null) {
			return false;
		}
		for (UserGroup g : this.getPermissions()) {
			if (g.equals(permission)) {
				return true;
			}
		}
		return false;
	}
}
