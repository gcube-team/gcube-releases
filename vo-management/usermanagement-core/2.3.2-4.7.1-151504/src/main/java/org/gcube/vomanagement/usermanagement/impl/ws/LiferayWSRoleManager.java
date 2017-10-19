package org.gcube.vomanagement.usermanagement.impl.ws;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.ws.utils.HttpUtils;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

public class LiferayWSRoleManager implements RoleManager{

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
	private static final String GET_USER_ROLES_IN_GROUP = "/role/get-user-group-roles/user-id/$USER_ID/group-id/$GROUP_ID";
	private static final String GET_ROLE_BY_NAME = "/role/get-role/company-id/$COMPANY_ID/name/$NAME";

	// logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LiferayWSRoleManager.class);


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
	public LiferayWSRoleManager(String user, String password, String host, String schema, int port) throws Exception{

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

	//simple role mapping
	protected static GCubeRole mapLRRole(String jsonRole) throws PortalException, SystemException, ParseException {
		logger.debug("Json object for role is " + jsonRole);
		if (jsonRole != null && !jsonRole.isEmpty()) {
			JSONParser parser = new JSONParser();
			JSONObject jsonRoleObject = (JSONObject)parser.parse(jsonRole);
			return new GCubeRole((Long)jsonRoleObject.get("roleId"), (String)jsonRoleObject.get("name"), (String)jsonRoleObject.get("description"));
		}
		else 
			return null;
	}

	@Override
	public boolean isAdmin(long userId) throws UserRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRole(long userId, long groupId, long roleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasTeam(long userId, long teamId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assignRoleToUser(long userId, long groupId, long roleId)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, RoleRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assignRolesToUser(long userId, long groupId, long[] roleId)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, RoleRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createRole(String roleName, String roleDescription)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRole(long roleId)
			throws UserManagementSystemException, RoleRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeRoleFromUser(long userId, long groupId, long roleId)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, RoleRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllRolesFromUser(long userId, long... groupIds)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GCubeRole updateRole(long roleId, String roleName,
			String roleDescription) throws RoleRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeRole getRole(long roleId) throws UserManagementSystemException,
	RoleRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeRole getRole(String roleName, long groupId)
			throws RoleRetrievalFault, GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getRoleId(String roleName, long groupId)
			throws RoleRetrievalFault, GroupRetrievalFault {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getRoleIdByName(String roleName) throws RoleRetrievalFault {

		String jsonRole = 
				HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_ROLE_BY_NAME.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$NAME", roleName), 
						credsProvider, localContext, target);

		logger.debug("Json returned is " + jsonRole);

		if(jsonRole != null){
			try{
				return mapLRRole(jsonRole).getRoleId();
			}catch(Exception e){
				logger.error("Exception while retrieving a role by name", e);
			}

		}

		return -1;
	}

	@Override
	public List<GCubeRole> listAllRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeRole> listAllGroupRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeRole> listRolesByUserAndGroup(long userId, long groupId)
			throws GroupRetrievalFault, UserRetrievalFault {

		List<GCubeRole> toReturn = new ArrayList<GCubeRole>();
		String jsonRoles = 
				HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_USER_ROLES_IN_GROUP.replace("$GROUP_ID", String.valueOf(groupId)).replace("$USER_ID", String.valueOf(userId)), 
						credsProvider, localContext, target);

		if(jsonRoles != null){
			try{

				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(jsonRoles);
				for (int i = 0; i < array.size(); i++) {

					toReturn.add(mapLRRole(array.get(i).toString()));

				}

			}catch(Exception e){
				logger.error("Exception while retrieving list of roles in group", e);
			}
		}

		return toReturn;
	}

	@Override
	public GCubeTeam createTeam(long creatorUserId, long groupId,
			String teamName, String teamDescription)
					throws GroupRetrievalFault, TeamRetrievalFault,
					UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeTeam createTeam(long groupId, String teamName,
			String teamDescription) throws GroupRetrievalFault,
			TeamRetrievalFault, UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeTeam getTeam(long groupId, String teamName)
			throws GroupRetrievalFault, TeamRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeTeam getTeam(long teamId) throws UserManagementSystemException,
	TeamRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeTeam deleteTeam(long teamId)
			throws UserManagementSystemException, TeamRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteTeams(long groupId)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUserTeams(long userId, long[] teamIds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUserTeams(long userId, List<GCubeTeam> teams) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GCubeTeam updateTeam(long teamId, String teamName,
			String teamDescription) throws TeamRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setUserTeams(long userId, long[] teamIds)
			throws TeamRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assignTeamToUser(long userId, long teamId)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, TeamRetrievalFault {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<GCubeTeam> listTeamsByGroup(long groupId)
			throws GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeTeam> listTeamsByUserAndGroup(long userId, long groupId)
			throws UserRetrievalFault, GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

}
