package gr.cite.repo.auth.app.entities;

import gr.cite.repo.auth.saml.messages.SamlLogoutResponse;

import java.io.UnsupportedEncodingException;

import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;

public class SamlLogoutResponseFactory {

	public SamlLogoutResponse create(byte[] privateKey, String samlResponseString)
			throws UnsupportedEncodingException, ConfigurationException,
			XMLParserException, UnmarshallingException {
		return new SamlLogoutResponse(privateKey, samlResponseString);
	}

}