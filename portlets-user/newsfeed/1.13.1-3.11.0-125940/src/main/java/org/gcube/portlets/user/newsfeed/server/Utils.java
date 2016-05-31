package org.gcube.portlets.user.newsfeed.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;

public class Utils {
	private static final Logger _log = LoggerFactory.getLogger(Utils.class);
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
	/**
	 * utility method that extract the hashtags from a text
	 * @param postText
	 * @return the list of hashtags present in the text
	 */
	protected static List<String> getHashTags(String postText) {
		List<String> hashtags = new ArrayList<>();		
		Pattern MY_PATTERN = Pattern.compile("^#\\w+|\\s#\\w+");
		Matcher matcher = MY_PATTERN.matcher(postText);
		while (matcher.find()) {
			hashtags.add("#"+matcher.group().replace(" ", "").replace("#", ""));
		}
		return hashtags;		
	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	protected static String escapeHtmlAndTransformUrl(String html) {
		if (html == null) {
			return null;
		}
		String toReturn = html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");

		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");
		//transfrom the URL in a clickable URL
		toReturn = transformUrls(toReturn);
		// then replace all the double spaces by the html version &nbsp;
		toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
		return toReturn;
	}
	/**
	 * utility method that convert a url ina text in a clickable url by the browser
	 * and if the user has just pasted a link, converts the link in: shared a link
	 * @param feedText
	 * @return the text with the clickable url in it
	 */
	protected static String transformUrls(String feedText) {
		StringBuilder sb = new StringBuilder();
		// separate input by spaces ( URLs have no spaces )
		String [] parts = feedText.split("\\s");
		// Attempt to convert each item into an URL.
		for (int i = 0; i < parts.length; i++) {			
			String toCheck = getHttpToken(parts[i]);
			if (toCheck != null) {
				try {					
					URL url = new URL(toCheck);
					if (i == 0 && parts.length == 1) //then he shared just a link 
						return sb.append("<span style=\"color:gray; font-size:12px;\">shared </span><a class=\"link\" href=\"").append(url).append("\" target=\"_blank\">").append("a link.").append("</a> ").toString();
					// If possible then replace with anchor...
					sb.append("<a class=\"link\" href=\"").append(url).append("\" target=\"_blank\">").append(url).append("</a> ");    
				} catch (MalformedURLException e) {
					// If there was an URL then it's not valid
					_log.error("MalformedURLException returning... ");
					return feedText;
				}
			} else {
				sb.append(parts[i]);
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	/**
	 * convert the mentioned people in HTML anchor and also Encode the params Base64
	 * @param escapedFeedText
	 * @param taggedPeople
	 * @return
	 */
	protected static String convertMentionPeopleAnchorHTML(String escapedFeedText, ArrayList<ItemBean> taggedPeople) {
		for (ItemBean tagged : taggedPeople) {
			String taggedHTML = "<a class=\"link\" href=\""+GCubeSocialNetworking.USER_PROFILE_LINK
					+"?"+
					new String(Base64.encodeBase64(GCubeSocialNetworking.USER_PROFILE_OID.getBytes()))+"="+
					new String(Base64.encodeBase64(tagged.getName().getBytes()))+"\">"+tagged.getAlternativeName()+"</a> ";
			escapedFeedText = escapedFeedText.replace(tagged.getAlternativeName(), taggedHTML);
		}
		return escapedFeedText;
	}
	/**
	 * check the tokens of a pasted text and see if there's any http link in it
	 * @param item a text token
	 * @return the actual http link
	 */
	private static String getHttpToken(String item) {
		if (item.startsWith("http") || item.startsWith("www") || item.startsWith("(www") || item.startsWith("(http")) {
			if (item.startsWith("(")) 
				item = item.substring(1, item.length());
			if (item.endsWith(".") || item.endsWith(")")) { //sometimes people write the url and close the phrase with a .
				item = item.substring(0, item.length()-1);
			}
			item = item.startsWith("www") ? "http://"+item : item;
			//System.out.println("getHttpToken returns -> " + item);
			return item;
		}
		return null;
	}
	
	/**
//	 * 
//	 * @param vreid
//	 * @param hashtag
//	 * @return the feed map for the client containing occurrences of the hashtag
//	 * @throws PrivacyLevelTypeNotFoundException
//	 * @throws FeedTypeNotFoundException
//	 * @throws FeedIDNotFoundException
//	 * @throws ColumnNameNotFoundException
//	 */
//	private HashMap<String, Feed> getFeedsMap(String vreid, String hashtag) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException {
//		if (hashtag.length() < 2)
//			throw new IllegalArgumentException("Hashtag length myst be greater than 1");
//		else {
//			HashMap<String, Feed> feedsMap = new HashMap<String, Feed>();
//			String upperCase = hashtag;
//			String lowerCase = hashtag;
//
//			char afterHashtag = hashtag.charAt(1);
//			if (Character.isUpperCase(afterHashtag)) {
//				upperCase = hashtag;
//				lowerCase =hashtag.substring(0,1)+Character.toLowerCase(afterHashtag)+hashtag.substring(2);
//			} else {
//				upperCase =hashtag.substring(0,1)+Character.toUpperCase(afterHashtag)+hashtag.substring(2);
//				lowerCase = hashtag;					
//			}	
//			
//			ArrayList<Feed> lowerCaseFeeds =  (ArrayList<Feed>) store.getVREFeedsByHashtag(vreid, lowerCase);
//			for (Feed feed : lowerCaseFeeds) {
//				feedsMap.put(feed.getKey(), feed);							
//			}
//			ArrayList<Feed> upperCaseFeeds =  (ArrayList<Feed>) store.getVREFeedsByHashtag(vreid, upperCase);
//			for (Feed feed : upperCaseFeeds) {
//				feedsMap.put(feed.getKey(), feed);							
//			}
//			return feedsMap;
//		}
//	}
}
