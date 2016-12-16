package org.gcube.common.homelibrary.jcr.workspace.accessmanager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accessmanager.AccessManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.ServletName;
import org.gcube.common.homelibrary.jcr.workspace.util.TokenUtility;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRAccessManager implements AccessManager{

	private Logger logger = LoggerFactory.getLogger(JCRAccessManager.class);

	Map<String, Endpoint> servlets;

	public JCRAccessManager(){
		super();
		servlets = JCRRepository.servlets;
	}

	public ACLType getACLByUser(String user, String absPath) {
		logger.debug("Get ACL for resource " + absPath + " for user: " + user);
		GetMethod getMethod = null;
		ACLType aclType = null;
		List<String> list = new ArrayList<>();
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.GET_ACL_BY_USER).uri().toString() +"?login=" + user + "&absPath=" + URLEncoder.encode(absPath, "UTF-8");
//			System.out.println(requestUrl);
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			String acl = (String) xstream.fromXML(getMethod.getResponseBodyAsString());
			list.add(acl);
			aclType =  WorkspaceUtil.getACLTypeByKey(list);
		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

			logger.error("Error deleting Permissions in AccessManager", e);
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
	
		
		return aclType;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getACL(String absPath) throws InternalErrorException {
		logger.debug("Get ACL for resource " + absPath);
		Map<String, List<String>> map = null;

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.GET_ACL).uri().toString() + "?absPath=" + URLEncoder.encode(absPath, "UTF-8");
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
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
			logger.error("Error reatriving ACL", e);
		}
		return map;

	}


	public boolean modifyAce(List<String> users, String absPath,
			List<String> privilegesList, String order) throws InternalErrorException {
		logger.debug("Modify ACL on resource " + absPath + " for users: " + users.toString());
		Boolean modified = true;
		try{
			Map<String, List<String>> map = getACL(absPath);
			if (map.containsKey(users.get(0)))
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
					String requestUrl = servlets.get(ServletName.MODIFY_ACL).uri().toString() + "?principalId=" + user +  "&resourcePath=" + URLEncoder.encode(absPath, "UTF-8") + privileges.toString() +"&order=" + order;
					getMethod =  new GetMethod(requestUrl);
					TokenUtility.setHeader(getMethod);
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
				if (getMethod.getResponseBodyAsString()==null)
					modified = false;
				else
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
		logger.debug("GET ACL of resource " + absPath);
		Map<String, List<String>> map = null;

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.GET_EACL).uri().toString() + "?absPath=" + URLEncoder.encode(absPath, "UTF-8");
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			//			logger.debug("Response " + getMethod.getResponseBodyAsString());

			XStream xstream = new XStream();
			try{
				if (getMethod.getResponseBodyAsString()==null)
					map = new HashMap<String, List<String>>();
				else
					map = (Map<String, List<String>>) xstream.fromXML(getMethod.getResponseBodyAsString());

				//				System.out.println("*** " + map);
			}catch (Exception e) {
				logger.error("Error reatriving EACL", e);
			}

			if(getMethod != null)
				getMethod.releaseConnection();

		}catch (Exception e) {
			logger.error("Error modifing ACLs: " + e);
			throw new InternalErrorException(e);
		}



		return map;

	}


	@Override
	public boolean setReadOnlyACL(List<String> users, String absPath)
			throws InternalErrorException {
		logger.debug("Set READ ONLY ACL on resource " + absPath + " for users: " + users.toString());
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
		logger.debug("Set WRITE OWN ACL on resource " + absPath + " for users: " + users.toString());
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
		logger.debug("Set WRITE ALL ACL on resource " + absPath + " for users: " + users.toString());
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
		logger.debug("Set Access Denide on resource " + absPath + " for users: " + users.toString());
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
		logger.debug("Set ADMIN ACL on resource " + absPath + " for users: " + users.toString());
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
		logger.debug("Delete ACES on resource " + resourcePath + " for users: " + users.toString());
		GetMethod getMethod = null;
		Boolean modified = true;
		try {

			HttpClient httpClient = new HttpClient();      		
			StringBuilder applyTo= new StringBuilder();
			for (String user: users)
				applyTo.append("&applyTo=" + user);

			String requestUrl = servlets.get(ServletName.DELETE_ACL).uri().toString() + "?absPath=" + URLEncoder.encode(resourcePath, "UTF-8") +  applyTo;
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response DeleteAcesServlet: " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

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
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getDeniedMap(String absPath) throws RepositoryException, InternalErrorException {
		logger.debug("Get Denide map for resource " + absPath);
		Map<String, List<String>> map = null;

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.GET_DENIED_MAP).uri().toString() + "?absPath=" + URLEncoder.encode(absPath, "UTF-8");
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
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


	public boolean canAddChildren(String user, String absPath) {
		logger.debug("Check if " + user + " can add children on resource " + absPath);
		GetMethod getMethod = null;
		Boolean modified = true;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.CAN_ADD_CHILDREN).uri().toString() + "?login=" + user + "&absPath=" + URLEncoder.encode(absPath, "UTF-8") ;
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response DeleteAcesServlet: " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

			logger.error("Error adding children in AccessManager", e);
			return false;
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modified;
	}


	public boolean canDeleteChildren(String user, String absPath) {
		logger.debug("Check if " + user + " can delete children of resource " + absPath);
		GetMethod getMethod = null;
		Boolean modified = true;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.CAN_DELETE_CHILDREN).uri().toString() + "?login=" + user + "&absPath=" + URLEncoder.encode(absPath, "UTF-8") ;
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response DeleteAcesServlet: " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

			logger.error("Error deleting children Permissions in AccessManager", e);
			return false;
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modified;
	}


	public boolean canDelete(String user, String absPath, boolean root) {
		logger.debug("Check if " + user + " can delete resource " + absPath);
		GetMethod getMethod = null;
		Boolean modified = true;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.CAN_DELETE).uri().toString() + "?login=" + user + "&isRoot=" + root +"&absPath=" + URLEncoder.encode(absPath, "UTF-8") ;
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response DeleteAcesServlet: " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

			logger.error("Error deleting Permissions in AccessManager", e);
			return false;
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modified;
	}


	public boolean canModifyProperties(String user, String absPath, boolean root) {
		logger.debug("Check if " + user + " can modify resource " + absPath);
		GetMethod getMethod = null;
		Boolean modified = true;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.CAN_MODIFY).uri().toString() + "?login=" + user + "&isRoot=" + root +"&absPath=" + URLEncoder.encode(absPath, "UTF-8") ;
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response DeleteAcesServlet: " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			modified = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

			logger.error("Error modifying privileges in AccessManager", e);
			return false;
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return modified;
	}


	public boolean canReadNode(String user, String absPath) throws InternalErrorException {
		logger.debug("Check if " + user + " can read resource " + absPath);
		GetMethod getMethod = null;
		Boolean canRead = false;
		try {

			HttpClient httpClient = new HttpClient();      		

			String requestUrl = servlets.get(ServletName.CAN_READ).uri().toString() + "?login=" + user + "&absPath=" + URLEncoder.encode(absPath, "UTF-8") ;
//			System.out.println(requestUrl);
			getMethod =  new GetMethod(requestUrl);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);
			logger.debug("Response canReadNode: " + getMethod.getResponseBodyAsString());

			if(getMethod != null)
				getMethod.releaseConnection();

			XStream xstream = new XStream();

			canRead = (Boolean) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			if(getMethod != null)
				getMethod.releaseConnection();

			logger.error("Error reading privileges in AccessManager", e);
//			return false;
					throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return canRead;
	}




}
