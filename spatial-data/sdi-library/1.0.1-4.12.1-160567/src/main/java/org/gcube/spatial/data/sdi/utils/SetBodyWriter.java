package org.gcube.spatial.data.sdi.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Produces("multipart/mixed")
public class SetBodyWriter implements MessageBodyWriter<HashSet>{

	public SetBodyWriter() {
		System.out.println("MESSAGE BODY WRITER INIT");
	}
	
	@Override
	public void writeTo(HashSet arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4,
			MultivaluedMap<String, Object> arg5, OutputStream arg6) throws IOException, WebApplicationException {
		 try {
	            JAXBContext jaxbContext = JAXBContext.newInstance(Set.class);
	 
	            // serialize the entity myBean to the entity output stream
	            jaxbContext.createMarshaller().marshal(arg0, arg6);
	        } catch (JAXBException jaxbException) {
	            throw new ProcessingException(
	                "Error serializing a MyBean to the output stream", jaxbException);
	        }		
		
	}
	@Override
	public long getSize(HashSet arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
		return -1;
	}
	public boolean isWriteable(java.lang.Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
		return true;		
	};
}
