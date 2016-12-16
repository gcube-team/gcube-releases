package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBECSInstanceQuery;
import org.gcube.common.core.resources.GCUBECSInstance;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBECSInstanceQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBECSInstance> implements GCUBECSInstanceQuery {
	
	protected String getCollection() {return "Profiles/CSInstance";}
	protected Class<GCUBECSInstance> getResourceClass() {return GCUBECSInstance.class;}
}
