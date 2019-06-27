package org.gcube.socialnetworking.socialtoken;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.socialnetworking.tokenization.Token;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class HashTagToken extends ReplaceableToken {

	protected SanitizedHashTag sanitizedHashTag;
	
	public HashTagToken(String token, String delimiter, int start, int end) {
		super(token, delimiter, start, end);
	}
	
	public HashTagToken(Token token) {
		super(token);
	}
	
	public String getTokenReplacement() {
		if(!replaced) {
			try {
				String hashTag = getHashTag();
				String linkTarget = ReplaceableToken.createHref("", GCubeSocialNetworking.HASHTAG_OID, hashTag);
				tokenReplacement = sanitizedHashTag.getPrefix() + ReplaceableToken.createLink(linkTarget, hashTag, null) + sanitizedHashTag.getPostfix();
			} catch(Exception e) {
				tokenReplacement = token;
			} 
			replaced = true;
		}
		return tokenReplacement;
	}
	
	public String getHashTag() throws Exception {
		if(sanitizedHashTag==null) {
			sanitizedHashTag = new SanitizedHashTag(token);
		}
		return sanitizedHashTag.getHashTag();
	}
	
	
	public static SanitizedHashTag isHashTag(String hastag) {
		try {
			return new SanitizedHashTag(hastag);
		} catch(IllegalArgumentException e) {
			// not an HashTag
			return null;
		}
		
	}
}
