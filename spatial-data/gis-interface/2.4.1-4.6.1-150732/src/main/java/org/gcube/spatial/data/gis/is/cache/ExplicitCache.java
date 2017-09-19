package org.gcube.spatial.data.gis.is.cache;

import java.util.Collection;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;

public class ExplicitCache<T extends AbstractGeoServerDescriptor> extends GeoServerCache<T> {

	
	SortedSet<T> localCache=null;
	
	public ExplicitCache(Collection<T> toUseDescriptors) {
		localCache=new ConcurrentSkipListSet<T>(toUseDescriptors);
	}
	
	@Override
	protected SortedSet<T> getTheCache(Boolean forceUpdate) {
		return localCache;
	}
}
