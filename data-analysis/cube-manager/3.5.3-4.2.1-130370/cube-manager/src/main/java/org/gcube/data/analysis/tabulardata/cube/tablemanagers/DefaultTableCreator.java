package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.event.Event;

import org.gcube.data.analysis.tabulardata.cube.data.DatabaseWrangler;
import org.gcube.data.analysis.tabulardata.cube.data.SQLDatabaseWrangler.RandomString;
import org.gcube.data.analysis.tabulardata.cube.events.TableCreationEvent;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.IdColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.idioms.ColumnHasName;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class DefaultTableCreator implements TableCreator {

	Event<TableCreationEvent> tableCreatedEvent;
		
	private static Logger logger = LoggerFactory.getLogger(DefaultTableCreator.class);
	
	private static RandomString randomString = new RandomString(6);
	
	protected DatabaseWrangler dbWrangler;
	protected CubeMetadataWrangler mdWrangler;
	protected TableManager tableManager;

	protected List<Column> newTableColumns = Lists.newArrayList();

	protected List<Column> newDBColumns = Lists.newArrayList();

	// Following attributes are used for cloning
	protected Table tableToClone = null;
	protected Set<Column> columnsToRemove = Sets.newHashSet();
	protected boolean copyData = false;

	Map<Column, DataType> columnAlterMap = new HashMap<Column, DataType>();
	
	private TableType tableType;

	public DefaultTableCreator(DatabaseWrangler dbWrangler, CubeMetadataWrangler mdWrangler, TableManager tableManager,
			TableType tableType, Event<TableCreationEvent> tableCreatedEvent) {
		super();
		this.dbWrangler = dbWrangler;
		this.mdWrangler = mdWrangler;
		this.tableManager = tableManager;
		this.tableType = tableType;
		this.tableCreatedEvent = tableCreatedEvent;
	}

	@Override
	public TableCreator addColumn(Column column) {
		checkColumnToAdd(column);
		newTableColumns.add(column);
		newDBColumns.add(column);
		return this;
	}

	@Override
	public TableCreator addColumnAfter(Column columnToAdd, Column columnBefore) {
		checkColumnToAdd(columnToAdd);
		int index = newTableColumns.indexOf(columnBefore);
		if (index==-1) throw new IllegalArgumentException(columnBefore+" not found");
		newTableColumns.add(index+1, columnToAdd);
		newDBColumns.add(columnToAdd);
		return this;
	}

	@Override
	public TableCreator addColumnFirst(Column columnToAdd) {
		checkColumnToAdd(columnToAdd);
		newTableColumns.add(0, columnToAdd);
		newDBColumns.add(columnToAdd);
		return this;
	}
	
	@Override
	public TableCreator addColumnBefore(Column columnToAdd, Column columnAfter) {
		checkColumnToAdd(columnToAdd);
		int index = newTableColumns.indexOf(columnAfter);
		if (index==-1) throw new IllegalArgumentException(columnAfter+" not found");
		newTableColumns.add(index, columnToAdd);
		newDBColumns.add(columnToAdd);
		return this;
	}
	

	@Override
	public TableCreator addColumns(Column... columns) {
		for (Column column : columns) {
			addColumn(column);
		}
		return this;
	}
	
	@Override
	public TableCreator addColumnsAfter(Column columnBefore, Column... columns) {
		for (Column column : columns) {
			addColumnAfter(column, columnBefore);
		}
		return this;
	}

	@Override
	public TableCreator addColumnsBefore(Column columnAfter, Column... columns) {
		for (Column column : columns) {
			addColumnBefore(column, columnAfter);
		}
		return this;
	}

	public TableCreator removeColumn(Column columnToRemove){
		removeColumnInternal(columnToRemove);
		return this;
	}

	private void checkColumnToAdd(Column column) {
		if (!isAllowedColumn(column))
			throw new IllegalArgumentException("Invalid column type: " + column.getColumnType());
	}

	protected final boolean isAllowedColumn(Column column) {
		if (column.getColumnType().equals(new IdColumnType()))
			return false;
		return true;
	}

	@Override
	public TableCreator like(Table table, boolean copyData) {
		return like(table, copyData, new ArrayList<Column>(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableCreator like(Table table, boolean copyData, List<Column> columnsToRemove) {
		tableToClone = table;
		this.copyData = copyData;
		newTableColumns = new ArrayList<Column>();
		for (Column column : table.getColumnsExceptTypes(IdColumnType.class)){
			Column newColumn = new Column(column.getLocalId(), column.getDataType(), column.getColumnType());
			newColumn.setName(column.getName());
			if (column.hasRelationship())
				newColumn.setRelationship(column.getRelationship());
			List<ColumnMetadata> metadataToReuse = new ArrayList<ColumnMetadata>();
			for (ColumnMetadata metadata : column.getAllMetadata())
				if (metadata.isInheritable())
					metadataToReuse.add(metadata);
			if (!metadataToReuse.isEmpty())
				newColumn.setAllMetadata(metadataToReuse);
			newTableColumns.add(newColumn);
		}
		removeColumnsInternal(columnsToRemove);
		return this;
	}

	private void removeColumnsInternal(List<Column> columnsToRemove) {
		if (tableToClone==null) return;
		this.columnsToRemove = Sets.newHashSet();
		for (Column columnToRemove : columnsToRemove) {
			removeColumnInternal(columnToRemove);
		}
	}

	private void removeColumnInternal(Column columnToRemove) {
		if (tableToClone.getColumns().contains(columnToRemove)) {
			this.columnsToRemove.add(columnToRemove);
			this.newTableColumns.remove(columnToRemove);
		}
	}
	
	@Override
	public TableCreator changeColumnType(Column column, DataType newType) {
		this.columnAlterMap.put(column, newType);
		return this;
	}

	public Table create() throws TableCreationException {
		return create(null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Table create(String selectedTableName) throws TableCreationException {
		try{
			checkConsistency();

			setColumnNames();

			String tableName = selectedTableName;
			if (tableToClone != null) {
				
				//TODO: add clone with new table name
				// Handle cloning
				tableName = dbWrangler.cloneTable(tableToClone.getName(), copyData, false);
				

				for (Column column : columnsToRemove) {
					dbWrangler.removeColumn(tableName, column.getName());
				}

				for (Column column : newDBColumns) {
					dbWrangler.addColumn(tableName, column.getName(), column.getDataType(), column.getCreationDefaultValue());
				}
				
				for (Entry<Column, DataType> entry: this.columnAlterMap.entrySet()){
					dbWrangler.alterColumnType(tableName, entry.getKey().getName(), entry.getValue());
					
				}

			} else {
				// Handle simple creation of empty table
				if (selectedTableName == null)
					tableName = dbWrangler.createTable();
				else dbWrangler.createTable(selectedTableName);
				
				for (Column column : newDBColumns) {
					dbWrangler.addColumn(tableName, column.getName(), column.getDataType(), column.getCreationDefaultValue());
				}
			}
									
			Column idColumn = createIdColumn();
			
			if (tableToClone != null) 
				idColumn.setLocalId(tableToClone.getColumnsByType(IdColumnType.class).get(0).getLocalId());
			
			List<Column> columns = Lists.newArrayList(idColumn);
			columns.addAll(getAllColumnsExceptId());

			addIndexes(tableName, columns);

			Table newTable = createBaseTable(tableName, columns);
			
			
			// Clone metadata
			if (tableToClone != null) {
				cloneMetadata(tableToClone, newTable);
			}

				
			
			// Register codelist on metadata DB
			Table createdTable =  mdWrangler.save(newTable, false);
			tableCreatedEvent.fire(new TableCreationEvent(createdTable));
			return createdTable;
		}catch(TableCreationException tce){
			throw tce;
		}catch (Exception e) {
			e.printStackTrace();
			throw new TableCreationException(e.getMessage());
		}finally{
			resetTableCreator();
		}

	}

	private void resetTableCreator(){
		newTableColumns = Lists.newArrayList();
		newDBColumns = Lists.newArrayList();
		tableToClone = null;
		columnsToRemove = Sets.newHashSet();
		copyData = false;
	}

	protected void checkConsistency() throws TableCreationException {
		try {
			checkColumnsRelationship();
		} catch (Exception e) {
			throw new TableCreationException(e.getMessage());
		}
	}

	private void checkColumnsRelationship() throws Exception {
		for (Column column : getAllColumnsExceptId()) {
			if (column.hasRelationship()) {
				try {
					if (column.getColumnType() instanceof TimeDimensionColumnType && column.contains(PeriodTypeMetadata.class) )
						continue;
					Table targetTable = tableManager.get(column.getRelationship().getTargetTableId());
					targetTable.getColumnById(column.getRelationship().getTargetColumnId());
				} catch (NoSuchTableException e) {
					throw new Exception(String.format("Referenced Codelist with ID %1$s does not exists.", column
							.getRelationship().getTargetTableId()));
				}
			}
		}
	}

	protected void setColumnNames() {
		List<Column> columns = newDBColumns;
		for (Column column : columns) {
			if (column.hasName()) {
				continue;
			}
			String eligibleName;
			do {
				eligibleName = ColumnNameGenerator.generateColumnName();
			} while (!Collections2.filter(newTableColumns, new ColumnHasName(eligibleName)).isEmpty());
			column.setName(eligibleName);
		}
	}

	protected Table createBaseTable(String tableName, List<Column> columns) {
		Table result = new Table(tableType);
		result.setColumns(columns);
		result.setName(tableName);
		return result;
	}

	protected void cloneMetadata(Table sourceTable, Table destTable) {
		for (TableMetadata m : sourceTable.getAllMetadata()) {
			logger.info("source metadata : "+m);
			if (m.isInheritable())
				destTable.setMetadata(m);
		}
	}

	private List<Column> getAllColumnsExceptId() {
		List<Column> result = Lists.newArrayList(newTableColumns);
		if (tableToClone != null) 
			result.removeAll(tableToClone.getColumnsByType(new IdColumnType()));
		for (Column col: result){
			DataType type;
			if ((type=columnAlterMap.get(col))!=null)
				col.setDataType(type);
		}
		return result;
	}

	protected abstract void addIndexes(String tableName, Collection<Column> columns);

	protected Column createIdColumn() {
		return IdColumnFactory.create();
	}

	public static class ColumnNameGenerator {

		public static String generateColumnName() {
			return randomString.nextString().toLowerCase();
		}

	}

}
