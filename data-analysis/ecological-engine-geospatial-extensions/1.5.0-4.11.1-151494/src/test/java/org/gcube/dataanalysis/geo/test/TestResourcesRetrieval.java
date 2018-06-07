package org.gcube.dataanalysis.geo.test;

import java.net.URL;

import org.gcube.dataanalysis.geo.charts.GeoMapChart;

public class TestResourcesRetrieval {

	
	public static void main(String args[]){
		
		URL is = GeoMapChart.class.getClassLoader().getResource("raster_res/templatelayerres05.asc");
		System.out.println(is.getPath());
		
	}
}
