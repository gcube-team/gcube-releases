package org.gcube.data.oai.tmplugin;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.junit.Before;
import org.junit.Test;


public class JAXBTest {

	private JAXBContext jaxb;
	private Class<?>[] paramTypes = new Class<?>[]{WrapRepositoryRequest.class,WrapSetsRequest.class};


	@Before
	public void initJaxb() throws Exception{
		jaxb = JAXBContext.newInstance(paramTypes);
	}

	@Test
	public void jaxbMarshaller() throws Exception{
		Request request = new WrapSetsRequest("http://aquacomm.fcla.edu/cgi/oai2");
		request.setMetadataFormat("oai_dc");
		request.addSets("7375626A656374733D48");
		request.setName("aquacomm");
		request.setMetadataFormat("oai_dc");
		request.setDescription("oai test collection");
		request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		request.setTitleXPath("//*[local-name()='title']");
		request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	

		StringWriter sw = new StringWriter();
		jaxb.createMarshaller().marshal(request, sw);
		System.out.println(sw.toString());
	}

	@Test
	public void jaxbUnmarshaller() throws Exception{
		Request req =  (Request) jaxb.createUnmarshaller().unmarshal(JAXBTest.class.getResourceAsStream("/request.xml"));
		if (req instanceof WrapSetsRequest) System.out.println("perfect");
	}

}
