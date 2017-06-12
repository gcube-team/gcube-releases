package gr.cite.clustermanager.trafficshaping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.exceptions.NoAvailableLayer;
import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.clustermanager.model.GosDefinition;

public class SimpleTrafficShaper implements TrafficShaper , Serializable{

	private static final long serialVersionUID = -6855110779203690552L;
	
	private DataMonitor dataMonitor;
	
	@Autowired
	public void setDataMonitor(DataMonitor dataMonitor){
		this.dataMonitor = dataMonitor;
	}
	
	
	/**
	 * Fetches a random one from the available
	 */
	@Override
	public GosDefinition getAppropriateGosForLayer(String layerID) throws NoAvailableLayer{
		Set<GosDefinition> availableGos = dataMonitor.getAvailableGosFor(layerID);
		if(availableGos==null || availableGos.isEmpty())
			throw new NoAvailableLayer("Cluster manager said that there are nowhere any available layers by the ID "+layerID);
		return new ArrayList<GosDefinition>(availableGos).get(ThreadLocalRandom.current().nextInt(0, availableGos.size()));
	}
	
	@Override
	public GosDefinition getGosForNewLayer() throws NoAvailableGos{
		Set<GosDefinition> gosDefinitions = dataMonitor.getAllGosEndpoints();
		if(gosDefinitions==null || gosDefinitions.isEmpty())
			throw new NoAvailableGos("Cluster manager said that there are no available GOS services");
		return new ArrayList<GosDefinition>(gosDefinitions).get(ThreadLocalRandom.current().nextInt(0, gosDefinitions.size()));
	}

	@Override
	public Set<GosDefinition> getAllGosEndpoints() throws NoAvailableGos{
		Set<GosDefinition> gosDefinitions = dataMonitor.getAllGosEndpoints();
		if(gosDefinitions==null || gosDefinitions.isEmpty())
			throw new NoAvailableGos("Cluster manager said that there are no available GOS services");
		return gosDefinitions;
	}
	
	
}
