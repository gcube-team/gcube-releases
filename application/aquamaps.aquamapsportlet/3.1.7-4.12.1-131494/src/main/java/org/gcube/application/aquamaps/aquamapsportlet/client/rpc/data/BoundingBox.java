package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BoundingBox implements IsSerializable{
	double N=90;
	double S=-90;
	double W=180;
	double E=-180;
	
	public BoundingBox() {		
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
	public String toString(){
		return String.valueOf(N)+","+
		String.valueOf(S)+","+
		String.valueOf(W)+","+
		String.valueOf(E);
	}
	
	/**
	 * Sets comma separated coordinates  
	 * 
	 * @param str coordinates order : N , S , W , E 
	 */
	
	public void parse(String str){			
		String[] values= str.split(",");
		N=Double.parseDouble(values[0]);
		S=Double.parseDouble(values[1]);
		W=Double.parseDouble(values[2]);
		E=Double.parseDouble(values[3]);		
	}

	
}
