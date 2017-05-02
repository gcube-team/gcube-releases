package org.gcube.data.analysis.tabulardata.operation.time.validation;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.time.TimeDimensionValidatorTest;

public class DayInvalid extends TimeDimensionValidatorTest{

	@Override
	protected PeriodType getTargetPeriodType() {
		return PeriodType.DAY;
	}

	@Override
	protected int getTargetColumnIndex() {
		return 2;
	}

}
