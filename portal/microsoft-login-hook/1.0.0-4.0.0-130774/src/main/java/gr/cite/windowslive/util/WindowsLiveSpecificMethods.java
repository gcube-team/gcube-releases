package gr.cite.windowslive.util;

import gr.cite.windowslive.model.WindowsLiveUserInfo;

/**
 * @author mnikolopoulos
 *
 */
public class WindowsLiveSpecificMethods {
	
	public static String returnEmailAddress(WindowsLiveUserInfo windowsLiveUserInfo){
		String emailAddress = "";
		if (!(windowsLiveUserInfo.getEmails().getAccount().isEmpty())){
			emailAddress = windowsLiveUserInfo.getEmails().getAccount();
		}else{
			emailAddress = windowsLiveUserInfo.getEmails().getPreferred();
		}
		
		return emailAddress;
	}
	
}
