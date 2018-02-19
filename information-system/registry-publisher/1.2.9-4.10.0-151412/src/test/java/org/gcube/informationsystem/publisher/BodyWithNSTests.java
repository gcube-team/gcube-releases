package org.gcube.informationsystem.publisher;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;

public class BodyWithNSTests {

//	@Test
	public void buildMassiAndvalentinaGeneric() throws Exception {

		GenericResource generic = new GenericResource();
		generic.newProfile().name("test Nov 6 Rob").type("test").description("this is a test " + new Date());

		String text = "<ns3:Record xmlns:ns3=\"http://gcube-system.org/namespaces/common\" "
				+ "xmlns:ns2=\"http://gcube-system.org/namespaces/data/tm\"><ciao>nn va</ciao></ns3:Record>";

		generic.profile().newBody(text);

		StringWriter writer = new StringWriter();
		Resources.marshal(generic,writer);
		
		GenericResource parsed = Resources.unmarshal(GenericResource.class,new StringReader(writer.toString()));
		
		Resources.print(parsed);
		
		Resources.validate(parsed);
		ScopeProvider.instance.set("/gcube/devsec");
		RegistryPublisher rp=RegistryPublisherFactory.create();
		rp.create(generic);
		
	}

}
