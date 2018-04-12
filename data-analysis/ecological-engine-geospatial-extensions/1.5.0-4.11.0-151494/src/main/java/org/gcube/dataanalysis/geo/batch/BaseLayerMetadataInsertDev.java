package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class BaseLayerMetadataInsertDev {
	
	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
	static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	/*
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geoserverurl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
	*/
	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String user = "admin";
	static String password = "admin";
//	static String workspace= "aquamaps";
	static String workspace= "timeseriesgisdb";
 	
	public static void main(String[] args) throws Exception{
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
		
		
		faoarea(metadataInserter);
		metadataInserter.insertMetaData();
		
		
		eezall(metadataInserter);
		metadataInserter.insertMetaData();
		
		lme(metadataInserter);
		metadataInserter.insertMetaData();
//		gebco();
		
		meow(metadataInserter);
		metadataInserter.insertMetaData();
				
		ices(metadataInserter);
		metadataInserter.insertMetaData();
		
		longhurst(metadataInserter);
		metadataInserter.insertMetaData();
		
		ihovseez(metadataInserter);
		metadataInserter.insertMetaData();
		
		iho(metadataInserter);
		metadataInserter.insertMetaData();
		
		
		/*
		DepthMeanAnnual(metadataInserter);
		metadataInserter.insertMetaData();
		
		SSTAnMean(metadataInserter);
		metadataInserter.insertMetaData();
		SalinityMean(metadataInserter);
		metadataInserter.insertMetaData();
		PrimProdMean(metadataInserter);
		metadataInserter.insertMetaData();
		environments(metadataInserter);
		metadataInserter.insertMetaData();
		IceConAnn(metadataInserter);
		metadataInserter.insertMetaData();
		*/
		
		
	}
	
	// eezall	
		private static void eezall(GenericLayerMetadata metadataInserter ){
			metadataInserter.setTitle("Exclusive Economic Zones Boundaries (EEZ)");
			metadataInserter.setLayerName(workspace+":WorldEEZv72012HR");
			metadataInserter.setResolution(0);
			metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
			metadataInserter.setAbstractField("VLIZ (2012). Maritime Boundaries Geodatabase, version 7. Available online at http://www.marineregions.org/. Consulted on 2013-06-05. This dataset represents Exclusive Economic Zones (EEZ) of the world. Up to now, there was no global public domain cover available. Therefore, the Flanders Marine Institute decided to develop its own database. The database includes two global GIS-layers: one contains polylines that represent the maritime boundaries of the world countries, the other one is a polygon layer representing the Exclusive Economic Zone of countries. The database also contains digital information about treaties. Please note that the EEZ shapefile also includes the internal waters of each country");
			metadataInserter.setCustomTopics("i-Marine","Exclusive Economic Zones","VLIZ");
				
		}
		
	// lme
	private static void lme(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("Large Marine Ecosystems of the World");
		metadataInserter.setLayerName(workspace+":lmes64");
		metadataInserter.setResolution(0);
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setAbstractField("This dataset represents the Large Marine Ecosystems of the world. It was composed by the National Oceanic and Atmospheric Administration (NOAA). The dataset exists as a polygon and as a polyline layer. The dataset can be downloaded from http://www.edc.uri.edu/lme/gisdata.htm.");
		metadataInserter.setCustomTopics("i-Marine","Large Marine Ecosystems","NOAA");
	}
	
	//meow
	private static void meow(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("Marine Ecoregions of the World, MEOW (Spalding et al., 2007)");
		metadataInserter.setLayerName(workspace+":meowecos");
		metadataInserter.setResolution(0);
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setAbstractField("MEOW is a biogeographic classification of the world's coasts and shelves. It is the first ever comprehensive marine classification system with clearly defined boundaries and definitions and was developed to closely link to existing regional systems. The ecoregions nest within the broader biogeographic tiers of Realms and Provinces. MEOW represents broad-scale patterns of species and communities in the ocean, and was designed as a tool for planning conservation across a range of scales and assessing conservation efforts and gaps worldwide. The current system focuses on coast and shelf areas (as this is where the majority of human activity and conservation action is focused) and does not consider realms in pelagic or deep benthic environment. It is hoped that parallel but distinct systems for pelagic and deep benthic biotas will be devised in the near future. The project was led by The Nature Conservancy (TNC) and the World Wildlife Fund (WWF), with broad input from a working group representing key NGO, academic and intergovernmental conservation partners. (source: http://www.worldwildlife.org/science/ecoregions/marine/item1266.html). Reference: Spalding, M. D. Fox, H. E. Allen, G. R. Davidson, N. Ferdana, Z. A. Finlayson, M. Halpern, B. S. Jorge, M. A. Lombana, A. Lourie, S. A., (2007). Marine Ecoregions of the World: A Bioregionalization of Coastal and Shelf Areas. Bioscience 2007, VOL 57; numb 7, pages 573-584.");
		metadataInserter.setCustomTopics("i-Marine","Marine Ecoregions of the World","MEOW","Spalding");
	}
	
	//ICES
	private static void ices(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("ICES Ecoregions");
		metadataInserter.setLayerName(workspace+":Ecoregions20090430");
		metadataInserter.setResolution(0);
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setAbstractField("ICES EcoRegions are large-scale management units for the ICES regional seas and are used in advisory reports to segment advice into the different sea areas. The EcoRegions were first referenced by the predecessor to ACOM (Advisory Committee) in 2004 (source: http://www.ices.dk/InSideOut/mayjun09/j.html)");
		metadataInserter.setCustomTopics("i-Marine","ICES Ecoregions","ICES");
	}
	
	//longhurst
	private static void longhurst(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("Longhurst Biogeographical Provinces");
		metadataInserter.setLayerName(workspace+":Longhurstworldv42010");
		metadataInserter.setResolution(0);
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setAbstractField("VLIZ (2009). Longhurst Biogeographical Provinces. Available online at http://www.marineregions.org/.  This dataset represents a partition of the world oceans into provinces as defined by Longhurst (1995; 1998; 2006), and are based on the prevailing role of physical forcing as a regulator of phytoplankton distribution. The dataset represents the initial static boundaries developed at the Bedford Institute of Oceanography, Canada. Note that the boundaries of these provinces are not fixed in time and space, but are dynamic and move under seasonal and interannual changes in physical forcing. At the first level of reduction, Longhurst recognised four principal biomes (also referred to as domains in earlier publications): the Polar Biome, the Westerlies Biome, the Trade-Winds Biome, and the Coastal Boundary Zone Biome. These four Biomes are recognisable in every major ocean basin. At the next level of reduction, the ocean basins are partitioned into provinces, roughly ten for each basin. These partitions provide a template for data analysis or for making parameter assignments on a global scale. Please refer to Longhurst's publications when using these shapefiles. Consulted on 2013-06-05. Reference: References:  Longhurst, A.R et al. (1995). An estimate of global primary production in the ocean from satellite radiometer data. J. Plankton Res. 17, 1245-1271. Longhurst, A.R. (1995). Seasonal cycles of pelagic production and consumption. Prog. Oceanogr. 36, 77-167. Longhurst, A.R. (1998). Ecological Geography of the Sea. Academic Press, San Diego. 397p. (IMIS). Longhurst, A.R. (2006). Ecological Geography of the Sea. 2nd Edition. Academic Press, San Diego, 560p.");
		metadataInserter.setCustomTopics("i-Marine","Longhurst Biogeographical Provinces","Longhurst","VLIZ");
	}
	
	
	//EEZ vs IHO
	private static void ihovseez(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("Marineregions: the intersect of the Exclusive Economic Zones and IHO areas");
		metadataInserter.setLayerName(workspace+":EEZIHOunionv2");
		metadataInserter.setResolution(0);
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setAbstractField("VLIZ (2010). Intersect of IHO Sea Areas and Exclusive Economic Zones (version 1). Available online at http://www.marineregions.org/. Consulted on 2013-06-05. VLIZ (2012). Intersect of IHO Sea Areas and Exclusive Economic Zones (version 2). Available online at http://www.marineregions.org/. Consulted on 2013-06-05. The maritime boundaries provide a useful tool to limit national marine areas, but do not include information on marine regional and sub regional seas. This hampers the usage of these boundaries for implementing nature conservation strategies or analyzing marine biogeographic patterns. For example, a species occurring in the German EEZ can live in the North Sea, the Baltic Sea or Kattegat area. Each of these different marine areas has very distinct hydrological, oceanographic and ecological conditions. Therefore, by combining the information on regional seas and national maritime boundaries, we can include both a environmental and managerial factor. We propose to overlay the information from the maritime boundaries (the Exclusive Economic Zones) with the IHO Sea Areas (IHO, 1953). This map including the global oceans and seas, has been drafted for hydrographic purposes, but also gives an unequivocal and acceptable distinction of the regional seas and oceans from an oceanographic point of view. The combination of these two boundaries allows us for example to create national regional sea areas for the global ocean.");
		metadataInserter.setCustomTopics("i-Marine","Marineregions: the intersect of the Exclusive Economic Zones and IHO areas","Marineregions","VLIZ","EEZ","IHO");
	}
	
	
	//iho
		private static void iho(GenericLayerMetadata metadataInserter ){
			metadataInserter.setTitle("IHO Sea Areas");
			metadataInserter.setLayerName(workspace+":WorldSeas");
			metadataInserter.setResolution(0);
			metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
			metadataInserter.setAbstractField("VLIZ (2005). IHO Sea Areas. Available online at http://www.marineregions.org/. Consulted on 2013-06-05. This dataset represents the boundaries of the major oceans and seas of the world. The source for the boundaries is the publication 'Limits of Oceans & Seas, Special Publication No. 23' published by the IHO in 1953. The dataset was composed by the Flanders Marine Data and Information Centre. NB: The Southern Ocean is not included in the IHO publication and its limits are subject of discussion among the scientific community. The Flanders Marine Institute acknowledges the controversy around this subject but decided to include the Southern Ocean in the dataset as this term is often used by scientists working in this area.");
			metadataInserter.setCustomTopics("i-Marine","IHO Sea Areas","Marineregions","VLIZ","IHO");
		}
		
		
	//gebco
	private static void gebco() throws Exception{
		NetCDFMetadata metadataInserter = new NetCDFMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setThreddsCatalogUrl("http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setTitle("General Bathymetric Chart of the Oceans (GEBCO) 3D");
		metadataInserter.setLayerName("z");
		metadataInserter.setLayerUrl("http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/gebco_08_OCEANS_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc");
		metadataInserter.setSourceFileName("gebco_08_OCEANS_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc");
		metadataInserter.setResolution(0.0083);
		metadataInserter.setAbstractField("The GEBCO_08 Grid: a global 30 arc-second grid. The General Bathymetric Chart of the Oceans (GEBCO) consists of an international group of experts who work on the development of a range of bathymetric data sets and data products, including gridded bathymetric data sets, the GEBCO Digital Atlas, the GEBCO world map and the GEBCO Gazetteer of Undersea Feature Names.");
		metadataInserter.setCustomTopics("i-Marine","General Bathymetric Chart of the Oceans","GEBCO","3D");
		metadataInserter.insertMetaData();
	}
	
	// IceConAnn 
	private static void IceConAnn(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("IceConAnn");
		metadataInserter.setLayerName("aquamaps:iceConAnn");
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setAbstractField("Mean Annual Ice Concentration");
		metadataInserter.setCustomTopics("i-Marine","Mean Annual Ice Concentration");
	}
	//DepthMeanAnnual
	private static void DepthMeanAnnual(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("DepthMeanAnnual");
		metadataInserter.setLayerName("aquamaps:DepthMeanAnnual");
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setAbstractField("Mean Depth at half-degree resolution");
		metadataInserter.setCustomTopics("i-Marine","Mean Depth");
	}
	
	//faoarea 
	private static void faoarea(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("FAO Fishing Areas");
		metadataInserter.setResolution(0);
		metadataInserter.setLayerName(workspace+":WorldFaoZones");
		metadataInserter.setCategoryTypes("_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setAbstractField("The dataset represents the boundaries of the FAO Fishing Areas. The source for the boundaries is the description that can be found on the FAO website. The dataset was composed by the Flanders Marine Data and Information Centre.");
		metadataInserter.setCustomTopics("i-Marine","FAO Areas");
	}
	
	//SSTANMean
	private static void SSTAnMean(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("SSTAnMean");
		metadataInserter.setLayerName("aquamaps:sstAnMean");
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setAbstractField("Mean Annual Sea Surface Temperature at half-degree resolution");
		metadataInserter.setCustomTopics("i-Marine","Mean Annual Sea Surface Temperature");
	}
	
	//SalinityMean
	private static void SalinityMean(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("SalinityMean");
		metadataInserter.setLayerName("aquamaps:salinityMean");
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setAbstractField("Mean Annual Salinity at half-degree resolution");
		metadataInserter.setCustomTopics("i-Marine","Mean Annual Salinity");
	}
	
	// PrimProdMean
	private static void PrimProdMean(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("PrimProdMean");
		metadataInserter.setLayerName("aquamaps:primProdMean");
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.BIOTA.name()+"_");
		metadataInserter.setAbstractField("Mean Annual Primary Production at half-degree resolution");
		metadataInserter.setCustomTopics("i-Marine","Mean Annual Primary Production");
	}
	
	 //environments 
	private static void environments(GenericLayerMetadata metadataInserter ){
		metadataInserter.setTitle("environments");
		metadataInserter.setLayerName("aquamaps:environments");
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.BIOTA.name()+"_");
		metadataInserter.setAbstractField("Aggregated environmental and biota data at half-degree resolution");
		metadataInserter.setCustomTopics("i-Marine","Aggregated environmental and biota data");
	}
	
	
	
}
