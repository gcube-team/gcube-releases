package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies;

import java.util.HashMap;
import java.util.Map;

public class AgenciesMap extends HashMap<String, AgenciesList> implements Map<String, AgenciesList> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -910945058442245785L;

	public AgenciesMap ()
	{
		super ();
	}

	public AgenciesMap (int initialCapability)
	{
		super (initialCapability);
	}
	
	public AgenciesMap (int initialCapability,float loadFactor)
	{
		super (initialCapability,loadFactor);
	}

	public AgenciesMap (AgenciesMap agenciesMap)
	{
		super (agenciesMap);
	}
	
	@Override
	public AgenciesList put(String key, AgenciesList value) {
		
		if (value != null && value.isExpired()) throw new IllegalArgumentException("Expired value");
		
		return super.put(key, value);
	}
	
	@Override
	public AgenciesList get(Object key) {
		AgenciesList response = super.get(key);
		
		if (response != null && response.isExpired())
		{
			super.remove(key);
			response = null;
		}
		
		return response;
	}
}
