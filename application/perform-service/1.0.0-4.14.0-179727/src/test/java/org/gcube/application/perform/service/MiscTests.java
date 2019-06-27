package org.gcube.application.perform.service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;

import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;

public class MiscTests {

	public static void main(String[] args) {
		HashMap<DBField,Object> condition=new HashMap<DBField,Object>();
		
		condition.put(DBField.Farm.fields.get(DBField.Farm.FARM_ID), "suca");
		condition.put(DBField.Farm.fields.get(DBField.Farm.UUID), "boh");
		System.out.println(new DBQueryDescriptor(condition));
		
		
		
	
//		theMap.put("farmid", "128");
//		ImmutableMultivaluedMap<String, String> map=new ImmutableMultivaluedMap<>(theMap);
//		
		
	}

}
