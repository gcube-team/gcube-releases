package gr.uoa.di.madgik.urlresolutionlibrary.streamable;

import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class HTTPLocator implements Streamable {
	private static Logger logger = Logger.getLogger(HTTPLocator.class.getName());

	public static final String HTTP_PROTOCOL = "http";
	public static final String HTTPS_PROTOCOL = "https";
	private static final int HTTPS_PORT = 443;
	private static final int HTTP_PORT = 80;
	private static final String NO_PASSWORD = "nopassword";
	private static final String CERTIFICATES_FILE_VAR = "CERTIFICATES_FILE_VAR";

	private boolean isSecure = false;
	private String url;
	private HttpClient httpClient;
	private String certificateFilename;
	private String host;
	private String path = "/";
	private int port = HTTP_PORT;
	private InputStream is;

	public HTTPLocator(String uri) throws ParseException {
		this.url = uri;
		parseURL(url);

		if (this.isSecure)
			this.certificateFilename = getCertificateFileName();
	}

	/**
	 * @return a String that shows the absolute path to the certificate of the corresponding host
	 * @throws ParseException if the environment variable is not set or the host is not is not in
	 * the property file.
	 * 
	 * Each system should specify an environment variable that shows a property file
	 * This property file is a map with format %key% = %value% in each line where key is the host 
	 * an value is the absolute path of the corresponding certificate file
	 */
	private String getCertificateFileName() throws ParseException {
		String certfname = null;
		String certificateMapFile = null;

		certificateMapFile = System.getenv(CERTIFICATES_FILE_VAR);
		if (certificateMapFile == null)
			throw new ParseException("Certificate Map file variable not found. Please specify : "
					+ CERTIFICATES_FILE_VAR + " enviroment variable");

		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(certificateMapFile);
		} catch (FileNotFoundException e1) {
			throw new ParseException("Certificate Map " + certificateMapFile + " file could not be loaded");
		}

		try {
			prop.load(in);

		} catch (IOException e) {
			throw new ParseException("Certificate Map " + certificateMapFile + " file could not be loaded");
		}
		try {
			in.close();
		} catch (IOException e) {
		}

		certfname = prop.getProperty(this.host);

		return certfname;
	}

	/**
	 * Parses the URL
	 * 
	 * @param url
	 *            The url in format http[s]://host:port/path
	 * @throws ParseException
	 */
	private void parseURL(String url) throws ParseException {
		String tmpUri = url.trim();

		if (!tmpUri.startsWith(HTTP_PROTOCOL + "://")) {
			if (!tmpUri.startsWith(HTTPS_PROTOCOL + "://"))
				throw new ParseException("url is not in http(s) format");
			else {
				this.isSecure = true;
				this.port = HTTPS_PORT;
			}
		}
		if (isSecure)
			tmpUri = tmpUri.substring((HTTPS_PROTOCOL + "://").length());
		else
			tmpUri = tmpUri.substring((HTTP_PROTOCOL + "://").length());

		String[] parts = tmpUri.split("/", 2);
		if (parts[0].contains(":")) {
			String[] parts1 = parts[0].split(":");
			this.host = parts1[0];
			this.port = Integer.parseInt(parts1[1]);
		} else
			this.host = parts[0];
		if (parts.length == 2)
			this.path += parts[1];

		logger.log(Level.INFO, "Parsing results for : " + url);
		logger.log(Level.INFO, "Host 	 : " + this.host);
		logger.log(Level.INFO, "Port 	 : " + this.port);
		logger.log(Level.INFO, "Path 	 : " + this.path);
		logger.log(Level.INFO, "Secure 	 : " + this.isSecure);
	}

	@Override
	public InputStream getInputStream() throws Exception {
		URI uri = null;
		httpClient = new DefaultHttpClient();

		if (isSecure) {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(null, NO_PASSWORD.toCharArray());
			TrustManager[] tm = null;

			if (certificateFilename != null && certificateFilename.trim().length() > 0) {
				FileInputStream is = new FileInputStream(certificateFilename);

				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				Certificate cert = cf.generateCertificate(is);
				keystore.setCertificateEntry("cert", cert);

				String algo = TrustManagerFactory.getDefaultAlgorithm();
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(algo);
				tmf.init(keystore);
				tm = tmf.getTrustManagers();
			}

			SSLContext clientSSLContext = SSLContext.getInstance("TLS");
			clientSSLContext.init(null, tm, null);

			SSLSocketFactory socketFactory = new SSLSocketFactory(clientSSLContext);
			// Scheme https = new Scheme(HTTPS_PROTOCOL, port, socketFactory);
			Scheme https = new Scheme(HTTPS_PROTOCOL, socketFactory, port);
			httpClient.getConnectionManager().getSchemeRegistry().register(https);
			uri = new URI(HTTPS_PROTOCOL, null, host, port, path, null, null);
			// return uri.toURL().openStream();

		} else {
			// Scheme https = new Scheme(HTTP_PROTOCOL, port,
			// PlainSocketFactory.getSocketFactory());
			Scheme https = new Scheme(HTTP_PROTOCOL, PlainSocketFactory.getSocketFactory(), port);
			httpClient.getConnectionManager().getSchemeRegistry().register(https);
			uri = new URI(HTTP_PROTOCOL, null, host, port, path, null, null);
			// return uri.toURL().openStream();
		}

		HttpGet get = new HttpGet(uri);
		HttpResponse response = httpClient.execute(get);
		HttpEntity entity = response.getEntity();
		is = entity.getContent();

		return is;
	}

	@Override
	public void close() {
		httpClient.getConnectionManager().shutdown();
		try {
			is.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "HTTP Locator inputstream close failed", e);
		}
	}
}
