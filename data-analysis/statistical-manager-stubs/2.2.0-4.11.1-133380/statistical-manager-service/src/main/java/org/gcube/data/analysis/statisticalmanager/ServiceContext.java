package org.gcube.data.analysis.statisticalmanager;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;

  

public class ServiceContext extends GCUBEServiceContext {
	/** Single context instance, created eagerly */
	private static ServiceContext singleton = new ServiceContext();
			
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
	public static String SERVICE_NAME = "StatisticalManager";
	
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return singleton;}
	
	/** {@inheritDoc} */
	@Override
	protected String getJNDIName() {
                  return "gcube/data/analysis/statisticalmanager";
        }
	
	@Override
	public void initialise(GCUBEServiceContext ctxt) throws Exception {
		// TODO Auto-generated method stub
		super.initialise(ctxt);
		
		
		
	}
	
	
	
}