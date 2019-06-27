/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class HttpCheckAvailabilityUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 16, 2016
 */
public class HttpCheckAvailabilityUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpCheckAvailabilityUtil.class);
	private static final int DEFAULT_MAX_ATTEMPS = 3;
	private static final int DEFAULT_MS_TIME_WAIT = 500;

	/**
	 * Check url status performing a HTTP GET.
	 *
	 * @param urlToCkeck the url to ckeck
	 * @param maxAttemps the max attemps. Max number of attemps it must be >= 1
	 * @param msTimeWait the time offset. Time to wait in ms between two attemps
	 * @param httpStatusCode the http status code. The http status code to check (i.e. HttpStatus.SC_OK)
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public static boolean checkUrlStatus(String urlToCkeck, int maxAttemps, int msTimeWait, int httpStatusCode) throws Exception{
		String url = urlToCkeck;

		if(url==null || url.isEmpty())
			throw new Exception("Invalid URL, it is null or empty!");

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		logger.info("Checking status code "+httpStatusCode +" calling url" + url);

		// Create a method instance.
		GetMethod method = new GetMethod(url);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		int attemps = 0;
		boolean statusOK = false;

		if(maxAttemps<1)
			maxAttemps = DEFAULT_MAX_ATTEMPS;

		if(msTimeWait<0)
			msTimeWait = DEFAULT_MS_TIME_WAIT;

		try{
			logger.debug("Max attemps are: "+maxAttemps);
			while(attemps<maxAttemps && !statusOK){
				try {

					logger.debug("Perfoming attemp number: "+(attemps+1));
					// Execute the method.
					int statusCode = client.executeMethod(method);

					if(statusCode == httpStatusCode){
						logger.info("Response has status code: "+statusCode+ ", at URL: " + url);
						statusOK = true;
					}

				} catch (UnknownHostException e){
					logger.error("Fatal UnknownHostException: " + e.getMessage());
				} catch (HttpException e) {
					logger.error("Fatal protocol violation: " + e.getMessage());
				} catch (IOException e) {
					logger.error("Fatal transport error: ", e);
				}

				if(!statusOK){
					logger.debug("Sleeping: "+msTimeWait);
					Thread.sleep(msTimeWait);
				}else{
					logger.debug("Status code is OK, I'm exiting..");
				}

				attemps++;
			}
			logger.debug("Attemps performed: "+attemps);
			logger.info("Is URL reachable? "+statusOK);
			return statusOK;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

	}
}
