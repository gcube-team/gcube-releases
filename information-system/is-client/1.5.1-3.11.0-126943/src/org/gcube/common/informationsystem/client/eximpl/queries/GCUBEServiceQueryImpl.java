package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBEServiceQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBEService> implements GCUBEServiceQuery {
	
	protected String getCollection() {return "Profiles/Service";}
	protected Class<GCUBEService> getResourceClass() {return GCUBEService.class;}
		
}
