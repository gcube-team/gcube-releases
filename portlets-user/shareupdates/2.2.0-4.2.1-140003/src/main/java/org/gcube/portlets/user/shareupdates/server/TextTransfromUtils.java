package org.gcube.portlets.user.shareupdates.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.gcube.portlets.user.shareupdates.server.metaseeker.MetaSeeker;
import org.gcube.portlets.user.shareupdates.server.opengraph.OpenGraph;
import org.gcube.portlets.user.shareupdates.shared.LinkPreview;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlparser.beans.StringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.ssl.HttpsURLConnection;
/**
 * this class contains utility method for parsing and trasforming users pasted text containing URLs and other utility methods
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings({ "deprecation", "restriction" })
public class TextTransfromUtils {
	/**
	 * 
	 */
	private static Logger _log = LoggerFactory.getLogger(ShareUpdateServiceImpl.class);

	/**
	 * generate the description parsing the content (Best Guess)
	 * @param link the link to check
	 * @return the description guessed
	 */
	private static String createDescriptionFromContent(String link) {
		StringBean sb = new StringBean();
		sb.setURL(link);
		sb.setLinks(false);
		String description = sb.getStrings();
		description = ((description.length() > 256) ? description.substring(0, 256)+"..." : description);
		return description;
	}
	/**
	 * try with HtmlCleaner API to read the images
	 * @param pageURL
	 * @return the title of the page or null if can't read it
	 * @throws IOException
	 */
	protected static ArrayList<String> getImagesWithCleaner(URL pageURL) throws IOException {
		ArrayList<String> images = new ArrayList<String>();
		URLConnection conn = pageURL.openConnection();
		//pretend you're a browser (make my request from Java more “browsery-like”.) 
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		Charset charset = OpenGraph.getConnectionCharset(conn);
		BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		String inputLine;
		StringBuffer headContents = new StringBuffer();

		// Loop through each line, looking for the closing head element
		while ((inputLine = dis.readLine()) != null) {
			headContents.append(inputLine + "\r\n");
		}

		String headContentsStr = headContents.toString();
		HtmlCleaner cleaner = new HtmlCleaner();
		// parse the string HTML
		TagNode pageData = cleaner.clean(headContentsStr);
		// open only the title tags
		TagNode[] imgs = pageData.getElementsByName("img", true);
		int upTo =  (imgs.length > 15) ? 15 : imgs.length;
		for (int i = 0; i < upTo; i++) {
			if (imgs[i].hasAttribute("src")) { 
				String imageUrl = getImageUrlFromSrcAttribute(pageURL, imgs[i].getAttributeByName("src"));
				_log.trace("[FOUND image] " + imageUrl);
				if (imageUrl.endsWith(".gif") || imageUrl.endsWith(".GIF")) 
					_log.trace("[Gif image] SKIP " + imageUrl);					
				else {
					images.add(imageUrl);
				}
				
			}
		}
		return images;
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
	 * to use when OpenGraph is not available, Tries Metadata first, then Best guess from page content 
	 * @param pageUrl
	 * @param link
	 * @param host
	 * @return a LinPreview object instance filled with the extracted information
	 * @throws IOException
	 */
	protected static LinkPreview getInfoFromHTML(URLConnection connection, URL pageUrl, String link, String host)  throws Exception {
		LinkPreview toReturn = null;
		String title = "";
		String description = "";

		URLConnection conn = pageUrl.openConnection();
		//pretend you're a browser (make my request from Java more “browsery-like”.) 
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		MetaSeeker ms = null;
		try {
			title = getTitleFromHeader(pageUrl);
			_log.trace("Found Title=" + title);
			ms = new MetaSeeker(connection, pageUrl);

			//try the metadata, otherwise ask the guesser
			description = (ms.getContent("description") != null &&  ! ms.getContent("description").isEmpty()) ?  ms.getContent("description") : createDescriptionFromContent(link);

			ArrayList<String> images = new ArrayList<String>();
			images = getImagesWithCleaner(pageUrl);
			toReturn = new LinkPreview(title, description, link, host, images);

		} catch(Exception e) {
			_log.error("[MANUAL-PARSE] Something wrong with the meta seeker returning ... ");
			e.printStackTrace();
			return toReturn;
		}
		return toReturn;
	}

	/**
	 * @param pageURL
	 * @return the title of the page or null if can't read it
	 * @throws IOException
	 */
	private static String getTitleFromHeader(URL pageURL) throws IOException {
		URLConnection conn = pageURL.openConnection();
		//pretend you're a browser (make my request from Java more “browsery-like”.) 
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		Charset charset = OpenGraph.getConnectionCharset(conn);
		BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		String inputLine;
		StringBuffer headContents = new StringBuffer();

		// Loop through each line, looking for the closing head element
		while ((inputLine = dis.readLine()) != null)
		{
			if (inputLine.contains("</head>")) {
				inputLine = inputLine.substring(0, inputLine.indexOf("</head>") + 7);
				inputLine = inputLine.concat("<body></body></html>");
				headContents.append(inputLine + "\r\n");
				break;
			}
			headContents.append(inputLine + "\r\n");
		}

		String headContentsStr = headContents.toString();
		HtmlCleaner cleaner = new HtmlCleaner();
		// parse the string HTML
		TagNode pageData = cleaner.clean(headContentsStr);
		// open only the title tags
		TagNode[] title = pageData.getElementsByName("title", true);
		if (title != null && title.length > 0 && title[0].getChildren().size() > 0) {
			String theTitle = title[0].getChildren().get(0).toString();
			_log.trace("theTitle: " + theTitle);
			return theTitle;
		}
		return "No-title";
	}
	protected static String replaceAmpersand(String toReplace) {
		String toReturn = toReplace.replaceAll("&amp;", "&");
		return toReturn;
	}
	/**
	 * this method handles the non trusted https connections
	 */
	protected static void trustAllHTTPSConnections() {
		// Create a trust manager that does not validate certificate chains  
		TrustManager[] trustAllCerts = new TrustManager[]{  
				new X509TrustManager() {  
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
						return null;  
					}  

					public void checkClientTrusted(  
							java.security.cert.X509Certificate[] certs, String authType) {  
					}  

					public void checkServerTrusted(  
							java.security.cert.X509Certificate[] certs, String authType) {  
					}  
				}  
		};  
		try {  
			SSLContext sc = SSLContext.getInstance("SSL");  
			sc.init(null, trustAllCerts, new java.security.SecureRandom());  
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());  
		} catch (Exception e) {  
			System.out.println("Error" + e);  
		}  		
	}
	
	
}
