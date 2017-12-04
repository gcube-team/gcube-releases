package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class DuplicateRowValidatorFactory extends TableValidatorFactory {

	private static final List<Parameter> parameters = new ArrayList<Parameter>();
	
	private static final OperationId OPERATION_ID = new OperationId(5003);
	
	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	
	public static TargetColumnParameter KEY;
	public static MultivaluedStringParameter INVALIDATE_MODE;
	
	static{
		ArrayList<TableType> allowedTableTypes=new ArrayList<>();
		ArrayList<ColumnType> allowedColumnTypes=new ArrayList<>();
		
		
		allowedTableTypes.add(new CodelistTableType());
		allowedTableTypes.add(new GenericTableType());
		allowedTableTypes.add(new DatasetTableType());
		allowedTableTypes.add(new HierarchicalCodelistTableType());
		allowedTableTypes.add(new TimeCodelistTableType());
		
		allowedColumnTypes.add(new AnnotationColumnType());
		allowedColumnTypes.add(new AttributeColumnType());
		allowedColumnTypes.add(new CodeColumnType());
		allowedColumnTypes.add(new CodeDescriptionColumnType());
		allowedColumnTypes.add(new CodeNameColumnType());
		allowedColumnTypes.add(new DimensionColumnType());
		allowedColumnTypes.add(new MeasureColumnType());
		allowedColumnTypes.add(new TimeDimensionColumnType());
		
		KEY=new TargetColumnParameter("key", "Key", "To check uniqueness column", 
				new Cardinality(0, Integer.MAX_VALUE),allowedTableTypes, allowedColumnTypes);		
		
		INVALIDATE_MODE = new MultivaluedStringParameter("invalidatemode", "Invalidate mode", "describe the type of key to invalidate", Cardinality.OPTIONAL, Arrays.asList("Older","Newer"));
		parameters.add(KEY);
	}
	
	@Inject
	public DuplicateRowValidatorFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory factory) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory=factory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new DuplicateRowValidator(invocation, cubeManager, connectionProvider,evaluatorFactory);
	}

	@Override
	protected String getOperationName() {
		return "Duplicate tuple validation";
	}

	@Override
	protected String getOperationDescription() {
		return "Check for duplicate tuples in table";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		Object obj=invocation.getParameterInstances().get(KEY.getIdentifier());
		if(obj instanceof Iterable<?>){
			List<Column> toCheckColumns=new ArrayList<>();
			for(ColumnReference ref:(Iterable<ColumnReference>)obj)
				toCheckColumns.add(cubeManager.getTable(ref.getTableId()).getColumnById(ref.getColumnId()));			
			return String.format("Check if %s form a uniqe column set",OperationHelper.getColumnLabelsSnippet(toCheckColumns));
		}else{
			ColumnReference ref=(ColumnReference)obj;
			Column col=cubeManager.getTable(ref.getTableId()).getColumnById(ref.getColumnId());
			return String.format("Check if %s is unique", OperationHelper.retrieveColumnLabel(col));
		}
	}
}
