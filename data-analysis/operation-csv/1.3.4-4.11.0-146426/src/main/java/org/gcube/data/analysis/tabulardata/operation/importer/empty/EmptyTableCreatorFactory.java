package org.gcube.data.analysis.tabulardata.operation.importer.empty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ImportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.DataTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class EmptyTableCreatorFactory extends ImportWorkerFactory{

	public static final Parameter DATA_TYPE=new DataTypeParameter("dataType", "Data Type", "To set data type", Cardinality.ONE);
	public static final Parameter COLUMN_TYPE=new ColumnTypeParameter("columnType","Column type","The type of the new column",Cardinality.ONE);
	public static final Parameter LABEL= new LocalizedTextParameter("label", "label", "To set label", Cardinality.ONE);
	public static final Parameter RELATIONSHIP= new TargetColumnParameter("relationship", "relationship", "defines relationship to an external table", Cardinality.OPTIONAL);
	
	
	
	public static final Parameter PERIOD_TYPE= new MultivaluedStringParameter("periodtype", "period type", "defines period for a timedimension column", Cardinality.OPTIONAL,
			Arrays.asList(PeriodType.DAY.name(), PeriodType.MONTH.name(), PeriodType.YEAR.name()));
	
	public static final CompositeParameter COMPOSITE= new CompositeParameter("column", "column", "column definition", new Cardinality(1, Integer.MAX_VALUE), 
			Arrays.asList(DATA_TYPE, COLUMN_TYPE, LABEL));

	
	private CubeManager cubeManager;
	
	@Inject
	public EmptyTableCreatorFactory(CubeManager cubeManager) {
		if (cubeManager == null)
			throw new IllegalArgumentException("cubeManager cannot be null");
		this.cubeManager = cubeManager;
		
	}
	
	@Override
	protected OperationId getOperationId() {
		return new OperationId(103l);
	}



	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		return new EmptyTableCreatorWorker(invocation, cubeManager);
	}

	@Override
	protected String getOperationName() {
		return "Empty table creator";
	}

	@Override
	protected String getOperationDescription() {
		return "Creates an empty table with given columns";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Collections.singletonList((Parameter)COMPOSITE);
	}

}
