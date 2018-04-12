package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data;

import java.util.HashMap;

//a single entry from a category
public class Entry {
	
	HashMap<String,String> attributes;
	HashMap<String,String> types;
	
	public HashMap<String,String> getAttributes(){
		return attributes;
	}
	
	public HashMap<String,String> getTypes(){
		return types;
	}
	
	public void addAttribute(String column,String value){
		if (value==null)
			value = "";
		
		attributes.put(column, value);
	}
	
	public void addType(String column,String value){
		if (value==null)
			value = "";
		
		types.put(column, value);
	}
	
	public Entry(){
		attributes = new HashMap<String, String>();
		types = new HashMap<String, String>();
	}
	
	public String toString(){
		
		StringBuffer returningString = new StringBuffer();
		returningString.append("{");
		for (String att: attributes.keySet()){
			String value = attributes.get(att);
			returningString.append(att+"="+value+"|"+types.get(att).toUpperCase()+"; ");
		}
		returningString.append("}");
		return returningString.toString();
	}
}
