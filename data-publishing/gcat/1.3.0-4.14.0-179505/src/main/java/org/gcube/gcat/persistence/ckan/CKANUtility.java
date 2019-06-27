package org.gcube.gcat.persistence.ckan;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueRunningCluster;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.gcat.utils.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANUtility {
	
	private static final Logger logger = LoggerFactory.getLogger(CKANUtility.class);
	
	public static final String MEMBER_ROLE = "member";
	private static final String API_KEY = "apikey";
	
	public static DataCatalogue getCatalogue() throws Exception {
		String context = ContextUtility.getCurrentContext();
		logger.debug("Discovering ckan instance in context {}", context);
		return DataCatalogueFactory.getFactory().getUtilsPerScope(context);
	}
	
	public static String getSysAdminAPI() {
		try {
			DataCatalogueRunningCluster catalogueRunningInstance = new DataCatalogueRunningCluster(
					ContextUtility.getCurrentContext());
			return catalogueRunningInstance.getSysAdminToken();
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected static String getCKANUsername(String username) {
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);
		return ckanUsername;
	}
	
	public static String getCKANUsername() {
		return getCKANUsername(ContextUtility.getUsername());
	}
	
	public static JsonNode getCKANUser(boolean addToOrganization) {
		return createCKANUser(getCKANUsername(), addToOrganization);
	}
	
	public static JsonNode getCKANUser() {
		return getCKANUser(true);
	}
	
	protected static JsonNode createCKANUser(String ckanUsername, boolean addToOrganization) {
		ckanUsername = CKANUtility.getCKANUsername(ckanUsername);
		CKANUser ckanUser = new CKANUser();
		ckanUser.setApiKey(getSysAdminAPI());
		try {
			ckanUser.setName(ckanUsername);
			ckanUser.read();
		} catch(WebApplicationException e) {
			if(e.getResponse().getStatusInfo() == Status.NOT_FOUND) {
				ckanUser.setName(ckanUsername);
				ckanUser.create();
			}else {
				throw e;
			}
		} 
		JsonNode jsonNode = ckanUser.getJsonNodeResult();
		if(addToOrganization) {
			addUserToOrganization(ckanUsername, MEMBER_ROLE, false);
		}
		return jsonNode;
	}
	
	public static void addUserToOrganization(String ckanUsername, String organizationName, String role, boolean force) {
		CKANOrganization ckanOrganization = new CKANOrganization();
		ckanOrganization.setApiKey(getSysAdminAPI());
		ckanOrganization.setName(organizationName);
		ckanOrganization.addUserToOrganisation(ckanUsername, role, force);
	}
	
	protected static void addUserToOrganization(String ckanUsername, String role, boolean force) {
		String organizationName = CKANOrganization.getCKANOrganizationName();
		addUserToOrganization(ckanUsername, organizationName, role, force);
	}
	
	public static String getApiKey() throws Exception {
		String ckanUsername = getCKANUsername();
		return getApiKey(ckanUsername);
	}
	
	protected static String getApiKey(String ckanUsername) {
		try {
			String apiKey = getCatalogue().getApiKeyFromUsername(ckanUsername);
			if(apiKey == null) {
				JsonNode jsonNode = createCKANUser(ckanUsername, true);
				apiKey = jsonNode.get(API_KEY).asText();
			}
			return apiKey;
		}catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
}
