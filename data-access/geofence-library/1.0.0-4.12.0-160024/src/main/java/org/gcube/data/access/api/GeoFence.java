package org.gcube.data.access.api;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.gcube.data.access.bean.GSInstance;
import org.gcube.data.access.bean.GSInstanceList;
import org.gcube.data.access.bean.GSUser;
import org.gcube.data.access.bean.Group;
import org.gcube.data.access.bean.Instance;
import org.gcube.data.access.bean.Rule;
import org.gcube.data.access.bean.RuleList;
import org.gcube.data.access.bean.Rules;
import org.gcube.data.access.bean.User;
import org.gcube.data.access.bean.UserGroup;
import org.gcube.data.access.bean.UserGroupList;
import org.gcube.data.access.bean.UserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class GeoFence {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static RestTemplate restTemplate = null;
	private String geofenceRestUrl = null;
	
	/**
	 * Constructor of GeoFence to build the REST API to access on geofence instance.
	 * @param geofenceRestUrl It's the REST API URL of your geofence instance.
	 */
	public GeoFence(String geofenceRestUrl) {
		
		if (restTemplate == null)
			restTemplate = new RestTemplate(); 
		logger.debug("RestTemplate instance: " + restTemplate);
		
		this.geofenceRestUrl = geofenceRestUrl;
		logger.info("GeoFenceRestUrl used: " + this.geofenceRestUrl);
	}
	
	
	/****************
	 * USER SECTION *
	 ****************/
		
	/**
	 * Returns a GeoServer User (GSUser) object using a specific id of user. 
	 * @param id It's the userId of user you are looking for. 
	 * @return GSUser object.
	 */
	public GSUser getUserById(String id) {
		String url = geofenceRestUrl + "/users/id/{id}";		
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		
		logger.info("Call to getUserById with url: " + url + " [" + map + "]");
		GSUser gsuser = restTemplate.getForObject(url, GSUser.class, map);
		logger.debug(marshal(gsuser, GSUser.class));
		return gsuser;
	}
	
	/**
	 * Returns a GeoServer User (GSUser) object using a specific name of user. 
	 * @param userName It's the name of user you are looking for. 
	 * @return GSUser object.
	 */
	public GSUser getUserByUsername(String userName) {
		String url = geofenceRestUrl + "/users/name/{userName}";		
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		
		logger.info("Call to getUserByUsername with url: " + url + " [" + map + "]");
		GSUser gsuser = restTemplate.getForObject(url, GSUser.class, map);
		logger.debug(marshal(gsuser, GSUser.class));
		return gsuser;
	}

	/**
	 * Returns an HttpStatus (status) when you try to create the User object. 
	 * @param user It's the User object you are creating. 
	 * @return HttpStatus object.
	 */
	public HttpStatus createUser(User user) {
		String url = geofenceRestUrl + "/users";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		logger.info("Call to createUser with url: " + url);
		try {
			HttpEntity<User> request = new HttpEntity<User>(user, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);			
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the User object. 
	 * @param user It's the User object you are updating.
	 * @return HttpStatus object.
	 */	
	public HttpStatus updateUser(User user) {	
		String id = user.getId();		
		if (StringUtils.hasText(id)){
			logger.info("Call to updateUser with id " + id);
			return updateUserById(id, user.getPassword(), user.getEmailAddress(), user.isAdmin(), user.isEnabled());
		}		
		String userName = user.getName();		
		if (StringUtils.hasText(userName)){
			logger.info("Call to updateUser with userName " + userName);
			return updateUserByUsername(userName, user.getPassword(), user.getEmailAddress(), user.isAdmin(), user.isEnabled());
		}		
		return HttpStatus.BAD_REQUEST;
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the User object using the id. 
	 * @param id It's the id of user.
	 * @param password It's the password of user.
	 * @param email It's the email of user.
	 * @param admin It's the boolean flag to define if user is administrator or not.
	 * @param enabled It's the boolean flag to define if user is enabled or not.
	 * @return HttpStatus object.
	 */
	public HttpStatus updateUserById(String id, String password, String email, boolean admin, boolean enabled) {
		String url = geofenceRestUrl + "/users/id/{id}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);		
		logger.info("Call to updateUserById with url: " + url + " [" + map + "]");	
		User user = new User(password, email, enabled, admin);	
		logger.debug(marshal(user, User.class));		
		try {
			HttpEntity<User> request = new HttpEntity<User>(user, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the User object using the userName. 
	 * @param userName It's the username of user.
	 * @param password It's the password of user.
	 * @param email It's the email of user.
	 * @param admin It's the boolean flag to define if user is administrator or not.
	 * @param enabled It's the boolean flag to define if user is enabled or not.
	 * @return HttpStatus object.
	 */	
	public HttpStatus updateUserByUsername(String userName, String password, String email, boolean admin, boolean enabled) {
		String url = geofenceRestUrl + "/users/name/{userName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);		
		logger.info("Call to updateUserByUsername with url: " + url + " [" + map + "]");		
		User user = new User(password, email, enabled, admin);	
		logger.debug(marshal(user, User.class));		
		try {
			HttpEntity<User> request = new HttpEntity<User>(user, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}

	/**
 	 * Returns an HttpStatus (status) when you try to delete the User object using the id. 
	 * @param id It's the id of user.
	 * @param removeAllRules It's a flag to remove all rules together to the user.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteUserById(String id, boolean removeAllRules) {
		String url = geofenceRestUrl + "/users/id/{id}?cascade={removeAllRules}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("removeAllRules", Boolean.toString(removeAllRules));

		logger.info("Call to deleteUserById with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}

	/**
 	 * Returns an HttpStatus (status) when you try to delete the User object using the userName. 
	 * @param userName It's the username of user.
	 * @param removeAllRules It's a flag to remove all rules together to the user.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteUserByUsername(String userName, boolean removeAllRules) {
		String url = geofenceRestUrl + "/users/name/{userName}?cascade={removeAllRules}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("removeAllRules", Boolean.toString(removeAllRules));
		
		logger.info("Call to deleteUserByUsername with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}

	/**
  	 * Returns a UserList object within a list of Users object. 
	 * @return UserList object.
	 */
	public UserList getUserList() {
		String url = geofenceRestUrl + "/users";
		logger.info("Call to getUserList with url: " + url);

		UserList ul = restTemplate.getForObject(url, UserList.class);
		logger.debug(marshal(ul, UserList.class));
		return ul;
	}
	

	/*********************
	 * USERGROUP SECTION *
	 *********************/

	/**
 	 * Returns an HttpStatus (status) when you try to create the UserGroup object using the UserGroup object. 
	 * @param userGroup It's the UserGroup object you are creating.
	 * @return HttpStatus object.
	 */
	public HttpStatus createUserGroup(UserGroup userGroup) {
		String url = geofenceRestUrl + "/groups";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		logger.info("Call to createUserGroup with url: " + url);
		try {
			HttpEntity<UserGroup> request = new HttpEntity<UserGroup>(userGroup, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}	
	
	/**
	 * Returns an Group object using a specific id of user. 
	 * @param id It's the id of group you are looking for. 
	 * @return Group object.
	 */
	public Group getUserGroupById(String id) {
		String url = geofenceRestUrl + "/groups/id/{id}";			
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		
		logger.info("Call to getUserGroupById with url: " + url + " [" + map + "]");
		Group g = restTemplate.getForObject(url, Group.class, map);
		logger.debug(marshal(g, Group.class));
		return g;
	}
	
	/**
	 * Returns an Group object using a specific userName of user. 
	 * @param userName It's the username of group you are looking for.
	 * @return Group object.
	 */
	public Group getUserGroupByName(String userName) {
		String url = geofenceRestUrl + "/groups/name/{userName}";		
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		
		logger.info("Call to getUserGroupByName with url: " + url + " [" + map + "]");
		Group g = restTemplate.getForObject(url, Group.class, map);
		logger.debug(marshal(g, Group.class));
		return g;
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Group object using a group. 
	 * @param group It's the Group object you are updating.
	 * @return HttpStatus object.
	 */
	public HttpStatus updateUserGroup(Group group) {	
		String id = group.getId();
		logger.info("Call to updateUserGroup - id " + id);
		if (StringUtils.hasText(id))
			return updateUserGroupById(id, group.isEnabled());
	
		String name = group.getName();
		logger.info("Call to updateUserGroup - name " + name);
		if (StringUtils.hasText(name))
			return updateUserGroupByName(name, group.isEnabled());

		return HttpStatus.BAD_REQUEST;
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Group object using a group. 
	 * @param id It's the id of group you are updating.
	 * @param enabled It's the enabled flag of group. 
	 * @return HttpStatus object.
	 */
	public HttpStatus updateUserGroupById(String id, boolean enabled) {
		String url = geofenceRestUrl + "/groups/id/{id}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);		
		logger.info("Call to updateUserGroupById with url: " + url + " [" + map + "]");	
		UserGroup group = new UserGroup(enabled);
		logger.debug(marshal(group, UserGroup.class));		
		try {
			HttpEntity<UserGroup> request = new HttpEntity<UserGroup>(group, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Group object using a name. 
	 * @param name It's the name of Group object you are updating.
	 * @param enabled It's the enabled flag of group.
	 * @return HttpStatus object.
	 */	
	public HttpStatus updateUserGroupByName(String name, boolean enabled) {
		String url = geofenceRestUrl + "/groups/name/{name}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);		
		logger.info("Call to updateUserGroupByName with url: " + url + " [" + map + "]");	
		UserGroup group = new UserGroup(enabled);	
		logger.debug(marshal(group, UserGroup.class));		
		try {
			HttpEntity<UserGroup> request = new HttpEntity<UserGroup>(group, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to delete the Group object using the groupId. 
	 * @param groupId It's the groupId of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteUserGroupById(String groupId) {		
		String url = geofenceRestUrl + "/groups/id/{groupId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId", groupId);

		logger.info("Call to deleteUserGroupById with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to delete the Group object using the groupName.
	 * @param groupName It's the id of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteUserGroupByName(String groupName) {		
		String url = geofenceRestUrl + "/groups/name/{groupName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupName", groupName);

		logger.info("Call to deleteUserGroupByName with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns a UserGroupList object within a list of UserGroups object. 
	 * @return UserGroupList object.
	 */
	public UserGroupList getUserGroupList() {
		String url = geofenceRestUrl + "/groups";
		logger.info("Call to getUserGroupList with url: " + url);
		
		UserGroupList ugl = restTemplate.getForObject(url, UserGroupList.class);
		logger.debug(marshal(ugl, UserGroupList.class));
		return ugl;
	}
		
	/**
	 * Returns an HttpStatus (status) when you try to assign the user by id to group by id.
	 * @param userId It's the id of user.
	 * @param groupId It's the id of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus assignToUserGroupByUserIdGroupId(String userId, String groupId) {
		String url = geofenceRestUrl + "/users/id/{userId}/group/id/{groupId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("groupId", groupId);
		
		logger.info("Call to assignToUserGroupByUserIdGroupId with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to assign the user by id to group by name.
	 * @param userId It's the id of user.
	 * @param groupName It's the name of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus assignToUserGroupByUserIdGroupName(String userId, String groupName) {
		String url = geofenceRestUrl + "/users/id/{userId}/group/name/{groupName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("groupName", groupName);
		
		logger.info("Call to assignToUserGroupByUserIdGroupName with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}		

	/**
	 * Returns an HttpStatus (status) when you try to assign the user by username to group by id.
	 * @param userName It's the username of user.
	 * @param groupId It's the id of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus assignToUserGroupByUserNameGroupId(String userName, String groupId) {
		String url = geofenceRestUrl + "/users/name/{userName}/group/id/{groupId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("groupId", groupId);
		
		logger.info("Call to assignToUserGroupByUserNameGroupId with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}		
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to assign the user by username to group by name.
	 * @param userName It's the username of user.
	 * @param groupName It's the name of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus assignToUserGroupByUserNameGroupName(String userName, String groupName) {
		String url = geofenceRestUrl + "/users/name/{userName}/group/name/{groupName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("groupName", groupName);
		
		logger.info("Call to assignToUserGroupByUserIdGroupName with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to remove the user by id and the group by id. 
	 * @param userId It's the id of user.
	 * @param groupId It's the id of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus removeUserGroupByUserIdGroupId(String userId, String groupId) {
		String url = geofenceRestUrl + "/users/id/{userId}/group/id/{groupId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("groupId", groupId);
		
		logger.info("Call to removeUserGroupByUserIdGroupId with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to remove the user by id and the group by name. 
	 * @param userId It's the id of user.
	 * @param groupName It's the name of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus removeUserGroupByUserIdGroupName(String userId, String groupName) {
		String url = geofenceRestUrl + "/users/id/{userId}/group/name/{groupName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("groupName", groupName);
		
		logger.info("Call to removeUserGroupByUserIdGroupName with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}		

	/**
	 * Returns an HttpStatus (status) when you try to remove the user by username and the group by id.
	 * @param userName It's the username of user.
	 * @param groupId It's the id of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus removeUserGroupByUserNameGroupId(String userName, String groupId) {
		String url = geofenceRestUrl + "/users/name/{userName}/group/id/{groupId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("groupId", groupId);
		
		logger.info("Call to removeUserGroupByUserNameGroupId with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}		
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to remove the user by username and the group by name.
	 * @param userName It's the username of user.
	 * @param groupName It's the name of group.
	 * @return HttpStatus object.
	 */
	public HttpStatus removeUserGroupByUserNameGroupName(String userName, String groupName) {
		String url = geofenceRestUrl + "/users/name/{userName}/group/name/{groupName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("groupName", groupName);
		
		logger.info("Call to removeUserGroupByUserNameGroupName with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
		
	/*********************
	 * INSTANCES SECTION *
	 *********************/
	
	/**
	 * Returns an HttpStatus (status) when you try to create the Instance object. 
	 * @param instance It's the Instance object.
	 * @return HttpStatus object.
	 */
	public HttpStatus createInstance(Instance instance) {
		String url = geofenceRestUrl + "/instances";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		logger.info("Call to createInstance with url: " + url);
		try {
			HttpEntity<Instance> request = new HttpEntity<Instance>(instance, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an GSInstance object using a specific id of instance. 
	 * @param instanceId It's the id of Instance object.
	 * @return GSInstance object.
	 */
	public GSInstance getInstanceById(String instanceId) {
		String url = geofenceRestUrl + "/instances/id/{instanceId}";		
		Map<String, String> map = new HashMap<String, String>();
		map.put("instanceId", instanceId);
		
		logger.info("Call to getInstanceById with url: " + url + " [" + map + "]");
		GSInstance instance = restTemplate.getForObject(url, GSInstance.class, map);
		logger.debug(marshal(instance, GSInstance.class));
		return instance;
	}
	
	/**
	 * Returns an GSInstance object using a specific name of instance. 
	 * @param instanceName It's the name of Instance object.
	 * @return GSInstance object.
	 */
	public GSInstance getInstanceByName(String instanceName) {
		String url = geofenceRestUrl + "/instances/name/{instanceName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("instanceName", instanceName);
		
		logger.info("Call to getInstanceByName with url: " + url + " [" + map + "]");
		GSInstance instance = restTemplate.getForObject(url, GSInstance.class, map);
		logger.debug(marshal(instance, GSInstance.class));
		return instance;
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Instance object using an instance. 
	 * @param instance It's the Instance object.
	 * @return HttpStatus object.
	 */
	public HttpStatus updateInstance(Instance instance) {
		String id = instance.getId();
		if (StringUtils.hasText(id)){
			logger.info("Call to updateInstance with id " + id);
			return updateInstanceById(id, instance.getUsername(), instance.getPassword(), instance.getBaseURL(), instance.getDescription());
		}		
		String name = instance.getName();		
		if (StringUtils.hasText(name)){
			logger.info("Call to updateInstance with name " + name);
			return updateInstanceByName(name, instance.getUsername(), instance.getPassword(), instance.getBaseURL(), instance.getDescription());
		}	
		
		return HttpStatus.BAD_REQUEST;
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Instance object with a specific id.
	 * @param id It's the id of Instance object, not editable.
	 * @param username It's the username of Instance object.
	 * @param password It's the password of Instance object.
	 * @param baseURL It's the baseURL of Instance object.
	 * @param description It's the description of Instance object.
	 * @return HttpStatus object.
	 */
	public HttpStatus updateInstanceById(String id, String username, String password, String baseURL, String description) {
		String url = geofenceRestUrl + "/instances/id/{id}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);		
		
		logger.info("Call to updateUserGroupById with url: " + url + " [" + map + "]");		
		Instance instance = new Instance(username, password, baseURL, description);		
		logger.debug(marshal(instance, Instance.class));
		try {
			HttpEntity<Instance> request = new HttpEntity<Instance>(instance, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Instance object with a specific instanceName.
	 * @param instanceName It's the name of Instance object, not editable.
	 * @param username It's the username of Instance object.
	 * @param password It's the password of Instance object.
	 * @param baseURL It's the baseURL of Instance object.
	 * @param description It's the description of Instance object.
	 * @return HttpStatus object.
	 */
	public HttpStatus updateInstanceByName(String instanceName, String username, String password, String baseURL, String description) {
		String url = geofenceRestUrl + "/instances/name/{instanceName}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("instanceName", instanceName);		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);	
		
		logger.info("Call to updateInstanceByName with url: " + url + " [" + map + "]");		
		Instance instance = new Instance(username, password, baseURL, description);		
		logger.debug(marshal(instance, Instance.class));
		try {
			HttpEntity<Instance> request = new HttpEntity<Instance>(instance, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class, map);
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to delete the Instance object using the instanceId. 
	 * @param instanceId It's the id of Instance object.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteInstanceById(String instanceId) {
		String url = geofenceRestUrl + "/instances/id/{instanceId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("instanceId", instanceId);

		logger.info("Call to deleteInstanceById with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to delete the Instance object using the instanceName. 
	 * @param instanceName It's the name of Instance object.
	 * @param removeAllRules It's a parameter to removes all rules together the Instance object.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteInstanceByName(String instanceName, boolean removeAllRules) {
		String url = geofenceRestUrl + "/instances/name/{instanceName}?cascade={removeAllRules}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("instanceName", instanceName);
		map.put("removeAllRules", Boolean.toString(removeAllRules));
		
		logger.info("Call to deleteInstanceByName with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
	 * Returns a GSInstanceList object within a list of Instance object. 
	 * @return GSInstanceList object.
	 */
	public GSInstanceList getInstanceList() {
		String url = geofenceRestUrl + "/instances";
		logger.info("Call to getInstanceList with url: " + url);

		GSInstanceList instances = restTemplate.getForObject(url, GSInstanceList.class);
		logger.debug(marshal(instances, GSInstanceList.class));
		return instances;
	}
	
	
	
	/*********************
	 * RULES SECTION *
	 *********************/
	
	/**
	 * Returns an HttpStatus (status) when you try to create the Rule object. 
	 * @param rule It's the Rule object.
	 * @return HttpStatus object.
	 */
	public HttpStatus createRule(Rule rule) {		
		String url = geofenceRestUrl + "/rules";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		logger.info("Call to createInstance with url: " + url);
		try {
			HttpEntity<Rule> request = new HttpEntity<Rule>(rule, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}		
	}
	
	/**
	 * Returns a Rule object using a specific id of rule. 
	 * @param id It's the id of rule.
	 * @return Rule object.
	 */
	public Rules getRulesById(String id) {
		String url = geofenceRestUrl + "/rules/id/{id}";		
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		
		logger.info("Call to getRulesById with url: " + url + " [" + map + "]");
		Rules rules = restTemplate.getForObject(url, Rules.class, map);
		logger.debug(marshal(rules, Rules.class));
		return rules;
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Rule object. 
	 * @param rule It's the rule object.
	 * @return HttpStatus object.
	 */
	public HttpStatus updateRule(Rule rule) {
		String id = rule.getId();
		if (StringUtils.hasText(id)){
			logger.info("Call to updateInstance with id " + id);		

			return updateRuleById(id, rule.getPriority(), rule.getService(), rule.getRequest(), rule.getWorkspace(), 
					rule.getLayer(), rule.getUser().getId(), rule.getGroup().getId(), rule.getInstance().getId());
		}	
		return HttpStatus.BAD_REQUEST;		
	}
	
	/**
	 * Returns an HttpStatus (status) when you try to update the Rule object with a specific id.
	 * @param id It's the id of Instance object, not editable.
	 * @param priority It's the priority of Instance object.
	 * @param service It's the service of Instance object.
	 * @param request It's the request of Instance object.
	 * @param workspace It's the workspace of Instance object.
	 * @param layer It's the layer of Instance object.
	 * @param userId It's the userId of Instance object (not necessary the username).
	 * @param groupId It's the groupId of Instance object  (not necessary the name).
	 * @param instanceId It's the instanceId of Instance object (not necessary the name of instance).
	 * @return HttpStatus object.
	 */
	public HttpStatus updateRuleById(String id, String priority, String service, String request, String workspace, 
			String layer, String userId, String groupId, String instanceId) {
		
		String url = geofenceRestUrl + "/rules/id/{id}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		logger.info("Call to updateRule with url: " + url + " [" + map + "]");
		Rule rule = new Rule(priority, service, request, workspace, layer, userId, groupId, instanceId);		
		logger.debug(marshal(rule, Rule.class));
		
		try {
			HttpEntity<Rule> req = new HttpEntity<Rule>(rule, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, req, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}
	}
	
	/**
 	 * Returns an HttpStatus (status) when you try to delete the Rule object using the ruleId. 
	 * @param ruleId It's the id of Rule object.
	 * @return HttpStatus object.
	 */
	public HttpStatus deleteRule(String ruleId) {
		String url = geofenceRestUrl + "/rules/id/{ruleId}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("ruleId", ruleId);

		logger.info("Call to deleteRule with url: " + url + " [" + map + "]");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class, map);
			logger.debug("StatusCode: " + printHttpStatus(response.getStatusCode()));
			return response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			logger.debug("StatusCode: " + printHttpStatus(ex.getStatusCode()));
			return ex.getStatusCode();
		}	
	}
	
	/**
	 * Returns a RuleList object within a list of Rule object. 
	 * @return RuleList object.
	 */
	public RuleList getRulesList() {
		String url = geofenceRestUrl + "/rules";
		logger.info("Call to getRulesList with url: " + url);
		
		RuleList rl = restTemplate.getForObject(url, RuleList.class);
		logger.debug(marshal(rl, RuleList.class));
		return rl;
	}
	
	
	/*****************
	 * Utils methods *
	 *****************/
	
	private String printHttpStatus(HttpStatus status){		
		return status.value() + " - " + status.name();
	}
	
	private <T> String marshal(T object, Class<T> marshalClass) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(marshalClass);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(object, sw);
			return sw.toString();
		} catch (JAXBException e) {
			return null;
		}
	}

}
