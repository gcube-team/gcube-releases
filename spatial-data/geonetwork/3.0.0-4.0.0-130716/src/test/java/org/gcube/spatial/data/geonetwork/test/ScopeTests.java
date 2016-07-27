package org.gcube.spatial.data.geonetwork.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.junit.Test;

public class ScopeTests {

	String[] scopes=new String[]{
//			"/gcube",
//			"/gcube/devsec","/gcube/devsec/devVRE"
			
			"/d4science.research-infrastructures.eu"
			
			
	};
	
	
	@Test
	public void testUtils(){
				
		for(String scope:scopes){		
			ScopeProvider.instance.set(scope);
			System.out.println("Setted scope "+scope);
			System.out.println("Scope name : "+ScopeUtils.getCurrentScopeName());
			System.out.println("Parents"+ScopeUtils.getParentScopes());
		}
	}
	
	@Test
	public void testConfigs() throws MissingConfigurationException, MissingServiceEndpointException, Exception{
		for(String scope:scopes){
			System.out.println("SCOPE : "+scope);
			ScopeProvider.instance.set(scope);
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
		ScopeProvider.instance.set(scopes[0]);
		GeoNetworkAdministration admin=GeoNetwork.get();
		admin.login(LoginLevel.ADMIN);
		System.out.println(admin.getGroups());
		System.out.println(admin.getUsers());
	}
}
