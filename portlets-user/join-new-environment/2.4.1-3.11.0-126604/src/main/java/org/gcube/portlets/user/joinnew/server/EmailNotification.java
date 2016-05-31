package org.gcube.portlets.user.joinnew.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A class for sending email
 * 
 * @author M. Assante
 *
 */
public class EmailNotification {
	private static final String SENDER_EMAIL = "notificationSenderEmail";
	private static final String GATEWAY_NAME = "portalinstancename";
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
	
	private String portalName;

	/** Logger */
	private static Logger _log = LoggerFactory.getLogger(EmailNotification.class);	

	/**
	 * Class's constructor
	 * 
	 * @param sender 
	 * @param recipients 
	 * @param subject 
	 * @param body 
	 */
	public EmailNotification(String sender, String recipients[], String subject, String body) {
		this.emailSender = getNotificationSenderEmail();
		this.emailrecipients = recipients;
		this.emailSubject = subject;
		this.emailBody = body;
		this.portalName = getPortalInstanceName();		
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
			Address from = new InternetAddress(emailSender, portalName);
			mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
			mimeMessage.setFrom(from);

			// EMAIL RECIPIENTS
			for (int i=0; i<emailrecipients.length; i++) {
				Address address = new InternetAddress(emailrecipients[i]);
				mimeMessage.addRecipient(Message.RecipientType.TO, address);

			}

			mimeMessage.setSubject(emailSubject);
			// mimeMessage.setText(emailBody);
			mimeMessage.setContent(emailBody, "text/html; charset=UTF-8");
			mimeMessage.setSentDate(new Date());
			Transport.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("Failed to send the email message.", e);
		}
	}
	
	/**
	 * read the portal instance name from a property file and returns it
	 */
	protected static String getPortalInstanceName() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(GATEWAY_NAME);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "Gateway support";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Portal Name " + toReturn);
			return toReturn;
		}
		_log.debug("Returning Gateway Name: " + toReturn );
		return toReturn;
	}
	
	/**
	 * read the sender email for notifications name from a property file and returns it
	 */
	private static String getNotificationSenderEmail() {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";

		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			toReturn = props.getProperty(SENDER_EMAIL);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			toReturn = "do-not-reply@d4science.org";
			_log.error("gcube-data.properties file not found under $CATALINA_HOME/conf dir, returning default Email" + toReturn);
			return toReturn;
		}
		_log.debug("Returning SENDER_EMAIL: " + toReturn );
		return toReturn;
	}
}


