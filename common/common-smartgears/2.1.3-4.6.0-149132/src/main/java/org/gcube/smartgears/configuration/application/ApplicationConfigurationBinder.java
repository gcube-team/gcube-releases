package org.gcube.smartgears.configuration.application;

import static org.gcube.smartgears.utils.Utils.*;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.smartgears.extensions.ApplicationExtension;
import org.gcube.smartgears.handlers.application.ApplicationHandler;

/**
 * Binds {@link ApplicationConfiguration}s to and from XML serialisations.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ApplicationConfigurationBinder {

	/**
	 * Returns the application configuration from its XML serialisation.
	 * 
	 * @param stream the serialisation
	 * @return the configuration
	 * @throws RuntimeException if the serialisation is invalid
	 */
	public ApplicationConfiguration bind(InputStream stream) {

		try {

			JAXBContext ctx = JAXBContext.newInstance(DefaultApplicationConfiguration.class);

			return (ApplicationConfiguration) ctx.createUnmarshaller().unmarshal(stream);

		} catch (JAXBException e) {

			throw new RuntimeException("invalid service configuration", e);

		}
		finally {
			closeSafely(stream);
		}
	}

	/**
	 * Returns the handlers of the application from their XML serialisation.
	 * 
	 * @param stream the serialisation
	 * @return the handlers
	 * @throws RuntimeException if the serialisation is invalid
	 */
	public ApplicationHandlers bindHandlers(InputStream stream) {

		//collects  handler classes
		Set<Class<?>> classes = scanForHandlers();

		try {

			JAXBContext ctx = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

			return (ApplicationHandlers) ctx.createUnmarshaller().unmarshal(stream);

		} catch (JAXBException e) {

			throw unchecked(e);

		}
		finally {
			closeSafely(stream);
		}
	}
	
	/**
	 * Returns the extensions of the application from their XML serialisation.
	 * 
	 * @param stream the serialisation
	 * @return the extensions
	 * @throws RuntimeException if the serialisation is invalid
	 */
	public ApplicationExtensions bindExtensions(InputStream stream) {

		//collects  handler classes
		Set<Class<?>> classes = scanForExtensions();

		try {

			JAXBContext ctx = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

			return (ApplicationExtensions) ctx.createUnmarshaller().unmarshal(stream);

		} catch (JAXBException e) {

			throw unchecked(e);

		}
		finally {
			closeSafely(stream);
		}
	}
	
	
	
	private Set<Class<?>> scanForHandlers() throws RuntimeException {

		@SuppressWarnings("all")
		ServiceLoader<ApplicationHandler> handlerLoader = (ServiceLoader) ServiceLoader.load(ApplicationHandler.class);

		Set<Class<?>> scanned = new HashSet<Class<?>>();

		for (ApplicationHandler<?> handler : handlerLoader) {
			Class<?> handlerClass = handler.getClass();
			if (handlerClass.isInterface() || handlerClass.getModifiers() == Modifier.ABSTRACT)
				continue;
			else
				scanned.add(handlerClass);
		}

		//add top-level configuration
		scanned.add(ApplicationHandlers.class);

		return scanned;
	}
	
	private Set<Class<?>> scanForExtensions() throws RuntimeException {

		@SuppressWarnings("all")
		ServiceLoader<ApplicationExtension> handlerLoader = (ServiceLoader) ServiceLoader.load(ApplicationExtension.class);

		Set<Class<?>> scanned = new HashSet<Class<?>>();

		for (ApplicationExtension handler : handlerLoader) {
			Class<?> handlerClass = handler.getClass();
			if (handlerClass.isInterface() || handlerClass.getModifiers() == Modifier.ABSTRACT)
				continue;
			else
				scanned.add(handlerClass);
		}

		//add top-level configuration
		scanned.add(ApplicationExtensions.class);

		return scanned;
	}
}
