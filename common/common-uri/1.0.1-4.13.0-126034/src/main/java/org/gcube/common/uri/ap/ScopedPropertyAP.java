package org.gcube.common.uri.ap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A {@link ScopedAuthorityProvider} that provides authorities configured in a
 * {@link #AUHTORITY_PROVIDER_PROPERTY_FILE}.
 * <p>
 * The file must be available as a top-level classpath resource.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ScopedPropertyAP implements ScopedAuthorityProvider {

	/** The name of the authorities configuration file */
	public static final String AUHTORITY_PROVIDER_PROPERTY_FILE = "org.gcube.common.uri.authorities.properties";

	private Properties properties;

	/**
	 * Creates an instance.
	 */
	public ScopedPropertyAP() {

		InputStream propertyFile = getClass().getResourceAsStream("/" + AUHTORITY_PROVIDER_PROPERTY_FILE);

		if (propertyFile == null)
			throw new IllegalStateException("cannot find property file " + AUHTORITY_PROVIDER_PROPERTY_FILE);

		properties = new Properties();

		try {
			properties.load(propertyFile);
		} catch (IOException e) {
			throw new IllegalStateException("cannot access property file " + AUHTORITY_PROVIDER_PROPERTY_FILE);
		}
	}

	@Override
	public String authorityIn(String scope) {
		return properties.getProperty(scope);
	}
}
