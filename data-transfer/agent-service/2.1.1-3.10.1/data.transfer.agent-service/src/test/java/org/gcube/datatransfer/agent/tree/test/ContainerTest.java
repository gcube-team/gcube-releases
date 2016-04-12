package org.gcube.datatransfer.agent.tree.test;

import java.io.File;

import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)
public class ContainerTest {
	
	
	//@Deployment
	static Gar myGar  = new Gar(new File("src/test/resources/tree-manager-service.gar"));
	
	//@Deployment
	static Gar dependencies = new Gar("dependencies").addLibraries("src/test/resources");
	 
	
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(TestUtils.VO.toString());
	}
	
	@Test
	public void test() {
		
		System.out.println(ServiceContext.getContext().getStatus());
	}

}
