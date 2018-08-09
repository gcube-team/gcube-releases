package org.gcube.dataanalysis.geo.batch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDFDataExplorer;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.OGCFormatter;
import org.opengis.metadata.identification.TopicCategory;

import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

public class MetadataInsertDevExample {

	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
//	
	static String user = "admin";
	static String password = "Geey6ohz";

	static String threddsbaseURL= "thredds-d-d4s.d4science.org";
	static String threddsURL = "http://"+threddsbaseURL+"/thredds/catalog/public/netcdf/catalog.xml";
	
		
	//gebco
	private static void RasterExample() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		String title = "Etna Volcano SAR Analysis ";
		String filename = "geo_filt_070620-070725-sim_HDR_4rlks.nc";
		String layername = "band_1";
		
		int minusInt = filename.indexOf("-");
		int minusInt2 = filename.lastIndexOf("-");
		String datastart = filename.substring("geo_filt_".length(),minusInt);
		String dataend  = filename.substring(minusInt,minusInt2);
		System.out.println("DataStart: "+datastart);
		System.out.println("DataEnd: "+dataend);
		
		metadataInserter.setTitle(title);
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.0083);
		metadataInserter.setAbstractField("SAR analysis of the Etna volcano. Produced by the Istituto Nazionale di Geofisica e Vulcanologia (INGV). Hosted on the D4Science Thredds Catalog: "+threddsURL);
		metadataInserter.setCustomTopics("D4Science","ENVRI","Etna","SAR Analysis","Istituto Nazionale di Geofisica e Vulcanologia","INGV","Thredds");
		metadataInserter.setAuthor("D4Science");
		
		String url = OGCFormatter.getOpenDapURL(threddsURL, filename);
		System.out.println("URL "+url);
		
		GridDataset gds = ucar.nc2.dt.grid.GridDataset.open(url);
		List<GridDatatype> gridTypes = gds.getGrids();
		GridDatatype gdt = gridTypes.get(0);
		
		double minX = NetCDFDataExplorer.getMinX(gdt.getCoordinateSystem());
		double maxX = NetCDFDataExplorer.getMaxX(gdt.getCoordinateSystem());
		double minY = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
		double maxY = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
		
		System.out.println("minX: "+minX+" minY: "+minY+" maxX:"+maxX+" maxY:"+maxY);
		
		String wms = OGCFormatter.getWmsNetCDFUrl(url, layername, OGCFormatter.buildBoundingBox(minX, minY, maxX, maxY)).replace("width=676", "width=640").replace("height=330", "height=480");
		
		System.out.println("WMS "+wms);
		String wcs = OGCFormatter.getWcsNetCDFUrl(url, layername, OGCFormatter.buildBoundingBox(minX, minY, maxX, maxY)).replace("width=676", "width=640").replace("height=330", "height=480");
		System.out.println("WCS "+wcs);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/"+filename,wms,wcs,url};
		
		String [] protocols = {"HTTP","WMS","WCS","OPeNDAP"};
		
		DateFormat formatter = new SimpleDateFormat("yyMMdd");
		Date datestart = formatter.parse(datastart);
		Date dateend = formatter.parse(dataend);
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		metadataInserter.setXLeftLow(minX);
		metadataInserter.setYLeftLow(minY);
		metadataInserter.setXRightUpper(maxX);
		metadataInserter.setYRightUpper(maxY);
		
		CoordinateAxis xAxis = gdt.getCoordinateSystem().getXHorizAxis();
		CoordinateAxis yAxis = gdt.getCoordinateSystem().getYHorizAxis();
		
		double resolutionX = Math.abs((double) (xAxis.getMaxValue() - xAxis.getMinValue()) / (double) xAxis.getShape()[0]);
		double resolutionY = Math.abs((double) (yAxis.getMaxValue() - yAxis.getMinValue()) / (double) yAxis.getShape()[0]);
		
		metadataInserter.setResolution(Math.max(resolutionX, resolutionY));
		
		System.out.println("Resolution: "+Math.max(resolutionX, resolutionY));
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void VectorMapExample() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature Isoline at 3 km depth");
		metadataInserter.setAbstractField("Temperature map at 3km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:iso_3000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:iso_3000&styles=&bbox=7.59535694122314,36.5945739746094,17.2513008117676,46.1617622375488&width=512&height=507&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:iso_3000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
}
