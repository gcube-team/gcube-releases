package it.rdlab.asserts.soa3.validate;

import it.rdlab.soa3.asserts.configuration.ConfigurationBean;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.ConfigurationException;

public interface IAssertionValidator {

	public abstract void configure(ConfigurationBean configuration);

	/**
	 * This method is invoked by the clients/stubs to validate the signature of
	 * assertions using the public key of the signed entity read from a file, returns null if signature is not valid
	 * 
	 * @param xml SAML assertion in String 
	 * @return Assertion object if signature is valid or null if the signature is invalid
	 * @throws AssertionValidationException in case of any exceptions
	 * @throws ConfigurationException if an invalid configuration has been inserted
	 */
	public abstract Assertion validateAssertions(String xml)
			throws AssertionValidationException, ConfigurationException;

	/**
	 * 
	 * Generates an assertion object from a string
	 * 
	 * @param assertionString
	 * @return an assertion object if the generation process has been correctly completed, null otherwise
	 */
	public abstract Assertion getAssertionObject(String assertionString);

	/**
	 * 
	 * Validates the lifetime of the assertion basing on the notBefore and notOnOrAfter properties
	 * 
	 * @param assertion
	 * @return true if the validation is OK, false otherwise 
	 */
	public abstract boolean validateTimeInterval(Assertion assertion);

	/**
	 * 
	 * Validates the signature of the assertion
	 * 
	 * @param assertion the assertion
	 * @return true if the signature is valid, false otherwise
	 * @throws ConfigurationException
	 */
	public abstract boolean validateSignature(Assertion assertion)
			throws ConfigurationException;

}