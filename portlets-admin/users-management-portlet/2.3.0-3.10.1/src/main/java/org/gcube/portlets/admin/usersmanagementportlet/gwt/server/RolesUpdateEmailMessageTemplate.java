package org.gcube.portlets.admin.usersmanagementportlet.gwt.server;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Constructs the body of the email message when user's roles have been updated
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RolesUpdateEmailMessageTemplate {	
	private String currentVO;
	
	private ArrayList<String> availableRoles;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(RolesUpdateEmailMessageTemplate.class);
	
	
	public RolesUpdateEmailMessageTemplate(String VO, ArrayList<String> availableRoles) {
		this.currentVO = VO;
		this.availableRoles = availableRoles;
	}
	
	public String createBodyMessage() {
		String bodyMsg = "<p style=\"font-family:verdana;color:darkgreen\">Your roles have been changed for the VO/VRE: <b><i>'" + this.currentVO + "'</i></b></p>";
		bodyMsg += "<br/><p style=\"font-family:verdana;color:darkgreen\"> Your roles now are: <br/><ul>";
		if (availableRoles != null && availableRoles.size() > 0) {
			for (int i=0; i<availableRoles.size(); i++) {
				bodyMsg += "<li><p style=\"font-size:small;color:darkgreen\">" + availableRoles.get(i) + "</p></li><br/>";
			}
			bodyMsg += "</ul>";
		}
		else {
			bodyMsg += "<p><b>No Roles available. You have been removed from this VO/VRE</b></p><br/>";
		}
		bodyMsg += "<p style=\"font-family:verdana;color:navy\"><b><i>The '" + this.currentVO + "' VO/VRE administration team</i></b></p>";
		
		logger.debug("The body msg that will be sent is.... ");
		logger.debug(bodyMsg);
		
		
		return bodyMsg;
	}
	
	
}