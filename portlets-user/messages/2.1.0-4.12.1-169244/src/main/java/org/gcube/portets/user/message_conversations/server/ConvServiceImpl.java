package org.gcube.portets.user.message_conversations.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.portets.user.message_conversations.client.MessageService;
import org.gcube.portets.user.message_conversations.shared.ConvMessage;
import org.gcube.portets.user.message_conversations.shared.CurrUserAndPortalUsersWrapper;
import org.gcube.portets.user.message_conversations.shared.FileModel;
import org.gcube.portets.user.message_conversations.shared.MessageUserModel;
import org.gcube.portets.user.message_conversations.shared.WSUser;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
/**
 * @author Massimiliano Assante, CNR-ISTI
 */
@SuppressWarnings("serial")
public class ConvServiceImpl extends RemoteServiceServlet implements MessageService {
	private static final Logger _log = LoggerFactory.getLogger(ConvServiceImpl.class);

	private PortalContext pContext;
	private UserManager um;

	public void init() {
		um =  new LiferayUserManager();
		pContext = PortalContext.getConfiguration();
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}

	private GCubeUser getCurrentUser(HttpServletRequest httpServletRequest) {	
		if (isWithinPortal()) {
			try {
				long userId = PortalUtil.getUser(httpServletRequest).getUserId();
				long groupId = pContext.getCurrentGroupId(httpServletRequest);
				if (GroupLocalServiceUtil.hasUserGroup(userId, groupId))
					return um.getUserById(userId);
				else {
					_log.error("User not authorised in Group, the logged user id=" + userId + " does not belong to group " + groupId);
					return null;
				}
			} catch (Exception e) {
				_log.warn("Could not read user from LR PortalUtil in delegate servlet");
				return null;
			}
		} else {			
			return pContext.getCurrentUser(getThreadLocalRequest());
		}
	}

	@Override
	public ArrayList<ConvMessage> getMessages(boolean sent) {
		ArrayList<ConvMessage> toReturn = new ArrayList<>();
		try {
			GCubeUser user = getCurrentUser(getThreadLocalRequest());
			pContext = PortalContext.getConfiguration();		
			_log.debug("*** Reading user = " +user.getFullname());
			String scope = pContext.getCurrentScope(getThreadLocalRequest());
			ScopeProvider.instance.set(scope);
			String token = pContext.getCurrentUserToken(scope, user.getUserId());
			SecurityTokenProvider.instance.set(token);
			Workspace workspace = null;
			try {
				HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
				HomeManager manager = factory.getHomeManager();
				workspace = manager.getHome().getWorkspace();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			List<WorkspaceMessage> listMessages = null;

			listMessages = (sent) ? 
					workspace.getWorkspaceMessageManager().getSentMessages() : workspace.getWorkspaceMessageManager().getReceivedMessages();
					if (listMessages == null) {
						_log.error("listMessages is null, Could not read messages of " + user.getFullname());
						return null;
					}
					_log.debug("Got messages of " + user.getFullname() + " how many? " + listMessages.size());
					//the messages are returned from the oldest to the new one, so we reverse them
					Collections.reverse(listMessages);
					LiferayUserManager um = new LiferayUserManager();

					for (WorkspaceMessage m : listMessages) {
						String previewBody = m.getBody().length() > 80 ? m.getBody().substring(0, 79) + " ..." : m.getBody();
						MessageUserModel mu = null;
						GCubeUser sender = null;
						try {
							if (sent) {
								if (m.getAddresses().size() < 2) {
									GCubeUser recipient = um.getUserByUsername(m.getAddresses().get(0));
									mu = new MessageUserModel(recipient.getUserId(), recipient.getUsername(), recipient.getFullname(), recipient.getUserAvatarURL(), "", "");
								} else {
									//we have at least 2 recipients 
									GCubeUser recipient1 = um.getUserByUsername(m.getAddresses().get(0));
									GCubeUser recipient2 = um.getUserByUsername(m.getAddresses().get(1));
									String label2Display = recipient1.getFirstName() + " & " + recipient2.getFirstName();
									if (m.getAddresses().size() > 2) 
										label2Display += " & ...";
									mu = new MessageUserModel(recipient1.getUserId(), recipient1.getUsername(), label2Display, null, "", "");

								}
							} else { //received message
								sender = um.getUserByUsername(m.getSender().getPortalLogin());
								mu = new MessageUserModel(sender.getUserId(), sender.getUsername(), sender.getFullname(), sender.getUserAvatarURL(), "", "");
							}
						} catch (Exception ex) {
							if (!sent) {
								mu = new MessageUserModel(m.getSender().getPortalLogin());
							} else {
								mu = new MessageUserModel(m.getAddresses().get(0));
							}
						}
						boolean hasAttachments = !m.getAttachmentsIds().isEmpty();
						if (!sent) { //received messages

							toReturn.add(new ConvMessage(
									m.getId(), 
									m.getSubject(), 
									mu, 
									new Date(m.getSendTime().getTimeInMillis()), 
									previewBody, 
									m.isRead(),
									hasAttachments));
						} else { //sent messages
							ArrayList<MessageUserModel> recipients = new ArrayList<>();
							for (String rec : m.getAddresses()) {
								recipients.add(new MessageUserModel(rec));
							}
							toReturn.add(new ConvMessage(
									m.getId(), 
									m.getSubject(), 
									mu, 
									recipients,
									new Date(m.getSendTime().getTimeInMillis()), 
									previewBody, 
									m.isRead(),
									hasAttachments));
						}
					}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		_log.trace("get All Messages Received ");
		return toReturn;
	}

	@Override
	public boolean markMessageUnread(String messageId, boolean sent) {
		GCubeUser user =  getCurrentUser(getThreadLocalRequest());
		String scope = pContext.getCurrentScope(getThreadLocalRequest());
		SecurityTokenProvider.instance.set(pContext.getCurrentUserToken(scope, user.getUserId()));
		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
			HomeManager manager = factory.getHomeManager();
			Workspace workspace = manager.getHome().getWorkspace();
			WorkspaceMessage m = (sent) ? 
					workspace.getWorkspaceMessageManager().getSentMessage(messageId): workspace.getWorkspaceMessageManager().getReceivedMessage(messageId);
					m.setStatus(false);
					return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ConvMessage getMessageById(String messageId, boolean sent) {
		ConvMessage toReturn = null;
		try {
			GCubeUser user =  getCurrentUser(getThreadLocalRequest());
			_log.debug("*** Reading user from liferay session = " +user.getFullname());
			String scope = pContext.getCurrentScope(getThreadLocalRequest());
			SecurityTokenProvider.instance.set(pContext.getCurrentUserToken(scope, user.getUserId()));
			LiferayUserManager um = new LiferayUserManager();
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
			HomeManager manager = factory.getHomeManager();
			Workspace workspace = manager.getHome().getWorkspace();
			WorkspaceMessage m = (sent) ? 
					workspace.getWorkspaceMessageManager().getSentMessage(messageId): workspace.getWorkspaceMessageManager().getReceivedMessage(messageId);
					MessageUserModel mu = null;
					GCubeUser sender = null;
					try {
						sender = um.getUserByUsername(m.getSender().getPortalLogin());
						mu = new MessageUserModel(sender.getUserId(), sender.getUsername(), sender.getFullname(), extractDomainFromEmail(sender.getEmail()));
					} catch (Exception ex) {
						mu = new MessageUserModel(m.getSender().getPortalLogin());
					}
					ArrayList<MessageUserModel> recipients = new ArrayList<>();
					for (String recipient : m.getAddresses()) {
						try {
							GCubeUser toAdd = um.getUserByUsername(recipient);
							recipients.add(new MessageUserModel(toAdd.getUserId(), toAdd.getUsername(), toAdd.getFullname(), extractDomainFromEmail(toAdd.getEmail())));
						}
						catch (Exception ex) {
							recipients.add(new MessageUserModel(recipient));
						}
					} 

					ArrayList<FileModel> attachments = new ArrayList<>();
					List<String> attachItems = m.getAttachmentsIds();
					for (String itemId : attachItems) {
						WorkspaceItem item = workspace.getItem(itemId);
						String downloadURL = null; //removed for performanc issue and done on demand
						attachments.add(new FileModel(item.getId(), item.getName(), null, item.isFolder(), downloadURL));
					}
					boolean hasAttachments = !attachItems.isEmpty();
					toReturn = new ConvMessage(
							m.getId(), 
							m.getSubject(), 
							mu, 
							recipients,
							new Date(m.getSendTime().getTimeInMillis()), 
							m.getBody(), 
							m.isRead(),
							attachments,
							hasAttachments);
					if (!sent)
						m.setStatus(true); //marked as read
		} catch (Exception e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	@Override
	public String getAttachmentDownloadURL(String itemId) {
		GCubeUser user =  getCurrentUser(getThreadLocalRequest());
		String scope = pContext.getCurrentScope(getThreadLocalRequest());
		SecurityTokenProvider.instance.set(pContext.getCurrentUserToken(scope, user.getUserId()));
		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
			HomeManager manager = factory.getHomeManager();
			Workspace workspace = manager.getHome().getWorkspace();
			WorkspaceItem item = workspace.getItem(itemId);
			String downladURL = item.getPublicLink(false);
			downladURL = (downladURL.startsWith("https")) ? downladURL : downladURL.replace("http", "https");
			return downladURL;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean saveAttachmentToWorkspaceFolder(String itemId, String destinationFolderId) {
		GCubeUser user =  getCurrentUser(getThreadLocalRequest());
		String scope = pContext.getCurrentScope(getThreadLocalRequest());
		SecurityTokenProvider.instance.set(pContext.getCurrentUserToken(scope, user.getUserId()));
		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
			HomeManager manager = factory.getHomeManager();
			Workspace workspace = manager.getHome().getWorkspace();
			WorkspaceItem copied = workspace.copy(itemId, destinationFolderId);
			return (copied != null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteMessageById(String messageId, boolean sent) {
		GCubeUser user =  getCurrentUser(getThreadLocalRequest());
		_log.debug("deleteMessageById reading user from liferay session = " +user.getFullname() + " m id = " + messageId);
		String scope = pContext.getCurrentScope(getThreadLocalRequest());
		SecurityTokenProvider.instance.set(pContext.getCurrentUserToken(scope, user.getUserId()));
		try {
			HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
			HomeManager manager = factory.getHomeManager();
			WorkspaceMessageManager workspaceMessanger = manager.getHome().getWorkspace().getWorkspaceMessageManager();
			if (sent)  
				workspaceMessanger.deleteSentMessage(messageId);
			else {
				workspaceMessanger.deleteReceivedMessage(messageId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public ArrayList<WSUser> searchUsers(String keyword) {
		ArrayList<WSUser> toReturn = new ArrayList<>();
		if (isWithinPortal()) {
			OrderByComparator comparator = OrderByComparatorFactoryUtil.create("User_", "screenname", true);
			try {
				_log.debug("Searching " + keyword);
				List<User> lrUsers = UserLocalServiceUtil.search(ManagementUtils.getCompany().getCompanyId(), keyword, 0, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, comparator);
				for (User user : lrUsers) {
					toReturn.add(new WSUser(""+user.getUserId(), user.getScreenName(), user.getFullName(), extractDomainFromEmail(user.getEmailAddress())));
				}
			} catch (SystemException | PortalException e) {
				e.printStackTrace();
			}
		} else { //development
			for (int i = 0; i < 10; i++) {
				toReturn.add(new WSUser("andrea.rossi", "andrea.rossi", "Andrea Rossi", "m.assante@gmail.com"));
				if (i % 2 == 0)
					toReturn.add(new WSUser(""+i, "username"+i, "userGetFullname()"+i, "user.getEmail()"+i));
				else
					toReturn.add(new WSUser(""+i, "ciccio"+i, "ciccioNome"+i, "ciccioEMail"+i));
			}		
		}
		return toReturn;
	}

	@Override
	public ArrayList<WSUser> getUsersInfo(String[] usernames) {
		ArrayList<WSUser> toReturn = new ArrayList<>();
		if (isWithinPortal()) {			
			try {
				for (String username : usernames) {
					User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);
					toReturn.add(new WSUser(""+user.getUserId(), user.getScreenName(), user.getFullName(), extractDomainFromEmail(user.getEmailAddress())));
				}
			} catch (SystemException | PortalException e) {
				e.printStackTrace();
			}
		} else { //development	
			toReturn.add(new WSUser("andrea.rossi", "andrea.rossi", "Andrea Rossi", "rossi@gmail.com"));
			toReturn.add(new WSUser("ginoi", "gino", "Gino Pino", "gino@hotmail.com"));
		}
		return toReturn;
	}

	@Override
	public boolean sendToById(ArrayList<String> recipientIds, ArrayList<String> listAttachmentsId, String subject, String body) {
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currentUser = pContext.getCurrentUser(getThreadLocalRequest());

		if (listAttachmentsId == null)
			listAttachmentsId = new ArrayList<String>();
		try {
			Workspace workspace = HomeLibrary.getUserWorkspace(currentUser.getUsername());
			_log.info("Sending message to: " + recipientIds.toString());
			String checkedSubject = subject;
			String checkedBody = body;
			String messageId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(checkedSubject, checkedBody, listAttachmentsId, recipientIds);
			try {
				body += getPublicLinksForAttachs(workspace, listAttachmentsId);
			}
			catch (InternalErrorException|ItemNotFoundException e) {
				_log.error("Ops, could not generate publick link for some of the attachments");
			}
			_log.debug("Sending message notification to: " + recipientIds.toString());

			List<GenericItemBean> recipients = getUsersbyUserId(recipientIds);

			NotificationsManager nm = new ApplicationNotificationsManager(new SocialNetworkingSite(getThreadLocalRequest()), pContext.getCurrentScope(getThreadLocalRequest()), 
					new SocialNetworkingUser(
							currentUser.getUsername(),
							currentUser.getEmail(), 
							currentUser.getFullname(), 
							currentUser.getUserAvatarURL()
							));
			Thread thread = new Thread(new MessageNotificationsThread(recipients, messageId, checkedSubject, body, nm));
			thread.start();

			return (messageId != null);
		} catch (Exception e) {
			_log.error("While Sending message to: " + recipientIds.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * utility method extract the @domain.com from an email address
	 * return  @unknown-domain in case of no emails
	 */
	private String extractDomainFromEmail(String email) {
		int index =  email.indexOf('@');
		if (index > 0)
			return email.substring(index);
		else 
			return "@unknown-domain";
	}
	/**
	 * return the User instance given his id
	 * @param recipientIds
	 * @return
	 */
	private List<GenericItemBean> getUsersbyUserId(List<String> recipientIds)  {
		List<GenericItemBean> recipients = new ArrayList<GenericItemBean>();
		for (String userid : recipientIds) {
			GCubeUser user = null;
			try {
				user = new LiferayUserManager().getUserByUsername(userid);
				recipients.add(new GenericItemBean(""+user.getUserId(), user.getUsername(), user.getFullname(), ""));
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}	
		return recipients;
	}	
	/**
	 * 
	 * @param workspace
	 * @param listAttachmentsId
	 * @return
	 * @throws ItemNotFoundException 
	 * @throws InternalErrorException 
	 */
	private String getPublicLinksForAttachs(Workspace workspace, ArrayList<String> listAttachmentsId) throws ItemNotFoundException, InternalErrorException{

		if (listAttachmentsId != null && (!listAttachmentsId.isEmpty()) ) {
			List<WorkspaceItem> attachments = new ArrayList<WorkspaceItem>();
			for (String itemId : listAttachmentsId) {
				attachments.add(workspace.getItem(itemId));
			}

			StringBuilder builder = new StringBuilder();

			if(attachments!=null && attachments.size() > 0){
				builder.append("\n\n\nThe following ");
				String msg = attachments.size()>1?"files were attached to this message:":"file was attached to this message:";
				builder.append(msg+"\n");
				for (WorkspaceItem workspaceItem : attachments) {

					if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {
						FolderItem folderItem = (FolderItem) workspaceItem;
						String publicLink = "";
						String itemName = "";
						try {
							itemName = workspaceItem.getName();
							publicLink = folderItem.getPublicLink(true);
						}
						catch (InternalErrorException e) {
							_log.warn("An error occurred when creating public link for attachment, skipping file: " + itemName);
							return "";
						}
						builder.append(itemName + " ("+publicLink+")");
						builder.append("\n");
					}
				}
				_log.debug("returning public links: "+builder.toString());
				return builder.toString();
			}
			else return "";
		}	
		else return "";
	}


	/**
	 * 
	 * @return the list of workspace users
	 */
	@Override
	public CurrUserAndPortalUsersWrapper getWorkspaceUsers() {

		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currentUser = pContext.getCurrentUser(getThreadLocalRequest());
		_log.debug("trying to get WorkspaceUsers ..");
		WSUser currUser = null;
		ArrayList<WSUser> portalUsers = new ArrayList<WSUser>();

		try {
			if (isWithinPortal()) {
				UserManager um = new LiferayUserManager();
				GroupManager gm = new LiferayGroupManager();
				List<GCubeUser> users = um.listUsersByGroup(gm.getRootVO().getGroupId());
				for (GCubeUser user : users) {
					_log.trace("Trying to get additional info for "+user.getUsername());
					portalUsers.add(new WSUser(user.getUserId()+"", user.getUsername(), user.getFullname(), user.getEmail()));

				}
			} else {
				for (int i = 0; i < 10; i++) {
					portalUsers.add(new WSUser(""+i, "username"+i, "userGetFullname()"+i, "user.getEmail()"+i));
				}				
			}
			currUser = new WSUser(currentUser.getUsername(), currentUser.getUsername(), currentUser.getFullname(), currentUser.getEmail());


		} catch (Exception e) {
			_log.error("Error in server get all contacts ", e);
		}
		CurrUserAndPortalUsersWrapper toReturn = new CurrUserAndPortalUsersWrapper(currUser, portalUsers);
		return toReturn;
	}





}
