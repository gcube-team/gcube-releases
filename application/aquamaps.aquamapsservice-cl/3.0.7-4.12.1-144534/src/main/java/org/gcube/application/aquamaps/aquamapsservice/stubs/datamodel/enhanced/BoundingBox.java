package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;


public class BoundingBox extends DataModel{
	private Double N=90d;
	private Double S=-90d;
	private Double W=180d;
	private Double E=-180d;
	public Double getN() {
		return N;
	}

	public void setN(Double n) {
		N = n;
	}

	public Double getS() {
		return S;
	}

	public void setS(Double s) {
		S = s;
	}

	public Double getW() {
		return W;
	}

	public void setW(Double w) {
		W = w;
	}

	public Double getE() {
		return E;
	}

	public void setE(Double e) {
		E = e;
	}

	public BoundingBox() {		
	}
	
	public BoundingBox(Double n, Double s, Double w, Double e) {
		super();
		N = n;
		S = s;
		W = w;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((E == null) ? 0 : E.hashCode());
		result = prime * result + ((N == null) ? 0 : N.hashCode());
		result = prime * result + ((S == null) ? 0 : S.hashCode());
		result = prime * result + ((W == null) ? 0 : W.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoundingBox other = (BoundingBox) obj;
		if (E == null) {
			if (other.E != null)
				return false;
		} else if (!E.equals(other.E))
			return false;
		if (N == null) {
			if (other.N != null)
				return false;
		} else if (!N.equals(other.N))
			return false;
		if (S == null) {
			if (other.S != null)
				return false;
		} else if (!S.equals(other.S))
			return false;
		if (W == null) {
			if (other.W != null)
				return false;
		} else if (!W.equals(other.W))
			return false;
		return true;
	}
	
	
	
}
