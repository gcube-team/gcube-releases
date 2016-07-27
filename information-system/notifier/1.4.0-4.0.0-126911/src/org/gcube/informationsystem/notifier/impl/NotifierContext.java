package org.gcube.informationsystem.notifier.impl;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

/**
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public class NotifierContext extends GCUBEStatefulPortTypeContext{
	
	static private final String PORTTYPE_NAME = "gcube/informationsystem/notifier/Notifier";
	
	static NotifierContext cache = new NotifierContext();
	
	private NotifierContext() {}
	
	public static NotifierContext getContext() {
		return cache;
	}
	
	
	public String getJNDIName() {
		
		return PORTTYPE_NAME;
	}
	
	
	public String getNamespace() {
		return "http://gcube-system.org/namespaces/informationsystem/notifier";
	}

	public String getServiceName() {
		return ServiceContext.getContext().getName();
	}

	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	public static GCUBEStatefulPortTypeContext getPortTypeContext() {
    	return cache;
    }
	
	
	
}
