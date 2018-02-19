package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class OpenBioMetadataInsertDev {
	
	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
	
	/*
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geoserverurl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
	*/
	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String user = "admin";
	static String password = "admin";
	
	
	
	
	public static void main(String[] args) throws Exception{
		examplelayer("Rainfall OpenModeller", "rain_coolest.tiff");
		examplelayer("Average Temperature OpenModeller", "temp_avg.tiff");
	}
	
	
		

	private static void examplelayer(String layername, String filename) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle(layername);
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.5);
		metadataInserter.setAbstractField("Example layer from OpenModeller. OpenModeller aims to provide a flexible, user friendly, cross-platform environment where the entire process of conducting a fundamental niche modeling experiment can be carried out. Hosted on the D4Science Thredds Catalog: http://thredds.d4science.org/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("D4Science","EUBrazilOpenBio","OpenModeller",layername,filename,"Thredds");
		metadataInserter.setAuthor("D4Science");
		String [] urls = {"http://thredds.d4science.org/thredds/fileServer/public/netcdf/"+filename};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	
}
