package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.graph;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;

public class CustomListenableDirectedWeightedGraph<V,E> extends ListenableDirectedWeightedGraph<V,E>{

	
	public CustomListenableDirectedWeightedGraph(Class arg0) {
		super(arg0);
	}
	
	public void setEdgeWeight(E e, double weight) {
	    super.setEdgeWeight(e, weight);
	    
	    ((CustomWeightedEdge)e).setWeight(weight);
	}

	public E addEdge(V o1,V o2) {
	    E out = super.addEdge(o1,o2);
	    ((CustomWeightedEdge)out).setEdges(o1,o2);
	    
	    return out;
	}
	
}
