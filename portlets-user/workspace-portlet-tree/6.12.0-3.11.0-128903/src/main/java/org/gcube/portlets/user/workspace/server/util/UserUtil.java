package org.gcube.portlets.user.workspace.server.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;



/**
 * The Class UserUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 17, 2015
 */
public class UserUtil {
	
	static UserManager um = new LiferayUserManager();
	protected static Logger logger = Logger.getLogger(UserUtil.class);

	
	/**
	 * Gets the user full name.
	 *
	 * @param portalLogin the portal login
	 * @return the user full name if is available, the input parameter portalLogin otherwise
	 */
	public static String getUserFullName(String portalLogin){
//		return user.getPortalLogin(); //for testing in eclipse
		
//		logger.trace("Finding full name for: "+portalLogin);
		
		if(portalLogin==null)
			return "";
		
		if (WsUtil.isWithinPortal()) { //INTO PORTAL
			
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
				logger.error("An error occurred in getUserFullName "+ume,ume);
			}catch (Exception e) {
				logger.error("An error occurred in getUserFullName "+e,e);
				logger.warn("Return portal login "+portalLogin);
				return portalLogin;
			}
			
			if (curr != null){
//				logger.trace("Return "+curr.getFullname() +" full name for: "+portalLogin);
				return curr.getFullname();
			}
		}else{
			logger.trace("DEVELOPEMENT MODE ON");
			logger.trace("Returning input login: "+portalLogin);
			return portalLogin;
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
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		List<String> login = new ArrayList<String>();
//		login.add("ale");
//		login.add("pepe");
		System.out.println(separateFullNameToCommaForPortalLogin(login));
		
	}
}
