package org.gcube.data.integration;

import static org.gcube.data.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.tml.proxies.TServiceFactory.*;
import static org.gcube.data.trees.data.Nodes.*;

import java.util.Iterator;

import javax.inject.Named;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.tm.services.TBinderService;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.tr.Constants;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.TemplateFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)
public class WriteTests {

	@Deployment
	static Gar gar = serviceGar();
	
	@Named(org.gcube.data.tml.Constants.binderWSDDName)
	static TBinderService service;
	
	static TBinder binder;
	
	static TWriter proxy;
	
	@BeforeClass
	public static void setup() throws Exception {
	
		ScopeProvider.instance.set(test_scope);
		
		binder = TServiceFactory.binder().at(binder_url()).build();
		
		BindSource request = new BindSource("test");
		
		BindRequest params = new BindRequest(Constants.TR_NAME,request.toElement());
		Binding binding = binder.bind(params).get(0);
		
		//sets clients shared by tests
		proxy = writer().at(binding.writerRef()).build();
		
	}
	
	@Test
	public void addMany() throws Exception {
	
		int size = 100;
		
		Tree prototype = t(e("a","this is"),e("b","a test"));
		
		Iterator<Tree> trees = TemplateFactory.aTreeLike(prototype).generate(size);
		
		Stream<Tree> addedTrees = proxy.add(convert(trees));
		
		while (addedTrees.hasNext()) {
			try {
				Tree t = addedTrees.next();
				System.out.println("added:"+t.id()+":"+t.sourceId());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
