package org.gcube.social_networking.socialutillibrary;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.htmlparser.beans.StringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * Hashtag regex enhanced for ticket #4937
	 */
	private static final String HASHTAG_REGEX = "^#\\w+([.]?\\w+)*|\\s#\\w+([.]?\\w+)*";

	/**
	 * Pattern for URLS
	 */
	private static final Pattern URL_PATTERN = Pattern.compile(
			"\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
					"(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
					"|mil|biz|info|mobi|name|aero|jobs|museum" + 
					"|travel|[a-z]{2,5}))(:[\\d]{1,5})?" + 
					"(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
					"((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
					"([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
					"(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
					"([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
			"(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

	/**
	 * 
	 * @param preview
	 * @return
	 */
	public static String convertFileNameAnchorHTML(String url) {
		StringBuilder sb = new StringBuilder();
		sb.append("<span style=\"color:gray; font-size:12px;\">shared </span><a class=\"link\" href=\"").append(url).append("\" target=\"_blank\">").append("a file.").append("</a> ").toString();
		return sb.toString();
	}

	/**
	 * 
	 * @param session the Asl Session
	 * @param withinPortal true when is on Liferay portal
	 * @return the users plus the groups belonging to the current organization (scope) 
	 */
	public static ArrayList<ItemBean> getDisplayableItemBeans(String scope, String currUser, boolean withinPortal) {
		ArrayList<ItemBean> portalBeans = new ArrayList<ItemBean>();
		try {
			if (withinPortal) {
				UserManager um = new LiferayUserManager();
				GroupManager gm = new LiferayGroupManager();
				RoleManager rm = new LiferayRoleManager();
				ScopeBean sb = new ScopeBean(scope);
				List<GCubeUser> users = null;
				List<GCubeTeam> teams = null;

				if (sb.is(Type.INFRASTRUCTURE)){
					users = um.listUsersByGroup(gm.getRootVO().getGroupId());

					// we need to retrieve vres to whom the current user belongs and then retrieve their teams
					List<GCubeGroup> groupsOfCurrentUser = gm.listGroupsByUser(um.getUserId(currUser));

					teams = new ArrayList<GCubeTeam>();

					// retrieve the teams of each group
					for (GCubeGroup gCubeGroup : groupsOfCurrentUser) {
						List<GCubeTeam> partialTeamList = rm.listTeamsByGroup(gCubeGroup.getGroupId());
						if(partialTeamList != null && !partialTeamList.isEmpty())
							teams.addAll(partialTeamList);
					}

					logger.debug("Teams retrieved are "  + teams);

				}
				else if (sb.is(Type.VRE)) { //must be in VRE
					//get the name from the scope
					String orgName = scope.substring(scope.lastIndexOf("/")+1, scope.length());
					//ask the users
					users = um.listUsersByGroup(gm.getGroupId(orgName));
					// ask the teams
					teams = rm.listTeamsByGroup(gm.getGroupId(orgName));
					logger.debug("Teams retrieved are "  + teams);
				}
				else {
					logger.error("Error, you must be in SCOPE VRE OR INFRASTURCTURE, you are in VO SCOPE returning no users");
					return portalBeans;
				}				
				for (GCubeUser user : users) {
					if (user.getUsername().compareTo("test.user") != 0 && user.getUsername().compareTo(currUser) != 0)  { //skip test.user & current user
						portalBeans.add(new ItemBean(user.getUserId()+"", user.getUsername(), user.getFullname(), user.getUserAvatarURL()));
					}
				}
				for (GCubeTeam gCubeTeam : teams) {
					portalBeans.add(new ItemBean(gCubeTeam.getTeamId()+"", gCubeTeam.getTeamName()));
				}
			}
			else { //test users
				portalBeans.add(new ItemBean("12111", "massimiliano.assante", "Test User #1", ""));
				portalBeans.add(new ItemBean("14111", "massimiliano.assante", "Test Second User #2", ""));
				portalBeans.add(new ItemBean("11511", "massimiliano.assante", "Test Third User", ""));
				portalBeans.add(new ItemBean("11611", "massimiliano.assante", "Test Fourth User", ""));
				portalBeans.add(new ItemBean("11711", "massimiliano.assante", "Test Fifth User", ""));
				portalBeans.add(new ItemBean("11811", "massimiliano.assante", "Test Sixth User", ""));
				portalBeans.add(new ItemBean("15811", "massimiliano.assante", "Ninth Testing User", ""));
				portalBeans.add(new ItemBean("15811", "massimiliano.assante", "Eighth Testing User", ""));
				portalBeans.add(new ItemBean("11211", "giogio.giorgi", "Seventh Test User", ""));
				portalBeans.add(new ItemBean("2222", "pino.pinetti", "Tenth Testing User", ""));
			}
		} catch (Exception e) {
			logger.error("Error in server get all contacts ", e);
		}
		return portalBeans;
	}

	/**
	 * utility method that extract the hashtags from a text
	 * @param postText
	 * @return the list of hashtags present in the text
	 */
	public static List<String> getHashTags(String postText) {
		List<String> hashtags = new ArrayList<>();		
		Pattern MY_PATTERN = Pattern.compile(HASHTAG_REGEX);//Pattern.compile("^#\\w+|\\s#\\w+");
		Matcher matcher = MY_PATTERN.matcher(postText);
		while (matcher.find()) {
			hashtags.add("#"+matcher.group().replace(" ", "").replace("#", ""));
		}
		return hashtags;		
	}

	/**
	 * utility method that extract an url ina text when you paste a link.
	 * It returns the first (if any) meaningful url among the ones available.
	 * @param feedText
	 * @return the text with the clickable url in it
	 */
	public static String extractURL(String feedText) {
		// separate input by spaces ( URLs have no spaces )
		feedText = feedText.replaceAll("(\r\n|\n)"," <br/> ");
		String [] parts = feedText.split("\\s");
		// Attempt to convert each item into an URL.   
		for( String item : parts ) {
			String toCheck = getHttpToken(item);
			logger.debug("To check is " + toCheck);
			if (toCheck != null) {
				try {					
					new URL(toCheck);
					return toCheck;
				} catch (MalformedURLException e) {
					// If there was an URL then it's not valid
					logger.error("MalformedURLException skipping token " + toCheck);
				}
			}
		}
		return null;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtmlAndTransformUrl(String html) {
		if (html == null) {
			return null;
		}
		String toReturn = escapeHtml(html);

		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");
		//transfrom the URL in a clickable URL
		toReturn = transformUrls(toReturn);
		// then replace all the double spaces by the html version &nbsp;
		toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
		return toReturn;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(String html) {
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	/**
	 * utility method that convert a url ina text in a clickable url by the browser
	 * and if the user has just pasted a link, converts the link in: shared a link
	 * @param feedText
	 * @return the text with the clickable url in it
	 */
	public static String transformUrls(String feedText) {
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
					sb.append("<a class=\"link\" style=\"font-size:14px;\" href=\"").append(url).append("\" target=\"_blank\">").append(url).append("</a> ");    
				} catch (MalformedURLException e) {
					// If there was an URL then it's not valid
					logger.error("MalformedURLException not converting token = " + toCheck);
					sb.append(parts[i]);
					sb.append(" ");
					//return feedText;
				}
			} else {
				sb.append(parts[i]);
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	/**
	 * check the tokens of a pasted text and see if there's any http link in it
	 * @param item a text token
	 * @return the actual http link
	 */
	public static String getHttpToken(String originalItem) {
		// apply pattern
		String item = null;
		
		//needed because we escape the text and a URL containing the "&" would arrive ad &amp; and the matcher below would stop at ;
		if (originalItem.startsWith("http") || originalItem.startsWith("www") ) {
			originalItem = originalItem.replaceAll("amp;", "");
		}
		
		Matcher matcher = URL_PATTERN.matcher(originalItem);
		if(matcher.find()){
			logger.debug("Found match url " + matcher.group());
			item = matcher.group();
		}else
			return null;

		item = item.startsWith("www") ? "http://"+item : item;
		logger.debug("getHttpToken returns -> " + item);
		return item;
	}

	/**
	 * convert the mentioned people in HTML anchor and also Encode the params Base64
	 * @param escapedFeedText
	 * @param taggedPeople
	 * @return
	 */
	public static String convertMentionPeopleAnchorHTML(String escapedFeedText, ArrayList<ItemBean> taggedPeople, HttpServletRequest request) {
		String pageToRedirectURL = "";
		String httpGETAttrName = "";
		String httpGETAttrValue ="";

		for (ItemBean tagged : taggedPeople) {
			if (! tagged.isItemGroup()) {
				pageToRedirectURL = PortalContext.getConfiguration().getSiteLandingPagePath(request)+GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
				httpGETAttrName = GCubeSocialNetworking.USER_PROFILE_OID;
				httpGETAttrValue = tagged.getName();
			} else {
				long teamId = Long.parseLong(tagged.getId());
				try {
					GCubeTeam theTeam = new LiferayRoleManager().getTeam(teamId);
					//returns the VRE url e.g. /devVRE
					String vreURL  = new LiferayGroupManager().getGroup(theTeam.getGroupId()).getFriendlyURL();
					//append the members url
					pageToRedirectURL= GCubePortalConstants.PREFIX_GROUP_URL + vreURL + GCubePortalConstants.GROUP_MEMBERS_FRIENDLY_URL;
					httpGETAttrName = GCubeSocialNetworking.GROUP_MEMBERS_OID;		
					httpGETAttrValue = tagged.getId();
				} catch (Exception e) {
					logger.error("Error while retrieving team", e);
				} 
			}			
			String taggedHTML = "<a class=\"link\" href=\""+pageToRedirectURL
					+"?"+
					new String(Base64.encodeBase64(httpGETAttrName.getBytes()))+"="+
					new String(Base64.encodeBase64(httpGETAttrValue.getBytes()))+"\">"+tagged.getAlternativeName()+"</a> ";
			escapedFeedText = escapedFeedText.replace(tagged.getAlternativeName(), taggedHTML);
		}
		return escapedFeedText;
	}

	/**
	 * convert the hashtag in HTML anchor and also Encode the params Base64
	 * @param escapedFeedText
	 * @param hashtags
	 * @return
	 */
	public static String convertHashtagsAnchorHTML(String escapedFeedText, List<String> hashtags) {
		for (String hashtag : hashtags) {
			String taggedHTML = "<a class=\"link\" style=\"font-size:14px;\" href=\"?"+
					new String(Base64.encodeBase64(GCubeSocialNetworking.HASHTAG_OID.getBytes()))+"="+
					new String(Base64.encodeBase64(hashtag.getBytes()))+"\">"+hashtag+"</a>";
			//TODO: does not work if the word is no preceeded by #
			//dirty trick: double replace because \\boundaries do not accept # char
			final String placeholder = "ñöñö-ñöñö";
			String first = escapedFeedText.replaceAll("(?i)\\b"+hashtag.substring(1)+"\\b", placeholder);
			escapedFeedText = first.replaceAll("#"+placeholder, taggedHTML);
			//this is needed if there is a word equal to an hashtagged one without '#' e.g. #job and job
			escapedFeedText = escapedFeedText.replaceAll(placeholder, hashtag.substring(1)); 
		}
		return escapedFeedText;
	}

	/**
	 * generate the description parsing the content (Best Guess)
	 * @param link the link to check
	 * @return the description guessed
	 */
	public static String createDescriptionFromContent(String link) {
		StringBean sb = new StringBean();
		sb.setURL(link);
		sb.setLinks(false);
		String description = sb.getStrings();
		description = ((description.length() > 256) ? description.substring(0, 256)+"..." : description);
		return description;
	}
}
