package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.touchgraph.graphlayout.Edge;

public class CustomWeightedEdge extends DefaultWeightedEdge{

	@Override
	public String toString(){
		return "["+o1+":"+o2+":"+weight+"%]";
	}
	
	private double weight;
	private Object o1;
	private Object o2;
	
	public void setWeight(double weight){
		this.weight = weight;
	}

	public void setEdges(Object o1,Object o2){
		this.o1=o1;
		this.o2=o2;
	} 
	
}
