package org.gcube.application.framework.oaipmh.objectmappers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * By defining an instance of the RecordTemplate, the Repository "knows" what it's output will be.  
 * It also creates all xsd for the output by considering the structure of the instance of this.
 * @author nikolas
 *
 */
public class RecordTemplateCustom {
	
	private String recordName; //used for the XSD file
	private HashMap<String, MetadataElement> nameType; //variables names and types
	
	private HashMap<String, HashMap<String, MetadataElement>> subNameTypes; // groups of name-types 
	
	/**
	 * 
	 * @param name the name of the record template
	 */
	public RecordTemplateCustom(String name){
		this.recordName = name;
		nameType = new HashMap<String, MetadataElement>();
		subNameTypes = new HashMap<String, HashMap<String, MetadataElement>>();
	}
	
	public String getRecordName(){
		return recordName;
	}
	
	public void addNameType(String name, String minOccurs, String maxOccurs, String type){
		nameType.put(name, new MetadataElement(name, minOccurs, maxOccurs, type));
	}
	
	public HashMap<String, MetadataElement> getBaseNameTypes(){
		return nameType;
	}
	
//	public Collection <MetadataElement> getBaseNameTypesValues(){
//		return nameType.values();
//	}
	
//	public MetadataElement getBaseNameType(String name){
//		return nameType.get(name);
//	}
	
//	public String getTypeOf(String name){
//		return (String)nameType.get(name).getType();
//	}
	
	public void addSubGroupNameTypes(String subGroupName, HashMap<String, MetadataElement> nameTypes){
		subNameTypes.put(subGroupName, nameTypes);
	}
	
	public HashMap<String, MetadataElement> getSubGroupNameTypes(String subGroupName){
		return subNameTypes.get(subGroupName);
	}
	
	public Set<String> getAllSubGroupNames(){
		return subNameTypes.keySet();
	}
	
	

	
}
