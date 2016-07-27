package org.gcube.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.ex.ApplicationProfileNotFoundException;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.notifications.thread.PostNotificationsThread;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.liferay.portal.model.UserModel;
import com.liferay.portal.service.UserLocalServiceUtil;

public class Utils {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(Utils.class);

	public final static String NO_TEXT_FILE_SHARE = "_N0_73X7_SH4R3_";

	// name of the portlet for vre notification
	public static final String NEWS_FEED_PORTLET_CLASSNAME = "org.gcube.portlets.user.newsfeed.server.NewsServiceImpl";

	/**
	 * Validate token.
	 * @param token
	 * @return null if validation fails
	 */
	public static AuthorizationEntry validateToken(String token){

		// set the root scope
		ScopeProvider.instance.set("/" + PortalContext.getConfiguration().getInfrastructureName());

		AuthorizationEntry res;
		try {

			res = authorizationService().build().get(token);

		} catch (Exception e) {

			_log.error("The token is not valid. This request will be rejected!!! (" + token + ")", e);
			return null;

		}

		return res;
	}

	/**
	 * Method used when an application needs to publish something.
	 * @param feedText
	 * @param uriParams
	 * @param previewTitle
	 * @param previewDescription
	 * @param httpImageUrl
	 * @return true upon success, false on failure
	 */
	public static Feed shareApplicationUpdate(
			String postText, 
			String uriParams, 
			String previewTitle, 
			String previewDescription, 
			String httpImageUrl,
			ApplicationProfile applicationProfile,
			AuthorizationEntry ae,
			boolean notifyGroup,
			HttpServletRequest request
			){

		String escapedFeedText = TextTransfromUtils.escapeHtmlAndTransformUrl(postText);

		List<String> hashtags = TextTransfromUtils.getHashTags(postText);
		if (hashtags != null && !hashtags.isEmpty())
			escapedFeedText = TextTransfromUtils.convertHashtagsAnchorHTML(escapedFeedText, hashtags);

		Feed toWrite = 
				buildFeed(
						escapedFeedText, 
						uriParams == null ? "" : uriParams, 
								previewTitle == null ? "" : previewTitle, 
										previewDescription == null ? "" : previewDescription, 
												httpImageUrl == null ? "" : httpImageUrl, 
														applicationProfile, 
														ae.getScope());

		// try to save it
		boolean res = CassandraConnection.getDatabookStore().saveAppFeed(toWrite);

		if(res){
			_log.info("Feed correctly written by application " + ae.getUserName());

			// send notifications to other members if needed
			if(notifyGroup){

				_log.debug("Sending notifications for " + ae.getUserName() + " " + ae.getScope());

				try{

					String name = new ScopeBean(ae.getScope()).name(); // scope such as devVRE

					// retrieve group information
					GroupManager gManager = new LiferayGroupManager();
					long companyid = gManager.getGroupId(name);
					String groupName =  gManager.getGroup(companyid).getGroupName();

					_log.debug("Company id and name " + companyid + " " + groupName);

					//  build the notification manager
					SocialNetworkingSite site = new SocialNetworkingSite(request);
					SocialNetworkingUser user = new SocialNetworkingUser(ae.getUserName(), "", applicationProfile.getName(), applicationProfile.getImageUrl());

					NotificationsManager nm = new ApplicationNotificationsManager(site, ae.getScope(), user, NEWS_FEED_PORTLET_CLASSNAME);

					// start notification thread
					new Thread(new PostNotificationsThread(
							toWrite.getKey(), 
							toWrite.getDescription(), 
							""+companyid, 
							nm, 
							new ArrayList<String>())
							).start();

				}catch (Exception e) {
					_log.debug("Feed succesfully created but unable to send notifications.");
				}
			}
			return toWrite;
		}
		else
			return null;

	}

	/**
	 * Build an ApplicationProfile Feed.
	 * 
	 * @param description add a description for the update you are sharing
	 * @param uriParams the additional parameters your applicationProfile needs to open the subject of this update  e.g. id=12345&type=foo
	 * @param previewTitle the title to show in the preview
	 * @param previewDescription the description to show in the preview
	 * @param previewThumbnailUrl the image url to show in the preview
	 * @return a feed instance ready to be written
	 */
	private static Feed buildFeed(
			String description, 
			String uriParams, 
			String previewTitle, 
			String previewDescription, 
			String previewThumbnailUrl,
			ApplicationProfile applicationProfile,
			String scopeApp) {

		String uri = applicationProfile.getUrl();

		//add the GET params if necessary
		if (uriParams != null && uriParams.compareTo("") != 0)
			uri += "?"+uriParams;		

		Feed toReturn = new Feed( 
				UUID.randomUUID().toString(), 
				FeedType.PUBLISH, 
				applicationProfile.getKey(), 
				new Date(), 
				scopeApp, 
				uri, 
				previewThumbnailUrl, 
				description, 
				PrivacyLevel.SINGLE_VRE, 
				applicationProfile.getName(), 
				"no-email", 
				applicationProfile.getImageUrl(), 
				previewTitle, 
				previewDescription, 
				"", 
				true);

		return toReturn;
	}	

	/**
	 * This method looks up the applicationProfile profile among the ones available in the infrastructure
	 * @param idApp as identifier of your application (as reported in the ApplicationProfile)
	 * @param scopeApp the scope of the application
	 */
	public static ApplicationProfile getProfileFromInfrastrucure(String idApp, String scopeApp) {
		ScopeBean scope =  new ScopeBean(scopeApp);

		_log.debug("Trying to fetch applicationProfile profile from the infrastructure for " + idApp + " scope: " +  scope);

		try {

			ApplicationProfile toReturn = new ApplicationProfile();
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Body/AppId/string() " +
					" eq '" + idApp + "'" +
					"return $profile");

			String currScope = ScopeProvider.instance.get();
			String scopeToQuery = PortalContext.getConfiguration().getInfrastructureName();
			ScopeProvider.instance.set("/"+scopeToQuery);

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");

			else {

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				List<String> currValue = null;
				currValue = helper.evaluate("/Resource/Profile/Name/text()");
				if (currValue != null && currValue.size() > 0) {

					toReturn.setName(currValue.get(0));

				} 
				else throw new ApplicationProfileNotFoundException("Your applicationProfile NAME was not found in the profile");

				currValue = helper.evaluate("/Resource/Profile/Description/text()");
				if (currValue != null && currValue.size() > 0) {

					toReturn.setDescription(currValue.get(0));

				} 
				else _log.warn("No Description exists for " + toReturn.getName());

				currValue = helper.evaluate("/Resource/Profile/Body/AppId/text()");

				if (currValue != null && currValue.size() > 0) {

					toReturn.setKey(currValue.get(0));

				}
				else throw new ApplicationProfileNotFoundException("Your applicationProfile ID n was not found in the profile, consider adding <AppId> element in <Body>");

				currValue = helper.evaluate("/Resource/Profile/Body/ThumbnailURL/text()");
				if (currValue != null && currValue.size() > 0) {

					toReturn.setImageUrl(currValue.get(0));

				}
				else throw new ApplicationProfileNotFoundException("Your applicationProfile Image Url was not found in the profile, consider adding <ThumbnailURL> element in <Body>");
				currValue = helper.evaluate("/Resource/Profile/Body/EndPoint/Scope/text()");
				if (currValue != null && currValue.size() > 0) {

					List<String> scopes = currValue;
					boolean foundUrl = false;
					for (int i = 0; i < scopes.size(); i++) {
						if (currValue.get(i).trim().compareTo(scope.toString()) == 0) {								
							toReturn.setUrl(helper.evaluate("/Resource/Profile/Body/EndPoint/URL/text()").get(i));
							toReturn.setScope(scope.toString());
							foundUrl = true;
							break;
						}						
					}

					if (! foundUrl)
						throw new ApplicationProfileNotFoundException("Your applicationProfile URL was not found in the profile for Scope: " + scope.toString());
				}

				else throw new ApplicationProfileNotFoundException("Your applicationProfile EndPoint was not found in the profile, consider adding <EndPoint><Scope> element in <Body>");
				_log.debug("Returning " + toReturn);
				ScopeProvider.instance.set(currScope);
				return toReturn;
			}

		} catch (Exception e) {

			_log.error("Error while trying to fetch applicationProfile profile from the infrastructure");
			_log.error(e.toString());
			return null;

		} 

	}

	/**
	 * Allows user to post a feed in a certain vre.
	 * @param userId
	 * @param postText
	 * @param vreId
	 * @param previewTitle
	 * @param previewDescription
	 * @param previewHost
	 * @param previewUrl
	 * @param urlThumbnail
	 * @param notifyGroup
	 * @return The written Feed
	 */
	public static Feed shareUserUpdate(
			String userId,
			String postText,
			String vreId, 
			String previewTitle, 
			String previewDescription, 
			String previewHost,
			String previewUrl,
			String urlThumbnail, 
			boolean notifyGroup,
			HttpServletRequest httpServletRequest) {

		String escapedFeedText = TextTransfromUtils.escapeHtmlAndTransformUrl(postText);

		List<String> hashtags = TextTransfromUtils.getHashTags(postText);
		if (hashtags != null && !hashtags.isEmpty())
			escapedFeedText = TextTransfromUtils.convertHashtagsAnchorHTML(escapedFeedText, hashtags);

		long companyId;
		UserModel user;
		try{

			companyId = SiteManagerUtil.getCompany().getCompanyId();
			user = UserLocalServiceUtil.getUserByScreenName(companyId, userId);

		}catch(Exception e){

			_log.error("Unable to get user informations, feed write fails.", e);
			return null;

		}

		String email = user.getEmailAddress();
		String fullName = user.getFirstName() + " " + user.getLastName();
		String thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();

		String linkTitle = previewTitle == null ? "" : previewTitle;
		String linkDesc = previewDescription == null ? "" : previewDescription;
		String host = previewHost == null ? "" : previewHost;
		String url = previewUrl == null ? "" : previewUrl;
		if (urlThumbnail == null)
			urlThumbnail = "null";

		//this means the user has shared a file without text in it.
		String textToPost = "";
		if (escapedFeedText.trim().compareTo(NO_TEXT_FILE_SHARE) == 0) {

			textToPost = TextTransfromUtils.convertFileNameAnchorHTML(url);

		} else {

			textToPost = escapedFeedText;

		}

		Feed toShare = new Feed(UUID.randomUUID().toString(), FeedType.PUBLISH, userId, new Date(),
				vreId, url, urlThumbnail, textToPost, PrivacyLevel.SINGLE_VRE, fullName, email, thumbnailURL, linkTitle, linkDesc, host);

		_log.info("Attempting to save Feed with text: " + textToPost + " Level = " + PrivacyLevel.SINGLE_VRE + " Timeline = " + vreId);

		boolean result = CassandraConnection.getDatabookStore().saveUserFeed(toShare);

		if(vreId != null && vreId.compareTo("") != 0 && result) {

			_log.trace("Attempting to write onto " + vreId);

			try {

				try{
					_log.info("Sleeping waiting for cassandra's update");
					Thread.sleep(1000);

				}catch(Exception e){

					_log.error(e.toString());

				}
				CassandraConnection.getDatabookStore().saveFeedToVRETimeline(toShare.getKey(), vreId);

				if (hashtags != null && !hashtags.isEmpty())
					CassandraConnection.getDatabookStore().saveHashTags(toShare.getKey(), vreId, hashtags);

			} catch (FeedIDNotFoundException e) {

				_log.error("Error writing onto VRES Time Line" + vreId);
			}  

			_log.trace("Success writing onto " + vreId);				
		}

		if (!result) 
			return null;

		//send the notification about this posts to everyone in the group if notifyGroup is true
		if (vreId != null && vreId.compareTo("") != 0 && notifyGroup) {

			try{

				// get the site from the http request
				SocialNetworkingSite site = new SocialNetworkingSite(httpServletRequest);

				// handle the user
				UserManager uManager = new LiferayUserManager();
				GCubeUser userInfo = uManager.getUserByUsername(userId);
				SocialNetworkingUser socialUser = 
						new SocialNetworkingUser(userId, userInfo.getEmail(), userInfo.getFullname(), userInfo.getUserAvatarURL());


				// handle the scope
				String name = new ScopeBean(vreId).name(); // scope such as devVRE

				GroupManager gManager = new LiferayGroupManager();
				long companyid = gManager.getGroupId(name);
				String groupName =  gManager.getGroup(companyid).getGroupName();

				_log.debug("Company id and name " + companyid + " " + groupName);

				NotificationsManager nm = new ApplicationNotificationsManager(site, vreId, socialUser, NEWS_FEED_PORTLET_CLASSNAME);
				new Thread(
						new PostNotificationsThread(
								toShare.getKey(), 
								toShare.getDescription(), 
								""+companyid, 
								nm, 
								hashtags)
						).start();

				_log.debug("Start sending notifications for feed written by " + userId);
			}catch(Exception e){
				_log.error("Unable to notify users", e);
			}
		}
		return toShare;
	}
}
