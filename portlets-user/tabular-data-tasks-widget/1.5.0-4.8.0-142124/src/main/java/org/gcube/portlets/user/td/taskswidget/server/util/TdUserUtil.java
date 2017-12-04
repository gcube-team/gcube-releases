package org.gcube.portlets.user.td.taskswidget.server.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;



public class TdUserUtil {

	static UserManager um = new LiferayUserManager();
	protected static Logger logger = Logger.getLogger(TdUserUtil.class);


	/**
	 *
	 * @param user
	 * @return An empty string whether the full name is not retrievable from User Manager, full name otherwise
	 */
	public static String getUserFullName(String portalLogin){
//		return user.getPortalLogin(); //for testing in eclipse
//		logger.trace("Finding full name for: "+portalLogin);

		if(portalLogin==null)
			return "";

		if (portalLogin.compareTo(ConstantsTdTasks.TEST_USER) != 0) { //skip test.user

			 GCubeUser curr = null;

			try {
				try {
					curr = um.getUserByUsername(portalLogin);

				} catch (UserManagementSystemException e) {
					logger.error("An error occurred in getUserFullName "+e,e);
				} catch (UserRetrievalFault e) {
					logger.error("An error occurred in getUserFullName "+e,e);
				}
			} catch (Exception e) {
				logger.error("An error occurred in getUserFullName "+e,e);
				logger.warn("Return portal login "+portalLogin);
				return portalLogin;
			}

			if (curr != null){

//				logger.trace("Return "+curr.getFullname() +" full name for: "+portalLogin);
				return curr.getFullname();

			}
		}else{

			logger.trace("Return "+ConstantsTdTasks.TEST_USER_FULL_NAME +" full name for: "+portalLogin);
			return ConstantsTdTasks.TEST_USER_FULL_NAME;
		}

		logger.trace("Return empty full name for: "+portalLogin);
		return "";
	}


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
