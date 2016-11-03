package org.gcube.vomanagement.vomsapi.impl.ssl;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.globus.gsi.TrustedCertificates;

/**
 * Simple trust manager validating server certificates against supplied trusted
 * CAs.
 * 
 */
class MyX509TrustManager implements X509TrustManager {
	private static Logger logger = Logger.getLogger(MyX509TrustManager.class);

	private X509Certificate[] certificateAuthorities;

	private Set trustAnchors;

	/**
	 * Creates an instance with supplied trusted root CAs.
	 * 
	 * @param certificateAuthorities
	 *            CA' certificates
	 * @throws CertificateException
	 *             certificate exception
	 * @throws IOException
	 *             IO exception
	 */
	public MyX509TrustManager(X509Certificate[] certificateAuthorities)
			throws IOException {

		String certificatesDir = "/etc/grid-security/certificates/";

		this.certificateAuthorities = certificateAuthorities;
		this.trustAnchors = new HashSet();

		X509Certificate[] certs = TrustedCertificates
				.loadCertificates(certificatesDir);

		for (int i = 0; i < certs.length; i++) {

			this.trustAnchors.add(new TrustAnchor(certs[i], null));

		}
	}

	// not used on a client
	public X509Certificate[] getAcceptedIssuers() {
		logger.debug("getAcceptedIssuers()");
		return this.certificateAuthorities;
	}

	// not used on a client
	public void checkClientTrusted(X509Certificate[] certs, String authType) {
		logger.debug("checkClientTrusted()");
	}

	/**
	 * Validates certificate chain sent by a server against trusted CAs.
	 * 
	 * @param certs
	 *            certificate chain
	 * @param authType
	 *            type of authentication
	 * @throws CertificateException
	 *             certificate exception
	 */
	public void checkServerTrusted(X509Certificate[] certs, String authType)
			throws CertificateException {
		logger.debug("checkServerTrusted(certs: " + certs.length
				+ ", authType=" + authType + ")");
		for (int i = 0; i < certs.length; i++) {
			logger.debug("cert[" + i + "]="
					+ certs[i].getSubjectX500Principal().toString());
		}

		// validate server certificate
		try {
			PKIXParameters pkixParameters = new PKIXParameters(
					this.trustAnchors);
			pkixParameters.setRevocationEnabled(false);
			CertificateFactory certFact = CertificateFactory
					.getInstance("X.509");
			CertPath path = certFact.generateCertPath(Arrays.asList(certs));
			CertPathValidator certPathValidator = CertPathValidator
					.getInstance("PKIX");
			certPathValidator.validate(path, pkixParameters);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			logger.error(e.getMessage(), e);
		} catch (CertPathValidatorException e) {
			CertificateException ce;
			logger.error(e.getMessage(), e);
			ce = new CertificateException(e.getMessage());
			ce.setStackTrace(e.getStackTrace());
			throw ce;
		}

		logger.debug("server is trusted");
	}
}
