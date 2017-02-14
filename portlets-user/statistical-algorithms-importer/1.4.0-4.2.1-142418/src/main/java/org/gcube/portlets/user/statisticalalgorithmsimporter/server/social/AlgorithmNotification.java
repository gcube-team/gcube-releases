package org.gcube.portlets.user.statisticalalgorithmsimporter.server.social;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TDMNotification notification sharing TR, templates or rules
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AlgorithmNotification extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(AlgorithmNotification.class);
	private HttpServletRequest httpServletRequest;
	private ServiceCredentials serviceCredentials;

	// private NotificationType notificationType;
	private Project project;
	private ArrayList<Recipient> recipients;

	public AlgorithmNotification(HttpServletRequest httpServletRequest,
			ServiceCredentials serviceCredentials, Project project,
			ArrayList<Recipient> recipients) {
		this.serviceCredentials = serviceCredentials;
		this.project = project;
		this.recipients = recipients;
		this.httpServletRequest = httpServletRequest;
		// this.notificationType = NotificationType.SAI_ALGORITHM_PUBLICATION;

	}

	public void run() {
		algorithmPublicationEmail();
	}

	private void algorithmPublicationEmail() {
		try {
			Workspace workspace = HomeLibrary
					.getUserWorkspace(serviceCredentials.getUserName());

			List<String> recipientIds = retrieveListAddressee();

			List<GenericItemBean> recipients = retrieveRecipients();

			String subject = "[SAI] New software publication requested";
			String body = "The user "
					+ serviceCredentials.getFullName()
					+ "\n\n has requested to publish the algorithm "
					+ project.getInputData().getProjectInfo()
							.getAlgorithmName()
					+ " with the following jar "
					+ project.getProjectTarget().getProjectDeploy()
							.getCodeJar().getPublicLink();

			String messageId;

			messageId = workspace.getWorkspaceMessageManager()
					.sendMessageToPortalLogins(subject, body,
							new ArrayList<String>(), recipientIds);

			logger.debug("Sending message notification to: "
					+ recipientIds.toString());

			SocialNetworkingSite site = new SocialNetworkingSite(
					httpServletRequest);
			SocialNetworkingUser user = new SocialNetworkingUser(
					serviceCredentials.getUserName(),
					serviceCredentials.getEmail(),
					serviceCredentials.getFullName(),
					serviceCredentials.getUserAvatarURL());
			NotificationsManager nm = new ApplicationNotificationsManager(site,
					serviceCredentials.getScope(), user);

			Thread thread = new Thread(new MessageNotificationsThread(
					recipients, messageId, subject, body, nm));
			thread.start();

		} catch (InternalErrorException | WorkspaceFolderNotFoundException
				| HomeNotFoundException e) {
			logger.error("AlgorithmPublicationEmail(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();

		}

	}

	private List<GenericItemBean> retrieveRecipients() {
		List<GenericItemBean> genericItemBeanRecipients = new ArrayList<GenericItemBean>();
		for (Recipient recipient : recipients) {
			genericItemBeanRecipients.add(new GenericItemBean(recipient
					.getUser(), recipient.getUser(), recipient.getName() + " "
					+ recipient.getSurname(), ""));
		}

		return genericItemBeanRecipients;
	}

	private List<String> retrieveListAddressee() {
		ArrayList<String> addressee = new ArrayList<String>();
		for (Recipient recipient : recipients) {
			addressee.add(recipient.getUser());
		}
		return addressee;

	}

}
