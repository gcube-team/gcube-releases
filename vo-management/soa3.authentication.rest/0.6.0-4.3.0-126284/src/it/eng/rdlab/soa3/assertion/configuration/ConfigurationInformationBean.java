package it.eng.rdlab.soa3.assertion.configuration;

import java.util.Properties;

/**
 * 
 * Configuration Bean
 * 
 * @author Ciro Formisano
 *
 */
public class ConfigurationInformationBean implements ConfigurationBean {

	private Properties props;
	
	public ConfigurationInformationBean () 
	{
		this.props = new Properties();
	}
	
	/**
	 * 
	 * Sets the property
	 * 
	 * @param name
	 * @param value
	 */
	public void setProperty (String name, String value)
	{
		this.props.setProperty(name, value);
	}
	
	/** {@inheritDoc}*/
	@Override
	public String getProperty(String propertyname)
	{
		return this.props.get(propertyname).toString().trim();
	}

}
