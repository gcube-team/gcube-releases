package org.gcube.data.analysis.sdmx.converter;

public interface ColumnIdConverter {

	
	public String registry2Local (String registryColumnId);
	
	public String local2Registry (String localColumnId);
	
	public String getIdentificator ();
	
}
