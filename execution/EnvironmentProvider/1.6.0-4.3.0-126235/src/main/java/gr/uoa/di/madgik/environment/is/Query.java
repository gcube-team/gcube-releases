package gr.uoa.di.madgik.environment.is;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;

import java.util.HashMap;
import java.util.Map;

public class Query 
{
	private Map<String, String> queryAttributes = new HashMap<String, String>();
	
	public Query() { }
	
	public Query(Map<String, String> attributes)
	{
		this.queryAttributes = queryAttributes;
	}
	
	public Query setAttribute(String key, String value) throws EnvironmentInformationSystemException
	{
		if(queryAttributes.containsKey(key)) 
			throw new EnvironmentInformationSystemException("Query attribute \"" + key + "\" already specified");
		this.queryAttributes.put(key, value);
		return this;
	}
	
	public Map<String, String> getQueryAttributes() {
		return queryAttributes;
	}
	
}
