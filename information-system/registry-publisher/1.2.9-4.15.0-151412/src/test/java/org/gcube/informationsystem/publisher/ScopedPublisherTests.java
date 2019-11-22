package org.gcube.informationsystem.publisher;

import static org.gcube.common.resources.gcore.Resources.print;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopedPublisherTests {

	static GenericResource generic;
	static ScopedPublisher sp;
	static List<String> scopes;
	private static final Logger log = LoggerFactory.getLogger(RegistryPublisherTests.class);
	static Resource r;
	
	@BeforeClass
	public static void init(){
//		ScopeProvider.instance.set("/gcube/devsec");
		generic = Resources.unmarshal(GenericResource.class, PublisherTest.class.getClassLoader().getResourceAsStream("generic.xml"));
		sp=RegistryPublisherFactory.scopedPublisher();
		scopes=new ArrayList<String>();
		scopes.add("/gcube/devNext");
		scopes.add("/gcube/devsec/devVRE");
	}
	
	
	@Test
	public void printTest(){
		//print it
		print(generic);
		//resource-specific tests
		assertEquals(Type.GENERIC,generic.type());

	}
	
	@Test
	public void scopedPublisherCreate(){
		try {
			r=sp.create(generic, scopes);
		} catch (RegistryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("new resource created: ");
		String currentScope=ScopeProvider.instance.get();
		ScopeGroup<String> scopes=r.scopes();
		for(Iterator<String> it=scopes.iterator(); it.hasNext();){
			String scope=it.next();
			if(currentScope==null){
				currentScope=scope;
			}
			log.info("set scope "+scope);
			ScopeProvider.instance.set(scope);
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq 'PublisherBotoxTest' and $resource/Profile/Name eq 'TestNewPublisher' ");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> resources = client.submit(query);
			if(resources.size() > 0){
				System.out.println("resource found in scope "+scope+" resource found "+resources.size());
				GenericResource resource=resources.get(0);
				log.info("r founded "+resource.id()+" "+resource.toString());
				log.info("original resource: "+r.id()+" "+r.toString());
				assertEquals(resource, r);
				log.info("assert ok in scope "+scope);
			}
		}
		ScopeProvider.instance.set(currentScope);
	}
	
	
	@Test
	public void removeResource(){
		log.info("remove resource "+r.id()+" from all scopes ");
		AdvancedScopedPublisher advancedPublisher=new AdvancedScopedPublisher(sp);
		advancedPublisher.forceRemove(r);
		
	}


}
