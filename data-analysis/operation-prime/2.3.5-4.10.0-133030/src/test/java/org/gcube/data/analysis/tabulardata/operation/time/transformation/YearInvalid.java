package org.gcube.data.analysis.tabulardata.operation.time.transformation;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.time.ChangeToTimeDimensionTest;

public class YearInvalid extends ChangeToTimeDimensionTest {

	@Override
	public PeriodType getTestPeriodType() {
		return PeriodType.YEAR;
	}

	@Override
	public int getTestColumnIndex() {
		return 10;
	}

}
