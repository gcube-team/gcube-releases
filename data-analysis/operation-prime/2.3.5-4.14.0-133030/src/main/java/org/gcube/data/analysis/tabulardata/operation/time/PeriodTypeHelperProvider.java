package org.gcube.data.analysis.tabulardata.operation.time;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PeriodTypeHelperProvider {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(PeriodTypeHelperProvider.class);

	@Inject
	@Any
	Instance<PeriodTypeHelper> helpers;


	public PeriodTypeHelper getHelper(PeriodType periodType) {
		for (PeriodTypeHelper helper : helpers) {
			if (helper.getManagedPeriodType().equals(periodType))
				return helper;
		}
		throw new UnsupportedOperationException(String.format("Period type '%s' is not supported.", periodType));
	}
}
