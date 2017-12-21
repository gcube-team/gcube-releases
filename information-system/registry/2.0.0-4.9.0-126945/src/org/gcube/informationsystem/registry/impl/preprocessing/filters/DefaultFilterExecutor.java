package org.gcube.informationsystem.registry.impl.preprocessing.filters;

import java.util.List;

import org.gcube.common.core.resources.GCUBEResource;

/**
 * Default filter for {@link GCUBEResource}
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DefaultFilterExecutor extends FilterExecutor {

	//private List<Filter> filters = null;
	
	protected DefaultFilterExecutor(List<Filter> filters) {
		//this.filters = filters;
	}
	
	@Override
	public boolean accept(GCUBEResource resource) throws InvalidFilterException {
		logger.trace("Accept() on DefaultFilterExecutor invoked");
		return true;
	}

}
