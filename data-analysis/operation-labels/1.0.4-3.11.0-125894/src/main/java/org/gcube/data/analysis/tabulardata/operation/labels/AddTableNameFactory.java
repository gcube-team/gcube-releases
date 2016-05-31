package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableMetadataWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;

@Singleton
public class AddTableNameFactory extends TableMetadataWorkerFactory {

	private static final OperationId OPERATION_ID = new OperationId(1008);
	
	CubeManager cubeManager;

	private static List<Parameter> parameters = new ArrayList<Parameter>();

	public static LocalizedTextParameter NAME_LABEL_PARAMETER = new LocalizedTextParameter("NAME_PARAMETER_ID",
			"Table name", "The table name to set", Cardinality.ONE);

	static {
		parameters.add(NAME_LABEL_PARAMETER);
	}

	@Inject
	public AddTableNameFactory(CubeManager cubeManager) {
		this.cubeManager = cubeManager;
	}

	@Override
	public MetadataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);				
		return new AddTableName(cubeManager, invocation);
	}

	@Override
	protected String getOperationName() {
		return "Add table name";
	}

	@Override
	protected String getOperationDescription() {
		return "Add a name label to a table";
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
		LocalizedText text=OperationHelper.getParameter(NAME_LABEL_PARAMETER, invocation);		
		return String.format("Add label %s [%s]",text.getValue(),text.getLocale());
	}
}
