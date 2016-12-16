package org.gcube.portal.custom.communitymanager;

import java.util.List;

import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;

import com.liferay.portal.model.Portlet;
/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 * @version 1.0 - Jan 4 2012
 *
 */
public interface CommunityManager {
	/**
	 * 
	 * @param communityName .
	 * @param communityDesc .
	 * @param parentID .
	 * @return the newly created CommunityID
	 */
	long createCommunity(String communityName, String communityDesc, long parentID) ;
	/**
	 * 
	 * @param usernameCreator .
	 * @param communityName .
	 * @param communityDesc .
	 * @param siteLayout .
	 * @param parentID -
	 * @return the newly created CommunityID
	 */
	long createCommunity(String usernameCreator, String communityName, String communityDesc,	GCUBESiteLayout siteLayout, long parentID) ;
	/**
	 * @param communityName -
	 * @return list of belonging portlet
	 */
	List<Portlet> getGCubePortlets(String communityName);	
}
