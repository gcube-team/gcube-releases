package org.gcube.smartgears.configuration.library;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.utils.Utils;

/**
 * Binds {@link ContainerConfiguration}s to and from XML serialisations.
 * 
 * @author Fabio Simeoni
 * 
 */
public class SmartGearsConfigurationBinder {

	/**
	 * Returns a {@link ContainerConfiguration} from its XML serialisation.
	 * 
	 * @param stream the serialisation
	 * @return the configuration
	 * @throws RuntimeException if the serialisation is invalid
	 */
	public SmartGearsConfiguration bind(InputStream stream) {

		try {

			JAXBContext ctx = JAXBContext.newInstance(SmartGearsConfiguration.class);

			SmartGearsConfiguration config = (SmartGearsConfiguration) ctx.createUnmarshaller().unmarshal(stream);
			
			return config;

		} catch (JAXBException e) {

			throw new RuntimeException("invalid library configuration", e);

		}
		finally {
			
			Utils.closeSafely(stream);
		}
	}
		
}
