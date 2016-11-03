package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableMetadataWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;

@Singleton
public class AddColumnNameFactory extends TableMetadataWorkerFactory {

	private static final OperationId OPERATION_ID = new OperationId(1006);
	
	CubeManager cubeManager;
	
	private static List<Parameter> parameters = new ArrayList<Parameter>();

	public static MapParameter NAME_LABEL_PARAMETER = new MapParameter("NAME_PARAMETER_ID",
			"mapNameColumns", "The map (ColumnReference -> LocalizedName) to set", Cardinality.ONE, ColumnReference.class, LocalizedText.class);

	
	
	static {
		parameters.add(NAME_LABEL_PARAMETER);
	}
	
	
	@Inject
	public AddColumnNameFactory(CubeManager cubeManager) {		
		this.cubeManager = cubeManager;
	}


	@Override
	public MetadataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);		
		return new AddColumnName(cubeManager, invocation);
	}

	@Override
	protected String getOperationName() {
		return "Add column name";
	}

	@Override
	protected String getOperationDescription() {
		return "Add a name label to a column";
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
		@SuppressWarnings("unchecked")
		Map<ColumnReference, LocalizedText> mapParameter=OperationHelper.getParameter(NAME_LABEL_PARAMETER, invocation);
		StringBuilder labelsReport = new StringBuilder();
		for (Entry<ColumnReference, LocalizedText> entry : mapParameter.entrySet()){
			Column col=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(entry.getKey().getColumnId());
			LocalizedText text = entry.getValue();
			labelsReport.append(String.format(" %s [%s] to %s ,",text.getValue(),text.getLocale(),OperationHelper.retrieveColumnLabel(col)));
		}
		labelsReport.deleteCharAt(labelsReport.lastIndexOf(","));		
		return String.format("Setting labels: %s",labelsReport.toString() );
	}
}
