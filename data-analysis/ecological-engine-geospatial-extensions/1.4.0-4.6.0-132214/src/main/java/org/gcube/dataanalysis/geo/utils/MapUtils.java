package org.gcube.dataanalysis.geo.utils;

import java.util.List;

public class MapUtils {
	
	public static String globalASCIIMap(List<Double> values, double xstep,double ystep){
		int k = 0;
		StringBuffer sb = new StringBuffer();
		for (double i = -90; i < 90; i += ystep) {
			for (double j = -180; j < 180; j += xstep) {
				double value = values.get(k);
				if (Double.isNaN(value) )
					sb.append(" ");
				else
					sb.append("_");
				k++;
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	//values map with inverted y axis
	public static String globalASCIIMap(double[][] values){
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = values.length-1; i >0 ; i --) {
			for (int j = 0; j < values[0].length; j ++) {
				double value = values[i][j];
				if (Double.isNaN(value) )
					sb.append(" ");
				else
					sb.append("_");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
}
