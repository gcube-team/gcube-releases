package org.gcube.data.analysis.tabulardata.operation.column;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.HarmonizationRuleTable;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.utils.Harmonizations;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateAmbiguousReferenceFactory;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ColumnCreatorWorker;

public class ChangeToDimensionColumn extends ColumnCreatorWorker {

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory evaluatorFactory;

	private Table targetTable;

	private Column targetColumn;

	private Table refTable;

	private Column refColumn;

	private Table newTable;

	private Column dimensionColumn;

	private Map<TDTypeValue, Long> codeMapping;




	public ChangeToDimensionColumn(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory evaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory=evaluatorFactory;
	}

	@Override
	protected WorkerResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"creating new table");
		checkAborted();
		createNewTable();
		updateProgress(0.5f,"filling table with data");
		checkAborted();
		applyHarmonizationsIfAny();
		checkAborted();
		fillNewTableWithData();
		checkAborted();
		newTable = cubeManager.removeColumn(newTable.getId(), targetColumn.getLocalId());
		updateProgress(0.8f, "preparating table for future rollback");
		return new ImmutableWorkerResult(newTable, createDiff(targetTable, targetColumn));
	}

	@SuppressWarnings("unchecked")
	private void retrieveParameters() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		ColumnReference columnReferenceParam = OperationHelper.getParameter(
				ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER, getSourceInvocation());
		refTable = cubeManager.getTable(columnReferenceParam.getTableId());
		refColumn = refTable.getColumnById(columnReferenceParam.getColumnId());
		try{
			codeMapping = OperationHelper.getParameter(
					ValidateAmbiguousReferenceFactory.MAPPING_PARAMETER, getSourceInvocation());
		}catch(Exception e){
			codeMapping = Collections.emptyMap();
		}
	}

	@SuppressWarnings("unchecked")
	private void createNewTable() {
		ColumnRelationship relationship = new ImmutableColumnRelationship(refTable.getId(), refColumn.getLocalId());
		dimensionColumn = new DimensionColumnFactory().create(relationship);
		
		ArrayList<ColumnMetadata> toSetMeta=new ArrayList<>();		
		if (targetColumn.contains(NamesMetadata.class))
			toSetMeta.add(targetColumn.getMetadata(NamesMetadata.class));

		if(getSourceInvocation().getParameterInstances().containsKey(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier())){
			Object additional=getSourceInvocation().getParameterInstances().get(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier());
			if(additional instanceof Collection<?>) toSetMeta.addAll((Collection<? extends ColumnMetadata>) additional);
			else toSetMeta.add((ColumnMetadata) additional);
		}		
		dimensionColumn.setAllMetadata(toSetMeta);

		newTable = cubeManager.createTable(targetTable.getTableType())
				.like(targetTable, true).addColumnAfter(dimensionColumn, targetColumn).create();
	}

	@Override
	public List<ColumnLocalId> getCreatedColumns() {
		return Collections.singletonList(dimensionColumn.getLocalId());
	}
	
	private void fillNewTableWithData() throws WorkerException {
		List<String> sqlUpdateQueries = new ArrayList<String>();
		sqlUpdateQueries.add(getFillDimensionColumnSQLCommand());
		sqlUpdateQueries.addAll(getFillMappingColumnSQLCommand(codeMapping));

		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlUpdateQueries.toArray(new String[sqlUpdateQueries.size()]));
		} catch (SQLException e) {
			throw new WorkerException("Unable to create a reference for some data",e);
		}
	}

	private List<String> getFillMappingColumnSQLCommand(Map<TDTypeValue, Long> mapping) {
		if(mapping ==null || mapping.isEmpty()) return Collections.emptyList();
		List<String> queries = new ArrayList<String>();
		for(Entry<TDTypeValue, Long> entry : mapping.entrySet())
			queries.add(String
					.format("UPDATE %s SET %s = %s WHERE %s = %s", newTable.getName(), dimensionColumn.getName(), entry.getValue(), 
							targetColumn.getName(), this.evaluatorFactory.getEvaluator(entry.getKey()).evaluate()));
		return queries;
	}

	private String getFillDimensionColumnSQLCommand() {
//		return String
//				.format("UPDATE %1$s AS new_table SET %2$s = refCol.id FROM (SELECT id, %5$s as val FROM %4$s) as refCol,"
//						+ " %6$s as targetTable WHERE targetTable.id = new_table.id and refCol.val=targetTable.%3$s;",
//						newTable.getName(), dimensionColumn.getName(), targetColumn.getName(), refTable.getName(),
//						refColumn.getName(), targetTable.getName());
		
		return String
				.format("UPDATE %1$s AS new_table SET %2$s = refCol.id FROM (SELECT id, %5$s as val FROM %4$s) as refCol"
						+ " WHERE refCol.val=new_Table.%3$s;",
						newTable.getName(), dimensionColumn.getName(), targetColumn.getName(), refTable.getName(),
						refColumn.getName());
		
		
	}

	@SuppressWarnings("unchecked")
	private Table createDiff(Table targetTable, Column targetColumn){
		List<Column> columnsToRemove = new ArrayList<Column>(targetTable.getColumns().size()-1);
		for (Column col : targetTable.getColumnsExceptTypes(IdColumnType.class))
			if(!col.equals(targetColumn))
				columnsToRemove.add(col);
		TableCreator tableCreator = cubeManager.createTable(targetTable.getTableType()).like(targetTable, true, columnsToRemove);
		Table toReturn = tableCreator.create();
		GenericMapMetadata gmm = new GenericMapMetadata(Collections.singletonMap(ChangeTypeRollbackableWorker.REFERENCE_COLUMN_KEY, dimensionColumn.getLocalId().getValue()));
		return cubeManager.modifyTableMeta(toReturn.getId()).setTableMetadata(gmm).create();
	}

	private void applyHarmonizationsIfAny() throws WorkerException{
		// check whether the referred column has harmonization rules to apply
		try{
			HarmonizationRuleTable existingRuleTable = refTable.getMetadata(HarmonizationRuleTable.class);
			if(Harmonizations.isColumnUnderRules(refColumn.getLocalId(), existingRuleTable, connectionProvider, evaluatorFactory)){
				Harmonizations.harmonizeTable(existingRuleTable, 
						refTable.getColumnReference(refColumn), newTable.getColumnReference(targetColumn), 
						newTable, connectionProvider, evaluatorFactory);
			}
		}catch(NoSuchMetadataException e){
			// No harmonization defined at all
		}catch(SQLException e){
			throw new WorkerException("Unable to apply harmonizations",e);
		}
	}

}
