package org.gcube.informationsystem.registry.impl.filters;

import java.util.List;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.informationsystem.registry.impl.filters.Filter.FILTEROPERATION;

/**
 * Filter for {@link GCUBEHostingNode} resource
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GHNFilterExecutor extends FilterExecutor {
	
	private List<Filter> filters = null;
	
	private GCUBEHostingNode node = null;
	
	protected GHNFilterExecutor(List<Filter> filters) {
		this.filters = filters;
	}
	
	@Override
	public boolean accept(GCUBEResource resource) throws InvalidFilterException {				
		
		if (resource.getType().compareTo(GCUBEHostingNode.TYPE) != 0) {
			throw new InvalidFilterException("Cannot apply " + this.getClass().getName() + " filter to " + resource.getType());
		}
		this.node = (GCUBEHostingNode) resource;
		logger.trace("Applying N."  + this.filters.size() + " filters to GHN");
		for (Filter filter : this.filters) {
			if (!this._accept(filter))
				return false;
		}
				
		return true;
	}

	private boolean _accept(Filter filter) {
		
		if ((filter.getTarget().compareToIgnoreCase("Site/Domain") == 0) && 
				((filter.getOperation() == FILTEROPERATION.exclude) || (filter.getOperation() == FILTEROPERATION.exclude_if_contains)))
			return !this.isExcludedDomain(filter.getValue());
		
		if ((filter.getTarget().compareToIgnoreCase("GHNDescription/Name") == 0) && 
				((filter.getOperation() == FILTEROPERATION.exclude) || (filter.getOperation() == FILTEROPERATION.exclude_if_contains)))
			return !this.isExcludedHost(filter.getValue());
		
		logger.warn("Target "+ filter.getTarget() +" or Operation " + filter.getOperation().name() + " not supported by the GHNFilter");
		return true;
	}

	private boolean isExcludedHost(String host) {
		logger.trace("Checking host " + this.node.getNodeDescription().getName());
		if (this.node.getNodeDescription().getName().split(":")[0].compareToIgnoreCase(host) == 0) {
			logger.warn("Detected banned hostname " + host + " for GHN " + this.node.getID());
			return true;
		}
		return false;		
	}

	private boolean isExcludedDomain(String domain) {
		logger.trace("Checking domain " + this.node.getSite().getDomain());
		if (this.node.getSite().getDomain().compareToIgnoreCase(domain) == 0 ) {
			logger.warn("Detected banned domain " + domain + " for GHN " + this.node.getID());
			return true;
		}
		return false;
	}
	
}
