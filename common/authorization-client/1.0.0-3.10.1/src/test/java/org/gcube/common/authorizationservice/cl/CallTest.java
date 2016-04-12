package org.gcube.common.authorizationservice.cl;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.BannedService;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;
public class CallTest {

	
	@Test
	public void call(){
		ScopeProvider.instance.set("/gcube/devsec");
		try{
			System.out.println(authorizationService().build().get("a00affeb-0b75-4152-a134-e5c432a9a70a"));
		}catch(ObjectNotFound onf){
			onf.printStackTrace();
		}
	}
	
	@Test
	public void requestToken(){
		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		String token = authorizationService().build().generate("fabio.sinibaldi", Arrays.asList("User"));
		System.out.println("token is: "+token);
		
	}
		
	@Test
	public void denyService(){
		
		ScopeProvider.instance.set("/gcube/devsec");
		authorizationService().build().deny("giancarlo.panichi", "WPS", "DataMiner");
			
	}
	
	@Test
	public void allowService(){
		
		ScopeProvider.instance.set("/gcube/devsec");
		authorizationService().build().allow("lucio.lelii", "Test",  "AuthorizationTest");
			
	}
	
	@Test
	public void getBannedServices(){
		
		ScopeProvider.instance.set("/gcube/devsec");
		List<BannedService> bannedServices = authorizationService().build().getBannedServices("lucio.lelii");
		for (BannedService banService : bannedServices)
			System.out.println(banService);
			
	}
	
}
