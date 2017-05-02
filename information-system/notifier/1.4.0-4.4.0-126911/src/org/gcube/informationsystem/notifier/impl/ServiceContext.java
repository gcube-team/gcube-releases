package org.gcube.informationsystem.notifier.impl;


import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;

/**
 * 
 * @author Andrea Manzi(ISTI_CNR)
 *
 */
public class ServiceContext extends GCUBEServiceContext{

	public static final String JNDI_NAME="gcube/informationsystem/notifier";
		
	protected static ServiceContext cache = new ServiceContext();

	private ServiceContext() {}

	public static ServiceContext getContext() {
		return cache;
	}
	public String getJNDIName() {
		return JNDI_NAME;
	}
	
	@Override 
	protected void onReady() throws Exception {
		
		for (GCUBEScope scope: this.getInstance().getScopes().values())
		{
			ServiceContext.getContext().setScope(scope);
			NotifierResource resource=(NotifierResource)NotifierContext.getPortTypeContext().getWSHome().create(NotifierContext.getPortTypeContext().makeKey("NotifierResource"+"_"+scope.toString().replace("/", "_")));
			resource.store();
		}
		
	}


}