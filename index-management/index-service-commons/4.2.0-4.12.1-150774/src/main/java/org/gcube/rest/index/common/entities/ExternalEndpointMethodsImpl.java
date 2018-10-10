package org.gcube.rest.index.common.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.rest.index.common.entities.ExternalEndpointInfo.ExternalXmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ExternalEndpointMethodsImpl implements ExternalEndpointMethods {
	
	private static final Logger logger = LoggerFactory.getLogger(ExternalEndpointMethodsImpl.class);

	@Override
	public ExternalEndpointResponse sendGet(String url) throws ExternalEndpointException {
		
		URL obj = null;
		HttpURLConnection con = null;
		ExternalEndpointResponse extResponse = new ExternalEndpointResponse();
		try {
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();
		} catch (MalformedURLException e) {
			logger.error("Malformed Description Url: "+url, e);
			throw new ExternalEndpointException("Malformed Description Url: "+url, e);
		} catch (IOException e) {
			logger.error("Could not open connection to Url: "+url, e);
			throw new ExternalEndpointException("Could not open connection to Url: "+url, e);
		}

		try {
			con.setRequestMethod("GET");
		} catch (ProtocolException e) {
			logger.error("Could not set request method to 'GET'", e);
			throw new ExternalEndpointException("Could not set request method to 'GET'", e);
		}

		try {
			extResponse.setResponseCode(con.getResponseCode());
		} catch (IOException e) {
			logger.error("Problem with response code", e);
			throw new ExternalEndpointException("Problem with response code", e);
		}
		logger.info("\nSending 'GET' request to URL : " + url);
		logger.info("Response Code : " + extResponse.getResponseCode());

		if(extResponse.getResponseCode() == 200) {
			
		
			BufferedReader in = null;
			String inputLine;
			StringBuffer response = new StringBuffer();
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				extResponse.setXml(response.toString());
				in.close();
			} catch (IOException e) {
				logger.error("Problem while getting response", e);
				throw new ExternalEndpointException("Problem while getting response", e);
			}
		}
		
		return extResponse;
	}

	@Override
	public Document loadXMLFromString(String xml) throws ExternalEndpointException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = null;
	    Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
		
		    InputSource is = new InputSource(new StringReader(xml));
			doc = builder.parse(is);
		} catch (SAXException e) {
			logger.error("Error while parsing xml", e);
			throw new ExternalEndpointException("Error while parsing xml", e);
		} catch (IOException e) {
			logger.error("Error while reading xml", e);
			throw new ExternalEndpointException("Error while parsing xml", e);
		} catch (ParserConfigurationException e) {
			logger.error("Error while parsing configuration", e);
			throw new ExternalEndpointException("Error while parsing configuration", e);
		}
	    doc.getDocumentElement().normalize();
	    return doc;
	}

	@Override
	public ExternalEndpointInfo fillExternalEnpointInfo(Document doc, String descriptionUrl) {
		
		String urlTemplate = null;
		String searchBaseUrl = null;
		String searchTerm = null;
		ExternalEndpointInfo extInfo = new ExternalEndpointInfo();
		
		if(!"OpenSearchDescription".equals(doc.getDocumentElement().getNodeName())) {
			logger.error("Malformed opensearch description document");
			return null;
		}
	
		NodeList nList = doc.getElementsByTagName("Url");
		boolean rssOrAtomFound = false;
		//Searching for rss or atom url type. Prefer rss
        for (int temp = 0; temp < nList.getLength(); temp++) {
           Node nNode = nList.item(temp);
          
           if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
              
              if(eElement.getAttribute("type").contains("rss")) {
            	  extInfo.setSearchType(ExternalXmlType.RSS);
            	  urlTemplate = eElement.getAttribute("template");
            	  String[] urlTemplateSplitted = urlTemplate.split("\\?");
            	  
            	  searchBaseUrl = urlTemplateSplitted[0];
            	  searchTerm = parseExternalUrlQuery(urlTemplateSplitted[1]);
            	  rssOrAtomFound = true;
            	  break;
              }else if(eElement.getAttribute("type").contains("atom")){
            	  extInfo.setSearchType(ExternalXmlType.ATOM);
            	  urlTemplate = eElement.getAttribute("template");
            	  String[] urlTemplateSplitted = urlTemplate.split("\\?");
            	  
            	  searchBaseUrl = urlTemplateSplitted[0];
            	  searchTerm = parseExternalUrlQuery(urlTemplateSplitted[1]);
            	  rssOrAtomFound = true;
              }
          
           }
        }
		
        if(rssOrAtomFound){
			extInfo.setDescriptionBaseUrl(descriptionUrl);
			extInfo.setSearchBaseUrl(searchBaseUrl);
			extInfo.setSearchTerm(searchTerm);
			return extInfo;
        }
		
		return null;
	}
	
	private String parseExternalUrlQuery(String urlQuery) {
   		
         String[] splittedQuery = urlQuery.split("&");
         
         for(String query : splittedQuery) {
         	if(query.contains("searchTerms")) {
         		return query.split("=")[0];
         	}
         }
         
         return null;
	}

}
