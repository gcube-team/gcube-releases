package it.eng.rdlab.soa3.config;

import it.eng.rdlab.soa3.authn.rest.AuthenticationContext;
import it.eng.rdlab.soa3.authn.rest.impl.LDAPAuthenticationContext;
import it.eng.rdlab.soa3.config.beans.ConfigurationFileBean;
import it.eng.rdlab.soa3.config.beans.DefaultValues;
import it.eng.rdlab.soa3.config.beans.PropertiesNameBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ConfigurationManager 
{
	private Properties properties;
	private Log log;
	private static ConfigurationManager instance;
	private PropertiesNameBean labels;
	private AuthenticationContext authenticationContext;
	private DefaultValues defaults;
	
//	private final String 	LDAP_URL = "LDAP_URL",
//							LDAP_BASE = "LDAP_BASE",
//							LDAP_DN = "LDAP_USER_DN",
//							LDAP_PWD = "LDAP_PASSWORD",
//							CA_CERT = "CA_CERT",
//							ASSERTION_SIGNATURE_VALIDATION = "ASSERTION_SIGNATURE_VALIDATION",
//							ASSERTION_TIME_VALIDATION = "ASSERTION_TIME_VALIDATION",
//							SAML_ASSERTION_SOURCE_URL = "SAML_ASSERTION_SOURCE_URL";
	
	private ConfigurationManager ()
	{
		this.log = LogFactory.getLog(this.getClass());
		Resource resource = new ClassPathResource("it/eng/rdlab/soa3/config/beans.xml");
		log.debug("Using the config from file " + resource.getFilename());
		BeanFactory factory = new XmlBeanFactory(resource);
		this.labels = (PropertiesNameBean) factory.getBean("labels");
		ConfigurationFileBean configurationFileBean = (ConfigurationFileBean) factory.getBean("configurationFile");
		this.defaults = (DefaultValues) factory.getBean("defaults");
		this.properties = new Properties();
		InputStream stream = configurationFileBean.getConfigurationStream();
		
		if (stream == null)
		{
			this.log.error("Unable to find a configuration file: the application won't work");
		}
		else
		{
			try {
				this.properties.load(stream);
			} catch (IOException e) {
				this.log.error("Unable to load the configuration file: the application won't work",e);
			}
			
		}
		
		this.authenticationContext = configureLdap(factory);

	}
	
	
	private AuthenticationContext configureLdap(BeanFactory factory) 
	{
		this.log.debug("Configuring authentication context");
		LDAPAuthenticationContext authenticationContext = (LDAPAuthenticationContext) factory.getBean("authenticationContext");
		authenticationContext.setUrl(this.properties.getProperty(this.labels.getLdapUrl(),authenticationContext.getUrl()));
		authenticationContext.setBase(this.properties.getProperty(this.labels.getLdapBase(),authenticationContext.getBase()));
		authenticationContext.setUserDn(this.properties.getProperty(this.labels.getLdapDN(),authenticationContext.getUserDn()));
		authenticationContext.setPassword(this.properties.getProperty(this.labels.getLdapPwd(),authenticationContext.getPassword()));
		this.log.debug(" configured context source and ldap template..");
		return authenticationContext;

	}
	
	
	public static ConfigurationManager getInstance ()
	{
		if (instance == null) instance = new ConfigurationManager();
		
		return instance;
	}
	
	public AuthenticationContext getAuthenticationContext ()
	{
		return this.authenticationContext;
	}
	
	
	public String getCaCert ()
	{
		return this.properties.getProperty(this.labels.getCaCert(),defaults.getCaCert());
		
	}
	
	public String getAssertionSignatureValidation ()
	{
		return this.properties.getProperty(this.labels.getAssertionSignatureValidation(),defaults.getAssertionSignatureValidation());
		
	}
	
	public String getAssertionTimeValidation ()
	{
		return this.properties.getProperty(this.labels.getAssertionTimeValidation(),defaults.getAssertionTimeValidation());
		
	}
	
	public String getAssertionServiceUrl ()
	{
		return this.properties.getProperty(this.labels.getAssertionSourceUrl(),defaults.getAssertionSourceUrl());
		
	}

}
