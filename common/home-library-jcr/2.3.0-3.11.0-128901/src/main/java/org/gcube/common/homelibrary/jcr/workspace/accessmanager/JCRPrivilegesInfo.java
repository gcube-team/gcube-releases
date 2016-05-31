package org.gcube.common.homelibrary.jcr.workspace.accessmanager;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.AccessControlUtil;

/**
 * Helper class to assist in the usage of access control from scripts.
 */
public class JCRPrivilegesInfo {

	private static final String NO_LIMIT 		= "hl:noOwnershipLimit";
	private static final String REMOVE_ROOT 	= "hl:removeSharedRoot";
	private static final String WRITE_ALL 		= "hl:writeAll";

	private static final String READ 			= "jcr:read";
	private static final String WRITE 			= "jcr:write";	
	private static final String ADMINISTRATOR 	= "jcr:all";;

	public static final String OWNER 				= "hl:owner";
	public static final String PORTAL_LOGIN  		= "hl:portalLogin";

	/**
	 * Return the supported Privileges for the specified node.
	 * 
	 * @param node the node to check
	 * @return array of Privileges
	 * @throws RepositoryException
	 */
	public Privilege [] getSupportedPrivileges(Node node) throws RepositoryException {
		return getSupportedPrivileges(node.getSession(), node.getPath());
	}

	/**
	 * Returns the supported privileges for the specified path.
	 * 
	 * @param session the session for the current user
	 * @param absPath the path to get the privileges for
	 * @return array of Privileges
	 * @throws RepositoryException
	 */
	public Privilege [] getSupportedPrivileges(Session session, String absPath) throws RepositoryException {
		AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
		Privilege[] supportedPrivileges = accessControlManager.getSupportedPrivileges(absPath);
		return supportedPrivileges;
	}

	/**
	 * Wrapper class that holds the set of Privileges that are granted 
	 * and/or denied for a specific principal.
	 */
	public static class AccessRights {
		private Set<Privilege> granted = new HashSet<Privilege>();
		private Set<Privilege> denied = new HashSet<Privilege>();

		private transient static ResourceBundle resBundle = null; 
		private ResourceBundle getResourceBundle(Locale locale) {
			if (resBundle == null || !resBundle.getLocale().equals(locale)) {
				resBundle = ResourceBundle.getBundle(getClass().getPackage().getName() + ".PrivilegesResources", locale);
			}
			return resBundle;
		}


		public Set<Privilege> getGranted() {
			return granted;
		}
		public Set<Privilege> getDenied() {
			return denied;
		}

		public String getPrivilegeSetDisplayName(Locale locale) {
			if (denied != null && !denied.isEmpty()) {
				//if there are any denied privileges, then this is a custom privilege set
				return getResourceBundle(locale).getString("privilegeset.custom");
			} else {
				if (granted.isEmpty()) {
					//appears to have an empty privilege set
					return getResourceBundle(locale).getString("privilegeset.none");
				}

				if (granted.size() == 1) {
					//check if the single privilege is jcr:all or jcr:read
					Iterator<Privilege> iterator = granted.iterator();
					Privilege next = iterator.next();
					if ("jcr:all".equals(next.getName())) {
						//full control privilege set
						return getResourceBundle(locale).getString("privilegeset.all");
					} else if ("jcr:read".equals(next.getName())) {
						//readonly privilege set
						return getResourceBundle(locale).getString("privilegeset.readonly");
					} 
				} else if (granted.size() == 2) {
					//check if the two privileges are jcr:read and jcr:write
					Iterator<Privilege> iterator = granted.iterator();
					Privilege next = iterator.next();
					Privilege next2 = iterator.next();
					if ( ("jcr:read".equals(next.getName()) && "jcr:write".equals(next2.getName())) ||
							("jcr:read".equals(next2.getName()) && "jcr:write".equals(next.getName())) ) {
						//read/write privileges
						return getResourceBundle(locale).getString("privilegeset.readwrite");
					}
				}

				//some other set of privileges
				return getResourceBundle(locale).getString("privilegeset.custom");
			}
		}
	}

	/**
	 * Returns the mapping of declared access rights that have been set for the resource at
	 * the given path. 
	 * 
	 * @param node the node to get the access rights for
	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
	 * @throws RepositoryException
	 */
	public Map<Principal, AccessRights> getDeclaredAccessRights(Node node) throws RepositoryException {
		Map<Principal, AccessRights> accessRights = getDeclaredAccessRights(node.getSession(), node.getPath());
		return accessRights;
	}

	/**
	 * Returns the mapping of declared access rights that have been set for the resource at
	 * the given path. 
	 * 
	 * @param session the current user session.
	 * @param absPath the path of the resource to get the access rights for
	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
	 * @throws RepositoryException
	 */
	public Map<Principal, AccessRights> getDeclaredAccessRights(Session session, String absPath) throws RepositoryException {
		Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
		AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);
		if (entries != null) {
			for (AccessControlEntry ace : entries) {
				Principal principal = ace.getPrincipal();
				AccessRights accessPrivileges = accessMap.get(principal);
				if (accessPrivileges == null) {
					accessPrivileges = new AccessRights();
					accessMap.put(principal, accessPrivileges);
				}
				boolean allow = AccessControlUtil.isAllow(ace);
				if (allow) {
					accessPrivileges.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
				} else {
					accessPrivileges.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
				}
			}
		}

		return accessMap;
	}

	private AccessControlEntry[] getDeclaredAccessControlEntries(Session session, String absPath) throws RepositoryException {
		AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
		AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
		for (AccessControlPolicy accessControlPolicy : policies) {
			if (accessControlPolicy instanceof AccessControlList) {
				AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
				return accessControlEntries;
			}
		}
		return new AccessControlEntry[0];
	}

	/**
	 * Returns the declared access rights for the specified Node for the given
	 * principalId.
	 * 
	 * @param node the JCR node to retrieve the access rights for
	 * @param principalId the principalId to get the access rights for
	 * @return access rights for the specified principal
	 * @throws RepositoryException
	 */
	public AccessRights getDeclaredAccessRightsForPrincipal(Node node, String principalId) throws RepositoryException {
		return getDeclaredAccessRightsForPrincipal(node.getSession(), node.getPath(), principalId);
	}

	/**
	 * Returns the declared access rights for the resource at the specified path for the given
	 * principalId.
	 * 
	 * @param session the current JCR session
	 * @param absPath the path of the resource to retrieve the rights for
	 * @param principalId the principalId to get the access rights for
	 * @return access rights for the specified principal
	 * @throws RepositoryException
	 */
	public AccessRights getDeclaredAccessRightsForPrincipal(Session session, String absPath, String principalId) throws RepositoryException {
		AccessRights rights = new AccessRights();
		if (principalId != null && principalId.length() > 0) {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
			AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
			for (AccessControlPolicy accessControlPolicy : policies) {
				if (accessControlPolicy instanceof AccessControlList) {
					AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
					for (AccessControlEntry ace : accessControlEntries) {
						if (principalId.equals(ace.getPrincipal().getName())) {
							boolean isAllow = AccessControlUtil.isAllow(ace);
							if (isAllow) {
								rights.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
							} else {
								rights.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
							}
						}
					}
				}
			}
		}

		return rights;
	}




	/**
	 * Returns the mapping of effective access rights that have been set for the resource at
	 * the given path. 
	 * 
	 * @param node the node to get the access rights for
	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
	 * @throws RepositoryException
	 */
	public Map<Principal, AccessRights> getEffectiveAccessRights(Node node) throws RepositoryException {
		Map<Principal, AccessRights> accessRights = getEffectiveAccessRights(node.getSession(), node.getPath());
		return accessRights;
	}

	/**
	 * Returns the mapping of effective access rights that have been set for the resource at
	 * the given path. 
	 * 
	 * @param session the current user session.
	 * @param absPath the path of the resource to get the access rights for
	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
	 * @throws RepositoryException
	 */
	public Map<Principal, AccessRights> getEffectiveAccessRights(Session session, String absPath) throws RepositoryException {
		Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
		AccessControlEntry[] entries = getEffectiveAccessControlEntries(session, absPath);
		if (entries != null) {
			for (AccessControlEntry ace : entries) {
				Principal principal = ace.getPrincipal();
				AccessRights accessPrivleges = accessMap.get(principal);
				if (accessPrivleges == null) {
					accessPrivleges = new AccessRights();
					accessMap.put(principal, accessPrivleges);
				}
				boolean allow = AccessControlUtil.isAllow(ace);
				if (allow) {
					accessPrivleges.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
				} else {
					accessPrivleges.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
				}
			}
		}

		return accessMap;
	}

	private AccessControlEntry[] getEffectiveAccessControlEntries(Session session, String absPath) throws RepositoryException {
		AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
		AccessControlPolicy[] policies = accessControlManager.getEffectivePolicies(absPath);
		for (AccessControlPolicy accessControlPolicy : policies) {
			if (accessControlPolicy instanceof AccessControlList) {
				AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
				return accessControlEntries;
			}
		}
		return new AccessControlEntry[0];
	}

	/**
	 * Returns the effective access rights for the specified Node for the given
	 * principalId.
	 * 
	 * @param node the JCR node to retrieve the access rights for
	 * @param principalId the principalId to get the access rights for
	 * @return access rights for the specified principal
	 * @throws RepositoryException
	 */
	public AccessRights getEffectiveAccessRightsForPrincipal(Node node, String principalId) throws RepositoryException {
		return getEffectiveAccessRightsForPrincipal(node.getSession(), node.getPath(), principalId);
	}

	/**
	 * Returns the effective access rights for the resource at the specified path for the given
	 * principalId.
	 * 
	 * @param session the current JCR session
	 * @param absPath the path of the resource to retrieve the rights for
	 * @param principalId the principalId to get the access rights for
	 * @return access rights for the specified principal
	 * @throws RepositoryException
	 */
	public AccessRights getEffectiveAccessRightsForPrincipal(Session session, String absPath, String principalId) throws RepositoryException {
		AccessRights rights = new AccessRights();
		if (principalId != null && principalId.length() > 0) {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
			AccessControlPolicy[] policies = accessControlManager.getEffectivePolicies(absPath);
			for (AccessControlPolicy accessControlPolicy : policies) {
				if (accessControlPolicy instanceof AccessControlList) {
					AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
					for (AccessControlEntry ace : accessControlEntries) {
						if (principalId.equals(ace.getPrincipal().getName())) {
							boolean isAllow = AccessControlUtil.isAllow(ace);
							if (isAllow) {
								rights.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
							} else {
								rights.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
							}
						}
					}
				}
			}
		}

		return rights;
	}

	/**
	 * Checks whether the current user has been granted privileges
	 * to add children to the specified path.
	 * 
	 * @param owner
	 * @param currentUser
	 * @param absPath
	 * @return true if the current user has the privileges, false otherwise
	 * @throws InternalErrorException
	 */
	public static boolean canAddChildren(String owner, String currentUser, String absPath) throws InternalErrorException {
		Session session = JCRRepository.getSession(currentUser);

		//		logger.debug ("-> canAddChildren? - currentUser: " + currentUser + " - on node: " + absPath + " - owner: " + owner);

		if (owner.equals(currentUser)){
			//			System.out.println("yes");
			return true;
		}

		try {
			//			System.out.println("getPrincipal " + (session.getUserID() + "-  abspath " + absPath ));

			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
			return accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES)
			});
		} catch (RepositoryException e) {
			return false;
		}finally{
			session.logout();
		}
	}



	/**
	 * Checks whether the current user has been granted privileges
	 * to delete children of the specified path.
	 * 
	 * @param user
	 * @param absPath
	 * @return true if the user can delete children
	 * @throws InternalErrorException
	 */
	public static boolean canDeleteChildren(String user, String absPath) throws InternalErrorException {
		Session session = JCRRepository.getSession(user);
		try {
			return (canDeleteChildren(session, absPath));
		}finally{
			session.logout();
		}
	}


	/**
	 * Checks whether the current user has been granted privileges
	 * to delete children of the specified path.
	 * 
	 * @param session
	 * @param absPath
	 * @return true if the user can delete children
	 */
	private static boolean canDeleteChildren(Session session, String absPath) {
		try {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
			return accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_CHILD_NODES), accessControlManager.privilegeFromName(NO_LIMIT)
			});

		} catch (RepositoryException e) {
			return false;
		}

	}

	/**
	 * Checks whether the current user has been granted privileges
	 * to delete the specified path.
	 * 
	 * @param owner
	 * @param currentUser
	 * @param absPath
	 * @param root
	 * @return true if the current user can delete the node specified by the absPath
	 * @throws InternalErrorException
	 */
	public static boolean canDelete(String owner, String currentUser, String absPath, boolean root) throws InternalErrorException {

		//		System.out.println("-> canDelete? - currentUser: " + currentUser + " - on node: " + absPath + " - owner: " + owner);

		if (owner.equals(currentUser)){
			//			System.out.println("yes");
			return true;
		}

		Session session = JCRRepository.getSession(currentUser);

		//		System.out.println("session.getUserID(): " + session.getUserID());

		try {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
			String parentPath;
			int lastSlash = absPath.lastIndexOf('/');
			if (lastSlash == 0) {
				//the parent is the root folder.
				parentPath = "/";
			} else {
				//strip the last segment
				parentPath = absPath.substring(0, lastSlash);
			}

			try{

				accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(NO_LIMIT), accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_NODE)
				});

			}catch (Exception e) {
				throw new InternalErrorException("Error retrieving privilege: " + e);
			}

			boolean canDelete = false;
			if (root)
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_NODE), accessControlManager.privilegeFromName(NO_LIMIT), accessControlManager.privilegeFromName(REMOVE_ROOT)
				});
			//				}) && canDeleteChildren(session, parentPath);
			else{
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(Privilege.JCR_REMOVE_NODE), accessControlManager.privilegeFromName(NO_LIMIT)
				}) && canDeleteChildren(session, parentPath);	


			}
			//			System.out.println("canDelete? " + canDelete);
			//			System.out.println("canDeleteChildren? " + canDeleteChildren(session, parentPath));
			return canDelete;
		} catch (RepositoryException e) {
			return false;
		}finally{
			session.logout();
		}
	}


	/**
	 * 
	 * Checks whether the current user has been granted privileges
	 * to modify properties of the specified path.
	 * 
	 * @param owner
	 * @param currentUser
	 * @param absPath
	 * @param root
	 * @return true if the current user can modify properties of node specified by absPath
	 * @throws InternalErrorException
	 */
	public static boolean canModifyProperties(String owner, String currentUser, String absPath, boolean root) throws InternalErrorException {
		if (owner.equals(currentUser))
			return true;
		Session session = JCRRepository.getSession(currentUser);
		try {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);

			boolean canDelete = false;

			if (root)
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), accessControlManager.privilegeFromName(NO_LIMIT), accessControlManager.privilegeFromName(REMOVE_ROOT)
				});
			else
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES), accessControlManager.privilegeFromName(NO_LIMIT)
				});

			return canDelete;

		} catch (RepositoryException e) {
			return false;
		}finally{
			session.logout();
		}
	}


	/**
	 *  Get ACL by username
	 * @param user
	 * @param absPath
	 * @return an ACLType privilege
	 * @throws InternalErrorException
	 */
	public static ACLType getACLByUser(String user, String absPath) throws InternalErrorException {
		Session session = JCRRepository.getSession(user);
		try {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
	
			Node node = session.getNode(absPath);
			String owner = getOwner(node);
			if (owner.equals(user)){
				return ACLType.ADMINISTRATOR;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(ADMINISTRATOR)
			})){
				return ACLType.ADMINISTRATOR;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(WRITE_ALL)
			})){
				return ACLType.WRITE_ALL;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(WRITE)
			})){
				return ACLType.WRITE_OWNER;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(READ)
			})){
				return ACLType.READ_ONLY;
			}
		} catch (RepositoryException e) {
			throw new InternalErrorException("ACLType Unknown " + e);
		}
		return null;

	}

	private static String getOwner(Node node) throws PathNotFoundException, RepositoryException {
		String portalLogin;
		try{
			portalLogin = node.getProperty(PORTAL_LOGIN).getString();
		}catch (Exception e) {
			Node nodeOwner = node.getNode(OWNER);		
			portalLogin = nodeOwner.getProperty(PORTAL_LOGIN).getString();
		}
		return portalLogin;

	}

	public static boolean canReadNode(String owner, String currentUser,
			String absPath) throws InternalErrorException {
		if (owner.equals(currentUser))
			return true;
		Session session = JCRRepository.getSession(currentUser);
		try {
			AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);

			boolean canRead = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(Privilege.JCR_READ)
				});

			return canRead;

		} catch (RepositoryException e) {
			return false;
		}finally{
			session.logout();
		}
	}



}
