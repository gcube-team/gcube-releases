package org.gcube.data.spd.client.proxies;


import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;

public interface Occurrence {

	public Stream<OccurrencePoint> getByIds(Stream<String> ids);
	
	public String createLayer(Stream<PointInfo> coordinatesLocator);
	
	public Stream<OccurrencePoint> getByKeys(Stream<String> keys);
	
}
