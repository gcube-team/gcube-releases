

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 26, 2013
 *
 */
public class HttpRequestUtil {

	private static final int CONNECTION_TIMEOUT = 1000;
	public static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

	public static boolean urlExists(String urlConn) throws Exception {


		if(urlConn==null || urlConn.isEmpty())
			return false;

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

				httpConnection.disconnect();

				if (code == 200) {
					logger.trace("status code is "+code+" - on url connection: "+urlConn);
					return true;
				}else
					logger.warn("status code is "+code+" - on url connection: "+urlConn);

//				logger.trace("result: "+result);

			} else {
				logger.error("error - not a http request!");
			}

			return false;

		} catch (SocketTimeoutException e) {
			logger.error("Error SocketTimeoutException with url " +urlConn, e);
			return true;
		} catch (MalformedURLException e) {
			logger.error("Error MalformedURLException with url " +urlConn, e);
			throw new Exception("Error MalformedURLException");
		} catch (IOException e) {
			logger.error("Error IOException with url " +urlConn, e);
			throw new Exception("Error IOException");
		}catch (Exception e) {
			logger.error("Error Exception with url " +urlConn, e);
			throw new Exception("Error Exception");
		}
	}



	public static void main(String[] args) throws Exception {
		System.out.println(HttpRequestUtil.urlExists("http://geoserver2.d4science.research-infrastructures.eu/geoserver/wms"));
	}
}
