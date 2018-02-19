package org.gcube.data.analysis.excel.data;

import java.util.List;

import org.gcube.data.analysis.excel.ColumnModel;

public interface DataColumn extends ColumnModel{
	
	public List<String> getStringValues ();

	
}
