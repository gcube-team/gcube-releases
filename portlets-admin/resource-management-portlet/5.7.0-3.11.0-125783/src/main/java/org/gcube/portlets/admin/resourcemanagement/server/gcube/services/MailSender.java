/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: MailSender.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.server.gcube.services;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.gcube.resourcemanagement.support.server.utils.ServerConsole;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class MailSender {
	private static final String LOG_PREFIX = "[RPM-SENDMAIL]";

	/**
	 * Given a list of addresses in the form addr1;addr2...
	 * builds the list of InternetAddress to use to send mail.
	 * @param toParse a string of email addresses of the form addr1;addr2...
	 * @return the array of converted valid email addresses
	 */
	private static InternetAddress[] buildAddress(final String toParse) {
		if (toParse == null || toParse.trim().length() == 0) {
			return null;
		}

		List<InternetAddress> toReturn = new Vector<InternetAddress>();
		StringTokenizer parser = new StringTokenizer(toParse, ";");
		while (parser.hasMoreTokens()) {
			try {
				toReturn.add(new InternetAddress(parser.nextToken()));
			} catch (AddressException e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
		}
		return toReturn.toArray(new InternetAddress[]{});
	}

	public static void sendMail(
			final String sender,
			final String target,
			final String cc,
			final String subject,
			final String bodyText,
			final String[] attachments) throws Exception {

		if ((target == null || target.trim().length() == 0)
				&& (cc == null || cc.trim().length() == 0)) {
			ServerConsole.warn(LOG_PREFIX, "No valid mail recipients specified.");
			return;
		}

		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.put("mail.host", "smtp.isti.cnr.it");
		properties.put("mail.port", "587");
		properties.put("mail.auth", "true");
		properties.put("mail.smtps.auth", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(properties,
				new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("smtp-user", "UiBe7chae7eh");
			}
		});
		Transport transport = null;
		try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			throw e;
		}

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));

			// MAIL - TO
			InternetAddress[] addressTo = MailSender.buildAddress(target);
			if (addressTo != null) {
				message.setRecipients(Message.RecipientType.TO, addressTo);
			}

			// MAIL - CC
			InternetAddress[] addressCC = MailSender.buildAddress(cc);
			if (addressCC != null) {
				message.setRecipients(Message.RecipientType.CC, addressCC);
			}

			message.setSubject(subject);
			message.setSentDate(new Date());

			//
			// Set the email message text.
			//
			MimeBodyPart messagePart = new MimeBodyPart();
			messagePart.setText(bodyText);

			//
			// Set the email attachment file
			//

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messagePart);

			for (String filename : attachments) {
				MimeBodyPart attachmentPart = new MimeBodyPart();
				FileDataSource fileDataSource = new FileDataSource(filename) {
					@Override
					public String getContentType() {
						return "application/octet-stream";
					}
				};
				attachmentPart.setDataHandler(new DataHandler(fileDataSource));
				attachmentPart.setFileName(new File(filename).getName());
				multipart.addBodyPart(attachmentPart);
			}

			message.setContent(multipart);

			transport.connect();
			// FIXME waiting new mail implementation (skipping liferay mail.jar) with CC support.
			transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
