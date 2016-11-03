package org.gcube.datatransformation.adaptors.common;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.gcube.datatransformation.adaptors.common.xmlobjects.TreeResource;

public class CustomMarshaller {

	
	
	public static String marshalTreeResource(TreeResource tr) throws JAXBException{
		JAXBContext jaxbContext = JAXBContext.newInstance(TreeResource.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		StringWriter outputxml = new StringWriter();
		jaxbMarshaller.marshal(tr, outputxml);
		outputxml.flush();
		return outputxml.toString();
	}
	
	
	
}
