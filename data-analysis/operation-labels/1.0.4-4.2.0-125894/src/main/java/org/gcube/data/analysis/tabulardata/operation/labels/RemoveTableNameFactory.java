package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
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
public class RemoveTableNameFactory extends TableMetadataWorkerFactory{

	private static final OperationId OPERATION_ID = new OperationId(1009);
	
	CubeManager cubeManager;

	private static List<Parameter> parameters = new ArrayList<Parameter>();

	public static LocalizedTextParameter NAME_LABEL_PARAMETER = new LocalizedTextParameter("NAME_PARAMETER_ID",
			"Table name", "The table name to remove", Cardinality.ONE);

	static {
		parameters.add(NAME_LABEL_PARAMETER);
	}

	@Inject
	public RemoveTableNameFactory(CubeManager cubeManager) {
		super();
		this.cubeManager = cubeManager;
	}
	
	@Override
	public MetadataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);		
		checkExistingTableName(invocation);
		return new RemoveTableName(invocation, cubeManager);
	}

	@Override
	protected String getOperationName() {
		return "Remove table name";
	}

	@Override
	protected String getOperationDescription() {
		return "Removes the label from a table";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
	private void checkExistingTableName(OperationInvocation invocation)throws InvalidInvocationException{
		try {
			NamesMetadata meta=cubeManager.getTable(invocation.getTargetTableId()).getMetadata(NamesMetadata.class);
			if(!meta.getTexts().contains(retrieveNameToRemove(invocation))) throw new InvalidInvocationException(invocation,"Specified label doesn't exist for selected table");
		} catch (NoSuchTableException e) {
			throw new InvalidInvocationException(invocation,"Provided target table id does not exist");
		} catch (NoSuchMetadataException e) {
			throw new InvalidInvocationException(invocation,"No Names Metadata associated for selected table");
		}
	}
	private LocalizedText retrieveNameToRemove(OperationInvocation invocation) {
		return (LocalizedText) invocation.getParameterInstances().get(NAME_LABEL_PARAMETER.getIdentifier());
	}
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkExistingTableName(invocation);
		LocalizedText text=OperationHelper.getParameter(NAME_LABEL_PARAMETER, invocation);		
		return String.format("Remove label %s [%s]",text.getValue(),text.getLocale());
	}
}
