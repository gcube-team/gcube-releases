package org.gcube.vomanagement.vomsapi.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * <p>
 * {@link VOMSAPIConfiguration} objects contain the configuration parameters to
 * interact with VOMS and MyProxy services. <br> The set of configuration
 * parameters are enumerated in the {@link VOMSAPIConfigurationProperty} enum.
 * </p>
 * 
 * <p>
 * The configuration parameters can be supplied either programmatically, passing
 * a {@link Properties} object to the constructor, or by a configuration file,
 * passing the appropriate {@link File} object to the constructor.<br> In the
 * first case, the {@link Properties} file must contain proprties of the
 * {@link VOMSAPIConfigurationProperty} enumeration.
 * </p>
 * 
 * <p>
 * Using the zero argument constructor will result in the default configuration
 * properties to be loaded. The default configuration file is named
 * VOMS-API.properties. A file with this name will be searched in the standard
 * java search path.<br> NOTE: The default fiel name can be changed
 * programmatically using the static method
 * <code>setDefaultConfigurationFile()</code>.
 * </p>
 * 
 * <p>
 * At creation time the {@link VOMSAPIConfiguration} will try to load
 * credentials configured, if any. In this case precedence will be given to
 * proxy credentials, set using the {@link VOMSAPIConfigurationProperty}.CLIENT_PROXY
 * property.<br> If the proxy crdentials cannot be found, and End Entity
 * Credentials have been configured using the
 * {@link VOMSAPIConfigurationProperty}.CLIENT_CERT,
 * {@link VOMSAPIConfigurationProperty}.CLIENT_KEY properties, these will be
 * loaded.<br> If credentials have been configured, but cannot be loaded, a
 * {@link VOMSAPIConfigurationException} will be thrown. The same exception will
 * also be thrown if loaded credentials are expired.
 * </p>
 * 
 * @author Paolo Roccetti
 */
public class VOMSAPIConfiguration {

	// this is the default name of the properties file
	private static File defaultConfigurationFile = new File(
			"VOMS-API.properties");

	/**
	 * This method set the default configuration {@link File} to load
	 * {@link VOMSAPIConfiguration} properties. This file will be used to load
	 * properties when the zero-argument constructor is used. This file
	 * initially point to the VOMS-API.properties {@link File}.
	 * 
	 * @param defaultConfigFile
	 *            the {@link File} containing the defualt VOMS-API configuration
	 *            properties.
	 */
	public static void setDefaultConfigurationFile(File defaultConfigFile) {
		logger.info("Setting default configuration file to "
				+ defaultConfigurationFile.getAbsolutePath());
		VOMSAPIConfiguration.defaultConfigurationFile = defaultConfigFile;
	}

	/**
	 * Get the default configuration {@link File}
	 * 
	 * @return the default configuration {@link File}
	 */
	public static File getDefaultConfigurationFile() {
		return defaultConfigurationFile;
	}

	/**
	 * The log4j instance
	 */
	private static Logger logger = Logger.getLogger(VOMSAPIConfiguration.class
			.getName());

	private ExtendedGSSCredential credentials = null;

	private Properties properties;

	private transient String password = null;

	/**
	 * Creates a new {@link VOMSAPIConfiguration} object using the default
	 * configuration file.
	 * 
	 * @throws VOMSAPIConfigurationException
	 *             if an error occurs loading credentials for the default
	 *             configuration
	 */
	public VOMSAPIConfiguration() throws VOMSAPIConfigurationException {
		this(defaultConfigurationFile);
	}

	/**
	 * Creates a new {@link VOMSAPIConfiguration} object using the given
	 * configuration {@link Properties}. The {@link Properties} object will be
	 * cloned to avoid later changes.
	 * 
	 * @param configurationProperties
	 *            the configuration properties to use
	 * 
	 * @throws VOMSAPIConfigurationException
	 *             if credentials have been set in the configuration
	 *             {@link Properties} object, but cannot be load.
	 */
	public VOMSAPIConfiguration(Properties configurationProperties)
			throws VOMSAPIConfigurationException {

		logger.debug("Loading configuration properties from object");

		this.properties = new Properties();

		// clone the property set (to avoid later changes)
		Enumeration propEnum = configurationProperties.propertyNames();
		while (propEnum.hasMoreElements()) {
			String property = (String) propEnum.nextElement();
			this.properties.setProperty(property, configurationProperties
					.getProperty(property));
		}

		logger.info("Loaded configuration properties from object");

		// initializes the VOMSAPIConfiguration object
		intializeConfiguration();

	}

	/**
	 * Create a new VOMSAPIConfiguration using the given configuration file.
	 * 
	 * @param configurationFile
	 *            the configuration file to use
	 * 
	 * @throws VOMSAPIConfigurationException
	 *             if an error occurs loading the given configuration file
	 */
	public VOMSAPIConfiguration(File configurationFile)
			throws VOMSAPIConfigurationException {

		if (configurationFile == null) {
			logger.error("Configuration file cannot be null");
			throw new NullPointerException("Configuration file cannot be null");
		}

		// load properties file
		this.properties = new Properties();

		logger.info("Using configuration file "
				+ configurationFile.getAbsolutePath());

		// Read properties file
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(configurationFile);
			this.properties.load(fis);
		} catch (IOException e) {
			logger.error("Cannot load configuration file "
					+ configurationFile.getAbsolutePath(), e);
			throw new VOMSAPIConfigurationException(
					"Cannot load configuration file "
							+ configurationFile.getAbsolutePath(), e);
		} finally {
			// try to close the stream
			try {
				fis.close();
			} catch (IOException e) {
				logger.error(
					"Cannot close the stream of the configuration file "
							+ configurationFile, e);
				throw new VOMSAPIConfigurationException(
						"Cannot close the stream of the configuration file "
								+ configurationFile, e);
			}
		}

		logger.info("Loaded configuration file " + configurationFile);

		// initializes the VOMSAPIConfiguration object
		intializeConfiguration();

	}

	// This method initializes the VOMSAPIConfiguration object from the
	// properties loaded
	private void intializeConfiguration() throws VOMSAPIConfigurationException {

		// removes the client password from the VOMS-API properties,
		// saving it in a dedicated object field, that field cannot be retrieved
		// and will not be printed
		this.password = (String) this.properties
				.remove(VOMSAPIConfigurationProperty.CLIENT_PWD.toString());

		logger
				.info("Initializing the VOMSAPIConfiguration object with properties "
						+ this.properties.toString()
						+ (this.password != null ? " \n NOTE: A CLIENT_PWD property has also been loaded,"
								+ " but is not shown in logs for security reasons"
								: ""));

		// load initial credentials
		loadCredentials();
	}

	// try to load credentials using configured properties
	private void loadCredentials() throws VOMSAPIConfigurationException {

		// try to load credentials from the proxy file, if set
		String clientProxy = this.properties
				.getProperty(VOMSAPIConfigurationProperty.CLIENT_PROXY
						.toString());
		String clientCert = this.properties
				.getProperty(VOMSAPIConfigurationProperty.CLIENT_CERT
						.toString());
		String clientKey = this.properties
				.getProperty(VOMSAPIConfigurationProperty.CLIENT_KEY.toString());

		if (clientProxy != null) {
			try {
				this.credentials = ProxyUtil.loadProxyCredentials(clientProxy);
				logger.debug("Loaded "
						+ CredentialsUtil.stringCredentials(this.credentials)
						+ " from proxy file " + clientProxy);

			} catch (Exception e) {
				logger.error("Cannot load credentials from proxy file: "
						+ clientProxy, e);
				throw new VOMSAPIConfigurationException(
						"Cannot load credentials from proxy file: "
								+ clientProxy, e);
			}

			// try to load credentials from the client certificate, if set
		} else if (clientCert != null) {

			// load end entity credentials
			try {
				this.credentials = CredentialsUtil.loadEndEntityCredentials(
					clientCert, clientKey, this.password);
			} catch (Exception e) {
				logger.error(
					"Cannot load end entity credentials from certificate file: "
							+ clientCert + " and key file: " + clientKey, e);
				throw new VOMSAPIConfigurationException(
						"Cannot load end entity credentials from certificate file: "
								+ clientCert + " and key file: " + clientKey, e);
			}

		}

		// check if loaded credentials are valid
		if (this.credentials != null) {
			verifyCredentials(this.credentials);
		}
	}

	/**
	 * Get the value of a {@link VOMSAPIConfigurationProperty}. The value is
	 * returned as {@link String}. For non-string properties see other get
	 * methods of this class.
	 * 
	 * @param property
	 *            the {@link VOMSAPIConfigurationProperty} to return.
	 * 
	 * @return the value corresponding to the given
	 *         {@link VOMSAPIConfigurationProperty}
	 */
	public String getProperty(VOMSAPIConfigurationProperty property) {

		String value = this.properties.getProperty(property.toString());

		// if the value is null, try with the default value
		if (value == null) {
			value = property.getDefaultValue();
		}

		return value;
	}

	/**
	 * Get the value of the {@link VOMSAPIConfigurationProperty}.VOMS_PORT as
	 * int.
	 * 
	 * @return the value of the {@link VOMSAPIConfigurationProperty}.VOMS_PORT.
	 * 
	 */
	public int getVOMSPort() {

		// get value
		String value = getProperty(VOMSAPIConfigurationProperty.VOMS_PORT);

		return Integer.parseInt(value);
	}

	/**
	 * Get the value of the {@link VOMSAPIConfigurationProperty}.MYPROXY_PORT
	 * as int.
	 * 
	 * @return the value of the {@link VOMSAPIConfigurationProperty}.MYPROXY_PORT.
	 * 
	 */
	public int getMyProxyPort() {

		// get value
		String value = getProperty(VOMSAPIConfigurationProperty.MYPROXY_PORT);

		return Integer.parseInt(value);
	}

	/**
	 * Return true if the property {@link VOMSAPIConfigurationProperty}.RUNS_IN_WS_CORE
	 * has been set to true.
	 * 
	 * @return true if the property {@link VOMSAPIConfigurationProperty}.RUNS_IN_WS_CORE
	 *         has been set to true.
	 */
	public boolean runsInWSCore() {

		// get value
		String value = getProperty(VOMSAPIConfigurationProperty.RUNS_IN_WS_CORE);

		return Boolean.parseBoolean(value);
	}

	/**
	 * Get credentials associated to this {@link VOMSAPIConfiguration} object.
	 * If credentials have been explicitly set for this
	 * {@link VOMSAPIConfiguration} using the setCredentials() method, they will
	 * be returned, otherwise credentials from the configuration file are
	 * returned.
	 * 
	 * @return the {@link ExtendedGSSCredential} associated to this
	 *         {@link VOMSAPIConfiguration} object, null of no credentials have
	 *         been set.
	 */
	public ExtendedGSSCredential getCredentials() {

		logger.debug("Returning "
				+ CredentialsUtil.stringCredentials(this.credentials));

		return this.credentials;

	}

	/**
	 * Set credentials associated to this {@link VOMSAPIConfiguration} object.
	 * 
	 * @param credentials
	 *            the credentials to use to perform requests to MyProxy and
	 *            VOMS.
	 * @throws VOMSAPIConfigurationException
	 *             if given credentials are expired or not valid for some reason
	 * 
	 */
	public void setCredentials(ExtendedGSSCredential credentials)
			throws VOMSAPIConfigurationException {

		verifyCredentials(credentials);

		logger.debug("Setting "
				+ CredentialsUtil.stringCredentials(credentials));

		this.credentials = credentials;

	}

	// verify if credentials are valid, in this case an exception will be thrown
	private void verifyCredentials(ExtendedGSSCredential credentials)
			throws VOMSAPIConfigurationException {

		// check if credentials are valid
		if (CredentialsUtil.isExpired(credentials)) {
			logger.error(CredentialsUtil.stringCredentials(credentials)
					+ " are expired!!!");
			throw new VOMSAPIConfigurationException(CredentialsUtil
					.stringCredentials(credentials)
					+ " are expired!!!");
		}

	}

	@Override
	public String toString() {

		return "VOMSAPIConfiguration["
				+ this.properties.toString()
				+ ",\n\t"
				+ CredentialsUtil.stringCredentials(this.credentials)
				+ ", \n\t"
				+ (this.password != null ? " A password to decrypt credentials is also present in the configuration "
						: " A password to decrypt credentials is NOT present in the configuration ")
				+ "]";
	}
}
