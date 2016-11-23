package org.gcube.informationsystem.publisher;


import java.net.URI;
import java.net.URISyntaxException;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.AdvancedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.junit.BeforeClass;
import org.junit.Test;


public class RemoveByIdTests {

	static GenericResource generic;
	static AdvancedPublisher arp;
	static RegistryPublisher rp;
	static Resource r;
	private static String scope="/gcube/devNext";
	private String id="c66bb9b0-e571-11e2-9f13-fcb948ca7a75";//aedc90ed-6df2-437c-ac0f-ffecbafda893
	private Type type=Type.ENDPOINT;
	private String  endpointString="http://node20.d.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/informationsystem/registry/ResourceRegistration";
	
	@BeforeClass
	public static void init(){
		ScopeProvider.instance.set(scope);
		rp=RegistryPublisherFactory.create();
		arp=new AdvancedPublisher(rp);
	}
	
	
	@Test
	public void removeFromCurrentScope(){
		arp.removeById(id, type);
	}
	
//	@Test
	public void removeFromCurrentScopeWithEndpoint(){
		URI endpoint=null;
		try {
			endpoint=new URI(endpointString);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		arp.removeById(id, type, endpoint);
	}

}
