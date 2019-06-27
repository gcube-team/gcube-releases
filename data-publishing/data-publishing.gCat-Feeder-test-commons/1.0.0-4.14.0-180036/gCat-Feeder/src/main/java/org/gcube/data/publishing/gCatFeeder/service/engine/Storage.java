package org.gcube.data.publishing.gCatFeeder.service.engine;

import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.data.publishing.gCatFeeder.service.model.reports.ExecutionReport;

public interface Storage {

	
	public String storeReport(ExecutionReport report) throws InternalError;
}
