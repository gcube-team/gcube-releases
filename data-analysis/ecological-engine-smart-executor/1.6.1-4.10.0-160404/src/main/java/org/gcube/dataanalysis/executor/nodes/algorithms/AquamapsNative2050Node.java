package org.gcube.dataanalysis.executor.nodes.algorithms;


public class AquamapsNative2050Node extends AquamapsNativeNode{
	
	public AquamapsNative2050Node(){
		super();
		type = "2050";
	}
	
	public String getName() {
		return "AQUAMAPS_NATIVE_2050";
	}

	public String getDescription() {
		return "Algorithm for Native Range in 2050 by Aquamaps on a single node";
	}
	
}
