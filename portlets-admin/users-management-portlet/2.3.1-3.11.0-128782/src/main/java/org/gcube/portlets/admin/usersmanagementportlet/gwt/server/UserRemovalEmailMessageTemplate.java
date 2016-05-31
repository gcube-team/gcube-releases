package org.gcube.portlets.admin.usersmanagementportlet.gwt.server;

import org.apache.log4j.Logger;

/**
 * Constructs the body of the email message when a user is removed from a VO
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UserRemovalEmailMessageTemplate {

private String currentVO;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(UserRegistrationEmailMessageTemplate.class);
	
	
	public UserRemovalEmailMessageTemplate(String VO) {
		this.currentVO = VO;
		//this.availableRoles = availableRoles;
	}
	
	public String createBodyMessage() {
		String bodyMsg = "<p style=\"font-family:verdana;color:darkgreen\"><em>You have been unregistered from the <b><i>" + this.currentVO + " VO/VRE</i></b></em></p>";

		bodyMsg += "<p style=\"font-family:verdana;color:navy\"><i>The '" + this.currentVO + "' VO/VRE administration team</i></p>";
		
		logger.debug("The body msg that will be sent is.... ");
		logger.debug(bodyMsg);
		
		
		return bodyMsg;
	}
}
