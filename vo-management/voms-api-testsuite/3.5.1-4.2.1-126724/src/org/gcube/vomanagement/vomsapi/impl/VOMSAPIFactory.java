package org.gcube.vomanagement.vomsapi.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.CredentialsManager;
import org.gcube.vomanagement.vomsapi.ExtendedVOMSAdmin;
import org.gcube.vomanagement.vomsapi.VOMSACL;
import org.gcube.vomanagement.vomsapi.VOMSAdmin;
import org.gcube.vomanagement.vomsapi.VOMSAttributeManager;
import org.gcube.vomanagement.vomsapi.VOMSAttributes;
import org.gcube.vomanagement.vomsapi.impl.ssl.MySSLSocketFactory;
import org.gcube.vomanagement.vomsapi.impl.utils.VOMSServerBean;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.VOMSACLServiceLocator;
import org.glite.wsdl.services.org_glite_security_voms_service_admin.VOMSAdminServiceLocator;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.VOMSAttributesServiceLocator;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * <p>
 * Factory for VOMS-API stub objects. Each {@link VOMSAPIFactory} is built from
 * a {@link VOMSAPIConfiguration} object. The zero-argument constructor will use
 * the default {@link VOMSAPIConfiguration}, see the
 * {@link VOMSAPIConfiguration} class for details.
 * </p>
 * <p>
 * <b>IMPORTANT NOTE:</b> When the library is used from outside a Ws-Core
 * container, i.e. when the {@link VOMSAPIConfigurationProperty}.RUNS_IN_WS_CORE
 * is false, there is a concurrency problem with Axis. <br>Basically, the Axis
 * engine uses the same {@link SSLSocketFactory} to create {@link SSLSocket} for
 * all connections, and, since credentials are set in the socket factory itself,
 * there is the risk to use wrong credentials to perform an axis call. <br>To solve
 * this issue axis invocations made from {@link VOMSAPIStub} objects have been
 * synchronized. The synchronization mechanism is effective only if the
 * {@link VOMSAPIConfigurationProperty}.RUNS_IN_WS_CORE is false.
 * </p>
 * @author Paolo Roccetti, Ciro Formisano
 */
public class VOMSAPIFactory {

	private static Logger logger = Logger.getLogger(VOMSAPIFactory.class
			.getName());

	private VOMSAPIConfiguration config;

	// This is the SSLSocketFactory to create SSLSocket objects, when the
	// library is not being used in a Ws-Core container
	private SSLSocketFactory sslFactory = null;

	//These are credentials used the last time to create the sslFactory object
	private ExtendedGSSCredential lastUsedCredentials;
	
	// VOMS server list
	private List<VOMSServerBean> serverList;

	/**
	 * Build a new {@link VOMSAPIFactory} object configured with the default
	 * {@link VOMSAPIConfiguration}. See the {@link VOMSAPIConfiguration} class
	 * for configuration details.
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} defaults
	 */
	public VOMSAPIFactory() throws VOMSAPIConfigurationException {
		this(new VOMSAPIConfiguration());

	}

	/**
	 * Build a new {@link VOMSAPIFactory} object configured with the given
	 * {@link VOMSAPIConfiguration}. See the {@link VOMSAPIConfiguration} class
	 * for configuration details.
	 * 
	 * @param configuration the {@link VOMSAPIConfiguration} object to configure the factory
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 */
	public VOMSAPIFactory(VOMSAPIConfiguration configuration)
			throws VOMSAPIConfigurationException {
		this.config = configuration;

		//creates a new sslFactory
		refreshSSLFactory();
		
	}

	public void setServerList (List<VOMSServerBean> serverList)
	{
		this.serverList = serverList;
	}

	/**
	 * Get the {@link VOMSAdmin} interface containing basic methods for VOMS
	 * administration.
	 * 
	 * 
	 * @return an implementation of the {@link VOMSAdmin} interface to manage
	 *         the VOMS VO.
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 * @throws ServiceException -
	 *             if an exception occurs in API initialization
	 */
	public VOMSAdmin getVOMSAdmin() throws VOMSAPIConfigurationException,
			ServiceException {

		// create locator
		VOMSAdminServiceLocator locator = new VOMSAdminServiceLocator();

		// configure locator
		configureLocator(locator);

		// create service URL
		URL url = createURL(locator.getVOMSAdminWSDDServiceName());

		// create service portType
		org.glite.wsdl.services.org_glite_security_voms_service_admin.VOMSAdmin portType = locator
				.getVOMSAdmin(url);

		// create the VOMSAdminImpl object
		return new VOMSAdminImpl(portType, this);
	}

	/**
	 * Get the {@link ExtendedVOMSAdmin} interface containing additional methods
	 * for VOMS administration.
	 * 
	 * @return an implementation of the {@link ExtendedVOMSAdmin} interface to
	 *         manage the VOMS VO.
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 * @throws ServiceException -
	 *             if an exception occurs in API initialization
	 */
	public ExtendedVOMSAdmin getExtendedVOMSAdmin()
			throws VOMSAPIConfigurationException, ServiceException {
		return new ExtendedVOMSAdminImpl(getVOMSAdmin(), getVOMSACL(), this);

	}

	/**
	 * Get the {@link VOMSAttributes} interface containing methods for VOMS
	 * attributes administration.
	 * 
	 * @return an implementation of the {@link VOMSAttributes} interface to
	 *         manage VOMS Attributes.
	 * 
	 * @throws VOMSAPIConfigurationException
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 * @throws ServiceException
	 *             if an exception occurs in API initialization
	 */
	public VOMSAttributes getVOMSAttributes()
			throws VOMSAPIConfigurationException, ServiceException {

		// create locator
		VOMSAttributesServiceLocator locator = new VOMSAttributesServiceLocator();

		// configure locator
		configureLocator(locator);

		// create service URL
		URL url = createURL(locator.getVOMSAttributesWSDDServiceName());

		// create service portType
		org.glite.wsdl.services.org_glite_security_voms_service_attributes.VOMSAttributes portType = locator
				.getVOMSAttributes(url);

		// create the VOMSAttibutesImpl object
		return new VOMSAttributesImpl(portType, this);
	}

	/**
	 * Get the {@link VOMSACL} interface containing methods for VOMS ACL
	 * administration.
	 * 
	 * @return an implementation of the {@link VOMSACL} interface to manage VOMS
	 *         Access Control Lists.
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 * @throws ServiceException -
	 *             if an exception occurs in API initialization
	 */
	public VOMSACL getVOMSACL() throws VOMSAPIConfigurationException,
			ServiceException {

		// create locator
		VOMSACLServiceLocator locator = new VOMSACLServiceLocator();

		// configure locator
		configureLocator(locator);

		// create service URL
		URL url = createURL(locator.getVOMSACLWSDDServiceName());

		// create service portType
		org.glite.wsdl.services.org_glite_security_voms_service_acl.VOMSACL portType = locator
				.getVOMSACL(url);

		// create the VOMACLImpl object
		return new VOMSACLImpl(portType, this);

	}

	/**
	 * Get a CredentialsManager to deal with user credentials
	 * 
	 * @return an implementation of the {@link CredentialsManager} interface to
	 *         manage user credentials.
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 * @throws ServiceException -
	 *             if an exception occurs in API initialization
	 */
	public CredentialsManager getCredentialsManager()
			throws VOMSAPIConfigurationException, ServiceException {
		CredentialsManager response = new CredentialsManagerImpl(this.config, getExtendedVOMSAdmin());
		response.setServerList(this.serverList);
		return response;
	}
	
	/**
	 * Get a VOMSAttributeManager to deal with user credentials
	 * 
	 * @return an implementation of the {@link VOMSAttributeManager} interface to
	 *         manage user credentials.
	 * 
	 * @throws VOMSAPIConfigurationException -
	 *             thrown if an exception occurs in API initialization, due to a
	 *             wrong setting in the {@link VOMSAPIConfiguration} object
	 * @throws ServiceException -
	 *             if an exception occurs in API initialization
	 */
	public VOMSAttributeManager getVOMSAttributeManager()
			throws VOMSAPIConfigurationException, ServiceException {
		VOMSAttributeManager response = new VOMSAttributeManagerImpl(this.config, getExtendedVOMSAdmin());
		response.setServerList(this.serverList);
		return response;
	}

	/**
	 * Get the {@link VOMSAPIConfiguration} object that is used to configure
	 * objects created by this factory.
	 * 
	 * @return the {@link VOMSAPIConfiguration} object used to configure this
	 *         factory
	 */
	public VOMSAPIConfiguration getVOMSAPIConfiguration() {
		return this.config;
	}

	// generate the URL for a VOMS API interface
	private URL createURL(String wsddServiceName)
			throws VOMSAPIConfigurationException {
		try {
			return new URL(
					config
							.getProperty(VOMSAPIConfigurationProperty.VOMS_PROTOCOL),
					config.getProperty(VOMSAPIConfigurationProperty.VOMS_HOST),
					config.getVOMSPort(),
					"/voms/"
							+ config
									.getProperty(VOMSAPIConfigurationProperty.VO_NAME)
							+ "/services/" + wsddServiceName);
		} catch (Exception e) {
			logger.error("Cannot create URL for VOMS interface "
					+ wsddServiceName, e);
			throw new VOMSAPIConfigurationException(
					"Cannot create URL for VOMS interface " + wsddServiceName
							+ ", the VOMS configuration in use is " + config, e);
		}
	}

	// configure a locator for the standalone use
	private void configureLocator(Service locator) {

		// Following settings are needed if the VOMS-API is not being used from
		// a ws-core container
		if (!config.runsInWSCore()) {
			EngineConfiguration clientConfig = new org.apache.axis.configuration.BasicClientConfig();
			locator.setEngineConfiguration(clientConfig);
			locator.setEngine(new AxisClient(clientConfig));
		}

	}

	/**
	 * This method return the lock to synchronize axis invocations in the
	 * VOMS-API library. If the library is not being used in a Ws-Core
	 * container, the {@link MySSLSocketFactory}.LOCK is returned, while in the
	 * other case a new object is returned. This allows for synchronized calls
	 * only if really needed.
	 * 
	 * @return the lock object used to synchronize axis calls.
	 */
	Object getLock() {

		/*
		 * The lock mechanism basically uses the {@link MySSLSocketFactory}.LOCK
		 * object to synchronize calls. The following code has been introduced
		 * in {@link VOMSAPIStub} objects to call VOMS stubs methods.
		 * 
		 * synchronized (getFactory().getLock()) {
		 * getFactory().prepareForCall(this);
		 * 
		 * .... invocations to VOMS stubs .....
		 * 
		 * getFactory().exitFromCall(this); }
		 * 
		 * The synchronization uses a synchronized block to avoid deadlocks
		 * deriving from a cutom management of locks The prepareForCall() set
		 * appropriate credentials in the SSLSocketFactory, while the
		 * exitFromCall() reset credentials to avoid further use. This block
		 * must wrap every call to native VOMS stubs!
		 */
		if (!config.runsInWSCore()) {
			return MySSLSocketFactory.LOCK;
		} else {
			return new Object();
		}
	}

	/**
	 * This method prepares the given interface for an Axis call. Credentials
	 * are configured in the {@link VOMSAPIStub}, or in the
	 * {@link SSLSocketFactory}, depending on the
	 * {@link VOMSAPIConfigurationProperty}.RUNS_IN_WS_CORE property. </br>
	 * 
	 * This method assumes the lock for the Axis call has already been acquired.
	 * This can be done calling this method from within a
	 * <code>synchronized(vomsAPIFactory.getLock()) {
	 * ...}</code> block.
	 * 
	 * @param the
	 *            object to configure for the call
	 * @throws VOMSAPIConfigurationException
	 *             if the library is being used in a standalone client, and
	 *             credentials cannot be configured for the call
	 */
	void prepareForCall(VOMSAPIStub stub) throws VOMSAPIConfigurationException {

		// If the VOMS-API is not running in WS-Core, credentials are configured in the MySSLSocketFactory
		if (!config.runsInWSCore()) {
			
			//check if the sslFactory needs to be refreshed because credentials have changed
			refreshSSLFactory();

			// configure credentials in the SSLSocketFactory
			MySSLSocketFactory.setCurrentSSLFactory(this.sslFactory);
		} else {
			// configure credentials in stubs
			stub.configureVOMSAPIStubForCall();
		}

	}

	/**
	 * This method reset the configuration after Axis call has been executed. If
	 * the VOMS-API library is not being used in Ws-Core, the
	 * {@link SSLSocketFactory} needs to be reset to avoid the use of current
	 * credentials in future calls.
	 */
	void exitFromCall() {

		if (!config.runsInWSCore()) {
			// reset the MySSLSocketFactory configuration
			MySSLSocketFactory.resetSSLFactory();
		}
	}
	

	//This method is used to refresh the SSLFactory when credentials are changed
	private void refreshSSLFactory() throws VOMSAPIConfigurationException {

		// If the VOMS-API is not running in WS-Core, 
		//and credentials have changed from the last creation of the sslFactory
		if (!config.runsInWSCore() && (config.getCredentials() != this.lastUsedCredentials)) {
			
			//get new credentials
			this.lastUsedCredentials = this.config.getCredentials();
			
			//creates the new sslFactory
			this.sslFactory = MySSLSocketFactory.createSSLFactory(this.lastUsedCredentials);	
			
		}
		
	}

}
