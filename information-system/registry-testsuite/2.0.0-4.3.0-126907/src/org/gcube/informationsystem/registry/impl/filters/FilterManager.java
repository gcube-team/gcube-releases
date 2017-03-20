package org.gcube.informationsystem.registry.impl.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.utils.logging.GCUBELog;


/**
 * 
 * {@link GCUBEResource} filter factory
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class FilterManager {
	
	protected static GCUBELog logger = new GCUBELog(FilterManager.class);	
	
	protected static Map<String, List<Filter>> filters = Collections.synchronizedMap(new HashMap<String, List<Filter>>());
	
	protected static FilterReader reader = null;
	
	/**
	 * Gets the {@link FilterExecutor} for the given {@link GCUBEResource} type
	 * @param resourceType the resource type
	 * 
	 * @return the filter executor
	 */
	public static FilterExecutor getExecutor(String resourceType) {		
		initReader();
		logger.trace("Executor for " + resourceType + " requested");
		logger.trace("Number of filters available: " + getFilters(resourceType).size());
		if (resourceType.compareTo(GCUBEHostingNode.TYPE) == 0) 		
			return new GHNFilterExecutor(getFilters(resourceType));		
		
		if (resourceType.compareTo(GCUBERunningInstance.TYPE) == 0) 		
			return new RIFilterExecutor(getFilters(resourceType));
		
		return new DefaultFilterExecutor(getFilters(resourceType));		
	}
	
	/**
	 * Gets the {@link Filter}s the given {@link GCUBEResource} type
	 * @param resourceType the resource type
	 * 
	 * @return the list of filters
	 */
	protected synchronized static List<Filter> getFilters(String resourceType) {				
		
		if (!filters.containsKey(resourceType))			
			filters.put(resourceType, new ArrayList<Filter>());		
		
		return filters.get(resourceType);
	}		
	
	
	private synchronized static void initReader() {
		if (reader == null)	reader = new FilterReader();
	}
}
