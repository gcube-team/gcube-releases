package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;

public class GCUBERuntimeResourceQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBERuntimeResource> implements GCUBERuntimeResourceQuery {

	protected String getCollection() {return "Profiles/RuntimeResource";}
	protected Class<GCUBERuntimeResource> getResourceClass() {return  GCUBERuntimeResource.class;}

}
