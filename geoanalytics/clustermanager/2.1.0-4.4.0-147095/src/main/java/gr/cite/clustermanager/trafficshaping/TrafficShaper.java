package gr.cite.clustermanager.trafficshaping;

import java.util.Set;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.exceptions.NoAvailableLayer;
import gr.cite.clustermanager.model.GosDefinition;

public interface TrafficShaper {

	public GosDefinition getAppropriateGosForLayer(String layerID) throws NoAvailableLayer;

	public GosDefinition getGosForNewLayer() throws NoAvailableGos;

	public Set<GosDefinition> getAllGosEndpoints() throws NoAvailableGos;
	
	
}
