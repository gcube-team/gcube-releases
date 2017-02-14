package org.gcube.portlets.docxgenerator.utils;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class provides various static methods that relax X509 certificate and
 * hostname verification while using the SSL over the HTTP protocol.
 * 
 * @author Manuele Simi (ISTI-CNR)
 */
public final class SSLRelaxer {

	/**
	 * Hostname verifier.
	 */
	private static HostnameVerifier hostnameVerifier;
	/**
	 * Thrust managers.
	 */
	private static TrustManager[] trustManagers;
	
	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames only for the given connection
	 * 
	 * @param conn the connection
	 */ 
	public static void trustAllHostnames(HttpsURLConnection conn) {
		// Create a trust manager that does not validate certificate chains
		if (hostnameVerifier == null) {
			hostnameVerifier = new FakeHostnameVerifier();
		}
		// Install the all-trusting host name verifier:
		conn.setHostnameVerifier(hostnameVerifier);
	} 

	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones, for the given connection
	 * 
	 * @param conn the connection
	 */
	public static void trustAllHttpsCertificates(HttpsURLConnection conn) {

		SSLContext context;
		// Create a trust manager that does not validate certificate chains
		if (trustManagers == null) {
			trustManagers = new TrustManager[] { new FakeX509TrustManager() };
		} 
		// Install the all-trusting trust manager:
		try {
			context = SSLContext.getInstance("SSL");
			context.init(null, trustManagers, new SecureRandom());
		} catch (GeneralSecurityException gse) {
			throw new IllegalStateException(gse.getMessage());
		} 
		conn.setSSLSocketFactory(context.getSocketFactory());
	} 

	
	/**
	 * This class implements a fake hostname verificator, trusting any host
	 * name.
	 * 
	 * @author Francis Labrie
	 */
	static class FakeHostnameVerifier implements HostnameVerifier {

		/**
		 * Always return true, indicating that the host name is an acceptable
		 * match with the server's authentication scheme.
		 * 
		 * @param hostname
		 *            the host name.
		 * @param session
		 *            the SSL session used on the connection to host.
		 * @return the true boolean value indicating the host name is trusted.
		 */
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return (true);
		}
	} 

	/**
	 * This class allow any X509 certificates to be used to authenticate the
	 * remote side of a secure socket, including self-signed certificates.
	 * 
	 * @author Francis Labrie
	 */
	static class FakeX509TrustManager implements X509TrustManager {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {

		}

		@Override
		public void checkClientTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}
	} 
} 