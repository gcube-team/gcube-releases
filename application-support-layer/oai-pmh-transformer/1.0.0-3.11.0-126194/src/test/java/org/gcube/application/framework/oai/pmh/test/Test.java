package org.gcube.application.framework.oai.pmh.test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.gcube.application.framework.oaipmh.Response;
import org.gcube.application.framework.oaipmh.exceptions.MissingRequestParameters;
import org.gcube.application.framework.oaipmh.objectmappers.Identifier;
import org.gcube.application.framework.oaipmh.objectmappers.MetadataElement;
import org.gcube.application.framework.oaipmh.objectmappers.Record;
import org.gcube.application.framework.oaipmh.objectmappers.RecordTemplateCustom;
import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.gcube.application.framework.oaipmh.verbcontainers.GetRecord;
import org.w3c.dom.Element;

public class Test {

	
	public static void main(String [] args) throws MalformedURLException, MissingRequestParameters{
		
		//FIRST CREATE THE INPUT DATA DESCRIBING THE REPOSITORY  
		ArrayList<String> emails = new ArrayList<String>();
		emails.add("laskarisn@di.uoa.gr");
		
		//this describes the record template (what form of data the repository will output)  
		RecordTemplateCustom recordTemplate = new RecordTemplateCustom("FigisRecord");
		recordTemplate.addNameType("id", "0", "1", "string");
		recordTemplate.addNameType("datestamp", "0", "1", "string");
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
		
		//NOW CREATE THE REPOSITORY
		Repository rep = new Repository("FIGIS", "http://dionysus.di.uoa.gr:8081", emails, Calendar.getInstance().getTime(), recordTemplate, new Properties());
		
		//MATERIALIZE ALL THE GENERATED XSDs, SO AS TO BE ACCESSIBLE BY HARVESTERS FROM THE INTERNET 
		try {
			rep.materializeXSDonFilesystem();
		} catch (TransformerException e1) {e1.printStackTrace();}
		
		//the properties from the HTTP get request (e.g. "verb")
		Properties props = new Properties();

		//TESTS BELOW SHOULD BE RUN ONE AT A TIME, NOT ALL TOGETHER.
		
		/*
		//test verb="Identify"
		props.put("verb", "Identify");
		Response resp = new Response();
		try {
			System.out.println(resp.getIdentifyResponse(props,rep));
		} catch (Exception e) {e.printStackTrace();}
		*/
		
		/*
		//test verb="ListMetadataFormats"
		props.put("verb", "ListMetadataFormats");
		Response resp = new Response();
		try {
			System.out.println(resp.getListMetadataFormatsResponse(props, rep));
		} catch (Exception e) {e.printStackTrace();}
		*/
		
		/*
		//test verb="ListSets"
		props.put("verb", "ListSets");
		Response resp = new Response();
		try {
			System.out.println(resp.getListSetsResponse(props,rep));
		} catch (Exception e) {e.printStackTrace();}
		*/
		
		/*
		//test verb="GetRecord"  -- add all the record variables, according to the template defined within the Repository.
		HashMap<String,String> values = new HashMap<String, String>();
		values.put("id", "1234567");
		values.put("datestamp", Toolbox.dateTimeNow()); //or the original, if available...
		values.put("scientificName", "Carcharodon Carcarias");
		values.put("population", "15762");
		values.put("subTypes:subID", "ufjkgh");
		values.put("subTypes:subScientificName", "UIYJKHUIG");
		values.put("subTypes:subPopulation","18");
		
		Record record = new Record(values, recordTemplate, rep.getSets(), rep.getCustomMetadataXSD());
	
		props.put("verb", "GetRecord");
		Response resp = new Response();
		try {
			System.out.println(resp.getGetRecordResponse(props, rep, record));
		} catch (TransformerException e) {e.printStackTrace();}
		*/
		
		/*
		//test ListRecords
		//loop over this, adding the records. In this example we have static (the same) values for each of the 10 loops
		ArrayList<Record> records = new ArrayList<Record>();
		for(int i=0;i<10;i++){
			HashMap<String,String> values = new HashMap<String, String>();
			values.put("id", "1234567");
			values.put("datestamp", Toolbox.dateTimeNow()); //or the original, if available...
			values.put("scientificName", "Carcharodon Carcarias");
			values.put("population", "15762");
			values.put("subTypes:subID", "ufjkgh");
			values.put("subTypes:subScientificName", "UIYJKHUIG");
			values.put("subTypes:subPopulation","18");
			
			Record record = new Record(values, recordTemplate, rep.getSets(), rep.getCustomMetadataXSD());
			records.add(record);
		}
		props.put("verb", "ListRecords");
		Response resp = new Response();
		try {
			System.out.println(resp.getListRecordsResponse(props, rep, records));
		} catch (TransformerException e) {e.printStackTrace();}
		*/
		
		
		/*
		//test ListIdentifiers
		//loop over this, adding the records. In this example we have static (the same) values for each of the 10 loops
		ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
		for(int i=0;i<10;i++){
			HashMap<String,String> values = new HashMap<String, String>();
			values.put("id", "1234567");
//			values.put("datestamp", Toolbox.dateTimeNow()); //or the original, if available...
			Identifier identifier = new Identifier(values, rep.getSets());
			identifiers.add(identifier);
		}
		props.put("verb", "ListIdentifiers");
		Response resp = new Response();
		try {
			System.out.println(resp.getListIdentifiersResponse(props, rep, identifiers, 0, 1000));//getListRecordsResponse(props, rep, records));
		} catch (TransformerException e) {e.printStackTrace();}
		
		*/
		
		
	}
	
	
	
}
