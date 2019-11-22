package org.gcube.spatial.data.geonetwork.iso;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoundingBox {

	public static final BoundingBox WORLD_EXTENT=new BoundingBox(90d, -90d, 180d, -180d);
	
	private Double north=90d;
	private Double south=-90d;
	private Double west=180d;
	private Double east=-180d;
	
//	public BoundingBox(double n, double s, double w, double e) {		
//		North = n;
//		South = s;
//		West = w;
//		East = e;
//	}

	
	/**
	 * 
	 * @return E, S, W, N
	 */
	public double[] toArray(){
		return new double[]{
			east,south,west,north	
		};
	}
	
	/**
	 * 
	 * @return E, S, W, N
	 */
	@Override
	public String toString() {
		return east+","+south+","+west+","+north;
	}
	
	
	
}
