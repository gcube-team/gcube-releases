package org.gcube.socialnetworking.socialtoken;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.gcube.socialnetworking.tokenization.Token;

public class ReplaceableToken extends Token {
	
	protected boolean replaced;
	protected String tokenReplacement;
	
	public static String createHref(String baseURL, String attributeName, String attributeValue) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(baseURL);
		stringBuilder.append("?");
		stringBuilder.append(new String(Base64.encodeBase64(attributeName.getBytes())));
		stringBuilder.append("=");
		stringBuilder.append(new String(Base64.encodeBase64(attributeValue.getBytes())));
		return stringBuilder.toString();
		
	}
	
	public static String createLink(String linkTarget, String linkValue, Map<String,String> additionalAttributes) {
		Map<String,String> attributes = new HashMap<>();
		if(additionalAttributes != null) {
			attributes.putAll(additionalAttributes);
		}
		attributes.put("class", "link");
		attributes.put("href", linkTarget);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<a");
		for(String key : attributes.keySet()) {
			stringBuilder.append(" ");
			stringBuilder.append(key);
			stringBuilder.append("=\"");
			stringBuilder.append(attributes.get(key));
			stringBuilder.append("\"");
		}
		stringBuilder.append(">");
		stringBuilder.append(linkValue);
		stringBuilder.append("</a>");
		return stringBuilder.toString();
	}
	
	public ReplaceableToken(String token, String delimiter, int start, int end) {
		super(token, delimiter, start, end);
		this.tokenReplacement = token;
		replaced = false;
	}
	
	public ReplaceableToken(Token token) {
		this(token.getToken(), token.getDelimiter(), token.getStart(), token.getEnd());
	}
	
	public ReplaceableToken(Token token, String tokenReplacement) {
		this(token);
		this.tokenReplacement = tokenReplacement;
	}
	
	@Override
	public String getDelimiter() {
		return delimiter.replaceAll("(\r\n|\n)", "<br/>");
	}
	
	public String getTokenReplacement() {
		if(!replaced) {
			/* Switching encoding of HTML tag delimiters '<' and '>' arriving from portlet to thei representation.
			 * This avoid to display them as text because of the subsequent substitution of '&' with '&amp;'
			 * The tag delimiter will be switched back to their encoding
			 */  
			tokenReplacement = tokenReplacement.replaceAll("(&lt;)", "<").replaceAll("(&gt;)", ">");
			// Encoding '&' to display it
			tokenReplacement = tokenReplacement.replaceAll("&", "&amp;");
			// Encoding the HTML tag delimiters
			tokenReplacement = tokenReplacement.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			
			replaced = true;
		}
		return tokenReplacement;
	}
}
