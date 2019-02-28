package org.gcube.datatransfer.resolver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class HttpRequestUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class HttpRequestUtil {

	private static final String SERVICE_EXCEPTION_REPORT = "ServiceExceptionReport"; //WMS Exception
	private static final String OWS_EXCEPTION_REPORT = "ows:ExceptionReport"; //OWS Exception
	private static final int CONNECTION_TIMEOUT = 1000;
	public static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

	/**
	 * Url exists.
	 *
	 * @param urlConn the url conn
	 * @param verifyIsWmsGeoserver the verify is wms geoserver
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public static boolean urlExists(String urlConn, boolean verifyIsWmsGeoserver) throws Exception {

		URL url;
		try {
			url = new URL(urlConn);

			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
		    connection.setReadTimeout(CONNECTION_TIMEOUT+CONNECTION_TIMEOUT);

			logger.trace("open connection on: " + url);

			// Cast to a HttpURLConnection
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;

				httpConnection.setRequestMethod("GET");

				int code = httpConnection.getResponseCode();

				if(verifyIsWmsGeoserver)
					return isGeoserver(httpConnection);

				httpConnection.disconnect();

				if (code == 200) {
					return true;
				}

//				logger.trace("result: "+result);

			} else {
				logger.error("error - not a http request!");
			}

			return false;

		} catch (SocketTimeoutException e) {
			logger.error("Error SocketTimeoutException with url " +urlConn);
			throw new Exception("Error SocketTimeoutException");
		} catch (MalformedURLException e) {
			logger.error("Error MalformedURLException with url " +urlConn);
			throw new Exception("Error MalformedURLException");
		} catch (IOException e) {
			logger.error("Error IOException with url " +urlConn);
			throw new Exception("Error IOException");
		}catch (Exception e) {
			logger.error("Error Exception with url " +urlConn);
			throw new Exception("Error Exception");
		}
	}


	/**
	 * Checks if is geoserver.
	 *
	 * @param httpConnection the http connection
	 * @return true, if is geoserver
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static boolean isGeoserver(HttpURLConnection httpConnection) throws IOException{

		BufferedReader  rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
		String line;
	    String result = "";

	    int code = httpConnection.getResponseCode();

		if (code == 200) {
			while ((line = rd.readLine()) != null) {
	            result += line;
	         }
		}
		else{
			rd.close();
			return false;
		}
		if(result.contains(OWS_EXCEPTION_REPORT) || result.contains(SERVICE_EXCEPTION_REPORT)){
			rd.close();
			return true;
		}
		rd.close();
		return false;

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(HttpRequestUtil.urlExists("http://geoserver2.d4science.research-infrastructures.eu/geoserver/wms", true));
	}
}
