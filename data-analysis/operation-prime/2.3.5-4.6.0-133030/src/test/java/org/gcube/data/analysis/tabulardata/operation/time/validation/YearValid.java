package org.gcube.data.analysis.tabulardata.operation.time.validation;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.time.TimeDimensionValidatorTest;

public class YearValid extends TimeDimensionValidatorTest {

	@Override
	protected PeriodType getTargetPeriodType() {
		return PeriodType.YEAR;
	}

	@Override
	protected int getTargetColumnIndex() {
		return 9;
	}

}
