package org.gcube.data.analysis.tabulardata.operation.view.charts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnResourceCreatorWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.IntegerParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemover;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

public class TopRatingChartCreatorFactory extends ColumnResourceCreatorWorkerFactory {

	protected static final Parameter SAMPLE_SIZE = new IntegerParameter("sampleSize", "Sample size", "the sample size", Cardinality.ONE);
	
	protected static final Parameter VALUE_OP = new MultivaluedStringParameter("valueOperation", "Value operation", "operation to apply to values", Cardinality.ONE, Arrays.asList("AVG","SUM", "MAX", "MIN"));
	
	@Inject
	private CubeManager cubeManager;
	
	@Inject
	private DatabaseConnectionProvider connectionProvider;
	
	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		return new TopRatingChartCreatorWorker(invocation, cubeManager, connectionProvider);
	}

	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}

	@Override
	protected String getOperationName() {
		return "TopRatingChart";
	}

	@Override
	protected String getOperationDescription() {
		return "Creates a set of charts on a dataset";
	}

	@Override
	protected OperationType getOperationType() {
		return OperationType.RESOURCECREATOR;
	}

	@Override
	protected List<Parameter> getParameters() {
		return Arrays.asList(SAMPLE_SIZE, VALUE_OP);
	}

	@Override
	protected OperationId getOperationId() {
		return new OperationId(9000);
	}

	@Override
	public List<ColumnType> getAllowedColumnTypes() {
		return Collections.singletonList((ColumnType)new DimensionColumnType());
	}

	@Override
	public List<TableType> getAllowedTableTypes() {
		return Collections.singletonList((TableType)new DatasetTableType());
	}

	@Override
	public ResourceRemover getResourceRemover() {
		return TopRatingChartCreatorWorker.StorageRemover.getInstance();
	}

	
}
