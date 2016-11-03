package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

public class CSquareCodesConverter {
	
	
		public static String convertHalfDegree(double x, double y){
				
				if (x ==0)
					x = 0.25;
				if (y ==0)
					y = 0.25;
				
				String csquare = it.cnr.aquamaps.CSquare.centroidToCode(x,y, 0.5);
				return csquare;
		}
	
		public static void main(String[] args){
			System.out.println(it.cnr.aquamaps.CSquare.centroidToCode(0.5,0.5, 0.0001));
		}
}
