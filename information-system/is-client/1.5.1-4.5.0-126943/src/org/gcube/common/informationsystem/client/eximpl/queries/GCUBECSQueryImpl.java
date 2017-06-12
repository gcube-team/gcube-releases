package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBECSQuery;
import org.gcube.common.core.resources.GCUBECS;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBECSQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBECS> implements GCUBECSQuery {
	
	protected String getCollection() {return "Profiles/CS";}
	protected Class<GCUBECS> getResourceClass() {return GCUBECS.class;}
}
