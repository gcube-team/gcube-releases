package org.gcube.data.analysis.excel.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.excel.metadata.MetadataColumn;
import org.gcube.data.analysis.excel.metadata.format.DataFormat;

public class StringColumn extends MetadataColumn implements DataColumn {

	private List<String> data;

	public StringColumn(String name, DataFormat format,ColumnType type) {
		super (name,format,type);
		this.data = new ArrayList<>();
	}
	
	public StringColumn(String name, DataFormat format,ArrayList<String> data,ColumnType type) {
		super (name,format,type);
		this.data = data;
	}
	
	public void addData (String value)
	{
		this.data.add(value);
	}
	
	public void setAllData (List<String> data)
	{
		this.data = data;
	}
	
	public void setData (String value, int index)
	{
		int position = index+1;
		
		if (position <= this.data.size()) this.data.set(index, value);
		
		else if (position> this.data.size())
		{
			for (int i = data.size();i<index; i++)
			{
				data.add(null);
			}
			
			data.add(value);
		}
		
	}
	
	@Override
	public List<String> getStringValues() 
	{
		return this.data;
	}



}
