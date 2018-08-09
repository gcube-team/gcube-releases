package org.gcube.dataanalysis.geo.batch;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;
import org.opengis.metadata.identification.TopicCategory;

public class OscarMetadataInsertProd {
	
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	//static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
	
	static String user = "admin";
	static String password = "kee9GeeK";
	
	public static void main(String[] args) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		String scope ="/d4science.research-infrastructures.eu/gCubeApps";
//		String scope ="/gcube/devsec";
		ScopeProvider.instance.set(scope);
		String targetScope = scope.substring(scope.lastIndexOf("/")+1);
		System.out.println("target scope "+targetScope);
		GeoNetworkAdministration reader=GeoNetwork.get();
		reader.login(LoginLevel.ADMIN);
		ScopeConfiguration targetConfiguration = null;
		for(ScopeConfiguration configuration : reader.getConfiguration().getExistingConfigurations()){
			if(configuration.getAssignedScope().equals(targetScope)){ 
				targetConfiguration = configuration;
			}
		}
	
		if(targetConfiguration==null)
		throw new MissingConfigurationException("Scope "+targetScope+" has no configuration");	
 
		int targetUserId=UserUtils.getByName(reader.getUsers(),targetConfiguration.getAccounts().get(Account.Type.SCOPE).getUser()).getId();
		int targetGroup=targetConfiguration.getDefaultGroup();

		System.out.println("Target group:"+targetGroup);
		metadataInserter.setGeonetworkGroup(""+targetGroup);
		
		metadataInserter.setResolution(0.3326);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-80);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(80);
		
		Oscar(metadataInserter);
		metadataInserter.insertMetaData();
		
	}
	
	private static void Oscar(GenericLayerMetadata metadataInserter) throws Exception{
		
		metadataInserter.setTitle("Ocean Surface Zonal and Meridional currents between 1992 and 2015 from Ocean Surface Current Analyses Real-time (OSCAR-NASA)");
		
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_"+"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_");
		
		metadataInserter.setAbstractField("Ocean Surface Zonal and Meridional currents between 1992 and 2015 from OSCAR - Ocean Surface Current Analyses Real-time (NASA) - https://podaac.jpl.nasa.gov/dataset/OSCAR_L4_OC_third-deg");
		
		metadataInserter.setCustomTopics("Ocean Surface Currents","OSCAR","NASA","LAS", "Ocean Surface Zonal Currents", "Ocean Surface Meridional Currents");
		
		metadataInserter.setAuthor("D4Science");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		
		Date datestart = formatter.parse("1992");
		Date dateend = formatter.parse("2015");
		
		metadataInserter.setStartDate(datestart);
		metadataInserter.setEndDate(dateend);
		
		String [] urls = {"http://thredds.d4science.org/thredds/fileServer/public/netcdf/oscar_vel/oscar_vel1999_2015.nc"};
		String [] protocols = {"HTTP"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	
	
}
