package gr.uoa.di.madgik.urlresolutionlibrary.streamable;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.LocatorException;
import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class FTPLocator implements Streamable {
	private static Logger logger = Logger.getLogger(FTPLocator.class.getName());

	public static final String FTP_PROTOCOL = "ftp";
	public static final String FTPS_PROTOCOL = "ftps";
	private static final String ANONYMOUS_USERNAME = "anonymous";
	private static final String ANONYMOUS_PASSWORD = "";
	private static final int DEFAULT_FTP_PORT = 21;
	private static final int DEFAULT_FTPS_PORT = 22;
	private static final int FTP_OK_REPLYCODE = 230;

	private int port = DEFAULT_FTP_PORT; // default port
	private String username = ANONYMOUS_USERNAME;
	private String password = ANONYMOUS_PASSWORD;
	private boolean isSecure = false;
	private String host;
	private String path;
	private InputStream is;

	private FTPClient ftpClient = null;

	public FTPLocator(String url) throws ParseException {
		parseURL(url);
	}

	/**
	 * Parses the URL
	 * 
	 * @param url
	 *            The url in format ftp[s]://username:password@host/path
	 * @throws ParseException
	 */
	private void parseURL(String url) throws ParseException {
		String tmpUrl = url.trim();

		if (!tmpUrl.startsWith(FTP_PROTOCOL + "://")) {
			if (!tmpUrl.startsWith(FTPS_PROTOCOL + "://"))
				throw new ParseException("url is not in ftp(s) format");
			else {
				this.isSecure = true;
				this.port = DEFAULT_FTPS_PORT;
			}
		}
		if (isSecure)
			tmpUrl = tmpUrl.substring((FTPS_PROTOCOL + "://").length());
		else
			tmpUrl = tmpUrl.substring((FTP_PROTOCOL + "://").length());

		if (tmpUrl.contains("@")) {
			int atIdx = tmpUrl.lastIndexOf('@');

			if (atIdx >= tmpUrl.length())
				throw new ParseException("url has not username and password");

			String credentials = tmpUrl.substring(0, atIdx);
			String hostpath = tmpUrl.substring(atIdx + 1, tmpUrl.length());

			String[] parts2 = credentials.split(":");
			if (parts2 == null || parts2.length != 2)
				throw new ParseException("url has not username and password");

			this.username = parts2[0];
			this.password = parts2[1];

			tmpUrl = hostpath;
		}

		String[] parts = tmpUrl.split("/", 2);
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
		logger.log(Level.INFO, "Secure 	 : " + this.isSecure);
	}

	@Override
	public InputStream getInputStream() throws Exception {
		if (isSecure)
			ftpClient = new FTPSClient();
		else
			ftpClient = new FTPClient();
		ftpClient.connect(this.host, this.port);

		if (!ftpClient.login(this.username, this.password)) {
			ftpClient.logout();
			throw new LocatorException("login error");
		}

		if (ftpClient.getReplyCode() != FTP_OK_REPLYCODE)
			throw new LocatorException("FTP reply : " + ftpClient.getReplyString());

		is = ftpClient.retrieveFileStream(this.path);

		return is;
	}

	@Override
	public void close() {
		try {
			ftpClient.logout();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Ftp client logout failed", e);
		}

		if (ftpClient.isConnected()) {
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Ftp client disconnect failed", e);
			}
		}
		try {
			is.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Ftp client input stream close failed", e);
		}
	}
}
