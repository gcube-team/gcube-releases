package org.gcube.spatial.data.geonetwork.test;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.junit.Test;

import it.geosolutions.geonetwork.util.GNSearchRequest;

public class ScopeTests {

	String[] scopes=new String[]{
//			"/gcube",
//			"/gcube/devsec",
//			"/gcube/devsec/devVRE",
//			"/gcube/devNext/NextNext"
			
			"/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling",
			"/d4science.research-infrastructures.eu/gCubeApps"
			
	};
	
	
	@Test
	public void testUtils(){
				
		for(String scope:scopes){		
			TokenSetter.set(scope);
			System.out.println("Setted scope "+scope);
			System.out.println("Scope name : "+ScopeUtils.getCurrentScopeName());
			System.out.println("Parents"+ScopeUtils.getParentScopes());
		}
	}
	
	@Test
	public void testConfigs() throws MissingConfigurationException, MissingServiceEndpointException, Exception{
		for(String scope:scopes){
			System.out.println("SCOPE : "+scope);
			TokenSetter.set(scope);
			Configuration config=GeoNetwork.get().getConfiguration();
			System.out.println(config.getScopeConfiguration());
			Account account=config.getScopeConfiguration().getAccounts().get(Type.CKAN);
			System.out.println("CKAN : "+account.getUser()+" "+account.getPassword());;

		}
		
		Configuration config=GeoNetwork.get().getConfiguration();
		System.out.println(config.getAdminAccount().getUser()+" "+config.getAdminAccount().getPassword());
	}
	
	@Test
	public void testGNUsersAndGroups() throws Exception{
		TokenSetter.set(scopes[0]);
		GeoNetworkAdministration admin=GeoNetwork.get();
		admin.login(LoginLevel.ADMIN);
		System.out.println(admin.getGroups());
		System.out.println(admin.getUsers());
	}
	
	@Test
	public void getCount() throws Exception{
		for(String scope:scopes){
			TokenSetter.set(scope);
			GeoNetworkReader reader=GeoNetwork.get();
			final GNSearchRequest req=new GNSearchRequest();
			req.addParam(GNSearchRequest.Param.any,"");
			int publicCount=reader.query(req).getCount();
			
			reader.login(LoginLevel.CKAN);
			int totalCount=reader.query(req).getCount();
			
			reader.login(LoginLevel.ADMIN);
			int existingCount=reader.query(req).getCount();
			System.out.println("SCOPE "+scope+" found "+totalCount+" (public access : "+publicCount+", local +"+(totalCount-publicCount)+", existing in instance : "+existingCount+")");
		}
	}
}
