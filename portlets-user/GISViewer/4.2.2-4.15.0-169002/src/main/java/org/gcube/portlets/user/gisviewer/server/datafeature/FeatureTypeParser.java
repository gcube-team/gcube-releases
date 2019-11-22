package org.gcube.portlets.user.gisviewer.server.datafeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Property;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FeatureTypeParser {
	
	/**
	 * 
	 */
	protected static final String TYPE = "type";
	/**
	 * 
	 */
	protected static final String NAME = "name";
	
	public static Logger logger = Logger.getLogger(FeatureTypeParser.class);
	
	public static List<Property> getProperties(String geoserverURL, String layerName) {
		
		String wfsRequestURL = "";
		String abstractFeatureTypeElement = "";

		//CASE MAP SERVER
		if(geoserverURL!=null && geoserverURL.contains("wxs")){
			logger.info("wms url contains 'wxs', is a map server? no appending /wfs");
			wfsRequestURL = geoserverURL;
			abstractFeatureTypeElement = "element";
		}else{
			logger.info("is geoserver append /wfs");
			wfsRequestURL = geoserverURL +"/wfs";
			abstractFeatureTypeElement = "xsd:element";
		}
		
		wfsRequestURL = wfsRequestURL.endsWith("?")?wfsRequestURL:wfsRequestURL+"?";
		
		wfsRequestURL+="service=wfs&version=1.1.0&request=DescribeFeatureType&typeName="+ layerName;
		
//		wfsRequestURL = geoserverURL
//			+ "/wfs?service=wfs&version=1.1.0&request=DescribeFeatureType&typeName="
//			+ layerName;

		logger.info("Get property create wfs request: "+wfsRequestURL);
		System.out.println("Get property create wfs request: "+wfsRequestURL);
		
		List<Property> properties = new ArrayList<Property>();
		
		try {

			// Now we parse the XML file with JAXP.
			// First of all we do need to load the document into DOM Document object.
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); 
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(wfsRequestURL);
			
			// normalize text representation
            doc.getDocumentElement ().normalize ();

            // get all layer's properties 
			NodeList elements = doc.getElementsByTagName(abstractFeatureTypeElement);
			
            for(int i=0; i<elements.getLength() ; i++){
                Node node = elements.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                	// get property name and property type
                    Element element = (Element)node;
                    
                    String name = element.getAttribute(NAME);
                    String type = element.getAttribute(TYPE);
                    
                    if (name!=null && type!=null)
                    	properties.add(new Property(name, type));
                }
            }
            
		} catch (ParserConfigurationException e) {
			logger.error("Error: ",e);
		} catch (SAXException e) {
			logger.error("Error: ",e);
		} catch (IOException e) {
			logger.error("Error: ",e);
		}
		
		logger.info("Get property returning "+properties.size()+" properties");
		System.out.println("Get property returning "+properties.size()+" properties");
		
		return properties;
	}
	
	public static void main(String[] args) {
		
		//MAPSERVER
		String LINK = "http://egip.brgm-rec.fr/wxs/?";
		String layerName = "HeatFlowUnit";
		
		//GEOSERVER
//		String LINK = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/wfs";
//		String layerName = "aquamaps:depthMean";

		System.out.println(getProperties(LINK, layerName));
	}
}