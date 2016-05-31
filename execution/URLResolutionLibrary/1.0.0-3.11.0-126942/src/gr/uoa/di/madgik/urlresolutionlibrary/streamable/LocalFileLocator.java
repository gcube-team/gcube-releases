package gr.uoa.di.madgik.urlresolutionlibrary.streamable;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class LocalFileLocator implements Streamable {
	private static Logger logger = Logger.getLogger(LocalFileLocator.class.getName());

	public static final String LOCALFILE_PROTOCOL = "file";

	private String url;
	private String path = "/";
	private InputStream is;

	public LocalFileLocator(String uri) throws ParseException {
		this.url = uri;
		parseURL(url);
	}

	/**
	 * Parses the URL
	 * 
	 * @param url
	 *            The url in format file:///path
	 * @throws ParseException
	 */
	private void parseURL(String url) throws ParseException {
		String tmpUrl = url.trim();

		if (!tmpUrl.startsWith(LOCALFILE_PROTOCOL + "://"))
			throw new ParseException("uri is not in local file format");

		this.path = tmpUrl.substring((LOCALFILE_PROTOCOL + "://").length());

		logger.log(Level.INFO, "Parsing results for : " + url);
		logger.log(Level.INFO, "Path 	 : " + this.path);
	}

	@Override
	public InputStream getInputStream() throws Exception {
		is = new FileInputStream(this.path);

		return is;
	}

	@Override
	public void close() {
		try {
			is.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Local File Locator inputstream close failed", e);
		}
	}

}
