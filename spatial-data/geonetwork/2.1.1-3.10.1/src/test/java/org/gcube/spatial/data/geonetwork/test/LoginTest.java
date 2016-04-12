package org.gcube.spatial.data.geonetwork.test;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;

import java.io.File;
import java.util.EnumSet;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.ConfigurationManager;
import org.opengis.metadata.Metadata;

public class LoginTest {

	
//	private static final String defaultScope="/gcube/devsec";
	private static final String defaultScope="/d4science.research-infrastructures.eu/gCubeApps";
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set(defaultScope);
		System.out.println("Checking scope : "+defaultScope);
		GeoNetworkReader reader=GeoNetwork.get();
				reader.login(LoginLevel.DEFAULT);
		System.out.println(reader.getById(94669));
		
//		ConfigurationManager.setConfiguration(MyConfiguration.class);

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
		publisher.login(level);
		GNInsertConfiguration config=new GNInsertConfiguration(publisher.getConfiguration().getScopeGroup()+"", "datasets", "_none_", true);
		long id=publisher.insertMetadata(config,new File(metaPath));
		
		//****Visibility means
		GNPrivConfiguration privConfig=new GNPrivConfiguration();			
		if(level.equals(LoginLevel.SCOPE)){
			privConfig.addPrivileges(publisher.getConfiguration().getScopeGroup(),EnumSet.of(GNPriv.VIEW));
		}
		publisher.setPrivileges(id, privConfig);			
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
