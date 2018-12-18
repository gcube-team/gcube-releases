package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.util.Comparator;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;

public class ThreddsCluster extends AbstractCluster<ThreddsDescriptor,ThreddsController> {

	
	public ThreddsCluster(long objectsTTL, ISModule retriever, String cacheName) {
		super(objectsTTL, retriever, cacheName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ThreddsController translate(ServiceEndpoint e) throws InvalidServiceEndpointException {
		return new ThreddsController(e);
	}

	@Override
	protected Comparator getComparator() {
		return null;
	}

}
