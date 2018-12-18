package org.gcube.informationsystem.registry.impl.preprocessing.filters;

import java.util.List;

import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.informationsystem.registry.impl.preprocessing.filters.Filter.FILTEROPERATION;


/**
 * Filter for {@link GCUBERunningInstance} resource
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class RIFilterExecutor extends FilterExecutor {

	private List<Filter> filters = null;

	private GCUBERunningInstance instance = null;
	
	protected RIFilterExecutor(List<Filter> filters) {
		this.filters = filters;
	}
	
	@Override
	public boolean accept(GCUBEResource resource) throws InvalidFilterException {
	
		if (resource.getType().compareTo(GCUBERunningInstance.TYPE) != 0) {
			throw new InvalidFilterException("Cannot apply " + this.getClass().getName() + " filter to " + resource.getType());
		}
		this.instance = (GCUBERunningInstance) resource;
		
		logger.trace("Applying N."  + this.filters.size() + " filters to RI");
		for (Filter filter : this.filters) {
			if (!this._accept(filter))
				return false;
		}
				
		return true;
		
	}

	private boolean _accept(Filter filter) {
		if ((filter.getTarget().compareToIgnoreCase("AccessPoint/RunningInstanceInterfaces/Endpoint") == 0) && (filter.getOperation() == FILTEROPERATION.exclude_if_contains))
			return !this.isExcludedIfContainsEPR(filter.getValue());
		
		if ((filter.getTarget().compareToIgnoreCase("AccessPoint/RunningInstanceInterfaces/Endpoint") == 0) && (filter.getOperation() == FILTEROPERATION.exclude))
			return !this.isExcludedEPR(filter.getValue());
		
		return true;
	}

	private boolean isExcludedIfContainsEPR(String value) {
		
		for (Endpoint endpoint: this.instance.getAccessPoint().getRunningInstanceInterfaces().getEndpoint()) {
			logger.trace("Checking endpoint " + endpoint.getValue());
			if (endpoint.getValue().contains(value)) {
				logger.warn("Detected banned EPR " + value + " for RI " + this.instance.getID());
				return true;	
			}
			
		}
			
		return false;
	}

	private boolean isExcludedEPR(String value) {
		
		for (Endpoint endpoint: this.instance.getAccessPoint().getRunningInstanceInterfaces().getEndpoint()) {
			logger.trace("Checking endpoint " + endpoint.getValue());
			if (endpoint.getValue().compareToIgnoreCase(value) == 0) {
				logger.warn("Detected banned EPR " + value + " for RI " + this.instance.getID());
				return true;	
			}
			
		}
			
		return false;
	}

}
