package org.gcube.data.analysis.tabulardata.operation.view;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.view.charts.TopRatingChartCreatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ChartCreatorTest extends OperationTester<TopRatingChartCreatorFactory>{

	@Inject
	TopRatingChartCreatorFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;
	
	@Inject
	DatasetHelper datasetHelper;

	private static Table testCodelist;

	private static Table testDataset;
	
	@Before
	public void setupTestTables(){
		testCodelist = codelistHelper.createSpeciesCodelist();
		testDataset = datasetHelper.createSampleDataset(testCodelist);
	}
	
	@Override
	protected WorkerFactory<?> getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("sampleSize", 25);
		parameterMap.put("valueOperation", "SUM");
		return parameterMap;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testDataset.getColumnsByType(new DimensionColumnType()).get(0).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testDataset.getId();
	}

}
