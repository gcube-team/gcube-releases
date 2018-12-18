package org.gcube.vomanagement.usermanagement.impl.ws;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.ws.utils.HttpUtils;
import org.gcube.vomanagement.usermanagement.model.Email;
import org.gcube.vomanagement.usermanagement.model.GCubeMembershipRequest;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.MembershipRequestStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Exploit Liferay JSON Web Service to perform UserManager's operations.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LiferayWSUserManager implements UserManager{

	// save host/port and schema
	private String schema;
	private String host;
	private int port;

	// These properties are needed to save authentication credentials once.
	private HttpClientContext localContext;
	private CredentialsProvider credsProvider;
	private HttpHost target;

	// Several JSON calls use this property, so it will be discovered once (at init)
	private Long companyId;

	// the base path of the JSONWS apis
	private static final String API_BASE_URL = "/api/jsonws";

	// get methods paths
	private static final String GET_COMPANY_ID = "/company/get-company-by-web-id/web-id/liferay.com";
	private static final String GET_USER_BY_USERNAME = "/user/get-user-by-screen-name/company-id/$COMPANY_ID/screen-name/$USER_ID";
	private static final String GET_USER_BY_EMAIL= "/user/get-user-by-email-address/company-id/$COMPANY_ID/email-address/$EMAIL";
	private static final String GET_USERS_BY_GROUP = "/user/get-group-users/group-id/$GROUP_ID";
	private static final String GET_USER_CUSTOM_FIELD_BY_KEY = "/expandovalue/get-json-data/company-id/$COMPANY_ID/class-name/com.liferay.portal.model.User/table-name/CUSTOM_FIELDS/column-name/$CUSTOM_FIELD_KEY/class-pk/$USER_ID";
	//private static final String UPDATE_USER_CUSTOM_FIELD_BY_KEY = "/expandovalue/add-value/company-id/$COMPANY_ID/class-name/com.liferay.portal.model.User/table-name/CUSTOM_FIELDS/column-name/$CUSTOM_FIELD_KEY/class-pk/$USER_ID/data/$VALUE";
	private static final String GET_CONTACT_BY_USER_ID = "/contact/get-contact/contact-id/$CONTACT_ID";
	private static final String GET_ROLES_IN_GROUP_BY_USER = "/role/get-user-group-roles/user-id/$USER_ID/group-id/$GROUP_ID";
	private static final String GET_IDS_USERS_HAVING_GLOBAL_ROLE = "/user/get-role-user-ids/role-id/$ROLE_ID";
	private static final String GET_USER_BY_ID = "/user/get-user-by-id/user-id/$USER_ID";
	private static final String GET_USER_ID_BY_GROUP = "/user/get-group-user-ids/group-id/$GROUP_ID";

	// logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LiferayWSUserManager.class);

	// some pre-defined constants
	private static final String USER_LOCATION_INDUSTRY_KEY = "industry";
	private static final int USERS_EXECUTOR_FACTOR = 20;

	/**
	 * In order to contact the json ws of Liferay, user and password are needed. The host in which the current JVM
	 * machine runs needs to be authorized.
	 * @param user
	 * @param password
	 * @param host the host to contact
	 * @param port the port number
	 * @throws Exception 
	 * @schema the schema (http/https) https is suggested!
	 */
	public LiferayWSUserManager(String user, String password, String host, String schema, int port) throws Exception{

		this.host = host;
		this.port = port;
		this.schema = schema;

		target = new HttpHost(host, port, schema);
		credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials(user, password));

		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(target, basicAuth);

		// Add AuthCache to the execution context
		localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		// retrieve the company-id which will used later on
		retrieveCompanyId();
	}

	/**
	 * Retrieve the company id value, which will be used later on for the other calls
	 * @throws Exception 
	 */
	private void retrieveCompanyId() throws Exception {

		String json = HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_COMPANY_ID, credsProvider, localContext, target);

		if(json != null){
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject)parser.parse(json);
			companyId = (Long)jsonObject.get("companyId");
			logger.info("Company id retrieved is " + companyId);
		}else
			throw new Exception("Failed to retrieve the company-id. The following calls will fail!");

	}

	/**
	 * Maps the JSON user to the GCubeUser.class object
	 * @param json
	 * @return
	 */
	private GCubeUser mapLRUser(String json){	
		try{
			if (json != null) {
				JSONParser parser = new JSONParser();
				JSONObject userJSON = (JSONObject)parser.parse(json);
				// TODO skip for now
				List<Email> emails = new ArrayList<Email>();
				//			for (EmailAddress e : u.getEmailAddresses()) {
				//				emails.add(new Email(e.getAddress(), e.getType().toString(), e.isPrimary()));
				//			}

				String locationIndustry = "";
				try {
					locationIndustry = (String) readCustomAttr((long)userJSON.get("userId"), USER_LOCATION_INDUSTRY_KEY);
				} catch (Exception e1) {
					logger.warn("Failed to retrieve property " + USER_LOCATION_INDUSTRY_KEY, e1);
				}

				// retrieve the contact id information (it is into the user json)
				long contactId = (long)userJSON.get("contactId");

				// retrieve contact json obj from contactId
				String jsonContact = getContactJson(contactId);
				JSONObject contactJSON = (JSONObject)parser.parse(jsonContact);

				return new GCubeUser(
						(long)userJSON.get("userId"), 
						(String)userJSON.get("screenName"), 
						(String)userJSON.get("emailAddress"), 
						(String)userJSON.get("firstName"),
						(String)userJSON.get("middleName"),
						(String)userJSON.get("lastName"),
						buildFullName(userJSON),
						(long)userJSON.get("createDate"),
						getUserAvatarAbsoluteURL((String)userJSON.get("uuid"), (long)userJSON.get("portraitId")), // skip for now TODO getUserAvatarAbsoluteURL(u)
						(boolean)contactJSON.get("male"),
						(String)userJSON.get("jobTitle"),
						locationIndustry, 
						emails);
			}
		}catch(Exception e){
			logger.error("Exception while mapping the json user object to the GCubeUser java object", e);
		}
		return null;
	}

	/**
	 * Evaluate the user's avatar url path (absolute!)
	 * @param userUuid
	 * @param portraitId
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	private String getUserAvatarAbsoluteURL(String userUuid, long portraitId) throws UnsupportedEncodingException, NoSuchAlgorithmException{

		// url looks like LIFERAY_PORTAL/image/user_male_portrait?img_id=21125690&img_id_token=aPduzUQfxcz9kiLzD0yrChPU8k4%3D
		// where img_id_token is the sha-1/base64 encoding, encoded as query parameter, of userUuid
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		String imageId =  URLEncoder.encode(Base64.encodeBase64String(md.digest(userUuid.getBytes())), "UTF-8");

		return schema + "://" + host + ":" + port + "/image/user_male_portrait?img_id=" + portraitId + "&img_id_token=" + imageId;

	}

	/**
	 * From the json representing the user builds the fullname (which is missing, so firstname, lastname and middlename will be used)
	 * @param userJSON
	 * @return String representing the fullname
	 */
	private String buildFullName(JSONObject userJSON) {
		String first = (String)userJSON.get("firstName");
		String middle = (String)userJSON.get("middleName");
		String last = (String)userJSON.get("lastName");
		String fullname =
				(first == null | first.isEmpty() ? "" : first + " ") +
				(middle == null | middle.isEmpty() ? "" : middle + " ") +
				(last == null | last.isEmpty() ? "" : last);
		fullname = fullname.endsWith(" ") ? fullname.substring(0, fullname.length() - 1) : fullname;
		logger.info("Built fullname is " + fullname);
		return fullname;
	}

	/**
	 * Given the contactId value, retrieves the json object related to this information
	 * @param contactId
	 * @return
	 */
	private String getContactJson(long contactId) {

		return HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_CONTACT_BY_USER_ID.replace("$CONTACT_ID", String.valueOf(contactId)), 
				credsProvider, localContext, target);
	}

	@Override
	public GCubeUser createUser(boolean autoScreenName, String username,
			String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry,
			String backgroundSummary, boolean male, String reminderQuestion,
			String reminderAnswer) throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeUser createUser(boolean autoScreenName, String username,
			String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry,
			String backgroundSummary, boolean male, String reminderQuestion,
			String reminderAnswer, boolean sendEmail, boolean forcePasswordReset)
					throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeUser createUser(boolean autoScreenName, String username,
			String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry,
			String backgroundSummary, boolean male, String reminderQuestion,
			String reminderAnswer, boolean sendEmail,
			boolean forcePasswordReset, byte[] portraitBytes)
					throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeUser createUser(boolean autoScreenName, String username,
			String email, String firstName, String middleName, String lastName,
			String jobTitle, String location_industry,
			String backgroundSummary, boolean male, String reminderQuestion,
			String reminderAnswer, boolean sendEmail,
			boolean forcePasswordReset, byte[] portraitBytes, String mySpacesn,
			String twittersn, String facebooksn, String skypesn,
			String jabbersn, String aimsn) throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeUser getUserByUsername(String username)
			throws UserManagementSystemException, UserRetrievalFault {

		String jsonUser = 
				HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USER_BY_USERNAME.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$USER_ID", username), 
						credsProvider, localContext, target);

		if(jsonUser != null){
			logger.debug("Json user retrieved");
			return mapLRUser(jsonUser);
		}else
			return null;

	}

	@Override
	public GCubeUser getUserByScreenName(String username)
			throws UserManagementSystemException, UserRetrievalFault {
		return getUserByUsername(username);
	}

	@Override
	public GCubeUser getUserByEmail(String email)
			throws UserManagementSystemException, UserRetrievalFault {
		String jsonUser = 
				HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USER_BY_EMAIL.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$EMAIL", email), 
						credsProvider, localContext, target);

		if(jsonUser != null){
			logger.debug("Json user retrieved");
			return mapLRUser(jsonUser);
		}else
			return null;
	}

	@Override
	public GCubeUser getUserById(long userId)
			throws UserManagementSystemException, UserRetrievalFault {

		String jsonUser = 
				HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USER_BY_ID.replace("$USER_ID", String.valueOf(userId)), 
						credsProvider, localContext, target);

		if(jsonUser != null){
			logger.debug("Json user retrieved");
			return mapLRUser(jsonUser);
		}else
			return null;

	}

	@Override
	public long getUserId(String username)
			throws UserManagementSystemException, UserRetrievalFault {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUserProfessionalBackground(long userId)
			throws UserManagementSystemException, UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserProfessionalBackground(long userId, String summary)
			throws UserManagementSystemException, UserRetrievalFault {
		// TODO Auto-generated method stub

	}

	@Override
	public List<GCubeUser> listUsers() throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeUser> listUsers(boolean indexed)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<GCubeUser> listUsersByGroup(long groupId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {

		try{
			final List<GCubeUser> toReturn = new ArrayList<GCubeUser>();
			String jsonUsers = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USERS_BY_GROUP.replace("$GROUP_ID", String.valueOf(groupId)), 
							credsProvider, localContext, target);

			if(jsonUsers != null){

				logger.debug("Trying to parse json users array ");
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(jsonUsers);

				// use executors to speed up this process
				int numberOfThreads = (int) Math.ceil((double)array.size()/(double)USERS_EXECUTOR_FACTOR);
				logger.trace("Number of concurrent threads is going to be " + numberOfThreads);

				// let do the job by some threads
				ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
				int start = 0;
				int offset = USERS_EXECUTOR_FACTOR;

				for(int i = 0; i < numberOfThreads; i++){
					start = USERS_EXECUTOR_FACTOR * i;
					offset = (start + offset) > array.size() ? array.size() - start: offset;
					logger.trace("Start = " + start + ", offset=" + offset);
					final List usersPortion = array.subList(start, start + offset);

					executor.submit(new Runnable() {
						@Override
						public void run() {
							logger.debug("Thread is " + Thread.currentThread().getName());

							ArrayList<GCubeUser> localList = new ArrayList<GCubeUser>();
							for(int i = 0; i < usersPortion.size(); i++){
								localList.add(mapLRUser(((JSONObject)usersPortion.get(i)).toJSONString()));
							}

							// avoid contention on a single retrieved value but add them all at the end
							synchronized (toReturn){
								toReturn.addAll(localList);
							}
						}
					});
				}

				// wait threads to finish
				executor.shutdown();
				executor.awaitTermination(2, TimeUnit.MINUTES);

			}else
				return null;

			return toReturn;
		}catch(Exception e){
			logger.error("Something went wrong, sorry", e);
			return null;
		}
	}

	@Override
	public List<GCubeUser> listUsersByGroup(long groupId, boolean indexed)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeUser> listUsersByGroupName(String name)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<GCubeUser> getUserContactsByGroup(long userId, long scopeGroupId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeMembershipRequest> listMembershipRequestsByGroup(
			long groupId) throws UserManagementSystemException,
			GroupRetrievalFault, UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeMembershipRequest getMembershipRequestsById(
			long membershipRequestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeMembershipRequest> getMembershipRequests(long userId,
			long groupId, MembershipRequestStatus status)
					throws UserManagementSystemException, GroupRetrievalFault,
					UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeMembershipRequest requestMembership(long userId, long groupId,
			String comment) throws UserManagementSystemException,
			GroupRetrievalFault, UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeMembershipRequest acceptMembershipRequest(long requestUserId,
			long groupId, boolean addUserToGroup, String replyUsername,
			String replyComment) throws UserManagementSystemException,
			GroupRetrievalFault, UserManagementPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeMembershipRequest rejectMembershipRequest(long userId,
			long groupId, String replyUsername, String replyComment)
					throws UserManagementSystemException, GroupRetrievalFault,
					UserManagementPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<GCubeUser, List<GCubeRole>> listUsersAndRolesByGroup(long groupId)
			throws GroupRetrievalFault, UserManagementSystemException,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeUser> listUsersByGroupAndRole(final long groupId, final long roleId)
			throws UserManagementSystemException, RoleRetrievalFault,
			GroupRetrievalFault, UserRetrievalFault {

		final List<GCubeUser> toReturn = new ArrayList<GCubeUser>(0);

		try {

			final List<GCubeUser> usersInGroup = listUsersByGroup(groupId);
			logger.debug("Number of users is " + usersInGroup.size());

			if(usersInGroup == null || usersInGroup.isEmpty())
				return toReturn;

			// use executors to speed up this process
			int numberOfThreads = (int) Math.ceil((double)usersInGroup.size()/(double)USERS_EXECUTOR_FACTOR);
			logger.trace("Number of concurrent threads is going to be " + numberOfThreads);

			// let do the job by some threads
			ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
			int start = 0;
			int offset = USERS_EXECUTOR_FACTOR;

			for(int i = 0; i < numberOfThreads; i++){
				start = USERS_EXECUTOR_FACTOR * i;
				offset = (start + offset) > usersInGroup.size() ? usersInGroup.size() - start: offset;
				logger.trace("Start = " + start + ", offset=" + offset);
				final List<GCubeUser> usersPortion = usersInGroup.subList(start, start + offset);

				executor.submit(new Runnable() {

					@Override
					public void run() {
						logger.debug("Thread is " + Thread.currentThread().getName());
						ArrayList<GCubeUser> localUsersHavingRole = new ArrayList<GCubeUser>(0);

						for(GCubeUser user: usersPortion){
							long userId = user.getUserId();
							String userRoles = 
									HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_ROLES_IN_GROUP_BY_USER.replace("$GROUP_ID", String.valueOf(groupId)).replace("$USER_ID", String.valueOf(userId)), 
											credsProvider, localContext, target);

							boolean userHasRole = false;
							JSONParser parser = new JSONParser();
							JSONArray array;
							try {
								array = (JSONArray)parser.parse(userRoles);

								for (int i = 0; i < array.size(); i++) {
									try {
										GCubeRole role = LiferayWSRoleManager.mapLRRole(array.get(i).toString());
										if(role.getRoleId() == roleId){
											userHasRole = true;
											break;
										}
									} catch (PortalException | SystemException | ParseException e) {
										logger.warn("Failed to retrieve a role for user " + user.getUsername());
									}
								}

								// add if he/she has
								if(userHasRole)
									localUsersHavingRole.add(user);

							} catch (ParseException e1) {
								logger.warn("Failed to parse role for user " + user.getUsername());
							}
						}

						// avoid contention on a single retrieved value but add them all at the end
						synchronized (toReturn) {
							toReturn.addAll(localUsersHavingRole);
						}
					}
				});
			}

			// wait threads to finish
			executor.shutdown();
			executor.awaitTermination(2, TimeUnit.MINUTES);

		} catch (Exception e) {
			logger.warn("Failed to retrieve users that have role whit id " + roleId + " in group with id " + groupId);
			return null;
		}

		return toReturn;
	}

	@Override
	public List<GCubeUser> listUsersByTeam(long teamId)
			throws UserManagementSystemException, TeamRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignUserToGroup(long groupId, long userId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault, UserManagementPortalException {
		// TODO Auto-generated method stub

	}

	@Override
	public void dismissUserFromGroup(long groupId, long userId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub

	}

	@Override
	public List<GCubeUser> listUnregisteredUsersByGroup(long groupId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPasswordChanged(String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean userExistsByEmail(String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFullNameFromEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUserByEMail(String email)
			throws UserManagementSystemException,
			UserManagementPortalException, PortalException, SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] getUserAvatarBytes(String screenName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserOpenId(String screenName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateContactInformation(String screenName,
			String mySpacesn, String twittersn, String facebooksn,
			String skypesn, String jabbersn, String aimsn) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateJobTitle(long userId, String theJob) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Serializable readCustomAttr(long userId, String attributeKey)
			throws UserRetrievalFault {
		String toReturn = null;
		try{
			String jsonCustomField = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USER_CUSTOM_FIELD_BY_KEY.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$CUSTOM_FIELD_KEY", attributeKey).replace("$USER_ID", String.valueOf(userId)), 
							credsProvider, localContext, target);
			if(jsonCustomField != null){
				logger.debug("Trying to parse custom field in json object");
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(jsonCustomField);
				toReturn = (String)obj.get("data");
			}

		}catch(Exception e){
			logger.error("Something went wrong, sorry", e);
			return null;
		}

		return toReturn;
	}

	@Override
	public void saveCustomAttr(long userId, String attributeKey,
			Serializable value) throws UserRetrievalFault {
		// TODO Auto-generated method stub
	}

	@Override
	public List<GCubeUser> listUsersByGlobalRole(long roleId) {

		List<GCubeUser> toReturn = null;

		try{
			String listIds = HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_IDS_USERS_HAVING_GLOBAL_ROLE.replace("$ROLE_ID", String.valueOf(roleId)), 
					credsProvider, localContext, target);

			if(listIds != null){
				toReturn = new ArrayList<GCubeUser>();

				JSONParser parser = new JSONParser();
				JSONArray ids = (JSONArray)parser.parse(listIds);

				for (int i = 0; i < ids.size(); i++) {
					try {
						toReturn.add(getUserById(Long.valueOf(ids.get(i).toString())));
					} catch (Exception e) {
						logger.warn("Failed to retrieve user information", e);
					}
				}

			}

		}catch(Exception e){
			logger.error("Failed to retrieve the list");
			return null;
		}

		return toReturn;

	}

	@Override
	public List<Long> getUserIdsByGroup(long groupId) {

		List<Long> toReturn = null;

		try{

			String listIds = HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USER_ID_BY_GROUP.replace("$GROUP_ID", String.valueOf(groupId)), 
					credsProvider, localContext, target);

			if(listIds != null){
				toReturn = new ArrayList<Long>();

				JSONParser parser = new JSONParser();
				JSONArray ids = (JSONArray)parser.parse(listIds);

				for (int i = 0; i < ids.size(); i++) {
					try {
						toReturn.add(Long.valueOf(ids.get(i).toString()));
					} catch (Exception e) {
						logger.warn("Failed to retrieve user information", e);
					}
				}

			}

		}catch(Exception e){
			logger.error("Failed to retrieve the list of identifiers", e);
			return null;
		}

		return toReturn;

	}

	@Override
	public List<GCubeUser> listUsers(int start, int end) throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUsersCount() throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<GCubeUser> searchUsersByGroup(String keywords, long groupId) throws GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeUser> listUsersByGroup(long groupId, int start, int end)
			throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupUsersCount(long groupId) throws GroupRetrievalFault {
		// TODO Auto-generated method stub
		return 0;
	}

}
