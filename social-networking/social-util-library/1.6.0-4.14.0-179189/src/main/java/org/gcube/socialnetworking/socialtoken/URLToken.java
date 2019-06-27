package org.gcube.socialnetworking.socialtoken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.gcube.socialnetworking.tokenization.Token;

public class URLToken extends ReplaceableToken {
	
	protected SanitizedURL sanitizedURL;
	
	public URLToken(String token, String delimiter, int start, int end) {
		super(token, delimiter, start, end);
	}
	
	public URLToken(Token token) {
		super(token);
	}
	
	public String getTokenReplacement() {
		if(!replaced) {
			try {
				Map<String,String> anchorAttibutes = new HashMap<>(1);
				anchorAttibutes.put("target", "_blank");
				String url = getExtractedURL().toString();
				tokenReplacement = sanitizedURL.getPrefix() + ReplaceableToken.createLink(url, url, anchorAttibutes) + sanitizedURL.getPostfix();
			}catch(MalformedURLException e) {
				tokenReplacement = token;
			}
			replaced = true;
		}
		return tokenReplacement;
	}

	public URL getExtractedURL() throws MalformedURLException {
		if(sanitizedURL==null) {
			sanitizedURL = new SanitizedURL(token);
		}
		return sanitizedURL.getURL();
	}
	
	public static SanitizedURL isURL(String url) {
		try {
			return new SanitizedURL(url);
		} catch(MalformedURLException e) {
			// not an URL
			return null;
		}
		
	}
}
