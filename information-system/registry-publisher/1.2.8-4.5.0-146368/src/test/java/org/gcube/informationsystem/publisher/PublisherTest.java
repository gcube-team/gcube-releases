package org.gcube.informationsystem.publisher;

import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublisherTest {

	static Logger log = LoggerFactory.getLogger(PublisherTest.class); 
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		ScopeProvider.instance.set("/gcube/devsec/devVre");
//		log.trace("starting client");
//		RegistryPublisher registryPublisher = RegistryPublisherFactory.create();
//		AdvancedPublisher publisher=new AdvancedPublisher(registryPublisher);
//		publisher.removeFromAllScopes("testError", Type.GENERIC);
	}

	
	public static <T> T unmarshal(Class<T> clazz, String sample) throws Exception {
		return Resources.unmarshal(clazz,PublisherTest.class.getClassLoader().getResourceAsStream(sample));
	}
	
}
