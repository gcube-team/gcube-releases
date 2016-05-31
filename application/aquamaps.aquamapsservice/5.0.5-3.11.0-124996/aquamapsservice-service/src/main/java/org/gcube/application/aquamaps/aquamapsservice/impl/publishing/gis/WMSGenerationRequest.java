package org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis;

import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.GenerationRequest;

public class WMSGenerationRequest implements GenerationRequest{

	private int jobId;
	public WMSGenerationRequest(int jobId) {
		this.jobId=jobId;
	}
	public int getJobId() {
		return jobId;
	}
}
