package gr.cite.repo.auth.app;


import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.entities.SampleSamlApp;

import org.junit.ClassRule;

public class SamlAppSimpleSessionTest extends SamlAppSessionTest {

	
	@ClassRule
	public static final DropwizardSecureAppRule<SamlSecurityConfiguration> RULE = new DropwizardSecureAppRule<SamlSecurityConfiguration>(
			SampleSamlApp.class, "src/test/resources/test-saml-config-simple-session.yaml");

	@Override
	public DropwizardSecureAppRule<SamlSecurityConfiguration> getRule() {
		return RULE;
	}

	
}
