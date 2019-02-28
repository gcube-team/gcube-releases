package org.gcube.socialnetworking.socialtoken;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.socialnetworking.tokenization.Token;

public class HashTagToken extends ReplaceableToken {

	protected SanitizedHashTag sanitizedTag;
	
	public HashTagToken(String token, String delimiter, int start, int end) {
		super(token, delimiter, start, end);
	}
	
	public HashTagToken(Token token) {
		super(token);
	}
	
	public String getTokenReplacement() {
		if(!replaced) {
			try {
				String tag = getTag();
				String linkTarget = ReplaceableToken.createHref("", GCubeSocialNetworking.HASHTAG_OID, tag);
				tokenReplacement = ReplaceableToken.createLink(linkTarget, tag, null) + sanitizedTag.getPostfix();
			} catch(Exception e) {
				tokenReplacement = token;
			} 
			
			replaced = true;
		}
		return tokenReplacement;
	}
	
	public String getTag() throws Exception {
		if(sanitizedTag==null) {
			sanitizedTag = new SanitizedHashTag(token);
		}
		return sanitizedTag.getTag();
	}
	
}
