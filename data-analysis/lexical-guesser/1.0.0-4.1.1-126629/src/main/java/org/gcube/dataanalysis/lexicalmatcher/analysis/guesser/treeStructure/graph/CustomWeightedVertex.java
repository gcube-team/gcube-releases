package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.touchgraph.graphlayout.Edge;

public class CustomWeightedVertex {

	@Override
	public String toString() {

		return "[" + name + ":" + weight + "%]";
	}

	private double weight;
	private String name;

	public CustomWeightedVertex(String name, double weight) {
		this.weight = weight;
		this.name = name;
	}

	public CustomWeightedVertex(String name) {
		this.weight = 0;
		this.name = name;
	}

	public boolean equals(CustomWeightedVertex v) {

		if (v.name.equals(name))
			return true;
		else
			return false;

	}
}
