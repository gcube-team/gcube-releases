package org.gcube.datatransformation.datatransformationlibrary.model;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * A transformation program is an XML document describing one or more possible transformations from a source content type to a target content type. 
 * Each transformation program references to at most one {@link Program} and it contains one or more {@link TransformationUnit}s for each possible transformation.
 * </p>
 */
public class TransformationProgram {
	
	private String id=null;
	private String name=null;
	private String description=null;
	private Transformer transformer=null;
	private ArrayList<TransformationUnit> transformationUnits=null;
	
	/**
	 * Returns the id of the transformation program.
	 * @return the id of the transformation program.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id of the transformation program.
	 * @param id the id of the transformation program.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the name of the transformation program.
	 * @return the name of the transformation program.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the transformation program.
	 * @param name the name of the transformation program.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the description of the transformation program.
	 * @return the description of the transformation program.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description of the transformation program.
	 * @param description the description of the transformation program.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the TUs that this transformation program contains.
	 * @return the TUs that this transformation program contains.
	 */
	public ArrayList<TransformationUnit> getTransformationUnits() {
		return transformationUnits;
	}
	
	/**
	 * Sets the TUs that this transformation program contains.
	 * @param transformationUnits the TUs that this transformation program contains.
	 */
	public void setTransformationUnits(ArrayList<TransformationUnit> transformationUnits) {
		this.transformationUnits = transformationUnits;
	}
	
	/**
	 * Returns the transformer that this transformation program references.
	 * @return the transformer that this transformation program references.
	 */
	public Transformer getTransformer() {
		return transformer;
	}
	
	/**
	 * Sets the transformer that this transformation program references.
	 * @param transformer the transformer that this transformation program references.
	 */
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}
	
	/**
	 * A simple test for parsing and serializing transformation programs.
	 * @param args The arguments of the main method.
	 * @throws Exception If an error occurred in the test.
	 */
	public static void main(String[] args) throws Exception {
		TransformationProgram tp = new TransformationProgram();
		Transformer transformer = new Transformer();
		SoftwarePackage sft = new SoftwarePackage();
		sft.setId("fdsfds");
		sft.setLocation(new URL("http://lala.org"));
		ArrayList<SoftwarePackage> softwarePackages = new ArrayList<SoftwarePackage>();
		softwarePackages.add(sft);
		transformer.setSoftwarePackages(softwarePackages);
		tp.setTransformer(transformer);
		
		TransformationUnit transformationUnit = new TransformationUnit();
		transformationUnit.setId("0");
		Target trg = new Target();
		trg.setContentType(new ContentType());
		transformationUnit.setTarget(trg);
		ArrayList<TransformationUnit> tUnits = new ArrayList<TransformationUnit>();
		tUnits.add(transformationUnit);
		tp.setTransformationUnits(tUnits);
		System.out.println(tp.toXML());
	}
	
	/**
	 * Creates a transformation program instance from its xml representation.
	 * @param tpresource The xml representation.
	 * @throws Exception If the transformation program could not be parsed.
	 */
	public void fromDOM(Element tpresource) throws Exception {
		
		/* Parsing the ID */
		Element id = (Element)tpresource.getElementsByTagName(XMLDefinitions.ELEMENT_id).item(0);
		if(id!=null){
			this.setId(id.getTextContent());
		}
		
		/* Parsing the Name */
		Element name = (Element)tpresource.getElementsByTagName(XMLDefinitions.ELEMENT_name).item(0);
		if(name!=null){
			this.setName(name.getTextContent());
		}
		
		/* Parsing the Description */
		Element desc = (Element)tpresource.getElementsByTagName(XMLDefinitions.ELEMENT_description).item(0);
		if(desc!=null){
			this.setDescription(desc.getTextContent());
		}
		
		/* Parsing the Transformer */
		Element transformer = (Element)tpresource.getElementsByTagName(XMLDefinitions.ELEMENT_transformer).item(0);
		if(transformer!=null){
			this.transformer = new Transformer();
			this.transformer.fromDOM(transformer);
		}
//		
		/* Parsing the Transformations */
		ArrayList<TransformationUnit> tUnitslist = new ArrayList<TransformationUnit>();
		Element transformation;
		int cnt=0;
		while((transformation=(Element)tpresource.getElementsByTagName(XMLDefinitions.ELEMENT_transformationUnit).item(cnt++))!=null){
			TransformationUnit transformationUnitInst = new TransformationUnit();
			transformationUnitInst.setTransformationProgram(this);
			transformationUnitInst.fromDOM(transformation);
			tUnitslist.add(transformationUnitInst);
		}
		this.setTransformationUnits(tUnitslist);
	}
	
	public void toDom(Element tpresource) throws Exception{
		Document doc = tpresource.getOwnerDocument();
		Element resource = doc.createElement(XMLDefinitions.ELEMENT_transformationProgram);
		
		Element ID = doc.createElement(XMLDefinitions.ELEMENT_id);
		ID.setTextContent(this.id);
		resource.appendChild(ID);
		
		Element type = doc.createElement(XMLDefinitions.ELEMENT_type);
//		type.setTextContent(XMLDefinitions.VALUE_genericresource);
		resource.appendChild(type);
		
		Element profile = doc.createElement(XMLDefinitions.ELEMENT_profile);
		resource.appendChild(profile);
		
		Element secType = doc.createElement("SecondaryType");
		secType.setTextContent("DTSTransformationProgram");
		profile.appendChild(secType);
		
		Element name = doc.createElement(XMLDefinitions.ELEMENT_name);
		name.setTextContent(this.name);
		profile.appendChild(name);
		
		Element desciption = doc.createElement(XMLDefinitions.ELEMENT_description);
		desciption.setTextContent(this.description);
		profile.appendChild(desciption);
		
		Element body = doc.createElement(XMLDefinitions.ELEMENT_body);
		profile.appendChild(body);
		
		Element tp = doc.createElement(XMLDefinitions.ELEMENT_transformationProgram);
		body.appendChild(tp);
		
		//Transformer...
		if(this.transformer!=null){
			this.transformer.toDOM(tp);
		}else{
			Element transformerelm = doc.createElement(XMLDefinitions.ELEMENT_transformer);
			tp.appendChild(transformerelm);
		}
		
		//Transformations...
		if(this.transformationUnits==null || this.transformationUnits.size()<1){
			throw new Exception("Transformation program must contain at least one transformationUnit");
		}
		Element transformations = doc.createElement(XMLDefinitions.ELEMENT_transformationUnits);
		for(TransformationUnit tr: this.getTransformationUnits()){
			tr.toDOM(transformations);
		}
		tp.appendChild(transformations);
		
		tpresource.appendChild(resource);
	}
	
	private static Logger log = LoggerFactory.getLogger(TransformationProgram.class);
	/**
	 * Returns the xml representation of the transformation program.
	 * @return the xml representation of the transformation program.
	 * @throws Exception If the transformation program could not be serialized.
	 */
	public String toXML() throws Exception{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element resource = doc.createElement(XMLDefinitions.ELEMENT_resource);
			doc.appendChild(resource);
			
			Element ID = doc.createElement(XMLDefinitions.ELEMENT_id);
			ID.setTextContent(this.id);
			resource.appendChild(ID);
			
			Element type = doc.createElement(XMLDefinitions.ELEMENT_type);
//			type.setTextContent(XMLDefinitions.VALUE_genericresource);
			resource.appendChild(type);
			
			Element profile = doc.createElement(XMLDefinitions.ELEMENT_profile);
			resource.appendChild(profile);
			
			Element secType = doc.createElement("SecondaryType");
			secType.setTextContent("DTSTransformationProgram");
			profile.appendChild(secType);
			
			Element name = doc.createElement(XMLDefinitions.ELEMENT_name);
			name.setTextContent(this.name);
			profile.appendChild(name);
			
			Element desciption = doc.createElement(XMLDefinitions.ELEMENT_description);
			desciption.setTextContent(this.description);
			profile.appendChild(desciption);
			
			Element body = doc.createElement(XMLDefinitions.ELEMENT_body);
			profile.appendChild(body);
			
			Element tp = doc.createElement(XMLDefinitions.ELEMENT_transformationProgram);
			body.appendChild(tp);
			
			//Transformer...
			if(this.transformer!=null){
				this.transformer.toDOM(tp);
			}else{
				Element transformerelm = doc.createElement(XMLDefinitions.ELEMENT_transformer);
				tp.appendChild(transformerelm);
			}
			
			//Transformations...
			if(this.transformationUnits==null || this.transformationUnits.size()<1){
				throw new Exception("Transformation program must contain at least one transformationUnit");
			}
			Element transformations = doc.createElement(XMLDefinitions.ELEMENT_transformationUnits);
			for(TransformationUnit tr: this.getTransformationUnits()){
				tr.toDOM(transformations);
			}
			tp.appendChild(transformations);
			
			//Creating the xml...
			TransformerFactory tFactory = TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tFactory.newTransformer();
	        transformer.setOutputProperty("omit-xml-declaration", "yes");
	        StringWriter sw = new StringWriter();
	        StreamResult result = new StreamResult(sw);
	        DOMSource source = new DOMSource(doc);
	        transformer.transform(source, result);
	        return sw.getBuffer().toString();
	        
		} catch (Exception e) {
			log.error("Could not serialize Transformation Program", e);
			throw new Exception("Could not serialize Transformation Program");
		}
	}
}
