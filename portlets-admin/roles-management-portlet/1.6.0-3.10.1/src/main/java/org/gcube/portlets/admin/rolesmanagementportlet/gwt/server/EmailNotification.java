package org.gcube.portlets.admin.rolesmanagementportlet.gwt.server;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared.RecipientTypeConstants;

/**
 * A class for sending email
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class EmailNotification {
	
	/**
	 * The sender of the email
	 */
	private String emailSender;
	
	/**
	 * The recipients of the email
	 */
	private String emailrecipients[];
	
	private boolean notifySupportTeam = false;
	
	private static final String supportTeamEmailAddress = "support_team@d4science.org";
	
	/**
	 * The to, cc or bcc value
	 */
	private String recipientType;
	
	/**
	 * Email's subject
	 */
	private String emailSubject;

	/**
	 * Email's body message
	 */
	private String emailBody;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(EmailNotification.class);
	
	/**
	 * Class's constructor
	 * 
	 * @param sender 
	 * @param recipients 
	 * @param subject 
	 * @param body 
	 */
	public EmailNotification(String sender, String recipients[], String subject, String body, String copyVisibility, boolean notifySupportTeam) {
		if (sender == null)
			ScopeHelper.getSupportMainlingListAddr();
		else
			this.emailSender = sender;
		this.emailrecipients = recipients;
		this.emailSubject = subject;
		this.emailBody = body;
		this.recipientType = copyVisibility;
		this.notifySupportTeam = notifySupportTeam;
	}
	
	public void sendEmail() {
		Properties props = System.getProperties();
        String mailServiceHost = "localhost";	
        props.put("mail.smtp.host", mailServiceHost);
        String mailServicePort = "25";
        props.put("mail.smtp.port", mailServicePort);
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);
        Message mimeMessage = new MimeMessage(session);
        
        try {
        	// EMAIL SENDER
            Address from = new InternetAddress(emailSender);
            mimeMessage.setFrom(from);
            
            // if the user has set this value the support team mailing list will receive this email
            if (notifySupportTeam == true) {
            	 Address address = new InternetAddress(supportTeamEmailAddress);
            	 mimeMessage.addRecipient(Message.RecipientType.TO, address);
            }
            
            // bcc is the default one
            RecipientType recipientTypeValue = Message.RecipientType.BCC;
            // EMAIL RECIPIENTS
            if (this.recipientType.equals(RecipientTypeConstants.EMAIL_TO))
            	recipientTypeValue = Message.RecipientType.TO;
            else if (this.recipientType.equals(RecipientTypeConstants.EMAIL_CC))
            	recipientTypeValue = Message.RecipientType.CC;
            
            for (int i=0; i<emailrecipients.length; i++) {
            	 Address address = new InternetAddress(emailrecipients[i]);
            	 mimeMessage.addRecipient(recipientTypeValue, address);
            }
           
            mimeMessage.setSubject(emailSubject);
           // mimeMessage.setText(emailBody);
            mimeMessage.setContent(emailBody, "text/html");
            mimeMessage.setSentDate(new Date());
            Transport.send(mimeMessage);
        } catch (Exception e) {
        	e.printStackTrace();
           logger.error("Failed to send the email message.", e);
        }
}
}
