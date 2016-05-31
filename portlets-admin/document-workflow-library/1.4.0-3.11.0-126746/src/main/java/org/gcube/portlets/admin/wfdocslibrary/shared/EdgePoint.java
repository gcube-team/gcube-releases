package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EdgePoint implements Serializable {
	protected int left;
	protected int top;
	private EdgeDirection edgeDirection;
	
	public EdgePoint() {}
	
	public EdgePoint(int left, int top, EdgeDirection edgeDirection) {
		super();
		this.left = left;
		this.top = top;
		this.edgeDirection = edgeDirection;
	}
	
	public EdgePoint(int left, int top) {
		this.left = left;
		this.top = top;
	}
	
	public EdgePoint(double left, double top) {
		this.left = new Double(left).intValue();
		this.top = new Double(top).intValue();
	}

	public EdgeDirection getEdgeDirection() {
		return edgeDirection;
	}

	public void setEdgeDirection(EdgeDirection edgeDirection) {
		this.edgeDirection = edgeDirection;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void setTop(int top){
		this.top = top;
	}
	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}
}
