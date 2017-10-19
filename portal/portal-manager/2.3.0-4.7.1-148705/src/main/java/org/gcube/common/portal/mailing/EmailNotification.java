package org.gcube.common.portal.mailing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.templates.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class EmailNotification {
	private static Logger _log = LoggerFactory.getLogger(EmailNotification.class);	
	private static final String MAIL_SERVICE_HOST = "localhost";
	private static final String MAIL_SERVICE_PORT = "25";
	/**
	 * The list of recipient addresses in "to"
	 */
	private String emailrecipients[];
	/**
	 * The list of recipient addresses with cc
	 */
	private final List<InternetAddress> emailRecipientsInCC;
	/**
	 * The list of recipient addresses with bcc
	 */
	private final List<InternetAddress> emailRecipientsInBCC;
	/**
	 * Email's subject
	 */
	private final String emailSubject;
	/**
	 * Email's body message in text/html
	 */
	private StringBuffer emailBodyTextHTML;
	/**
	 * Email's body message in text/plain
	 */
	private StringBuffer emailBodyTextPlain;
	/**
	 * Email's message from template
	 */
	private Template selectedTemplate;

	private HttpServletRequest request;
	/**
	 *  
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail in HTML</li>
	 * <li>The recipient addresses</li>
	 * <li>the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case)</li>
	 * </ul> 
	 * 
	 * @param recipient an email address
	 * @param subject the subject of your email 
	 * @param bodyTextHTML the body of your email in HTML 
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public EmailNotification(String recipient, String subject, String bodyTextHTML, HttpServletRequest httpServletRequest) { 
		this(new EmailBuilder(subject, httpServletRequest, new String[]{recipient}).contentTextHTML(bodyTextHTML));
	}
	/**
	 *  
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail in HTML</li>
	 * <li>One or several "to" recipient addresses</li>
	 * <li>the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case)</li>
	 * </ul> 
	 * 
	 * @param recipients an array of email addresses
	 * @param subject the subject of your email
	 * @param bodyTextHTML the body of your email in HTML
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public EmailNotification(String recipients[], String subject, String bodyTextHTML, HttpServletRequest httpServletRequest) {
		this(new EmailBuilder(subject, httpServletRequest, recipients).contentTextHTML(bodyTextHTML));
	}
	/**
	 *  
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the mail</li>
	 * <li>The body of the mail in HTML</li>
	 * <li>One or several "to" recipient addresses</li>
	 * <li>the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case)</li>
	 * </ul> 
	 * 
	 * @param recipients a list of email addresses
	 * @param subject the subject of your email
	 * @param bodyTextHTML the body of your email in text/html
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public EmailNotification(List<String> recipients, String subject, String bodyTextHTML, HttpServletRequest httpServletRequest) {
		this(new EmailBuilder(subject, httpServletRequest, recipients.toArray(new String[recipients.size()])).contentTextHTML(bodyTextHTML));
	}

	/**
	 * private constructor for the see {@EmailBuilder}
	 * @param builder
	 */
	private EmailNotification(EmailBuilder builder) {
		this.emailSubject = builder.subject;
		this.request = builder.httpServletRequest;
		this.emailrecipients = builder.emailrecipients;
		this.selectedTemplate = builder.template;
		if (this.selectedTemplate != null) {
			this.emailBodyTextHTML =  new StringBuffer(selectedTemplate.getTextHTML()).append(getWarningLegalText(false));
			this.emailBodyTextPlain =  new StringBuffer(selectedTemplate.getTextPLAIN()).append(getWarningLegalText(true));
		} else {
			this.emailBodyTextHTML =  (builder.bodyTextHTML != null) ? new StringBuffer(builder.bodyTextHTML).append(getWarningLegalText(false)) : null;
			this.emailBodyTextPlain = (builder.bodyTextPlain != null) ? new StringBuffer(builder.bodyTextPlain).append(getWarningLegalText(true)) : null;
		}
		
		this.emailRecipientsInCC = builder.emailRecipientsInCC;
		this.emailRecipientsInBCC = builder.emailRecipientsInBCC;
	}

	/**
	 * @deprecated use fluent API {@link} EmailBuilder e.g. EmailNotification en = new EmailBuilder(subject, bodyTextHTML, httpServletRequest, recipients).cc(email).build();
	 * @param email
	 */
	public void addRecipientInCC(String email) {
		try {
			emailRecipientsInCC.add(new InternetAddress(email));
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}
	/**
	 * You can also use fluent API {@link} EmailBuilder e.g. EmailNotification en = new EmailBuilder(subject, bodyTextHTML, httpServletRequest, recipients).bcc(email).build();
	 * @param email the recipient addresses
	 */
	public void addRecipientInBCC(String email) {
		try {
			emailRecipientsInBCC.add(new InternetAddress(email));
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}

	/**
	 * You can also use {@link} EmailBuilder e.g. EmailNotification en = new EmailBuilder(subject, bodyTextHTML, httpServletRequest, recipients).bcc(email).build();
	 * @param email the recipient addresses
	 */
	public void addContentTextPlain(String bodyTextPlain) {
		this.emailBodyTextPlain = new StringBuffer(bodyTextPlain).append(getWarningLegalText(true));
	}
	/**
	 * Sends the email message. The message can be anything with any content and that must be delivered to something or someone.
	 *
	 */
	@SuppressWarnings("deprecation")
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
			
			if (emailBodyTextPlain != null) {
				final MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(emailBodyTextPlain.toString(), "text/plain; charset=UTF-8"); 

				final MimeBodyPart htmlPart = new MimeBodyPart();
				htmlPart.setContent(emailBodyTextHTML.toString(), "text/html; charset=UTF-8");

				final Multipart mp = new MimeMultipart("alternative");
				mp.addBodyPart(textPart);
				mp.addBodyPart(htmlPart);
				// Set Multipart as the message's content
				mimeMessage.setContent(mp);
			} else {
				mimeMessage.setContent(emailBodyTextHTML.toString(), "text/html; charset=UTF-8");
			}
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
	/**
	 * 
	 * @param plainText
	 * @return
	 */
	private static StringBuffer getWarningLegalText(boolean plainText) {
		StringBuffer toReturn = new StringBuffer();
		if (!plainText) {
			toReturn.append("<p>")
			.append("<p><div style=\"color:#999999; font-size:10px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif; padding-top:15px;\">")
			.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain ")
			.append("information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message. ")
			.append("If you have received this communication in error, please notify the sender and destroy and delete any copies you may have received.")
			.append("</div></p>");
		} else {
			toReturn.append("\n\n---------\n")
			.append("WARNING / LEGAL TEXT: This message is intended only for the use of the individual or entity to which it is addressed and may contain ")
			.append("information which is privileged, confidential, proprietary, or exempt from disclosure under applicable law. \n If you are not the intended recipient or the person responsible for delivering the message to the intended recipient, you are strictly prohibited from disclosing, distributing, copying, or in any way using this message. ")
			.append("If you have received this communication in error, please notify the sender and destroy and delete any copies you may have received.");
		}
		return toReturn;
	}


	@Override
	public String toString() {
		return "EmailNotification [emailrecipients=" + Arrays.toString(emailrecipients) + ", emailRecipientsInCC="
				+ emailRecipientsInCC + ", emailRecipientsInBCC=" + emailRecipientsInBCC + ", emailSubject="
				+ emailSubject + ", emailBodyTextHTML=" + emailBodyTextHTML + ", emailBodyTextPlain="
				+ emailBodyTextPlain + "]";
	}
	/**
	 * 
	 * EmailBuilder class for builder pattern
	 *
	 */
	public static class EmailBuilder {

		private final String[] emailrecipients;
		/**
		 * The subject
		 */
		private final String subject;
		/**
		 * The template, if any
		 */
		private Template template;
		/**
		 * The email body content in HTML
		 */
		private String bodyTextHTML;
		/**
		 * The email body content in Simple Text
		 */
		private String bodyTextPlain;
		/**
		 * The list of recipient addresses with cc
		 */
		private List<InternetAddress> emailRecipientsInCC;
		/**
		 * The list of recipient addresses with cbc
		 */
		private List<InternetAddress> emailRecipientsInBCC;
		
		private HttpServletRequest httpServletRequest;
		
		
		/**
		 * 
		 * Initialize the email with the following information:
		 * <ul>
		 * <li>The subject of the mail</li>
		 * <li>The body of the mail in HTML</li>
		 * <li>One or several "to" recipient addresses</li>
		 * </ul> 
		 * 
		 * @param emailrecipients an list of email addresses to be added in the TO 
		 * @param subject the subject of your email
		 * @param bodyTextHTML the body of your email in HTML
		 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
		 */
		public EmailBuilder(String subject, HttpServletRequest httpServletRequest, String ... emailrecipients) {
			this.subject = subject;
			this.httpServletRequest = httpServletRequest;
			this.emailrecipients = emailrecipients;
			emailRecipientsInCC = new ArrayList<InternetAddress>();
			emailRecipientsInBCC = new ArrayList<InternetAddress>();
		}
		/**
		 * 
		 * @param bodyTextPlain
		 * @return
		 */
		public EmailBuilder withTemplate(Template selectedTemplate) {
			this.template = selectedTemplate;
			return this;
		}
		/**
		 * 
		 * @param bodyTextHTML
		 * @return
		 */
		public EmailBuilder contentTextHTML(String bodyTextHTML) {
			this.bodyTextHTML = bodyTextHTML;
			return this;
		}
		/**
		 * 
		 * @param bodyTextPlain
		 * @return
		 */
		public EmailBuilder contentTextPlain(String bodyTextPlain) {
			this.bodyTextPlain = bodyTextPlain;
			return this;
		}
		/**
		 * Add a "cc" recipient address.
		 * 
		 * @param cc one or several recipient addresses
		 * @return this instance for fluent use
		 */
		public EmailBuilder cc(String... cc) {
			for (String email : cc) {
				try {
					this.emailRecipientsInCC.add(new InternetAddress(email));
				} catch (AddressException e) {
					e.printStackTrace();
				}
			}			
			return this;
		}
		/**
		 * Add a "bcc" recipient address.
		 * 
		 * @param bcc  one or several recipient addresses
		 * @return this instance for fluent use
		 */
		public EmailBuilder bcc(String... bcc) {
			for (String email : bcc) {
				try {
					this.emailRecipientsInBCC.add(new InternetAddress(email));
				} catch (AddressException e) {
					e.printStackTrace();
				}
			}			
			return this;
		}
		/**
		 * build the see {@link EmailNotification} object
		 */
		public EmailNotification build() {
			EmailNotification email = new EmailNotification(this);
			validateUserObject(email);
			return email;
		}

		private void validateUserObject(EmailNotification email) {
			if (emailrecipients.length == 0) {
				throw new IllegalArgumentException("The array of email recipients cannot be empty");
			}
			//if a template exists but also the body params are not empty
			if (template != null && (bodyTextHTML != null || bodyTextPlain != null)) {
				throw new IllegalArgumentException("Template is not null, but also the bodyText (plain or html) which one should I use?");
			}
		}
	}
}


