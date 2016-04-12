package org.gcube.portlets.admin.usersmanagementportlet.gwt.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.RecipientTypeConstants;
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
	
	private ArrayList<String> managersEmails = null;

	private boolean notifySupportTeam = false;

	private boolean useConfigEmailAddress = false;

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
	public EmailNotification(String sender, String recipients[], ArrayList<String> managersEmails, String subject, String body, String copyVisibility, boolean notifySupportTeam, boolean useConfigEmail) {
		if (sender == null)
			this.emailSender = ScopeHelper.getSupportMainlingListAddr();
		else
			this.emailSender = sender;
		this.emailrecipients = recipients;
		this.emailSubject = readPortalInstanceNameFromConfigFile() + " - " + subject;
		this.emailBody = body;
		this.recipientType = copyVisibility;
		this.notifySupportTeam = notifySupportTeam;
		this.managersEmails = managersEmails;
		this.useConfigEmailAddress = useConfigEmail;
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

			if (useConfigEmailAddress == true) {
				String address;
				// read the properties file to get the email address
				try {
					address = readEmailAddressFromConfigFile();
				} catch (Exception e) {
					logger.error("Could not read the config file. Using sender's address in the TO field.");
					address = emailSender;
				}
				logger.debug("Using the address from the config file as TO type. The address is -> " + address);
				Address maddress = new InternetAddress(address);
				mimeMessage.addRecipient(Message.RecipientType.TO, maddress);
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
				logger.debug("RecipientType -> " + recipientTypeValue);
				mimeMessage.addRecipient(recipientTypeValue, address);
			}

			if (this.managersEmails != null) {
				for (String managerEmail : managersEmails) {
					Address address = new InternetAddress(managerEmail);
					mimeMessage.addRecipient(Message.RecipientType.BCC, address);
				}
			}

			mimeMessage.setSubject(emailSubject);
			mimeMessage.setContent(emailBody, "text/html; charset=utf-8");
			mimeMessage.setSentDate(new Date());

			Transport.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to send the email message.", e);
		}
	}

	private String readEmailAddressFromConfigFile() throws IOException {
		Properties prop = new Properties();
		String catalinaHome = System.getProperty("catalina.base");
		String fileName = catalinaHome + File.separator + "shared" + File.separator +
				"d4s" +  File.separator + "umPortlet-email.config";
		logger.debug("Using file -> " + fileName);
		InputStream is = new FileInputStream(fileName);

		prop.load(is);
		String address = prop.getProperty("email.to");
		return address;
	}
	
	private String readPortalInstanceNameFromConfigFile() {
		try {
			Properties prop = new Properties();
			String catalinaHome = System.getProperty("catalina.base");
			String fileName = catalinaHome + File.separator + "conf" + File.separator +
							"gcube-data.properties";
			logger.debug("Using file -> " + fileName);
			InputStream is = new FileInputStream(fileName);

			prop.load(is);
			String address = prop.getProperty("portalinstancename");
			if (address != null)
				return address;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "[PORTAL - iMARINE]";
	}
}
