package org.gcube.data.analysis.tabulardata.operation.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateTableFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class ChangeTableTypeFactory extends TableTransformationWorkerFactory {

	
	public static MultivaluedStringParameter TABLE_TYPE_PARAMETER;
	
	private static final OperationId OPERATION_ID = new OperationId(1002);

	private final static Map<String,TableType> availableTableTypes = new HashMap<>();


	
	private static List<Parameter> parameters;
	
	static {
		availableTableTypes.put(new GenericTableType().getName(),new GenericTableType());
		availableTableTypes.put(new DatasetTableType().getName(),new DatasetTableType());
		availableTableTypes.put(new CodelistTableType().getName(),new CodelistTableType());
		
		
		TABLE_TYPE_PARAMETER=new MultivaluedStringParameter("tableType", "Table Type",
				"Table type", Cardinality.ONE, new ArrayList<String>(availableTableTypes.keySet()));
		
		parameters=Collections.singletonList((Parameter)TABLE_TYPE_PARAMETER);
	}

	@Inject
	private CubeManager cubeManager;

	@Inject 
	private ValidateTableFactory validationFactory;
	
	

	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);	
		Table targetTable = cubeManager.getTable(invocation.getTargetTableId());
		String targetTableTypeName = OperationHelper.getParameter(TABLE_TYPE_PARAMETER, invocation);
		TableType targetTableType = availableTableTypes.get(targetTableTypeName);
		return new ChangeTableType(invocation, cubeManager, targetTable, targetTableType);
	}

	public static  TableType getTableType(String targetTableTypeName) {
		return availableTableTypes.get(targetTableTypeName);
	}



	@Override
	protected String getOperationName() {
		return "Change table type";
	}

	@Override
	protected String getOperationDescription() {
		return "Modify the table type";
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	
	@Override
	public List<WorkerFactory<ValidationWorker>> getPrecoditionValidations() {
		return Collections.singletonList((WorkerFactory<ValidationWorker>) validationFactory);
	}
	
	@Override
	public String describeInvocation(OperationInvocation arg0)
			throws InvalidInvocationException {
		String targetTypeName=OperationHelper.getParameter(TABLE_TYPE_PARAMETER, arg0);		
		return String.format("Set table as %s",targetTypeName);
	}
}
