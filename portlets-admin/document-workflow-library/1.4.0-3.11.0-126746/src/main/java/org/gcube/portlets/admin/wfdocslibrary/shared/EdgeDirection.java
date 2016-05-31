package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EdgeDirection implements Serializable {
    public static EdgeDirection N = new EdgeDirection("N", Math.PI/2);
    public static EdgeDirection NE = new EdgeDirection("NE", Math.PI/2);
    public static EdgeDirection NW = new EdgeDirection("NE", Math.PI/2);
    public static EdgeDirection S = new EdgeDirection("S", 3*Math.PI/2);
    public static EdgeDirection SW = new EdgeDirection("SW", 3*Math.PI/2);
    public static EdgeDirection SE = new EdgeDirection("SE", 3*Math.PI/2);
    public static EdgeDirection E = new EdgeDirection("E", Math.PI);
    public static EdgeDirection W = new EdgeDirection("W", 0);
    
    private String id;
    private double angle;
    
    public EdgeDirection() {}
    
    public EdgeDirection(String id, double angle) {
		super();
		this.id = id;
		this.angle = angle;
	}

	/**
     * @return all defined directions
     */
    public static EdgeDirection[] getAll(){
    	return new EdgeDirection[]{S, E, W, N,SE,SW,NE,NW};
    }
    
    /**
     * @return true if it is horizontal direction
     */
    public boolean isHorizontal(){
    	return this == W || this == E;
    }
    
    /**
     * @return true if it is vertical direction
     */
    public boolean isVertical(){
    	return this == N || this == S;
    }

    /**
     * @return representing angle value
     */
    public double getAngle(){
    	return angle;
    }

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public static EdgeDirection getNE() {
		return NE;
	}

	public static void setNE(EdgeDirection nE) {
		NE = nE;
	}

	public static EdgeDirection getNW() {
		return NW;
	}

	public static void setNW(EdgeDirection nW) {
		NW = nW;
	}

	public static EdgeDirection getSW() {
		return SW;
	}

	public static void setSW(EdgeDirection sW) {
		SW = sW;
	}

	public static EdgeDirection getSE() {
		return SE;
	}

	public static void setSE(EdgeDirection sE) {
		SE = sE;
	}

	public static void setN(EdgeDirection n) {
		N = n;
	}

	public static void setS(EdgeDirection s) {
		S = s;
	}

	public static void setE(EdgeDirection e) {
		E = e;
	}

	public static void setW(EdgeDirection w) {
		W = w;
	}
	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return id;
    }
    
    public static EdgeDirection getN() {
		return N;
	}

	public static EdgeDirection getNe() {
		return NE;
	}

	public static EdgeDirection getNw() {
		return NW;
	}

	public static EdgeDirection getS() {
		return S;
	}

	public static EdgeDirection getSw() {
		return SW;
	}

	public static EdgeDirection getSe() {
		return SE;
	}

	public static EdgeDirection getE() {
		return E;
	}

	public static EdgeDirection getW() {
		return W;
	}

}