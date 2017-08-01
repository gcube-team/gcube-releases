package org.gcube.dataanalysis.geo.batch;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class BaseLayerAddonMetadataInsertDev {
	
	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
	static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	/*
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geoserverurl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
	*/
	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String user = "admin";
	static String password = "Geey6ohz";
	static String workspace= "aquamaps";
//	static String workspace= "timeseriesgisdb";
 	
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setGeoserverUrl(geoserverurl);
		metadataInserter.setResolution(0.5);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-85.5);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(85.5);
		
		
		terrestrialecoregions(metadataInserter);
		metadataInserter.insertMetaData();
		/*
		nafo(metadataInserter);
		metadataInserter.insertMetaData();
		tdwg(metadataInserter);
		metadataInserter.insertMetaData();
		seavox(metadataInserter);
		metadataInserter.insertMetaData();
		continental(metadataInserter);
		metadataInserter.insertMetaData();
		*/
		
	}
	
	//TNC
			private static void terrestrialecoregions(GenericLayerMetadata metadataInserter ){
				metadataInserter.setTitle("Terrestrial Ecoregional Boundaries (TNC)");
				metadataInserter.setLayerName(workspace+":tnc_terr_ecoregions");
				metadataInserter.setResolution(0);
				metadataInserter.setCategoryTypes("_"+TopicCategory.BOUNDARIES.name()+"_"+"_"+TopicCategory.BIOTA.name()+"_");
				metadataInserter.setAbstractField("Global Ecoregions, Major Habitat Types, Biogeographical Realms and The Nature Conservancy Terrestrial Assessment Units as of December 14, 2009. Developed originally by Olson, D. M. and E. Dinerstein (2002), Bailey (1995) and Environment Canada (Wiken, 1986), these data layers were modified by The Nature Conservancy (TNC) to be used in its Biodiversity Planning exercises in the process known as Ecoregional Assessments.");
				metadataInserter.setCustomTopics("i-Marine","TNC","Terrestrial Ecoregional Boundaries");
			}
			
	//nafo
		private static void nafo(GenericLayerMetadata metadataInserter ){
			metadataInserter.setTitle("The NAFO Convention Area");
			metadataInserter.setLayerName(workspace+":Divisions");
			metadataInserter.setResolution(0);
			metadataInserter.setCategoryTypes("_"+TopicCategory.BOUNDARIES.name()+"_");
			metadataInserter.setAbstractField("The NAFO Convention Area encompasses a very large portion of the Atlantic Ocean and includes the 200-mile zones of Coastal States jurisdiction (USA, Canada, St. Pierre et Miquelon and Greenland). The total area under NAFO's Convention is 6,551,289 km2. Geographic delineations of the NAFO Convention Area are described in the NAFO Convention and are included in Marine Regions.");
			metadataInserter.setCustomTopics("i-Marine","NAFO","Convention Area");
		}
		
	//tdwg
	private static void tdwg(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("TDWG Geography Level-1");
		metadataInserter.setLayerName(workspace+":level1");
		metadataInserter.setResolution(0);
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setAbstractField("R. K. Brummitt with assistance from F. Pando, S. Hollis, N. A. Brummitt and others. Plant Taxonomic Database Standards No. 2.ed. 2. World Geographical Scheme for Recording Plant Distributions, ed. 2. 2001. xv, 137 pp.; 17 maps. This scheme meets the need for a standard yet adaptable system of geographical units for use in recording plant distributions and arranging specimens. Because a purely political arrangement cannot meet all the needs of botanists, the scheme's arrangement compromises between a politically and a phytogeographically oriented system. It identifies geographic units worldwide in a four-level hierarchy, incorporating continents, regions, provinces and countries. Each geographical unit at each level has its own numeric or alphanumeric code.");
		metadataInserter.setCustomTopics("i-Marine","TDGW","Geography Shapefiles");
	}
	
	//seavox
		private static void seavox(GenericLayerMetadata metadataInserter ){
			metadataInserter.setTitle("The SeaVoX Salt and Fresh Water Body Gazetteer");
			metadataInserter.setLayerName(workspace+":SeaVoX_sea_areas_polygons_v14");
			metadataInserter.setResolution(0);
			metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
			metadataInserter.setAbstractField("The SeaVoX Salt and Fresh Water Body Gazetteer. The data set consists of a polygon file defining the limits of water bodies from the SeaVoX Salt and Fresh Water Body Gazetteer. SeaVoX is a combined SeaDataNet and MarineXML vocabulary content governance group, it is moderated by BODC. This data set defines the geographic extent of the terms specified by the SeaVoX vocabulary governance to describe coherent regions of the hydrosphere. Includes land masses enclosing freshwater bodies. The coastline data set used in the shapefile is taken from the World Vector Shoreline data set (scale 1:250,000).");
			metadataInserter.setCustomTopics("i-Marine","SeaVox","Salt and Fresh Water");
		}

		//continental 
	private static void continental(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("Continental margins between 140m and 3500m depth (IFREMER - COMARGE, 2009)");
		metadataInserter.setResolution(0);
		metadataInserter.setLayerName(workspace+":ContinentalMargins");
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_"+"_"+TopicCategory.BOUNDARIES.name()+"_");
		metadataInserter.setAbstractField("IFREMER (Vion, A.; Menot, L.), (2009). This layer has been prepared in the framework of COMARGE, one of the field project of the Census of Marine Life. It is intended to represent continental margins worldwide, with the exclusion of the continental shelf. The continental margins have been defined based on bathymetry and expert opinion. The upper margin of the boundary has been set at 140 m depth, which is the average depth of the shelf break, except in Antarctica where the shelf break goes deeper and the upper boundary has been set up at 500 m. The lower boundary has been set at 3500 m depth. Both isobaths were extracted from S2004 Bathymetry (a global bathymetry at 1 arc-minute resolution). The upper and lower boundaries were manually edited to follow the contour of continental margins in particular cases.");
		metadataInserter.setCustomTopics("i-Marine","Continental margins between 140m and 3500m depth","IFREMER","COMARGE");
	}
		
	
	
}
