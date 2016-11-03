package org.gcube.common.authorizationservice.cl;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;
import org.gcube.common.authorization.library.policies.Users;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.junit.Test;
public class CallTest {

	@Test
	public void resolveNodeToken() throws Exception{
		System.out.println(resolveToken("36501a0d-a205-4bf1-87ad-4c7185faa0d6")); //81caac0f-8a0d-4923-9312-7ff0eb3f2d5e|98187548"));
	}
	
	@Test
	public void requestNodeToken()  throws Exception {
		System.out.println(_requestNodeToken());
	}
	
	@Test
	public void addPolicy()  throws Exception {
		SecurityTokenProvider.instance.set(requestTestToken("/gcube/devNext/NextNext"));
		List<Policy> policies = new ArrayList<Policy>();
		policies.add(new User2ServicePolicy("/gcube/devNext/NextNext", new ServiceAccess(), Users.one("lucio.lelii"), Action.ACCESS ));
		authorizationService().addPolicies(policies);
	}
		
	
	@Test
	public void getPolicies()  throws Exception{
		SecurityTokenProvider.instance.set(requestTestToken("/gcube/devNext/NextNext"));
		List<Policy> policies = authorizationService().getPolicies("/gcube/devsec/devVRE");
		System.out.println(policies);	
	}
	
	@Test
	public void removePolicy()  throws Exception {
		authorizationService().removePolicies(2, 3, 4);
	}
	
	@Test(expected=RuntimeException.class)
	public void createKeyWithError()  throws Exception {
		authorizationService().generateApiKey("TEST");
	}
/*	
	@Test
	public void getSymmKey() throws Exception{
		SecurityTokenProvider.instance.set(_requestNodeToken());
		authorizationService().getSymmKey("/tmp");		
	}*/
	
	
	@Test
	public void createKey()  throws Exception {
		String token = requestTestToken("/gcube");
		SecurityTokenProvider.instance.set(token);
		String key = authorizationService().generateApiKey("PIPPO");
		System.out.println("key : "+key);
		System.out.println(resolveToken(key));
	}
	
	@Test
	public void retrieveApiKeys()  throws Exception {
		String token = requestTestToken("/gcube/devNext");
		SecurityTokenProvider.instance.set(token);
		Map<String, String> keys = authorizationService().retrieveApiKeys();
		System.out.println("keys : "+keys);
		
	}
	
	public String _requestNodeToken()  throws Exception {
		SecurityTokenProvider.instance.set(requestTestToken("/gcube/devNext/NextNext"));
		String token = authorizationService().requestActivation(new ContainerInfo("mynode",8080));
		return token;
	}
	
	@Test
	public void createTestToken()  throws Exception {
		System.out.println(requestTestToken("/gcube/devNext/NextNext"));
	}
	
	private String requestTestToken(String context) throws Exception{
		return authorizationService().generateUserToken(new UserInfo("test.token", new ArrayList<String>()), context);
	}
	
	private AuthorizationEntry resolveToken(String token) throws Exception{
		AuthorizationEntry entry = authorizationService().get(token);
		return entry;
	}
}
