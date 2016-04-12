package it.rdlab.soa3.asserts.configuration;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;

/**
 * 
 * Configurator class
 * 
 * @author Ciro Formisano
 *
 */
public class ConfigurationManager extends ConfigurationInformationBean
{
	
	private final String CLASSPATH_PROPERTIES = "/it/rdlab/asserts/validate/assertsvalidation.properties";
	private Properties props;
	private static Logger log = Logger.getLogger(ConfigurationManager.class);
	
	private static ConfigurationManager instance;
	
	private ConfigurationManager () 
	{
		super ();
	}
	
	private void init ()  throws IOException, ResourceException
	{
		this.props.load(new ClasspathResource(CLASSPATH_PROPERTIES).getInputStream());
	}

	public static final ConfigurationManager getInstance ()
	{
		if (instance == null) 
		{
			log.debug("Generating new instance..");
			
			try
			{
				instance = new ConfigurationManager();
				instance.init();
			} 
			catch (Exception e)
			{
				log.error("Unable to complete the instance creation",e);
				
			}
		}
		
		return instance;
	}

}
