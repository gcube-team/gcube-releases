package org.gcube.common.homelibrary.jcr.workspace.accessmanager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accessmanager.AccessManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
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


	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getACL(String absPath) throws InternalErrorException {

		Map<String, List<String>> map = null;

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = url + "/acl/GetACL?" + JCRRepository.getCredentials() + "&absPath=" + URLEncoder.encode(absPath, "UTF-8");
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


		XStream xstream = new XStream();

		try{
			map = (Map<String, List<String>>) xstream.fromXML(getMethod.getResponseBodyAsString());
		}catch (Exception e) {
			logger.error("Error reatriving EACL", e);
		}
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


	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> getEACL(String absPath) throws InternalErrorException {

		Map<String, List<String>> map = null;

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = url + "/acl/GetEACL?" + JCRRepository.getCredentials() + "&absPath=" + URLEncoder.encode(absPath, "UTF-8");
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


		XStream xstream = new XStream();

		try{
			map = (Map<String, List<String>>) xstream.fromXML(getMethod.getResponseBodyAsString());
		}catch (Exception e) {
			logger.error("Error reatriving EACL", e);
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

//	/**
//	 * Returns the mapping of declared access rights that have been set for the resource at
//	 * the given path. 
//	 * 
//	 * @param session the current user session.
//	 * @param absPath the path of the resource to get the access rights for
//	 * @return map of access rights.  Key is the user/group principal, value contains the granted/denied privileges
//	 * @throws RepositoryException
//	 * @throws InternalErrorException 
//	 */
//	public Map<String, List<String>> getGrantedMap(String absPath) throws RepositoryException, InternalErrorException {
//		//		System.out.println("++++ abspath "+ absPath);
//		Session session = JCRRepository.getSession();
//
//		Map<String, List<String>> map = new HashMap<String, List<String>>();
//
//		Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
//		AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);
//		if (entries != null) {
//			for (AccessControlEntry ace : entries) {
//				List<String> privilegesList = null;
//				Principal principal = ace.getPrincipal();
//				//				System.out.println("Principal " + principal.getName());
//				AccessRights accessPrivileges = accessMap.get(principal);
//				if (accessPrivileges == null) {
//					accessPrivileges = new AccessRights();
//					accessMap.put(principal, accessPrivileges);
//				}
//
//				accessPrivileges.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
//				Set<Privilege> deniedPrivileges = accessPrivileges.getDenied();
//				for(Privilege priv: deniedPrivileges){
//					//					System.out.println("Denied--> " + priv.getName());
//					if (privilegesList==null)
//						privilegesList = new ArrayList<String>();
//					privilegesList.add(priv.getName());
//				}
//				map.put(principal.getName(), privilegesList);	
//			}
//		}
//
//		return map;
//	}


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
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getDeniedMap(String absPath) throws RepositoryException, InternalErrorException {
		Map<String, List<String>> map = null;

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = url + "/acl/GetDeniedMap?" + JCRRepository.getCredentials() + "&absPath=" + URLEncoder.encode(absPath, "UTF-8");
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


		XStream xstream = new XStream();

		try{
			map = (Map<String, List<String>>) xstream.fromXML(getMethod.getResponseBodyAsString());
		}catch (Exception e) {
			logger.error("Error reatriving EACL", e);
		}
		return map;
	}




//	private AccessControlEntry[] getDeclaredAccessControlEntries(Session session, String absPath) throws RepositoryException {
//		AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
//		AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
//		for (AccessControlPolicy accessControlPolicy : policies) {
//			if (accessControlPolicy instanceof AccessControlList) {
//				AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
//				return accessControlEntries;
//			}
//		}
//		return new AccessControlEntry[0];
//	}




}
