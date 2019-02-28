package org.gcube.gcat.persistence.ckan;

import java.util.Map;
import java.util.Set;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.gcat.utils.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANOrganization extends CKAN {
	
	private static Logger logger = LoggerFactory.getLogger(CKANOrganization.class);
	
	// CKAN Connector sanitize the Organization name as following
	//organizationName.replaceAll(" ", "_").replace(".", "_").toLowerCase()
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.organization_list
	public static final String ORGANIZATION_LIST = CKAN.CKAN_API_PATH + "organization_list";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.organization_create
	public static final String ORGANIZATION_CREATE = CKAN.CKAN_API_PATH + "organization_create";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.organization_show
	public static final String ORGANIZATION_SHOW = CKAN.CKAN_API_PATH + "organization_show";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.organization_update
	public static final String ORGANIZATION_UPDATE = CKAN.CKAN_API_PATH + "organization_update";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.organization_patch
	public static final String ORGANIZATION_PATCH = CKAN.CKAN_API_PATH + "organization_patch";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.organization_delete
	public static final String ORGANIZATION_DELETE = CKAN.CKAN_API_PATH + "organization_delete";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.organization_purge
	public static final String ORGANIZATION_PURGE = CKAN.CKAN_API_PATH + "organization_purge";
	
	// see https://docs.ckan.org/en/latest/api/#ckan.logic.action.create.organization_member_create
	public static final String ORGANIZATION_MEMBER_CREATE = CKAN.CKAN_API_PATH + "organization_member_create";
	
	// https://docs.ckan.org/en/latest/api/index.html#ckan.logic.action.get.organization_list_for_user
	public static final String ORGANIZATION_LIST_FOR_USER = CKAN.CKAN_API_PATH + "organization_list_for_user";
	
	protected static final String USERNAME_KEY = "username";
	protected static final String ROLE_KEY = "role";
	
	
	public CKANOrganization() {
		super();
		LIST = ORGANIZATION_LIST;
		CREATE = ORGANIZATION_CREATE;
		READ = ORGANIZATION_SHOW;
		UPDATE = ORGANIZATION_UPDATE;
		PATCH = ORGANIZATION_PATCH;
		DELETE = ORGANIZATION_DELETE;
		PURGE = ORGANIZATION_PURGE;
	}
	
	protected static final String ORGANIZATION_PERMISSION_KEY = "permission"; 
	protected static final String ORGANIZATION_PERMISSION_VALUE_READ = "read";
	
	public String getUserRole(String gCubeUsername) {
		Map<String,Map<CkanOrganization,RolesCkanGroupOrOrg>> rolesPerOrganization = dataCatalogue
				.getUserRoleByOrganization(gCubeUsername, CKANUtility.getApiKey(gCubeUsername));
		if(rolesPerOrganization.containsKey(name)) {
			Map<CkanOrganization,RolesCkanGroupOrOrg> map = rolesPerOrganization.get(name);
			Set<CkanOrganization> ckanOrganizations = map.keySet();
			for(CkanOrganization ckanOrganization : ckanOrganizations) {
				return map.get(ckanOrganization).name().toLowerCase();
			}
		}
		return null;
	}
	
	public void addUserToOrganisation(String gCubeUsername, String role, boolean force) {
		String ckanUsername = CKANUtility.getCKANUsername(gCubeUsername);
		String userRole = getUserRole(gCubeUsername);
		if((userRole==null || force)) {
			if(userRole!=null && userRole.toLowerCase().compareTo(role.toLowerCase())==0) {
				logger.debug("User {} is already member of Organisation {} with role {}", ckanUsername, name, userRole);
				return;
			}
			ObjectNode objectNode = mapper.createObjectNode();
			objectNode.put(ID_KEY, name);
			objectNode.put(USERNAME_KEY, ckanUsername);
			objectNode.put(ROLE_KEY, role);
			sendPostRequest(ORGANIZATION_MEMBER_CREATE, getAsString(objectNode));
			logger.debug("User {} successfully added to Organisation {} with role {}", ckanUsername, name, role);
		}else {
			logger.debug("User {} is already member of Organisation {} with role {}", ckanUsername, name, userRole);
		}
	}
	
	public static String getCKANOrganizationName() {
		String context = ContextUtility.getCurrentContext();
		return getCKANOrganizationName(context);
	}
	
	public static String getCKANOrganizationName(String context) {
		ScopeBean scopeBean = new ScopeBean(context);
		return scopeBean.name().toLowerCase();
	}
}
