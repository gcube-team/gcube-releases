package org.gcube.vomanagement.vomsapi;

import org.gcube.vomanagement.vomsapi.impl.CredentialsRetrievalException;
import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfiguration;
import org.gcube.vomanagement.vomsapi.impl.VOMSAdminException;
import org.gridforum.jgss.ExtendedGSSCredential;

/**
 * <p>
 * A {@link CredentialsManager} object retrieve credentials for gCube users.<br> Two
 * types of credentials can be retireved: plain credentials and attributed
 * credentials. Plain credentials does not contains any user role, while
 * attributed credentials contains the set of roles held by the user in the
 * given VOMS group.</p>
 * 
 * <p>
 * Credentials are retrieved from an account on the MyProxy service, using the
 * given username and password. Configuration parameters for the MyProxy are set
 * in the {@link VOMSAPIConfiguration} object.</p>
 * 
 * <p>
 * If properly configured, the MyProxy service can also act as an Online
 * Certification Authority, issuing short-term credentials for gCube users. In
 * this case only the username of the Online CA user must be supplied, and
 * credentials configured in the {@link VOMSAPIConfiguration} object will be used to
 * secure the retrieve operation.</p>
 * 
 * @author Andrea Turli, Paolo Roccetti, Ciro Formisano
 * 
 */
public interface CredentialsManager extends VOMSServerManager{

	/**
	 * Retrieves plain credentials for the given OnlineCA user.
	 * 
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @throws CredentialsRetrievalException
	 *             if an exception occurs retrieving credentials
	 * 
	 * @return plain credentials for the user from the OnlineCA
	 */
	public ExtendedGSSCredential getPlainCredentials(String userName)
			throws CredentialsRetrievalException;

	/**
	 * Retrieves plain credentials for the given MyProxy account.
	 * 
	 * @param userName
	 *            the username of the MyProxy account
	 * 
	 * @param password
	 *            the password of the MyProxy account
	 * 
	 * @throws CredentialsRetrievalException
	 *             if an exception occurs retrieving credentials
	 * 
	 * @return plain credentials for the user from the given MyProxy account
	 */
	public ExtendedGSSCredential getPlainCredentials(String userName,
			String password) throws CredentialsRetrievalException;

	/**
	 * 
	 * Retrieves attributed credentials for the given OnlineCA user. Credentials
	 * are enriched with roles held by the user in the given VOMS group.
	 * 
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @param groupName
	 *            the group name in VOMS where to search for user roles
	 * 
	 * @return attributed credentials for the user
	 * 
	 * @throws CredentialsRetrievalException
	 *             if an exception occurs retrieving credentials
	 * 
	 * @throws VOMSAdminException
	 *             if an exception occurs enriching credentials with VOMS roles
	 */
	public ExtendedGSSCredential getAttributedCredentials(String userName,
			String groupName) throws CredentialsRetrievalException,
			VOMSAdminException;

	/**
	 * 
	 * Retrieves attributed credentials for the given MyProxy account.
	 * Credentials are enriched with roles held by the user in the given VOMS
	 * group.
	 * 
	 * @param userName
	 *            the username of the OnlineCA user
	 * 
	 * @param password
	 *            the password of the MyProxy account
	 * 
	 * @param groupName
	 *            the group name in VOMS where to search for user roles
	 * 
	 * @return attributed credentials for the user
	 * 
	 * @throws CredentialsRetrievalException
	 *             if an exception occurs retrieving credentials
	 * 
	 * @throws VOMSAdminException
	 *             if an exception occurs enriching credentials with VOMS roles
	 */
	public ExtendedGSSCredential getAttributedCredentials(String userName,
			String password, String groupName)
			throws CredentialsRetrievalException, VOMSAdminException;
}