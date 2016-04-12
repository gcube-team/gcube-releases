package org.gcube.common.homelibrary.jcr.workspace.accessmanager;

import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.AccessControlUtil;
import org.gcube.common.homelibrary.home.workspace.accessmanager.AccessManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo.AccessRights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRAccessManager implements AccessManager{

	private Logger logger = LoggerFactory.getLogger(JCRAccessManager.class);

	public static final String JUST_OWNER 				= "hl:justOwner";

	public static String url;

	public JCRAccessManager(){
		super();
		url = JCRRepository.url;
	}



	public Map<String, List<String>> getACL(String absPath) throws InternalErrorException {

		Session session = JCRRepository.getSession();

		List<AccessControlEntry> allEntries = null;
		try {
			allEntries = new ArrayList<AccessControlEntry>(); 

			AccessControlManager accessControlManager = session.getAccessControlManager();
			AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
			for (AccessControlPolicy accessControlPolicy : policies) {
				if (accessControlPolicy instanceof AccessControlList) {
					AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
					for (AccessControlEntry accessControlEntry : accessControlEntries) {
						allEntries.add(accessControlEntry);
					}
				}
			}
		} catch (RepositoryException e) {
			logger.error("Error getting ACL in AccessManager for node: " +  absPath, e);
			throw new InternalErrorException(e);
		}


		Map<String, List<String>> map = getMap(allEntries);


		return map;

	}


	public boolean modifyAce(List<String> users, String absPath,
			List<String> privilegesList, String order) throws InternalErrorException {

		Boolean modified = true;
		try{
			deleteAces(absPath, users);
		}catch (Exception e) {
			logger.error("Error deleting old ACLs: " + e);
			throw new InternalErrorException(e);
		}

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			StringBuilder privileges= new StringBuilder();
			for (String privilege: privilegesList){	
				privileges.append("&privilege@" + privilege);
			}

			if(order == null)
				order = "first";

			for (String user: users){
				try{
					String requestUrl = url + "/ModifyAceServlet?" + JCRRepository.getCredentials() + "&principalId=" + user +  "&resourcePath=" + URLEncoder.encode(absPath, "UTF-8") + privileges.toString() +"&order=" + order;
					logger.debug(requestUrl);
					getMethod =  new GetMethod(requestUrl);
					httpClient.executeMethod(getMethod);
					logger.debug("Response " + getMethod.getResponseBodyAsString());

					if(getMethod != null)
						getMethod.releaseConnection();

				}catch (Exception e) {
					logger.error("Error modifing ACLs: " + e);
					throw new InternalErrorException(e);
				}
			}

			XStream xstream = new XStream();

			try{
				modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());
			}catch (Exception e) {
				modified = false;
				logger.error("Error in Modify ace", e);
			}
			//
			//			if (modified)
			//				logger.debug(absPath + " ACL modified");
			//			else
			//				System.out.println(absPath + " ACL has not been modified");

		} catch (Exception e) {
			logger.error("Error in Add or Modify Permissions in AccessManager", e);
			modified = false;
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modified;
	}

	@Override
	public Map<String, List<String>> getEACL(String absPath) throws InternalErrorException {

		//		System.out.println("********* " + absPath);
		Session session = JCRRepository.getSession();

		List<AccessControlEntry> allEntries = null;
		try {
			allEntries = new ArrayList<AccessControlEntry>();	
			AccessControlManager accessControlManager = session.getAccessControlManager();
			AccessControlPolicy[] policies = accessControlManager.getEffectivePolicies(absPath);

			for (AccessControlPolicy accessControlPolicy : policies) {
				if (accessControlPolicy instanceof AccessControlList) {
					AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
					for (AccessControlEntry accessControlEntry : accessControlEntries) {
						allEntries.add(accessControlEntry);
					}
				}
			}
		} catch (RepositoryException e) {
			logger.error("Error getting Effective ACL for node: " + absPath,e);
			throw new InternalErrorException(e);
		}

		//		System.out.println("get Effetctive ACL for path " + absPath);

		Map<String, List<String>> map = getMap(allEntries);

		//print
		logger.debug("ACL map on "+ absPath +" : " + map.toString());

		return map;
	}


	private Map<String, List<String>> getMap(List<AccessControlEntry> allEntries) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		//		System.out.println("entry size " + allEntries.size());
		for (AccessControlEntry entry: allEntries){	

			List<String> privilegesList = null;

			String key = entry.getPrincipal().getName();

			if (!key.equals("everyone")){
				try{			
					privilegesList = map.get(key);
					//					System.out.println("privilegesList size " + privilegesList.size() );
				}catch (Exception e) {
					//					System.out.println("key: "+ key + ", does not exist yet");
				}

				Privilege[] privileges = entry.getPrivileges();
				for (int i=0; i< privileges.length; i++){
					//					System.out.println("* "+ i + ")" + privileges[i].getName());
					if (privilegesList==null)
						privilegesList = new ArrayList<String>();
					privilegesList.add(privileges[i].getName());

				}
				map.put(entry.getPrincipal().getName(), privilegesList);	
			}
		}    			
		return map;
	}


	@Override
	public boolean setReadOnlyACL(List<String> users, String absPath)
			throws InternalErrorException {

		boolean flag = true;
		List<String> privileges = new ArrayList<String>();
		privileges.add("jcr:read=granted");
		try{
			flag = modifyAce(users, absPath, privileges, null);
		}catch (Exception e) {
			logger.error("Error setting WriteOwner to users " + users.toString() + " to path " + absPath);
			flag = false;
		}
		return flag;
	}


	//	/**
	//	 * If a user does not exist, create it now
	//	 * @param users
	//	 * @throws InternalErrorException
	//	 */
	//	private void checkUserList(List<String> users) throws InternalErrorException {
	//		JCRUserManager um = new JCRUserManager();
	//		for (String user: users){
	//			if (um.createUser(user))
	//				logger.trace("user " + user  + " has been created");
	//		}
	//	}



	@Override
	public boolean setWriteOwnerACL(List<String> users, String absPath)
			throws InternalErrorException {
		logger.debug("setAuthorAce - users: " + users.toString() + " - absPath: " + absPath);
		boolean flag = true;

		//			checkUserList(users);
		List<String> privileges = new ArrayList<String>();
		privileges.add("jcr:write=granted");
		try{
			flag = modifyAce(users, absPath, privileges, null);
		}catch (Exception e) {
			logger.error("Error setting WriteOwner to users " + users.toString() + " to path " + absPath);
			flag = false;
		}
		logger.debug("Ace modified");

		return flag;

	}

	@Override
	public boolean setWriteAllACL(List<String> users, String absPath)
			throws InternalErrorException {
		List<String> privileges = new ArrayList<String>();
		privileges.add("hl:writeAll=granted");
		boolean flag = true;
		try{
			//			checkUserList(users);
			flag = modifyAce(users, absPath, privileges, null);
		}catch (Exception e) {
			logger.error("Error setting Write All to users " + users.toString() + " to path " + absPath);
			flag = false;
			//			throw new InternalErrorException(e);
		}
		return flag;

	}


	/**
	 * Set access denied to user in list
	 * @param users
	 * @param absPath
	 * @return
	 * @throws InternalErrorException
	 */
	public boolean setAccessDenied(List<String> users, String absPath)
			throws InternalErrorException {

		List<String> privileges = new ArrayList<String>();
		//		privileges.add("hl:noRights=granted");
		privileges.add("jcr:read=denied");
		boolean flag = true;
		try{
			//			checkUserList(users);
			flag = modifyAce(users, absPath, privileges, null);
		}catch (Exception e) {
			logger.error("Error removing Read privilege to users " + users.toString() + " to path " + absPath);
			flag = false;
			//			throw new InternalErrorException(e);
		}
		return flag;

	}

	@Override
	public boolean setAdminACL(List<String> users, String absPath)
			throws InternalErrorException {
		List<String> privileges = new ArrayList<String>();
		privileges.add("jcr:all=granted");
		boolean flag = true;
		try{
			//			checkUserList(users);
			flag = modifyAce(users, absPath, privileges, null);
		}catch (Exception e) {
			logger.error("Error setting Admin to users " + users.toString() + " to path " + absPath);
			flag = false;
			//			throw new InternalErrorException(e);
		}
		return flag;

	}

	@Override
	public boolean deleteAces(String resourcePath, List<String> users) throws InternalErrorException {

		GetMethod getMethod = null;
		Boolean modified = true;
		try {

			HttpClient httpClient = new HttpClient();      		
			StringBuilder applyTo= new StringBuilder();
			for (String user: users)
				applyTo.append("&applyTo=" + user);

			String requestUrl = url + "/DeleteAcesServlet?" + JCRRepository.getCredentials() + "&absPath=" + URLEncoder.encode(resourcePath, "UTF-8") +  applyTo;
			logger.debug(requestUrl);
			getMethod =  new GetMethod(requestUrl);
			httpClient.executeMethod(getMethod);
			logger.debug("Response " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			logger.error("Error deleting Permissions in AccessManager", e);
			return false;
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modified;
	}

	/**
	 * Returns the mapping of declared access rights that have been set for the resource at
	 * the given path. 
	 * 
	 * @param session the current user session.
	 * @param absPath the path of the resource to get the access rights for
	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
	 * @throws RepositoryException
	 * @throws InternalErrorException 
	 */
	public Map<String, List<String>> getGrantedMap(String absPath) throws RepositoryException, InternalErrorException {
		//		System.out.println("++++ abspath "+ absPath);
		Session session = JCRRepository.getSession();

		Map<String, List<String>> map = new HashMap<String, List<String>>();

		Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
		AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);
		if (entries != null) {
			for (AccessControlEntry ace : entries) {
				List<String> privilegesList = null;
				Principal principal = ace.getPrincipal();
				//				System.out.println("Principal " + principal.getName());
				AccessRights accessPrivileges = accessMap.get(principal);
				if (accessPrivileges == null) {
					accessPrivileges = new AccessRights();
					accessMap.put(principal, accessPrivileges);
				}

				accessPrivileges.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
				Set<Privilege> deniedPrivileges = accessPrivileges.getDenied();
				for(Privilege priv: deniedPrivileges){
					//					System.out.println("Denied--> " + priv.getName());
					if (privilegesList==null)
						privilegesList = new ArrayList<String>();
					privilegesList.add(priv.getName());
				}
				map.put(principal.getName(), privilegesList);	
			}
		}

		return map;
	}


	/**
	 * Returns the mapping of declared access rights that have been set for the resource at
	 * the given path. 
	 * 
	 * @param session the current user session.
	 * @param absPath the path of the resource to get the access rights for
	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
	 * @throws RepositoryException
	 * @throws InternalErrorException 
	 */
	public Map<String, List<String>> getDeniedMap(String absPath) throws RepositoryException, InternalErrorException {
		//		System.out.println("++++ abspath "+ absPath);
		Session session = JCRRepository.getSession();

		Map<String, List<String>> map = new HashMap<String, List<String>>();

		Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
		AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);

		if (entries != null) {
			for (AccessControlEntry ace : entries) {
				List<String> privilegesList = null;
				Principal principal = ace.getPrincipal();
				//				System.out.println("Principal " + principal.getName());
				AccessRights accessPrivileges = accessMap.get(principal);
				if (accessPrivileges == null) {
					accessPrivileges = new AccessRights();
					accessMap.put(principal, accessPrivileges);
				}

				accessPrivileges.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
				Set<Privilege> deniedPrivileges = accessPrivileges.getDenied();
				for(Privilege priv: deniedPrivileges){
					//					System.out.println("Denied--> " + priv.getName());
					if (privilegesList==null)
						privilegesList = new ArrayList<String>();
					privilegesList.add(priv.getName());
				}
				map.put(principal.getName(), privilegesList);	
			}
		}

		return map;
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




}
