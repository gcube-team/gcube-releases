package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.awt.Color;
import java.util.UUID;

import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation.Scales;

public class RegressionTestGeoServerProd {

	
public static void main(String[] args){
		
		//set test table
		String testTable = "f2fe089a0_642b_11df_822d_c9c25747d1f8";
//		String testTable = "species2010_06_07_11_27_36_827";
		
//		String testTable = "default2010_06_01_19_00_44_125";
		
		//setup the information object
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
//		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		//setup the main layer to visualize
		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle("Species_prob");
		gisLayer1.setLayerName(testTable);
		
		//setup the group
		GISGroupInformation gisGroup = new GISGroupInformation();
		gisGroup.setGroupName("testGroupJP");
		gisGroup.setTemplateGroupName(GISOperations.TEMPLATEGROUP);
		//choose if this is a template group (depht, salinity etc) or a single layer group 
		gisGroup.setTemplateGroup(false);
		
		
		//Standard Style - not used in this example
		GISStyleInformation style = new GISStyleInformation();
		style.setStyleName(GISOperations.DEFAULTSTYLE);
		//
		
		//CREATION OF A NEW STYLE
		GISStyleInformation newstyle = new GISStyleInformation();
		newstyle.setStyleName("newstylejptest"+UUID.randomUUID());
		Color c1 = Color.green;
		Color c2 = Color.blue;
		newstyle.setGradientBase(c1);
		newstyle.setGradientMax(c2);
		
		newstyle.setMax(1.00);
		newstyle.setMin(0.00);
		
		/*
		newstyle.setMax(15.00);
		newstyle.setMin(1.00);
		*/
		newstyle.setNumberOfClasses(2);
		newstyle.setScaleType(Scales.linear);
		newstyle.setStyleAttribute("probability");
//		newstyle.setStyleAttribute("maxspeciescountinacell");
		newstyle.setValuesType(Double.class);
//		newstyle.setValuesType(Integer.class);
		//
		
		//add the layer to the visualizing ones
		gisInfo.addLayer(gisLayer1);
		//add the group to the generating ones
		gisInfo.setGroup(gisGroup);
		//associate the style to the layer
//		gisInfo.addStyle(gisLayer1.getLayerName(), newstyle);
		gisInfo.addStyle(gisLayer1.getLayerName(), style);
		
		
		try {
			new GISOperations().createNewGroupOnGeoServer(gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		generateGisMap(gisInfo);
		
		//OLD CODE
		/*
		try {
			createLayers(gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		/*
		try {
//			generateStyle(gisInfo,newstyle);
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
		
		
	}

}
