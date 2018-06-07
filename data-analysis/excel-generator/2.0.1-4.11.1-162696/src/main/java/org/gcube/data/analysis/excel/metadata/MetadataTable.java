package org.gcube.data.analysis.excel.metadata;

import java.util.ArrayList;

import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.Table;

public interface MetadataTable extends Table{

	public ArrayList<ColumnModel> getColumns();

}
