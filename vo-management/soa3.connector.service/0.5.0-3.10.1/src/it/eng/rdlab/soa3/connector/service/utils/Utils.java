package it.eng.rdlab.soa3.connector.service.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class Utils 
{
	
	/**
	  * Chooses JAXB as the annotation for the serialization/deserialization
	  *  
	  * @return mapper ObjectMapper
	  * 
	  */
	public static ObjectMapper getMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
		return mapper;
	}

}
