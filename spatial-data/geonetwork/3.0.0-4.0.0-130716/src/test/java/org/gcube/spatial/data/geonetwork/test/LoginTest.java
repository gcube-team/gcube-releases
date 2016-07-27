package org.gcube.spatial.data.geonetwork.test;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.opengis.metadata.Metadata;

public class LoginTest {

	
	private static final String defaultScope="/gcube/devsec/devVRE";
//	private static final String defaultScope="/gcube";
//	private static final String defaultScope="/d4science.research-infrastructures.eu/gCubeApps";
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set(defaultScope);
//		System.out.println("Checking scope : "+defaultScope);

//		GeoNetworkReader reader=GeoNetwork.get();
//		reader.login(LoginLevel.CKAN);
//		System.out.println(queryAll(reader));
//		
		
		
//		 Filter second search
		ScopeProvider.instance.set("/gcube");
		GeoNetworkReader reader=GeoNetwork.get();
		reader.login(LoginLevel.DEFAULT);
		List<Long> firstLevelIds=getIds(queryAll(reader));
			
		System.out.println("First Level count : "+firstLevelIds.size());
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		reader=GeoNetwork.get();
		reader.login(LoginLevel.CKAN);
		List<Long> secondLevelIds=getIds(queryAll(reader));
		System.out.println("Second Level count "+secondLevelIds.size());
		secondLevelIds.removeAll(firstLevelIds);
		System.out.println("Second level filtered count : "+secondLevelIds.size());
		System.out.println("IDS : ");
		System.out.println(secondLevelIds);
		
		
//		String metaFile="meta.xml";
//		long id=publishMeta(LoginLevel.PRIVATE, metaFile);
////		long id=142301;
//		System.out.println("Published "+metaFile+" with id "+id);
//		for(LoginLevel lvl: LoginLevel.values()){
//			System.out.println("Accessible as "+lvl+" "+isReadable(id, lvl));
//		}
		
		
//		checkLevelsCount();
		
//		getMetaById("5a68c6a4-916b-4789-8442-ee3a4aac14d5", LoginLevel.DEFAULT);
		
	}
	
	
	private static GNSearchResponse queryAll(GeoNetworkReader reader) throws GNLibException, GNServerException, MissingServiceEndpointException, MissingConfigurationException{
		System.out.println("Scope configuration : "+reader.getConfiguration().getScopeConfiguration());
		final GNSearchRequest req=new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any,"");
		return reader.query(req);
	}
	
	private static List<Long> getIds(GNSearchResponse resp){
		List<Long> toReturn=new ArrayList<>();
		Iterator<GNMetadata> iterator=resp.iterator();
		while(iterator.hasNext()){
			toReturn.add(iterator.next().getId());
		}
		return toReturn;
	}
	
	public static void checkLevelsCount() throws Exception{
//		GeoNetworkReader reader=GeoNetwork.get(new MyConfiguration());
		GeoNetworkReader reader=GeoNetwork.get();
		System.out.println(reader.getConfiguration());
		GNSearchRequest request=new GNSearchRequest();
		request.addParam(GNSearchRequest.Param.any, "");
		for(LoginLevel lvl: LoginLevel.values()){
			try{
				System.out.print("LOGIN LEVEL : "+lvl+"\t");
				reader.login(lvl);
				System.out.println(" Number of elements found : "+reader.query(request).getCount());
			}catch (Exception e) {
				e.printStackTrace(System.err);
			}			
		}
	}
	
	
	public static long publishMeta(LoginLevel level, String metaPath) throws Exception{
		GeoNetworkPublisher publisher=GeoNetwork.get();
		ScopeConfiguration scopeConfig=publisher.getConfiguration().getScopeConfiguration();
		publisher.login(level);
		GNInsertConfiguration config=publisher.getCurrentUserConfiguration("datasets", "_none_");
		long id=publisher.insertMetadata(config,new File(metaPath));
		
//		//****Visibility means
//		GNPrivConfiguration privConfig=new GNPrivConfiguration();			
//		if(level.equals(LoginLevel.SCOPE)){
//			privConfig.addPrivileges(publisher.getConfiguration().getScopeGroup(),EnumSet.of(GNPriv.VIEW));
//		}
//		publisher.setPrivileges(id, privConfig);			
		return id;
	}

	
	public static boolean isReadable(long id,LoginLevel level) throws Exception{
		GeoNetworkReader reader=GeoNetwork.get();
		reader.login(level);
		try{
			reader.getById(id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	
	public static Metadata getMetaById(String id, LoginLevel level) throws Exception{
		GeoNetworkReader reader=GeoNetwork.get();
		reader.login(level);
		return reader.getById(id);
	}
}
