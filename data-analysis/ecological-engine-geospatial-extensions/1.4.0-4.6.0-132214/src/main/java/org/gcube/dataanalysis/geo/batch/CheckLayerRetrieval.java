package org.gcube.dataanalysis.geo.batch;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.configuration.DefaultConfiguration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.TopicCategory;

public class CheckLayerRetrieval {
	
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	//static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";

	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String geonetworkUser = "admin";
	//static String geonetworkPwd = "Geey6ohz";
	static String geonetworkPwd = "kee9GeeK";
 	
	public static void main(String[] args) throws Exception{
		String scope = "/d4science.research-infrastructures.eu/gCubeApps";
		String title ="oscar";
		ScopeProvider.instance.set(scope);
		
		GeoNetworkAdministration reader=GeoNetwork.get();
		reader.login(LoginLevel.SCOPE);
		
		//Configure search request
		GNSearchRequest req=new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any,title);
		req.addConfig(GNSearchRequest.Config.similarity, "1");
		GNSearchResponse resp=reader.query(req);
		System.out.println("Found N layers: "+resp.getCount());
		Long id = 0L;
		Metadata meta = null;
		DefaultConfiguration geonetworkCfg = (DefaultConfiguration) reader.getConfiguration();
		Integer scopePublicGroup = geonetworkCfg.getScopeConfiguration().getPublicGroup();
		System.out.println("scopePublicGroup "+scopePublicGroup  );
		for(GNSearchResponse.GNMetadata metadata:resp){
			id = metadata.getId();
			
			//
			
			System.out.println("ID "+id  );
			System.out.println("Name "+metadata.getInfo().getName());
			meta = reader.getById(id);
			Identification idf = meta.getIdentificationInfo().iterator().next();
			String otitle = idf.getCitation().getTitle().toString();
			
			((DefaultMetadata) meta).setFileIdentifier(UUID.randomUUID().toString());
			System.out.println("Title "+otitle);
		}
	}
	
	
	public static void main2(String[] args) throws Exception{
		//String startScope = "/d4science.research-infrastructures.eu/gCubeApps/ScalableDataMining";
		String targetScope = "/d4science.research-infrastructures.eu/gCubeApps";
		
//		String targetScope = "/gcube/devsec";
		//String title ="Ocean Surface Zonal Currents (u) in 1992 from OSCAR Third Degree Sea Surface Velocity [oscar_vel1992_180.nc]";
		String title ="oscar";
		checkLayerInScope(title, targetScope, targetScope);
	}
	
public static void checkLayerInScope(String title,String startScope,String targetScope) throws Exception{
		
		ScopeProvider.instance.set(startScope);
		
		GeoNetworkAdministration reader=GeoNetwork.get();
		reader.login(LoginLevel.SCOPE);
		DefaultConfiguration geonetworkCfg = (DefaultConfiguration)reader.getConfiguration();
		Integer scopePublicGroup = geonetworkCfg.getScopeConfiguration().getPublicGroup();
		Map<Account.Type,Account> accounts = geonetworkCfg.getScopeConfiguration().getAccounts();
		Account account = accounts.get(Account.Type.SCOPE);
		String geonetworkUser =  account.getUser();
		String geonetworkPassword =  account.getPassword();
		System.out.println("GeoNetwork user "+geonetworkUser);
		System.out.println("GeoNetwork password "+geonetworkPassword);
		System.out.println("GeoNetwork scope Public Group "+scopePublicGroup);
		
		//getScopeConfiguration().getPublicGroup();
		
		//Configure search request
		GNSearchRequest req=new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any,title);
		req.addConfig(GNSearchRequest.Config.similarity, "1");
		GNSearchResponse resp=reader.query(req);
		
		//Iterate through results and access found metadata 
		Long id = 0L;
		Metadata meta = null;
		System.out.println("Found N layers: "+resp.getCount());
		for(GNSearchResponse.GNMetadata metadata:resp){
			id = metadata.getId();
			System.out.println("ID "+id  );
			System.out.println("Name "+metadata.getInfo().getName());
			meta = reader.getById(id);
			Identification idf = meta.getIdentificationInfo().iterator().next();
			String otitle = idf.getCitation().getTitle().toString();
			System.out.println("Title "+otitle);
			if (!otitle.toLowerCase().contains(title.toLowerCase())){
				System.out.println("Invalid layer");
				continue;
			}
		
		
		//look for target configuration
		ScopeConfiguration targetConfiguration=null;
		targetScope = targetScope.substring(targetScope.lastIndexOf("/")+1);
		System.out.println("target scope "+targetScope);
		for(ScopeConfiguration configuration : reader.getConfiguration().getExistingConfigurations())
			if(configuration.getAssignedScope().equals(targetScope)) targetConfiguration= configuration;
 
		if(targetConfiguration==null)
			throw new MissingConfigurationException("Scope "+targetScope+" has no configuration");	
 
		int targetUserId=UserUtils.getByName(reader.getUsers(),targetConfiguration.getAccounts().get(Account.Type.SCOPE).getUser()).getId();
		int targetGroup=targetConfiguration.getDefaultGroup();
			
		System.out.println("INFO: ID "+id +" targetUserId "+ targetUserId + " targetGroup "+ targetGroup);
		System.out.println("Done with "+otitle);
		Thread.sleep(2000);
		//break;
		}
		
		System.out.println("All done");
	}

	
	
	
}
