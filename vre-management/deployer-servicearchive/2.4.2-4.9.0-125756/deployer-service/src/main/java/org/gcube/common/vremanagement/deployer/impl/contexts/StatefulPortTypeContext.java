package org.gcube.common.vremanagement.deployer.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

public class StatefulPortTypeContext extends GCUBEStatefulPortTypeContext {
	
	
	static StatefulPortTypeContext cache = new StatefulPortTypeContext();
	
	private StatefulPortTypeContext() {}
	
	public static StatefulPortTypeContext getContext() {
		return cache;
	}

	@Override
	public String getJNDIName() {		
		return "gcube/common/vremanagement/Deployer";
	}

	@Override
	public String getNamespace() {
		return "http://gcube-system.org/namespaces/vremanagement/deployer";
	}

	@Override
	public GCUBEServiceContext getServiceContext() {		
		return ServiceContext.getContext();
	}

}
