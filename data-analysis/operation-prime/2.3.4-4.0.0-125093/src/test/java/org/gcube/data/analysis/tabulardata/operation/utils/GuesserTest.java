package org.gcube.data.analysis.tabulardata.operation.utils;

import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class GuesserTest extends OperationTester<GuesserWorkerFactory>{

	@Inject
	private GenericHelper genericHelper;

	@Inject
	private GuesserWorkerFactory factory;

	private Table testTable;
	
	private Table codelistTable;

	@Before
	public void before() {
		testTable = genericHelper.createDatasetWithSpecies();
		codelistTable = genericHelper.createSpeciesGenericTable();
	}
	
	@Override
	protected WorkerFactory<?> getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		return Collections.singletonMap(GuesserWorkerFactory.CODELISTS_PARAMETER.getIdentifier(),(Object) Collections.singletonList(Integer.parseInt(codelistTable.getId().getValue()+"") ));
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(3).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

	
}
