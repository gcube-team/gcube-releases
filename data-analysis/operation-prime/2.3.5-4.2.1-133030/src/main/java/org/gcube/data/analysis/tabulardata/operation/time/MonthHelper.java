package org.gcube.data.analysis.tabulardata.operation.time;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

@Singleton
public class MonthHelper extends PeriodTypeHelper {

	@Inject
	public MonthHelper(CubeManager cubeManager) {
		super(cubeManager);
	}

	@Override
	public PeriodType getManagedPeriodType() {
		return PeriodType.MONTH;
	}
	

}
