package org.gcube.application.framework.http.oaipmh;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.xml.transform.TransformerException;

import org.gcube.application.framework.http.oaipmh.Data.Pair;
import org.gcube.application.framework.oaipmh.objectmappers.MetadataElement;
import org.gcube.application.framework.oaipmh.objectmappers.RecordTemplateCustom;
import org.gcube.application.framework.oaipmh.objectmappers.RecordTemplateDC;
import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCubeRepository {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GCubeRepository.class);
	
	
    /**
     * 
     * @param collections  the (id,name) pairs of the collections
     * @param browsableFields  hashmap of (collectionID, listof(fieldID,fieldName))
     * @param presentableFields hashmap of (collectionID, listof(fieldID,fieldName))
     * @return
     * @throws MalformedURLException 
     */
	public static Repository createRepository (String host, String email, HashMap <String, String> collections, HashMap <String, ArrayList<Pair>> browsableFields, HashMap <String, ArrayList<Pair>> presentableFields) throws MalformedURLException {
		
		//FIRST CREATE THE INPUT DATA DESCRIBING THE REPOSITORY  
		ArrayList<String> emails = new ArrayList<String>();
		emails.add(email);
		
		Properties sets = new Properties();
		//now add the available collections as sets
		for(String collectionName : collections.values())
			sets.put(collectionName, collectionName); // first parameter is the setSpec, second one is the description
		
		// RecordTemplate describes the record template (what form of data the repository will output)  
		RecordTemplateCustom recordTemplateCustom = new RecordTemplateCustom("gCubeRecord");
		//and add all available presentable fields as a possible output on the record
		
		recordTemplateCustom.addNameType("id", "0", "1", "string"); //this should be the objectID when generating records
		recordTemplateCustom.addNameType("datestamp", "0", "1", "string");
		
		RecordTemplateDC recordTemplateDC = null;
		try {
			recordTemplateDC = new RecordTemplateDC("gcube2dc.properties");
			for(ArrayList<Pair> pList : presentableFields.values()){
				for(Pair pair : pList){
					recordTemplateCustom.addNameType(pair.getName(), "0", "1", "string");
					recordTemplateDC.addNameType(pair.getName(), "0", "1", "string");
				}
			}
		} catch (IOException e) {
			logger.debug("Could not find the .properties file for the DC mappings, so it will not support DC output."+e);
		}

		//NOW CREATE THE REPOSITORY
		Repository rep = new Repository("gCube", host , emails, Calendar.getInstance().getTime(), recordTemplateDC, recordTemplateCustom, sets);
		
		//MATERIALIZE ALL THE GENERATED XSDs, SO AS TO BE ACCESSIBLE BY HARVESTERS FROM THE INTERNET 
		try {
			rep.materializeXSDonFilesystem();
		} catch (TransformerException e1) {e1.printStackTrace();}
		
		return rep;
	}
	
	
	
}
