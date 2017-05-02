package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBECollectionQuery;
import org.gcube.common.core.resources.GCUBECollection;

import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBECollectionQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBECollection> implements GCUBECollectionQuery {
	
	protected String getCollection() {return "Profiles/Collection";}
	protected Class<GCUBECollection> getResourceClass() {return GCUBECollection.class;}
}
