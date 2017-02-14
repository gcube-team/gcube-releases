package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;

public class RegressionTestGeoServerDevGroup {

	
public static void main(String[] args){
		
		//set test table
		String testTable = "overallTimeSeries_ts_79fae010_310b_11df_b8b3_aa10916debe6_f3761ffc_55e1_4e85_879b_ded8752f6926";

		//setup the information object
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
//		gisInfo.setGisUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		//setup the main layer to visualize
		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle("Species_prob");
		gisLayer1.setLayerName(testTable);
		
		//setup the group
		GISGroupInformation gisGroup = new GISGroupInformation();
		//gisGroup.setGroupName("testGroupJP"+UUID.randomUUID());
		gisGroup.setGroupName("testGroupJPa6968c22-6570-49ac-97e6-7b0c59d4649f");
		System.out.println("GROUPNAME: "+gisGroup.getGroupName());
		gisGroup.setTemplateGroupName(GISOperations.TEMPLATEGROUP);
		//choose if this is a template group (depht, salinity etc) or a single layer group 
		gisGroup.setTemplateGroup(false);

		//Standard Style - not used in this example
		GISStyleInformation style = new GISStyleInformation();
		style.setStyleName("newstyle4overallTimeSeries_ts_79fae010_310b_11df_b8b3_aa10916debe6_f3761ffc_55e1_4e85_879b_ded8752f6926");
		
		//CREATION OF A NEW STYLE

		//add the layer to the visualizing ones
		gisInfo.addLayer(gisLayer1);
		//add the group to the generating ones
		gisInfo.setGroup(gisGroup);
		//associate the style to the layer
		gisInfo.addStyle(gisLayer1.getLayerName(), style);

		try {
			new GISOperations().createNewGroupOnGeoServer(gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		try {
//			generateStyle(gisInfo,newstyle);
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
		
		
	}

}
