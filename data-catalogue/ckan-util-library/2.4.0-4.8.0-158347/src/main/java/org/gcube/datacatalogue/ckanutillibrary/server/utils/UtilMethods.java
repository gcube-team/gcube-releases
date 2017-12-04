package org.gcube.datacatalogue.ckanutillibrary.server.utils;

import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some utility methods used within the library.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class UtilMethods {

	private static final Logger logger = LoggerFactory.getLogger(UtilMethods.class);
	private final static String HTTPS = "https";
	private final static String HTTP = "http";

	/**
	 * Ckan username has _ instead of . (that is, costantino.perciante -> costantino_perciante)
	 * @param owner
	 * @return
	 */
	public static String fromUsernameToCKanUsername(String username){
		if(username == null)
			return null;

		return username.trim().replaceAll("\\.", "_");
	}
	
	/**
	 * Liferay username has . instead of _ (that is, costantino_perciante -> costantino.perciante)
	 * @param owner
	 * @return
	 */
	public static String fromCKanUsernameToUsername(String ckanUsername){
		if(ckanUsername == null)
			return null;

		return ckanUsername.trim().replaceAll("_", ".");
	}

	/**
	 * Generate the catalogue's dataset name from its title
	 * @param title
	 * @return
	 */
	public static String fromProductTitleToName(String title) {
		if(title == null)
			return null;

		String regexTitleNameTransform = "[^A-Za-z0-9_-]";
		return title.trim().replaceAll(regexTitleNameTransform, "_").replaceAll("_+", "_").toLowerCase();
	}

	/**
	 * Convert a display group name to group id
	 * @param groupName
	 * @return
	 */
	public static String fromGroupTitleToName(String groupName){
		if(groupName == null)
			return null;

		String regexGroupNameTransform = "[^A-Za-z0-9-]";
		String modified = groupName.trim().replaceAll(regexGroupNameTransform, "-").replaceAll("-+", "-").toLowerCase();

		if(modified.startsWith("-"))
			modified = modified.substring(1);
		if(modified.endsWith("-"))
			modified = modified.substring(0, modified.length() -1);

		return modified;

	}

	/**
	 * Utility method to check if a something at this url actually exists
	 * @param URLName
	 * @return
	 */
	public static boolean resourceExists(String URLName){

		if(URLName == null || URLName.isEmpty())
			return false;

		try {
			// replace https
			String urlToTest = URLName.replace(HTTPS, HTTP);
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con = (HttpURLConnection) new URL(urlToTest).openConnection();
			con.setRequestMethod("HEAD");
			logger.debug("Return code is " + con.getResponseCode());
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			logger.error("Exception while checking url", e);
			return false;
		}
	}

	/**
	 * Builds a string made of key + scope
	 * @param key
	 * @param scope
	 * @return
	 */
	public static String concatenateSessionKeyScope(String key, String scope){
		if(key == null || scope == null)
			throw new IllegalArgumentException("Key or scope null");
		return key.concat(scope);
	}
	
}
