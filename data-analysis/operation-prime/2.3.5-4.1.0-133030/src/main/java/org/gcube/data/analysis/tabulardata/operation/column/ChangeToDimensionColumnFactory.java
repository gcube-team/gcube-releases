package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.mapping.SQLModelMapper;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateAmbiguousReferenceFactory;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

import com.google.common.collect.Lists;

public class ChangeToDimensionColumnFactory extends ChangeColumnTypeTransformationFactory {

	private static final ColumnType MANAGED_COLUMN_TYPE = new DimensionColumnType();

	private static final OperationId OPERATION_ID = new OperationId(2006);
	
	private final static List<Parameter> parameters = new ArrayList<Parameter>();

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;

	private SQLModelMapper modelMapper;
	
	private ValidateDimensionColumnFactory validationFactory;
	
	private ValidateAmbiguousReferenceFactory ambiguousValidationfactory;

	private final static List<ColumnType> allowedSourceColumnTypes = Lists.newArrayList();
	
	static {
		allowedSourceColumnTypes.add(new CodeColumnType());
		allowedSourceColumnTypes.add(new CodeNameColumnType());
		allowedSourceColumnTypes.add(new CodeDescriptionColumnType());
		allowedSourceColumnTypes.add(new AttributeColumnType());
		allowedSourceColumnTypes.add(new AnnotationColumnType());
		
		
		//Needed by validation
		parameters.add(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER);
		parameters.add(ValidateDimensionColumnFactory.EXTERNAL_CONDITION_PARAMETER);
		parameters.add(ValidateAmbiguousReferenceFactory.MAPPING_PARAMETER);
		parameters.add(ADDITIONAL_META_PARAMETER);		
	}

	@Inject
	public ChangeToDimensionColumnFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			ValidateDimensionColumnFactory validationFactory,  
			ValidateAmbiguousReferenceFactory ambiguousValidationfactory,
			SQLExpressionEvaluatorFactory evaluatorFactory, SQLModelMapper modelMapper) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.validationFactory= validationFactory;
		this.ambiguousValidationfactory = ambiguousValidationfactory;
		this.evaluatorFactory=evaluatorFactory;
		this.modelMapper = modelMapper;
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		checkCodelistUnicity(invocation, cubeManager);
		return new ChangeToDimensionColumn(invocation, cubeManager, connectionProvider,evaluatorFactory, modelMapper);
	}

	@SuppressWarnings("unchecked")
	private void checkCodelistUnicity(OperationInvocation invocation,
			CubeManager cubeManager) throws InvalidInvocationException {
		ColumnReference columnRef = OperationHelper.getParameter(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER, invocation);
		Table table = cubeManager.getTable(invocation.getTargetTableId());
		Column opColumn = table.getColumnById(invocation.getTargetColumnId());
		for (Column column: table.getColumnsByType(DimensionColumnType.class)){
			if (column.getRelationship().getTargetTableId().equals(columnRef.getTableId()) && opColumn.getLocalId()!=column.getLocalId())
				throw new InvalidInvocationException(invocation, "Codelist reference already present in the table (two different columns of a table cannot point to the same external table)");
		}
	}

	@Override
	public boolean isRollbackable() {
		return true;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable, Table createdTable,
			OperationInvocation oldInvocation) {
		return new ChangeTypeRollbackableWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}

	@Override
	protected ColumnType getManagedColumnType() {
		return MANAGED_COLUMN_TYPE;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public List<WorkerFactory<ValidationWorker>> getPrecoditionValidations() {
		return Arrays.asList((WorkerFactory<ValidationWorker>)validationFactory, ambiguousValidationfactory);
	}
	
	@Override
	protected List<ColumnType> getAllowedSourceColumnTypes() {
		return allowedSourceColumnTypes;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		ColumnReference codelistRef=OperationHelper.getParameter(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER, invocation);
		Table codelist=cubeManager.getTable(codelistRef.getTableId());
		Column col=codelist.getColumnById(codelistRef.getColumnId());
		Column targetColumn=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		return String.format("Set %s as Dimension, referring %s.%s [%s]",
				OperationHelper.retrieveColumnLabel(targetColumn),OperationHelper.retrieveTableLabel(codelist),
				OperationHelper.retrieveColumnLabel(col),col.getColumnType().getName());		
	}

}
