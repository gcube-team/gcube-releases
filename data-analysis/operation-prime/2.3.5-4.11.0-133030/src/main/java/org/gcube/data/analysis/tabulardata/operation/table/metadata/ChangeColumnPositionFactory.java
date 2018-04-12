package org.gcube.data.analysis.tabulardata.operation.table.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableMetadataWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;

public class ChangeColumnPositionFactory extends TableMetadataWorkerFactory{

	private static final OperationId OPERATION_ID=new OperationId(1011);
	
	public static final TargetColumnParameter COLUMN_ORDER= new TargetColumnParameter("order", "Column order", "The resulting column order", new Cardinality(2, Integer.MAX_VALUE),
			Arrays.asList(new TableType[]{new CodelistTableType(),
					new DatasetTableType(),new GenericTableType(),
					new HierarchicalCodelistTableType(),new TimeCodelistTableType()}),
					Arrays.asList(new ColumnType[]{
							new AnnotationColumnType(),new AttributeColumnType(),new CodeColumnType(), new CodeDescriptionColumnType(), new CodeNameColumnType(),
							new DimensionColumnType(),new MeasureColumnType(),new TimeDimensionColumnType()}));
	
	CubeManager cubeManager;
	
	@Inject
	public ChangeColumnPositionFactory(CubeManager cubeManager) {
		this.cubeManager = cubeManager;
	}

	@Override
	public MetadataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		internalCheck(invocation);
		return new ChangeColumnPositionWorker(cubeManager, invocation);
	}	
		
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected String getOperationName() {
		return "ChangeColumnPosition";
	}

	@Override
	protected String getOperationDescription() {
		return "Modifies the column position";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Collections.singletonList((Parameter)COLUMN_ORDER);
	}
	
	private void internalCheck(OperationInvocation invocation) throws InvalidInvocationException{
		Table table = cubeManager.getTable(invocation.getTargetTableId());
		List<ColumnReference> order=(List<ColumnReference>) invocation.getParameterInstances().get(COLUMN_ORDER.getIdentifier());
		List<Column> existing= table.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class);
		if(order.size()!=existing.size())
			throw new InvalidInvocationException(invocation, String.format("Specified order size %s must match existing column count %s",order.size(),existing.size()));
		
		for(ColumnReference ref:order){
			try{
				if(!ref.getTableId().equals(invocation.getTargetTableId())) throw new Exception();
				table.getColumnById(ref.getColumnId());
			}catch(Exception e){
				throw new InvalidInvocationException(invocation, String.format("Invalid column with ID %s found in specified order",ref.getColumnId()));
			}
		}		
	}
	

}
