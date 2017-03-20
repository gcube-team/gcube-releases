package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnMetadataParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;

public abstract class ChangeColumnTypeTransformationFactory extends ColumnTransformationWorkerFactory {
	
	public static ColumnMetadataParameter ADDITIONAL_META_PARAMETER=new ColumnMetadataParameter("additionalMeta", "Additional Metadata", "Metadata to add to the column.", new Cardinality(0, Integer.MAX_VALUE));
	
	@Override
	protected String getOperationName() {
		return String.format("Change to %s column", getManagedColumnType().getName());
	}

	@Override
	protected String getOperationDescription() {
		return String.format("Change the column type to %1$s or modify a %1$s column", getManagedColumnType().getName());
	}

	protected abstract ColumnType getManagedColumnType();
	
	protected abstract List<ColumnType> getAllowedSourceColumnTypes();

	protected void checkAllowedColumnTypeTransition(OperationInvocation invocation,CubeManager cubeManager)throws InvalidInvocationException{
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		TableType tableType=targetTable.getTableType();
		if(!tableType.getAllowedColumnTypes().contains(getManagedColumnType())){
			throw new InvalidInvocationException(invocation, String.format("Column type %s is not allowed for table type %s. Allowed types are %s.",getManagedColumnType(),tableType,tableType.getAllowedColumnTypes()));
		}
		Column col=targetTable.getColumnById(invocation.getTargetColumnId());
		if(!getAllowedSourceColumnTypes().contains(col.getColumnType())){
			throw new InvalidInvocationException(invocation, String.format("Source column type %s is not allowed for transition to %s. Allowed types are %s.",col.getColumnType(),getManagedColumnType(),getAllowedSourceColumnTypes()));
		}
	}
	
	
}
