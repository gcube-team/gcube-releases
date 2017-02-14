package org.gcube.vomanagement.vomsapi.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * <p>
 * This class contains some utility methods to interact with
 * {@link ExtendedGSSCredential} objects.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> In a perfect world, this class should belongs to the gCore security package.
 * It will be moved there if nad when agreed with gCore developers.
 * </p>
 * 
 * @author Paolo Roccetti
 */
public class CredentialsUtil {

	private static Logger logger = Logger.getLogger(CredentialsUtil.class
			.getName());

	//register the bouncycastle provider that is needed by the loadEndEntityCredentials() method
	static{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	
	/**
	 * <p>
	 * This method creates a {@link String} representation of a
	 * {@link ExtendedGSSCredential} object.
	 * </p>
	 * 
	 * <p>
	 * If <code>null</code> is passed as argument the method will return the
	 * "Null credentials!!!" string. If an exception occurs during credentials
	 * parsing, it will be logged by this method and the "Not parsable
	 * credentials!!!" string is returned. This simpifies the management of
	 * exceptions in the external code.
	 * </p>
	 * 
	 * @param credentials
	 *            the credentials to describe.
	 * 
	 * @return a {@link String} describing the given credentials.
	 */
	public static String stringCredentials(ExtendedGSSCredential credentials) {

		if (credentials == null)
			return "Null credentials!!!";

		try {
			return "Credentials[DN='" + getIdentityDN(credentials) + ", CA='"
					+ getIssuerDN(credentials) + "', lifetime "
					+ credentials.getRemainingLifetime() / 3600 + " hours]";
		} catch (GSSException e) {
			// exception creating credentials info
			logger.error("Not parsable credentials!!!", e);
			return "Not parsable credentials!!!";
		}
	}

	/**
	 * <p>
	 * Return the issuer of given credentials, i.e. the Distinguished Name of
	 * the Certification Authority that issued the credentials.<br> If null is given
	 * as argument the "Null Credentials!!!" string will be returned.<br> If
	 * credentials are not null, but the issuer cannot be determined for some
	 * reason, the method will return <code>null</code>.
	 * </p>
	 * 
	 * @param credentials
	 *            the credentials to search for the issuer.
	 * 
	 * @return a {@link String} containing the issuer DN, or null if the issuer
	 *         cannot be determined.
	 */
	public static String getIssuerDN(ExtendedGSSCredential credentials) {

		if (credentials == null)
			return "Null Credentials!!!";
		
		//able to manage globus credentials only
		if (credentials instanceof GlobusGSSCredentialImpl) {
			GlobusGSSCredentialImpl cred = (GlobusGSSCredentialImpl) credentials;
			return CertUtil.toGlobusID(cred.getGlobusCredential().getIssuer());

		}

		return null;

	}

	/**
	 * <p>
	 * Return the identity of given credentials, i.e. the Distinguished Name of
	 * the identity given credentials refers to.<br>If null is given as argument the
	 * "Null Credentials!!!" string will be returned.<br>If credentials are
	 * not null, but the identity cannot be determined for some reason, the
	 * method will return <code>null</code>.
	 * </p>
	 * 
	 * @param credentials
	 *            the credentials to get the identity from.
	 * 
	 * @return a {@link String} containing the identity DN, or null if the identity
	 *         cannot be determined.
	 */
	public static String getIdentityDN(ExtendedGSSCredential credentials) {

		if (credentials == null)
			throw new NullPointerException("Credentials cannot be null");

		//able to manage globus credentials only
		if (credentials instanceof GlobusGSSCredentialImpl) {
			GlobusGSSCredentialImpl cred = (GlobusGSSCredentialImpl) credentials;
			return cred.getGlobusCredential().getIdentity();
		}

		return null;

	}

	/**
	 * <p>
	 * Verify if passed credentials are expired, i.e. if the remaining lifetime
	 * of credentials is zero. If the lifetime of credentials cannot be
	 * determined true is returned.
	 * </p>
	 * 
	 * @param credentials
	 *            the credentials to verify
	 * 
	 * @return true if credentials are expired, or if the lifetime cannot be
	 *         determined, false otherwise.
	 */
	public static boolean isExpired(ExtendedGSSCredential credentials) {
		
		if (credentials == null)
			throw new NullPointerException("Credentials cannot be null");
		
		try {
			return credentials.getRemainingLifetime() == 0;
		} catch (GSSException e) {
			logger.error("Cannot verify lifetime of "
					+ CredentialsUtil.stringCredentials(credentials), e);
			return true;
		}
	}

	/**
	 * This method loads an End Entity Credentials in the PEM format from the
	 * local file system, provided the location of the public certificate, the
	 * private key and the password to decrypt the private key, if needed.
	 * 
	 * @param certFile
	 *            the file containing the public certificate in PEM format
	 * 
	 * @param keyFile
	 *            the key containing the private key in PEM format
	 * 
	 * @param password
	 *            the password to decrypt the private key, if needed.
	 * 
	 * @return the {@link ExtendedGSSCredential} object containing credentials
	 * 
	 * @throws GeneralSecurityException
	 *             if the private key or the public cert cannot be read
	 * @throws IOException
	 *             if the private key or the public cert cannot be read
	 * @throws GSSException
	 *             if the {@link GSSCredential} object cannot be created
	 * 
	 */
	public static ExtendedGSSCredential loadEndEntityCredentials(String certFile,
			String keyFile, String password) throws IOException,
			GeneralSecurityException, GSSException {
		OpenSSLKey key = new BouncyCastleOpenSSLKey(keyFile);

		if (key.isEncrypted()) {
			key.decrypt(password);
		}

		PrivateKey userKey = key.getPrivateKey();
		X509Certificate[] userCerts = CertUtil.loadCertificates(certFile);
		GlobusCredential proxy = new GlobusCredential(userKey, userCerts);
		return new GlobusGSSCredentialImpl(proxy,
				GSSCredential.INITIATE_AND_ACCEPT);
	}

}
