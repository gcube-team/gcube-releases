package org.gcube.portlets.widgets.workspaceuploader.server.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.portlets.widgets.workspaceuploader.shared.ContactModel;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;



/**
 * The Class UserUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class UserUtil {

	static UserManager um = new LiferayUserManager();
	protected static Logger logger = Logger.getLogger(UserUtil.class);


	/**
	 * Gets the user full name.
	 *
	 * @param portalLogin the portal login
	 * @return the user full name
	 */
	public static String getUserFullName(String portalLogin){
		if(portalLogin==null)
			return "";

		if (WsUtil.isWithinPortal()) { //INTO PORTAL

			GCubeUser curr = null;

			try {
				try {
					curr = um.getUserByUsername(portalLogin);

				} catch (UserManagementSystemException e) {

					logger.error("An error occurred in getUserByUsername "+e,e);
				} catch (UserRetrievalFault e) {

					logger.error("An error occurred in getUserByUsername "+e,e);
				}
			}catch (Exception e) {
				logger.error("An error occurred in getUserByUsername "+e,e);
				logger.warn("Return portal login "+portalLogin);
				return portalLogin;
			}

			if (curr != null){

//				logger.trace("Return "+curr.getFullname() +" full name for: "+portalLogin);
				return curr.getFullname();

			}
		}else{
			logger.trace("DEVELOPEMENT MODE ON");
			logger.trace("Returning "+WsUtil.TEST_USER_FULL_NAME +" full name for: "+portalLogin);
			return WsUtil.TEST_USER_FULL_NAME;
		}

		logger.trace("Return portal login as full name for: "+portalLogin);
		return portalLogin;
	}

	/**
	 * Gets the list login by info contact model.
	 *
	 * @param listContacts the list contacts
	 * @return the list login by info contact model
	 */
	public static List<String> getListLoginByInfoContactModel(List<ContactModel> listContacts){

		List<String> listUsers = new ArrayList<String>();

		for (ContactModel infoContactModel : listContacts) {
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
	public static String separateUsersNamesToComma(List<ContactModel> listContacts){

		String users = "";

		for (int i = 0; i < listContacts.size()-1; i++) {
			users+= listContacts.get(i).getFullName() + ", ";
		}

		if(listContacts.size()>1)
			users += listContacts.get(listContacts.size()-1).getFullName();

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

		logger.trace("SeparateFullNameToCommaForPortalLogin converting: "+listLogin);

		//N-1 MEMBERS
		for (int i = 0; i < listLogin.size()-1; i++) {
//			logger.trace("Converting: "+i+") "+listLogin.get(i));
			users+= getUserFullName(listLogin.get(i)) + ", ";
		}

		//LAST MEMBER
		if(listLogin.size()>=1){
//			logger.trace("Converting: "+(listLogin.size()-1)+") " +listLogin.get(listLogin.size()-1));
			users += getUserFullName(listLogin.get(listLogin.size()-1));
		}

		logger.trace("SeparateFullNameToCommaForPortalLogin returning: "+users);

		return users;
	}
}
