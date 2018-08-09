package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;

public class DataColumnBean extends ColumnBean
{
	private List<String> data;
	
	public DataColumnBean (Column column,ConceptMutableBean associatedConcept)
	{
		super (column,associatedConcept);
		this.data = new ArrayList<>();
	}
	
	public DataColumnBean (Column column,ConceptMutableBean associatedConcept,List<String> data)
	{
		super (column,associatedConcept);
		if (data != null) this.data = data;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	
}
