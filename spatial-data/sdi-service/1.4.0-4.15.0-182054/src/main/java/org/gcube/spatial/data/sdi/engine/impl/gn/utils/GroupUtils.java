package org.gcube.spatial.data.sdi.engine.impl.gn.utils;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.gcube.spatial.data.sdi.model.gn.Group;
import org.gcube.spatial.data.sdi.utils.StringUtils;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import it.geosolutions.geonetwork.exception.GNLibException;

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
        	Element descElement=record.getChild("description");
        	String description=descElement!=null?descElement.getText():"";
        	Element mailElement=record.getChild("email");
        	String email=mailElement!=null?mailElement.getText():"";
        	toReturn.add(new Group(name,description,email,id));
        }
        return toReturn;
		}catch(Exception e){
			throw new GNLibException("Unable to parse response", e);
		}
        
	}
	
	public static Set<Group> parseUserJSONResponse(String groupResponse) throws GNLibException {
		try{
			HashSet<Group> toReturn=new HashSet<>();
			JSONArray array=new JSONArray(groupResponse);
			for(int i=0;i<array.length();i++){
				JSONObject groupObj=array.getJSONObject(i);
				Integer id=groupObj.getInt("id");
				String name=groupObj.getString("name");
				String description=groupObj.getString("description");
				String email=groupObj.getString("email");				
				
				toReturn.add(new Group(name,description,email,id));
			}
			return toReturn;
			}catch(Exception e){
				throw new GNLibException("Unable to parse group JSON response ",e);
			}
	}
	

	
	public static Group generateRandomGroup(Set<Group> existing, Integer nameLenght){
		Set<String> existingNames=new HashSet<>();
		int maxId=0;
		for(Group g:existing){
			existingNames.add(g.getName());
			if(maxId<g.getId())maxId=g.getId();
		}
		return new Group(StringUtils.generateNewRandom(existingNames, nameLenght), "generated group", "no.mail@nothing.org", maxId+1);		
	}
	
	
	
	
	
	public static Group getByName(Set<Group> toLookInto,String toLookFor){
		for(Group g:toLookInto)
			if(g.getName().equals(toLookFor)) return g;
		return null;
	}

	
}
