package it.eng.rdlab.soa3.um.rest.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationManager 
{
	//private final String catalina_home = "catalina.home";
	private Properties properties;
	private Log log;
	private static ConfigurationManager instance;
	
	private final String 	CONFIGURATION_ROOT = "CONFIGURATION_ROOT",
							CONFIGURATION_FILE= "CONFIGURATION_FILE";
	
	private final String 	LDAP_URL = "LDAP_URL",
							LDAP_BASE = "LDAP_BASE",
							LDAP_DN = "LDAP_USER_DN",
							LDAP_PWD = "LDAP_PASSWORD",
							LDAP_DUMMY_ROOT = "LDAP_DUMMY_ROOT";
	
	private final String	DEFAULT_PROP_FILE = "/it/eng/rdlab/soa3/um/rest/conf/configuration.properties",
							PROP_FILES = "/it/eng/rdlab/soa3/um/rest/conf/configurationfiles.properties";
	
	private ConfigurationManager ()
	{
		this.log = LogFactory.getLog(this.getClass());
		Properties files = getConfigurationFiles();
		String root = files.getProperty(CONFIGURATION_ROOT,"");
		String path = files.getProperty(CONFIGURATION_FILE,"");
		
		if (root != null && root.startsWith("$"))
		{
			root = System.getProperty(root.substring(1));
			log.debug("Actual root "+root);
		}
	
		path = root+path;
		log.debug("Configuration path = "+path);
		
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(path));
		} catch (Exception e) 
		{
			this.log.warn("Configuration file not found",e);
			this.log.warn("Using default values");
			
			try {
				this.properties.load(this.getClass().getResourceAsStream(DEFAULT_PROP_FILE));
			} catch (IOException e1) 
			{
				this.log.error("Unable to find properties from the classpath",e);
			}

		} 
	}
	
	public static ConfigurationManager getInstance ()
	{
		if (instance == null) instance = new ConfigurationManager();
		
		return instance;
	}
	
	private Properties getConfigurationFiles ()
	{
		Properties fileProperties = new Properties();
		try 
		{
			fileProperties.load(this.getClass().getResourceAsStream(PROP_FILES));
		} catch (IOException e1) 
		{
			this.log.error("Unable to find properties from the classpath",e1);
			
		}
		
		return fileProperties;
	}
	
	public String getLdapUrl ()
	{
		return this.properties.getProperty(LDAP_URL);
	}
	
	public String getLdapBase ()
	{
		return this.properties.getProperty(LDAP_BASE);
	}
	
	public String getLdapUserDN ()
	{
		return this.properties.getProperty(LDAP_DN);
	}
	
	public String getLdapPwd ()
	{
		return this.properties.getProperty(LDAP_PWD);
	}
	
	public String getLdapDummyRoot ()
	{
		return this.properties.getProperty(LDAP_DUMMY_ROOT);
	}
	

}
