package org.gcube.data.analysis.tabulardata.operation.sdmx;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class SDMXCodelistExportTests extends OperationTester<SDMXCodelistExporterFactory> {

	@Inject
	SDMXCodelistExporterFactory exporterFactory;

	@Inject
	SDMXCodelistImporterFactory importerFactory;

	Table testTable;

	private static Map<String, Object> parameters = new HashMap<String, Object>();

	static {
		parameters.put("registryBaseUrl", "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest");
		parameters.put("agency", "SDMX");
		parameters.put("id", "NEW_CL_DIVISION");
		parameters.put("version", "2.0");
	}

	@Before
	public void before() throws Exception {
		createTestTable();
	}

	private void createTestTable() throws InvalidInvocationException {
		Map<String, Object> tableCreationParameters = new HashMap<String, Object>();
		tableCreationParameters.put("registryBaseUrl", "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
		tableCreationParameters.put("agency", "FAO");
		tableCreationParameters.put("id", "CL_DIVISION");
		tableCreationParameters.put("version", "0.1");
		OperationInvocation invocation = InvocationCreator.getCreator(importerFactory.getOperationDescriptor())
				.setParameters(tableCreationParameters).create();
		Worker<WorkerResult> worker = importerFactory.createWorker(invocation);
		try {
			worker.run();
			Assert.assertNotNull(worker.getResult().getResultTable());
			testTable = worker.getResult().getResultTable();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected WorkerFactory<ResourceCreatorWorker> getFactory() {
		return exporterFactory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		return SDMXCodelistExportTests.parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
}
