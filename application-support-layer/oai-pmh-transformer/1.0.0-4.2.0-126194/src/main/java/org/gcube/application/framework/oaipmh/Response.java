package org.gcube.application.framework.oaipmh;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.application.framework.oaipmh.constants.ResponseConstants;
import org.gcube.application.framework.oaipmh.exceptions.MissingRequestParameters;
import org.gcube.application.framework.oaipmh.objectmappers.Identifier;
import org.gcube.application.framework.oaipmh.objectmappers.Record;
import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.gcube.application.framework.oaipmh.verbcontainers.ErrorParam;
import org.gcube.application.framework.oaipmh.verbcontainers.GetRecord;
import org.gcube.application.framework.oaipmh.verbcontainers.Identify;
import org.gcube.application.framework.oaipmh.verbcontainers.ListIdentifiers;
import org.gcube.application.framework.oaipmh.verbcontainers.ListMetadataFormats;
import org.gcube.application.framework.oaipmh.verbcontainers.ListRecords;
import org.gcube.application.framework.oaipmh.verbcontainers.ListSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parses the request and forms the appropriate response (entry point class) for all OAI-PMH requests.
 * @author nikolas
 *
 */
public class Response {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Response.class);
	
	private Date responseDate;
	
	private String verb;
	
	private String verbResponse;
	
	
	public String getIdentifyResponse(Properties requestProps, Repository repository) throws MissingRequestParameters, TransformerException{ //verb could be enum, instead of string
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(Identify.formulateIdentifyElement(repository));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	public String getListMetadataFormatsResponse(Properties requestProps, Repository repository) throws MissingRequestParameters, TransformerException{ //
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(ListMetadataFormats.formulateMetadataFormatsElement(repository));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	public String getListSetsResponse(Properties requestProps, Repository repository) throws MissingRequestParameters, TransformerException{ //
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(ListSets.formulateListSetsElement(repository));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	public String getGetRecordResponse(Properties requestProps, Repository repository, Record record) throws MissingRequestParameters, TransformerException{
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(GetRecord.formulateGetRecordElement(record));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	public String getListRecordsResponse(Properties requestProps, Repository repository, ArrayList<Record> records, int cursor, int total) throws MissingRequestParameters, TransformerException{
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(ListRecords.formulateListRecordsElement(records, cursor, total));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	public String getListIdentifiersResponse(Properties requestProps, Repository repository, ArrayList<Identifier> identifiers, int cursor, int total) throws MissingRequestParameters, TransformerException{
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(ListIdentifiers.formulateListIdentifiersElement(identifiers, cursor, total));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	public String getErrorResponse(Properties requestProps, Repository repository, String errorMessage) throws TransformerException, MissingRequestParameters{
		Element oaipmh = getResponseElement(requestProps,repository);
		oaipmh.appendChild(ErrorParam.formulateErrorElement(errorMessage));
		return ElementGenerator.domToXML(oaipmh);
	}
	
	
	/**
	 * Creates the top-level element of all responses. Just append the verb response Element before applying any DOM 2 XML transformation
	 */
	private Element getResponseElement(Properties requestProps, Repository repository) throws MissingRequestParameters{
		//first check if requestProps do have the minimum required mappings  
		if(checkMinimumMappings(requestProps) == false)
			throw new MissingRequestParameters("A required parameter was missing");
		//now start forming the response
		Element oaipmh = ElementGenerator.getDocument().createElement("OAI-PMH");
		for(Object key : ResponseConstants.getHeaderProps().keySet())
			oaipmh.setAttribute((String)key,(String)ResponseConstants.getHeaderProps().get(key));
		//header child elements
		Element respDate = ElementGenerator.getDocument().createElement("responseDate");
		respDate.appendChild(ElementGenerator.getDocument().createTextNode(Toolbox.dateTimeNow()));
		oaipmh.appendChild(respDate);
		
		verb = requestProps.getProperty("verb");
		Element req = ElementGenerator.getDocument().createElement("request");
		req.setAttribute("verb", verb);
		req.appendChild(ElementGenerator.getDocument().createTextNode(repository.getBaseURL()));
		oaipmh.appendChild(req);
		return oaipmh;
	}
	
	
	
	/**
	 * * this function gets the xml-encoded response for the request (header + verb response) 
	 * 
	 * @param requestProps  should countain at least the key "verb"
	 * @return the xml-encoded response
	 * @throws MissingRequestParameters 
	 * @throws TransformerException 
	 */
//	public String getResponse(Properties requestProps, Repository repository) throws MissingRequestParameters, TransformerException{ //verb could be enum, instead of string
//		//first check if requestProps do have the minimum required mappings  
//		if(checkMinimumMappings(requestProps) == false)
//			throw new MissingRequestParameters("A required parameter was missing");
//		//now start forming the response
//		Element oaipmh = ElementGenerator.getDocument().createElement("OAI-PMH");
//		for(Object key : ResponseConstants.getHeaderProps().keySet())
//			oaipmh.setAttribute((String)key,(String)ResponseConstants.getHeaderProps().get(key));
//		//header child elements
//		Element respDate = ElementGenerator.getDocument().createElement("responseDate");
//		respDate.appendChild(ElementGenerator.getDocument().createTextNode(Toolbox.dateTimeNow()));
//		oaipmh.appendChild(respDate);
//		
//		Element req = ElementGenerator.getDocument().createElement("request");
//		req.setAttribute("verb", requestProps.getProperty("verb"));
//		req.appendChild(ElementGenerator.getDocument().createTextNode(repository.getBaseURL()));
//		oaipmh.appendChild(req);
//		
//		verb = requestProps.getProperty("verb");
//		oaipmh.appendChild(getVerbResponseElement(verb, repository));
//		
//		return ElementGenerator.domToXML(oaipmh);
//	}
	/**
	 * this function gets the dom element holding the response from the verb classes 
	 * @return
	 */
	/*
	private Node getVerbResponseElement(String verb, Repository repository){
		if(verb.equalsIgnoreCase("identify"))
			return Identify.formulateIdentifyElement(repository);
		if(verb.equalsIgnoreCase("listmetadataformats"))
			return ListMetadataFormats.formulateMetadataFormatsElement(repository);
		if(verb.equalsIgnoreCase("listsets"))
			return ListSets.formulateListSetsElement(repository);
		if(verb.equalsIgnoreCase("getrecord"))
			return GetRecord.formulateGetRecordElement(record) //.formulateListSetsElement(repository);
		
		
		//in case it's none of the above,
		return null;
	}
	*/
	

	/**
	 * Checks if the requestProps do contain the minimum set of required (key,value) to form the response  
	 * @param requestProps
	 * @return
	 */
	private boolean checkMinimumMappings(Properties requestProps){
		if(!requestProps.containsKey("verb"))
			return false;
		
		//if there's no required parameter missing, return true 
		return true;
	}
	

	
}

