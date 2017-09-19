package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;

@Singleton
public class ChangeToAnnotationColumnFactory extends SimpleTextTypedColumnTypeTransformationFactory  {

		
	
	public static final AnnotationColumnType MANAGED_COLUMN_TYPE = new AnnotationColumnType();

	private static final OperationId OPERATION_ID = new OperationId(2000);
	
	
	@Inject
	public ChangeToAnnotationColumnFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		super(cubeManager, connectionProvider);
	}

	@Override
	protected ColumnType getManagedColumnType() {
		return MANAGED_COLUMN_TYPE;
	}

	@Override
	protected List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
}
