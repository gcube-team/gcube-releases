package org.gcube.vomanagement.vomsapi.impl.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

import org.apache.log4j.Logger;

/**
 * Implementation of X509KeyManager, which always returns one pair of a private
 * key and certificate chain.
 */
class MyX509KeyManager implements X509KeyManager {
	private static Logger logger = Logger.getLogger(MyX509KeyManager.class);

	private final X509Certificate[] certChain;

	private final PrivateKey key;

	public MyX509KeyManager(Certificate[] cchain, PrivateKey key) {
		this.certChain = new X509Certificate[cchain.length];
		System.arraycopy(cchain, 0, this.certChain, 0, cchain.length);
		this.key = key;
	}

	/**
	 * Gets client aliases
	 * 
	 * @param name
	 * @param list
	 *            of principal
	 * @return list of aliases
	 */
	public String[] getClientAliases(String string, Principal[] principals) {
		logger.debug("getClientAliases()");
		return null;
	}

	// Intented to be implemented by GUI for user interaction, but we have only
	// one key.
	/**
	 * Choose client alias
	 * 
	 * @param keyType
	 *            list of keys
	 * @param list
	 *            of issuers
	 * @param open
	 *            socket
	 * @return chosen alias
	 */
	public String chooseClientAlias(String[] keyType, Principal[] issuers,
			Socket socket) {
		logger.debug("chooseClientAlias()");
		for (int i = 0; i < keyType.length; i++)
			logger.debug("keyType[" + i + "]=" + keyType[i]);
		for (int i = 0; i < issuers.length; i++)
			logger.debug("issuers[" + i + "]=" + issuers[i]);
		return "thealias";
	}

	/**
	 * Gets server aliases
	 * 
	 * @param name
	 * @param list
	 *            of principal
	 * @return list of aliases
	 */
	public String[] getServerAliases(String string, Principal[] principals) {
		logger.debug("getServerAliases()");
		return null;
	}

	/**
	 * Choose server alias
	 * 
	 * @param keyType
	 *            list of keys
	 * @param list
	 *            of issuers
	 * @param open
	 *            socket
	 * @return chosen alias
	 */
	public String chooseServerAlias(String string, Principal[] principals,
			Socket socket) {
		logger.debug("chooseServerAlias()");
		return null;
	}

	/**
	 * Gets Certificate chain
	 * 
	 * @param certificate
	 *            alias
	 * @return list of X509Certificate
	 */
	public X509Certificate[] getCertificateChain(String alias) {
		logger.debug("getCertificateChain()");
		return certChain;
	}

	/**
	 * Gets private key
	 * 
	 * @param certificate
	 *            alias
	 * @return private key
	 */
	public PrivateKey getPrivateKey(String alias) {
		logger.debug("getPrivateKey()");
		return key;
	}
}
