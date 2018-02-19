package org.gcube.vomanagement.usermanagement.impl.ws;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementNameException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.VirtualGroupNotExistingException;
import org.gcube.vomanagement.usermanagement.impl.ws.utils.HttpUtils;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GroupMembershipType;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Exploit Liferay JSON Web Service to perform GroupManager's operations.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LiferayWSGroupManager implements GroupManager {

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
	private static final String GET_GROUP_BY_NAME = "/group/get-group/company-id/$COMPANY_ID/name/$GROUP_NAME";
	private static final String GET_GROUP_BY_ID = "/group/get-group/group-id/$GROUP_ID";
	private static final String GET_GROUPS_BY_PARENT_ID = "/group/get-groups/company-id/$COMPANY_ID/parent-group-id/$GROUP_PARENT_ID/site/$SITE";
	private static final String GET_GROUPS_BY_USERID = "/group/get-user-sites-groups/user-id/$USER_ID/class-names/%5B%22com.liferay.portal.model.Group%22%5D/max/$MAX_GROUP";
	private static final String GET_GROUP_CUSTOM_FIELDS = "/expandovalue/get-data/company-id/$COMPANY_ID/class-name/com.liferay.portal.model.Group/table-name/CUSTOM_FIELDS/column-name/$CUSTOM_KEY/class-pk/$GROUP_ID";

	// logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LiferayWSGroupManager.class);

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
	public LiferayWSGroupManager(String user, String password, String host, String schema, int port) throws Exception{

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
	 * Map a json object representing a group to a GCubeGroup object
	 * @param jsonGroup
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	private GCubeGroup mapLRGroup(String jsonGroup) throws PortalException, SystemException, UserManagementSystemException, GroupRetrievalFault {

		try{
			if (jsonGroup != null) {
				JSONParser parser = new JSONParser();
				JSONObject jsonGroupObject = (JSONObject)parser.parse(jsonGroup);
				long logoId = 0; // TODO
				long groupId = (long)jsonGroupObject.get("groupId");

				// faster way to determine if it is a VRE or a VO or the ROOT Vo
				String threePath = (String)jsonGroupObject.get("treePath");

				if (isVREByTreePath(threePath)) {
					logger.debug("********** IS VRE");
					return new GCubeGroup(
							groupId, 
							(long)jsonGroupObject.get("parentGroupId"), 
							(String)jsonGroupObject.get("name"), 
							(String)jsonGroupObject.get("description"), 
							(String)jsonGroupObject.get("friendlyURL"), 
							logoId, 
							null, 
							getMappedGroupMembershipType(((Long)jsonGroupObject.get("type")).intValue()));
				}
				else if (isVOByTreePath(threePath)) {
					logger.debug("********** IS VO");
					List<GCubeGroup> vres = new ArrayList<GCubeGroup>();
					List<String> vreInJson = getChildren(groupId);
					if(vreInJson != null)
						for (String vreJson : vreInJson) {
							vres.add(mapLRGroup(vreJson));
						}
					return new GCubeGroup(
							groupId, 
							(long)jsonGroupObject.get("parentGroupId"), 
							(String)jsonGroupObject.get("name"), 
							(String)jsonGroupObject.get("description"), 
							(String)jsonGroupObject.get("friendlyURL"), 
							logoId, 
							vres, 
							getMappedGroupMembershipType(((Long)jsonGroupObject.get("type")).intValue()));
				} else if (isRootVOByTreePath(threePath)) {
					logger.debug("********** IS ROOT VO");
					List<GCubeGroup> vos = new ArrayList<GCubeGroup>();
					List<String> vosInJson = getChildren(groupId);
					if(vosInJson != null)
						for (String voInJson : vosInJson) 
							vos.add(mapLRGroup(voInJson));
					return new GCubeGroup(
							groupId, 
							(long)jsonGroupObject.get("parentGroupId"), // it is 0
							(String)jsonGroupObject.get("name"), 
							(String)jsonGroupObject.get("description"), 
							(String)jsonGroupObject.get("friendlyURL"), 
							logoId, 
							vos, 
							getMappedGroupMembershipType(((Long)jsonGroupObject.get("type")).intValue()));
				} else{
					logger.warn("This groupId does not correspond to a (root-)VO ora VRE");
					return null;
				}
			}
		}catch(Exception e){
			logger.error("There was an error while trying to map the group with json " + jsonGroup + " to the GcubeGroup class", e);
		}

		return null;
	}

	private boolean isVREByTreePath(String threePath) throws Exception {
		// faster way to determine if it is a VRE or a VO
		if(threePath == null || threePath.isEmpty())
			throw new Exception("threePath is missing");
		return threePath.split("/").length == 4; // e.g. for a VRE is  /21654/21657/21660/ (4)
	}

	private boolean isVOByTreePath(String threePath) throws Exception {
		if(threePath == null || threePath.isEmpty())
			throw new Exception("threePath is missing");
		return threePath.split("/").length == 3; // e.g. for a VO is  /21654/21657/ (3)
	}


	private boolean isRootVOByTreePath(String threePath) throws Exception {
		if(threePath == null || threePath.isEmpty())
			throw new Exception("threePath is missing");
		return threePath.split("/").length == 2; // e.g. for a Root VO is  /21654/ (2)
	}

	/**
	 * Retrieve the threePath given the groupId of the group
	 * @param groupId
	 * @return
	 */
	private String getTreePathFromGroup(long groupId){

		try{

			String jsonGroup = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUP_BY_ID.replace("$GROUP_ID", String.valueOf(groupId)), 
							credsProvider, localContext, target);

			if(jsonGroup != null){
				JSONParser parser = new JSONParser();
				JSONObject jsonGroupObject = (JSONObject)parser.parse(jsonGroup);
				return (String)jsonGroupObject.get("treePath");
			}else
				return null;

		}catch(Exception e){
			logger.error("Failed to retrieve threePath information", e);
			return null;
		}

	}

	/**
	 * Retrieve the group children of the group having  id groupId
	 * @param groupId
	 * @return Json representions of groups
	 */
	private List<String> getChildren(long groupId) {
		List<String> jsonChildren = new ArrayList<String>();
		try{
			String jsonGroups = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUPS_BY_PARENT_ID.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$GROUP_PARENT_ID", String.valueOf(groupId))
							.replace("$SITE", Boolean.toString(true)), 
							credsProvider, localContext, target);

			if(jsonGroups != null){
				logger.debug("***** CHILDREN GROUP SET IS " + jsonGroups);
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(jsonGroups);
				for (int i = 0; i < array.size(); i++) {
					jsonChildren.add(((JSONObject)array.get(i)).toJSONString());
				}
				return jsonChildren;
			}
		}catch(Exception e){
			logger.error("Error while returning the children of the group with id " + groupId, e);
		}
		return null;
	}

	/**
	 * 
	 * @param type
	 * @return the correspondent mapping to the gcube model
	 */
	private GroupMembershipType getMappedGroupMembershipType(int type) {
		switch (type) {
		case 2:
			return GroupMembershipType.RESTRICTED;
		case 1:
			return GroupMembershipType.OPEN;
		default:
			return GroupMembershipType.PRIVATE;
		}
	}

	@Override
	public GCubeGroup createRootVO(String rootVOName, String description)
			throws UserManagementNameException, UserManagementSystemException,
			UserRetrievalFault, GroupRetrievalFault,
			UserManagementPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeGroup createVO(String virtualOrgName, long rootVOGroupId,
			String description) throws UserManagementNameException,
			UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, UserManagementPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCubeGroup createVRE(String virtualResearchEnvName,
			long virtualOrgGroupId, String description)
					throws UserManagementNameException, UserManagementSystemException,
					UserRetrievalFault, GroupRetrievalFault,
					UserManagementPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGroupParentId(long groupId)
			throws UserManagementSystemException, GroupRetrievalFault {
		try {
			String jsonGroup = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUP_BY_ID.replace("$GROUP_ID", String.valueOf(groupId)), 
							credsProvider, localContext, target);
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(jsonGroup);
			return (long)obj.get("parentGroupId");
		}catch (Exception e) {
			logger.error("Unable to determine the parent group id of the group with id " + groupId);
		}
		return -1;
	}

	@Override
	public long getGroupId(String groupName)
			throws UserManagementSystemException, GroupRetrievalFault {
		try{
			String jsonGroup = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUP_BY_NAME.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$GROUP_NAME", groupName), 
							credsProvider, localContext, target);

			if(jsonGroup != null){
				logger.debug("Trying to parse json group object");
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(jsonGroup);
				return (Long)obj.get("groupId");
			}else
				return -1;
		}catch(Exception e){
			logger.error("Error while retrieving the group id, returning -1", e);
		}
		return -1;
	}

	@Override
	public GCubeGroup getGroup(long groupId)
			throws UserManagementSystemException, GroupRetrievalFault {
		try{
			String jsonGroup = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUP_BY_ID.replace("$GROUP_ID", String.valueOf(groupId)), 
							credsProvider, localContext, target);

			if(jsonGroup != null){
				return mapLRGroup(jsonGroup);
			}else
				return null;

		}catch(Exception e){
			logger.error("Error while retrieving the group id, returning null", e);
		}
		return null;
	}

	@Override
	public List<VirtualGroup> getVirtualGroups()
			throws VirtualGroupNotExistingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VirtualGroup> getVirtualGroups(long actualGroupId)
			throws GroupRetrievalFault, VirtualGroupNotExistingException {
		List<VirtualGroup> toReturn = new ArrayList<VirtualGroup>();

		try {

			String jsonCustomFields = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUP_CUSTOM_FIELDS.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$CUSTOM_KEY", CustomAttributeKeys.VIRTUAL_GROUP.getKeyName()).replace("$GROUP_ID", String.valueOf(actualGroupId)), 
							credsProvider, localContext, target);

			if(jsonCustomFields != null){

				//parse this array
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(jsonCustomFields);

				for (int i = 0; i < array.size(); i++) {
					String obj = (String)array.get(i);
					String[] splits = obj.split("\\|");
					String gName = splits[0];
					String gDescription = splits[1];
					toReturn.add(new VirtualGroup(gName, gDescription));
				}					
			}

			return toReturn;
		} catch (Exception e) {
			logger.error("Failed to read Virtualgroups for this group", e);
		}

		return null;

	}

	@Override
	public long getGroupIdFromInfrastructureScope(String scope)
			throws IllegalArgumentException, UserManagementSystemException,
			GroupRetrievalFault {

		logger.debug("called getGroupIdFromInfrastructureScope on " + scope);
		if(!scope.startsWith("/")){
			throw new IllegalArgumentException("Scope should start with '/' ->" + scope);
		}

		if(scope.endsWith("/")){
			throw new IllegalArgumentException("Scope should not end with '/' ->" + scope);
		}

		// splits this scope
		String[] splits = scope.split("/");
		if (splits.length > 4)
			throw new IllegalArgumentException("Scope is invalid, too many '/' ->" + scope);

		if (splits.length == 2) //is a root VO 
			return getGroupId(splits[1]);

		else if (splits.length == 3) {//is a VO 
			try {
				long parentGroupId = getGroupId(splits[1]); // get the root
				List<String> vosInJson = null;
				vosInJson = getChildren(parentGroupId); // get the vos
				return checkChildrenAndReturnId(vosInJson, splits[2]);
			} catch (Exception e) {
				logger.error("Failed to retrieve the group id for this context", e);
			}
		}
		else if (splits.length == 4) {//is a VRE 
			try {
				logger.debug("is a VRE scope " + scope);
				long parentGroupId = getGroupId(splits[2]); // get the vo
				List<String> vresInJson = null;
				vresInJson = getChildren(parentGroupId); // get the vres
				return checkChildrenAndReturnId(vresInJson ,splits[3]);
			} catch (Exception e) {
				logger.error("Failed to retrieve the group id for this context", e);
			}

		}
		return -1;
	}

	/**
	 * Among the groups, find -if any- the one that has the name nameToFind and return its id.
	 * @param groups
	 * @param nameToFind
	 * @return
	 * @throws ParseException
	 */
	private long checkChildrenAndReturnId(List<String> groups, String nameToFind) throws ParseException {
		JSONParser parser = new JSONParser();
		for (String group : groups) {
			JSONObject obj = (JSONObject)parser.parse(group);
			if(obj.get("name").equals(nameToFind))
				return (long) obj.get("groupId");
		}
		return -1;
	}

	@Override
	public GCubeGroup getRootVO() throws UserManagementSystemException,
	GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRootVOName() throws UserManagementSystemException,
	GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfrastructureScope(long groupId)
			throws UserManagementSystemException, GroupRetrievalFault {
		try {
			GCubeGroup group = getGroup(groupId);
			String treePath = getTreePathFromGroup(group.getGroupId());
			if (isVREByTreePath(treePath)){
				long voId = group.getParentGroupId();
				GCubeGroup voGroup = getGroup(voId);
				long rootVoId = voGroup.getParentGroupId();
				String rootGroupName = getGroup(rootVoId).getGroupName();
				return "/" + rootGroupName + "/" + voGroup.getGroupName() + "/" + group.getGroupName();
			}
			if (isVOByTreePath(treePath)){
				String rootVoName = getGroup(group.getParentGroupId()).getGroupName();
				return "/" + rootVoName + "/" + group.getGroupName();
			}
			if (isRootVOByTreePath(treePath)) 
				return "/"+group.getGroupName();

		}catch (Exception e) {
			logger.error("Unable to retrieve the Infrastructure scope for group id " + groupId);
		} 
		return null;
	}

	@Override
	public String getScope(long groupId) throws UserManagementSystemException,
	GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeGroup> listGroups() throws UserManagementSystemException,
	GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeGroup> listGroupsByUser(long userId)
			throws UserRetrievalFault, UserManagementSystemException,
			GroupRetrievalFault {
		List<GCubeGroup> toReturn = new ArrayList<GCubeGroup>();
		try{
			String jsonGroups = // TODO evaluate the max number of groups to return before, somehow
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUPS_BY_USERID.replace("$USER_ID", String.valueOf(userId)).replace("$MAX_GROUP", String.valueOf(1000)), 
							credsProvider, localContext, target);

			if(jsonGroups != null){
				logger.debug("Trying to parse json object");
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(jsonGroups);
				for (int i = 0; i < array.size(); i++) {
					toReturn.add(mapLRGroup(((JSONObject)array.get(i)).toJSONString()));	
				}
			}else
				return null;
		}catch(Exception e){
			logger.error("Error while retrieving the group id, returning -1", e);
		}
		return toReturn;
	}

	@Override
	public List<GCubeGroup> listVresByUser(long userId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		List<GCubeGroup> toReturn = new ArrayList<GCubeGroup>();
		try{
			String jsonGroups = // TODO evaluate the max number of groups to return before, somehow
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUPS_BY_USERID.replace("$USER_ID", String.valueOf(userId)).replace("$MAX_GROUP", String.valueOf(1000)), 
							credsProvider, localContext, target);

			if(jsonGroups != null){
				logger.debug("Trying to parse json object");
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(jsonGroups);
				for (int i = 0; i < array.size(); i++) {
					String threePath = (String)((JSONObject)array.get(i)).get("treePath");
					if(isVREByTreePath(threePath))
						toReturn.add(mapLRGroup(((JSONObject)array.get(i)).toJSONString()));	
				}
				return toReturn;
			}
		}catch(Exception e){
			logger.error("Error while retrieving the group id, returning -1", e);
		}

		return null;
	}

	@Override
	public Set<GCubeGroup> listGroupsByUserAndSite(long userId,
			String serverName) throws UserRetrievalFault,
			UserManagementSystemException, GroupRetrievalFault,
			VirtualGroupNotExistingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<GCubeGroup> listGroupsByUserAndSiteGroupId(long userId,
			long siteGroupId) throws UserRetrievalFault,
			UserManagementSystemException, GroupRetrievalFault,
			VirtualGroupNotExistingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<GCubeGroup, List<GCubeRole>> listGroupsAndRolesByUser(long userId)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isRootVO(long groupId) throws UserManagementSystemException,
	GroupRetrievalFault {
		try {
			long groupParentId = getGroupParentId(groupId);
			return (groupParentId == 0);
		} catch (Exception e) {
			logger.error("Error while checking if group with id " + groupId + " is the root VO");
		} 
		return false;
	}

	@Override
	public Boolean isVO(long groupId) throws UserManagementSystemException,
	GroupRetrievalFault {
		try {
			long groupParentId = getGroupParentId(groupId);
			if (groupParentId != 0) {
				return !isVRE(groupId);
			}
		} catch (Exception e) {
			logger.error("Error while checking if group with id " + groupId + " is a VO");
		} 
		return false;
	}

	@Override
	public Boolean isVRE(long groupId) throws UserManagementSystemException,
	GroupRetrievalFault {
		try {
			long groupParentId = getGroupParentId(groupId);
			if (groupParentId != 0) {
				return getGroupParentId(groupParentId) != 0; 
			}
		} catch (Exception e) {
			logger.error("Error while checking if group with id " + groupId + " is a VRE");
		} 
		return false;
	}

	@Override
	public Serializable readCustomAttr(long groupId, String attributeKey)
			throws GroupRetrievalFault {

		String result = 
				HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUP_CUSTOM_FIELDS.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$GROUP_ID", String.valueOf(groupId)).replace("$CUSTOM_KEY", attributeKey), 
						credsProvider, localContext, target);

		logger.debug("Data is " + result);

		return result;
	}

	@Override
	public void saveCustomAttr(long groupId, String attributeKey,
			Serializable value) throws GroupRetrievalFault {
		// TODO Auto-generated method stub

	}

	@Override
	public String updateGroupDescription(long groupId, String description)
			throws GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGroupLogoURL(long logoId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubeGroup> getGateways() {
		List<GCubeGroup> gateways = new ArrayList<GCubeGroup>();
		try{

			String jsonGroups = 
					HttpUtils.executeHTTPGETRequest(API_BASE_URL + GET_GROUPS_BY_PARENT_ID.replace("$COMPANY_ID", String.valueOf(companyId)).replace("$GROUP_PARENT_ID", String.valueOf(0))
							.replace("$SITE", Boolean.toString(true)), 
							credsProvider, localContext, target);

			JSONArray candidateGateways = (JSONArray)(new JSONParser().parse(jsonGroups));
			List<Long> idsCandidateGateways = new ArrayList<Long>();

			for (int i = 0; i < candidateGateways.size(); i++) {
				idsCandidateGateways.add((Long) ((JSONObject)(candidateGateways.get(i))).get("groupId"));	
			}

			// real gateways have no children as well
			for (int i = 0; i < idsCandidateGateways.size(); i++) {
				String friendlyUrl = (String) ((JSONObject)candidateGateways.get(i)).get("friendlyURL");

				// check if it is not a group created by Liferay
				boolean defaultGroup = friendlyUrl.equals("/guest") || friendlyUrl.equals("/global");

				if(defaultGroup)
					continue;

				List<String> children = getChildren(idsCandidateGateways.get(i));
				if(children == null || children.isEmpty())
					gateways.add(mapLRGroup(((JSONObject)candidateGateways.get(i)).toJSONString()));
			}

		}catch(Exception e){
			logger.error("Failed to retrieve the list of gateways", e);
			return null;
		}
		return gateways;
	}

}