package org.gcube.contentmanagement.timeseries.geotools.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DistanceCalculator;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.utils.Couple;

public class TimeSeriesAggregator {
	
	ConnectionsManager connManager;

	private static String simpleAggregationQuery = "select distinct %1$s,sum(%2$s) from %3$s group by %1$s";
	//select distinct country,species, sum(quantity) FROM tablename t group by country;";
	private static String splitElementsQuery = "select distinct %1$s,%2$s,sum(%3$s) FROM %4$s t group by %1$s,%2$s order by %2$s";
	
	
	public TimeSeriesAggregator(ConnectionsManager connManager){
		this.connManager = connManager;
	}
	
	//returns a structure like this:
	//aggregationColumnElement->(OtherColumnElement,value)
	public Map<String,List<Couple>> aggregateTimeSeries(String aggregationcolumn,String informationColumn,String quantitiesColumn,String timeSeriesName) throws Exception{
		Map<String,List<Couple>> OutMap = new HashMap<String, List<Couple>>();
		
		String query = String.format(splitElementsQuery,aggregationcolumn,informationColumn,quantitiesColumn,timeSeriesName);
		AnalysisLogger.getLogger().warn("aggregateTimeSeries->QUERY:"+query);
		List<Object> aggregatedTS = connManager.TimeSeriesQuery(query);
		for (Object row:aggregatedTS){
			Object[] singleRow = (Object[]) row;
			String place = ""+singleRow[0];
			String information = ""+singleRow[1];
			String quantity = ""+singleRow[2];
			List<Couple> clist = OutMap.get(information);
			if (clist==null){
				List<Couple> informationList = new ArrayList<Couple>();
				informationList.add(new Couple(place,quantity));
				OutMap.put( information, informationList);
			}
			else{
				clist.add(new Couple(place, quantity));
			}
		}
		
		/*
		 * little check for similarity among the entries - only for debug purposes
		 
		for (String h:OutMap.keySet()){
			for (String t:OutMap.keySet()){
				DistanceCalculator d = new DistanceCalculator();
				double cd = d.CD(false,h, t, true , true);
				if (!h.equals(t)&&(cd>0.80))
					AnalysisLogger.getLogger().warn("aggregateTimeSeries->SIMILARITY FOUND:"+h+" vs "+t);
			}
		}
		*/
		
		return OutMap;
	}
	
	
	public Map<String,String> aggregateTimeSeries(String aggregationcolumn,String quantitiesColumn,String timeSeriesName) throws Exception{
		Map<String,String> OutMap = new HashMap<String, String>();
		
		String query = String.format(simpleAggregationQuery,aggregationcolumn,quantitiesColumn,timeSeriesName);
		AnalysisLogger.getLogger().trace("aggregateTimeSeries->Aggregation query "+query);
		List<Object> aggregatedTS = new ArrayList<Object>();
		aggregatedTS = connManager.TimeSeriesQuery(query);
		if (aggregatedTS==null)
			throw new Exception("Error: Wrong Parameters, invalid quantity column");

		for (Object row:aggregatedTS){
			Object[] singleRow = (Object[]) row;
			String criteria = ""+singleRow[0];
			String quantity = ""+singleRow[1];
			OutMap.put(criteria, quantity);
		}
		
		return OutMap;
	}
	
}
