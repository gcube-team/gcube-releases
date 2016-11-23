package org.gcube.portal.socialmail;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private static final Logger _log = LoggerFactory.getLogger(Utils.class);

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	protected static String escapeHtmlAndTransformUrl(String html) {
		if (html == null) {
			return null;
		}
		String toReturn = html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");

		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");
		//transfrom the URL in a clickable URL
		toReturn = transformUrls(toReturn);
		// then replace all the double spaces by the html version &nbsp;
		toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
		return toReturn;
	}
	/**
	 * utility method that convert a url ina text in a clickable url by the browser
	 * and if the user has just pasted a link, converts the link in: shared a link
	 * @param feedText
	 * @return the text with the clickable url in it
	 */
	protected static String transformUrls(String feedText) {
		StringBuilder sb = new StringBuilder();
		// separate input by spaces ( URLs have no spaces )
		String [] parts = feedText.split("\\s");
		// Attempt to convert each item into an URL.
		for (int i = 0; i < parts.length; i++) {			
			String toCheck = getHttpToken(parts[i]);
			if (toCheck != null) {
				try {					
					URL url = new URL(toCheck);
					if (i == 0 && parts.length == 1) //then he shared just a link 
						return sb.append("<span style=\"color:gray; font-size:12px;\">shared </span><a class=\"link\" href=\"").append(url).append("\" target=\"_blank\">").append("a link.").append("</a> ").toString();
					// If possible then replace with anchor...
					sb.append("<a class=\"link\" href=\"").append(url).append("\" target=\"_blank\">").append(url).append("</a> ");    
				} catch (MalformedURLException e) {
					// If there was an URL then it's not valid
					_log.error("MalformedURLException returning... ");
					return feedText;
				}
			} else {
				sb.append(parts[i]);
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	/**
	 * check the tokens of a pasted text and see if there's any http link in it
	 * @param item a text token
	 * @return the actual http link
	 */
	private static String getHttpToken(String item) {
		if (item.startsWith("http") || item.startsWith("www") || item.startsWith("(www") || item.startsWith("(http")) {
			if (item.startsWith("(")) 
				item = item.substring(1, item.length());
			if (item.endsWith(".") || item.endsWith(")")) { //sometimes people write the url and close the phrase with a .
				item = item.substring(0, item.length()-1);
			}
			item = item.startsWith("www") ? "http://"+item : item;
			//System.out.println("getHttpToken returns -> " + item);
			return item;
		}
		return null;
	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	protected static String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
