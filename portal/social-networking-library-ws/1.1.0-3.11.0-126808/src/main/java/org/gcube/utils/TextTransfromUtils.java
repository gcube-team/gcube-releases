package org.gcube.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class contains utility method for parsing and trasforming users pasted text containing URLs and other utility methods
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class TextTransfromUtils {

	// logger
	private static Logger _log = LoggerFactory.getLogger(TextTransfromUtils.class);

	/**
	 * 
	 * @param preview
	 * @return
	 */
	protected static String convertFileNameAnchorHTML(String url) {
		StringBuilder sb = new StringBuilder();
		sb.append("<span style=\"color:gray; font-size:12px;\">shared </span><a class=\"link\" href=\"").append(url).append("\" target=\"_blank\">").append("a file.").append("</a> ").toString();
		return sb.toString();
	}

	/**
	 * utility method that extract the hashtags from a text
	 * @param postText
	 * @return the list of hashtags present in the text
	 */
	protected static List<String> getHashTags(String postText) {
		List<String> hashtags = new ArrayList<>();		
		Pattern MY_PATTERN = Pattern.compile("^#\\w+|\\s#\\w+");
		Matcher matcher = MY_PATTERN.matcher(postText);
		while (matcher.find()) {
			hashtags.add("#"+matcher.group().replace(" ", "").replace("#", ""));
		}
		return hashtags;		
	}
	/**
	 * convert the hashtag in HTML anchor and also Encode the params Base64
	 * @param escapedFeedText
	 * @param hashtags
	 * @return
	 */
	protected static String convertHashtagsAnchorHTML(String escapedFeedText, List<String> hashtags) {
		for (String hashtag : hashtags) {
			String taggedHTML = "<a class=\"link\" style=\"font-size:14px;\" href=\"?"+
					new String(Base64.encodeBase64(GCubeSocialNetworking.HASHTAG_OID.getBytes()))+"="+
					new String(Base64.encodeBase64(hashtag.getBytes()))+"\">"+hashtag+"</a>";
			// does not work if the word is no preceeded by #
			//dirty trick: double replace because \\boundaries do not accept # char
			final String placeholder = "ñöñö-ñöñö";
			String first = escapedFeedText.replaceAll("(?i)\\b"+hashtag.substring(1)+"\\b", placeholder);
			escapedFeedText = first.replaceAll("#"+placeholder, taggedHTML);
			//this is needed if there is a word equal to an hashtagged one without '#' e.g. #job and job
			escapedFeedText = escapedFeedText.replaceAll(placeholder, hashtag.substring(1)); 
		}
		return escapedFeedText;
	}

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
	 * utility method that extract an url ina text when you paste a link
	 * @param feedText
	 * @return the text with the clickable url in it
	 */
	protected static String extractURL(String feedText) {
		// separate input by spaces ( URLs have no spaces )
		feedText = feedText.replaceAll("(\r\n|\n)"," <br/> ");
		String [] parts = feedText.split("\\s");
		// Attempt to convert each item into an URL.   
		for( String item : parts ) {
			String toCheck = getHttpToken(item);
			if (toCheck != null) {
				try {					
					new URL(toCheck);
					return toCheck;
				} catch (MalformedURLException e) {
					// If there was an URL then it's not valid
					_log.error("MalformedURLException returning... ");
					return null;
				}
			}
		}
		return null;
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
			System.out.println("getHttpToken returns -> " + item);
			return item;
		}
		return null;
	}
	/**
	 * There are several ways to refer an image in a HTML, this method use an heuristic to get the actual image url
	 * @param pageURL the url 
	 * @param srcAttr the content of the img src attribute
	 * @return the image url ready to be referred outside native environment
	 */
	protected static String getImageUrlFromSrcAttribute(URL pageURL, String srcAttr) {
		String imageUrl = srcAttr;
		_log.trace("imageUrl="+imageUrl);
		if (imageUrl.startsWith("http") || imageUrl.startsWith("//")) { // double slash inherits the protocol
			_log.trace("Direct link case");
			return imageUrl;
		}
		if (imageUrl.startsWith("/"))  {//referred as absolute path case 
			_log.trace("Absolute Path case");
			imageUrl = pageURL.getProtocol()+"://"+pageURL.getHost()+imageUrl;
		}
		else if (imageUrl.startsWith("../")) { //relative path case
			_log.trace("Relative Path case");
			String imageFolder = pageURL.toString().substring(0, pageURL.toString().lastIndexOf("/"));
			imageUrl= imageFolder + "/" + imageUrl;			
		}
		else if (!imageUrl.contains("/") || !imageUrl.startsWith("/")) {  //the image is probably in the same folder or in a path starting from the last slash
			_log.trace("probably in the same folder");
			// e.g. http://www.adomain.com/docrep/018/i3328e/i3328e00.htm?utm_source	
			String checkedURL = pageURL.toString();
			if (! checkedURL.endsWith("/")) {
				checkedURL+="/";
			}
			String imageFolder = pageURL.toString().substring(0, pageURL.toString().lastIndexOf("/"));
			//it means the url was sth like http://www.asite.com without ending slash
			if (imageFolder.compareToIgnoreCase("http:/") == 0 || imageFolder.compareToIgnoreCase("https:/") == 0) {
				imageFolder = pageURL.toString();
			}
			imageUrl= imageFolder + "/" + imageUrl;
		}
		else if (!imageUrl.startsWith("http") ) { //e.g. http://adomain.com/anImage.png
			_log.trace("In the root");
			imageUrl = pageURL.toExternalForm().endsWith("/") ? pageURL.toExternalForm() + imageUrl :  pageURL.toExternalForm() + "/" + imageUrl;
		} 
		return imageUrl;
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
					sb.append("<a class=\"link\" style=\"font-size:14px;\" href=\"").append(url).append("\" target=\"_blank\">").append(url).append("</a> ");    
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

	protected static String replaceAmpersand(String toReplace) {
		String toReturn = toReplace.replaceAll("&amp;", "&");
		return toReturn;
	}
}
