/*
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class UserUtil.
 *
 * @author Francesco Mangiacrapa 
 * Nov 25, 2016
 */
public class UserUtil {

	static UserManager um = new LiferayUserManager();
	protected static Logger logger = LoggerFactory.getLogger(UserUtil.class);


	/**
	 * Gets the user full name.
	 *
	 * @param portalLogin the portal login
	 * @return the user full name
	 */
	public static String getUserFullName(String portalLogin){

		if(portalLogin==null)
			return "";

		logger.info("Get user full name for: "+portalLogin);
		logger.info("Into portal: "+WsUtil.isWithinPortal());
		if (portalLogin != null && WsUtil.isWithinPortal()) { //skip test.user

			GCubeUser curr = null;

			try {
				try {
					curr = um.getUserByUsername(portalLogin);

				} catch (UserManagementSystemException e) {
					logger.warn("An error occurred in getUserFullName "+e);
				} catch (UserRetrievalFault e) {
					logger.warn("An error occurred in getUserFullName "+e);
				}
			}catch (Exception e) {
				logger.error("An error occurred in getUserFullName "+e,e);
				logger.warn("Return portal login "+portalLogin);
				return portalLogin;
			}

			if (curr != null){
				return curr.getFullname();
			}
		}else{

			logger.info("We are out of portal, returning login portalLogin "+portalLogin+ "as full name");
			return portalLogin;
		}

		logger.info("Return portal login as full name for: "+portalLogin);
		return portalLogin;
	}


	/**
	 * Gets the organization users.
	 *
	 * @param scope the scope
	 * @return the organization users
	 */
	public static List<GCubeUser> getOrganizationUsers(String scope) {
		try {
			logger.info("Getting organization users by scope: "+scope);
			UserManager um = new LiferayUserManager();
			GroupManager gm = new LiferayGroupManager();
			ScopeBean sb = new ScopeBean(scope);

			if (sb.is(Type.INFRASTRUCTURE)){
				logger.info("Returning users by group for INFRASTRUCTURE: "+gm.getRootVO().getGroupId());
				return um.listUsersByGroup(gm.getRootVO().getGroupId());
			}else if (sb.is(Type.VRE)) { //must be in VRE
				//get the name from the scope
				String orgName = scope.substring(scope.lastIndexOf("/")+1, scope.length());
				//ask the users
				logger.info("Returning users by group for VRE: "+orgName);
				return um.listUsersByGroup(gm.getGroupId(orgName));
			}else {
				logger.error("Error, you must be in SCOPE VRE OR INFRASTURCTURE, you are in VO SCOPE returning no users");
				return null;
			}
		} catch (Exception e) {
			logger.error("Error in server get all contacts ", e);
		}
		return null;
	}

	/**
	 * Gets the list login by info contact model.
	 *
	 * @param listContacts the list contacts
	 * @return the list login by info contact model
	 */
	public static List<String> getListLoginByInfoContactModel(List<InfoContactModel> listContacts){

		List<String> listUsers = new ArrayList<String>();

		for (InfoContactModel infoContactModel : listContacts) {
			listUsers.add(infoContactModel.getLogin());
		}

		return listUsers;
	}

	/**
	 * Separate users names to comma.
	 *
	 * @param listContacts the list contacts
	 * @return the string
	 */
	public static String separateUsersNamesToComma(List<InfoContactModel> listContacts){

		String users = "";

		for (int i = 0; i < listContacts.size()-1; i++) {
			users+= listContacts.get(i).getName() + ", ";
		}

		if(listContacts.size()>1)
			users += listContacts.get(listContacts.size()-1).getName();

		return users;
	}


	/**
	 * Separate full name to comma for portal login.
	 *
	 * @param listLogin the list login
	 * @return the string
	 */
	public static String separateFullNameToCommaForPortalLogin(List<String> listLogin){

		String users = "";

		for (int i = 0; i < listLogin.size()-1; i++) {
			users+= getUserFullName(listLogin.get(i)) + ", ";
		}

		if(listLogin.size()>1)
			users += getUserFullName(listLogin.get(listLogin.size()-1));

		return users;
	}

}
