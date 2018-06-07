package org.gcube.spatial.data.sdi.engine;

import java.util.List;

import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoServiceDescriptor;

public interface RoleManager {

	public Credentials getMostAccessible(List<Credentials> toFilter,boolean considerAdmin);
	
	public <T extends GeoServiceDescriptor> List<T> filterByRole(List<T> toFilter, boolean considerAdmin);
	
}
