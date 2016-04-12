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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.validator.routines.UrlValidator;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.storage.UriResolverReaderParameter;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.communitymanager.impl.OrganizationManagerImpl;
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
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ShareUpdateServiceImpl extends RemoteServiceServlet implements	ShareUpdateService {
	/**
	 * 
	 */
	private static final String ADMIN_ROLE = "Administrator";

	public static final String TEST_USER = "test.user";

	private static final String STORAGE_OWNER = "gCubeSocialFramework";
	public static final String UPLOAD_DIR = "/social-framework-uploads";
	private static final String NEWS_FEED_PORTLET_CLASSNAME = "org.gcube.portlets.user.newsfeed.server.NewsServiceImpl";
	private final static String ATTR_TO_CHECK = "Postnotificationviaemail";

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
			String vreId, LinkPreview preview, String urlThumbnail, ArrayList<String> mentionedUserFullNames, boolean notifyGroup) {


		_log.debug("Writing a new post with text " + postText);
		// escape text
		String escapedFeedText = TextTransfromUtils.escapeHtmlAndTransformUrl(postText);

		// get hashtags
		List<String> hashtags = TextTransfromUtils.getHashTags(postText);
		if (hashtags != null && !hashtags.isEmpty())
			escapedFeedText = TextTransfromUtils.convertHashtagsAnchorHTML(escapedFeedText, hashtags);

		// retrieve mentioned users
		ArrayList<ItemBean> mentionedUsers = null; 
		if (mentionedUserFullNames != null && ! mentionedUserFullNames.isEmpty()) {
			mentionedUsers = getSelectedUserIds(mentionedUserFullNames);
			escapedFeedText = TextTransfromUtils.convertMentionPeopleAnchorHTML(escapedFeedText, mentionedUsers);
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
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreId != null && vreId.compareTo("") != 0) {
			vreScope2Set = (withinPortal) ? getScopeByOrganizationId(vreId) : session.getScope();
		}		

		// build the feed to share (and save on cassandra)
		Feed toShare = new Feed(UUID.randomUUID().toString(), feedType, username, feedDate,
				vreScope2Set, url, urlThumbnail, escapedFeedText, pLevel, fullName, email, thumbnailAvatarURL, linkTitle, linkDesc, host);

		_log.info("Attempting to save Feed with text: " + escapedFeedText + " Level: " + pLevel + " Timeline="+vreScope2Set);

		boolean result = store.saveUserFeed(toShare);

		//need to put the feed into VRES Timeline too
		if (pLevel == PrivacyLevel.VRES) {
			_log.trace("PrivacyLevel was set to VRES attempting to write onto User's VRES Timelines");
			for (GroupModel vre : getUserVREs(username)) {					
				String vreScope = getScopeByOrganizationId(vre.getGroupId());
				_log.trace("Attempting to write onto " + vreScope);
				try {
					store.saveFeedToVRETimeline(toShare.getKey(), vreScope);
				} catch (FeedIDNotFoundException e) {
					_log.error("Error writing onto VRES Time Line" + vreScope);
				}  //save the feed
				_log.trace("Success writing onto " + vreScope);				
			}

		} 
		//share on a single VRE Timeline
		//receives a VreId(groupId) get the scope from the groupId
		else if (pLevel == PrivacyLevel.SINGLE_VRE && vreId != null && vreId.compareTo("") != 0) {
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
				toShare.getUriThumbnail(), toShare.getLinkHost(), null);


		//send the notification about this posts to everyone in the group if notifyGroup is true
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreId != null && vreId.compareTo("") != 0 && notifyGroup) {
			NotificationsManager nm = new ApplicationNotificationsManager(session, NEWS_FEED_PORTLET_CLASSNAME);
			Thread thread = new Thread(new PostNotificationsThread(toShare.getKey(), escapedFeedText, ""+session.getGroupId(), nm, hashtags));
			thread.start();

		}
		//send the notification to the mentioned users	
		if (mentionedUsers != null && mentionedUsers.size() > 0) {
			NotificationsManager nm = new ApplicationNotificationsManager(session);
			ArrayList<GenericItemBean> toPass = new ArrayList<GenericItemBean>();
			for (ItemBean u : mentionedUsers) {
				toPass.add(new GenericItemBean(u.getId(), u.getName(), u.getAlternativeName(), u.getThumbnailURL()));
			}			
			Thread thread = new Thread(new MentionNotificationsThread(toShare.getKey(), escapedFeedText, nm, toPass));
			thread.start();
		}

		return cf;	

	}


	/**
	 * Share a post with at least one attachment.
	 */
	@Override
	public ClientFeed sharePostWithAttachments(String feedText, FeedType feedType,
			PrivacyLevel pLevel, String vreId, ArrayList<UploadedFile> uploadedFiles,
			ArrayList<String> mentionedUserFullNames, boolean notifyGroup, boolean saveCopyWokspace) {

		_log.debug("Incoming text is " + feedText);

		// escape text
		String escapedFeedText = TextTransfromUtils.escapeHtmlAndTransformUrl(feedText);

		// get the list of hashtags
		List<String> hashtags = TextTransfromUtils.getHashTags(feedText);
		if (hashtags != null && !hashtags.isEmpty())
			escapedFeedText = TextTransfromUtils.convertHashtagsAnchorHTML(escapedFeedText, hashtags);

		// get the list of mentioned users
		ArrayList<ItemBean> mentionedUsers = null; 
		if (mentionedUserFullNames != null && ! mentionedUserFullNames.isEmpty()) {
			mentionedUsers = getSelectedUserIds(mentionedUserFullNames);
			escapedFeedText = TextTransfromUtils.convertMentionPeopleAnchorHTML(escapedFeedText, mentionedUsers);
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
			firstAttachmenturlThumbnail = 
					firstAttachment.getThumbnailUrl() != null ? 
							firstAttachment.getThumbnailUrl() : firstAttachmenturlThumbnail;

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

		_log.debug("Url vale " + firstAttachmentDownloadUrl);
		if (escapedFeedText.trim().compareTo(ShareUpdateForm.NO_TEXT_FILE_SHARE) == 0) {
			if(uploadedFiles.size() <= 1){
				textToPost = TextTransfromUtils.convertFileNameAnchorHTML(firstAttachmentDownloadUrl);
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
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreId != null && vreId.compareTo("") != 0) {
			vreScope2Set = (withinPortal) ? getScopeByOrganizationId(vreId) : session.getScope();
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
			for (GroupModel vre : getUserVREs(username)) {					
				String vreScope = getScopeByOrganizationId(vre.getGroupId());
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
		else if (pLevel == PrivacyLevel.SINGLE_VRE && vreId != null && vreId.compareTo("") != 0) {
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


		//send the notification about this posts to everyone in the group if notifyGroup is true
		if (pLevel == PrivacyLevel.SINGLE_VRE && vreId != null && vreId.compareTo("") != 0 && notifyGroup) {
			NotificationsManager nm = new ApplicationNotificationsManager(session, NEWS_FEED_PORTLET_CLASSNAME);
			Thread thread = new Thread(new PostNotificationsThread(toShare.getKey(), textToPost, ""+session.getGroupId(), nm, hashtags));
			thread.start();

		}
		//send the notification to the mentioned users	
		if (mentionedUsers != null && mentionedUsers.size() > 0) {
			NotificationsManager nm = new ApplicationNotificationsManager(session);
			ArrayList<GenericItemBean> toPass = new ArrayList<GenericItemBean>();
			for (ItemBean u : mentionedUsers) {
				toPass.add(new GenericItemBean(u.getId(), u.getName(), u.getAlternativeName(), u.getThumbnailURL()));
			}			
			Thread thread = new Thread(new MentionNotificationsThread(toShare.getKey(), textToPost, nm, toPass));
			thread.start();
		}

		//it means I also should upload a copy of the files on the user's Workspace root folder
		if (saveCopyWokspace) {

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

		return cf;	

	}


	@Override
	public UserSettings getUserSettings() {
		try {
			ASLSession session = getASLSession();
			String username = session.getUsername();
			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";			

			if (isWithinPortal() && username.compareTo(TEST_USER) != 0) {
				long companyId = OrganizationsUtil.getCompany().getCompanyId();
				com.liferay.portal.model.UserModel user = UserLocalServiceUtil.getUserByScreenName(companyId, username);

				thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
				fullName = user.getFirstName() + " " + user.getLastName();
				email = user.getEmailAddress();
				ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);

				String accountURL = themeDisplay.getURLMyAccount().toString();
				HashMap<String, String> vreNames = getUserVreNames(username);

				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmailAddress(), accountURL, true, isAdmin(), vreNames);

				UserSettings toReturn = new UserSettings(userInfo, 0, session.getScopeName(), isInfrastructureScope(), isNotificationViaEmailEnabled(session));
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

	private boolean isNotificationViaEmailEnabled(ASLSession session) throws PortalException, SystemException {
		Organization currOrg =  OrganizationLocalServiceUtil.getOrganization(session.getGroupId());
		return OrganizationManagerImpl.readOrganizationCustomAttribute(session.getUsername(), currOrg, ATTR_TO_CHECK);
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
	 * 
	 * Schema.org microdata  <-- This is still a TODO
	 */
	@Override
	public LinkPreview checkLink(String linkToCheck) {
		LinkPreview toReturn = null;
		_log.info("to check " + linkToCheck);
		//look for a url in text
		linkToCheck = TextTransfromUtils.extractURL(linkToCheck);
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
			pageURL = new URL(linkToCheck);
			if (pageURL.getProtocol().equalsIgnoreCase("https")) {
				System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
				java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); 
				TextTransfromUtils.trustAllHTTPSConnections();
				siteConnection = (HttpsURLConnection) pageURL.openConnection();
			}
			else
				siteConnection = (HttpURLConnection) pageURL.openConnection();
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
		for (GroupModel vre : getUserVREs(username)) {		
			if (vre.getGroupName().compareTo(getASLSession().getGroupName())==0)
				toReturn.put(vre.getGroupId(), vre.getGroupName());
		}
		//		}

		return toReturn;
	}
	/**
	 * tell if the user is a portal administrator or not
	 * @param username
	 * @return true if is admin
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private boolean isAdmin() throws PortalException, SystemException {
		com.liferay.portal.model.User currUser = OrganizationsUtil.validateUser(getASLSession().getUsername());
		List<Organization> organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
		Organization rootOrganization = null;
		for (Organization organization : organizations) {
			if (organization.getName().equals(OrganizationsUtil.getRootOrganizationName() ) ) {
				rootOrganization = organization;
				break;
			}
		}		
		try {
			_log.trace("root: " + rootOrganization.getName() );
			return (hasRole(ADMIN_ROLE, rootOrganization.getName(), currUser));
		}
		catch (NullPointerException e) {
			_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder");
			return false;
		}
	}

	/**
	 * 
	 * @param rolename
	 * @param organizationName
	 * @param user
	 * @return
	 * @throws SystemException 
	 */
	private boolean hasRole(String rolename, String organizationName, com.liferay.portal.model.User user) throws SystemException {
		for (Role role : user.getRoles()) 
			if (role.getName().compareTo(rolename) == 0 ) 
				return true;
		return false;
	}
	/**
	 * 
	 * @param username
	 * @return
	 */
	private ArrayList<GroupModel> getUserVREs(String username) {
		ArrayList<GroupModel> toReturn = new ArrayList<GroupModel>();
		com.liferay.portal.model.User currUser;
		try {
			GroupManager gm = new LiferayGroupManager();
			currUser = OrganizationsUtil.validateUser(username);
			for (Organization org : currUser.getOrganizations()) 
				if (gm.isVRE(org.getOrganizationId()+"")) {
					GroupModel toAdd = gm.getGroup(""+org.getOrganizationId());
					toReturn.add(toAdd);
				}
		} catch (Exception e) {
			_log.error("Failed reading User VREs for : " + username);
			e.printStackTrace();
			return toReturn;
		} 
		return toReturn;
	}

	private String getScopeByOrganizationId(String vreid) {
		GroupManager gm = new LiferayGroupManager();
		try {
			return gm.getScope(vreid);
		} catch (Exception e) {
			_log.error("Could not find a scope for this VREid: " + vreid);
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
			ArrayList<ItemBean> allUsers = getPortalUsers();
			ArrayList<ItemBean> toReturn = new ArrayList<ItemBean>();
			for (String fullName : fullNames) 
				for (ItemBean puser : allUsers) {					
					if (puser.getAlternativeName().compareTo(fullName) == 0) {
						toReturn.add(puser);
						break;
					}
				}
			return toReturn;
		}
	}

	@Override
	public ArrayList<ItemBean> getPortalUsers() {
		ASLSession session = getASLSession();
		boolean withinPortal = false;
		if (isWithinPortal() && session.getUsername().compareTo(TEST_USER) != 0) {
			withinPortal = true;
		}
		return getOrganizationUsers(session.getScope(), session.getUsername(), withinPortal);
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

	/**
	 * 
	 * @param session the Asl Session
	 * @param withinPortal true when is on Liferay portal
	 * @return the users belonging to the current organization (scope)
	 */
	public static ArrayList<ItemBean> getOrganizationUsers(String scope, String currUser, boolean withinPortal) {
		ArrayList<ItemBean> portalUsers = new ArrayList<ItemBean>();
		try {
			if (withinPortal) {
				UserManager um = new LiferayUserManager();
				GroupManager gm = new LiferayGroupManager();
				ScopeBean sb = new ScopeBean(scope);
				List<UserModel> users = null;

				if (sb.is(Type.INFRASTRUCTURE)) 
					users = um.listUsersByGroup(gm.getRootVO().getGroupId());
				else if (sb.is(Type.VRE)) { //must be in VRE
					//get the name from the scope
					String orgName = scope.substring(scope.lastIndexOf("/")+1, scope.length());
					//ask the users
					users = um.listUsersByGroup(gm.getGroupId(orgName));
				}
				else {
					_log.error("Error, you must be in SCOPE VRE OR INFRASTURCTURE, you are in VO SCOPE returning no users");
					return portalUsers;
				}				
				for (UserModel user : users) {
					if (user.getScreenName().compareTo("test.user") != 0 && user.getScreenName().compareTo(currUser) != 0)  { //skip test.user & current user
						String thumbnailURL = "";
						com.liferay.portal.model.UserModel lifeUser = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), user.getScreenName());
						thumbnailURL = "/image/user_male_portrait?img_id="+lifeUser.getPortraitId();
						portalUsers.add(new ItemBean(user.getUserId(), user.getScreenName(), user.getFullname(), thumbnailURL));
					}
				}
			}
			else { //test users
				portalUsers.add(new ItemBean("12111", "massimiliano.assante", "Test User #1", ""));
				portalUsers.add(new ItemBean("14111", "massimiliano.assante", "Test Second User #2", ""));
				portalUsers.add(new ItemBean("11511", "massimiliano.assante", "Test Third User", ""));
				portalUsers.add(new ItemBean("11611", "massimiliano.assante", "Test Fourth User", ""));
				portalUsers.add(new ItemBean("11711", "massimiliano.assante", "Test Fifth User", ""));
				portalUsers.add(new ItemBean("11811", "massimiliano.assante", "Test Sixth User", ""));
				portalUsers.add(new ItemBean("15811", "massimiliano.assante", "Ninth Testing User", ""));
				portalUsers.add(new ItemBean("15811", "massimiliano.assante", "Eighth Testing User", ""));
				portalUsers.add(new ItemBean("11211", "giogio.giorgi", "Seventh Test User", ""));
				portalUsers.add(new ItemBean("2222", "pino.pinetti", "Tenth Testing User", ""));
			}
		} catch (Exception e) {
			_log.error("Error in server get all contacts ", e);
		}
		return portalUsers;
	}
}
