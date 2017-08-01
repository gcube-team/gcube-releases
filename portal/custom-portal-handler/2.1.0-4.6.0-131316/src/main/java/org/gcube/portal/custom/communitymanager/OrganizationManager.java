package org.gcube.portal.custom.communitymanager;

import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;

import com.liferay.portal.model.Organization;


/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 *
 */
public interface OrganizationManager {

	/**
	 * 
	 * @param rootVoName the voName
	 * @param voDesc -
	 * @return the id of the created VO
	 */
	long createVO(String rootVoName, String voDesc, GCUBESiteLayout siteLayout,  String themeid);
	/**
	 * 
	 * @param voName the voName
	 * @param voDesc -
	 * @return the id of the created VO
	 */
	long createVO(String voName,  String voDesc, long parentID, GCUBESiteLayout siteLayout, String themeid);
	
	/**
	 * 
	 * @param rootVoName the voName
	 * @param voDesc -
	 * @return the organizationid of the created VO
	 */
	long createVRE(String voName, String voDesc, long parentid, GCUBESiteLayout siteLayout, String themeid);
	/**
	 * 
	 * @param username the screenname of the current user
	 * @param currOrg the organization in which to check the custom attribute
	 * @param attrToCheck the key to check
	 * @return true or false
	 */
	Boolean readOrganizationCustomAttribute(String username, Organization currOrg, String attrToCheck);

}
