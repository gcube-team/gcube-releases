package org.gcube.data.integration;

import static junit.framework.Assert.*;
import static org.gcube.data.TestUtils.*;

import javax.inject.Named;
import javax.xml.namespace.QName;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tm.services.TBinderService;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tr.Constants;
import org.gcube.data.tr.Store;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.tr.requests.Mode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)
public class BinderTest {

	@Deployment
	static Gar gar = serviceGar();
	
	@Named(org.gcube.data.tm.Constants.TBINDER_NAME)
	static TBinderService service;
	
	static TBinder binder;
	
	@BeforeClass
	public static void setup() {
	
		ScopeProvider.instance.set(test_scope);
		
		binder = TServiceFactory.binder().at(binder_url()).build();
	}
	
	
	
	@Test
	public void createSource() throws Exception {
		

		BindSource request = new BindSource("test");
		request.addTypes(new QName("type1"),new QName("type2"));
		
		BindRequest params = new BindRequest(Constants.TR_NAME,request.toElement());
		Binding binding = binder.bind(params).get(0);
		
		assertNotNull(binding);
		assertNotNull(binding.source());
		assertNotNull(binding.readerRef());
		assertNotNull(binding.writerRef());
		
		
		Store store = store(binding.source());
		assertTrue(store.location().exists());
		
	}
	
	@Test
	public void reconfigureSource() throws Exception {
		
		BindRequest params = new BindRequest(Constants.TR_NAME,new BindSource("test").toElement());
		Binding binding = binder.bind(params).get(0);
		
		ScopeProvider.instance.set("/gcube/devsec/vre1");
		
		params = new BindRequest(Constants.TR_NAME,new BindSource(binding.source(),Mode.READABLE).toElement());
		binding = binder.bind(params).get(0);
		
		assertNotNull(binding.writerRef());
	}

}
