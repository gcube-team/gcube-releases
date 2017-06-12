package gr.cite.geoanalytics.util.mail.mailer;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.SmtpConfig.SMTPAuthentication;

@Component
public class GeoanalyticsJavaMailer implements Mailer
{
	private Configuration configuration = null;
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public void sendTo(String recipient, List<String> cc, List<String> bcc,
			String subject, String text) throws Exception
	{
		String from = "admin@geopolis-app.com";

		//String host = "localhost";
		
		Properties properties = System.getProperties();
		//properties.setProperty("mail.smtp.host", host);
		if(configuration.getSmtpConfig().isSmtpAuthenticationEnabled())
			properties.put("mail.smtp.auth", "true");
		
		properties.put("mail.smtp.host", configuration.getSmtpConfig().getSmtpServer());
		properties.put("mail.smtp.port", configuration.getSmtpConfig().getSmtpServerPort());

		Session session = null;
		if(configuration.getSmtpConfig().isSmtpAuthenticationEnabled())
		{
			if(configuration.getSmtpConfig().getSmtpAuthenticationType() == SMTPAuthentication.TLS)
				properties.put("mail.smtp.starttls.enable", "true");
			else
			{
				properties.put("mail.smtp.socketFactory.port", configuration.getSmtpConfig().getSmtpServerPort());
				properties.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
			}
			//TODO
			session = Session.getInstance(properties,
					  new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication("", "");
						}
					  });
		}
		else
			session = Session.getDefaultInstance(properties);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				recipient));
		message.setSubject(subject);
		message.setContent(text, "text/html");

		Transport.send(message);
	}

}
