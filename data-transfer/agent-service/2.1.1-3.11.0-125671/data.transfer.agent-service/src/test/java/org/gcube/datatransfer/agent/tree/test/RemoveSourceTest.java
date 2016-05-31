package org.gcube.datatransfer.agent.tree.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class RemoveSourceTest {
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	@Test
	public void process() {
		String id="test_22_5_2013";
		ScopeProvider.instance.set("/gcube/devsec");
//		ScopedPublisher sp=RegistryPublisherFactory.scopedPublisher();
//		List<String> scopes = new ArrayList<String>();
//		scopes.add("/gcube/devsec");
//		try {
//			sp.remove(id,Type.GENERIC,scopes);
//		} catch (RegistryNotFoundException e) {
//			e.printStackTrace();
//		}
		
	//	RegistryPublisher rp=RegistryPublisherFactory.create();
	//	rp.remove(id,Type.GENERIC);
	}
}
