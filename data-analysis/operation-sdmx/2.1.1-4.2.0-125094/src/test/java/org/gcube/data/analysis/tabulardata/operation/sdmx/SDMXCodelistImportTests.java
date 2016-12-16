package org.gcube.data.analysis.tabulardata.operation.sdmx;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class SDMXCodelistImportTests extends OperationTester<SDMXCodelistImporterFactory> {

	@Inject
	SDMXCodelistImporterFactory factory;
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put("registryBaseUrl", "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
		parameterInstances.put("agency", "FAO");
		parameterInstances.put("id", "CL_DIVISION");
		parameterInstances.put("version", "0.1");
		return parameterInstances;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return null;
	}

	@Override
	protected WorkerFactory<DataWorker> getFactory() {
		return factory;
	}
		
}
