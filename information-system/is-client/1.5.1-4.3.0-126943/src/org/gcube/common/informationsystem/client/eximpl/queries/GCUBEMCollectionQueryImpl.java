package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBEMCollectionQuery;
import org.gcube.common.core.resources.GCUBEMCollection;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBEMCollectionQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBEMCollection> implements GCUBEMCollectionQuery {
	
	protected String getCollection() {return "Profiles/MetadataCollection";}
	protected Class<GCUBEMCollection> getResourceClass() {return GCUBEMCollection.class;}
}
