package org.gcube.data.analysis.excel.data;

import java.util.ArrayList;

import org.gcube.data.analysis.excel.BasicTable;

public class DataTableImpl extends BasicTable implements DataTable{

	private ArrayList<DataColumn> columns;
	
	public DataTableImpl (String name)
	{
		super (name);
		this.columns = new ArrayList<>();
	}

	public DataTableImpl ()
	{
		super ();
		this.columns = new ArrayList<>();
	}

	
	public void addColumn (DataColumn column)
	{
		this.columns.add(column);
	}
	
	public void setColumn (DataColumn column, int position)
	{
		columns.set(position-1, column);
	}


	@Override
	public ArrayList<DataColumn> getColumns() {
		return columns;
	}
}
