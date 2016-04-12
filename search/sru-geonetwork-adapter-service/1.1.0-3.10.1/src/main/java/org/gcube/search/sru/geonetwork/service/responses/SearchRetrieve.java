package org.gcube.search.sru.geonetwork.service.responses;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.search.sru.geonetwork.commons.constants.Constants;
import org.gcube.search.sru.geonetwork.service.SruGeoNwService;
import org.gcube.search.sru.geonetwork.service.exceptions.CqlException;
import org.gcube.search.sru.geonetwork.service.exceptions.NotSupportedException;
import org.gcube.search.sru.geonetwork.service.parsers.CqlParser;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.io.Resources;

public class SearchRetrieve {

	public static final Logger logger = LoggerFactory.getLogger(SearchRetrieve.class);
	
	public final String xmlnsSru = "http://www.loc.gov/zing/srw/";
	public final String xmlnsZr = "http://explain.z3950.org/dtd/2.1/";
	public final String dcContextIdentifier = "info:srw/cql-context-set/1/dc-v1.1";
	public final String dcSchemaIdentifier = "info:srw/schema/1/dc-v1.1";
	public final String recordSchema = dcSchemaIdentifier;
	
	private Float version;
	private String recordPacking;
	private String totalNumRecords;
	
	private Transformer xsltTransformer;
	
	

	public SearchRetrieve(Float version, String recordPacking, String host, String port,String basePath, String sruName, String totalNumRecords) throws NotSupportedException, IOException{
		if(version>=1.2)
			throw new NotSupportedException("Only version 1.1 is supported.");
		this.version = version;
		if(recordPacking==null)
			recordPacking = "xml";
		if(!("xml").equals(recordPacking))
			throw new NotSupportedException("Record packing other than xml is not supported. Will output xml.");
		this.recordPacking = recordPacking;
		this.totalNumRecords = totalNumRecords;
		
		//for geonetwork metadata records to sru dc records transformations
		try {
			xsltTransformer = TransformerFactory.newInstance().newTransformer(new StreamSource(Resources.getResource(Constants.XSLT2DC_FILE_NAME).openStream()));
		} catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e) {
			logger.debug("Could not instantiate the XSLT transformer", e);
		}
		xsltTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xsltTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
		//for parsing xml strings to DOM Elements
		
		
//		try {
//			simpleDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//		} catch (ParserConfigurationException e) {
//			logger.debug("Could not instantiate the Xml to DOM transformer", e);
//		}
		
	}
	
	
	
	/**
	 * Transforms the given  geonetworkRecord (as returned from the geonetwork client), by applying the 
	 * Constants.XSLT2DC_FILE_NAME xstl and returns the record as a DOM Element
	 * @param geonetworkRecord
	 * @return
	 */
	public Element transformRecord(org.jdom.Element geonetworkRecord, DocumentBuilder docBuilder) {
		try {
			StringReader reader = new StringReader(elemToString(geonetworkRecord));
			StringWriter writer = new StringWriter();
			xsltTransformer.transform(new javax.xml.transform.stream.StreamSource(reader), new javax.xml.transform.stream.StreamResult(writer));
			Element transElem = ((Document)docBuilder.parse(new ByteArrayInputStream(writer.toString().getBytes("UTF-8")))).getDocumentElement();
			return transElem;
		} catch (Exception e) {
			logger.debug("Could not transform the record into dc format, using the xslt");
		}
		return null;
	}
	
	
	private String elemToString(org.jdom.Element elem) {
		return new XMLOutputter(Format.getPrettyFormat()).outputString(elem);
	}

	
	public String getRecordPacking(){
		return recordPacking;
	}
	public Float getVersion(){
		return version;
	}
	
	
	
//	public DocumentBuilder getSimpleDocBuilder() {
//		return simpleDocBuilder;
//	}
	
	
	
//	public static void main(String[] args) throws IOException, URISyntaxException, TransformerException, GNLibException, GNServerException, NumberFormatException, NotSupportedException {
//		SearchRetrieve sr = new SearchRetrieve(Float.parseFloat("1.1"), "xml", "dionysus.di.uoa.gr", "8080", "/", "/", "1000");
//		sr.transformRecord(sr.getARecord());
//	}
	
	
	
//	private org.jdom.Element getARecord() throws GNLibException, GNServerException, IOException {
//
//		String gnServiceURL = "http://www.fao.org/geonetwork";// "http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork";
//		String gnUsername = "admin";
//		String gnPassword = "admin";
//		// Create a GeoNetwork client pointing to the GeoNetwork service
//		GNClient client = new GNClient(gnServiceURL);
//
//		// Perform a login into GN
//		boolean logged = client.login(gnUsername, gnPassword);
//
//		if (!logged)
//			System.out.println("Could not log in. Will operate on public resources.");
//
//		GNSearchRequest searchRequest = new GNSearchRequest();
//
//		// add a predefined search field
//		searchRequest.addParam(GNSearchRequest.Param.any, "AVERAGE SEASONAL TEMPERATURE FOR | PRINCETON METEOROLOGICAL FORCING");// "tsetse");
//
//		// only local results
//		searchRequest.addConfig(GNSearchRequest.Config.remote, "off");
//
//		// do the search!
//		GNSearchResponse searchResponse = client.search(searchRequest);
//		
//		System.out.println("got " + searchResponse.getCount() + " results");
//
//		return client.get(searchResponse.getMetadata(0).getId());
//
//	}
	 
	 
	
}
