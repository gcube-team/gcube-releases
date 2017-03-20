package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBEGenericResourceQueryImpl  extends GCUBEResourceAbstractQueryImpl<GCUBEGenericResource> implements GCUBEGenericResourceQuery {
	
	protected String getCollection() {return "Profiles/GenericResource";}
	protected Class<GCUBEGenericResource> getResourceClass() {return GCUBEGenericResource.class;}
}
