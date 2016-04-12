package gr.uoa.di.madgik.urlresolutionlibrary.nonstreamable;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.globus.ftp.DataSink;
import org.globus.ftp.FileRandomIO;
import org.globus.ftp.GridFTPClient;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class GridFTPLocator implements NonStreamable {
	private static Logger logger = Logger.getLogger(GridFTPLocator.class.getName());

	public static final String GRIDFTP_PROTOCOL = "gridftp";

	private static final String ANONYMOUS_USERNAME = "anonymous";
	private static final String ANONYMOUS_PASSWORD = "";
	private static final int DEFAULT_GRIDFTP_PORT = 21;
	private int port = DEFAULT_GRIDFTP_PORT; // default port
	private String username = ANONYMOUS_USERNAME;
	private String password = ANONYMOUS_PASSWORD;
	private String host;
	private String path;
	private File tempFile;

	public GridFTPLocator(String url) throws ParseException {
		parseURI(url);
	}

	/**
	 * Parses the url
	 * 
	 * @param url
	 *            The url in format gridftp://username:password@host/path
	 * @throws ParseException
	 */
	private void parseURI(String url) throws ParseException {
		String tmpUri = url.trim();

		if (!tmpUri.startsWith(GRIDFTP_PROTOCOL + "://"))
			throw new ParseException("url is not in gridftp format");

		tmpUri = tmpUri.substring((GRIDFTP_PROTOCOL + "://").length());

		if (tmpUri.contains("@")) {
			int atIdx = tmpUri.lastIndexOf('@');

			if (atIdx >= tmpUri.length())
				throw new ParseException("url not in gridftp format. No username and/or password");

			String credentials = tmpUri.substring(0, atIdx);
			String hostpath = tmpUri.substring(atIdx + 1, tmpUri.length());

			String[] parts2 = credentials.split(":");
			if (parts2 == null || parts2.length != 2)
				throw new ParseException("url not in gridftp format. No username and/or password");

			this.username = parts2[0];
			this.password = parts2[1];

			tmpUri = hostpath;
		}

		String[] parts = tmpUri.split("/", 2);
		if (parts[0].contains(":")) {
			String[] parts1 = parts[0].split(":");
			this.host = parts1[0];
			this.port = Integer.parseInt(parts1[1]);
		} else
			this.host = parts[0];
		if (parts.length == 2)
			this.path = parts[1];

		logger.log(Level.INFO, "Parsing results for : " + url);
		logger.log(Level.INFO, "Host 	 : " + this.host);
		logger.log(Level.INFO, "Port 	 : " + this.port);
		logger.log(Level.INFO, "Username : " + this.username);
		logger.log(Level.INFO, "Password : " + this.password);
		logger.log(Level.INFO, "Path 	 : " + this.path);
	}

	@Override
	public void download() throws Exception {
		GridFTPClient client = new GridFTPClient(this.host, this.port);

		try {
			client.authorize(this.username, this.password);
		} catch (Exception e) {
			throw new Exception("Client authorize exception", e);
		}

		
		tempFile = File.createTempFile("gridftp", "file");
		
		DataSink ds = new FileRandomIO(new java.io.RandomAccessFile(tempFile, "rw"));
		long sourceFileSize = client.getSize(this.path);

		client.extendedGet(path, sourceFileSize, ds, null);
		client.close();
	}

	@Override
	public File getFile() {
		return tempFile;
	}

}
