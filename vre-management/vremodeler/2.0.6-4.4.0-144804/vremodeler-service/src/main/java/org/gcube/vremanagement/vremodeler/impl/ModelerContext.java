package org.gcube.vremanagement.vremodeler.impl;


import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

public class ModelerContext extends GCUBEStatefulPortTypeContext{

	private static ModelerContext cache = new ModelerContext();
	
    private ModelerContext(){}
    
	@Override
	public String getJNDIName() {
		return "gcube/vremanagement/vremodeler/ModelerService";
	}

	@Override
	public String getNamespace() {
		return "http://gcube-system.org/namespaces/vremanagement/vremodeler";
	}

	@Override
    public ServiceContext getServiceContext() {
		return ServiceContext.getContext();
    }

    public static ModelerContext getPortTypeContext() {
    	return cache;
    }

}
