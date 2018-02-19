package org.gcube.application.framework.core.util;

import java.util.HashMap;

/**
 * @author Valia Tsagkalidou (KNUA)
 *
 */
public class QueryString extends HashMap<String, String>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryString() {
		super();
	}

	public void addParameter(String name, String value)
	{
		this.put(name, value);
	}
	
	public void removeParameter(String name)
	{
		this.remove(name);
	}
	
}
