package org.gcube.portlets.admin.software_upload_wizard.server.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class SerializationUtil {

	public static String serialize(Object object) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(object, stringWriter);
		return stringWriter.toString();
	}

}
