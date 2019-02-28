package org.gcube.data.analysis.excel.data;

import java.util.ArrayList;

import org.gcube.data.analysis.excel.Table;

public interface DataTable  extends Table{

	public ArrayList<DataColumn> getColumns();
	

}
