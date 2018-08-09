package org.gcube.data.analysis.excel.metadata.format;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.excel.BasicTable;
import org.gcube.data.analysis.excel.data.DataColumn;
import org.gcube.data.analysis.excel.data.DataTable;

public class CodeList extends BasicTable implements DataTable {

	private class CodeListColumn implements DataColumn
	{
		private final String name;
		private final List<String> values;
		private ColumnType type;
		
		public CodeListColumn(String name, List<String> values, boolean isDescription) {
			this.name = name;
			this.values = values;
			this.type = isDescription ? ColumnType.CODE_DESCRIPTION : ColumnType.CODE;
		}

		@Override
		public String getName() 
		{
			return this.name;
		}

		@Override
		public DataFormat getDataFormat() {
			return null;
		}

		@Override
		public List<String> getStringValues() {
			return this.values;
		}

		@Override
		public ColumnType getColumnType() 
		{
			return this.type;
		}
		
	}
	
	private ArrayList<DataColumn> columns;
	
	public CodeList (String name)
	{
		super (name);
		this.columns = new ArrayList<>();
	}

	public void addColumn (String name, List<String> valueList, boolean isDescription)
	{
		this.columns.add(new CodeListColumn(name, valueList,isDescription));
	}
	
	@Override
	public ArrayList<DataColumn> getColumns() 
	{
		return columns;
	}
	
	
	
}
