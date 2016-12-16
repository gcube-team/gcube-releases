package org.gcube.contentmanagement.graphtools.tests.old;

import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.abstracts.SamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.contentmanagement.graphtools.data.databases.CommonDBExtractor;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.hibernate.SessionFactory;


public class TestDBExtractor {

	
	
	public static void main(String[] args) throws Exception{
		
		
		SessionFactory referenceDBSession = DatabaseFactory.initDBConnection("./hibernate.cfg.xml");
		
		CommonDBExtractor extractor = new CommonDBExtractor(referenceDBSession);
		
		String table = "ts_161efa00_2c32_11df_b8b3_aa10916debe6";
		String xDimension = "field5";
		String groupDimension = "field1";
		String yValue = "field6";
		String speciesColumn = "field3";
		String filter1 = "Brown seaweeds";
		String filter2 = "River eels";
		
		Map<String, SamplesTable> samplesMap = extractor.getMultiDimTemporalTables(table, xDimension, groupDimension, yValue, speciesColumn, filter1, filter2);
		
		System.out.println("MAP EXTRACTED : \n"+samplesMap.toString());
		
		GraphGroups graphgroups = new GraphGroups();
		
		
		for (String key:samplesMap.keySet()){
			
			SamplesTable stable = samplesMap.get(key);
			List<Point<? extends Number, ? extends Number>> singlegraph = GraphConverter2D.transformTable(stable);
		
			GraphData grd = new GraphData(singlegraph,true);
			graphgroups.addGraph("Distribution for "+key, grd);
			
		}
		
		System.out.println("finished!");
		
	}
}
