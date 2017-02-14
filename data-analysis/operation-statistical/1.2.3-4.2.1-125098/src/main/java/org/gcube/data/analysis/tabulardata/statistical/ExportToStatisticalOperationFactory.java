package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.export.csv.exporter.CSVExportFactory;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ExportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

@Singleton
public class ExportToStatisticalOperationFactory extends ExportWorkerFactory{

	private static OperationId OPERATION_ID=new OperationId(10002);
	
	private static List<Parameter> parameters=new ArrayList<Parameter>();
	
	public static SimpleStringParameter toEscapeFieldNamesParam=new SimpleStringParameter("escapeField", "To Escape Fields", "Field name list to be escaped", new Cardinality(0, Integer.MAX_VALUE));
	
	static{
		parameters.add(StatisticalOperationFactory.USER);
		parameters.add(toEscapeFieldNamesParam);
	}
	
	
	
	private CSVExportFactory csvExportFactory;
	private CubeManager cubeManager;
	
	@Inject
	public ExportToStatisticalOperationFactory(
			CSVExportFactory csvExportFactory, CubeManager cubeManager) {
		super();
		this.csvExportFactory = csvExportFactory;
		this.cubeManager = cubeManager;
	}
	
	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		StatisticalManagerDataSpace dataSpace =null;
		try{
			dataSpace = Common.getSMDataSpace();
		}catch (Exception e){
			throw new InvalidInvocationException(invocation,Constants.SERVICE_NOT_FOUND,e);
		}
		return new ExportToStatisticalOperation(invocation, csvExportFactory, cubeManager, dataSpace);
	}
	
	@Override
	protected String getOperationName() {
		return "Export to Statistical";
	}

	@Override
	protected String getOperationDescription() {
		return "Export the target table to the user's dataspace of Statistical Manager";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	
}
