package gr.uoa.di.madgik.urlresolutionlibrary.streamable;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class SFTPLocator implements Streamable {
	private static Logger logger = Logger.getLogger(SFTPLocator.class.getName());

	public static final String SFTP_PROTOCOL = "sftp";
	private static final String ANONYMOUS_USERNAME = "anonymous";
	private static final String ANONYMOUS_PASSWORD = "";
	private static final int DEFAULT_SFTP_PORT = 22;
	private static final String KNOWN_HOSTS_FILE_VAR = "KNOWN_HOSTS_FILE_VAR";

	private int port = DEFAULT_SFTP_PORT; // default port
	private String username = ANONYMOUS_USERNAME;
	private String password = ANONYMOUS_PASSWORD;
	private String host;
	private String path;
	private String knownHostsFile;
	private JSch jsch = null;
	private Session session = null;
	private ChannelSftp sftpChannel = null;

	public SFTPLocator(String url) throws ParseException {
		this.knownHostsFile = getKnownHostsFileName();
		parseURI(url);
	}
	
	
	/**
	 * @return a String that shows the absolute path to the known host file
	 * @throws ParseException if the environment variable is not set 
	 * 
	 * Each system should specify an environment variable that shows the known hosts file
	 */
	private String getKnownHostsFileName() throws ParseException {
		String knownHostsFile = System.getenv(KNOWN_HOSTS_FILE_VAR);
		if (knownHostsFile == null)
			throw new ParseException("Known hosts file variable not found. Please specify : "	+ KNOWN_HOSTS_FILE_VAR + " enviroment variable");
		return knownHostsFile;
	}

	/**
	 * 
	 * @param url
	 *            The url in format <code>sftp://username:password@host/path</code>
	 * @throws ParseException
	 * 
	 *             Parses the URL
	 */
	private void parseURI(String url) throws ParseException {
		String tmpUrl = url.trim();

		if (!tmpUrl.startsWith(SFTP_PROTOCOL + "://"))
			throw new ParseException("uri is not in ftp(s) format");

		tmpUrl = tmpUrl.substring((SFTP_PROTOCOL + "://").length());

		if (tmpUrl.contains("@")) {
			int atIdx = tmpUrl.lastIndexOf('@');

			if (atIdx >= tmpUrl.length())
				throw new ParseException("uri has not username and password");

			String credentials = tmpUrl.substring(0, atIdx);
			String hostpath = tmpUrl.substring(atIdx + 1, tmpUrl.length());

			String[] parts2 = credentials.split(":");
			if (parts2 == null || parts2.length != 2)
				throw new ParseException("uri has not username and password");

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
	}

	@Override
	public InputStream getInputStream() throws Exception {
		jsch = new JSch();

		jsch.setKnownHosts(this.knownHostsFile);
		session = jsch.getSession(this.username, this.host, this.port);
		session.setPassword(this.password);
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();
		sftpChannel = (ChannelSftp) channel;

		InputStream is = sftpChannel.get(this.path);

		return is;
	}

	@Override
	public void close() {
		try {
			sftpChannel.exit();
		} catch (Exception e) {
			logger.log(Level.WARNING, "SFTP Locator inputstream close failed", e);
		}
		session.disconnect();
	}

}
