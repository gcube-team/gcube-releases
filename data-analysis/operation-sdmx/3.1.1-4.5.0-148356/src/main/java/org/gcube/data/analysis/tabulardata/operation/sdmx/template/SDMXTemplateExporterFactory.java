package org.gcube.data.analysis.tabulardata.operation.sdmx.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.VoidExportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class SDMXTemplateExporterFactory extends VoidExportWorkerFactory {

	private Logger logger;
	
	private static final OperationId operationId = new OperationId(204);

	private static final List<Parameter> parameters = new ArrayList<Parameter>();

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	static {
		parameters.add(new RegexpStringParameter(TemplateWorkerUtils.REGISTRY_BASE_URL, "Registry REST URL",
				"Target SDMX Registry REST Service base URL", Cardinality.ONE,
				"^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
		parameters.add(new RegexpStringParameter(TemplateWorkerUtils.AGENCY, "Agency", "SDMX Agency", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(TemplateWorkerUtils.ID, "Id", "SDMX DSD id", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(TemplateWorkerUtils.VERSION, "Version", "SDMX Data set version", Cardinality.ONE,
				"[0-9]+(\\.[0-9]+)?"));
		parameters.add(new RegexpStringParameter(TemplateWorkerUtils.OBS_VALUE_COLUMN, "Observation Value", "Observation Value column", Cardinality.ONE,
				"[0-9a-z-]+"));

		parameters.add(new MapParameter(TemplateWorkerUtils.TEMPLATE,  "Template", "Template", Cardinality.ONE, String.class, Template.class)); 
				
//		parameters.add(new TargetTemplateParameter(TemplateWorkerUtils.TEMPLATE, "Template", "Template", Cardinality.ONE,Arrays.asList(TemplateCategory.DATASET)));
		
	}

	@Inject
	public SDMXTemplateExporterFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}


	private void checkTargetTemplateEligibility(Map<String,Template> templateMap,OperationInvocation invocation) throws InvalidInvocationException {
		this.logger.debug("Checking template eligibility...");
		
		if (templateMap == null || templateMap.get(TemplateWorkerUtils.TEMPLATE) == null) 
			throw new InvalidInvocationException(invocation,"Template not found");
		
		Template template =  templateMap.get(TemplateWorkerUtils.TEMPLATE);
		
		
		List<TemplateColumn<?>> templateColumns = template.getActualStructure();
		int measure = 0;
		int timeDimension = 0;
		Iterator<TemplateColumn<?>> templateColumnsIterator = templateColumns.iterator();
		
		while (templateColumnsIterator.hasNext() && (measure == 0 || timeDimension == 0))
		{
			TemplateColumn<?> templateColumn = templateColumnsIterator.next();
		
			logger.debug("Column category "+templateColumn.getColumnType());
			
			if (templateColumn.getColumnType() == ColumnCategory.MEASURE) measure = 1;
			else if (templateColumn.getColumnType() == ColumnCategory.TIMEDIMENSION) timeDimension = 2;
			
		}
		
		switch (measure+timeDimension)
		{
		case 0:
			this.logger.debug("Missing timeset and measure");
			throw new InvalidInvocationException(invocation,"Missing timeset and measure");
		case 1:
			this.logger.debug("Missing timeset");
			throw new InvalidInvocationException(invocation,"Missing timeset");
		case 2:
			this.logger.debug("Missing measure");
			throw new InvalidInvocationException(invocation,"Missing measure");
		default:
		}
		
		
	}

	
	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		this.logger.debug("Creating worker");
		Map<String, Template> templateMap = (Map<String, Template>) invocation.getParameterInstances().get(TemplateWorkerUtils.TEMPLATE);		
		performBaseChecks(invocation,cubeManager);
		checkTargetTemplateEligibility(templateMap,invocation);
		
		return new SDMXTemplateDefinitionExporter(templateMap.get(TemplateWorkerUtils.TEMPLATE),connectionProvider,invocation,cubeManager);
	}

	@Override
	protected String getOperationName() {
		return "Export a Template to SDMX registry";
	}

	@Override
	protected String getOperationDescription() {
		return "Retrieve the metadata of the Templateand exports them to the remote SDMX registry";
	}

	@Override
	protected OperationId getOperationId() {
		return operationId;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	



	

}
