package org.gcube.data.analysis;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Test;

public class TestIS {

	@Test
	public void getFromIS() throws Exception{
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		SecurityTokenProvider.instance.set("a5b623b6-6577-4271-aba6-7ada687d29cf-98187548");
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'ISO'").
		addCondition("$resource/Profile/Name/text() eq 'MetadataConstants'");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);

		//XStream xstream = new XStream();
		
		for(GenericResource resource : client.submit(query)){
			try{
				
				// Refactor logic to integrate 
				
//				// parse body as a XML serialization of a Computational Infrastructure
//				StringWriter writer = new StringWriter();
//				transformer.transform(new DOMSource(resource.profile().body()), new StreamResult(writer));
//				String theXML=writer.getBuffer().toString();
//				//				String theXML = writer.getBuffer().toString().replaceAll("\n|\r", "");
				//EnvironmentConfiguration config=(EnvironmentConfiguration) xstream.fromXML(resource.profile().bodyAsString());
				System.out.println(resource.profile().bodyAsString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
}
