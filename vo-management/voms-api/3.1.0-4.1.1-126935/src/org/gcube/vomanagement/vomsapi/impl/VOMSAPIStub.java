package org.gcube.vomanagement.vomsapi.impl;

import java.rmi.RemoteException;

import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.util.CredentialsUtil;
import org.glite.security.voms.FQAN;
import org.globus.axis.gsi.GSIConstants;
import org.globus.wsrf.impl.security.authentication.Constants;

import sun.security.validator.ValidatorException;

/**
 * The {@link VOMSAPIStub} is the root class of VOMS API stubs. Concrete
 * implementation of {@link VOMSAPIStub} are used to interact with MyProxy and
 * VOMS services. COncrete implementation of {@link VOMSAPIStub} can be obtained
 * usign a {@link VOMSAPIFactory} object.
 */
abstract class VOMSAPIStub {

	private static final String ROLE_PREFIX = "Role=";

	private VOMSAPIFactory factory;

	/**
	 * Constructor.
	 * 
	 * @param factory
	 *            the factory that creates this {@link VOMSAPIStub} object.
	 */
	VOMSAPIStub(VOMSAPIFactory factory) {
		super();
		this.factory = factory;
	}

	/**
	 * This method return the {@link VOMSAPIFactory} that created this
	 * {@link VOMSAPIStub} object.
	 */
	VOMSAPIFactory getFactory() {
		return factory;
	}

	/**
	 * This method prepares a {@link VOMSAPIStub} object for an Axis call. This
	 * is only needed if the VOMS-API library is being used in a Ws-Core
	 * container, thus this method is called, when needed, by the method
	 * {@link VOMSAPIFactory}.prepareForCall().
	 */
	abstract void configureVOMSAPIStubForCall();

	/**
	 * Configure security in a {@link Stub} object.
	 * 
	 * @param stub
	 *            the {@link Stub} object to configure.
	 */
	void configureSecurity(Stub stub) {

		// set credentials
		stub._setProperty(GSIConstants.GSI_CREDENTIALS, this.factory
				.getVOMSAPIConfiguration().getCredentials());

		// set authentication method
		stub._setProperty(Constants.GSI_TRANSPORT, Constants.ENCRYPTION);

	}

	/**
	 * Get the Certification Authority DN from the
	 * {@link VOMSAPIConfigurationProperty}.SIMPLE_CA field.
	 */
	String getCA() {
		return this.factory.getVOMSAPIConfiguration().getProperty(
			VOMSAPIConfigurationProperty.SIMPLE_CA);
	}

	/**
	 * Compose and return the user Distinguished Name starting from its Common
	 * Name and the DN prefix in the {@link VOMSAPIConfigurationProperty}.CN_PREFIX
	 */
	String getDN(String userName) {
		return this.factory.getVOMSAPIConfiguration().getProperty(
			VOMSAPIConfigurationProperty.CN_PREFIX)
				+ userName;
	}

	/**
	 * <p>
	 * This method return the role name contained in a FQAN string. Given, for
	 * intance, the following FQAN:<br>
	 * <code>"/testVO/testGroup/Role=testRole"</code><br> will cause the
	 * method to return the {@link String}<br> <code>"testRole"</code>.
	 * </p>
	 * 
	 * @param fqanString
	 *            the FQAN string to parse
	 * 
	 * @return the role name from the FQAN passed, if any, if no role is found,
	 *         a {@link NullPointerException} is thrown.
	 */
	String getRoleFromFQAN(String fqanString) {

		FQAN fqan = new FQAN(fqanString);

		String role = fqan.getRole();

		if (role == null || "".equals(role)) {
			throw new NullPointerException("The FQAN string \"" + fqanString
					+ "\" does not contains any role");
		} else {
			return role;
		}

	}

	/**
	 * <p>
	 * This method return the group name contained in a FQAN string. Given, for
	 * intance, the following FQAN:<br>
	 * <code>"/testVO/testGroup/Role=testRole"</code><br> will cause the
	 * method to return the {@link String}<br>
	 * <code>"/testVO/testGroup"</code>.
	 * </p>
	 * 
	 * @param fqanString
	 *            the FQAN to parse
	 * 
	 * @return the group name from the FQAN passed, if any, if no group is
	 *         found, a {@link NullPointerException} is thrown.
	 */
	String getGroupFromFQAN(String fqanString) {

		FQAN fqan = new FQAN(fqanString);

		String group = fqan.getGroup();

		if (group == null || "".equals(group)) {
			throw new NullPointerException("The FQAN string \"" + fqanString
					+ "\" does not contains any group");
		} else {
			return group;
		}

	}

	/**
	 * Discard the "Role=" prefix from roles arrays get from the VOMS.
	 * 
	 * @param roles
	 *            the role array
	 * 
	 * @return a new array with roles, cleaned from the "Role=" prefix. Elements
	 *         not starting with the Role= prefix will be returned unchanged.
	 */
	String[] discardRolePrefix(String[] roles) {

		if (roles == null)
			throw new NullPointerException("The array of roles cannot be null");

		String[] cleanedRoles = new String[roles.length];

		for (int i = 0; i < roles.length; i++) {
			cleanedRoles[i] = discardRolePrefix(roles[i]);
		}

		return cleanedRoles;
	}

	/**
	 * Discard the "Role=" prefix from a role get from the VOMS.
	 * 
	 * @param role
	 *            the role string with "Role=" prefix
	 * 
	 * @return a new {@link String} with the role name, cleaned from the "Role="
	 *         prefix. If the given {@link String} does not start with the Role=
	 *         prefix, it will be returned unchanged.
	 */
	String discardRolePrefix(String role) {

		if (role == null) {
			throw new NullPointerException("The role cannot be null");
		}

		return role.startsWith(VOMSAPIStub.ROLE_PREFIX) ? role.substring(5)
				: role;
	}

	/**
	 * Add the "Role=" prefix to a VOMS role.
	 * 
	 * @param role
	 *            the role string without the "Role=" prefix
	 * 
	 * @return a new {@link String} with the role name, starting with the
	 *         "Role=" prefix. If the given {@link String} already starts with
	 *         the Role= prefix, it will be returned unchanged.
	 */
	String addRolePrefix(String role) {

		if (role == null) {
			throw new NullPointerException("The role cannot be null");
		}

		return role.startsWith(VOMSAPIStub.ROLE_PREFIX) ? role : "Role=" + role;
	}

	/**
	 * This method handle known configuration exceptions logging hints about how
	 * to solve known problems
	 * 
	 * @param e
	 *            the exception
	 * 
	 * @param logger
	 *            the logger where to log information
	 */
	void handleException(RemoteException e, Logger logger) {

		// PRIX path building failed in SSL handshake
		if ((e.detail != null)
				&& (e.detail.getCause() != null)
				&& e.detail.getCause().getClass().equals(
					ValidatorException.class)) {

			logger.debug(e);

			// verify that credentials have been properly initialized
			if (factory.getVOMSAPIConfiguration().getCredentials() == null) {
				logger
						.error(">>> Cannot find credentials to contact VOMS. " +
										"Please configure credentials in the VOMSAPIConfiguration object");
			} else if (CredentialsUtil.isExpired(factory
					.getVOMSAPIConfiguration().getCredentials())) {
				logger.error(">>> "
						+ CredentialsUtil.stringCredentials(factory
								.getVOMSAPIConfiguration().getCredentials())
						+ " are expired!");
			}

			// log message about wrong configuration of VOMS-API
			logger
					.error(">>> If the voms-api library is running from a standalone client, "
							+ "the value of the configuration property "
							+ VOMSAPIConfigurationProperty.RUNS_IN_WS_CORE
									.toString()
							+ " in the configuration file should be set to false (current value is "
							+ getFactory().getVOMSAPIConfiguration()
									.runsInWSCore() + ")");

		}
	}
}
