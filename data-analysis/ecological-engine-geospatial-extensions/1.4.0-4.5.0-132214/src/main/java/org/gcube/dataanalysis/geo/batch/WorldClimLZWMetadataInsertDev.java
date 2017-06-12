package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class WorldClimLZWMetadataInsertDev {
	
	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
	
	/*
	 * static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geoserverurl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
	*/
	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String user = "admin";
	static String password = "admin";
	
	public static void main(String[] args) throws Exception{
		
		for (int i=1;i<20;i++) {
			worldclim(i);
//			break;
		}
	}
	
	
		
	//gebco
	private static void worldclim(int index) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("WorldClimBiolzw"+index);
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.0083);
		metadataInserter.setAbstractField("WorldClim is a set of global climate layers (climate grids) with a spatial resolution of about 1 square kilometer. The data can be used for mapping and spatial modeling in a GIS or with other computer programs and use LZW compression. Hijmans, R.J., S.E. Cameron, J.L. Parra, P.G. Jones and A. Jarvis, 2005. Very high resolution interpolated climate surfaces for global land areas. International Journal of Climatology 25: 1965-1978. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("D4Science","EUBrazilOpenBio","WorldClim","LZW","WorldClimBiolzw"+index+".tiff","Thredds");
		metadataInserter.setAuthor("D4Science");
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/WorldClimBiolzw"+index+".tiff"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	
}
