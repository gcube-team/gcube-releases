package org.gcube.socialnetworking.socialtoken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanitizedHashTag {
	
	private static final String TAG_REGEX = "^#[\\w-_]*";
	
	private static final Pattern pattern;
	
	static {
		pattern = Pattern.compile(TAG_REGEX);
	}
	
	protected String tag;
	protected String postfix;
	
	public SanitizedHashTag(String string) throws Exception {
		if(string==null || string.compareTo("")==0 || !string.startsWith("#")) {
			throw new Exception(string + "is not a valid TAG");
		}
		
		
	    Matcher matcher = SanitizedHashTag.pattern.matcher(string);
		
	    if(matcher.find()) {
			tag = string.substring(matcher.start(), matcher.end());
			postfix = string.substring(matcher.end());
	    }else {
	    	throw new Exception(string + "is not a valid TAG");
	    }
	}

	public String getTag() {
		return tag;
	}
	
	public String getPostfix() {
		return postfix;
	}

	
	
}