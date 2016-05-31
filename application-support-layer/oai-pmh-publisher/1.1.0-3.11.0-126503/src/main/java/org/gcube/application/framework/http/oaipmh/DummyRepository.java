package org.gcube.application.framework.http.oaipmh;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.gcube.application.framework.oaipmh.objectmappers.MetadataElement;
import org.gcube.application.framework.oaipmh.objectmappers.RecordTemplateCustom;
import org.gcube.application.framework.oaipmh.objectmappers.Repository;


/**
 * Static methods which create sample structures to demonstrate how to use the OAI_PMH_Transformer library.
 * @author nikolas
 *
 */
public class DummyRepository {
	
	
	/**
	 * <b>Creates a simple -sample- repository, just to demonstrate the usage of the library.</b>
	 * It creates the repository object and loads the Record template within.
	 * @return
	 */
	public static Repository createDummyTestRepository(){
		
		ArrayList<String> emails = new ArrayList<String>();
		emails.add("laskarisn@di.uoa.gr");
		
		RecordTemplateCustom recordTemplate = new RecordTemplateCustom("FigisRecord");
		recordTemplate.addNameType("id", "0", "1", "string");
		recordTemplate.addNameType("scientificName", "0", "1", "string");
		recordTemplate.addNameType("population", "0", "1", "string");

		HashMap<String, MetadataElement> subNameTypes = new HashMap<String, MetadataElement>();
		MetadataElement meta;
		meta = new MetadataElement("subID", "0", "1", "string");
		subNameTypes.put(meta.getName(), meta);
		meta = new MetadataElement("subScientificName", "0", "1", "string");
		subNameTypes.put(meta.getName(), meta);
		meta = new MetadataElement("subPopulation", "0", "1", "string");
		subNameTypes.put(meta.getName(), meta);
		recordTemplate.addSubGroupNameTypes("subTypes", subNameTypes);
		
		Repository rep = null;
		
		try {
			rep = new Repository("FIGIS", "http://dionysus.di.uoa.gr:8080", emails, Calendar.getInstance().getTime(), recordTemplate, new Properties());
			rep.materializeXSDonFilesystem();
		} catch (TransformerException e1) {e1.printStackTrace();} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return rep;
	}
	
	/**
	 * <b>Creates a simple -sample- record, just to demonstrate the usage of the library.</b>
	 * It creates the repository object and loads the Record template within.
	 * @return
	 */
//	public static Record createDummyTestRecord(){
//		return Record;
//	}
	
}
