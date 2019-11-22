package org.gcube.data.publishing.gCatFeeder.service.engine;

import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;

public interface ExecutionManager {

	public void submit(ExecutionDescriptor desc);
	
	public void stop();
	
	public void load();
	
}
