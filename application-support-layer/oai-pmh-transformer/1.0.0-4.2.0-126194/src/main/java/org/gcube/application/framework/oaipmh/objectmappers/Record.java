package org.gcube.application.framework.oaipmh.objectmappers;

import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.application.framework.oaipmh.constants.MetadataConstants;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class forms a repository Record based on the RecordTemplate, which is created  
 * @author nikolas
 *
 */
public class Record {
	
	private Element record; 
	
	/**
	 * FOR CUSTOM FORMAT OUTPUT ! <br><br>
	 * The input template should be initiated ONLY ONCE, and used as input for every record created. <br><br>
	 * 
	 * It gets a hashmap of {variable_name, value} and creates a dom Element holding the record in the metadata format specified. <br>
	 * <b>values</b> should contain at least an "id" value. For nested values, should be named after the following convention -> basename:subname (e.g. "more_properties:age") if it's not on the template root, but 1 level deeper.
	 * <br>Just seperate levels with a "<b>:</b>"
	 * 
	 * @param values Must have at least a variable named "id" within the values!
	 * @param template
	 * @param set Should contain one of the sets defined in the Repository 
	 * @param metadataXSDobject 
	 * @return
	 */
	public Record(HashMap<String,String> values, RecordTemplateCustom template, Properties sets, CustomMetadataXSD customMetadataXSD) {
		Document doc = ElementGenerator.getDocument();
		Element record = doc.createElement("record");
		record.appendChild(createRecordHeaderCustom(values,sets));
		record.appendChild(createRecordMetadataByCustom(values, template, customMetadataXSD));
		this.record = record;
	}
	
	
	private Element createRecordHeaderCustom(HashMap<String,String> values, Properties sets ){
		Document doc = ElementGenerator.getDocument();
		Element header = doc.createElement("header");
		Element identifier = doc.createElement("identifier");
		identifier.appendChild(doc.createTextNode(values.get("id")));
		Element datestamp = doc.createElement("datestamp");
		if(values.get("datestamp")!=null)
			datestamp.appendChild(doc.createTextNode(values.get("datestamp")));
		else
			datestamp.appendChild(doc.createTextNode(Toolbox.dateTimeNow()));
		header.appendChild(identifier);
		header.appendChild(datestamp);
		for(Object set : sets.values()){
			Element setSpec = doc.createElement("setSpec");
			setSpec.appendChild(doc.createTextNode((String)set));
			header.appendChild(setSpec);
		}
		
		return header;
	}
	
	
	private Element createRecordMetadataByCustom(HashMap<String,String> values, RecordTemplateCustom template, CustomMetadataXSD metadataXSDobject){
		Document doc = ElementGenerator.getDocument();
		Element metadata = doc.createElement("metadata");
		
		CustomMetadataXSD customMetadataXSD = (CustomMetadataXSD) metadataXSDobject;
		Element record = doc.createElement(customMetadataXSD.getName());
		record.setAttribute("xmlns", customMetadataXSD.getXmlnsPlusName());
		record.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		record.setAttribute("xsi:schemaLocation", customMetadataXSD.getXmlnsPlusName() + " " + customMetadataXSD.getXSDWebLocation());
		//now put within element 'record', all the attributes of the template (combined with the values)
		//first for base elements
		for(String elemName : template.getBaseNameTypes().keySet()){
			String elVal = values.get(elemName);
			if(elVal!=null){
				Element element = doc.createElement(elemName);
				element.appendChild(doc.createTextNode(elVal));
				record.appendChild(element);
			}
		}
		//then for elements in subgroups
		for(String rootElemName : template.getAllSubGroupNames()){
			Element group = doc.createElement(rootElemName);
			for(String subElemName : template.getSubGroupNameTypes(rootElemName).keySet()){
				String elVal = values.get(rootElemName+":"+subElemName);
				if(elVal!=null){
					Element element = doc.createElement(subElemName);
					element.appendChild(doc.createTextNode(elVal));
					group.appendChild(element);
				}
			}
			if(group.hasChildNodes())
				record.appendChild(group);
		}
		//at the end, include the record element in metadata element
		metadata.appendChild(record);
		return metadata;
	}
	
	
	
	/**
	 * FOR DC FORMAT OUTPUT ! <br><br>
	 * The input template should be initiated ONLY ONCE, and used as input for every record created. <br><br>
	 * 
	 * It gets a hashmap of {variable_name, value} and creates a dom Element holding the record in the metadata format specified. <br>
	 * <b>values</b> should contain at least an "id" value.
	 * <br>Just seperate levels with a "<b>:</b>"
	 * 
	 * @param values Must have at least a variable named "id" within the values!
	 * @param template
	 * @param set Should contain one of the sets defined in the Repository 
	 * @param metadataXSDobject 
	 * @return
	 */
	public Record(HashMap<String,String> values, RecordTemplateDC template, Properties sets, OAIDCMetadataXSD oaidcMetadataXSD) {
		Document doc = ElementGenerator.getDocument();
		Element record = doc.createElement("record");
		record.appendChild(createRecordHeaderDC(values,sets));
		record.appendChild(createRecordMetadataDC(values, template, oaidcMetadataXSD));
		this.record = record;
	}
	
	private Element createRecordHeaderDC(HashMap<String,String> values, Properties sets ){
		Document doc = ElementGenerator.getDocument();
		Element header = doc.createElement("header");
		Element identifier = doc.createElement("identifier");
		identifier.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml(values.get("_OBJECT_ID_"))));
		Element datestamp = doc.createElement("datestamp");
		if(values.get("datestamp")!=null)
			datestamp.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml(values.get("datestamp"))));
		else
			datestamp.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml(Toolbox.dateTimeNow())));
		header.appendChild(identifier);
		header.appendChild(datestamp);
		for(Object set : sets.values()){
			Element setSpec = doc.createElement("setSpec");
			setSpec.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml((String)set)));
			header.appendChild(setSpec);
		}
		
		return header;
	}
	
	private Element createRecordMetadataDC(HashMap<String,String> values, RecordTemplateDC template, OAIDCMetadataXSD oaidcMetadataXSD){
		Document doc = ElementGenerator.getDocument();
		Element record = doc.createElement(oaidcMetadataXSD.getName());
		record.setAttribute("xmlns:oai_dc", oaidcMetadataXSD.getXmlnsPlusName());
		record.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
		record.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		record.setAttribute("xsi:schemaLocation", oaidcMetadataXSD.getXmlnsPlusName() + " " + oaidcMetadataXSD.getPathXSD());
		//simple oai_dc does not have subfields (use the custom if you want subfields, or extend classes for more complex formats (e.g. DCMI)
		for(String elemName : template.getNameTypes().keySet()){
			String elVal = values.get(elemName);
			if(elVal!=null){
				Element element = doc.createElement("dc:"+elemName); //preappend the 'dc:' for all values added
				element.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml(elVal)));
				record.appendChild(element);
			}
		}
		
		return record;
	}
	
	
	public Element getRecordElement(){
		return record;
	}
	
	
}
