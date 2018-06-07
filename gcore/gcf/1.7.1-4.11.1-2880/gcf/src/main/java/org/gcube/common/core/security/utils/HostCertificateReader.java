package org.gcube.common.core.security.utils;


import java.io.IOException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.gsi.jaas.JaasGssUtil;
import org.globus.wsrf.security.SecurityException;
import org.globus.wsrf.security.SecurityManager;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * Host Certificate reader
 * 
 * @author Andrea Turli (ENG)
 * @author Manuele Simi (CNR-ISTI)
 * 
 */
public class HostCertificateReader {

	static GCUBELog logger = new GCUBELog(HostCertificateReader.class);

	/**
	 * Gets the remaining lifetime in seconds of the host credentials of the GHN
	 * 
	 * @return the remaining lifetime of the host's certificate in seconds
	 * @throws SecurityException
	 *             if it is impossible to access the host credentials
	 * @throws GSSException
	 *             if it is impossible to detect the host credentials
	 * @throws ExpiredCredentialsException
	 *             if the credentials are already expired
	 * @throws IndefiniteLifetimeCredentialsException
	 *             if the credentials will never expire
	 * @throws IOException
	 */
	public static int getHostCertificateLifetime() throws SecurityException,
			GSSException, ExpiredCredentialsException,
			IndefiniteLifetimeCredentialsException {
		GSSCredential hostCredentials = null;
		int remainingLifetime = 0;
		try {
			hostCredentials = JaasGssUtil.getCredential(SecurityManager.getManager().getSystemSubject());
			if (hostCredentials != null) {
				remainingLifetime = hostCredentials.getRemainingLifetime();
				if (remainingLifetime == 0)
					throw new ExpiredCredentialsException();
				else if (remainingLifetime == org.ietf.jgss.GSSContext.INDEFINITE_LIFETIME)
					throw new IndefiniteLifetimeCredentialsException();
			} else {
				throw new GSSException(remainingLifetime);
			}
		} catch (SecurityException e) {
			logger.error("Unable to access the host credentials", e);
			throw e;
		} catch (GSSException e) {
			logger.error("Unable to detect the host credentials", e);
			throw e;
		}
		return remainingLifetime;
	}
	
	/**
	 * Gets the DN of the host credentials of the GHN
	 * 
	 * @return the remaining lifetime of the host's certificate in seconds
	 * @throws SecurityException
	 *             if it is impossible to access the host credentials
	 * @throws GSSException
	 *             if it is impossible to detect the host credentials
	 * @throws ExpiredCredentialsException
	 *             if the credentials are already expired	 
	 */
	public static String getHostCertificateDN() throws SecurityException,
			GSSException, ExpiredCredentialsException {
		GSSCredential hostCredentials = null;
		int remainingLifetime = 0;
		try {
			hostCredentials = JaasGssUtil.getCredential(SecurityManager.getManager().getSystemSubject());
			if (hostCredentials != null) {
				return hostCredentials.getName().toString().trim();
			} else {
				throw new GSSException(remainingLifetime);
			}
		} catch (SecurityException e) {
			logger.error("Unable to access the host credentials", e);
			throw e;
		} catch (GSSException e) {
			logger.error("Unable to detect the host credentials", e);
			throw e;
		}
	
	}

	/**
	 * 
	 * Gets the CA of the host credentials of the GHN
	 * 
	 * @return the remaining lifetime of the host's certificate in seconds
	 * @throws SecurityException
	 *             if it is impossible to access the host credentials
	 * @throws GSSException
	 *             if it is impossible to detect the host credentials
	 * @throws ExpiredCredentialsException
	 *             if the credentials are already expired	 
	 *
	 */
	public static String getHostCertificateCA() throws SecurityException,
	GSSException, Exception{
		ExtendedGSSCredential hostCredentials = null;
		int remainingLifetime = 0;
		String caName = null;
		try {
			hostCredentials = (ExtendedGSSCredential)JaasGssUtil.getCredential(SecurityManager.getManager().getSystemSubject());
			if (hostCredentials != null) {
				caName = ProxyUtil.getCA(hostCredentials);
			} else {
				throw new GSSException(remainingLifetime);
			}
		} catch (SecurityException e) {
			logger.error("Unable to access the host credentials", e);
			throw e;
		} catch (GSSException e) {
			logger.error("Unable to detect the host credentials", e);
			throw e;
		}
		return caName.trim();
	}
	
	/**	Host credential already expired exception */
	public static class ExpiredCredentialsException extends Exception {private static final long serialVersionUID = 1L;};

	/**	Indefinite lifetime host credentials exception */
	public static class IndefiniteLifetimeCredentialsException extends Exception {private static final long serialVersionUID = 1L;}

	
}
