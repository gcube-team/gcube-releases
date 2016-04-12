package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.Collection;

import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

public interface TableManager {
	
	public Collection<Table> getAll();
	
	public Collection<Table> getAll(TableType tableType);
	
	public Table get(TableId id) throws NoSuchTableException;
	
	public void remove(TableId id) throws NoSuchTableException;
	
	public Table removeColumn(TableId id, ColumnLocalId localId) throws NoSuchTableException;
	
	public Table removeValidationColumns(TableId id) throws NoSuchTableException;

	public Table addValidationColumns(TableId id, Column ... validationColumns) throws NoSuchTableException;
	
	public void save(Table table, boolean overwrite);
}
