package org.gcube.data.publishing.gCatfeeder.collectors;

import java.util.Set;

import org.gcube.data.publishing.gCatfeeder.collectors.model.CustomData;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CollectorFault;

public interface DataCollector<T extends CustomData> {

	
	public Set<T> collect() throws CollectorFault;
	
	
	
}
