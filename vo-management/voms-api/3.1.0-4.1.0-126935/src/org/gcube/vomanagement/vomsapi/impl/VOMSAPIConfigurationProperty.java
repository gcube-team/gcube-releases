package org.gcube.vomanagement.vomsapi.impl;

/**
 * This is the set of properties used in the {@link VOMSAPIConfiguration} to
 * configure the VOMS API library.
 * 
 * @author Paolo Roccetti
 */
public enum VOMSAPIConfigurationProperty {

	/**
	 * The Distinguished Name of the Online CA.
	 */
	SIMPLE_CA(null),

	/**
	 * The prefix used to generate Distinguished Names for credentials issued by
	 * the Online CA.
	 */
	CN_PREFIX(null),

	/**
	 * The file containing proxy credentials used to perform invocations to
	 * MyProxy and VOMS.
	 */
	CLIENT_PROXY(null),

	/**
	 * The file containing the client public certificate used to contact the
	 * VOMS and MyProxy services.
	 */
	CLIENT_CERT(null),

	/**
	 * The file containing the client private key associated with the client
	 * certificate. If a CLIENT_CERT property has been specified this field is
	 * also required.
	 */
	CLIENT_KEY(null),

	/**
	 * The password to decrypt the key in the file pointed by the CLIENT_KEY
	 * property, if the key is encrypted.
	 */
	CLIENT_PWD(null),

	/**
	 * The hostname of the VOMS service
	 */
	VOMS_HOST(null),

	/**
	 * The VOMS VO name
	 */
	VO_NAME(null),

	/**
	 * The protocol used to contact the VOMS service, default to https
	 */
	VOMS_PROTOCOL("https"),

	/**
	 * The port used by the VOMS service, default to 443
	 */
	VOMS_PORT("443"),

	/**
	 * The hostname of the MyProxy service
	 */
	MYPROXY_HOST(null),

	/**
	 * The port of the MyProxy service, default to 7512
	 */
	MYPROXY_PORT("7512"),

	/**
	 * The directory where to temporary store proxy credentials, default to
	 * "proxies".
	 */
	PROXIES_DIR("proxies"),

	/**
	 * Indicates if the VOMS API library is being used from a Ws-Core serice, or
	 * from a standalone client. This parameter is needed as the way to perform
	 * invocation depends on this. Default of this parameter is false
	 */
	RUNS_IN_WS_CORE("false");

	private String defaultValue;

	private VOMSAPIConfigurationProperty(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * This method return the default value of the
	 * {@link VOMSAPIConfigurationProperty}
	 * 
	 * @return the default value of this {@link VOMSAPIConfigurationProperty},
	 *         null if a default value has not been defined for this property.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

}
