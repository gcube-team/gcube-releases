package org.gcube.data.analysis.tabulardata.operation.data.transformation;

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
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
@Singleton
public class NormalizationFactory extends TableTransformationWorkerFactory {

	private static final OperationId OPERATION_ID=new OperationId(3008);

	public static TargetColumnParameter TO_NORMALIZE_COLUMNS=new TargetColumnParameter("to_normalize", "To normalize", "Columns to normalize", new Cardinality(1,Integer.MAX_VALUE));
	public static final LocalizedTextParameter NORMALIZED_LABEL= new LocalizedTextParameter("norm_label", "Normalized label", "The normalized column label", Cardinality.OPTIONAL);
	public static final LocalizedTextParameter QUANTITY_LABEL= new LocalizedTextParameter("quant_label", "Quantity label", "The quantity column label", Cardinality.OPTIONAL);

	private static List<Parameter> params=Arrays.asList((Parameter)TO_NORMALIZE_COLUMNS,NORMALIZED_LABEL,QUANTITY_LABEL); 

	private CubeManager cubeManager;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	private DatabaseConnectionProvider connProvider;


	@Inject
	public NormalizationFactory(CubeManager cubeManager,
			SQLExpressionEvaluatorFactory evaluatorFactory,
			DatabaseConnectionProvider connProvider) {
		super();
		this.cubeManager = cubeManager;
		this.evaluatorFactory = evaluatorFactory;
		this.connProvider = connProvider;
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		performCustomChecks(invocation);
		return new NormalizationWorker(invocation, cubeManager, evaluatorFactory, connProvider);
	}

	private void performCustomChecks(OperationInvocation toDescribeInvocation) throws InvalidInvocationException{
		for(ColumnReference ref:getColumns(toDescribeInvocation)){
			if(!ref.getTableId().equals(toDescribeInvocation.getTargetTableId())) throw new InvalidInvocationException(toDescribeInvocation, "All column must belong to target table");
		}
	}


	static List<ColumnReference> getColumns(OperationInvocation invocation)throws InvalidInvocationException{
		try{
			ArrayList<ColumnReference> toReturn=new ArrayList<>();
			Object value=invocation.getParameterInstances().get(TO_NORMALIZE_COLUMNS.getIdentifier());
			if(value instanceof Iterable){
				for(ColumnReference ref:(Iterable<ColumnReference>)value)
					toReturn.add(ref);
			}else toReturn.add((ColumnReference) value);
			return toReturn;
		}catch(Exception e){
			throw new InvalidInvocationException(invocation, e);
		}
	}

	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		performCustomChecks(invocation);
		ArrayList<Column> toNormalize=new ArrayList<>();
		Table target=cubeManager.getTable(invocation.getTargetTableId());
		for(ColumnReference ref:getColumns(invocation)){
			toNormalize.add(target.getColumnById(ref.getColumnId()));
		}
		return String.format("Normalize %s",OperationHelper.getColumnLabelsSnippet(toNormalize));
	}
	
	
	@Override
	protected String getOperationName() {
		return "Normalize";
	}

	@Override
	protected String getOperationDescription() {
		return "Normalize columns";
	}

	@Override
	protected List<Parameter> getParameters() {
		return params;
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
}
