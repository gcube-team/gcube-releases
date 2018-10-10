package org.gcube.applicationsupportlayer.social.mailing;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.gcube.applicationsupportlayer.social.ScopeBeanExt;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class EmailNotificationsConsumer extends Thread {
	private static Logger _log = LoggerFactory.getLogger(EmailNotificationsConsumer.class);
	private static final String SERVICE_ENDPOINT_CATEGORY = "SMTPServer";
	private static final String SERVICE_ENDPOINT_NAME = "SMTP-ISTI";

	private String smtpUsername;
	private String smtpPasswd;
	private String mailServiceHost = "localhost";
	private String mailServicePort = "25";

	public EmailNotificationsConsumer(String context) {
		super();
		_log.info("EmailNotificationsConsumer thread started at " + new Date() + " trying to fetch SMTP configuration from infrastructure ...");
		//query
		try {
			String currScope = 	ScopeProvider.instance.get();
			ScopeBeanExt sbe = new ScopeBeanExt(context);
			String infraContext = sbe.getInfrastructureScope();
			List<ServiceEndpoint> resources = getConfigurationFromIS(infraContext);
			if (resources.size() > 1) {
				_log.error("Too many Service Endpoints having name " + SERVICE_ENDPOINT_NAME +" in this scope having Category " + SERVICE_ENDPOINT_CATEGORY);
			}
			else if (resources.size() == 0){
				_log.warn("There is no Service Endpoint having name " + SERVICE_ENDPOINT_NAME +" and Category " + SERVICE_ENDPOINT_CATEGORY + " in this scope. Using localhost:25");
			}
			else {
				for (ServiceEndpoint res : resources) {
					AccessPoint found = res.profile().accessPoints().iterator().next();
					mailServiceHost = found.address().split(":")[0].trim();
					mailServicePort = found.address().split(":")[1].trim();
					smtpUsername = found.username();
				
					ScopeProvider.instance.set(infraContext);
					smtpPasswd = StringEncrypter.getEncrypter().decrypt(found.password());
					ScopeProvider.instance.set(currScope);
					_log.info("Found SMTP Configuration: "+mailServiceHost+":"+mailServicePort+ " usr="+smtpUsername+ " pwd=*******");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 
	 * @param context 
	 * @return the
	 * @throws Exception
	 */
	private List<ServiceEndpoint> getConfigurationFromIS(String infraContext) throws Exception  {
;		
		String scope = infraContext;
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ SERVICE_ENDPOINT_CATEGORY +"'");
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ SERVICE_ENDPOINT_NAME +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}	

	@Override
	public void run() {
		Properties props = System.getProperties();
		Session session = null;
		props.put("mail.smtp.host", mailServiceHost);
		props.put("mail.smtp.port", mailServicePort);
		//if there is a service endpoint defined in the infrastructure for the SMTP Server authenticate against it
		if (smtpUsername != null) {
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "false");
			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUsername, smtpPasswd);
				}
			});
		}
		else { //use localhost (probaly postfix instance)
			session = Session.getDefaultInstance(props);
		}
		
		session.setDebug(true);
		
		for (;;) {
			try {
				Thread.sleep(1000*EmailPlugin.SECONDS2WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//_log.debug("Checking Emails Buffer ... ");
			if (EmailPlugin.BUFFER_EMAILS != null && EmailPlugin.BUFFER_EMAILS.size() > 0) {
				_log.debug("Emails Buffer not empty, sending emails ");
				Transport t = null;
				try {
					t = session.getTransport("smtp");
					t.connect();

					//sync method to ensure the producer do not put new emails in the meantime
					synchronized(EmailPlugin.BUFFER_EMAILS){
						for (NotificationMail mail : EmailPlugin.BUFFER_EMAILS) {
							Message m = mail.getMessageNotification(session);
							if (m != null) {
								m.saveChanges();
								Address[] addresses = m.getAllRecipients();
								try {
									t.sendMessage(m, addresses);
								}
								catch (com.sun.mail.smtp.SMTPSendFailedException ex) {									
									_log.error("Error while trying to send emails, emptying the buffer...");
									EmailPlugin.BUFFER_EMAILS = new ArrayList<NotificationMail>();
									ex.printStackTrace();													
								}
								_log.debug("Message sent to " + mail.getNotification2Send().getUserid());
							}
							else {
								_log.warn("Message not sent to " + mail.getNotification2Send().getUserid());
							}
						}
						//close session and empty the buffer
						_log.info("Emails sent emptying the buffer");
						EmailPlugin.BUFFER_EMAILS = new ArrayList<NotificationMail>();					
						t.close();
					}

				}
				catch (Exception e) {
					_log.error("Exception while trying to send emails, emptying the buffer...");
					EmailPlugin.BUFFER_EMAILS = new ArrayList<NotificationMail>();					
					e.printStackTrace();
					try {
						t.close();
					} catch (MessagingException e1) {
						e1.printStackTrace();
					}

				}				
			}
		}		
	}
}
