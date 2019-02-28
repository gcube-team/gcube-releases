package org.gcube.gcat.persistence.ckan;

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
	
}
