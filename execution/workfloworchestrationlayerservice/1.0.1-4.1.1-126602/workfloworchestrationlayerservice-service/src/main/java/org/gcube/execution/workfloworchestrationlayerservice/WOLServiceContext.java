package org.gcube.execution.workfloworchestrationlayerservice;

import org.gcube.common.core.contexts.GCUBEServiceContext;

public class WOLServiceContext extends GCUBEServiceContext {
	
	public static final String CONDOR_NODE = "condorNode";
	public static final String GRID_NODE = "gridNode";
	public static final String PE2NG_NODE = "pe2ngNode";
	public static final String HADOOP_NODE = "hadoopNode";
	
	/** Single context instance, created eagerly */
	private static WOLServiceContext cache = new WOLServiceContext();
	
	/** Returns cached instance */
	public static WOLServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	private WOLServiceContext(){};
		
	public String getJNDIName() {return "gcube/execution/WorkflowOrchestrationLayerService";}
	
	@Override
	protected void onReady() throws Exception
	{
		super.onReady();
	}

}
