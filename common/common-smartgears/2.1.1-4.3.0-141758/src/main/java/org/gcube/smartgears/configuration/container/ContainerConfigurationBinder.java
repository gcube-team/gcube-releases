package org.gcube.smartgears.configuration.container;

import static org.gcube.smartgears.utils.Utils.*;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.smartgears.handlers.container.ContainerHandler;
import org.gcube.smartgears.utils.Utils;

/**
 * Binds {@link ContainerConfiguration}s to and from XML serialisations.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ContainerConfigurationBinder {

	/**
	 * Returns a {@link ContainerConfiguration} from its XML serialisation.
	 * 
	 * @param stream the serialisation
	 * @return the configuration
	 * @throws RuntimeException if the serialisation is invalid
	 */
	public ContainerConfiguration bind(InputStream stream) {

		try {

			JAXBContext ctx = JAXBContext.newInstance(ContainerConfiguration.class);

			ContainerConfiguration config = (ContainerConfiguration) ctx.createUnmarshaller().unmarshal(stream);
			
			return config;

		} catch (JAXBException e) {

			throw new RuntimeException("invalid container configuration", e);

		}
		finally {
			
			Utils.closeSafely(stream);
		}
	}
	
	/**
	 * Returns the handlers of the container from their XML serialisation.
	 * 
	 * @param stream the serialisation
	 * @return the handlers
	 * @throws RuntimeException if the serialisation is invalid
	 */
	public ContainerHandlers bindHandlers(InputStream stream) {

		//collects  handler classes
		Set<Class<?>> classes = scanForConfigurationElements();

		try {

			JAXBContext ctx = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

			return (ContainerHandlers) ctx.createUnmarshaller().unmarshal(stream);

		} catch (JAXBException e) {

			throw unchecked(e);

		}
	}
	
	
	
	private Set<Class<?>> scanForConfigurationElements() throws RuntimeException {

		@SuppressWarnings("all")
		ServiceLoader<ContainerHandler> handlerLoader = (ServiceLoader) ServiceLoader.load(ContainerHandler.class);

		Set<Class<?>> scanned = new HashSet<Class<?>>();

		for (ContainerHandler handler : handlerLoader) {
			Class<?> handlerClass = handler.getClass();
			if (handlerClass.isInterface() || handlerClass.getModifiers() == Modifier.ABSTRACT)
				continue;
			else
				scanned.add(handlerClass);
		}

		//add top-level configuration
		scanned.add(ContainerHandlers.class);

		return scanned;
	}
		
}
