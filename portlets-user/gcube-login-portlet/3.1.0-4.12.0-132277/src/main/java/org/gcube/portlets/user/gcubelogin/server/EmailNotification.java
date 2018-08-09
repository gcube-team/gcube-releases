package org.gcube.portlets.user.gcubelogin.server;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

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
	public EmailNotification(String sender, String recipients[], String subject, String body) {
		this.emailSender = sender;
		this.emailrecipients = recipients;
		this.emailSubject = subject;
		this.emailBody = body;
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
            
            // EMAIL RECIPIENTS
            for (int i=0; i<emailrecipients.length; i++) {
            	 Address address = new InternetAddress(emailrecipients[i]);
            	 mimeMessage.addRecipient(Message.RecipientType.TO, address);

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


