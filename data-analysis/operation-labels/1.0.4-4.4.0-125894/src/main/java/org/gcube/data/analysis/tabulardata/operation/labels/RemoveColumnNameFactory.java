package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnMetadataWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;

@Singleton
public class RemoveColumnNameFactory extends ColumnMetadataWorkerFactory {

	private static final OperationId OPERATION_ID = new OperationId(1007);
	
	CubeManager cubeManager;
	
	private static List<Parameter> parameters = new ArrayList<Parameter>();

	public static LocalizedTextParameter NAME_LABEL_PARAMETER = new LocalizedTextParameter("NAME_PARAMETER_ID",
			"Column name", "The column name to remove", Cardinality.ONE);

	
	static {
		parameters.add(NAME_LABEL_PARAMETER);
	}
	
	
	@Inject
	public RemoveColumnNameFactory(CubeManager cubeManager) {
		super();
		this.cubeManager = cubeManager;
	}

	@Override
	public MetadataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);	
		checkExistingColumnLabel(invocation);
		return new RemoveColumnName(invocation, cubeManager);
	}

	@Override
	protected String getOperationName() {
		return "Remove column name";
	}

	@Override
	protected String getOperationDescription() {
		return "Removes a name label from a column";
	}


	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	
	private void checkExistingColumnLabel(OperationInvocation invocation)throws InvalidInvocationException{
		try {
			NamesMetadata meta=cubeManager.getTable(invocation.getTargetTableId()).
					getColumnById(invocation.getTargetColumnId()).getMetadata(NamesMetadata.class);
			if(!meta.getTexts().contains(retrieveNameToRemove(invocation))) throw new InvalidInvocationException(invocation,"Specified label doesn't exist for selected column");
		} catch (NoSuchColumnException e) {
			throw new InvalidInvocationException(invocation,"Provided target column id does not exist");
		} catch (NoSuchTableException e) {
			throw new InvalidInvocationException(invocation,"Provided target table id does not exist");
		} catch (NoSuchMetadataException e) {
			throw new InvalidInvocationException(invocation,"No Names Metadata associated for selected column");
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
		checkExistingColumnLabel(invocation);
		LocalizedText text=OperationHelper.getParameter(NAME_LABEL_PARAMETER, invocation);
		Column col=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		return String.format("Remove label %s [%s] from %s",text.getValue(),text.getLocale(),OperationHelper.retrieveColumnLabel(col));
	}
}
