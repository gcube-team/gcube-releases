package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetTableParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class UnionFactory extends TableTransformationWorkerFactory{

	private static final OperationId OPERATION_ID=new OperationId(3208);

	public static final TargetColumnParameter SOURCE_COLUMN_PARAMETER=new TargetColumnParameter("source", "Source", "Source column", Cardinality.ONE);
	public static final TargetColumnParameter TARGET_COLUMN_PARAMETER=new TargetColumnParameter("target", "Target", "Target Column", Cardinality.ONE);

	public static final CompositeParameter MAPPINGS_PARAMETER=new CompositeParameter(
			"mappings", "Mappings", "Mappings betweeen target and source table columns", new Cardinality(0, Integer.MAX_VALUE),
			Arrays.asList(new Parameter[]{SOURCE_COLUMN_PARAMETER,TARGET_COLUMN_PARAMETER}));

	public static final TargetTableParameter TABLE_PARAMETER = new TargetTableParameter("table", "Table", "target table for union", Cardinality.OPTIONAL);

	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;

	@Inject
	public UnionFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory evaluatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory=evaluatorFactory;
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		checkMappings(invocation);
		return new UnionWorker(invocation,cubeManager,connectionProvider,evaluatorFactory);
	}

	@Override
	protected String getOperationDescription() {
		return "Import data from a compatible table";
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected String getOperationName() {
		return "Union";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Collections.singletonList((Parameter)MAPPINGS_PARAMETER);
	}


	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		ColumnReference ref=(ColumnReference) getMappings(invocation, cubeManager).get(0).get(SOURCE_COLUMN_PARAMETER.getIdentifier());
		Table sourceTable=cubeManager.getTable(ref.getTableId());
		return String.format("Import %s rows",OperationHelper.retrieveTableLabel(sourceTable));
	}

	@SuppressWarnings("unchecked")
	private void checkMappings(OperationInvocation invocation)throws InvalidInvocationException{
		List<Map<String,Object>> mappings=getMappings(invocation, cubeManager);
		Table source=null;
		Table target=cubeManager.getTable(invocation.getTargetTableId());
		List<ColumnLocalId> mappedTargetColumns=new ArrayList<>();


		for(Map<String,Object> mapping:mappings){
			// all source columns must belong to same table
			ColumnReference sourceRef=(ColumnReference) mapping.get(SOURCE_COLUMN_PARAMETER.getIdentifier());
			if(source==null)source=cubeManager.getTable(sourceRef.getTableId());
			else {
				if(!sourceRef.getTableId().equals(source.getId())) throw new InvalidInvocationException(invocation, String.format("Incoherent source table id %s, expected %s",sourceRef.getTableId(),source.getId()));
			}
			// all target columns must belong to target table
			ColumnReference targetRef=(ColumnReference) mapping.get(TARGET_COLUMN_PARAMETER.getIdentifier());
			if(!targetRef.getTableId().equals(target.getId())) throw new InvalidInvocationException(invocation, String.format("Incoherent target table id %s, expected %s",targetRef.getTableId(),target.getId()));
			// all source-target columns must have compatible data types

			Column sourceCol=source.getColumnById(sourceRef.getColumnId());		
			DataType sourceType=sourceCol.getDataType();
			Column targetCol=target.getColumnById(targetRef.getColumnId());
			DataType targetType=targetCol.getDataType();
			if(!Cast.isCastSupported(sourceType, targetType)) 
				throw new InvalidInvocationException(invocation,String.format("Cannot map %s values to %s column.", OperationHelper.retrieveColumnLabel(sourceCol),OperationHelper.retrieveColumnLabel(targetCol)));


			// Dimensions must belong to same CL
			if(targetCol.getColumnType() instanceof DimensionColumnType){
				if(!(sourceCol.getColumnType() instanceof DimensionColumnType))
					throw new InvalidInvocationException(invocation, String.format("Dimension %s must be mapped with a similar dimension.",OperationHelper.retrieveColumnLabel(targetCol)));
				else if(!targetCol.getRelationship().getTargetTableId().equals(sourceCol.getRelationship().getTargetTableId()))
					throw new InvalidInvocationException(invocation, String.format("Dimensions %s and %s must point to same codelist.",OperationHelper.retrieveColumnLabel(targetCol),OperationHelper.retrieveColumnLabel(sourceCol)));

			}

			if(targetCol.getColumnType() instanceof TimeDimensionColumnType){
				if(!(sourceCol.getColumnType() instanceof TimeDimensionColumnType))
					throw new InvalidInvocationException(invocation, String.format("Time Dimension %s must be mapped with a similar dimension.",OperationHelper.retrieveColumnLabel(targetCol)));
				else if(!targetCol.getRelationship().getTargetTableId().equals(sourceCol.getRelationship().getTargetTableId()))
					throw new InvalidInvocationException(invocation, String.format("Time Dimensions %s and %s must point to same codelist.",OperationHelper.retrieveColumnLabel(targetCol),OperationHelper.retrieveColumnLabel(sourceCol)));

			}

			mappedTargetColumns.add(targetCol.getLocalId());			
		}

		for(Column col : target.getColumnsByType(DimensionColumnType.class,TimeDimensionColumnType.class)){
			if(!mappedTargetColumns.contains(col.getLocalId())){
				throw new InvalidInvocationException(invocation,String.format("%s column %s must be mapped", col.getColumnType().getName(),OperationHelper.retrieveColumnLabel(col)));
			}
		}
	}

	protected static TableId getTableParameter(OperationInvocation invocation){
		return (TableId)invocation.getParameterInstances().get(TABLE_PARAMETER.getIdentifier());
	}

	@SuppressWarnings("unchecked")
	protected static List<Map<String,Object>> getMappings(OperationInvocation invocation, CubeManager cubeManager){		
		Object actualParam=invocation.getParameterInstances().get(MAPPINGS_PARAMETER.getIdentifier());
		if (actualParam!=null){
			if(actualParam instanceof Map){
				// one param
				return Collections.singletonList((Map<String, Object>) actualParam);
			}else{
				Iterable<Map<String,Object>> it=(Iterable<Map<String, Object>>) actualParam;
				ArrayList<Map<String,Object>> toReturn=new ArrayList<Map<String,Object>>();
				for(Map<String,Object> elem:it)
					toReturn.add(elem);
				return toReturn;

			}		
		} else {
			TableId sourceId = getTableParameter(invocation);
			Table source = cubeManager.getTable(sourceId);
			Table target = cubeManager.getTable(invocation.getTargetTableId());
			List<Column> targetColumns = target.getColumnsExceptTypes(IdColumnType.class, ValidationColumnType.class);
			List<Column> sourceColumns = source.getColumnsExceptTypes(IdColumnType.class, ValidationColumnType.class);

			if (targetColumns.size()!= sourceColumns.size())
				return Collections.emptyList();

			ArrayList<Map<String,Object>> toReturn=new ArrayList<Map<String,Object>>();
			for (int i =0; i<targetColumns.size(); i++){
				Column targetColumn = targetColumns.get(i);
				Column sourceColumn = sourceColumns.get(i);
				Map<String, Object> singleEntryMap = new HashMap<String, Object>();
				singleEntryMap.put(TARGET_COLUMN_PARAMETER.getIdentifier(), new ColumnReference(target.getId(), targetColumn.getLocalId()));
				singleEntryMap.put(SOURCE_COLUMN_PARAMETER.getIdentifier(), new ColumnReference(source.getId(), sourceColumn.getLocalId()));
				toReturn.add(singleEntryMap);
			}
			return toReturn;
		}
	}

	@Override
	public boolean isRollbackable() {
		return true;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new UnionRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}
}
