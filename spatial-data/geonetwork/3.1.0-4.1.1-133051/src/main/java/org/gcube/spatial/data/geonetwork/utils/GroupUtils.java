package org.gcube.spatial.data.geonetwork.utils;

import it.geosolutions.geonetwork.exception.GNLibException;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import lombok.extern.slf4j.Slf4j;

import org.gcube.spatial.data.geonetwork.model.Group;
import org.jdom.input.SAXBuilder;

@Slf4j
public class GroupUtils {

	private static XPath xpath = XPathFactory.newInstance().newXPath();

	
	public static Set<Group> parseGroupXMLResponse(String xml) throws GNLibException {
		try{
			HashSet<Group> toReturn=new HashSet<>();
		SAXBuilder builder = new SAXBuilder();
        org.jdom.Element responseEl= builder.build(new StringReader(xml)).detachRootElement();
        for(Object recordObj:responseEl.getChildren("record")){
        	org.jdom.Element record=(org.jdom.Element) recordObj;
        	Integer id=Integer.parseInt(record.getChild("id").getText());
        	String name=record.getChild("name").getText();
        	String description=record.getChild("description").getText();
        	String email=record.getChild("email").getText();
        	toReturn.add(new Group(name,description,email,id));
        }
        return toReturn;
		}catch(Exception e){
			throw new GNLibException("Unable to parse response", e);
		}
        
	}
	

//	public static Set<Group> parseGroupPage(String page) throws GNLibException{
//		try{
//			page=page.substring(page.indexOf("<div id=\"content_container\""),page.lastIndexOf("</div>")+("</div>").length());
//			page=page.replaceAll("&nbsp;", "");
//			HashSet<Group> toReturn=new HashSet<>();
//			String expression = "//table/tr/td/table";
//			InputSource inputSource = new InputSource(new StringReader(page));
//			Node tableNode = (Node) xpath.evaluate(expression, inputSource, XPathConstants.NODE);
//
//			NodeList tableChildren=((Element)tableNode.getParentNode()).getElementsByTagName("tr");
//			for(int i=1;i<tableChildren.getLength();i++){ // starts from 1 to skip first row
//				Element tr=(Element)tableChildren.item(i);
//				NodeList tds=tr.getElementsByTagName("td");			
//				String name=XMLUtils.getElementContent((Element)tds.item(0));
//				String description=XMLUtils.getElementContent((Element)tds.item(1));
//				String mail=XMLUtils.getElementContent((Element)tds.item(2));
//				
//				String idLink=((Element)tds.item(3)).getElementsByTagName("button").item(0).getAttributes().getNamedItem("onclick").getNodeValue();
//				
//				Integer id=parseGroupId(idLink);
//				toReturn.add(new Group(name,description,mail,id));
//
//			}
//
//			return toReturn;
//		}catch(XPathExpressionException e){			
//			throw new GNLibException("Unable to parse response", e);
//		}
//	}

	//	 private static Element parse(String s) throws GNLibException {
	//	        try {
	//	            SAXBuilder builder = new SAXBuilder();
	//	            return builder.build(new StringReader(s)).detachRootElement();
	//	        } catch (Exception ex) {
	//	           log.error("Error parsing GN response: " + s);
	//	            throw new GNLibException("Error parsing GN response: " + ex.getMessage(), ex);
	//	        }
	//	    }

//	private static Integer parseGroupId(String tdContent){
//		Integer indexOfIdParameter=tdContent.indexOf("id=");
//		return Integer.parseInt(tdContent.substring(indexOfIdParameter+3, tdContent.indexOf("'", indexOfIdParameter)));
//	}

	
	
	public static Group generateRandomGroup(Set<Group> existing, Integer nameLenght){
		Set<String> existingNames=new HashSet<>();
		for(Group g:existing)existingNames.add(g.getName());		
		return new Group(StringUtils.generateNewRandom(existingNames, nameLenght), "generated group", "no.mail@nothing.org", null);		
	}
	
	
	
	
	
	public static Group getByName(Set<Group> toLookInto,String toLookFor){
		for(Group g:toLookInto)
			if(g.getName().equals(toLookFor)) return g;
		return null;
	}
    
}
