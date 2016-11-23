package org.gcube.application.framework.oaipmh.objectmappers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.gcube.application.framework.oaipmh.constants.ResponseConstants;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
* this class will hold all the information to generate a custom XSL schema file
* @author nikolas
*
*/
public class CustomMetadataXSD {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomMetadataXSD.class);
	
	private String xmlns;
	private String xmlnsPlusName;
	private String name; // desirable name of the generated metadata xsd file
	private String hostname; //the public hostname of the machine. e.g. server1.google.com 
	private int port; //the port number 
	private String xsdBasePath; //filesystem base path
	private String pathXSD; //actually the path is xsdBasePath+"/"+name+".xsd"
	
	private String contentXSD; //contains the "stringified" XSD (xml)
	private String description; //a description for the whole object 
	
//	private RecordTemplate recordTemplate;
	
	/**
	 * 
	 * @param name desirable name of the generated metadata xsd
	 * @param hostname the hostname of the current deployment (tomcat) e.g. "site.google.com"
	 * @param port the port of the current deployment e.g. 8080
	 */
	public CustomMetadataXSD(String name, String hostname, int port){
		xmlns = "http://www.w3.org/2001/XMLSchema";
		description = "Custom XSD created on-the-fly to support custom records";
		xsdBasePath = System.getProperty("catalina.base") + "/webapps/ROOT/dtd/oai_pmh";
		File dir = new File(xsdBasePath);
		dir.mkdirs(); //create all dirs up to that path
		this.name = name;
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Should be used only for debugging purposes. e.g check
	 * @return the xml content of the XSD file 
	 */
	public String getXSDFileContent(){
		return contentXSD;
	}
	
	public String getXmlnsPlusName(){
		return xmlnsPlusName;
	}
	
	
	/**
	 * The path of the file as seen from the internet
	 * @return
	 * @throws MalformedURLException 
	 */
	public String getXSDWebLocation(){
		URL url = null;
		try{
			url =  new URL("http" , hostname , port , pathXSD.split("/webapps/ROOT")[1]);
		} catch(MalformedURLException e){
			logger.debug("Could not form the url for the XSD web location. " + e);
			return "";
		}
		return url.toString();
	}
	
	
	
//	/**
//	 * Adds a custom field to the generated XSD 
//	 * @param name  name of the custom field
//	 * @param minOccurs min occurs of the field within a record 
//	 * @param maxOccurs max occurs of the field within a record
//	 * @param type data type of the field (usually string)
//	 */
//	public void addCustomField(String name, String minOccurs, String maxOccurs, String type){
//		fields.add(new MetadataElement(name, minOccurs, maxOccurs, type));
//	}
	
	
	/**
	 * This function creates the file on the filesystem in order to be accessible from harvesters <br><br>
	 * Should be called AFTER creating any "MetadataXSD" instance and AFTER adding it custom fields through addCustomField()
	 * @throws TransformerException 
	 */
	public void materializeXSDonFilesystem(RecordTemplateCustom recordTemplate) throws TransformerException{
		//set the path of the generated XSD file
		pathXSD = xsdBasePath + "/" + name + ".xsd";
		//generate the XSD file and set it on contentXSD 
		contentXSD = createXSD(recordTemplate);
		//now write it on the filesystem
		Toolbox.writeOnFile(pathXSD, contentXSD);
	}
	
	
	/**
	 * creates the XSD (String)
	 * @throws TransformerException 
	 */
	private String createXSD(RecordTemplateCustom recordTemplate) throws TransformerException{
		String webFilePath = getXSDWebLocation();
		String webBasePath = webFilePath.substring(0,webFilePath.lastIndexOf("/"));
		xmlnsPlusName = webBasePath + "/" + name + "/";
		Document doc = ElementGenerator.getDocument();
		Element schema = doc.createElement("schema");
		schema.setAttribute("xmlns", "http://www.w3.org/2001/XMLSchema");
		schema.setAttribute("xmlns:"+name, xmlnsPlusName);
		schema.setAttribute("targetNamespace", xmlnsPlusName);
		schema.setAttribute("elementFormDefault", "qualified");
		
		Element annotation = doc.createElement("annotation");
		Element documentation = doc.createElement("documentation");
		documentation.appendChild(doc.createTextNode(description));
		annotation.appendChild(documentation);
		schema.appendChild(annotation);
		
		Element element = doc.createElement("element");
		element.setAttribute("name", name);
		element.setAttribute("type", name+":"+recordTemplate.getRecordName());
		schema.appendChild(element);
		
		Element complexType = doc.createElement("complexType");
		complexType.setAttribute("name", recordTemplate.getRecordName());
		Element all = doc.createElement("all");
		
		//add all the base name-type
		for(MetadataElement field : recordTemplate.getBaseNameTypes().values()){
			Element el = doc.createElement("element");
			el.setAttribute("name", field.getName());
			el.setAttribute("minOccurs", field.getMinOccurs());
			el.setAttribute("maxOccurs", field.getMaxOccurs());
			el.setAttribute("type", field.getType());
			all.appendChild(el);
		}
		//also add the sub-nameTypes
		for(String subname : recordTemplate.getAllSubGroupNames()){
			Element el = doc.createElement("element");
			el.setAttribute("name", subname);
			el.setAttribute("minOccurs", "0");
			el.setAttribute("maxOccurs", "unlimited");
			el.setAttribute("type", name+":"+subname);
			all.appendChild(el);
		}
		complexType.appendChild(all);
		schema.appendChild(complexType);
		//now add the remaining complex types, just as above
		for(String subname : recordTemplate.getAllSubGroupNames()){
			HashMap<String,MetadataElement> subNameTypes = recordTemplate.getSubGroupNameTypes(subname);
			complexType = doc.createElement("complexType");
			complexType.setAttribute("name", subname);
			Element sequence = doc.createElement("sequence");
			for(MetadataElement el : subNameTypes.values()){
				Element elem = doc.createElement("element");
				elem.setAttribute("name", el.getName());
				elem.setAttribute("minOccurs", el.getMinOccurs());
				elem.setAttribute("maxOccurs", el.getMaxOccurs());
				elem.setAttribute("type", el.getType());
				sequence.appendChild(elem);
			}
			complexType.appendChild(sequence);
			schema.appendChild(complexType);
		}
		
		return ElementGenerator.domToXML(schema);

	}
	
	public String getName(){
		return name;
	}
	

}



////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
/// DO NOT EDIT THE CODE BELOW. WORKS PERFECTLY WITH THE OLD MODEL /////
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////

///**
// * this class will hold all the information to generate a custom XSL schema file
// * @author nikolas
// *
// */
//public class CustomMetadataXSD {
//	
//	private static final Logger logger = LoggerFactory.getLogger(CustomMetadataXSD.class);
//	
//	private String xmlns;
//	private String name; // desirable name of the generated metadata xsd file
//	private String hostname; //the public hostname of the machine. e.g. server1.google.com 
//	private int port; //the port number 
//	private String complexTypeName; //name of the complex (custom) structure within the xsd file
//	private ArrayList <MetadataElement> fields;
//	private String xsdBasePath; //filesystem base path
//	
//	private String pathXSD; //actually the path is xsdBasePath+"/"+name+".xsd"
//	private String contentXSD; //contains the "stringified" XSD (xml)
//	private String description;
//	
//	
//	/**
//	 * 
//	 * @param name desirable name of the generated metadata xsd
//	 * @param hostname the hostname of the current deployment (tomcat) e.g. "site.google.com"
//	 * @param port the port of the current deployment e.g. 8080
//	 */
//	public CustomMetadataXSD(String name, String hostname, int port){
//		fields = new ArrayList<MetadataElement>();
//		xmlns = "http://www.w3.org/2001/XMLSchema";
//		description = "Custom XSD created on-the-fly to support custom records";
//		xsdBasePath = System.getProperty("catalina.base") + "/webapps/ROOT/dtd/oai_pmh";
//		File dir = new File(xsdBasePath);
//		dir.mkdirs(); //create all dirs up to that path.
//		this.name = name;
//		this.complexTypeName = name + "Struct";
//		this.hostname = hostname;
//		this.port = port;
//	}
//	
//	/**
//	 * Should be used only for debugging purposes. e.g check
//	 * @return the xml content of the XSD file 
//	 */
//	public String getXSDFileContent(){
//		return contentXSD;
//	}
//	
//	
//	/**
//	 * The path of the file as seen from the internet
//	 * @return
//	 * @throws MalformedURLException 
//	 */
//	public String getXSDWebLocation(){
//		URL url = null;
//		try{
//			url =  new URL("http" , "noone.di.uoa.gr" , 8080 , pathXSD.split("/webapps/ROOT")[1]);
//		} catch(MalformedURLException e){
//			logger.debug("Could not form the url for the XSD web location. " + e);
//			return "";
//		}
//		return url.toString();
//	}
//	
//	/**
//	 * Adds a custom field to the generated XSD 
//	 * @param name  name of the custom field
//	 * @param minOccurs min occurs of the field within a record 
//	 * @param maxOccurs max occurs of the field within a record
//	 * @param type data type of the field (usually string)
//	 */
//	public void addCustomField(String name, String minOccurs, String maxOccurs, String type){
//		fields.add(new MetadataElement(name, minOccurs, maxOccurs, type));
//	}
//	
//	
//	/**
//	 * This function creates the file on the filesystem in order to be accessible from harvesters <br><br>
//	 * Should be called AFTER creating any "MetadataXSD" instance and AFTER adding it custom fields through addCustomField()
//	 * @throws TransformerException 
//	 */
//	public void materializeXSDonFilesystem() throws TransformerException{
//		//generate the XSD file and set it on contentXSD 
//		contentXSD = createXSD();
//		//set the path of the generated XSD file
//		pathXSD = xsdBasePath + "/" + name + ".xsd";
//		//now write it on the filesystem
//		Toolbox.writeOnFile(pathXSD, contentXSD);
//	}
//	
//	
//	/**
//	 * creates the XSD (String)
//	 * @throws TransformerException 
//	 */
//	private String createXSD() throws TransformerException{
//		Document doc = ElementGenerator.getDocument();
//		Element schema = doc.createElement("schema");
//		schema.setAttribute("xmlns", xmlns);
//		schema.setAttribute("xmlns:"+name, xsdBasePath+"/"+name+"/");
//		schema.setAttribute("targetNamespace", xsdBasePath+"/"+name+"/");
//		schema.setAttribute("elementFormDefault", "qualified");
//		
//		Element annotation = doc.createElement("annotation");
//		Element documentation = doc.createElement("documentation");
//		documentation.appendChild(doc.createTextNode(description));
//		annotation.appendChild(documentation);
//		schema.appendChild(annotation);
//		
//		Element element = doc.createElement("element");
//		element.setAttribute("name", name);
//		element.setAttribute("type", name+":"+complexTypeName);
//		schema.appendChild(element);
//		
//		Element complexType = doc.createElement("complexType");
//		complexType.setAttribute("name", complexTypeName);
//		Element all = doc.createElement("all");
//		
//		for(MetadataElement field : fields){
//			Element el = doc.createElement("element");
//			el.setAttribute("name", field.name);
//			el.setAttribute("minOccurs", field.minOccurs);
//			el.setAttribute("maxOccurs", field.maxOccurs);
//			el.setAttribute("type", field.type);
//			all.appendChild(el); 
//		}
//		complexType.appendChild(all);
//		schema.appendChild(complexType);
//		return ElementGenerator.domToXML(schema);
//
//	}
//	
//	public String getName(){
//		return name;
//	}
//	
//
//}
//
///**
// * for minOccurs and maxOccurs, values are from 0 to "unbounded"
// * @author nikolas
// *
// */
//class MetadataElement{
//	String name;
//	String minOccurs; //values from "0" to "unbounded"
//	String maxOccurs; //values from "0" to "unbounded"
//	String type; //type is usually string
//	public MetadataElement(String name, String minOccurs, String maxOccurs, String type){
//		this.name = name;
//		this.minOccurs = minOccurs;
//		this.maxOccurs = maxOccurs;
//		this.type = type;
//	}
//}




