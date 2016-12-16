package org.gcube.data.analysis.tabulardata;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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

}