package gr.cite.repo.auth.webapp.inject.modules;

import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.config.Security;
import gr.cite.repo.auth.app.entities.SamlResourceFactory;
import gr.cite.repo.auth.app.resources.SAMLResource;
import gr.cite.repo.auth.webapp.inject.providers.SecurityFilterWrapper;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class SamlSecureServletModule extends JerseyServletModule {

	protected final SamlSecurityConfiguration configuration;

	public SamlSecureServletModule(SamlSecurityConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	protected void configureServlets() {
		serve("/*").with(GuiceContainer.class);

		applyFilter(new SecurityFilterWrapper(configuration));
		
		//is added by the SamlSecureServletConfig default module
		//bind(LocationResolver.class).to(UrlLocationResolver.class);
		
		bind(SamlResourceFactory.class).in(Scopes.SINGLETON);
		bind(ViewMessageBodyWriterProvider.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	SAMLResource samlResourceProvider(SamlResourceFactory samlResourceFactory){
		Security securityConfiguration = configuration.getSecurity();
		
		Preconditions.checkNotNull(securityConfiguration);
		Preconditions.checkNotNull(samlResourceFactory);

		SAMLResource samlResource = null;
		try {
			samlResource = samlResourceFactory.create(
					securityConfiguration.getSpHost(),
					securityConfiguration.getIdpMetadataLocation(),
					securityConfiguration.getPrivateKeyFilename(),
					securityConfiguration.getCertificateFilename(),
					securityConfiguration.getInvalidateLocalSessionOnSamlError(),
					securityConfiguration.getTryRenewSessionOnLogout(),
					securityConfiguration.getBulkLogout());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return samlResource;
	}

	void applyFilter(SecurityFilterWrapper filterProvider) {
		List<String> urls = filterProvider.getProtectedUrls();

		String first = urls.get(0);
		List<String> rest = urls.size() > 1 ? urls.subList(1, urls.size())
				: new ArrayList<String>();

		filter(first, rest.toArray(new String[] {})).through(
				filterProvider.getFilter());
	}
}
