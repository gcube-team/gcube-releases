package org.gcube.data.analysis.tabulardata.operation.time.transformation;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.time.ChangeToTimeDimensionTest;

public class MonthValid extends ChangeToTimeDimensionTest {

	@Override
	public PeriodType getTestPeriodType() {
		return PeriodType.MONTH;
	}

	@Override
	public int getTestColumnIndex() {
		return 5;
	}

}
