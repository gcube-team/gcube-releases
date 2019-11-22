package org.gcube.data.publishing.gCatFeeder.service.mockups;

import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.StorageImpl;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.data.publishing.gCatFeeder.service.model.reports.ExecutionReport;

public class StorageMockup extends StorageImpl {

	
	
	
	@Override
	public String storeReport(ExecutionReport report) throws InternalError {
		return asFile(report).getAbsolutePath();
	}

}
