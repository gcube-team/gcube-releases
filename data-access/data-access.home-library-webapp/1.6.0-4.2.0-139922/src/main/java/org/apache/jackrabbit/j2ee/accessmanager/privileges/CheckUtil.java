package org.apache.jackrabbit.j2ee.accessmanager.privileges;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;

import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;

public class CheckUtil {

	
	public static boolean canDeleteChildren(String absPath, Session session) throws Exception {
//		JackrabbitAccessControlManager accessControlManager = (JackrabbitAccessControlManager) session.getAccessControlManager();
//		Set<Principal> users = new HashSet<Principal>();
//		users.add("");
//		accessControlManager.hasPrivileges(absPath, users, new Privilege[] {
//				accessControlManager.privilegeFromName(CustomPrivilege.JCR_REMOVE_CHILD_NODES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT)
//		});
		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();
			return accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(CustomPrivilege.JCR_REMOVE_CHILD_NODES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT)
			});

		} catch (RepositoryException e) {
			return false;
		}
	}
	
}
