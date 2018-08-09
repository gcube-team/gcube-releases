package org.gcube.data.analysis.tabulardata.operation.time;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.column.ChangeToTimeDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.TimeDimensionColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public abstract class ChangeToTimeDimensionTest extends OperationTester<ChangeToTimeDimensionColumnFactory> {

	@Inject
	private ChangeToTimeDimensionColumnFactory factory;
	
	@Inject
	private GenericHelper helper;
	
	private Table testTable;
	
	@Before
	public void setupTestTable(){
		testTable = helper.createTimePeriodTable();
	}

	@Override
	protected WorkerFactory<?> getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put(TimeDimensionColumnValidatorFactory.PERIOD_FORMAT_PARAMETER.getIdentifier(), getTestPeriodType().getName());
		parameters.put(TimeDimensionColumnValidatorFactory.FORMAT_ID_PARAMETER.getIdentifier(), getTestPeriodType().getAcceptedFormats().get(0).getId() );
		return parameters;
	}

	public abstract PeriodType getTestPeriodType() ;

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(getTestColumnIndex()).getLocalId();
	}

	public abstract int getTestColumnIndex();

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}
