package org.gcube.informationsystem.publisher;

import static org.gcube.common.resources.gcore.Resources.print;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryRemoveTests {

	static GenericResource generic;
	static RegistryPublisher rp;
	static Resource r;
	private static final Logger log = LoggerFactory.getLogger(RegistryUpdateTests.class);
	
	@BeforeClass
	public static void init(){
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		generic = Resources.unmarshal(GenericResource.class, PublisherTest.class.getClassLoader().getResourceAsStream("generic.xml"));
		rp=RegistryPublisherFactory.create();
	}
	
	@Test
	public void printTest(){
	//print it
		print(generic);
	//resource-specific tests
		assertEquals(Type.GENERIC,generic.type());
		r=rp.create(generic);
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void removeFromCurrentScope(){
		r=rp.remove(generic);
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String currentScope=ScopeProvider.instance.get();
		ScopeGroup<String> scopes=r.scopes();
		for(Iterator<String> it=scopes.iterator(); it.hasNext();){
			String scope=it.next();
			log.info("set scope "+scope);
			ScopeProvider.instance.set(scope);
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq 'PublisherBotoxTest' and $resource/Profile/Name eq 'TestNewPublisher5' ");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> resources = client.submit(query);
			if(resources.size() > 0){
				System.out.println("resource found in scope "+scope+ " size "+resources.size());
				GenericResource resource=resources.get(0);
				log.info("r founded "+resource.id()+" "+resource.toString());
				log.info("original resource: "+r.id()+" "+r.toString());
				assertEquals(resource.scopes(), r.scopes());
				log.info("assert ok in scope "+scope);
			}
		}
		ScopeProvider.instance.set(currentScope);
	}
	
	@AfterClass
	public static void removeResource(){
		String currentScope=ScopeProvider.instance.get();
		log.info("force remove resource "+r.id()+" to scope "+currentScope);
		AdvancedPublisher advancedPublisher=new AdvancedPublisher(rp);
		advancedPublisher.forceRemove(r);
		ScopeBean scopeBean=new ScopeBean(currentScope).enclosingScope();
		if(scopeBean!=null){
			String enclosedScope=scopeBean.toString();
			while( enclosedScope != null){
				ScopeProvider.instance.set(enclosedScope);
				advancedPublisher.forceRemove(r);
				scopeBean=new ScopeBean(enclosedScope).enclosingScope();
				if(scopeBean!=null){
					enclosedScope=scopeBean.toString();
				}else enclosedScope=null;
			}
		}
		ScopeProvider.instance.set(currentScope);

		
	}
}
