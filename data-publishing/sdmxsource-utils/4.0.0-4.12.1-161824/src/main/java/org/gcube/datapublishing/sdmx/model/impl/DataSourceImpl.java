package org.gcube.datapublishing.sdmx.model.impl;

import org.gcube.datapublishing.sdmx.model.DataSource;

public class DataSourceImpl implements DataSource{

	private String 	endpoint,
					name;
	
	public DataSourceImpl (String name, String endpoint)
	{
		this.name = name;
		this.endpoint = endpoint;
	}
	
	@Override
	public String getEndpoint() {
		return endpoint;
	}


	@Override
	public String getName ()
	{
		return this.name;
	}
	
	
	
	@Override
	public String toString() {
		return "Data Source name "+this.name+ ", endpoint"+this.endpoint;
		
	}
	
}
