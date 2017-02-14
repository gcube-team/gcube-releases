package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import java.awt.geom.Point2D.Double;

public class SurfaceTemperatureExtractor {

	
	
	public float [] getSST(Double[] points){
			
			int size = points.length;
			
			float [] Array = new float [size];

			for (int i = 0;i<size;i++){
//				short stt = (short)(Math.random()*40f);
				short stt = -9999;
				Array[i] = stt;
			}
			
			return Array;
	}

}
