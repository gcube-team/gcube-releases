package org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;
import org.gcube.common.homelibary.model.acl.AccessRights;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class JCRAccessControlManager {

	public static final String WRITE_ALL 		= "hl:writeAll";
	public static final String ADMINISTRATOR 	= "jcr:all";;
	public static final String READ_ONLY 		= "jcr:read";
	public static final String WRITE_OWNER 		= "jcr:write";
	
//	public static final String READ 			= "jcr:read";
//	public static final String WRITE 			= "jcr:write";	

	private Session session;
	private String login;

	public JCRAccessControlManager(Session session, String login) {
		this.session = session;
		this.login = login;
	}

	/**
	 * Get ACL 
	 * @param absPath
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<String>> getACL(String absPath) throws Exception {
//		System.out.println("get acl " + absPath);
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Map<String, AccessRights> acl = AccessControlUtil.getACL(absPath, session);
		Set<String> keys = acl.keySet();
		for (String principal: keys){
			if (!acl.get(principal).getGranted().isEmpty())
				map.put(principal, acl.get(principal).getGranted());				
		}
		return map;
	}


//	/**
//	 *  Get ACL by username
//	 * @param user
//	 * @param absPath
//	 * @return an ACLType privilege
//	 * @throws InternalErrorException
//	 */
//	public String getACLByUser(String absPath) throws Exception {
//
////		System.out.println("login " + login + " - " + absPath);
//		String owner = null;
//		try {
//		
//			AccessControlManager accessControlManager = session.getAccessControlManager();
//
//			Node node = session.getNode(absPath);
//			owner = getOwner(node);
//			
//			
//				if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
//					accessControlManager.privilegeFromName(ADMINISTRATOR)
//			})){
//				return ADMINISTRATOR;
//			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
//					accessControlManager.privilegeFromName(WRITE_ALL)
//			})){
//				return WRITE_ALL;
//			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
//					accessControlManager.privilegeFromName(WRITE_OWNER)
//			})){
//				return WRITE_OWNER;
//			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
//					accessControlManager.privilegeFromName(READ_ONLY)
//			})){
//				return READ_ONLY;
//			}
//		} catch (RepositoryException e) {
//			throw new Exception("ACLType Unknown " + e);
//		}
//		
////		Node node = session.getNode(absPath);
////		String owner = getOwner(node);
////
//		if (owner.equals(login))
//			return ADMINISTRATOR;
//		return READ_ONLY;
//
//
//	}

	/**
	 * Get owner
	 * @param node
	 * @return
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 */
	private String getOwner(Node node) throws PathNotFoundException, RepositoryException {
		String portalLogin;
		try{
			portalLogin = node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}catch (Exception e) {
			Node nodeOwner = node.getNode(NodeProperty.OWNER.toString());		
			portalLogin = nodeOwner.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}
		return portalLogin;

	}

	/**
	 * Get denied Map
	 * @param absPath
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<String>> getDeniedMap(String absPath) throws Exception {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Map<String, AccessRights> acl = AccessControlUtil.getACL(absPath, session);
		Set<String> keys = acl.keySet();
		for (String principal: keys){
			if (!acl.get(principal).getDenied().isEmpty())
				map.put(principal, acl.get(principal).getDenied());				
		}
		return map;
	}

	/**
	 * Get EACL map
	 * @param absPath
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<String>> getEACL(String absPath) throws Exception {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Map<String, AccessRights> acl = AccessControlUtil.getEACL(absPath, session);
		Set<String> keys = acl.keySet();
		for (String principal: keys){
			if (!acl.get(principal).getGranted().isEmpty()){
				map.put(principal, acl.get(principal).getGranted());	
			}
		}
		return map;
	}


	/**
	 * Can modify properties
	 * @param login
	 * @param absPath
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public boolean canModifyProperties(String login, String absPath, Boolean root) throws Exception {

		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();

			boolean canDelete = false;

			if (root)
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(CustomPrivilege.JCR_MODIFY_PROPERTIES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT), accessControlManager.privilegeFromName(CustomPrivilege.REMOVE_ROOT)
				});
			else
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(CustomPrivilege.JCR_MODIFY_PROPERTIES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT)
				});

			return canDelete;

		} catch (RepositoryException e) {
			return false;
		}
	}


	/**
	 * Can add children
	 * @param absPath
	 * @return
	 * @throws Exception
	 */
	public boolean canAddChildren(String absPath) throws Exception {

		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();
			return accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(CustomPrivilege.JCR_ADD_CHILD_NODES)
			});
		} catch (RepositoryException e) {
			return false;
		}
	}

	/**
	 * Can delete node
	 * @param login
	 * @param absPath
	 * @param isRoot
	 * @return
	 * @throws Exception
	 */
	public boolean canDelete(String login, String absPath, boolean isRoot) throws Exception {

		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();
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
						accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT), accessControlManager.privilegeFromName(CustomPrivilege.JCR_REMOVE_NODE)
				});

			}catch (Exception e) {
				throw new Exception("Error retrieving privilege: " + e);
			}

			boolean canDelete = false;
			if (isRoot)
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(CustomPrivilege.JCR_REMOVE_NODE), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT), accessControlManager.privilegeFromName(CustomPrivilege.REMOVE_ROOT)
				});
			//				}) && canDeleteChildren(session, parentPath);
			else{

				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(CustomPrivilege.JCR_REMOVE_NODE), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT)
				}) && canDeleteChildren(parentPath);


			}
			return canDelete;
		} catch (RepositoryException e) {
			return false;
		}
	}

	/**
	 * Can Delete Children
	 * @param absPath
	 * @return
	 */
	public Boolean canDeleteChildren(String absPath) {
		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();
			return accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(CustomPrivilege.JCR_REMOVE_CHILD_NODES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT)
			});

		} catch (RepositoryException e) {
			return false;
		}

	}

	/**
	 * Can Read Node
	 * @param absPath
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public boolean canReadNode(String absPath) throws Exception {

		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();

			boolean canRead = accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(CustomPrivilege.JCR_READ)
			});
			return canRead;
		} catch (RepositoryException e) {
			return false;
		}
	}


}
