package org.gcube.vomanagement.vomsapi.util;


import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSException;

/**
 * This class generates proxy certificates containing VOMS Attribute
 * Certificates and loads them in memory. The file used to generate the
 * certificate is deleted after the creation. By default the directory used to
 * temporary store proxy credentials is the <code>proxies</code> subdirectory
 * of the current directory where the JVM is running. This class wraps the
 * voms-proxy-init command that must be installed locally.
 * <p>
 * 
 * @author Andrea Turli
 */
public class InMemoryVOMSProxyFactory extends VOMSProxyFactory {

	/**
	 * the logger
	 */
	private static Logger logger = Logger
			.getLogger(InMemoryVOMSProxyFactory.class);

	/**
	 * the default Proxy Dir
	 */
	private static String defaultProxyDir = "proxies";

	/**
	 * the Proxy Dir
	 */
	private String proxyDir;

	/**
	 * Creates a new instance of <code>InMemoryVOMSProxyFactory</code> using
	 * the directory passed as parameter.
	 * 
	 * @param proxyDir
	 *            The directory used to temporary store proxy credentials.
	 * 
	 * @throws IOException
	 *             if the directory specified does not exists and cannot be
	 *             created.
	 */
	public InMemoryVOMSProxyFactory(String proxyDir) throws IOException {
		this.proxyDir = proxyDir;
		File dir = new File(proxyDir);
		dir.mkdirs();
	}

	/**
	 * Creates a new instance of <code>InMemoryVOMSProxyFactory</code> using
	 * the current default directory to temporary store proxy credentials.
	 * 
	 * @throws IOException
	 *             if the default directory used to temporary store proxy
	 *             credentials specified does not exists and cannot be created.
	 */
	public InMemoryVOMSProxyFactory() throws IOException {
		this(InMemoryVOMSProxyFactory.defaultProxyDir);
	}

	/**
	 * Set the default directory used to temporary store proxy credentials. All
	 * instances of <code>InMemoryVOMSProxyFactory</code> created after this
	 * method call will use as default the new <code>defaultProxyDir</code>.
	 * 
	 * @param defaultProxyDir
	 *            the new default directory used to temporary store proxy
	 *            credentials.
	 */
	public static void setDefaultProxyDir(String defaultProxyDir) {
		InMemoryVOMSProxyFactory.defaultProxyDir = defaultProxyDir;
	}

	/**
	 * Get the default directory used to temporary store proxy credentials.
	 * 
	 * @return The default directory used to temporary store proxy credentials.
	 */
	public static String getDefaultProxyDir() {
		return InMemoryVOMSProxyFactory.defaultProxyDir;
	}

	/**
	 * Get the directory used by this instance to temporary store proxy
	 * credentials.
	 * 
	 * @return The directory used by this instance to temporary store proxy
	 *         credentials.
	 */
	public String getProxyDir() {
		return this.proxyDir;
	}

	/**
	 * Create a new proxy certificate from an EEC and an encrypted private key.
	 * The new proxy is written to a file only at generation time. Then the
	 * proxy is loaded in memory and the file is deleted. The file will be
	 * created in the current <code>proxyDir</code> directory.
	 * <p>
	 * Please note that this does not prevent the new proxy to be red from other
	 * processes during its persistence on disk. This method should only be
	 * intended as an easy way to keep the file system clean from proxies.
	 * </p>
	 * 
	 * @param certificate
	 *            the file containing the End Entity Certificate
	 * 
	 * @param key
	 *            the file containing the private key
	 * 
	 * @param password
	 *            the password to decrypt the private key
	 * 
	 * @throws IOException
	 *             If an exception occurs during creation
	 * 
	 * @throws InterruptedException
	 *             If an exception occurs during creation
	 * 
	 * @throws GSSException
	 *             If an exception occurs during parsing of credentials
	 * 
	 * @return The new credentials just created
	 * 
	 */
	public synchronized ExtendedGSSCredential createInMemoryProxy(
			File certificate, File key, String password) throws IOException,
			InterruptedException, GSSException {

		// Generate temporary file where to store final proxy credentials
		File randomFile = new File(this.proxyDir + File.separator + "proxy_"
				+ System.currentTimeMillis());
		try {
			while (randomFile.createNewFile() != true) {
				randomFile = new File(this.proxyDir + File.separator + "proxy_"
						+ System.currentTimeMillis());
			}
		} catch (IOException e) {
			logger.error(
					"Cannot create the temporary file to store credentials", e);
			throw e;
		}

		// Create VOMSProxy
		this.setOutput(randomFile);
		try {
			this.createProxy(certificate, key, password);
		} catch (IOException e) {
			randomFile.delete();
			throw e;
		} catch (InterruptedException e) {
			randomFile.delete();
			throw e;
		}

		// Load Proxy
		ExtendedGSSCredential credentials;
		try {
			credentials = ProxyUtil.loadProxyCredentials(randomFile
					.getAbsolutePath());
		} catch (IOException e) {
			logger.error("Cannot load credentials", e);
			randomFile.delete();
			throw e;
		} catch (GSSException e) {
			logger.error("Cannot load credentials", e);
			randomFile.delete();
			throw e;
		}

		// delete file
		randomFile.delete();

		return credentials;

	}

	/**
	 * 
	 * Create a new proxy certificate from another proxy certificate. The new
	 * proxy is written to a file only at generation time. Then the proxy is
	 * loaded in memory and the file is deleted. The file will be created in the
	 * current <code>proxyDir</code> directory.
	 * <p>
	 * Please note that this does not prevent the new proxy to be red from other
	 * processes during its persistence on disk. This method should only be
	 * intended as an easy way to keep the file system clean from proxies.
	 * </p>
	 * 
	 * @param proxyFile
	 *            the file containing the Proxy Certificate
	 * 
	 * @throws IOException
	 *             If an exception occurs during creation
	 * 
	 * @throws InterruptedException
	 *             If an exception occurs during creation
	 * 
	 * @throws GSSException
	 *             If an exception occurs during parsing of credentials
	 * 
	 * @return The new credentials just created
	 */
	public ExtendedGSSCredential createInMemoryProxy(File proxyFile)
			throws IOException, InterruptedException, GSSException {
		return this.createInMemoryProxy(proxyFile, null, null);
	}

	/**
	 * Create a new proxy certificate from an EEC and an unencrypted private
	 * key. The new proxy is written to a file only at generation time. Then the
	 * proxy is loaded in memory and the file is deleted. The file will be
	 * created in the current <code>proxyDir</code> directory.
	 * <p>
	 * Please note that this does not prevent the new proxy to be red from other
	 * processes during its persistence on disk. This method should only be
	 * intended as an easy way to keep the file system clean from proxies.
	 * </p>
	 * 
	 * @param certificate
	 *            the file containing the End Entity Certificate
	 * @param key
	 *            the file containing the unencrypted private key
	 * 
	 * @throws IOException
	 *             If an exception occurs during creation
	 * 
	 * @throws InterruptedException
	 *             If an exception occurs during creation
	 * 
	 * @throws GSSException
	 *             If an exception occurs during parsing of credentials
	 * 
	 * @return The new credentials just created
	 */
	public ExtendedGSSCredential createInMemoryProxy(File certificate, File key)
			throws IOException, InterruptedException, GSSException {
		return this.createInMemoryProxy(certificate, key, null);
	}

	/**
	 * Create a new proxy certificate from an ExtendedGSSCredentials object in
	 * memory. The object provided will be persisted on local file system and
	 * deleted at the end of the method. The new proxy is written to a file only
	 * at generation time. Then the proxy is loaded in memory and the file is
	 * deleted. All files will be created in the current <code>proxyDir</code>
	 * directory.
	 * <p>
	 * Please note that this does not prevent the new or old proxy to be red
	 * from other processes during its persistence on disk. This method should
	 * only be intended as an easy way to keep the file system clean from
	 * proxies.
	 * </p>
	 * 
	 * @param credentials
	 *            the object containing proxy credentials
	 * 
	 * @throws IOException
	 *             If an exception occurs during creation
	 * 
	 * @throws InterruptedException
	 *             If an exception occurs during creation
	 * 
	 * @throws GSSException
	 *             If an exception occurs during parsing of credentials
	 * 
	 * @return The new credentials just created
	 */
	public ExtendedGSSCredential createInMemoryProxy(
			ExtendedGSSCredential credentials) throws IOException,
			InterruptedException, GSSException {

		ExtendedGSSCredential attributedCredentials;

		// Generate temporary file where to store original proxy credentials
		File randomFile = new File(this.proxyDir + File.separator + "proxy_"
				+ System.currentTimeMillis());
		ProxyUtil.storeProxyCredentials(randomFile.getAbsolutePath(),
				credentials);

		// get proxy
		attributedCredentials = this.createInMemoryProxy(randomFile);

		// remove original credentials
		randomFile.delete();

		return attributedCredentials;
	}

}
