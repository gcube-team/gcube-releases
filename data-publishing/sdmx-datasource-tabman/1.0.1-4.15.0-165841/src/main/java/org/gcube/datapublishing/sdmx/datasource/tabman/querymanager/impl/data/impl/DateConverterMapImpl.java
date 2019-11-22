package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DateConverter;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DateConverterMap;

public class DateConverterMapImpl implements DateConverterMap{

	private Map<String, DateConverter> dateConverters;
	private DateConverter defaultDateConverter;
	
	public void setDateConverters (Map<String, DateConverter> dateConverters)
	{
		this.dateConverters = dateConverters;
	}
	
	public void setDefaultDateConverter (DateConverter defaultDateConverter)
	{
		this.defaultDateConverter = defaultDateConverter;
	}

	@Override
	public DateConverter getDateConverter(DataType dataType) {

		DateConverter response = this.dateConverters.get(dataType.getName());
		
		if (response == null) response = this.defaultDateConverter;
		
		return response;
	}

}
