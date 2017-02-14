package org.gcube.data.analysis.tabulardata.operation.time.transformation;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.time.ChangeToTimeDimensionTest;

public class DayWithNulls extends ChangeToTimeDimensionTest{

	@Override
	public PeriodType getTestPeriodType() {
		return PeriodType.DAY;
	}

	@Override
	public int getTestColumnIndex() {
		return 11;
	}

}
