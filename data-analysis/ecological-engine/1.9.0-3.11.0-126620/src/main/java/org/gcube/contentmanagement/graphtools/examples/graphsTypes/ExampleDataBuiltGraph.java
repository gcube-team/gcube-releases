package org.gcube.contentmanagement.graphtools.examples.graphsTypes;

import java.util.ArrayList;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.data.GraphSamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;



public class ExampleDataBuiltGraph {

	
	public static void main(String[] args) throws Exception{
		
		
		
		
		ArrayList<String> l = new ArrayList<String>();
		ArrayList<Double> v = new ArrayList<Double>();
		ArrayList<String> stationaryLables = new ArrayList<String>();
		
		for (int i=0;i<10;i++){
			double d= 10*Math.random();
			v.add(d);
			l.add("value "+i);
			if (d>5){
				stationaryLables.add("statpoint"+i);
			}
		}
		GraphSamplesTable gts = new GraphSamplesTable("Random Series",l,v,true);
		GraphData grd = new GraphData(gts.getGraph(), false);
		GraphGroups graphgroups = new GraphGroups();
		graphgroups.addGraph("Distribution", grd);
		
		
		GraphConverter2D.anotateStationaryPoints(graphgroups,stationaryLables);
		
		GraphConverter2D.getStationaryPoints(grd);
		
		TransectLineGraph series = new TransectLineGraph("");
		series.renderGraphGroup(graphgroups);
		
		
//		series.renderGraphGroup(gg);
	
	}
	
	
		
}
