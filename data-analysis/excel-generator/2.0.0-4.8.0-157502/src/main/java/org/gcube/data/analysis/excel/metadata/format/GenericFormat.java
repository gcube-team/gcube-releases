package org.gcube.data.analysis.excel.metadata.format;

import org.gcube.data.analysis.excel.data.DataTable;
import org.gcube.data.analysis.excel.data.DataTableImpl;

public class GenericFormat implements DataFormat {

	private DataTableImpl formatTable;
	
	public GenericFormat (String reference, String format)
	{
		this.formatTable = new DataTableImpl(reference);
		this.formatTable.addColumn(new DataFormatColumn(format));
	}
	
	@Override
	public String getReference() {

		return this.formatTable.getTableName();
	}

	@Override
	public DataTable getDefinitionTable() {

		return this.formatTable;
	}

	@Override
	public boolean isCatchValue () 
	{
		return false;
	}

}
