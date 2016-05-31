package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBEGHNQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBEHostingNode> implements GCUBEGHNQuery {
	
	protected String getCollection() {return "Profiles/GHN";}
	protected Class<GCUBEHostingNode> getResourceClass() {return GCUBEHostingNode.class;}
}
