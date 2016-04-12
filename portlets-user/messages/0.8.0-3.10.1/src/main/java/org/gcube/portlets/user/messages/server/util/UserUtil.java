package org.gcube.portlets.user.messages.server.util;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.User;
import org.gcube.portlets.user.messages.server.MessagesServiceImpl;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;


public class UserUtil {
	
	static UserManager um = new LiferayUserManager();
	protected static Logger logger = Logger.getLogger(UserUtil.class);
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public static String getUserFullName(User user){
//		return user.getPortalLogin(); //for testing in eclipse
		
		if (user!=null && user.getPortalLogin().compareTo(MessagesServiceImpl.TEST_USER) != 0) { //skip test.user
			UserModel curr = null;
			try {
				try {
					curr = um.getUserByScreenName(user.getPortalLogin());
				
				} catch (UserManagementSystemException e) {
					logger.error("An error occurred in getUserFullName "+e,e);
				} catch (UserRetrievalFault e) {
					logger.error("An error occurred in getUserFullName "+e,e);
				}
			} catch (UserManagementPortalException ume) {
				
			}
			
			if (curr != null)
				return curr.getFullname();	
		}
		else{
			logger.warn("Current user is null or " +MessagesServiceImpl.TEST_USER+ "return full name "+MessagesServiceImpl.TEST_USER_FULLNAME);
			return MessagesServiceImpl.TEST_USER_FULLNAME;
		}
		
		return "";

	}
	
	

	/**
	 * 
	 * @param portalLogin
	 * @return
	 */
	public static String getUserFullName(String portalLogin){
//		return user.getPortalLogin(); //for testing in eclipse
		
		if (portalLogin!=null && portalLogin.compareTo(MessagesServiceImpl.TEST_USER) != 0) { //skip test.user
			UserModel curr = null;
			try {
				try {
					curr = um.getUserByScreenName(portalLogin);
				
				} catch (UserManagementSystemException e) {
				
					logger.error("An error occurred in getUserFullName "+e,e);
				} catch (UserRetrievalFault e) {
					
					logger.error("An error occurred in getUserFullName "+e,e);
				}
			} catch (UserManagementPortalException ume) {
			}
			
			if (curr != null)
				return curr.getFullname();	
		}
		else{
			logger.warn("Current user is null or " +MessagesServiceImpl.TEST_USER+ "return full name "+MessagesServiceImpl.TEST_USER_FULLNAME);
			return MessagesServiceImpl.TEST_USER_FULLNAME;
		}
		
		return "";

	}
}
