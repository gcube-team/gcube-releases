package org.gcube.data.spd.client.proxies;


import java.util.List;

import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;

public interface OccurrenceClient {

	public Stream<OccurrencePoint> getByIds(List<String> ids);
	
	public Stream<OccurrencePoint> getByKeys(List<String> keys);
	
}
