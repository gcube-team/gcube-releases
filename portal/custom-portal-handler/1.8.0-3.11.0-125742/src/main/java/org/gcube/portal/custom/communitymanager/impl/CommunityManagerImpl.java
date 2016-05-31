package org.gcube.portal.custom.communitymanager.impl;

import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portal.custom.communitymanager.CommunityManager;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.PortletsIdManager;
import org.gcube.portal.custom.communitymanager.components.GCUBELayoutTab;
import org.gcube.portal.custom.communitymanager.components.GCUBEPortlet;
import org.gcube.portal.custom.communitymanager.components.GCUBESiteLayout;
import org.gcube.portal.custom.communitymanager.types.GCUBELayoutType;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
/**
 * 
 * @author Massimiliano Assante, massimiliano.assante@isti.cnr.it
 * @version 1.0
 *
 */
public class CommunityManagerImpl extends OrganizationsUtil implements CommunityManager {
	
	private static Log _log = LogFactoryUtil.getLog(CommunityManagerImpl.class);
	/**
	 * 
	 */
	public static final String CURR_ORG = "CURR_RE_NAME";

	
	ASLSession session = null;
	/**
	 * 
	 */
	private String screenName;
	/**
	 * 
	 */
	private static CommunityManagerImpl singleton;

	/**
	 * 
	 * @return the singleton
	 */
	public static CommunityManagerImpl get() {
		return singleton;
	}
	/**
	 * 
	 * @param session the ASL session
	 */
	private CommunityManagerImpl(ASLSession session) {
		this.session = session;
		this.screenName = session.getUsername();
		singleton = this;
	}
	/**
	 * 
	 * @param session the session
	 * @return the singleton
	 */
	public static CommunityManagerImpl getInstance(ASLSession session) {			
		return new CommunityManagerImpl(session);		
	}

	/**
	 * Use this method for creating VRE programmatically and associate a default layout to it
	 * 
	 * @param communityName -
	 * @param communityDesc -
	 * @param parentID -
	 * @return the community created id
	 */
	public long createCommunity(String communityName, String communityDesc, long parentID) {
		String username = "";
		String email = "";
		GCUBESiteLayout siteLayout = null;
		try {			
			username = validateUser(screenName).getScreenName();	
			email = validateUser(screenName).getEmailAddress();	
				
		siteLayout = new GCUBESiteLayout(getCompany(), communityName, email);			
		siteLayout.addTab(new GCUBELayoutTab("Home", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("gCube Loggedin", PortletsIdManager.getLRPortletId(PortletsIdManager.GCUBE_LOGGEDIN))));
		siteLayout.addTab(new GCUBELayoutTab("Workspace", GCUBELayoutType.ONE_COL, 
				new GCUBEPortlet("gCube Workspace", PortletsIdManager.getLRPortletId(PortletsIdManager.WORKSPACE))));
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}	
		return createCommunity(username, communityName, communityDesc, siteLayout, parentID);

	}
	/**
	 * Use this method for creating VRE programmatically and associate it a layout
	 * 
	 * @param usernameCreator -
	 * @param communityName -
	 * @param communityDesc -
	 * @param siteLayout a <class>GCUBESiteLayout</class> instance to associate to it
	 * @param parentID the organization parent
	 * @return the community created id
	 */
	public long createCommunity(String usernameCreator, String communityName, String communityDesc,	GCUBESiteLayout siteLayout, long parentID) {
		String username;
		Group tocreate = null;
		try {
			username = usernameCreator;
			//create the group
			tocreate = createVRE(communityName, communityDesc, parentID);

			// associate a layout to the group
			createLayout(tocreate, validateUser(username), siteLayout);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		_log.debug("Returning Organization ID " + tocreate.getClassPK());
		return tocreate.getClassPK();
	}

	

	

	/**
	 * Create the group in the database
	 * 
	 * @return
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private Group createVRE(String communityName, String communityDesc, long parentID) throws PortalException, SystemException {

		User creator = validateUser(screenName);
		GroupManager gm = new LiferayGroupManager();
		
		long groupid = parentID;
		
		
		GroupModel currOrg = null;
		try {
			currOrg = gm.getGroup(""+groupid);
		} catch (UserManagementSystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GroupRetrievalFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		_log.info("Creating VRE " + communityName + " SUBORG OF " +currOrg.getGroupName());	
		GroupModel groupModel = null;
		try {
			groupModel = gm.createVRE(communityName, ""+groupid, ""+creator.getUserId(), "Description for "+communityName);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		_log.trace("Calling groupModel.getGroupId() ");
		long curGroupid = Long.parseLong(groupModel.getGroupId());
		_log.trace("curGroupid " + curGroupid );
		
		Group toReturn = OrganizationLocalServiceUtil.getOrganization(curGroupid).getGroup();
		_log.debug("GROUP created  id:" + toReturn.getGroupId());
		return toReturn;
	}


	/**
	 * 
	 * @param communityName -
	 * @return list of belonging portlet
	 */
	public List<Portlet> getGCubePortlets(String communityName) {
		return null;
	}	

	
}
