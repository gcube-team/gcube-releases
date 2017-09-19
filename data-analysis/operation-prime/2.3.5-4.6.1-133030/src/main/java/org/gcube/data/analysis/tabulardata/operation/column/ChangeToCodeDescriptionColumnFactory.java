package org.gcube.data.analysis.tabulardata.operation.column;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;

@Singleton
public class ChangeToCodeDescriptionColumnFactory extends SimpleTextTypedColumnTypeTransformationFactory {

	private static final OperationId OPERATION_ID = new OperationId(2005);
	private static final CodeDescriptionColumnType MANAGED_COLUMN_TYPE = new CodeDescriptionColumnType();

	
	
	@Inject
	public ChangeToCodeDescriptionColumnFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		super(cubeManager, connectionProvider);		
	}

	@Override
	protected ColumnType getManagedColumnType() {
		return MANAGED_COLUMN_TYPE;
	}
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	
	
}
