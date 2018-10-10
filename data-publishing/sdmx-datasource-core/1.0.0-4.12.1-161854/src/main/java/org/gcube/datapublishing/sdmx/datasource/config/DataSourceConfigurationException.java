package org.gcube.datapublishing.sdmx.datasource.config;

public class DataSourceConfigurationException extends Exception 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8560718507056143357L;

	public DataSourceConfigurationException() 
	{
		super ("Unable to configure the Data Source");
	}
	
	public DataSourceConfigurationException(String message) 
	{
		super ("Unable to configure the Data Source: "+message);
	}

	public DataSourceConfigurationException(Throwable cause) 
	{
		super ("Unable to configure the Data Source",cause);
	}

	public DataSourceConfigurationException(String message, Throwable cause) 
	{
		super ("Unable to configure the Data Source: "+message,cause);
	}

}
