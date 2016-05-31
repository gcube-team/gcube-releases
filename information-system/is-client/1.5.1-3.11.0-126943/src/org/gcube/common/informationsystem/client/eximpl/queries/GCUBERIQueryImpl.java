package org.gcube.common.informationsystem.client.eximpl.queries;

import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.informationsystem.client.eximpl.GCUBEResourceAbstractQueryImpl;


public class GCUBERIQueryImpl extends GCUBEResourceAbstractQueryImpl<GCUBERunningInstance> implements GCUBERIQuery {
	
	
	public GCUBERIQueryImpl() {
		this.clearConditions();
	}
	protected String getCollection() {return "Profiles/RunningInstance";}
	protected Class<GCUBERunningInstance> getResourceClass() {return GCUBERunningInstance.class;}
	

	@Override
	public void clearConditions() {
		super.clearConditions();
		this.addAtomicConditions(new AtomicCondition("/Profile/DeploymentData/Status","ready"));
	}
	
}
