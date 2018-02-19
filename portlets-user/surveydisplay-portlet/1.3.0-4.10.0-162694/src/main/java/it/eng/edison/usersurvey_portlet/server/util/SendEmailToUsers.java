package it.eng.edison.usersurvey_portlet.server.util;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.mailing.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.edison.usersurvey_portlet.server.entity.Survey;


/**
 * The Class SendEmailToUsers.
 */
public class SendEmailToUsers {

	/** The Constant _log. */
	private final static Logger _log = LoggerFactory.getLogger(SendEmailToUsers.class);
	/**
	 * Instantiates a new send email to users.
	 */
	public SendEmailToUsers() {
		
	}
	
	/**
	 * Instantiates a new send email to users.
	 *
	 * @param survey the survey instance
	 * @param UrlWithoutParams the url without params
	 * @param uuid the uuid
	 * @param fullNameUser the full name user
	 * @param emailUser the email user
	 * @param surveySender the survey sender
	 * @param currentGroupName the current group name
	 * @param surveyAdminFullName the survey admin full name
	 * @param isAnonymous the is anonymous
	 * @param request the request
	 */
	public SendEmailToUsers(Survey survey, String UrlWithoutParams, String uuid, String fullNameUser, String emailUser, String surveySender, String currentGroupName, String surveyAdminFullName, boolean isAnonymous, HttpServletRequest request) {
		sendInviteEmail(survey, UrlWithoutParams, uuid, fullNameUser, emailUser, surveySender, currentGroupName, surveyAdminFullName, isAnonymous, request);
	}
	
	/**
	 * Send invite email.
	 *
	 * @param UrlWithoutParams the url without params
	 * @param uuid the uuid
	 * @param fullNameUser the full name user
	 * @param email the email
	 * @param surveySender the survey sender
	 * @param currentGroupName the current group name
	 * @param surveyAdminFullName the survey admin full name
	 * @param isAnonymous the is anonymous
	 * @param request the request
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void sendInviteEmail(Survey survey, String UrlWithoutParams, String uuid, String fullNameUser, String email, String surveySender, String currentGroupName, String surveyAdminFullName, boolean isAnonymous, HttpServletRequest request) throws IllegalArgumentException {

		String emailSubject = "You have been invited to fill a survey on " + currentGroupName;

		
		
		EmailNotification mailToSend = new EmailNotification(
				email , 
				emailSubject, 
				getTextEmail(UrlWithoutParams, uuid, fullNameUser, email, surveySender, currentGroupName, surveyAdminFullName, isAnonymous), 
				request);
		mailToSend.sendEmail();
	}
	
	/**
	 * Gets the text email.
	 *
	 * @param UrlWithoutParams the url without params
	 * @param uuid the uuid
	 * @param fullNameUser the full name user
	 * @param email the email
	 * @param surveySender the survey sender
	 * @param currentGroupName the current group name
	 * @param surveyAdminFullName the survey admin full name
	 * @param isAnonymous the is anonymous
	 * @return the text email
	 */
	private String getTextEmail(String UrlWithoutParams, String uuid, String fullNameUser, String email, String surveySender, String currentGroupName, String surveyAdminFullName, boolean isAnonymous) {

		StringBuilder body = new StringBuilder();

		body.append("Dear " + fullNameUser + ",")
		.append("<br><br>")
		.append(surveySender + " invited to partecipate a survey in the context of <b>" + currentGroupName + "</b>.")
		.append("<br><br>");
		
		body.append("To participate this survey, please follow this <a href=\"" + UrlWithoutParams + "?UUID=" + uuid + "\">link</a>.");
			
		body.append("<br><br>")
		.append("Thank you very much for your time and cooperation.")
		.append("<br><br>");
		
		if(isAnonymous){
			body.append("<br><br>")
			.append("Please note: your participation in the survey is completely voluntary and all of your responses will be kept confidential. "
					+"<br> " + "The access code in the link is to remove you from the list once you have completed the survey. "
					+"<br> " + "No personally identifiable information will be associated with your responses to any reports of these data.");
		}
		
		return body.toString();
	}

}
