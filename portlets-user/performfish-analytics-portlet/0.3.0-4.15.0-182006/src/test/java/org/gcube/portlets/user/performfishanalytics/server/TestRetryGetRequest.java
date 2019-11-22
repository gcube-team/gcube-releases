/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import org.gcube.portlets.user.performfishanalytics.server.util.HttpCallerUtil;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 7, 2019
 */
public class TestRetryGetRequest {


	private static String serviceURL = "http://thepincopallinourl";

	public static void main(String[] args) {

		try {
			HttpCallerUtil httpCaller = new HttpCallerUtil(serviceURL, null, null);

			httpCaller.performGETRequestWithRetry(null, null, 5);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
