package org.gcube.data.analysis.excel.metadata.format;

import java.util.List;

import org.gcube.data.analysis.excel.data.DataTable;

public class CatchMeasureFormat implements DataFormat {

	private String reference;
	private CodeList formatTable;
	
	public CatchMeasureFormat (String reference, List<String> measures)
	{
		this.reference = reference;
		this.formatTable = new CodeList(reference);
		this.formatTable.addColumn(reference,measures,false);
	}
	
	@Override
	public String getReference() {

		return this.reference;
	}

	@Override
	public DataTable getDefinitionTable() {

		return this.formatTable;
	}

	@Override
	public boolean isCatchValue () 
	{
		return true;
	}

}
