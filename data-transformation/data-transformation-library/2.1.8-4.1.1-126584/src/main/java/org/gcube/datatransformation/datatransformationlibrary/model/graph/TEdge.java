package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * A transformationUnit edge is a transformationUnit unit which points to one node of the transformationUnit's graph.
 * </p>
 */
public class TEdge {
	private TransformationUnit transformationUnit=null;
	private TNode toNode=null;
	
	private double cost=1.0;
	
	protected TransformationUnit getTransformationUnit() {
		return transformationUnit;
	}
	
	protected void setTransformationUnit(TransformationUnit transformationUnit) {
		this.transformationUnit = transformationUnit;
	}
	
	protected TNode getToNode() {
		return toNode;
	}
	
	protected void setToNode(TNode toNode) {
		this.toNode = toNode;
	}
	
	protected TEdge(TransformationUnit transformationUnit, TNode toNode) {
		this.transformationUnit = transformationUnit;
		this.toNode = toNode;
	}

	protected TEdge() {
	}

	protected double getCost() {
		return cost;
	}

	protected void setCost(double cost) {
		this.cost = cost;
	}
}
