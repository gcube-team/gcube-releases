package org.gcube.application.perform.service.rest;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceCommons {

	private static final Logger log= LoggerFactory.getLogger(InterfaceCommons.class);
	
	public static final void checkMandatory(Object toCheck, String name) throws WebApplicationException{
		if(toCheck==null)
			throw new WebApplicationException(String.format("Parameter %1$s is mandatory",name),Response.Status.BAD_REQUEST);
	}
	
	public static final List<String> getParameter(MultivaluedMap<String,String> map,String paramName, boolean mandatory){
//		log.debug()
		if(map.containsKey(paramName)) {
			return map.get(paramName);
		}else if(mandatory) throw new WebApplicationException(String.format("Parameter %1$s is mandatory",paramName),Response.Status.BAD_REQUEST);
		return Collections.emptyList();
	}
}
