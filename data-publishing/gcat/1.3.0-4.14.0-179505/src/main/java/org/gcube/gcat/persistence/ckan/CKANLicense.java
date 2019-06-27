package org.gcube.gcat.persistence.ckan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANLicense extends CKAN {
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.license_list
	public static final String LICENSES_LIST = CKAN.CKAN_API_PATH + "license_list";
	
	public CKANLicense() {
		super();
		LIST = LICENSES_LIST; 
	}
	
	protected static ArrayNode getLicenses() {
		CKANLicense ckanLicense = new CKANLicense();
		ckanLicense.list(-1,-1);
		ArrayNode arrayNode = (ArrayNode) ckanLicense.getJsonNodeResult();
		return arrayNode;
	}

	public static boolean checkLicenseId(String licenseId) throws Exception {
		return checkLicenseId(getLicenses(), licenseId);
	}
	
	// TODO Use a Cache
	protected static boolean checkLicenseId(ArrayNode arrayNode, String licenseId) throws Exception {
		try {
			for(JsonNode jsonNode : arrayNode) {
				try {
					String id = jsonNode.get(ID_KEY).asText();
					if(id.compareTo(licenseId)==0) {
						return true;
					}
				}catch (Exception e) {
					
				}
			}
			return false;
		}catch (Exception e) {
			throw e;
		}
	}
	
}
