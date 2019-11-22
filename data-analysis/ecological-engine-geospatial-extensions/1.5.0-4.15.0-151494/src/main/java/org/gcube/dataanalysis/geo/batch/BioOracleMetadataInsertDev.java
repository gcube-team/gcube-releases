package org.gcube.dataanalysis.geo.batch;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class BioOracleMetadataInsertDev {
	
	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
//	static String geonetworkurl = "http://geoserver-last.d4science-ii.research-infrastructures.eu/geonetwork/";
	/*
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geoserverurl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
	*/
	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String user = "admin";
	static String password = "admin";
	
	public static void main(String[] args) throws Exception{
		calcite();
		chlorophyllMax();
		chlorophyllMean();
		chlorophyllMin();
		chlorophyllRange();
		cloudMax();
		cloudMean();
		cloudMin();
		diffuseattenuationMax();
		diffuseattenuationMean();
		diffuseattenuationMin();
		dissox();
		nitrateMean();
		parmax();
		parmean();
		ph();
		phosphate();
		salinity();
		silicate();
		sstmax();
		sstmean();
		sstmin();
		sstrange();
		
	}
	
	
	private static void calcite() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Calcite Concentration (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Calcite concentration (mol/m^3). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Calcite concentration","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/calcite.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void chlorophyllMax() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Chlorophyll A Concentration (Max)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Maximum Chlorophyll A Concentration (mg/m^3). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Maximum Chlorophyll A Concentration","Chlorophyll","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/chlomax.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void chlorophyllMean() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Chlorophyll A Concentration (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Chlorophyll A Concentration (mg/m^3). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Chlorophyll A Concentration","Chlorophyll","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/chlomean.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void chlorophyllMin() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Chlorophyll A Concentration (Min)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Minimum Chlorophyll A Concentration (mg/m^3). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Minimum Chlorophyll A Concentration","Chlorophyll","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/chlomin.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void chlorophyllRange() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Chlorophyll A Concentration (Range)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Range Chlorophyll A Concentration (mg/m^3). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Range Chlorophyll A Concentration","Chlorophyll","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/chlorange.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void cloudMax() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Cloud Fraction (Max)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Maximum Cloud Fraction (percentage). Aggregated between [2005-2010]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Maximum Cloud Fraction","Cloud Fraction","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2005");
		Date dateend = formatter.parse("2010");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/cloudmax.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void cloudMin() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Cloud Fraction (Min)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Minimum Cloud Fraction (percentage). Aggregated between [2005-2010]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Minimum Cloud Fraction","Cloud Fraction","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2005");
		Date dateend = formatter.parse("2010");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/cloudmin.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void cloudMean() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Cloud Fraction (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Cloud Fraction (percentage). Aggregated between [2005-2010]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Cloud Fraction","Cloud Fraction","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2005");
		Date dateend = formatter.parse("2010");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/cloudmean.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void diffuseattenuationMax() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Diffuse Attenuation Coefficient at 490mm (Max)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Maximum Diffuse Attenuation Coefficient at 490mm (m^-1). Aggregated between [2002-2009].  From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Maximum Diffuse Attenuation Coefficient","Diffuse Attenuation Coefficient","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/damax.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void diffuseattenuationMean() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Diffuse Attenuation Coefficient at 490mm (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Diffuse Attenuation Coefficient at 490mm (m^-1). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Diffuse Attenuation Coefficient","Diffuse Attenuation Coefficient","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/damean.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void diffuseattenuationMin() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Diffuse Attenuation Coefficient at 490mm (Min)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Minimum Diffuse Attenuation Coefficient at 490mm (m^-1). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Minimum Diffuse Attenuation Coefficient","Diffuse Attenuation Coefficient","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/damin.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void dissox() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Dissolved Oxygen Concentration (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Dissolved Oxygen Concentration (ml/l). Aggregated between [1898-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Dissolved Oxygen Concentration","Dissolved Oxygen Concentration","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1898");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/dissox.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void nitrateMean() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Nitrate (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Nitrate (umol/l). Aggregated between [1928-2008]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Nitrate","Nitrate","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1928");
		Date dateend = formatter.parse("2008");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/nitrate.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void parmax() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Photosynthetically Available Radiation (Max)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Maximum Photosynthetically Available Radiation (Einstein/m^2/day). Aggregated between [1997-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Maximum Photosynthetically Available Radiation","Photosynthetically Available Radiation","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1997");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/parmax.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void parmean() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Photosynthetically Available Radiation (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Photosynthetically Available Radiation (Einstein/m^2/day). Aggregated between [1997-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Photosynthetically Available Radiation","Photosynthetically Available Radiation","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1997");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/parmean.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void ph() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Ph (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Ph (unitless). Aggregated between [1910-2007]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Ph","Ph","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1910");
		Date dateend = formatter.parse("2007");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/ph.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void phosphate() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Phosphate (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Phosphate (umol/l). Aggregated between [1922-1986]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Phosphate","Phosphate","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1922");
		Date dateend = formatter.parse("1986");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/phosphate.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void salinity() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Salinity (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Salinity (PSS). Aggregated between [1961-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Salinity","Salinity","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1961");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/salinity.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void silicate() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Silicate (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Silicate (umol/l). Aggregated between [1930-2008]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Silicate","Silicate","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1930");
		Date dateend = formatter.parse("2008");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/silicate.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void sstmax() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Sea Surface Temperature (Max)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Maximum Sea Surface Temperature (°C). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Maximum Sea Surface Temperature","Sea Surface Temperature","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/sstmax.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void sstmean() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Sea Surface Temperature (Mean)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Mean Sea Surface Temperature (°C). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Mean Sea Surface Temperature","Sea Surface Temperature","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/sstmean.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void sstmin() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Sea Surface Temperature (Min)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Minimum Sea Surface Temperature (°C). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Minimum Sea Surface Temperature","Sea Surface Temperature","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/sstmin.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void sstrange() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		
		metadataInserter.setTitle("Bio-Oracle Sea Surface Temperature (Range)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+TopicCategory.OCEANS.name()+"_");
		metadataInserter.setResolution(0.083);
		metadataInserter.setAbstractField("Range Sea Surface Temperature (°C). Aggregated between [2002-2009]. From Bio-Oracle: Tyberghein L., Verbruggen H., Pauly K., Troupin C., Mineur F. & De Clerck O. Bio-ORACLE: a global environmental dataset for marine species distribution modeling. Global Ecology and Biogeography. Hosted on the D4Science Thredds Catalog: http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setCustomTopics("Range Sea Surface Temperature","Sea Surface Temperature","D4Science","i-Marine","Bio-Oracle","Thredds","2D");
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("2002");
		Date dateend = formatter.parse("2009");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/sstrange.asc"};
		String [] protocols = {"HTTP"};
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
}
