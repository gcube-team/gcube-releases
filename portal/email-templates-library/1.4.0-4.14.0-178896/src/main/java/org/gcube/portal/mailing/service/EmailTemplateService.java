package org.gcube.portal.mailing.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.common.portal.mailing.EmailNotification.EmailBuilder;
import org.gcube.common.portal.mailing.templates.Template;
import org.gcube.portal.mailing.message.Recipient;

/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class EmailTemplateService {
	/**
	 * 
	 * Initialize the email with the following information:
	 * <ul>
	 * <li>The subject of the email</li>
	 * <li>The template instance for this email</li>
	 * <li>The httpServletRequest object</li>
	 * <li>One or several "to" recipient addresses</li>
	 * </ul> 
	 * 
	 * @param emailrecipients an list of email addresses to be added in the TO 
	 * @param subject the subject of your email
	 * @param bodyTextHTML the body of your email in HTML
	 * @param httpServletRequest the httpServletRequest object if you have it, null otherwise (but the default sender will be applied in this case) 
	 */
	public static void send(String subject, Template selectedTemplate, HttpServletRequest httpServletRequest, Recipient ... emailrecipients) {
		List<String> toEmailrecipients = new ArrayList<>();
		List<String> ccEmailrecipients = new ArrayList<>();
		List<String> bccEmailrecipients = new ArrayList<>();
		for (Recipient recipient : emailrecipients) {
			switch (recipient.getType()) {
			case TO:
				toEmailrecipients.add(recipient.getAddress().getAddress());
				break;
			case CC:
				ccEmailrecipients.add(recipient.getAddress().getAddress());
				break;
			case BCC:
				bccEmailrecipients.add(recipient.getAddress().getAddress());
				break;
			}
		}
		EmailNotification mailToAdmin = 
				new EmailBuilder(subject, httpServletRequest, toEmailrecipients.toArray(new String[toEmailrecipients.size()]))
				.withTemplate(selectedTemplate)
				.cc(ccEmailrecipients.toArray(new String[ccEmailrecipients.size()]))
				.bcc(bccEmailrecipients.toArray(new String[bccEmailrecipients.size()]))
				.build();
		
		mailToAdmin.sendEmail();
	}
}
