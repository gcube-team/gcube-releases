package org.gcube.application.framework.oaipmh.objectmappers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class RecordTemplateDC {
	
	private Properties props; //should contain <gcube_field, dc_field> mappings
	
	private HashMap<String, MetadataElement> nameType; //variables names and types

	
	/**
	 * @param mappingsPropsFileURL the url of the .properties file to parse for the field mappings
	 * @throws IOException 
	 */
	public RecordTemplateDC(String mappingsPropsFileURL) throws IOException{
		nameType = new HashMap<String, MetadataElement>();
		props = new Properties();
		FileInputStream fip = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource(mappingsPropsFileURL).getPath());
		props.load(fip);
	}

	
	public void addNameType(String name, String minOccurs, String maxOccurs, String type){
		if(props.getProperty(name)!=null)
			nameType.put(props.getProperty(name), new MetadataElement(props.getProperty(name), minOccurs, maxOccurs, type));
	}
	
	public HashMap<String, MetadataElement> getNameTypes() {
		return nameType;
	}
	
	/**
	 * <gcube_field, dc_field> mappings
	 * @return
	 */
	public Properties getProps() {
		return props;
	}
	
}
