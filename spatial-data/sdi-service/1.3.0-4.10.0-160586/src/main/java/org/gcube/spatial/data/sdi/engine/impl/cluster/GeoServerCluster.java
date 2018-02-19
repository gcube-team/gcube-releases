package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.util.ArrayList;
import java.util.Comparator;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceInteractionException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.GeoServerDescriptor;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GeoServerCluster extends AbstractCluster<GeoServerDescriptor,GeoServerController>{

	private static final Comparator<GeoServerController> comparator=new Comparator<GeoServerController>() {
		@Override
		public int compare(GeoServerController o1, GeoServerController o2) {
			return o1.getHostedLayersCount().compareTo(o2.getHostedLayersCount());
		}
	};
	
	
	public GeoServerCluster(long objectsTTL, ISModule retriever, String cacheName) {
		super(objectsTTL, retriever, cacheName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Comparator<GeoServerController> getComparator() {
		return comparator;
	}
	
	
	@Override
	protected GeoServerController translate(ServiceEndpoint e) throws InvalidServiceEndpointException {
		return new GeoServerController(e);
	}
	
	@Override
	protected ArrayList<GeoServerController> getLiveControllerCollection() throws ConfigurationNotFoundException {		
		ArrayList<GeoServerController> toReturn= super.getLiveControllerCollection();
		for(GeoServerController controller:toReturn)
			try{
				controller.configure();
			}catch(ServiceInteractionException e) {
				log.warn("Unexpected exception while configuring GeoServer SE [ID : "+controller.getServiceEndpoint().id()+"]",e);
			}
		return toReturn;
		
	}
}
