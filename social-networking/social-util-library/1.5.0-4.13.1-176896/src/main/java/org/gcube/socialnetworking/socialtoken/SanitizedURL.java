package org.gcube.socialnetworking.socialtoken;

import java.net.MalformedURLException;
import java.net.URL;

public class SanitizedURL {
	
	private static String FINAL_CHARACTERS_TO_REMOVE = "[\\.\\,\\;\\)\\:]";
	
	protected String prefix;
	protected String postfix;
	protected final URL url;
	
	public SanitizedURL(String urlString) throws MalformedURLException {
		if(urlString==null || urlString.compareTo("")==0) {
			throw new MalformedURLException();
		}
		
		prefix = "";
		if(urlString.startsWith("(")) {
			prefix = urlString.substring(0, 1);
			urlString = urlString.substring(1);
		}
		
		if(urlString.startsWith("www.")) {
			urlString = "http://" + urlString;
		}
		
		postfix = urlString.substring(urlString.length()-1);
		if(postfix.matches(FINAL_CHARACTERS_TO_REMOVE)) {
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