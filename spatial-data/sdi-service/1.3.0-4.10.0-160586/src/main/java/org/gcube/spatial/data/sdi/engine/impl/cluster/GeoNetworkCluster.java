package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.util.ArrayList;
import java.util.Comparator;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceInteractionException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoNetworkCluster extends AbstractCluster<GeoNetworkDescriptor,GeoNetworkController>{

	private static final Comparator<GeoNetworkController> comparator=new Comparator<GeoNetworkController>() {
		@Override
		public int compare(GeoNetworkController o1, GeoNetworkController o2) {
			return o1.getDescriptor().getPriority().compareTo(o2.getDescriptor().getPriority());
		}
	};
	
	
	public GeoNetworkCluster(long objectsTTL, ISModule retriever, String cacheName) {
		super(objectsTTL, retriever, cacheName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Comparator<GeoNetworkController> getComparator() {
		return comparator;
	}
	
	@Override
	protected GeoNetworkController translate(ServiceEndpoint e) throws InvalidServiceEndpointException {
		return new GeoNetworkController(e);
	}
	
	@Override
	protected ArrayList<GeoNetworkController> getLiveControllerCollection() throws ConfigurationNotFoundException {		
		ArrayList<GeoNetworkController> toReturn= super.getLiveControllerCollection();
		try{
			toReturn.get(0).configure();
		}catch(ServiceInteractionException e) {
			log.warn("Unexpected exception while configuring GeoNetwork SE [ID : "+toReturn.get(0).getServiceEndpoint().id()+"]",e);
		}
		return toReturn;
	}
}
