package org.gcube.common.portal.mailing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante
 *
 */
public class EmailNotification {
	private static Logger _log = LoggerFactory.getLogger(EmailNotification.class);	
	/**
	 * The recipients of the email
	 */
	private String emailrecipients[];

	private List<InternetAddress> emailRecipientsInCC;
	private List<InternetAddress> emailRecipientsInBCC;
	/**
	 * Email's subject
	 */
	private String emailSubject;
	/**
	 * Email's body message
	 */
	private StringBuffer emailBody;
	/**
	 * 
	 */
	private HttpServletRequest request;

	private final String MAIL_SERVICE_HOST = "localhost";
	private String MAIL_SERVICE_PORT = "25";
	/**
	 * 
	 * @param recipient an email address
	 * @param subject the subject of your email
	 * @param body the body of your email
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public EmailNotification(String recipient, String subject, String body, HttpServletRequest httpServletRequest) { 
		String[] emailRecipients = new String[1];
		emailRecipients[0] = recipient;
		init(httpServletRequest, emailRecipients, subject, body);
	}
	/**
	 *  @param recipients an array of email addresses
	 * @param subject the subject of your email
	 * @param body the body of your email
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public EmailNotification(String recipients[], String subject, String body, HttpServletRequest httpServletRequest) {
		init(httpServletRequest, recipients, subject, body);
	}

	/**
	 * @param recipients a list of email addresses
	 * @param subject the subject of your email
	 * @param body the body of your email
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public EmailNotification(List<String> recipients, String subject, String body, HttpServletRequest httpServletRequest) {
		init(httpServletRequest, recipients.toArray(new String[recipients.size()]), subject, body);
	}

	private void init(HttpServletRequest httpServletRequest, String recipients[], String subject, String body) {
		request = httpServletRequest;
		emailrecipients = recipients;
		emailSubject = subject;
		emailRecipientsInCC = new ArrayList<InternetAddress>();
		emailRecipientsInBCC = new ArrayList<InternetAddress>();
		emailBody = new StringBuffer(body);
		emailBody.append("<p>")
		.append("<p><div style=\"color:#999999; font-size:10px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif; padding-top:15px;\">")
		.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain ")
		.append("information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message. ")
		.append("If you have received this communication in error, please notify the <sender> and destroy and delete any copies you may have received.")
		.append("</div></p>");
	}

	public void addRecipientInCC(String email) {
		try {
			emailRecipientsInCC.add(new InternetAddress(email));
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}

	public void addRecipientInBCC(String email) {
		try {
			emailRecipientsInBCC.add(new InternetAddress(email));
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail() {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", MAIL_SERVICE_HOST);
		props.put("mail.smtp.port", MAIL_SERVICE_PORT);
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(true);
		Message mimeMessage = new MimeMessage(session);
		try {
			String emailSender = "";
			String siteName = "";
			// EMAIL SENDER
			if (request != null) {
				emailSender = PortalContext.getConfiguration().getSenderEmail(request);
				siteName = PortalContext.getConfiguration().getGatewayName(request);
			} else {
				emailSender = PortalContext.getConfiguration().getSenderEmail();
				siteName = PortalContext.getConfiguration().getGatewayName();
			}
			Address from = new InternetAddress(emailSender, siteName);
			mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
			mimeMessage.setFrom(from);

			// EMAIL RECIPIENTS
			for (int i=0; i<emailrecipients.length; i++) {
				Address address = new InternetAddress(emailrecipients[i]);
				mimeMessage.addRecipient(Message.RecipientType.TO, address);
			} 
			// EMAIL CC Recipients
			for (InternetAddress email : emailRecipientsInCC) {
				mimeMessage.addRecipient(Message.RecipientType.CC, email);
			} 
			// EMAIL BCC Recipients
			for (InternetAddress email : emailRecipientsInBCC) {
				mimeMessage.addRecipient(Message.RecipientType.BCC, email);
			} 
			mimeMessage.setSubject(emailSubject);
			mimeMessage.setContent(emailBody.toString(), "text/html; charset=UTF-8");
			mimeMessage.setSentDate(new Date());

			try {
				Transport.send(mimeMessage);
			}
			catch (com.sun.mail.smtp.SMTPSendFailedException ex) {									
				_log.error("Error while trying to send emails");
				ex.printStackTrace();													
			}
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("Failed to send the email message.", e);
		}
	}


}


