package org.gcube.application.framework.oaipmh.objectmappers;

import java.util.HashMap;
import java.util.Properties;

import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Identifier {

	private Element identifier;
	
	/**
	 * It gets a hashmap of {variable_name, value} and creates a dom Element holding the record identifier (header). <br>
	 * <b>values</b> should contain at least an "id" value.
	 * 
	 * @param values Must have at least a key "id" and (optionally) a key "datestamp" within the values!
	 * @param set Should contain one of the sets defined in the Repository 
	 */
	public Identifier(HashMap<String,String> values, Properties sets) {
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
		this.identifier = header;
	}

	
	public Element getIdentifier(){
		return identifier;
	}
	
}
