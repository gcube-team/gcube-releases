package org.gcube.common.informationsystem.publisher.impl.context;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.gcube.common.core.contexts.GHNContext;

/**
 * 
 * Publisher local context
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ISPublisherContext {

    public static final String REGISTRY_CHANNEL_TIMEOUT_PROP_NAME = "REGISTRY_CHANNEL_TIMEOUT";
    
    public static final String COLLECTOR_CHANNEL_TIMEOUT_PROP_NAME = "COLLECTOR_CHANNEL_TIMEOUT";
    
    public static final String BULK_PUBLICATIONS_INTERVAL_PROP_NAME = "BULK_PUBLICATIONS_INTERVAL";

    public static final String MAX_PARALLEL_REGISTRATIONS_PROP_NAME = "MAX_PARALLEL_REGISTRATIONS";

    public static final String RESOURCE_PUBLICATION_MAX_ATTEMPTS_PROP_NAME = "RESOURCE_PUBLICATION_MAX_ATTEMPTS";

    /** The resource which contains the current configuration properties */
    private static final String PROPERTIES_RESOURCE = "ISPublisher.properties";

    //private static GCUBELog logger = new GCUBELog(ISPublisherContext.class);

    /** Properties set with the resource */
    private static Properties properties = new Properties();

    private static ISPublisherContext context = null;

    private ISPublisherContext() throws IOException {
	try {
	    // try to load the properties from the $GLOBUS_LOCATION/config folder	   
	    properties.load(new FileInputStream(GHNContext.getContext().getFile(PROPERTIES_RESOURCE)));
	} catch (Exception e) {
	    // otherwise, try to load it from the classpath
	    properties.load(this.getClass().getResourceAsStream(PROPERTIES_RESOURCE));
	}

    }

    /**
     * Gets the ISPublisher context
     * 
     * @return the context
     * @throws IOException
     *             if an error occurs when reading the configuration
     */
    public static ISPublisherContext getContext() throws IOException {
	if (context == null) {
	    context = new ISPublisherContext();
	}
	return context;
    }

    /**
     * Gets a ISPublisher property
     * 
     * @param <T>
     *            the type of the property
     * @param name
     *            the property name
     * @param type
     *            the type of the property
     * @return the property value
     * @throws NoSuchProperty
     *             if the property does not exist
     */
    public Object getProperty(String name) throws NoSuchProperty {
	if (properties.get(name) == null)
	    throw new NoSuchProperty();
	return properties.get(name);
    }

    /** Property exception */
    static public class NoSuchProperty extends Exception {
	private static final long serialVersionUID = -6576162414295412552L;
    }

    public static void main(String[] args) {
	try {
	    String parallel = (String) ISPublisherContext.getContext().getProperty(ISPublisherContext.MAX_PARALLEL_REGISTRATIONS_PROP_NAME);
	    System.out.println("MAX_PARALLEL_REGISTRATIONS=" + parallel);
	    String timeout = (String) ISPublisherContext.getContext().getProperty(ISPublisherContext.REGISTRY_CHANNEL_TIMEOUT_PROP_NAME);
	    System.out.println("REGISTRY_CHANNEL_TIMEOUT=" + timeout);
	    String attempts = (String) ISPublisherContext.getContext().getProperty(ISPublisherContext.RESOURCE_PUBLICATION_MAX_ATTEMPTS_PROP_NAME);
	    System.out.println("RESOURCE_PUBLICATION_MAX_ATTEMPTS=" + attempts);
	    // String fault = (String) ISPublisherContext.getContext().getProperty("faultPROP");
	    // System.out.println("FAULT=" + fault);
	} catch (NoSuchProperty e) {
	    System.err.println(e);
	} catch (IOException e) {
	    System.err.println(e);
	}
    }
}
