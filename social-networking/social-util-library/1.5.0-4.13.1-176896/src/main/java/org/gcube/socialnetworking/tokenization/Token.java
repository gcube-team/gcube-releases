package org.gcube.socialnetworking.tokenization;

public class Token{
	
	protected final String token;
	protected final String delimiter;
	protected final int start;
	protected final int end;
	
	/**
	 * @param token the Token String
	 * @param delimiter the delimiter after token
	 * @param start the start point in the original String 
	 * @param end the end point in the original String
	 */
	public Token(String token, String delimiter, int start, int end){
		this.token = token;
		this.delimiter = delimiter;
		this.start = start;
		this.end = end;
	}

	public String getToken() {
		return token;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
	
	@Override
	public String toString() {
		return String.format("Token '%s', Subsequent delimiter '%s', Start '%d', End '%d'", token, delimiter, start, end);
	}
	
	
}