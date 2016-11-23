package org.gcube.vomanagement.vomsapi.impl;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.CredentialsManager;
import org.gcube.vomanagement.vomsapi.ExtendedVOMSAdmin;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * Implementation of {@link CredentialsManager} interface.
 * 
 * @author Paolo Roccetti
 * 
 */
class CredentialsManagerImpl extends VOMSAttributeAdder implements CredentialsManager {

	private Logger logger;

	/**
	 * Creates a new {@link CredentialsManagerImpl} linked to the given
	 * {@link VOMSAPIFactory}.
	 * 
	 * @param configuration
	 *            the {@link VOMSAPIConfiguration} object used to find
	 *            configuration properties.
	 * 
	 * @param extendedVOMSAdmin
	 *            the {@link ExtendedVOMSAdmin} interface to query the VOMS
	 *            service
	 */
	CredentialsManagerImpl(VOMSAPIConfiguration configuration,
			ExtendedVOMSAdmin extendedVOMSAdmin) {
		super (configuration,extendedVOMSAdmin);
		this.logger = Logger.getLogger(this.getClass());
	}

	public ExtendedGSSCredential getPlainCredentials(String userName)
			throws CredentialsRetrievalException {
		return getPlainCredentials(userName, "      ");
	}

	public ExtendedGSSCredential getPlainCredentials(String userName,
			String password) throws CredentialsRetrievalException {

		// create myproxy object
		MyProxy myproxy = new MyProxy(config
				.getProperty(VOMSAPIConfigurationProperty.MYPROXY_HOST), config
				.getMyProxyPort());

		// get current credentials
		ExtendedGSSCredential credentials = config.getCredentials();

		// perform credentials retrieval
		return getPlainCredentials(myproxy, credentials, userName, password);

	}

	public ExtendedGSSCredential getAttributedCredentials(String userName,
			String groupName) throws CredentialsRetrievalException,
			VOMSAdminException {

		// get Credentials from MyProxy
		ExtendedGSSCredential credentials = getPlainCredentials(userName);

		// add VOMS roles
		return addVOMSRoles(credentials, groupName);

	}

	public ExtendedGSSCredential getAttributedCredentials(String userName,
			String password, String groupName)
			throws CredentialsRetrievalException, VOMSAdminException {

		// get Credentials from MyProxy
		ExtendedGSSCredential credentials = getPlainCredentials(userName);

		// add VOMS roles
		return addVOMSRoles(credentials, groupName);

	}

	// Retrieve credentials from a MyProxy service
	ExtendedGSSCredential getPlainCredentials(MyProxy myproxy,
			ExtendedGSSCredential credentials, String userName, String password)
			throws CredentialsRetrievalException {

		logger.debug("Retrieving credentials from MyProxy ("
				+ myproxy.getHost() + ":" + myproxy.getPort()
				+ " for username " + userName + " using "
				+ CredentialsUtil.stringCredentials(credentials));

		try {
			ExtendedGSSCredential retrievedCredentials = (ExtendedGSSCredential) myproxy
					.get(credentials, userName, password, 24 * 3600);
			logger.info("Retrieved "
					+ CredentialsUtil.stringCredentials(retrievedCredentials)
					+ " for username " + userName);
			return retrievedCredentials;
		} catch (MyProxyException e) {

			String msg = "Cannot get credentials from MyProxy at "
				+ myproxy.getHost() + ":" + myproxy.getPort()
				+ " for userName " + userName + " using "
				+ CredentialsUtil.stringCredentials(credentials);
			
			// Exception retrieving credentials
			logger.error(msg, e);
			throw new CredentialsRetrievalException(msg, e);
		}
	}


}
