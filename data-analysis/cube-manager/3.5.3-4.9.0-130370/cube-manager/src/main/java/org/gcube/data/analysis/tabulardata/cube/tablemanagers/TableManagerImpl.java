package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.data.DatabaseWrangler;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.DefaultTableCreator.ColumnNameGenerator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.idioms.ColumnHasName;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

import com.google.common.collect.Collections2;

@Default
public class TableManagerImpl implements TableManager {

	private DatabaseWrangler dbWrangler;

	private CubeMetadataWrangler cmWrangler;

	@Inject
	public TableManagerImpl(DatabaseWrangler dbWrangler, CubeMetadataWrangler cmWrangler) {
		this.dbWrangler = dbWrangler;
		this.cmWrangler = cmWrangler;
	}

	@Override
	public Collection<Table> getAll() {
		return cmWrangler.getAll();
	}

	@Override
	public Collection<Table> getAll(TableType tableType) {
		return cmWrangler.getAll(tableType);
	}

	@Override
	public Table get(TableId id) throws NoSuchTableException {
		try {
			return cmWrangler.get(id);
		} catch (org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException e) {
			throw new NoSuchTableException(id);
		}
	}



	//	@Override
	//	public void removeTableData(long id) throws NoSuchTableException {
	//		Table table = get(id);
	//		dbWrangler.removeTable(table.getName());
	//		table.setExists(false);
	//		cmWrangler.update(table);
	//	}

	//	@Override
	//	public void updateMetadata(Table table) throws CubeManagerException {
	//		Table t = get(table.getId());
	//		if (!table.sameStructureAs(t))
	//			throw new CubeManagerException("Provided table does not have the same structure of registered table.");
	//		cmWrangler.update(table);
	//	}

	@Override
	public void remove(TableId id) throws NoSuchTableException {
		Table tableToRemove = get(id);
		Collection<Table> tables = getAll();
		boolean dbTableUsedElsewhere = false;
		for (Table presentTable : tables) {
			if (!presentTable.equals(tableToRemove) && tableToRemove.getName().equals(presentTable.getName())){
				dbTableUsedElsewhere = true;
				break;
			}
		}

		if (!dbTableUsedElsewhere){
			if (dbWrangler.exists(tableToRemove.getName()))
				dbWrangler.removeTable(tableToRemove.getName());
		}

		removeTableMetadata(tableToRemove.getId());
	}

	private void removeTableMetadata(TableId id) throws NoSuchTableException{
		try {
			cmWrangler.remove(id);
		} catch (org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException e) {
			throw new NoSuchTableException(id);
		}
	}

	@Override
	public Table removeValidationColumns(TableId id) throws NoSuchTableException {
		Table table;
		try {
			table = cmWrangler.get(id);
		} catch (org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException e) {
			throw new NoSuchTableException(id);
		}
		List<Column> remainedColumns = new ArrayList<Column>();
		for (Column col: table.getColumns()){
			if (col.getColumnType() instanceof ValidationColumnType){
				dbWrangler.removeColumn(table.getName(), col.getName());
			} else remainedColumns.add(col);
		}

		table.setColumns(remainedColumns);
		return table;
	}

	@Override
	public Table addValidationColumns(TableId id, Column ... validationColumns) throws NoSuchTableException {
		Table table;
		try {
			table = cmWrangler.get(id);
		} catch (org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException e) {
			throw new NoSuchTableException(id);
		}
		List<Column> oldColumns = table.getColumns();

		//List<Column> newValidationColumns = new ArrayList<Column>();
		for (Column col: validationColumns){
			if (col.getColumnType() instanceof ValidationColumnType){
				String eligibleName;
				do {
					eligibleName = ColumnNameGenerator.generateColumnName();
				} while (!Collections2.filter(oldColumns, new ColumnHasName(eligibleName)).isEmpty());
				col.setName(eligibleName);
				dbWrangler.addColumn(table.getName(), col.getName(), col.getDataType(), col.getCreationDefaultValue());
				oldColumns.add(col);
			} 
		}
		return table;
	}

	public void save(Table table, boolean overwrite){
		cmWrangler.save(table, overwrite);
	}

	@Override
	public Table removeColumn(TableId id, ColumnLocalId localId)
			throws NoSuchTableException {
		Table table;
		Column column;
		try {
			table = cmWrangler.get(id);
			column = table.getColumnById(localId);
		} catch (Exception e) {
			throw new NoSuchTableException(id);
		}
		List<Column> columns = table.getColumns();
		columns.remove(column);
		dbWrangler.removeColumn(table.getName(), column.getName());
		table.setColumns(columns);
		return table;
	}
}
