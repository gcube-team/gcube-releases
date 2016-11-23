package org.gcube.opensearch.opensearchdatasource.processor;

import java.util.HashMap;
import java.util.Map;

public class OpenSearchGcqlCollectionQuery {
	public Map<String, String> parameters = new HashMap<String, String>();
	
	public boolean hasParameter(String parameter)
	{
		return parameters.containsKey(parameter);
	}
	
	public void addParameter(String parameter, String value) throws Exception {
		if (parameter == null)
			return;
		
		if (parameter.equalsIgnoreCase("gDocCollectionID")) {
			if(parameters.containsKey(parameter) && parameters.get(parameter).equals(value) == false) {
				throw new Exception("Duplicate query parameter. Trying to change value of param " + parameter + " from : " + parameters.get(parameter) + " to " + value);
			}
		}
		
		if (parameter.equalsIgnoreCase("gDocCollectionLang")) {
			if(parameters.containsKey(parameter) && parameters.get(parameter).equals(value) == false) {
				throw new Exception("Duplicate query parameter. Trying to change value of param " + parameter + " from : " + parameters.get(parameter) + " to " + value);
			}
		}

		if(parameters.containsKey(parameter)) {
			parameters.put(parameter, parameters.get(parameter) + " " + value);
		} else {
			parameters.put(parameter, value);
		}
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer();
		for(Map.Entry<String, String> param: parameters.entrySet()) {
			str.append(param.getKey() + "=" + "\"" + param.getValue() + "\"");
			str.append(" ");
		}
		return str.toString().trim();
	}
}
