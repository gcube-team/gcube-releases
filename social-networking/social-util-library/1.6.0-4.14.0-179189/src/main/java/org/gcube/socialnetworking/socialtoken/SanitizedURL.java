package org.gcube.socialnetworking.socialtoken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class SanitizedURL {
	
	private static String CHARACTERS_TO_REMOVE = "[\\.\\,\\;\\(\\)\\:\\\"\\'\\“\\”\\‘\\’\\«\\»]";
	
	protected String prefix;
	protected String postfix;
	protected final URL url;
	
	public SanitizedURL(String urlString) throws MalformedURLException {
		if(Objects.isNull(urlString) || urlString.isEmpty() || urlString.length()<2) {
			throw new MalformedURLException();
		}
		
		
		
		prefix = urlString.substring(0,1);
		if(prefix.matches(CHARACTERS_TO_REMOVE)) {
			prefix = urlString.substring(0, 1);
			urlString = urlString.substring(1);
		}else {
			prefix = "";
		}
		
		if(urlString.startsWith("www.")) {
			urlString = "http://" + urlString;
		}
		
		postfix = urlString.substring(urlString.length()-1);
		if(postfix.matches(CHARACTERS_TO_REMOVE)) {
			urlString = urlString.substring(0, urlString.length()-1);
		}else {
			postfix = "";
		}
		
		url = new URL(urlString);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPostfix() {
		return postfix;
	}

	public URL getURL() {
		return url;
	}
	
}