package org.gcube.socialnetworking.tokenization;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GCubeStringTokenizer {

	/**
	 * The default delimiter regex is any whitespaces '\s'
	 */
	public static final String DEFAULT_DELIMITER_REGEX = "\\s";
	
	private final String originalString;
	private final String delimiterRegex;
	
	private Pattern pattern;
	private Matcher matcher;
	
	private List<Token> tokens;
	
	public GCubeStringTokenizer(String string) {
		this(string, DEFAULT_DELIMITER_REGEX);
	}
	
	public GCubeStringTokenizer(String string, String delimiterRegex) {
		this.originalString = string;
		this.delimiterRegex = delimiterRegex;
		this.pattern = Pattern.compile(delimiterRegex);
	    this.matcher = pattern.matcher(originalString);
	}
	
	protected Token getToken(int tokenStart) {
		int tokenEnd = matcher.start();
		int delimiterStart = tokenEnd;
		int delimiterEnd = matcher.end(); 
		String tokenString = originalString.substring(tokenStart, tokenEnd);
		String delimiter = originalString.substring(delimiterStart, delimiterEnd);
		Token token = new Token(tokenString, delimiter, tokenStart, tokenEnd);
		return token;
	}

	public List<Token> getTokens() {
		if(tokens==null) {
			tokens = new ArrayList<>();
			int tokenStart = 0;
			while(matcher.find()) {
				Token token = getToken(tokenStart);
				tokens.add(token);
				tokenStart = matcher.end();
			}
			if(tokenStart!=originalString.length()){
				int tokenEnd = originalString.length();
				String tokenString = originalString.substring(tokenStart, tokenEnd);
				Token token = new Token(tokenString, "", tokenStart, tokenEnd);
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	public String getOriginalString() {
		return originalString;
	}

	public String getDelimiterRegex() {
		return delimiterRegex;
	}
	
}
