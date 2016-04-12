package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.ApplicationHandlers;
import org.gcube.smartgears.configuration.application.DefaultApplicationConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.extensions.ApplicationExtension;
import org.gcube.smartgears.handlers.application.ApplicationHandler;


public class TestUtils {

	
	public static String location = "target/ghn-home";
	public static String context_root = "test-app";
	public static String context_root_path = "/" + context_root;
	public static String servlet_name = "test";
	public static String scope = "/gcube/devsec";

	public static class Box<T> {
		
		T t;
		
		public void put(T t) {
			this.t=t;
		}
		
		public T get() {
			return t;
		}
		
	}
	
	/**
	 * Serialises a {@link ContainerConfiguration} to XML in a file.
	 * 
	 * @param config the configuration
	 * @param the file
	 * @return the serialisation
	 * @throws RuntimeException if the configuration cannot be serialised
	 */
	public static void serialise(ContainerConfiguration config, File file) {
		
		//serialises configuration
		
		try {
			JAXBContext ctx = JAXBContext.newInstance(ContainerConfiguration.class);
	
			FileWriter writer = new FileWriter(file);
			
			ctx.createMarshaller().marshal(config, writer);
			
			writer.flush();
			writer.close();
		
		} catch (Exception e) {
			
			throw new RuntimeException("invalid service configuration", e);
		}		

	}
	/**
	 * Serialises a {@link ApplicationConfiguration} to XML.
	 * 
	 * @param config the configuration
	 * @return the serialisation
	 * @throws RuntimeException if the configuration cannot be serialised
	 */
	public static String bind(ApplicationConfiguration config) {

		try {

			//collect handler classes
			List<Class<?>> classes = new ArrayList<Class<?>>();
			
			classes.add(DefaultApplicationConfiguration.class);
			if (config.persistence()!=null)
				classes.add(config.persistence().getClass());
			
			//serialises configuration
			
			JAXBContext ctx = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

			StringWriter writer = new StringWriter();

			ctx.createMarshaller().marshal(config, writer);

			return writer.toString();

		
		} catch (JAXBException e) {
			
			throw new RuntimeException("invalid application configuration", e);
			
		}
	}
	
	/**
	 * Serialises application handlers.
	 * 
	 * @param handlers the handlers
	 * @return the serialisation
	 * @throws RuntimeException if the handlers cannot be serialised
	 */
	public static String bind(ApplicationHandlers handlers) {

		try {

			//collect handler classes
			List<Class<?>> classes = new ArrayList<Class<?>>();
			
			classes.add(ApplicationHandlers.class);

			for (ApplicationHandler<?> h : handlers.lifecycleHandlers())
				classes.add(h.getClass());

			for (ApplicationHandler<?> h : handlers.requestHandlers())
				classes.add(h.getClass());

			
			//serialises configuration
			
			JAXBContext ctx = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

			StringWriter writer = new StringWriter();

			ctx.createMarshaller().marshal(handlers, writer);

			return writer.toString();

		} catch (JAXBException e) {
			
			throw new RuntimeException("invalid handler configuration", e);
			
		}
	}
	
	/**
	 * Serialises application extensions.
	 * 
	 * @param extensions the extensions
	 * @return the serialisation
	 * @throws RuntimeException if the extensions cannot be serialised
	 */
	public static String bind(ApplicationExtensions extensions) {

		try {

			//collect handler classes
			List<Class<?>> classes = new ArrayList<Class<?>>();
			
			classes.add(ApplicationExtensions.class);

			for (ApplicationExtension h : extensions.extensions())
				classes.add(h.getClass());

			
			//serialises configuration
			
			JAXBContext ctx = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));

			StringWriter writer = new StringWriter();

			ctx.createMarshaller().marshal(extensions, writer);

			return writer.toString();

		} catch (JAXBException e) {
			
			throw new RuntimeException("invalid handler configuration", e);
			
		}
	}
	
}
