package org.gcube.data.analysis.excel.metadata.format;

import org.gcube.data.analysis.excel.data.DataTable;

public interface DataFormat {
	
	public String getReference ();
	
	public DataTable getDefinitionTable();
	
	public boolean isCatchValue ();
}
