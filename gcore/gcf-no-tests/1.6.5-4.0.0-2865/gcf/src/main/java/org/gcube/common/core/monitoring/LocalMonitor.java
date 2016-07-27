package org.gcube.common.core.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;

/**
 * Abstract Local Monitor
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public abstract class LocalMonitor extends GCUBEHandler<LocalMonitor>{
	
	protected long interval;
	protected static HashMap<GCUBEScope,ArrayList<EndpointReferenceType>> brokerMap;
	
	/**
	 * Get the Monitor Interval
	 * @return the monitor interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * Set the monitor Interval
	 * @param interval the monitor interval
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	 /**
	 * Get the Broker Map
	 * @return the Broker Map
	 */
	public  HashMap<GCUBEScope,ArrayList<EndpointReferenceType>> getBrokerMap() {
		return brokerMap;
	}

	/**
	 * Set the Broker Map
	 * @param map the Broker Map
	 */
	public void setBrokerMap(HashMap<GCUBEScope,ArrayList<EndpointReferenceType>> map) {
		brokerMap = map;
	}
	/**
	 * Probes Implementation map
	 */
	protected Map<String,Class<? extends GCUBETestProbe>> implementationMap;
				
	/**
	 * Load Probes from file
	 * @param map the Properties map
	 */
	protected abstract void loadProbes(Map<String,Class<? extends GCUBETestProbe>> map) ;
	
	
}
