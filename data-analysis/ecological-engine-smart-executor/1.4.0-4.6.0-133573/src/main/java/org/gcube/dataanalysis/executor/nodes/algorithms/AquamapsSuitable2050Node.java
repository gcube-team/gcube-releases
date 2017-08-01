package org.gcube.dataanalysis.executor.nodes.algorithms;


public class AquamapsSuitable2050Node extends AquamapsSuitableNode{
	
	public AquamapsSuitable2050Node(){
		super();
		type = "2050";
	}
	
	public String getName() {
		return "AQUAMAPS_SUITABLE_2050";
	}

	public String getDescription() {
		return "Algorithm for Suitable Range in 2050 by Aquamaps on a single node";
	}
	
}
