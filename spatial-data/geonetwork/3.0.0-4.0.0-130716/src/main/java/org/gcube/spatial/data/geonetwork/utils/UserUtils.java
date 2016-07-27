package org.gcube.spatial.data.geonetwork.utils;

import it.geosolutions.geonetwork.exception.GNLibException;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.jdom.input.SAXBuilder;

public class UserUtils {

	public static Set<User> parseUserXMLResponse(String toParse) throws GNLibException{
		try{
			HashSet<User> toReturn=new HashSet<>();
			SAXBuilder builder = new SAXBuilder();
			org.jdom.Element responseEl= builder.build(new StringReader(toParse)).detachRootElement();
			for(Object recordObj:responseEl.getChildren("record")){
				org.jdom.Element record=(org.jdom.Element) recordObj;
				Integer id=Integer.parseInt(record.getChildText("id"));
				String username=record.getChildText("username");
				String password=record.getChildText("password");
				Profile profile=Profile.valueOf(record.getChildText("profile"));
				toReturn.add(new User(id,username, password, profile));
			}
			return toReturn;
		}catch(Exception e){
			throw new GNLibException("Unable to parse response", e);
		}

	}

	
	public static User generateRandomUser(Set<User> existing, Integer nameLenght, Integer passwordLength){
		Set<String> existingNames=new HashSet<>();
		for(User g:existing)existingNames.add(g.getUsername());		
		return new User(0,StringUtils.generateNewRandom(existingNames, nameLenght),StringUtils.generateRandomString(passwordLength),Profile.RegisteredUser);	
	}
	
	
	public static Set<Integer> parseGroupsByUserResponse(String toParse) throws GNLibException{
		try{
			HashSet<Integer> toReturn=new HashSet<>();
			SAXBuilder builder = new SAXBuilder();
			org.jdom.Element responseEl= builder.build(new StringReader(toParse)).detachRootElement();
			for(Object recordObj:responseEl.getChildren("group")){		
				org.jdom.Element record=(org.jdom.Element) recordObj;
				Integer id=Integer.parseInt(record.getChildText("id"));				
				toReturn.add(id);
			}
			return toReturn;
		}catch(Exception e){
			throw new GNLibException("Unable to parse response", e);
		}
	}
	
	
	public static User getByName(Set<User> toLookInto,String toLookFor){
		for(User g:toLookInto)
			if(g.getUsername().equals(toLookFor)) return g;
		return null;
	}
	
}
