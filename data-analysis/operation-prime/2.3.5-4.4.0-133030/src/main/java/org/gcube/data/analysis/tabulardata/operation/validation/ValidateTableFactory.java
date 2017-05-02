package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.table.ChangeTableTypeFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class ValidateTableFactory extends TableValidatorFactory {

	
	private static final OperationId OPERATION_ID = new OperationId(5011);
	
	private static final List<Parameter> parameters = new ArrayList<Parameter>();
	
	static{
		parameters.add(new MultivaluedStringParameter(ChangeTableTypeFactory.TABLE_TYPE_PARAMETER.getIdentifier(), "tableType", "optional table type", Cardinality.OPTIONAL, ChangeTableTypeFactory.TABLE_TYPE_PARAMETER.getAdmittedValues()));
	}
	
	@Inject
	private CubeManager cubeManager;
	@Inject
	private CodelistValidatorFactory codelistFactory;
	@Inject
	private ValidateDatasetFactory datasetFactory;
	@Inject 
	private ValidateGenericFactory genericFactory;
	
	@Override
	public ValidationWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cubeManager);
		Table table=cubeManager.getTable(arg0.getTargetTableId());
		TableType type=table.getTableType();
		
		if(arg0.getParameterInstances().containsKey(ChangeTableTypeFactory.TABLE_TYPE_PARAMETER.getIdentifier())){
			type=ChangeTableTypeFactory.getTableType(OperationHelper.getParameter(ChangeTableTypeFactory.TABLE_TYPE_PARAMETER, arg0));
		}
		
		if(type.equals(new CodelistTableType())||type.equals(new HierarchicalCodelistTableType()) || type.equals(new TimeCodelistTableType())) 
			return codelistFactory.createWorker(arg0);
		else if(type.equals(new DatasetTableType()))
			return datasetFactory.createWorker(arg0);
		else return genericFactory.createWorker(arg0);
	}

	@Override
	protected String getOperationDescription() {
		return "Perform validations based on current table type";
	}

	@Override
	protected String getOperationName() {
		return "Validate Table";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		TableType type=targetTable.getTableType();
		if(invocation.getParameterInstances().containsKey(ChangeTableTypeFactory.TABLE_TYPE_PARAMETER.getIdentifier())){
			type=ChangeTableTypeFactory.getTableType(OperationHelper.getParameter(ChangeTableTypeFactory.TABLE_TYPE_PARAMETER, invocation));
		}
		return String.format("Check if %s is a valid %s.",OperationHelper.retrieveTableLabel(targetTable),type.getName());
	}
}
