package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ColumnTypeCastValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

import com.google.common.collect.Lists;

@Singleton
public abstract class SimpleTextTypedColumnTypeTransformationFactory extends ChangeColumnTypeTransformationFactory {

	public final static List<ColumnType> allowedSourceColumnTypes = Lists.newArrayList();

	private static final List<Parameter> parameters=new ArrayList<>();
	
	static {
		allowedSourceColumnTypes.add(new AnnotationColumnType());
		allowedSourceColumnTypes.add(new MeasureColumnType());
		allowedSourceColumnTypes.add(new AttributeColumnType());
		allowedSourceColumnTypes.add(new CodeColumnType());
		allowedSourceColumnTypes.add(new CodeNameColumnType());
		allowedSourceColumnTypes.add(new CodeDescriptionColumnType());
		
		parameters.add(ADDITIONAL_META_PARAMETER);
	}

	private DatabaseConnectionProvider connectionProvider;
	
	protected CubeManager cubeManager;

	public SimpleTextTypedColumnTypeTransformationFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		invocation.getParameterInstances().put(ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER.getIdentifier(), new TextType() );
		return new SimpleTextTypedColumnTypeTransformation(invocation, cubeManager, connectionProvider, getManagedColumnType());
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

	protected List<ColumnType> getAllowedSourceColumnTypes() {
		return allowedSourceColumnTypes;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
	@Override
	public String describeInvocation(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0,cubeManager);
		checkAllowedColumnTypeTransition(arg0, cubeManager);
		Column targetColumn=cubeManager.getTable(arg0.getTargetTableId()).getColumnById(arg0.getTargetColumnId());		
		return String.format("Set %s as %s [%s]",OperationHelper.retrieveColumnLabel(targetColumn),getManagedColumnType().getName(),new TextType());
	}
}
