package gr.uoa.di.madgik.urlresolutionlibrary.streamable;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class HTTPSimpleLocator implements Streamable {
	private static Logger logger = Logger.getLogger(HTTPSimpleLocator.class.getName());

	public static final String HTTP_PROTOCOL = "http";
	public static final String HTTPS_PROTOCOL = "https";

	private boolean isSecure = false;
	private String url;
	private InputStream is;

	public HTTPSimpleLocator(String url) throws ParseException {
		this.url = url;
		parseURL(url);
	}

	/**
	 * Parses the URL
	 * 
	 * @param url
	 *            The url in format http[s]://path
	 * @throws ParseException
	 */
	private void parseURL(String url) throws ParseException {
		String tmpUrl = url.trim();

		if (!tmpUrl.startsWith(HTTP_PROTOCOL + "://")) {
			if (!tmpUrl.startsWith(HTTPS_PROTOCOL + "://"))
				throw new ParseException("url is not in http(s) format");
			else {
				this.isSecure = true;
			}
		}

		logger.log(Level.INFO, "Parsing results for : " + url);
		logger.log(Level.INFO, "Secure 	 : " + this.isSecure);
	}

	@Override
	public InputStream getInputStream() throws Exception {
		URL url = new URL(this.url);

		URLConnection urlConnection = null;
		if (isSecure)
			urlConnection = (HttpsURLConnection) url.openConnection();
		else
			urlConnection = (HttpURLConnection) url.openConnection();

		is = urlConnection.getInputStream();

		return is;
	}

	@Override
	public void close() {
		try {
			is.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "HTTPSimple Locator inputstream close failed", e);
		}
	}
}
