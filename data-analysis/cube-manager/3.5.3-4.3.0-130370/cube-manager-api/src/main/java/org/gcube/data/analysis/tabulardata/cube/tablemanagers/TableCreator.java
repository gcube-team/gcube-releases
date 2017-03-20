package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public interface TableCreator {
	
	public TableCreator addColumn(Column column);

	public TableCreator addColumns(Column... columns);

	public TableCreator like(Table table, boolean copyData);
	
	public TableCreator like(Table table, boolean copyData, List<Column> columnsToRemove);
	
	public Table create() throws TableCreationException;
	
	public Table create(String tableName) throws TableCreationException;
		
	TableCreator addColumnAfter(Column columnToAdd, Column columnBefore);

	TableCreator addColumnBefore(Column columnToAdd, Column columnAfter);

	TableCreator addColumnsAfter(Column columnBefore, Column ... columns);

	TableCreator addColumnsBefore(Column columnAfter, Column ... columns);
	
	TableCreator removeColumn(Column columnToRemove);
	
	TableCreator changeColumnType(Column column, DataType newType);

	TableCreator addColumnFirst(Column columnToAdd);
	
	//TableCreator addColumnAt(Column columnToAdd, int index);

}
