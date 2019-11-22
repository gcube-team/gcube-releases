/**
 * 
 */
package gr.cite.bluebridge.portlets.admin.usersmanagementportlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Panagiota Koltsida
 *
 */
public class EmailPartsConstruction {
	static String gCubeFileName = "gcube-data.properties";
	static String propFileName = "EmailTemplates.properties";
	static String catalinaHome = System.getProperty("catalina.base");
	static String filePath = catalinaHome + File.separator + "conf" + File.separator;
	
	private static Log logger = LogFactoryUtil.getLog(EmailPartsConstruction.class);
	
	public static String returnProperty(String bodyKey){
		InputStream is = null;
		try{
			Properties props = new Properties();
            logger.debug("Using file -> " + filePath + propFileName);
	        is = new FileInputStream(filePath + propFileName);
	        
	        if (is != null) {
	        	props.load(is);
			}
	        
	        String prop1 = props.getProperty(bodyKey);
	        logger.debug("The body msg that will be sent is.... ");
	        logger.debug(prop1);
	        return prop1;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String returnPortalName(){
		InputStream is = null;
		try{
			Properties props = new Properties();
            logger.debug("Using file -> " + filePath + gCubeFileName);
	        is = new FileInputStream(filePath + gCubeFileName);
	        
	        if (is != null) {
	        	props.load(is);
			}
	        
	        String prop1 = props.getProperty("portalinstancename");
	        logger.debug(prop1);
	        return prop1;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String returnBodyForMembershipRequestApproval(){
		InputStream is = null;
		String body = "";
		try{
			Properties props = new Properties();
            logger.debug("Using file -> " + filePath + propFileName);
	        is = new FileInputStream(filePath + propFileName);
	        
	        if (is != null) {
	        	props.load(is);
			}
	        
	        String prop1 = props.getProperty(body);
	        logger.debug("The body msg that will be sent is.... ");
	        logger.debug(prop1);
	        return prop1;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void sendDismissalEmailToUser(){
		InputStream is = null;
		try{
			Properties props = new Properties();
            logger.debug("Using file -> " + filePath + propFileName);
	        is = new FileInputStream(filePath + propFileName);
	        
	        if (is != null) {
	        	props.load(is);
			}
	        
	        String prop1 = props.getProperty("prop1");
	        System.out.println(prop1);
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String sendMailForMembershipRequestAcceptanceOrRejection(
			String acceptRejectBody,
			String userFullName, String groupName, String managerFullName,
			String managerEmail, String userEmail, String portalComplete){
		
		String body = returnProperty(acceptRejectBody);
		
		if(body != null){
			logger.info("portal name: " +portalComplete);
			return java.text.MessageFormat.format(body, userFullName, groupName, managerFullName, managerEmail, userEmail, portalComplete);
		}
		
		return "";
	}
	
	public static void sendMailForMembershipRequestRejection(
			String userFullName, String groupName, String managerFullName,
			String managerEmail, String userEmail, String portalComplete){
		
		String body = returnProperty("membershipRequestRejectionBody");
		
		if(body != null){
			java.text.MessageFormat.format(body, userFullName, groupName, managerFullName, managerEmail, userEmail);
		}
	}
	
	public static String subjectForMembershipRequestAcceptanceOrRejection(
			String subjectProperty, String groupName){
		
		String subject = returnProperty(subjectProperty);
		if(subject != null){
			String finalString = groupName;
			return java.text.MessageFormat.format(subject, finalString);
		}
		
		return "";
	}
	
	public static String subjectForUserDismissalFromSite(
			String subjectProperty, String groupName){
		return subjectForMembershipRequestAcceptanceOrRejection(subjectProperty, groupName);
	}
	
	public static void userDismissalFromSite(String groupName){
		String body = returnProperty("userDismissalFromSiteBody");
		
		if(body != null){
			java.text.MessageFormat.format(body, groupName);
		}
	}
	
	public static String getSiteTeamAssignmentSubject(String siteTeamName) {
		String subject = returnProperty("siteTeamAssignmentSubject");
		String response = "";
		
		if(subject != null && !subject.isEmpty()) {
			response = java.text.MessageFormat.format(subject, siteTeamName);
		}
		
		return response;
	}
	
	public static String getSiteTeamDismissalSubject(String siteTeamName) {
		String subject = returnProperty("siteTeamDismissalSubject");
		String response = "";
		
		if(subject != null && !subject.isEmpty()) {
			response = java.text.MessageFormat.format(subject, siteTeamName);
		}
		
		return response;
	}

	public static String getRoleAssignmentRevokeSubject(String groupname) {
		String subject = returnProperty("roleAssignmentRevokeSubject");
		String response = "";

		if(subject != null && !subject.isEmpty()) {
			response = java.text.MessageFormat.format(subject, groupname);
		}

		return response;
	}
	
	public static String constructPortal(String protocol, String port, String baseURL){
		
		
		return baseURL; 
	}
}