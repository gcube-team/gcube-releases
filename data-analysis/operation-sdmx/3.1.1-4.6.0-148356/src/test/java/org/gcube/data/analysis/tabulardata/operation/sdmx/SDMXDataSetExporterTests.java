package org.gcube.data.analysis.tabulardata.operation.sdmx;

import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.config.ProxyAuthenticator;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.DataStructureDefinitionWorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.SDMXDataStructureDefinitionExporterFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class SDMXDataSetExporterTests extends OperationTester<SDMXDataStructureDefinitionExporterFactory> {

	@Inject
	SDMXDataStructureDefinitionExporterFactory exporterFactory;

	Table testTable;

	@Inject
	CodelistHelper codelistHelper;
	
	@Inject
	DatasetHelper datasetHelper;
	
	
	private static Map<String, Object> parameters = new HashMap<String, Object>();

	static {
		parameters.put("registryBaseUrl", "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest");
		parameters.put("agency", "ENG");
		parameters.put("id", "NEW_DSD_DIVISION");
		parameters.put("version", "2.0");

	}

	@Before
	public void before() throws Exception {
		createTestTable();
	}

	private void createTestTable() throws InvalidInvocationException {

		ProxyAuthenticator authenticator = new ProxyAuthenticator();
		authenticator.setProxyHost("proxy.eng.it");
		authenticator.setProxyPort("3128");
		authenticator.setProxyUserName("username");
		authenticator.setProxyPassword("password");
		authenticator.configure();
		if (authenticator.isActive()) Authenticator.setDefault(authenticator);
		
		this.testTable = this.datasetHelper.createSampleDataset(this.codelistHelper.createSpeciesCodelist());
		
		parameters.put(DataStructureDefinitionWorkerUtils.OBS_VALUE_COLUMN, this.testTable.getColumnsByType(MeasureColumnType.class).get(0).getLocalId().getValue());

	}

	@Override
	protected WorkerFactory<ResourceCreatorWorker> getFactory() {
		return exporterFactory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		return SDMXDataSetExporterTests.parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return this.testTable.getId();
	}
}
