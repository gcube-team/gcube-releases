package org.gcube.portlets.user.geoexplorer.server.datafetcher;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.server.service.dao.MetadataPersistence;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */

public class MetadataBuffer<T extends FetchingElement> implements FetchingBuffer<T> {

	private MetadataPersistence metadaPersistence;
	
	public MetadataBuffer(MetadataPersistence persistence) {
		
		this.metadaPersistence = persistence;
		
	}

	@Override
	public void add(T e) throws Exception {
		metadaPersistence.insert((GeonetworkMetadata) e);
		
	}

	@Override
	public List<T> getList() throws Exception {
		return (List<T>) metadaPersistence.getList();
	}

	@Override
	public int size() throws Exception {
		return metadaPersistence.countItems();
	}

	@Override
	public List<T> getList(int startIndex, int offset) throws Exception {
		return (List<T>) metadaPersistence.getList(startIndex, offset);
	}
}
