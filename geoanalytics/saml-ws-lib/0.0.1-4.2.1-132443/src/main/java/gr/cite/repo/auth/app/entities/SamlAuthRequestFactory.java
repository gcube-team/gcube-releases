package gr.cite.repo.auth.app.entities;

import gr.cite.repo.auth.saml.messages.SamlAuthRequest;

public class SamlAuthRequestFactory {

	public SamlAuthRequest create(String issuer,
			String assertionConsumerServiceUrl) {
		return new SamlAuthRequest(issuer, assertionConsumerServiceUrl);
	}

}