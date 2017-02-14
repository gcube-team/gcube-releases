package org.gcube.common.core.security.utils;


import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;
import org.globus.axis.gsi.GSIConstants;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.ietf.jgss.GSSCredential;

/**
 * This class contains utility methods to manage credentials and security
 * related stuff for DILIGENT services and clients.
 * 
 * @author Paolo Roccetti
 */
public class ConfigureSecurity {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(ConfigureSecurity.class);

	/**
	 * This method allows to set security properties on service stubs.
	 * Encryption is used as default protection level. No delegation is
	 * performed.
	 * 
	 * A lifetime of 300 seconds will be used for the GSI Secure Conversation
	 * context.
	 * 
	 * @param stub
	 *            The <code>javax.xml.rpc.Stub</code> object where to set
	 *            security
	 * 
	 * @param credentials
	 *            The object containing credentials to be set in stub
	 */
	public static void setSecurity(Stub stub, GSSCredential credentials) {
		setSecurity(stub, true, credentials, GSIConstants.GSI_MODE_NO_DELEG);
	}

	/**
	 * This method allows to set security properties on service stubs. A
	 * lifetime of 300 seconds will be used for the GSI Secure Conversation
	 * context.
	 * 
	 * @param stub
	 *            The <code>javax.xml.rpc.Stub</code> object where to set
	 *            security
	 * 
	 * @param encryption
	 *            If true encryption of messages will be asked to the service,
	 *            if false signature only is used
	 * 
	 * @param credentials
	 *            The object containing credentials to be set in stub
	 * 
	 * @param delegation
	 *            The value of delegation required. allowed values are: <br>
	 *            <ul>
	 *            <li>org.globus.axis.gsi.GSIConstants.GSI_MODE_NO_DELEG: No
	 *            delegation is performed.
	 *            <li>org.globus.axis.gsi.GSIConstants.GSI_MODE_LIMITED_DELEG:
	 *            Limited delegation is performed (credentials cannot be further
	 *            delegated).
	 *            <li>org.globus.axis.gsi.GSIConstants.GSI_MODE_FULL_DELEG:
	 *            Full delegation is performed (credentials can be further
	 *            delegated).
	 *            </ul>
	 */
	public static void setSecurity(Stub stub, boolean encryption,
			GSSCredential credentials, String delegation) {
		setSecurity(stub, encryption, credentials, delegation, 300);
	}

	/**
	 * This method allows to set security properties on service stubs.
	 * 
	 * @param stub
	 *            The <code>javax.xml.rpc.Stub</code> object where to set
	 *            security, cannot be null.
	 * 
	 * @param encryption
	 *            If true encryption of messages will be asked to the service,
	 *            if false signature only is used
	 * 
	 * @param credentials
	 *            The object containing credentials to be set in stub, cannot be null.
	 * 
	 * @param delegation
	 *            The value of delegation required, cannot be null. Allowed values are: <br>
	 *            <ul>
	 *            <li>org.globus.axis.gsi.GSIConstants.GSI_MODE_NO_DELEG: No
	 *            delegation is performed
	 *            <li>org.globus.axis.gsi.GSIConstants.GSI_MODE_LIMITED_DELEG:
	 *            Limited delegation is performed (credentials cannot be further
	 *            delegated)
	 *            <li>org.globus.axis.gsi.GSIConstants.GSI_MODE_FULL_DELEG:
	 *            Full delegation is performed (credentials can be further
	 *            delegated)
	 *            </ul>
	 * @param contextLifetime
	 *            the lifetime of the GSI Secure Conversation context (in
	 *            seconds)
	 */
	public static void setSecurity(Stub stub, boolean encryption,
			GSSCredential credentials, String delegation, int contextLifetime) {

		if (stub == null) {
			throw new NullPointerException("Stub cannot be null to set security");
		}
		if (credentials == null) {
			throw new NullPointerException("Credentials cannot be null to set security");
		}
		if (delegation == null) {
			throw new NullPointerException("Delegation cannot be null to set security");
		}
		
		String name = null;
		try {
			name = credentials.getName().toString();
		} catch (Exception e) {
			logger.error("Exception retrieving credentials name", e);
			throw new IllegalArgumentException("Cannot retrieve name from credentials");
		}
		
		logger.debug("Set security on stub " + stub.getClass().getSimpleName()
				+ "[" + stub.toString() + "] with arguments: encryption("
				+ encryption + "), credentials(" + name
				+ "), delegation(" + delegation + "), contextLifetime(" + contextLifetime + ")");

		// credentials
		stub._setProperty(GSIConstants.GSI_CREDENTIALS, credentials);

		// Authentication method
		if (encryption) {
			stub._setProperty(Constants.GSI_SEC_CONV, Constants.ENCRYPTION);
		} else {
			stub._setProperty(Constants.GSI_SEC_CONV, Constants.SIGNATURE);
		}

		// delegation
		stub._setProperty(GSIConstants.GSI_MODE, delegation);

		// set Context lifetime
		stub._setProperty(Constants.CONTEXT_LIFETIME, contextLifetime);
	}
}