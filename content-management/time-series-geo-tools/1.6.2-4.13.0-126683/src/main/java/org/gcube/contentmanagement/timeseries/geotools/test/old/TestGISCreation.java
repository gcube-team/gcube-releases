package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.awt.Color;

import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation.Scales;

public class TestGISCreation {

	
public static void main(String[] args){
		
		//set test table
		String testTable = "overallTimeSeries_ts_82ce8dc0_2d07_11df_b8b3_aa10916debe6_d0eb505c_d6c5_400f_9c43_05beb420e07a";
		
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
		
		
		
		//CREATION OF A NEW STYLE
		GISStyleInformation newstyle = new GISStyleInformation();
		newstyle.setStyleName("newstylejptest222");
		Color c1 = Color.green;
		Color c2 = Color.blue;
		newstyle.setGradientBase(c1);
		newstyle.setGradientMax(c2);

		newstyle.setMax(96482.0);
		newstyle.setMin(14.0);
		newstyle.setNumberOfClasses(5);
		newstyle.setScaleType(Scales.linear);
		newstyle.setStyleAttribute("referencevalue");
		newstyle.setValuesType(Double.class);
		
		//setup the group
		GISGroupInformation gisGroup = new GISGroupInformation();
		gisGroup.setGroupName("testGroupJP");
		gisGroup.setTemplateGroupName(GISOperations.TEMPLATEGROUP);
		//choose if this is a template group (depht, salinity etc) or a single layer group 
		gisGroup.setTemplateGroup(false);
		//add the layer to the visualizing ones
		gisInfo.addLayer(gisLayer1);
		//add the group to the generating ones
		gisInfo.setGroup(gisGroup);
		//associate the style to the layer
		gisInfo.addStyle(gisLayer1.getLayerName(), newstyle);

		new GISOperations().generateGisMap(gisInfo);
		
	}

}
