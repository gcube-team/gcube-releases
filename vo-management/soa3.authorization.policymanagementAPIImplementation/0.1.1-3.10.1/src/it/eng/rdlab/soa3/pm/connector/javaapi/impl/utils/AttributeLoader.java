package it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeLoader 
{
	private Logger logger;
	private Properties properties;
	public String DEFAULT_ATTRIBUTE_FILE = "/it/eng/rdlab/soa3/pm/connector/service/resources/attributes.properties";
	private Map<String, String> valueKeyMap;
	private static AttributeLoader instance;
	
	public static AttributeLoader getInstance ()
	{
		if (instance == null) instance = new AttributeLoader();
		
		return instance;
	}
	
	private AttributeLoader() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.properties = new Properties();
		this.valueKeyMap = new HashMap<String, String> ();
		
		try 
		{
			this.properties.load(this.getClass().getResourceAsStream(DEFAULT_ATTRIBUTE_FILE));
		} 
		catch (IOException e) 
		{
			logger.error("Unable to load the attributes file",e);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public String getAttribute (String id)
	{
		String value = this.properties.getProperty(id,id);
		logger.debug("ID = "+id+ " value "+value);
		
		if (!this.valueKeyMap.containsKey(value)) this.valueKeyMap.put(value, id);
		
		return value;
		
	}
	
	
	public String getAttributeId (String value)
	{
		String id = this.valueKeyMap.get(value);
		
		if (id == null)
		{
			id = findId(value);
			this.valueKeyMap.put(value, id);
		}
		
		return id;
	}
	
	private String findId (String value)
	{
		
		String response = null;
		Iterator<Entry<Object, Object>> entries = this.properties.entrySet().iterator();
		
		while (entries.hasNext() && response == null)
		{
			Entry<Object, Object> entry = entries.next();
			String entryValue = (String) entry.getValue();
			logger.debug("Value found "+entryValue);
			
			if (entryValue.equals(value))
			{
				response = (String) entry.getKey();
				logger.debug("Key found "+response);
			}
			else logger.debug("Key not found");
			
		}
		
		if (response == null) response = value;
		
		logger.debug("Response = "+response);
		return response;

	}

}
