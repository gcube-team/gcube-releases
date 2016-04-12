package org.gcube.portlets.admin.usersmanagementportlet.gwt.server;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.usermanagement.model.UserModel;

/**
 * Constructs the body of the email message sent when a user is registered to a VO
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UserRegistrationEmailMessageTemplate {

	private String group;
	private UserModel registeredUser;
	private String portalURL;
	private UserModel manager;
	private boolean isVO;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(UserRegistrationEmailMessageTemplate.class);
	
	
	public UserRegistrationEmailMessageTemplate(UserModel registeredUser, UserModel manager, String group, boolean isVO, String portalURL) {
		this.group = group;
		this.registeredUser = registeredUser;
		this.portalURL = portalURL;
		this.manager = manager;
		this.isVO = isVO;
	}
	
	public String createBodyMessage() {
		String bodyMsg = "<p style=\"font-size:medium\">Dear <b>" + this.registeredUser.getFullname() + "</b>,</p>";
		bodyMsg += "<p style=\"font-size:medium\">Your request for accessing the ";
		
		if (this.isVO)
			bodyMsg += "<b>" + this.group + "</b> Virtual Organisation";
		else
			bodyMsg += "<b>" + this.group + "</b> Virtual Research Environment";
		
		bodyMsg += " has been approved by " + this.manager.getFullname() + " (" + this.manager.getEmail() + ")</p>";
		bodyMsg += "<p style=\"font-size:medium\">From now on you can access it here: " + this.portalURL + " using the following email: <b>" + this.registeredUser.getEmail() + "</b></p>";
		
		
		bodyMsg += "<br><br><p style=\"font-size:small; color:#bbbbbb\"> WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain " +
		"information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law.<br>" +
		"If you are not the intended recipient or the person responsible for " +
				"delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message.<br>" +
				"If you have received this communication in error, please notify the sender and destroy and delete any copies you may have received.</p>";


		
		
		logger.debug("The body msg that will be sent is.... ");
		logger.debug(bodyMsg);
		
		return bodyMsg;
	}
}
