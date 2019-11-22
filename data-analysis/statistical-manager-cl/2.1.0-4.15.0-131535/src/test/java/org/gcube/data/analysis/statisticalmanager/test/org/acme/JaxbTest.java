package org.gcube.data.analysis.statisticalmanager.test.org.acme;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.junit.Assert;
import org.junit.Test;

public class JaxbTest {

	
	
	@Test
	public void one() throws Exception {

		SMAbstractResource ab =new SMAbstractResource();
		
		 ab.resource(new SMObject());
		
		JAXBContext ctx = JAXBContext.newInstance(SMAbstractResource.class);
		
		Writer writer = new StringWriter();
		
		ctx.createMarshaller().marshal(ab, writer);
		
		System.out.println(writer.toString());
		
		SMAbstractResource resource = (SMAbstractResource) ctx.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
		
		Assert.assertNotNull(resource.resource());
	}
	
}

