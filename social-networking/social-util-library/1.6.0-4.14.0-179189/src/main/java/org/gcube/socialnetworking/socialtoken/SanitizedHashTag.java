package org.gcube.socialnetworking.socialtoken;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanitizedHashTag {
	
	private static final String RECOGNIZE_HASHTAG_REGEX = "^.{0,3}#[\\w-]*[\\W]{0,3}";
	private static final Pattern RECOGNIZE_HASHTAG_PATTERN;
	
	private static final String HASHTAG_REGEX = "#[\\w-]*";
	private static final Pattern HASHTAG_PATTERN;
	
	static {
		HASHTAG_PATTERN = Pattern.compile(HASHTAG_REGEX);
		RECOGNIZE_HASHTAG_PATTERN = Pattern.compile(RECOGNIZE_HASHTAG_REGEX);
	}
	
	protected String prefix;
	protected String hashTag;
	protected String postfix;
	
	public SanitizedHashTag(String string) throws IllegalArgumentException {
		if(Objects.isNull(string) || string.isEmpty()) {
			throw new IllegalArgumentException(string + " is not a valid TAG");
		}
		
		Matcher recognizeMatcher = SanitizedHashTag.RECOGNIZE_HASHTAG_PATTERN.matcher(string);
		if(!recognizeMatcher.find()) {
			throw new IllegalArgumentException(string + " is not a valid TAG");
		}else {
			if(recognizeMatcher.end()!=(string.length())) {
				throw new IllegalArgumentException(string + " is not a valid TAG");
			}
		}
		
	    Matcher matcher = SanitizedHashTag.HASHTAG_PATTERN.matcher(string);
		
	    if(matcher.find()) {
	    	prefix = string.substring(0,matcher.start());
			hashTag = string.substring(matcher.start(), matcher.end());
			postfix = string.substring(matcher.end());
	    }else {
	    	throw new IllegalArgumentException(string + " is not a valid TAG");
	    }
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getHashTag() {
		return hashTag;
	}
	
	public String getPostfix() {
		return postfix;
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Prefix : '");
		stringBuffer.append(prefix);
		stringBuffer.append("' - Hashtag : '");
		stringBuffer.append(hashTag);
		stringBuffer.append("' - Postfix : '");
		stringBuffer.append(postfix);
		stringBuffer.append("'");
		return stringBuffer.toString();
	}
	
	
}