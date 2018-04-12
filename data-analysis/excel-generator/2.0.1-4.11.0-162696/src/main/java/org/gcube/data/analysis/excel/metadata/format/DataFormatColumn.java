package org.gcube.data.analysis.excel.metadata.format;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.excel.data.DataColumn;

class DataFormatColumn implements DataColumn {

	private final String FORMAT_TAG = "format";
	private List<String> dataFormat;
	
	DataFormatColumn(String dataFormat) 
	{
		this.dataFormat = new ArrayList<>();
		this.dataFormat.add(dataFormat);	}
	
	@Override
	public String getName() {
		return FORMAT_TAG;
	}

	@Override
	public DataFormat getDataFormat() {
		return null;
	}

	
	@Override
	public List<String> getStringValues() 
	{
		return this.dataFormat;
	}

	@Override
	public ColumnType getColumnType() {

		return null;
	}
	

}
