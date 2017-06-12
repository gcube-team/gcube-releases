package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ValidateAmbiguousReferenceFactory extends ColumnValidatorFactory {

	private static List<Parameter> parameters;
	
	private static final OperationId OPERATION_ID = new OperationId(5007);
	
	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;
	
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	
	public static TargetColumnParameter TARGET_COLUMN_PARAMETER;
	
	public static  MapParameter MAPPING_PARAMETER=new MapParameter("mapping", "Mapping",
			"Maps dataset entry to external entry", Cardinality.OPTIONAL, TDTypeValue.class, Long.class);
	
	
	
	static {
		ArrayList<TableType> allowedTableTypes=new ArrayList<>();
		allowedTableTypes.add(new CodelistTableType());
	
		
		TARGET_COLUMN_PARAMETER=new TargetColumnParameter("refColumn", "Codelist referenced column",
				"A codelist column containing values that are contained in the target column", Cardinality.ONE, 
				allowedTableTypes);
		parameters=Arrays.asList(new Parameter[]{
				TARGET_COLUMN_PARAMETER,
				MAPPING_PARAMETER
			});
	}
	
	@Inject
	public ValidateAmbiguousReferenceFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new ValidateAmbiguousReference(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory);
	}
		
	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		performBaseChecks(toDescribeInvocation,cubeManager);
		Table table = cubeManager.getTable(toDescribeInvocation.getTargetTableId());
		Column column = table.getColumnById(toDescribeInvocation.getTargetColumnId());
		ColumnReference columnReference = (ColumnReference)toDescribeInvocation.getParameterInstances().get(TARGET_COLUMN_PARAMETER.getIdentifier());
		Table referredTable = cubeManager.getTable(columnReference.getTableId());
		Column referredColumn = referredTable.getColumnById(columnReference.getColumnId());
		String mappingDescription ="";
		if (toDescribeInvocation.getParameterInstances().get(MAPPING_PARAMETER.getIdentifier())!=null)
			mappingDescription = "passing mapping to resolve conflicts";
		return String.format("Validate column %s.%s for ambiguos referred values in column %s.%s  %s",
				OperationHelper.retrieveTableLabel(table), OperationHelper.retrieveColumnLabel(column), 
				OperationHelper.retrieveTableLabel(referredTable), OperationHelper.retrieveColumnLabel(referredColumn), 
				mappingDescription);
	}
	
	@Override
	protected String getOperationName() {
		return "Ambiguous external reference check";
	}

	@Override
	protected String getOperationDescription() {
		return "Check for ambiguous values in external references";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
}
