package org.gcube.data.analysis.tabulardata.operation.time;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.TimeDimensionColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public abstract class TimeDimensionValidatorTest extends OperationTester<TimeDimensionColumnValidatorFactory> {

	@Inject
	TimeDimensionColumnValidatorFactory factory;
	
	@Inject
	GenericHelper helper;
	
	Table testTable;
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}
	
	@Before
	public void setupTestTable(){
		testTable = helper.createTimePeriodTable();
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(TimeDimensionColumnValidatorFactory.PERIOD_FORMAT_PARAMETER.getIdentifier(), getTargetPeriodType().getName());
		return params;
	}

	protected abstract PeriodType getTargetPeriodType();

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(getTargetColumnIndex()).getLocalId();
	}

	protected abstract int getTargetColumnIndex();

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}
