package org.gcube.portal.social.networking.ws.methods.v1;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

/**
 * Messages services REST interface
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
@Path("/messages")
public class Messages {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Messages.class);

	// recipients separator
	private static final String RECIPIENTS_ID_SEPARATOR = ","; 

	//	user manager
	private UserManager um = new LiferayUserManager();

	@POST
	@Path("writeMessageToUsers/")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	/**
	 * Try to send a message to recipientsIds. The sender is the owner of the gcube-token if not otherwise stated.
	 * @param body
	 * @param subject
	 * @return ok on success, error otherwise
	 */
	public Response writeMessageToUsers(
			@FormParam("sender") String sender, // the optional sender, if missing the sender will be the token's owner.
			@FormParam("body") String body,
			@FormParam("subject") String subject,
			@FormParam("recipients") String recipientsIds,
			@Context HttpServletRequest httpServletRequest) {

		if(body == null || body.isEmpty() || subject == null || subject.isEmpty() || recipientsIds == null || recipientsIds.isEmpty()){

			logger.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.missingParameters).build();

		}

		Caller caller = AuthorizationProvider.instance.get();
		String senderId = caller.getClient().getId();
		String scope = ScopeProvider.instance.get();

		// check on sender id
		if(sender == null || sender.isEmpty())
			logger.info("Sender is going to be the token's owner [" + senderId  + "]");
		else{
			logger.info("Sender is going to be " + sender);
			senderId = sender;
		}

		// get the recipients ids (simple check, trim)
		List<String> recipientsListFiltered = new ArrayList<String>();
		String[] splittedRecipientsIds = recipientsIds.split(RECIPIENTS_ID_SEPARATOR);
		List<GenericItemBean> recipientsBeans = new ArrayList<GenericItemBean>();
		for (String recipientId : splittedRecipientsIds) {
			try{
				String tempId = recipientId.trim();
				if(tempId.isEmpty())
					continue;
				GCubeUser userRecipient = um.getUserByUsername(tempId);
				GenericItemBean beanUser = new GenericItemBean(userRecipient.getUsername(), userRecipient.getUsername(), userRecipient.getFullname(), userRecipient.getUserAvatarURL());
				recipientsBeans.add(beanUser);
				recipientsListFiltered.add(tempId);
			}catch(Exception e){
				logger.error("Unable to retrieve recipient information for recipient with id " + recipientId, e);
			}
		}

		if(recipientsListFiltered.isEmpty()){

			logger.error("Missing/wrong request parameters");
			return Response.status(Status.BAD_REQUEST).entity(ErrorMessages.badRequest).build();

		}

		try{

			logger.info("Trying to send message with body " + body + " subject " + subject + " to user " + recipientsIds + " from " + senderId);

			// sender info
			GCubeUser senderUser = um.getUserByUsername(senderId);
			Workspace workspace = HomeLibrary.getUserWorkspace(senderId);

			// send message
			logger.debug("Sending message to " + recipientsListFiltered);
			String messageId = workspace.getWorkspaceMessageManager()
					.sendMessageToPortalLogins(subject, body,
							new ArrayList<String>(), recipientsListFiltered);

			// send notification
			logger.debug("Message sent to " + recipientsIds + ". Sending message notification to: " + recipientsIds);
			SocialNetworkingSite site = new SocialNetworkingSite(
					httpServletRequest);
			SocialNetworkingUser user = new SocialNetworkingUser(
					senderUser.getUsername(), senderUser.getEmail(),
					senderUser.getFullname(), senderUser.getUserAvatarURL());

			NotificationsManager nm = new ApplicationNotificationsManager(site, scope, user);
			new Thread(new MessageNotificationsThread(recipientsBeans, messageId, subject, body, nm)).start();

		}catch(Exception e){

			logger.error("Unable to send message.", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		}

		return Response.status(Status.OK).build();
	}
}
