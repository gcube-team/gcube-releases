package org.gcube.vremanagement.executor.client.query.filter.impl;

import org.gcube.vremanagement.executor.client.query.filter.GCoreEndpointQueryFilter;

@SuppressWarnings("deprecation")
public class SpecificGCoreEndpointQueryFilter
		extends org.gcube.vremanagement.executor.client.plugins.query.filter.SpecificEndpointDiscoveryFilter
		implements GCoreEndpointQueryFilter {
	
	public SpecificGCoreEndpointQueryFilter(String endpointURI) {
		super(endpointURI);
	}
	
}
