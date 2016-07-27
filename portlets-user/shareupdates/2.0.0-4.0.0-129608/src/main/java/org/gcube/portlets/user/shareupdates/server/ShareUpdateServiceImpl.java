package org.gcube.portlets.user.shareupdates.server;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.validator.routines.UrlValidator;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.ClientFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MentionNotificationsThread;
import org.gcube.portal.notifications.thread.PostNotificationsThread;
import org.gcube.portlets.user.shareupdates.client.ShareUpdateService;
import org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm;
import org.gcube.portlets.user.shareupdates.server.opengraph.OpenGraph;
import org.gcube.portlets.user.shareupdates.shared.HashTagAndOccurrence;
import org.gcube.portlets.user.shareupdates.shared.LinkPreview;
import org.gcube.portlets.user.shareupdates.shared.UploadedFile;
import org.gcube.portlets.user.shareupdates.shared.UserSettings;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.social_networking.socialutillibrary.Utils;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;
/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ShareUpdateServiceImpl extends RemoteServiceServlet implements	ShareUpdateService {
	public static final String TEST_USER = "test.user";

	private static final String STORAGE_OWNER = "gCubeSocialFramework";
	public static final String UPLOAD_DIR = "/social-framework-uploads";
	private static final String NEWS_FEED_PORTLET_CLASSNAME = "org.gcube.portlets.user.newsfeed.server.NewsServiceImpl";

	/**
	 * 
	 */
	private static Logger _log = LoggerFactory.getLogger(ShareUpdateServiceImpl.class);
	/**
	 * The Cassandra store interface
	 */
	private DatabookStore store;
	/**
	 * connect to cassandra at startup
	 */
	public void init() {
		store = new DBCassandraAstyanaxImpl();	
	}

	public void destroy() {
		store.closeConnection();
	}


	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devsec/devVRE");			
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	public String getDevelopmentUser() {
		String user = TEST_USER;
		//		user = "costantino.perciante";
		return user;
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

	/**
	 * Share post that could contain a link preview.
	 */
	@Override
	public ClientFeed sharePostWithLinkPreview(String postText, FeedType feedType,	PrivacyLevel pLevel, 
			Long vreOrgId, LinkPreview preview, String urlThumbnail, ArrayList<String> mentionedUserFullNames, boolean notifyGroup) {

		// escape text
		String escapedFeedText = Utils.escapeHtmlAndTransformUrl(postText);

		// get hashtags
		List<String> hashtags = Utils.getHashTags(escapedFeedText);
		if (hashtags != null && !hashtags.isEmpty())
			escapedFeedText = Utils.convertHashtagsAnchorHTML(escapedFeedText, hashtags);

		// retrieve mentioned users
		ArrayList<ItemBean> mentionedUsers = null; 
		if (mentionedUserFullNames != null && ! mentionedUserFullNames.isEmpty()) {
			mentionedUsers = getSelectedUserIds(mentionedUserFullNames);
			escapedFeedText = Utils.convertMentionPeopleAnchorHTML(escapedFeedText, mentionedUsers, getThreadLocalRequest());
		}

		// get session
		ASLSession session = getASLSession();
		String username = session.getUsername();
		String email = username+"@isti.cnr.it";
		String fullName = username+" FULL";
		String thumbnailAvatarURL = "images/Avatar_default.png";

		boolean withinPortal = isWithinPortal();

		if (withinPortal && username.compareTo(TEST_USER) != 0) {
			try {
				UserInfo user = getUserSettings().getUserInfo();
				email = user.getEmailaddress();
				fullName = user.getFullName();
				thumbnailAvatarURL = user.getAvatarId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// get data from the preview of the link
		String linkTitle = preview.getTitle();
		String linkDesc = preview.getDescription();
		String host = preview.getHost();
		String url = preview.getUrl();
		if (urlThumbnail == null)
			urlThumbnail = "null";

		Date feedDate = new Date();

		//get the VRE scope if single channel post
		String vreScope2Set = "";
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreOrgId != null) {
			vreScope2Set = (withinPortal) ? getScopeByGroupId(vreOrgId) : session.getScope();
		}		

		// build the feed to share (and save on cassandra)
		Feed toShare = new Feed(UUID.randomUUID().toString(), feedType, username, feedDate,
				vreScope2Set, url, urlThumbnail, escapedFeedText, pLevel, fullName, email, thumbnailAvatarURL, linkTitle, linkDesc, host);

		_log.info("Attempting to save Feed with text: " + escapedFeedText + " Level: " + pLevel + " Timeline="+vreScope2Set);

		boolean result = store.saveUserFeed(toShare);

		//need to put the feed into VRES Timeline too
		if (pLevel == PrivacyLevel.VRES) {
			_log.trace("PrivacyLevel was set to VRES attempting to write onto User's VRES Timelines");
			for (GCubeGroup vre : getUserVREs(username)) {
				String vreScope = "";
				try {
					vreScope = new LiferayGroupManager().getInfrastructureScope(vre.getGroupId());
					_log.trace("Attempting to write onto " + vreScope);

					store.saveFeedToVRETimeline(toShare.getKey(), vreScope);
				} catch (FeedIDNotFoundException e) {
					_log.error("Error writing onto VRES Time Line" + vreScope);
				}
				catch (Exception e) {
					_log.error("Error retrieving user VRES");
				}//save the feed
				_log.trace("Success writing onto " + vreScope);				
			}

		} 
		//share on a single VRE Timeline
		//receives a VreId(groupId) get the scope from the groupId
		else if (pLevel == PrivacyLevel.SINGLE_VRE && vreOrgId != null) {
			_log.trace("Attempting to write onto " + vreScope2Set);
			try {
				store.saveFeedToVRETimeline(toShare.getKey(), vreScope2Set);
				if (hashtags != null && !hashtags.isEmpty())
					store.saveHashTags(toShare.getKey(), vreScope2Set, hashtags);
			} catch (FeedIDNotFoundException e) {
				_log.error("Error writing onto VRES Time Line" + vreScope2Set);
			}  //save the feed
			_log.trace("Success writing onto " + vreScope2Set);				
		}
		if (!result) 
			return null;

		//everything went fine
		ClientFeed cf = new  ClientFeed(toShare.getKey(), toShare.getType().toString(), username, feedDate, toShare.getUri(),
				TextTransfromUtils.replaceAmpersand(toShare.getDescription()), fullName, email, thumbnailAvatarURL, toShare.getLinkTitle(), toShare.getLinkDescription(), 
				toShare.getUriThumbnail(), toShare.getLinkHost(), null);


		// check if is needed to notify people in the vre
		notifyPeopleGroup(pLevel, vreOrgId, notifyGroup, username, email, fullName, thumbnailAvatarURL, toShare, hashtags, vreScope2Set, escapedFeedText);

		//send the notification to the mentioned users	
		if (mentionedUsers != null && mentionedUsers.size() > 0)
			notifyMentionedUsers(vreScope2Set, mentionedUsers, username, email, fullName, thumbnailAvatarURL, toShare, escapedFeedText);


		return cf;	

	}

	/**
	 * Share a post with at least one attachment.
	 */
	@Override
	public ClientFeed sharePostWithAttachments(String feedText, FeedType feedType,
			PrivacyLevel pLevel, Long vreOrgId, ArrayList<UploadedFile> uploadedFiles,
			ArrayList<String> mentionedUserFullNames, boolean notifyGroup, boolean saveCopyWokspace) {

		// escape text
		String escapedFeedText = Utils.escapeHtmlAndTransformUrl(feedText);

		// get the list of hashtags
		List<String> hashtags = Utils.getHashTags(escapedFeedText);
		if (hashtags != null && !hashtags.isEmpty())
			escapedFeedText = Utils.convertHashtagsAnchorHTML(escapedFeedText, hashtags);

		// get the list of mentioned users
		ArrayList<ItemBean> mentionedUsers = null; 
		if (mentionedUserFullNames != null && ! mentionedUserFullNames.isEmpty()) {
			mentionedUsers = getSelectedUserIds(mentionedUserFullNames);
			escapedFeedText = Utils.convertMentionPeopleAnchorHTML(escapedFeedText, mentionedUsers, getThreadLocalRequest());
		}

		ASLSession session = getASLSession();
		String username = session.getUsername();
		String email = username+"@isti.cnr.it";
		String fullName = username+" FULL";
		String thumbnailAvatarURL = "images/Avatar_default.png";

		boolean withinPortal = isWithinPortal();

		if (withinPortal && username.compareTo(TEST_USER) != 0) {
			try {
				UserInfo user = getUserSettings().getUserInfo();
				email = user.getEmailaddress();
				fullName = user.getFullName();
				thumbnailAvatarURL = user.getAvatarId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Managing attachments: the first one will use the same fields of a link preview.
		// If more than one attachments are present, they will be saved as Attachment objects.
		// In this way we can handle backward-compatibility.
		List<Attachment> attachments = null;

		String firstAttachmentName = "", 
				firstAttachmentDescription = "", 
				firstAttachmentFormat = "", 
				firstAttachmentDownloadUrl = "", 
				firstAttachmenturlThumbnail = "";

		if(uploadedFiles.size() > 0){

			// retrieve the first element (and remove it)
			UploadedFile firstAttachment = uploadedFiles.get(0);

			firstAttachmentName = firstAttachment.getFileName();
			firstAttachmentDescription = firstAttachment.getDescription();
			firstAttachmentFormat = firstAttachment.getFormat();
			firstAttachmentDownloadUrl = firstAttachment.getDownloadUrl();
			firstAttachmenturlThumbnail = firstAttachment.getThumbnailUrl() != null ? firstAttachment.getThumbnailUrl() : firstAttachmenturlThumbnail;

			// check if there are more files
			if(uploadedFiles.size() > 1){

				attachments = new ArrayList<>();

				// starting from 1
				for (int i = 1; i < uploadedFiles.size(); i++){
					UploadedFile file = uploadedFiles.get(i);

					attachments.add(new Attachment(
							UUID.randomUUID().toString(), 
							file.getDownloadUrl(), 
							file.getFileName(), 
							file.getDescription(), 
							file.getThumbnailUrl(), 
							file.getFormat())
							);
				}
			}

		}

		// evaluate the date (this will be the date of the post)
		Date feedDate = new Date();

		String textToPost = "";
		//this means the user has shared a file without text in it.
		if (escapedFeedText.trim().compareTo(ShareUpdateForm.NO_TEXT_FILE_SHARE) == 0) {
			if(uploadedFiles.size() <= 1){
				textToPost = Utils.convertFileNameAnchorHTML(firstAttachmentDownloadUrl);
			}
			else{
				StringBuilder sb = new StringBuilder();
				textToPost = sb.append("<span style=\"color:gray; font-size:12px;\">shared a set of files.</span>").toString();
			}
		} else {
			textToPost = escapedFeedText;
		}

		//get the VRE scope if single channel post
		String vreScope2Set = "";
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreOrgId != null ) {
			vreScope2Set = (withinPortal) ? getScopeByGroupId(vreOrgId) : session.getScope();
		}		

		Feed toShare = null;
		boolean result;
		if(uploadedFiles.size() <= 1){
			toShare = new Feed(
					UUID.randomUUID().toString(), feedType, username, feedDate,
					vreScope2Set, firstAttachmentDownloadUrl, firstAttachmenturlThumbnail, 
					textToPost, pLevel, fullName, email, thumbnailAvatarURL, 
					firstAttachmentName, firstAttachmentDescription, firstAttachmentFormat);

			// save the feed itself 
			result = store.saveUserFeed(toShare);
		}
		else{

			toShare = new Feed(
					UUID.randomUUID().toString(), feedType, username, feedDate,
					vreScope2Set, firstAttachmentDownloadUrl, firstAttachmenturlThumbnail, 
					textToPost, pLevel, fullName, email, thumbnailAvatarURL, 
					firstAttachmentName, firstAttachmentDescription, firstAttachmentFormat);

			// set the field multiFileUpload to true
			toShare.setMultiFileUpload(true);

			// save the feed itself plus the attachments
			result = store.saveUserFeed(toShare, attachments);

		}

		_log.info("Attempting to save Feed with text: " + textToPost + " Level: " + pLevel + " Timeline="+vreScope2Set);

		//need to put the feed into VRES Timeline too
		if (pLevel == PrivacyLevel.VRES) {
			_log.trace("PrivacyLevel was set to VRES attempting to write onto User's VRES Timelines");
			for (GCubeGroup vre : getUserVREs(username)) {
				String vreScope = getScopeByGroupId(vre.getGroupId());
				_log.trace("Attempting to write onto " + vreScope);
				try {
					store.saveFeedToVRETimeline(toShare.getKey(), vreScope);
				} catch (FeedIDNotFoundException e) {
					_log.error("Error writing onto VRES Time Line" + vreScope);
				}  //save the feed
				_log.trace("Success writing onto " + vreScope);				
			}

		} //share on a single VRE Timeline
		//receives a VreId(groupId) get the scope from the groupId
		else if (pLevel == PrivacyLevel.SINGLE_VRE && vreOrgId != null) {
			_log.trace("Attempting to write onto " + vreScope2Set);
			try {
				store.saveFeedToVRETimeline(toShare.getKey(), vreScope2Set);
				if (hashtags != null && !hashtags.isEmpty())
					store.saveHashTags(toShare.getKey(), vreScope2Set, hashtags);
			} catch (FeedIDNotFoundException e) {
				_log.error("Error writing onto VRES Time Line" + vreScope2Set);
			}  //save the feed
			_log.trace("Success writing onto " + vreScope2Set);				
		}
		if (!result) return null;

		//everything went fine
		ClientFeed cf = new  ClientFeed(toShare.getKey(), toShare.getType().toString(), username, feedDate, toShare.getUri(),
				TextTransfromUtils.replaceAmpersand(toShare.getDescription()), fullName, email, thumbnailAvatarURL, toShare.getLinkTitle(), toShare.getLinkDescription(), 
				toShare.getUriThumbnail(), toShare.getLinkHost(), attachments);


		// check if is needed to notify people in the vre
		notifyPeopleGroup(pLevel, vreOrgId, notifyGroup, username, email, fullName, thumbnailAvatarURL, toShare, hashtags, vreScope2Set, textToPost);

		//send the notification to the mentioned users	
		if (mentionedUsers != null && mentionedUsers.size() > 0) 
			notifyMentionedUsers(vreScope2Set, mentionedUsers, username, email, fullName, thumbnailAvatarURL, toShare, textToPost);

		//it means I also should upload a copy of the files on the user's Workspace root folder
		if (saveCopyWokspace) 
			saveCopyIntoWorkSpace(fullName, username, uploadedFiles);


		return cf;	

	}

	/**
	 * Check if vre notification must be performed and does it.
	 * @param pLevel
	 * @param vreOrgId
	 * @param notifyGroup
	 * @param username
	 * @param email
	 * @param fullName
	 * @param thumbnailAvatarURL
	 * @param toShare
	 * @param hashtags
	 * @param vreScope2Set
	 * @param postText
	 */
	private void notifyPeopleGroup(PrivacyLevel pLevel, Long vreOrgId,
			boolean notifyGroup, String username, String email,
			String fullName, String thumbnailAvatarURL, Feed toShare,
			List<String> hashtags, String vreScope2Set, String postText) {

		//send the notification about this posts to everyone in the group if notifyGroup is true
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreOrgId != null &&  notifyGroup) {
			NotificationsManager nm = new ApplicationNotificationsManager(
					new SocialNetworkingSite(getThreadLocalRequest()), 
					vreScope2Set, 
					new SocialNetworkingUser(username, email, fullName, thumbnailAvatarURL), 
					NEWS_FEED_PORTLET_CLASSNAME);
			Thread thread = new Thread(new PostNotificationsThread(toShare.getKey(), postText, ""+vreOrgId, nm, hashtags));
			thread.start();

		}
	}

	/**
	 * Save copy of the file(s) into the workspace
	 * @param fullName
	 * @param username
	 * @param uploadedFiles
	 */
	private void saveCopyIntoWorkSpace(String fullName, String username, ArrayList<UploadedFile> uploadedFiles){

		for(UploadedFile file: uploadedFiles){
			new Thread(
					new UploadToWorkspaceThread(
							fullName, 
							username, 
							file.getFileName(), 
							file.getFileAbsolutePathOnServer()))
			.start();
		}
	}

	/**
	 * Common method to notify users.
	 * @param vreScope2Set
	 * @param mentionedUsers
	 * @param username
	 * @param email
	 * @param fullName
	 * @param thumbnailAvatarURL
	 */
	private void notifyMentionedUsers(String vreScope2Set, ArrayList<ItemBean> mentionedUsers, String username, String email, String fullName, String thumbnailAvatarURL,
			Feed toShare, String escapedFeedText){

		NotificationsManager nm = new ApplicationNotificationsManager(
				new SocialNetworkingSite(getThreadLocalRequest()), 
				vreScope2Set, 
				new SocialNetworkingUser(username, email, fullName, thumbnailAvatarURL), 
				NEWS_FEED_PORTLET_CLASSNAME);
		ArrayList<GenericItemBean> toPass = new ArrayList<GenericItemBean>();
		// among the mentionedUsers there could be groups of people
		Map<String, ItemBean> uniqueUsersToNotify = new HashMap<>();
		UserManager um = new LiferayUserManager();

		for (ItemBean bean : mentionedUsers) {

			if(bean.isItemGroup()){

				// retrieve the users of this group
				try {
					List<GCubeUser> teamUsers = um.listUsersByTeam(Long.parseLong(bean.getId()));

					for (GCubeUser userTeam : teamUsers) {
						if(!uniqueUsersToNotify.containsKey(userTeam.getUsername()))
							uniqueUsersToNotify.put(userTeam.getUsername(), new ItemBean(userTeam.getUserId()+"",
									userTeam.getUsername(), userTeam.getFullname(), userTeam.getUserAvatarURL()));
					}

				} catch (NumberFormatException
						| UserManagementSystemException
						| TeamRetrievalFault | UserRetrievalFault e) {
					_log.error("Unable to retrieve team information", e);
				}

			}else{
				// it is a user, just add to the hashmap
				if(!uniqueUsersToNotify.containsKey(bean.getName()))
					uniqueUsersToNotify.put(bean.getName(), bean);

			}
		}

		// iterate over the hashmap
		Iterator<Entry<String, ItemBean>> userMapIterator = uniqueUsersToNotify.entrySet().iterator();
		while (userMapIterator.hasNext()) {
			Map.Entry<String, ItemBean> user = (Map.Entry<String, ItemBean>) userMapIterator
					.next();
			ItemBean userBean = user.getValue();
			toPass.add(new GenericItemBean(userBean.getId(), userBean.getName(), userBean.getAlternativeName(), userBean.getThumbnailURL()));
		}

		Thread thread = new Thread(new MentionNotificationsThread(toShare.getKey(), escapedFeedText, nm, null, toPass));
		thread.start();
	}


	@Override
	public UserSettings getUserSettings() {
		try {
			ASLSession session = getASLSession();
			String username = session.getUsername();
			_log.debug("getUserSettings() for " + username);

			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";			

			if (isWithinPortal() && username.compareTo(TEST_USER) != 0) {
				UserManager um = new LiferayUserManager();
				GCubeUser user = um.getUserByUsername(username);

				thumbnailURL = user.getUserAvatarURL();
				fullName = user.getFullname();
				email = user.getEmail();
				final String profilePageURL = 
						GCubePortalConstants.PREFIX_GROUP_URL + 
						PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest())+
						GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
				String accountURL = profilePageURL;
				try {
					accountURL = "";
				}catch (NullPointerException e) {
					e.printStackTrace();
				}
				HashMap<String, String> vreNames = getUserVreNames(username);
				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmail(), accountURL, true, isAdmin(), vreNames);
				UserSettings toReturn = new UserSettings(userInfo, 0, session.getScopeName(), isInfrastructureScope(), isNotificationViaEmailEnabled(session));
				_log.debug("getUserSettings() return " + toReturn);
				return toReturn;
			}
			else {
				_log.info("Returning test USER = " + session.getUsername());
				HashMap<String, String> fakeVreNames = new  HashMap<String, String>();
				fakeVreNames.put("/gcube/devsec/devVRE","devVRE");
				//fakeVreNames.put("/gcube/devNext/NexNext","NexNext");

				UserInfo user =  new UserInfo(session.getUsername(), fullName, thumbnailURL, email, "fakeAccountUrl", true, false, fakeVreNames);
				return new UserSettings(user, 0, session.getScopeName(), false, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UserSettings();
	}

	private boolean isNotificationViaEmailEnabled(ASLSession session) throws UserManagementSystemException, GroupRetrievalFault {
		GroupManager gm = new LiferayGroupManager();
		GCubeGroup group = gm.getGroup(session.getGroupId());
		return (Boolean) gm.readCustomAttr(group.getGroupId(), CustomAttributeKeys.POST_NOTIFICATION.getKeyName());
	}
	/**
	 * tell if the user is a portal administrator or not
	 * @param username
	 * @return true if is admin
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private boolean isAdmin() throws PortalException, SystemException  {
		if (! isWithinPortal())
			return false;
		try {
			GCubeUser curUser = new LiferayUserManager().getUserByUsername(getASLSession().getUsername());
			return new LiferayRoleManager().isAdmin(curUser.getUserId());
		}
		catch (Exception e) {
			_log.error("Could not check if the user is an Administrator, returning false");
			return false;
		}
	}
	/**
	 * generate a preview of the file, upload the file on the storage and shorts the link
	 */
	@Override
	public LinkPreview checkUploadedFile(String fileName, String fileabsolutePathOnServer) {
		LinkPreview toReturn = null;

		String randomUploadFolderName = UUID.randomUUID().toString();
		String remoteFilePath = UPLOAD_DIR + "/" + randomUploadFolderName + "/" + fileName;
		//get the Storage Client
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());
		IClient storageClient = new StorageClient(STORAGE_OWNER, AccessType.SHARED, MemoryType.PERSISTENT).getClient();
		ScopeProvider.instance.set(currScope);	

		String httpURL = "";
		String smpURI = "";
		String mimeType = null;
		if (isWithinPortal()) {
			//get the url to show, before actually uploading it
			//smpURI = storageClient.getUrl(true).RFile(remoteFilePath); //"http://ciccio.com";
			smpURI = storageClient.getHttpUrl(true).RFile(remoteFilePath);

			//The storage uploader Thread starts here asyncronouslyù
			try {
				mimeType = FilePreviewer.getMimeType(new File(fileabsolutePathOnServer), fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Thread thread = new Thread(new UploadToStorageThread(storageClient, fileName, fileabsolutePathOnServer, remoteFilePath, mimeType));
			thread.start();
		}

		try {
			//get the url to show (though it could not be ready for download at this stage)
			httpURL = smpURI;

			switch (mimeType) {
			case "application/pdf":
				toReturn = FilePreviewer.getPdfPreview(fileName, fileabsolutePathOnServer, httpURL, mimeType);
				break;
			case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
				mimeType = "application/wordprocessor";
				return FilePreviewer.getUnhandledTypePreview(fileName, fileabsolutePathOnServer, httpURL, mimeType);
			case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
				mimeType = "application/spreadsheet";
				return FilePreviewer.getUnhandledTypePreview(fileName, fileabsolutePathOnServer, httpURL, mimeType);			
			case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
				mimeType = "application/presentation";
				return FilePreviewer.getUnhandledTypePreview(fileName, fileabsolutePathOnServer, httpURL, mimeType);
			case "image/png":
			case "image/gif":
			case "image/tiff":
			case "image/jpg":
			case "image/jpeg":
			case "image/bmp":
				toReturn = FilePreviewer.getImagePreview(fileName, fileabsolutePathOnServer, httpURL, mimeType);
				break;
			default:
				return FilePreviewer.getUnhandledTypePreview(fileName, fileabsolutePathOnServer, httpURL, mimeType);

			}

		} catch (Exception e) {
			_log.error("Error while resolving or previewing file");
			e.printStackTrace();
			try {
				return FilePreviewer.getUnhandledTypePreview(fileName, fileabsolutePathOnServer, httpURL, "Error During upload on Server!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}	

		_log.debug("smpURI=" + smpURI);
		_log.debug("Returning httpURL=" + httpURL);
		return toReturn;
	}
	/**
	 * tries the following in the indicated order for Populating the Link preview
	 * Open Graph protocol
	 * Meta "title" and "description" tags
	 * Best guess from page content (not recommended)
	 */
	@SuppressWarnings("restriction")
	@Override
	public LinkPreview checkLink(String linkToCheck) {
		LinkPreview toReturn = null;
		_log.info("to check " + linkToCheck);
		//look for a url in text
		linkToCheck = Utils.extractURL(linkToCheck);
		if (linkToCheck == null)
			return null; //no url

		String[] schemes = {"http","https"};
		UrlValidator urlValidator = new UrlValidator(schemes);
		if (! urlValidator.isValid(linkToCheck)) {
			_log.warn("url is NOT valid, returning nothing");
			return null;
		}
		_log.debug("url is valid");

		URL pageURL;
		URLConnection siteConnection = null;
		try {
			pageURL = new URL(null, linkToCheck, new sun.net.www.protocol.https.Handler());
			if (pageURL.getProtocol().equalsIgnoreCase("https")) {
				System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
				java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 
				TextTransfromUtils.trustAllHTTPSConnections();
				siteConnection = (HttpsURLConnection) pageURL.openConnection();
			}
			else {
				pageURL = new URL(linkToCheck);
				siteConnection = (HttpURLConnection) pageURL.openConnection();
			}
		} catch (MalformedURLException e) {
			_log.error("url is not valid");
			return null;
		} catch (IOException e) {
			_log.error("url is not reachable");
			return null;
		}
		//pretend you're a browser (make my request from Java more “browsery-like”.) 
		siteConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		String title;
		String description;
		ArrayList<String> imageUrls = new ArrayList<String>();
		//get the host from the url
		String host = pageURL.getHost().replaceAll("www.", "");

		//try openGraph First
		OpenGraph ogLink = null;
		try {
			ogLink = new OpenGraph(linkToCheck, true, siteConnection);
			if (ogLink == null || ogLink.getContent("title") == null) { 
				//there is no OpenGraph for this link
				_log.info("No OpenGraph Found, going Best guess from page content") ;
				toReturn =  TextTransfromUtils.getInfoFromHTML(siteConnection, pageURL, linkToCheck, host);				
			} else {
				//there is OpenGraph
				_log.info("OpenGraph Found") ;
				title =  ogLink.getContent("title");
				description = (ogLink.getContent("description") != null)  ? ogLink.getContent("description") : "";
				description = ((description.length() > 256) ? description.substring(0, 256)+"..." : description);
				//look for the image ask the guesser if not present
				if (ogLink.getContent("image") != null) {
					String imageUrl = TextTransfromUtils.getImageUrlFromSrcAttribute(pageURL, ogLink.getContent("image"));
					imageUrls.add(imageUrl);
					_log.trace("OpenGraph getImage = " +imageUrl) ;
				}
				else {
					_log.trace("OpenGraph No Image, trying manuale parsing");
					ArrayList<String> images = TextTransfromUtils.getImagesWithCleaner(pageURL);
					if (! images.isEmpty())
						imageUrls = images;
				}
				toReturn = new LinkPreview(title, description, linkToCheck, host, imageUrls);
				return toReturn;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * return the id as key and the names as value of the vre a user is subscribed to
	 * @param username
	 * @return  the id as key and the names as value of the vre a user is subscribed to
	 */
	private HashMap<String, String> getUserVreNames(String username) {
		HashMap<String, String> toReturn = new HashMap<String, String>();
		for (GCubeGroup vre : getUserVREs(username)) {		
			if (vre.getGroupName().compareTo(getASLSession().getGroupName())==0)
				toReturn.put(vre.getGroupId()+"", vre.getGroupName());
		}
		return toReturn;
	}
	/**
	 * 
	 * @param username
	 * @return
	 */
	private ArrayList<GCubeGroup> getUserVREs(String username) {
		ArrayList<GCubeGroup> toReturn = new ArrayList<GCubeGroup>();
		GCubeUser currUser;
		try {
			GroupManager gm = new LiferayGroupManager();
			currUser = new LiferayUserManager().getUserByUsername(username);

			for (GCubeGroup group : gm.listGroupsByUser(currUser.getUserId())) 
				if (gm.isVRE(group.getGroupId())) {
					toReturn.add(group);
				}
		} catch (Exception e) {
			_log.error("Failed reading User VREs for : " + username);
			e.printStackTrace();
			return toReturn;
		} 
		return toReturn;
	}

	private String getScopeByGroupId(Long vreGroupId) {
		try {
			return new LiferayGroupManager().getInfrastructureScope(vreGroupId);
		} catch (Exception e) {
			_log.error("Could not find a scope for this vreGroupId: " + vreGroupId);
			return null;
		} 
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		ScopeBean scope = new ScopeBean(getASLSession().getScope());
		return 	scope.is(Type.INFRASTRUCTURE);
	}

	/**
	 * 
	 * @return the screennames of the addressee (user logins e.g. pino.pini)
	 */
	public ArrayList<ItemBean> getSelectedUserIds(ArrayList<String> fullNames) {
		if (fullNames == null) 
			return new ArrayList<ItemBean>();
		else {
			ArrayList<ItemBean> allbeans = getPortalItemBeans();
			ArrayList<ItemBean> toReturn = new ArrayList<ItemBean>();
			for (String fullName : fullNames) 
				for (ItemBean puser : allbeans) {					
					if (puser.getAlternativeName().compareTo(fullName) == 0) {
						toReturn.add(puser);
						break;
					}
				}
			return toReturn;
		}
	}

	@Override
	public ArrayList<ItemBean> getPortalItemBeans() {
		ASLSession session = getASLSession();
		boolean withinPortal = false;
		if (isWithinPortal() && session.getUsername().compareTo(TEST_USER) != 0) {
			withinPortal = true;
		}
		// retrieve user and group beans
		return Utils.getDisplayableItemBeans(session.getScope(), session.getUsername(), withinPortal);
	}

	@Override
	public ArrayList<ItemBean> getHashtags() {
		ASLSession session = getASLSession();
		String scope = session.getScope();
		_log.error("getting hashtags for " + scope);
		Map<String, Integer> map = store.getVREHashtagsWithOccurrence(scope);
		ArrayList<HashTagAndOccurrence> toSort = new ArrayList<HashTagAndOccurrence>();
		_log.trace("Got " + map.keySet().size() + " hashtags");
		for (String hashtag : map.keySet()) {
			toSort.add(new HashTagAndOccurrence(hashtag, map.get(hashtag)));
		}
		Collections.sort(toSort, Collections.reverseOrder());
		ArrayList<ItemBean> toReturn = new ArrayList<>();
		for (HashTagAndOccurrence wrapper : toSort) {
			String hashtag = wrapper.getHashtag();
			toReturn.add(new ItemBean(hashtag, hashtag, hashtag, null));
		}
		return toReturn;
	}
}
