package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
@Singleton
public class DenormalizationFactory extends TableTransformationWorkerFactory {

	private static final OperationId OPERATION_ID=new OperationId(3005);
	
	public static final int MAX_COLUMN_COUNT=1600;
	
	
//	public static TargetColumnParameter VALUE_COLUMN=new TargetColumnParameter("value_column", "Value column", "Values to aggregate", Cardinality.ONE,
//			Arrays.asList(new GenericTableType(),new DatasetTableType()),Arrays.asList((ColumnType)new MeasureColumnType()));
//	public static TargetColumnParameter ATTRIBUTE_COLUMN=new TargetColumnParameter("attribute_column", "Attribute Column", "Column by which denormalize", Cardinality.ONE,
//			Arrays.asList(new GenericTableType(),new DatasetTableType()),Arrays.asList(new AttributeColumnType(),new AnnotationColumnType()));
	
	
	public static TargetColumnParameter VALUE_COLUMN=new TargetColumnParameter("value_column", "Value column", "Values to aggregate", Cardinality.ONE);
	public static TargetColumnParameter ATTRIBUTE_COLUMN=new TargetColumnParameter("attribute_column", "Attribute Column", "Column by which denormalize", Cardinality.ONE);
	public static TargetColumnParameter REFERRED_COLUMN=new TargetColumnParameter("ref_column", "Referred Column", "Labels for the selected dimension", Cardinality.OPTIONAL);
	
	private static List<Parameter> params=Arrays.asList((Parameter)VALUE_COLUMN,ATTRIBUTE_COLUMN,REFERRED_COLUMN); 
	
	
	private CubeManager cubeManager;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	private DatabaseConnectionProvider connProvider;
	
	
	
	@Inject
	public DenormalizationFactory(CubeManager cubeManager,
			SQLExpressionEvaluatorFactory evaluatorFactory,DatabaseConnectionProvider connProvider, 
			ValidateDataWithExpressionFactory validationFactory) {
		super();
		this.cubeManager = cubeManager;
		this.evaluatorFactory = evaluatorFactory;
		this.connProvider = connProvider;
	}

	@Override
	public DataWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cubeManager);
		performCustomChecks(arg0);
		if(!isCountValuesValid(arg0)) throw new InvalidInvocationException(arg0, 
				"Too many distinct values on selected attribute column, couldn't denormalize table (max column count allowed: "+MAX_COLUMN_COUNT+")");
		return new DenormalizationWorker(arg0, cubeManager, evaluatorFactory,connProvider);
	}

	
	private boolean isCountValuesValid(OperationInvocation inv) throws InvalidInvocationException{
		Table targetTable=cubeManager.getTable(inv.getTargetTableId());
		Column attribute=targetTable.getColumnById(OperationHelper.getParameter(ATTRIBUTE_COLUMN, inv.getParameterInstances()).getColumnId());
		
		try {
			return SQLHelper.getSpecificCount(connProvider, targetTable.getName(), "DISTINCT("+attribute.getName()+")" , "true")+
					targetTable.getColumns().size()<MAX_COLUMN_COUNT;
		} catch (SQLException e) {
			throw new InvalidInvocationException(inv, "Unexpected exception while getting values count",e);
		}
	}
	
	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		performBaseChecks(toDescribeInvocation, cubeManager);
		performCustomChecks(toDescribeInvocation);
		Table targetTable=cubeManager.getTable(toDescribeInvocation.getTargetTableId());
		return String.format("Denormalize %s by %s.", 
			OperationHelper.retrieveColumnLabel(
					targetTable.getColumnById(OperationHelper.getParameter(VALUE_COLUMN, toDescribeInvocation).getColumnId())),
					OperationHelper.retrieveColumnLabel(
							targetTable.getColumnById(OperationHelper.getParameter(ATTRIBUTE_COLUMN, toDescribeInvocation).getColumnId())));
	}
	
	private void performCustomChecks(OperationInvocation invocation) throws InvalidInvocationException{
		ColumnReference attributeRef=OperationHelper.getParameter(ATTRIBUTE_COLUMN, invocation);
		
		if(!attributeRef.getTableId().equals(invocation.getTargetTableId()))
			throw new InvalidInvocationException(invocation, "Attribute column must belong to target table");
		
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		Column attribute=targetTable.getColumnById(attributeRef.getColumnId());
		if(attribute.getColumnType() instanceof DimensionColumnType || attribute.getColumnType() instanceof TimeDimensionColumnType){
			if(invocation.getParameterInstances().containsKey(REFERRED_COLUMN.getIdentifier()))
				if(!OperationHelper.getParameter(REFERRED_COLUMN, invocation).getTableId().equals(attribute.getRelationship().getTargetTableId()))
					throw new InvalidInvocationException(invocation, "Selected referred column must belong to attribute's codelist");
		}
		
		
		if(!OperationHelper.getParameter(VALUE_COLUMN, invocation).getTableId().equals(invocation.getTargetTableId()))
			throw new InvalidInvocationException(invocation, "Value column must belong to target table");
		
		DataType quantityDataType=cubeManager.getTable(invocation.getTargetTableId()).
				getColumnById(OperationHelper.getParameter(VALUE_COLUMN, invocation).getColumnId()).getDataType();
		
		if(!(quantityDataType instanceof NumericType) && ! (quantityDataType instanceof IntegerType)){
			throw new InvalidInvocationException(invocation, "Unable to aggregate over "+quantityDataType.getName()+" data type");
		}
		
		
		
	}
	
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	

	@Override
	protected String getOperationDescription() {
		return "Denormalize a Table";
	}

	@Override
	protected String getOperationName() {
		return "Denormalization";
	}

	@Override
	protected List<Parameter> getParameters() {
		return params;
	}

	
	
	@Override
	public Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap() {
		// TODO Auto-generated method stub
		return super.getPreconditionValidationMap();
	}
	
	
}
