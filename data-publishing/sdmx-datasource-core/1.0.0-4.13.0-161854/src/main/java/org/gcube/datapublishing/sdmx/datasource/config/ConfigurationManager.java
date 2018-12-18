package org.gcube.datapublishing.sdmx.datasource.config;

import java.util.List;

import org.gcube.datapublishing.sdmx.datasource.datatype.DataTypeManager;

public interface ConfigurationManager {

	public void init () throws DataSourceConfigurationException;
	
	public List<String> getExcludedQueryParameters ();
	
	public DataTypeManager getDataTypeManager ();
}
