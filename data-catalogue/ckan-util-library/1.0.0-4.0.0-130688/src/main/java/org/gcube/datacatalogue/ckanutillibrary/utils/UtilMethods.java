package org.gcube.datacatalogue.ckanutillibrary.utils;

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

	/**
	 * Ckan username has _ instead of . (that is, costantino.perciante -> costantino_perciante)
	 * @param owner
	 * @return
	 */
	public static String fromUsernameToCKanUsername(String username){
		if(username == null)
			return null;

		return username.replaceAll("\\.", "_");
	}

	/**
	 * Generate the catalogue's dataset name from its title
	 * @param title
	 * @return
	 */
	public static String nameFromTitle(String title) {
		if(title == null)
			return null;
		
		String convertedName = title.replaceAll(" ", "_");
		convertedName =	convertedName.replaceAll("\\.", "_");
		convertedName = convertedName.toLowerCase();
		if(convertedName.endsWith("_"))
			convertedName  = convertedName.substring(0, convertedName.length() - 2);

		return convertedName;
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
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			logger.debug("Return code is " + con.getResponseCode());
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			logger.error("Exception while checking url", e);
			return false;
		}
	}

}
