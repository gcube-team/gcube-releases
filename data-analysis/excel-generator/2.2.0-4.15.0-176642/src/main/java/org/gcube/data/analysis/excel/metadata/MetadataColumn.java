package org.gcube.data.analysis.excel.metadata;

import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.metadata.format.DataFormat;

public class MetadataColumn implements ColumnModel {

	private String name;
	private DataFormat dataFormat;
	private ColumnType type;
	
	public MetadataColumn (String name, DataFormat dataFormat,ColumnType type)
	{
		this.name = name;
		this.dataFormat = dataFormat;
		this.type = type;
	}

	@Override
	public DataFormat getDataFormat()
	{
		return this.dataFormat;
	}

	@Override
	public String getName() 
	{
		return this.name;
	}

	@Override
	public ColumnType getColumnType() {
		return this.type;
	}

	


	
	
}
