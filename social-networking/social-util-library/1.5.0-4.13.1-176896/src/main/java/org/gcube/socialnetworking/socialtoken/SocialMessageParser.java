package org.gcube.socialnetworking.socialtoken;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.socialnetworking.tokenization.GCubeStringTokenizer;
import org.gcube.socialnetworking.tokenization.Token;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocialMessageParser {

	private static final Logger logger = LoggerFactory.getLogger(SocialMessageParser.class);
	
	private final String originalMessage;
	
	private StringWriter stringWriter;
	
	private List<ReplaceableToken> tokens;
	private List<HashTagToken> tagTokens;
	private List<URLToken> urlTokens;
	
	private List<String> hashtags;
	
	public SocialMessageParser(String message) {
		this.originalMessage = message;
	}
	
	public List<ReplaceableToken> getTokens() {
		if(tokens==null){
			tokens = new ArrayList<>();
			tagTokens = new ArrayList<>();
			urlTokens = new ArrayList<>();
			hashtags = new ArrayList<>();
			
			GCubeStringTokenizer socialStringTokenizer = new GCubeStringTokenizer(originalMessage);
			for(Token token : socialStringTokenizer.getTokens()) {
				String tokenString = token.getToken();
				if(tokenString.startsWith("#")) {
					HashTagToken tagToken = new HashTagToken(token);
					try {
						hashtags.add(tagToken.getTag());
						tokens.add(tagToken);
						tagTokens.add(tagToken);
						continue;
					}catch (Exception e) {
						// Not a valid tag
					}
				}
				
				URL url = URLToken.isURL(tokenString);
				if(url!=null) {
					URLToken urlToken = new URLToken(token);
					tokens.add(urlToken);
					urlTokens.add(urlToken);
					continue;
				}
				
				ReplaceableToken replaceableToken = new ReplaceableToken(token);
				tokens.add(replaceableToken);
			}
		}
		return tokens;
	}
	
	public String getParsedMessage() {
		if(stringWriter==null) {
			stringWriter = new StringWriter();
			for(ReplaceableToken token : getTokens()) {
				stringWriter.append(token.getTokenReplacement());
				stringWriter.append(token.getDelimiter());
			}
		}
		return stringWriter.toString();
	}
	
	
	public String getParsedMessage(List<ItemBean> taggedPeople, String siteLandingPagePath) {
		String parsedMessage = getParsedMessage();
		for (ItemBean tagged : taggedPeople) {
			String baseURL;
			String hrefAttributeName;
			String hrefAttributeValue;
			if (! tagged.isItemGroup()) {
				baseURL = siteLandingPagePath+GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
				hrefAttributeName = GCubeSocialNetworking.USER_PROFILE_OID;
				hrefAttributeValue = tagged.getName();
			} else {
				try {
					long teamId = Long.parseLong(tagged.getId());
					GCubeTeam theTeam = new LiferayRoleManager().getTeam(teamId);
					//returns the VRE url e.g. /devVRE
					String vreURL  = new LiferayGroupManager().getGroup(theTeam.getGroupId()).getFriendlyURL();
					//append the members url
					baseURL= GCubePortalConstants.PREFIX_GROUP_URL + vreURL + GCubePortalConstants.GROUP_MEMBERS_FRIENDLY_URL;
					hrefAttributeName = GCubeSocialNetworking.GROUP_MEMBERS_OID;		
					hrefAttributeValue = tagged.getId();
				} catch (Exception e) {
					logger.error("Error while retrieving team {}", tagged.getAlternativeName(), e);
					continue;
				} 
			}
			
			String linkTarget = ReplaceableToken.createHref(baseURL, hrefAttributeName, hrefAttributeValue);
			String replacement = ReplaceableToken.createLink(linkTarget, tagged.getAlternativeName(), null);
			
			parsedMessage = parsedMessage.replace(tagged.getAlternativeName(), replacement);
		}
		return parsedMessage;
	}
	
	public List<String> getHashtags() {
		if(tokens==null){
			getTokens();
		}
		return hashtags;
	}
	
	public List<HashTagToken> getTagTokens() {
		if(tokens==null){
			getTokens();
		}
		return tagTokens;
	}
	
	public List<URLToken> getURLTokens() {
		if(tokens==null){
			getTokens();
		}
		return urlTokens;
	}
}
