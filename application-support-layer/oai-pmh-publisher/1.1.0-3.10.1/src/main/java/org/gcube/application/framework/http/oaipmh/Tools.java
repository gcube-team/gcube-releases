package org.gcube.application.framework.http.oaipmh;

import java.util.HashMap;

public class Tools {

	
	public static String getIDforName(HashMap<String,String> idName, String name){
		for(String id : idName.keySet())
			if(idName.get(id).equals(name))
				return id;
		return null;
	}
	
	
}
