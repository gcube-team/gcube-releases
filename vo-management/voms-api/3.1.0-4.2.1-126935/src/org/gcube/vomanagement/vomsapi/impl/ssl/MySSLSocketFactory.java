package org.gcube.vomanagement.vomsapi.impl.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.DefaultSocketFactory;
import org.apache.axis.components.net.SecureSocketFactory;
import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationException;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIFactory;
import org.gcube.vomanagement.vomsapi.impl.VOMSAdminException;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * <p>
 * Factory for SSLSockets for Axis, which creates sockets for SSLv3 connection.
 * </p>
 * <p>
 * This Factory is used to properly create sockets for outgoing Axis calls when
 * the VOMS-API library is not being used from a Ws-Core container.
 * </p>
 * 
 * <p>
 * As the Axis engine uses the same {@link SSLSocketFactory} to create
 * {@link SSLSocket} for all connections, when the library is used by more than
 * one thread, there is the risk to set the wrong credentials in the
 * {@link SSLSocket}. To avoid this, a synchronization mechanism has been
 * implemented. The {@link MySSLSocketFactory}<code>.LOCK</code> static field represents the
 * global semaphore to synchronize socket creation requests. See the
 * {@link VOMSAPIFactory} class for how to use the lock properly.
 * 
 * @author Paolo Roccetti
 */
public class MySSLSocketFactory extends DefaultSocketFactory implements
		SecureSocketFactory {

	/**
	 * This is the lock to be acquired before to invoke VOMS operations.
	 */
	public static final Object LOCK = new Object();

	/**
	 * The log4j instance
	 */
	private static Logger logger = Logger.getLogger(MySSLSocketFactory.class
			.getName());

	protected static SSLSocketFactory sslFactory = null;

	/**
	 * MySSLSocketFactory constructor
	 * 
	 * @param attributes
	 *            list of attributes
	 * @throws Exception
	 *             thrown if the intialization of the MySSLSocketFactory fails
	 */
	public MySSLSocketFactory(Hashtable attributes) throws Exception {
		super(attributes);

		logger.info("A MySSLSocketFactory has been created!");

	}

	/**
	 * Creates a secure socket
	 * 
	 * 
	 * @param host
	 *            the hostname of the server to contact
	 * @param port
	 *            the port of the server to contact
	 * @param otherHeaders
	 *            other headers
	 * @param useFullURL
	 *            use full URL option
	 * 
	 * @return A new {@link Socket} object
	 * 
	 * @throws Exception
	 *             if the new {@link Socket} object cannot be created
	 */
	public Socket create(String host, int port, StringBuffer otherHeaders,
			BooleanHolder useFullURL) throws Exception {

		// If the static SSLFactory is null, the configuration must be checked.
		if (MySSLSocketFactory.sslFactory == null) {
			throw new VOMSAdminException(
					"A SSLSocketFactory has not been configured for the current Axis call!!");
		}

		logger.debug("create secure socket for VOMS-API to " + host + ":"
				+ port + " using the SSLFactory "
				+ MySSLSocketFactory.sslFactory);

		// create SSL socket
		SSLSocket socket = (SSLSocket) sslFactory.createSocket();

		// enable only SSLv3
		socket.setEnabledProtocols(new String[] { "SSLv3" });

		// enable only ciphers without RC4 (some bug, probably in older globus)
		String[] ciphers = socket.getEnabledCipherSuites();
		ArrayList<String> al = new ArrayList<String>(ciphers.length);

		for (int i = 0; i < ciphers.length; i++) {
			if (ciphers[i].indexOf("RC4") == -1)
				al.add(ciphers[i]);
		}

		socket.setEnabledCipherSuites((String[]) al.toArray(new String[al
				.size()]));

		// connect as client
		socket.setUseClientMode(true);

		// read timeout
		socket.setSoTimeout(30000);

		// connect timeout
		socket.connect(new InetSocketAddress(host, port), 3000);

		logger.debug("Socket created to " + host + ":" + port
				+ "using the SSLFactory " + MySSLSocketFactory.sslFactory);
		return socket;
	}

	/**
	 * Creates a new {@link SSLSocketFactory} configured to use the given
	 * {@link ExtendedGSSCredential} for sockets.
	 * 
	 * @param credentials
	 *            the {@link ExtendedGSSCredential} object that will be used to
	 *            configure credentials for {@link Socket} objects created by
	 *            the returned factory
	 * 
	 * @return a new {@link SSLSocketFactory} configured to use the given
	 *         {@link ExtendedGSSCredential}
	 * 
	 * @throws VOMSAPIConfigurationException
	 *             when the {@link SSLSocketFactory} cannot be created
	 */
	public static SSLSocketFactory createSSLFactory(
			ExtendedGSSCredential credentials)
			throws VOMSAPIConfigurationException {

		logger.info("Registering MySSLSocketFactory as "
				+ "SecureSocketFactory in Axis");

		System.setProperty(
			"org.apache.axis.components.net.SecureSocketFactory",
			MySSLSocketFactory.class.getName());

		logger.debug("The MySSLSocketFactory has been registered as "
				+ "SecureSocketFactory in Axis");

		logger.info("Parsing " + CredentialsUtil.stringCredentials(credentials)
				+ " to get the private key, the public certificate "
				+ "and the trusted authorities");

		Certificate[] certs = null;
		PrivateKey key = null;
		X509Certificate[] trustedCerts = null;

		// creates the key, certs and trusted certs objects
		if (credentials instanceof GlobusGSSCredentialImpl) {
			GlobusCredential globusCred = ((GlobusGSSCredentialImpl) credentials)
					.getGlobusCredential();
			key = globusCred.getPrivateKey();
			certs = globusCred.getCertificateChain();
			trustedCerts = new X509Certificate[] { (X509Certificate) certs[certs.length - 1] };
		} else {
			logger.error("Cannot parse credentials as the credentials object "
					+ "is not an instance of GlobusGSSCredentialImpl");
			throw new VOMSAPIConfigurationException(
					"Cannot parse credentials as the credentials object "
							+ "is not an instance of GlobusGSSCredentialImpl");
		}

		logger.debug("Correctly parsed "
				+ CredentialsUtil.stringCredentials(credentials));

		logger.info("Creating KeyManager and TrustManager "
				+ "to initialize the SSLContext");

		// creates keymanager and trustmanager
		KeyManager[] keyManagers = new KeyManager[] { new MyX509KeyManager(
				certs, key) };

		TrustManager[] trustManagers;
		try {
			trustManagers = new TrustManager[] { new MyX509TrustManager(
					trustedCerts) };
		} catch (IOException e) {
			logger.error("Cannot create the MyX509TrustManager", e);
			throw new VOMSAPIConfigurationException(
					"Cannot create the MyX509TrustManager", e);
		}

		logger.debug("created KeyManager and TrustManager "
				+ "to initialize the SSLContext");

		logger.info("creating the SSLContext and SSLSocketFactory");

		// init SSLContext with our keymanager and trustmanager, and default
		// random device
		SSLContext sctx;
		try {
			sctx = SSLContext.getInstance("SSL");
			sctx.init(keyManagers, trustManagers, null);
		} catch (Exception e) {
			logger.error("Cannot create the SSLContext", e);
			throw new VOMSAPIConfigurationException(
					"Cannot create the SSLContext", e);
		}

		SSLSocketFactory sslFactory = sctx.getSocketFactory();

		logger.info("new SSLSocketFactory created");

		return sslFactory;
	}

	/**
	 * <p>
	 * This method reset the {@link MySSLSocketFactory} to its default
	 * behaviour. This method must be called after one or more Axis calls that
	 * uses the same {@link SSLSocketFactory} have been performed.
	 * </p>
	 * <p>
	 * This method is synchronized on the {@link MySSLSocketFactory}.LOCK
	 * object to avoid modifications of the {@link SSLSocketFactory} from
	 * external code in the same set of Axis calls.
	 * </p>
	 */
	public static void resetSSLFactory() {
		synchronized (MySSLSocketFactory.LOCK) {
			MySSLSocketFactory.sslFactory = null;
		}
	}

	/**
	 * <p>
	 * This method set the {@link MySSLSocketFactory} to create sockets with the
	 * given {@link SSLSocketFactory}. This method must be called before to
	 * perform one or more Axis calls that uses the same
	 * {@link SSLSocketFactory}.
	 * <p>
	 * </p>
	 * This method is synchronized on the {@link MySSLSocketFactory}.LOCK
	 * object to avoid modifications of the {@link SSLSocketFactory} from
	 * external code in the same set of Axis calls.
	 * </p>
	 * 
	 * @param sslFactory
	 *            the {@link SSLSocketFactory} to use to create
	 *            {@link SSLSocket} objects.
	 */
	public static void setCurrentSSLFactory(SSLSocketFactory sslFactory) {
		synchronized (MySSLSocketFactory.LOCK) {
			MySSLSocketFactory.sslFactory = sslFactory;
		}
	}

}
