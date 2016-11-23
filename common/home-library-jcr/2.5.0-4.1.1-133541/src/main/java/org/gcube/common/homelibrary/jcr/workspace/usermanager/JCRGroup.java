package org.gcube.common.homelibrary.jcr.workspace.usermanager;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.ServletName;
import org.gcube.common.homelibrary.jcr.workspace.util.TokenUtility;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class JCRGroup implements GCubeGroup{

//	public static final String USERS =	"hl:users";
	private Logger logger = LoggerFactory.getLogger(JCRGroup.class);

	Map<String, Endpoint> servlets;
	public String name;

	public Group group;

	public JCRGroup(final String name) throws InternalErrorException {
		super();
		this.name = name;
		this.servlets = JCRRepository.servlets;

	}

	@Override
	public String getName() {
		return name;
	}


	@Override
	public boolean removeMembers(List<String> usersList) throws InternalErrorException {
		try{
			updateGroup(null, usersList);
		}catch (Exception e) {
			return false;
		}
		return true;

	}

	@Override
	public boolean addMembers(List<String> users) throws InternalErrorException {
		try{
			updateGroup(users, null);
		}catch (Exception e) {
			return false;
		}
		return true;

	}

	@Override
	public boolean addMember(final String user) throws InternalErrorException {

		try {
			updateGroup(new ArrayList<String>(){/**
			 * 
			 */
				private static final long serialVersionUID = 1L;

				{ add(user);}}, null);
		} catch (InternalErrorException e) {
			return false;
		}

		return true;

	}

	@Override
	public boolean removeMember(final String user) throws InternalErrorException {

		try {
			updateGroup(null, new ArrayList<String>(){/**
			 * 
			 */
				private static final long serialVersionUID = 1L;

				{ add(user);}});
		} catch (InternalErrorException e) {
			return false;
		}
		return true;

	}


	@Override
	public boolean isMember(final String member) throws InternalErrorException {
		List<String> members;

		try{
			members = getMembers();

			if (members.contains(member))
				return true;

		}catch (Exception e) {
			logger.error("Error retrieving Users in UserManager", e);
			throw new InternalErrorException(e);
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMembers() throws InternalErrorException {

		GetMethod getMethod = null;
		List<String> users = null;
		try {

			HttpClient httpClient = new HttpClient();          
			getMethod =  new GetMethod(servlets.get(ServletName.GROUP_MEMBERSHIP).uri().toString() + "?groupName=" + name);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);

			logger.debug("Response " + getMethod.getResponseBodyAsString());


			XStream xstream = new XStream();
			users = (List<String>) xstream.fromXML(getMethod.getResponseBodyAsString());


		} catch (Exception e) {
			logger.error("Error retrieving Users in UserManager", e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		//get return
		return users;	

	}


	//UPDATE GROUP

	public void updateGroup(List<String> members, List<String> membersToDelete) throws InternalErrorException {

		//es. http://localhost:8080/?param1=value1&param1=value2&param1=value3

		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();            

			StringBuilder listMembers;
			if (members != null){
				listMembers = new StringBuilder();
				for (String member: members){
					listMembers.append("&member=");
					listMembers.append(member);
				}
//				System.out.println("listMembers: " + url + "/UpdateGroupServlet?groupName=" + name + listMembers);
//				logger.debug("URL: " + url + "/UpdateGroupServlet?groupName=" + name + listMembers);
				getMethod =  new GetMethod(servlets.get(ServletName.UPDATE_GROUP).uri().toString() + "?groupName=" + name + listMembers);
				TokenUtility.setHeader(getMethod);
			}

			StringBuilder listMembersToDelete;
			if (membersToDelete != null){
				listMembersToDelete = new StringBuilder();
				for (String member: membersToDelete){
					listMembersToDelete.append("&memberToDelete=");
					listMembersToDelete.append(member);
				}
//				System.out.println("listMembersToDelete" + url + "/UpdateGroupServlet?groupName=" + name + listMembersToDelete);
//				logger.debug("URL: " + url + "/UpdateGroupServlet?groupName=" + name + listMembersToDelete);
				getMethod =  new GetMethod(servlets.get(ServletName.UPDATE_GROUP).uri().toString() + "?groupName=" + name + listMembersToDelete);
				TokenUtility.setHeader(getMethod);}


			//			getMethod =  new GetMethod(url + "/CreateUserServlet?userName=" + userName );
			httpClient.executeMethod(getMethod);

			logger.debug("Response " + getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			logger.error("Error updating Group " + name, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

	}

	@Override
	public String getDisplayName() throws InternalErrorException {
		String displayName;
		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();            
			getMethod =  new GetMethod(servlets.get(ServletName.GET_DISPLAY_NAME).uri().toString() + "?groupName=" + name);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);

			logger.debug("Response " + getMethod.getResponseBodyAsString());


			XStream xstream = new XStream();
			displayName = (String) xstream.fromXML(getMethod.getResponseBodyAsString());


		} catch (Exception e) {
			return null;
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		//get return
		return displayName;	
	}

	@Override
	public boolean setDisplayName(String displayName) throws InternalErrorException {

		boolean flag;
		GetMethod getMethod = null;
		try {

			HttpClient httpClient = new HttpClient();            
			getMethod =  new GetMethod(servlets.get(ServletName.SET_DISPLAY_NAME).uri().toString() + "?groupName=" + name + "&displayName=" + displayName);
			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);

			logger.debug("Response " + getMethod.getResponseBodyAsString());

			flag = true;
			//			XStream xstream = new XStream();
			//			flag = (boolean) xstream.fromXML(getMethod.getResponseBodyAsString());


		} catch (Exception e) {
			flag = false;
			//			logger.error("Error retrieving Users in UserManager", e);
			//			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		//get return
		return flag;	
	}








}

