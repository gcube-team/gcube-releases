package org.gcube.data.analysis.tabulardata.operation.column;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.TimeDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.time.PeriodTypeHelper;
import org.gcube.data.analysis.tabulardata.operation.time.PeriodTypeHelperProvider;
import org.gcube.data.analysis.tabulardata.operation.validation.TimeDimensionColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ColumnCreatorWorker;

import com.google.common.collect.Lists;

public class ChangeToTimeDimensionColumn extends ColumnCreatorWorker {

	//private static final Logger log = LoggerFactory.getLogger(ChangeToTimeDimensionColumn.class);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;


	private Table targetTable;

	private Column targetColumn;

	private PeriodType periodType;

	private ValueFormat timeFormat;
	
	private Table newTable;

	private Table timeCodelist;

	private PeriodTypeHelper helper ;
	
	private PeriodTypeHelperProvider periodTypeHelperProvider;

	private Column timeDimensionColumn;

	private SQLExpressionEvaluatorFactory evaluator;
	
	public ChangeToTimeDimensionColumn(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			PeriodTypeHelperProvider periodTypeHelperProvider, SQLExpressionEvaluatorFactory evaluator) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.periodTypeHelperProvider = periodTypeHelperProvider;
		this.evaluator = evaluator;
	}

	@Override
	protected WorkerResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		this.helper = periodTypeHelperProvider.getHelper(periodType);
		updateProgress(0.1f,"creating time codelist");
		checkAborted();
		createTimeCodelist();
		updateProgress(0.3f, "creating new table");
		checkAborted();
		createNewTable();
		updateProgress(0.6f,"linking table to the time codelist");
		checkAborted();
		linkNewTableToTimeCodelist();
		checkAborted();
		newTable = cubeManager.removeColumn(newTable.getId(), targetColumn.getLocalId());
		updateProgress(0.8f,"preparating table for future rollback");
		checkAborted();
		return new ImmutableWorkerResult(newTable,  createDiff(targetTable, targetColumn), Lists.newArrayList(timeCodelist));
	}

	private void linkNewTableToTimeCodelist() throws WorkerException {
		try {
			String sql = helper.getUpdateDimensionColumnSQL(targetColumn,
					newTable, timeDimensionColumn.getName(), timeCodelist.getName(), timeFormat, evaluator);
			SQLHelper.executeSQLCommand(sql, connectionProvider);
		}catch (MalformedExpressionException e) {
			throw new WorkerException("error converting types",e);
		} catch (SQLException e) {
			throw new WorkerException("Unable to link target table tuples to the newly created time codelist", e);
		}
	}

	private void createNewTable() {
		timeDimensionColumn = new TimeDimensionColumnFactory().create(periodType);
		Column refColumn = timeCodelist.getColumnByName(periodType.getName()); 
		timeDimensionColumn.setRelationship(new ImmutableColumnRelationship(timeCodelist.getId(), refColumn.getLocalId()));

		newTable = cubeManager.createTable(targetTable.getTableType())
				.like(targetTable, true).addColumnAfter(timeDimensionColumn, targetColumn).create();
	}

	private void createTimeCodelist() throws WorkerException {
		PeriodTypeHelper helper = periodTypeHelperProvider.getHelper(periodType);
		timeCodelist = helper.createTimeCodelist();
	}


	private void retrieveParameters() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		String periodTypeName = OperationHelper.getParameter(
				TimeDimensionColumnValidatorFactory.PERIOD_FORMAT_PARAMETER, getSourceInvocation());
		periodType = PeriodType.fromName(periodTypeName);
		String timeFormatId = (String)getSourceInvocation().getParameterInstances().get(TimeDimensionColumnValidatorFactory.FORMAT_ID_PARAMETER.getIdentifier());
		if (timeFormatId!=null)
			timeFormat = periodType.getTimeFormatById(timeFormatId);
		else timeFormat = periodType.getAcceptedFormats().get(0);
	}

	@SuppressWarnings("unchecked")
	private Table createDiff(Table targetTable, Column targetColumn){
		List<Column> columnsToRemove = new ArrayList<Column>(targetTable.getColumns().size()-1);
		for (Column col : targetTable.getColumnsExceptTypes(IdColumnType.class))
			if(!col.equals(targetColumn))
				columnsToRemove.add(col);
		TableCreator tableCreator = cubeManager.createTable(targetTable.getTableType()).like(targetTable, true, columnsToRemove);
		Table toReturn = tableCreator.create();
		GenericMapMetadata gmm = new GenericMapMetadata(Collections.singletonMap(ChangeTypeRollbackableWorker.REFERENCE_COLUMN_KEY, timeDimensionColumn.getLocalId().getValue()));
		return cubeManager.modifyTableMeta(toReturn.getId()).setTableMetadata(gmm).create();
	}

	@Override
	public List<ColumnLocalId> getCreatedColumns() {
		return Collections.singletonList(timeDimensionColumn.getLocalId());
	}
	
	
}
