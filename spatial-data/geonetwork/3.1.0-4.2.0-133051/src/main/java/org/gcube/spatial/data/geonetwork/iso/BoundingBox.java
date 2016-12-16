package org.gcube.spatial.data.geonetwork.iso;

public class BoundingBox {

	public static final BoundingBox WORLD_EXTENT=new BoundingBox(90, -90, 180, -180);
	
	private double N=90;
	private double S=-90;
	private double W=180;
	private double E=-180;
	
	public BoundingBox(double n, double s, double w, double e) {
		super();
		N = n;
		S = s;
		W = w;
		E = e;
	}

	/**
	 * Assumed order is E, S, W, N
	 * 
	 * @param bbox
	 */
	public BoundingBox(double[] bbox) {
		// TODO Auto-generated constructor stub
	}
	
	
	public double getN() {
		return N;
	}

	public void setN(double n) {
		N = n;
	}

	public double getS() {
		return S;
	}

	public void setS(double s) {
		S = s;
	}

	public double getW() {
		return W;
	}

	public void setW(double w) {
		W = w;
	}

	public double getE() {
		return E;
	}

	public void setE(double e) {
		E = e;
	}
	
	
	/**
	 * 
	 * @return E, S, W, N
	 */
	public double[] toArray(){
		return new double[]{
			E,S,W,N	
		};
	}
	
	/**
	 * 
	 * @return E, S, W, N
	 */
	@Override
	public String toString() {
		return E+","+S+","+W+","+N;
	}
	
	
	
}
