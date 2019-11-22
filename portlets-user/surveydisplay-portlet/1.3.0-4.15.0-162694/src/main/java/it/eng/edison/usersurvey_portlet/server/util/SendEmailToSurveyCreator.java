package it.eng.edison.usersurvey_portlet.server.util;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.mailing.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class SendEmailToSurveyCreator.
 */
public class SendEmailToSurveyCreator {

	/** The Constant _log. */
	private final static Logger _log = LoggerFactory.getLogger(SendEmailToSurveyCreator.class);
	
	/** The Constant MAIL_SERVICE_HOST. */
	private final static String MAIL_SERVICE_HOST = "mail.eng.it";
	
	/** The Constant MAIL_SERVICE_PORT. */
	private final static String MAIL_SERVICE_PORT = "25";
	
	/** The Constant SENDER_EMAIL. */
	private static final String SENDER_EMAIL = "notificationSenderEmail";
	
	/** The Constant GATEWAY_NAME. */
	private static final String GATEWAY_NAME = "portalinstancename";
	
	/**
	 * Instantiates a new send email to survey creator.
	 */
	public SendEmailToSurveyCreator() {
		
	}
	
	/**
	 * Instantiates a new send email to survey creator.
	 *
	 * @param UrlWithoutParams the url without params
	 * @param fullNameUser the full name user
	 * @param emailCreatorManager the email creator manager
	 * @param surveyCreator the survey creator
	 * @param currentGroupName the current group name
	 * @param request the request
	 */
	public SendEmailToSurveyCreator(String questionnaireTitle, String UrlWithoutParams, String fullNameUser, String emailCreatorManager, String surveyCreator, String currentGroupName, HttpServletRequest request) {
		sendInviteEmail(questionnaireTitle, UrlWithoutParams, fullNameUser, emailCreatorManager, surveyCreator, currentGroupName, request);
	}
	
	/**
	 * Send invite email.
	 *
	 * @param UrlWithoutParams the url without params
	 * @param fullNameUser the full name user
	 * @param emailSurveyManager the email survey manager
	 * @param surveyManager the survey manager
	 * @param currentGroupName the current group name
	 * @param request the request
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void sendInviteEmail(String questionnaireTitle, String UrlWithoutParams, String fullNameUser, String emailSurveyManager, String surveyManager, String currentGroupName, HttpServletRequest request) throws IllegalArgumentException {

		String emailSubject = null;
		
		if(currentGroupName == null){
			emailSubject = currentGroupName + " member has answered a questionnaire";
		} else {
			emailSubject = currentGroupName + " member has answered a questionnaire on " + currentGroupName;
		}

		EmailNotification mailToSend = new EmailNotification(
				emailSurveyManager , 
				emailSubject, 
				getTextEmail(questionnaireTitle, UrlWithoutParams, fullNameUser, surveyManager, currentGroupName), 
				request);
		mailToSend.sendEmail();
	}
	
	/**
	 * Gets the text email.
	 *
	 * @param UrlWithoutParams the url without params
	 * @param fullNameUser the full name user
	 * @param surveyManager the survey manager
	 * @param currentGroupName the current group name
	 * @return the text email
	 */
	private String getTextEmail(String questionnaireTitle, String UrlWithoutParams, String fullNameUser, String surveyManager, String currentGroupName) {

		StringBuilder body = new StringBuilder();

		if(currentGroupName == null){
			body.append("Dear " + surveyManager + ",")
			.append("<br><br>")
			.append(fullNameUser + " has answered a questionnaire: " + questionnaireTitle)
			.append("<br>");
		} else {
			body.append("Dear " + surveyManager + ",")
			.append("<br><br>")
			.append(fullNameUser + " has answered the questionnaire "+ questionnaireTitle + " on <b>" + currentGroupName + "</b>.")
			.append("<br>");
		}
		
		body.append("To see responses connect to Questionnaire Manager application on " + currentGroupName);
		
		return body.toString();
	}

}
