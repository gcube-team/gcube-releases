package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.queries.GCUBEExternalRIQuery;
import org.gcube.common.core.resources.GCUBEExternalRunningInstance;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBEExternalRIQueryImpl  extends GCUBEResourceAbstractQueryImpl<GCUBEExternalRunningInstance> implements GCUBEExternalRIQuery {
	
	protected String getCollection() {return "Profiles/ExternalRunningInstance";}
	protected Class<GCUBEExternalRunningInstance> getResourceClass() {return GCUBEExternalRunningInstance.class;}
}
