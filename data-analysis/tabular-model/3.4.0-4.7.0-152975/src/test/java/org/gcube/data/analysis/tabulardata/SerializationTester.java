package org.gcube.data.analysis.tabulardata;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.data.analysis.tabulardata.model.resources.SDMXResource;
import org.gcube.data.analysis.tabulardata.model.resources.SDMXResource.TYPE;
import org.junit.Assert;

public class SerializationTester {

	public SerializationTester() {
		super();
	}

	public static void roundTripTest(Object object) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(object, stringWriter);
		String result = stringWriter.toString();
		System.err.println(result);
	
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Object unmarshalledObj = (Object) unmarshaller.unmarshal(new StringReader(result));
		Assert.assertEquals(object, unmarshalledObj);		
	}

	public static void main(String[] args) throws MalformedURLException, JAXBException {
		roundTripTest(new SDMXResource(new URL("http://www.google.it"), "prova", "1.0", "agency", "primarymeasure", TYPE.CODE_LIST));
	}
	
}