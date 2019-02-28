package org.gcube.portlets.user.statisticalalgorithmsimporter.server.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.SessionUtil;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.InformationSystemUtils;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AlgorithmNotification extends Thread {
	private static final String MESSAGE_SUBJECT = "[SAI] New software publication requested";
	private static Logger logger = LoggerFactory.getLogger(AlgorithmNotification.class);
	private HttpServletRequest httpServletRequest;
	private ServiceCredentials serviceCredentials;
	private String socialNetworkingServiceURL;
	private ArrayList<Recipient> recipients;
	private String body;
	private boolean serviceUp;

	public AlgorithmNotification(HttpServletRequest httpServletRequest, ServiceCredentials serviceCredentials,
			String body) {
		this.serviceCredentials = serviceCredentials;
		this.httpServletRequest = httpServletRequest;
		this.body = body;
	}

	public void run() {
		algorithmPublicationEmail();
	}

	private void algorithmPublicationEmail() {
		try {
			retrieveSocialNetworkingService();
			testSocialNetworksingServiceIsUp();
			retrieveRecipient();
			sendEmailToAdministrators();
		} catch (Throwable e) {
			logger.error("AlgorithmPublicationEmail(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();

		}

	}

	public void retrieveSocialNetworkingService() {
		try {
			socialNetworkingServiceURL = InformationSystemUtils
					.retrieveSocialNetworkingService(serviceCredentials.getScope());
			logger.info("SocialNetworkingServiceURL: " + socialNetworkingServiceURL);
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);

		}
	}

	private void testSocialNetworksingServiceIsUp() {
		if (socialNetworkingServiceURL == null || socialNetworkingServiceURL.isEmpty()) {
			logger.error("SocialNetworkingService URL is undefined");
			serviceUp = false;
			return;
		} else {
			try {
				String requestUrl = socialNetworkingServiceURL + "/";
				logger.debug("SocialNetworkingUrl request=" + requestUrl);

				URL urlObj = new URL(requestUrl);
				HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
				connection.setRequestMethod("GET");
				int code = connection.getResponseCode();
				if (code == 200) {
					serviceUp = true;
				} else {
					serviceUp = false;
				}

				return;
			} catch (MalformedURLException e) {
				logger.error("SocialNetworksingService URL seems to be invalid: " + e.getLocalizedMessage(), e);
				serviceUp = false;
			} catch (IOException e) {
				logger.error("SocialNetworksingService error occured in request: " + e.getLocalizedMessage(), e);
				serviceUp = false;
			} catch (Throwable e) {
				logger.error("SocialNetworksingService error occured: " + e.getLocalizedMessage(), e);
				serviceUp = false;
			}
		}
	}

	private void retrieveRecipient() {
		if (serviceUp) {
			logger.error("SocialNetworkingService retrieve administartors");

			// https://socialnetworking-d-d4s.d4science.org/social-networking-library-ws/rest/2/users/get-usernames-by-role?role-name=DataMiner-Manager&gcube-token=
			// serviceURL=http://socialnetworking-d-d4s.d4science.org:80/social-networking-library-ws/rest
			// we must add this
			// 2/users/get-usernames-by-role?role-name=DataMiner-Manager&gcube-token=
			try {
				String requestUrl = socialNetworkingServiceURL
						+ "/2/users/get-usernames-by-role?role-name=DataMiner-Manager&gcube-token="
						+ serviceCredentials.getToken();
				logger.debug("SocialNetworkingService request=" + requestUrl);

				URL urlObj = new URL(requestUrl);
				HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
				connection.setRequestMethod("GET");
				// connection.setRequestProperty("Authorization", "Basic " +
				// encoded);
				InputStream is = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuffer usersWithAdminRole = new StringBuffer();
				logger.info("SocialNetworkingService retrieve response");
				while ((line = reader.readLine()) != null) {
					usersWithAdminRole.append(line);
				}
				
				
				logger.debug("SocialNetworkingService response: "+usersWithAdminRole.toString());
				convertJSONIntoRecipient(usersWithAdminRole.toString());
				return;

			} catch (MalformedURLException e) {
				logger.error("SocialNetworkingService URL seems to be invalid: " + e.getLocalizedMessage(), e);
				logger.info("Use default administrators for notifications");
				recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());

			} catch (IOException e) {
				logger.error("SocialNetworkingService error occured in request: " + e.getLocalizedMessage(), e);
				logger.info("Use default administrators for notifications");
				recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());
			} catch (Throwable e) {
				logger.error("SocialNetworkingService error occured: " + e.getLocalizedMessage(), e);
				logger.info("Use default administrators for notifications");
				recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());
			}
		} else {
			logger.info("Use default administrators for notifications");
			recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());
		}

	}

	private void convertJSONIntoRecipient(String usersWithAdminRole) {
		try {
			JSONObject usersWithAdminRoleObject = new JSONObject(usersWithAdminRole);
			boolean requestSuccess = usersWithAdminRoleObject.getBoolean("success");
			if (requestSuccess) {
				recipients = new ArrayList<>();
				JSONArray usersAdminArray = usersWithAdminRoleObject.getJSONArray("result");
				for (int i = 0; i < usersAdminArray.length(); i++) {
					String usernameAdmin = usersAdminArray.getString(i);
					if (usernameAdmin != null && !usernameAdmin.isEmpty()) {
						Recipient userAdmin = new Recipient(usernameAdmin, "", "");
						recipients.add(userAdmin);
					}
				}
				if (recipients.isEmpty()) {
					recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());
					logger.info("Use default administrators for notifications");
				}

			} else {
				recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());
				logger.info("Use default administrators for notifications");

			}

		} catch (Throwable e) {
			logger.error("SocialNetworkingService error occured retrieving administrators: " + e.getLocalizedMessage(),
					e);
			logger.info("Use default administrators for notifications");
			recipients = SessionUtil.getDefaultRecipients(httpServletRequest.getServletContext());

		}
	}

	private void sendEmailToAdministrators() {
		if (serviceUp) {
			sendByService();
		} else {
			//sendByNotificationManager();
		}

	}

	private void sendByService() {
		// 2/messages/write-message
		try {
			String requestUrl = socialNetworkingServiceURL + "/2/messages/write-message?gcube-token="
					+ serviceCredentials.getToken();
			logger.debug("SocialNetworkingService request=" + requestUrl);

			URL urlObj = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			String parameter = createJSONMessage();
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(parameter);
			wr.flush();
			wr.close();

			int code = connection.getResponseCode();
			logger.info("Response Code: " + code);
			if (code == 200 || code == 201) {
				logger.info("Mesage send to administrators");
				InputStream is = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuffer response = new StringBuffer();
				logger.info("SocialNetworkingService retrieve response");
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				logger.debug("SocialNetworkingService response: " + response.toString());
			} else {
				if (code == 500) {

					logger.error("Error sending message to administrators");
					InputStream es = connection.getErrorStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(es));
					String line = null;
					StringBuffer response = new StringBuffer();
					logger.info("SocialNetworkingService retrieve error");
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					logger.debug("SocialNetworkingService error: " + response.toString());
				} else {

				}
			}
		} catch (MalformedURLException e) {
			logger.error("SocialNetworkingService URL seems to be invalid: " + e.getLocalizedMessage(), e);
			
		} catch (IOException e) {
			logger.error("SocialNetworkingService error occured in request: " + e.getLocalizedMessage(), e);
			
		} catch (Throwable e) {
			logger.error("SocialNetworkingService error occured: " + e.getLocalizedMessage(), e);
			
		}

	}

	private String createJSONMessage() {
		try {
			//
			// "body": "string" /* The body of the message */
			// "subject": "string" /* The subject of the message */
			// "recipients": [
			// { /* A recipient object */
			// "id": "string" /* The id of the recipient */
			// }
			// ]
			//
			
			JSONObject message = new JSONObject();
			message.put("subject", MESSAGE_SUBJECT);
			String bodyEscaped=body.replace("\\", "\\\\");
			logger.debug("body: "+body);
			logger.debug("bodyEscaped: "+bodyEscaped);
			
			message.put("body", bodyEscaped);
			JSONArray messageRecipipientsArray = new JSONArray();
			for (Recipient recipient : recipients) {
				JSONObject rec = new JSONObject();
				rec.put("id", recipient.getUser());
				messageRecipipientsArray.put(rec);
			}
			message.put("recipients", messageRecipipientsArray);
			logger.debug("Message: "+message.toString());
			return message.toString();
		} catch (Throwable e) {
			logger.error("SocialNetworkingService error in message creation: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw e;
		}

	}

	
	/*
	private void sendByNotificationManager() {
		try {
			Workspace workspace = HomeLibrary.getUserWorkspace(serviceCredentials.getUserName());

			List<String> recipientIds = retrieveListAddressee();

			List<GenericItemBean> recipients = retrieveRecipients();

			String subject = MESSAGE_SUBJECT;

			String messageId;

			messageId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, body,
					new ArrayList<String>(), recipientIds);

			logger.debug("Sending message notification to: " + recipientIds.toString());

			SocialNetworkingSite site = new SocialNetworkingSite(httpServletRequest);
			SocialNetworkingUser user = new SocialNetworkingUser(serviceCredentials.getUserName(),
					serviceCredentials.getEmail(), serviceCredentials.getFullName(),
					serviceCredentials.getUserAvatarURL());
			NotificationsManager nm = new ApplicationNotificationsManager(site, serviceCredentials.getScope(), user);

			Thread thread = new Thread(new MessageNotificationsThread(recipients, messageId, subject, body, nm));
			thread.start();

		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException e) {
			logger.error("Error send SAI notfications to admin: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}

	}

	private List<GenericItemBean> retrieveRecipients() {
		List<GenericItemBean> genericItemBeanRecipients = new ArrayList<GenericItemBean>();
		for (Recipient recipient : recipients) {
			genericItemBeanRecipients.add(new GenericItemBean(recipient.getUser(), recipient.getUser(),
					recipient.getName() + " " + recipient.getSurname(), ""));
		}

		return genericItemBeanRecipients;
	}

	private List<String> retrieveListAddressee() {
		ArrayList<String> addressee = new ArrayList<String>();
		for (Recipient recipient : recipients) {
			addressee.add(recipient.getUser());
		}
		return addressee;

	} */

}
