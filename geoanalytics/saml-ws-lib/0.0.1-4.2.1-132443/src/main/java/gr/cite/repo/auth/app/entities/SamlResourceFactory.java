package gr.cite.repo.auth.app.entities;

import gr.cite.repo.auth.app.cookies.CookieFactory;
import gr.cite.repo.auth.app.resources.SAMLResource;
import gr.cite.repo.auth.app.utils.LocationResolver;

import java.io.IOException;

import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;

import com.google.inject.Inject;

public class SamlResourceFactory {
	private final SamlResponseFactory samlResponseFactory;
	private final SamlAuthRequestFactory samlAuthRequestFactory;
	private final CookieFactory cookieFactory;
	private final LocationResolver locationResolver;

	@Inject
	public SamlResourceFactory(SamlResponseFactory samlResponseFactory,
			SamlAuthRequestFactory samlAuthRequestFactory,
			LocationResolver locationResolver,
			CookieFactory cookieFactory) {
		super();
		this.samlResponseFactory = samlResponseFactory;
		this.samlAuthRequestFactory = samlAuthRequestFactory;
		this.locationResolver = locationResolver;
		this.cookieFactory = cookieFactory;
	}

	public SAMLResource create(String spHost, String idpMetadataLocation,
			String privateKeyFilename, String certificateFilename,
			Boolean invalidateLocalSessionOnSamlError,
			Boolean tryRenewSessionOnLogout, Boolean bulkLogout) throws IOException, ConfigurationException, XMLParserException, UnmarshallingException {
		SAMLResource samlResource = new SAMLResource(
				spHost, 
				idpMetadataLocation,
				privateKeyFilename, 
				certificateFilename, 
				samlResponseFactory,
				samlAuthRequestFactory, 
				locationResolver,
				cookieFactory,
				invalidateLocalSessionOnSamlError, 
				tryRenewSessionOnLogout, 
				bulkLogout);

		return samlResource;
	}

}