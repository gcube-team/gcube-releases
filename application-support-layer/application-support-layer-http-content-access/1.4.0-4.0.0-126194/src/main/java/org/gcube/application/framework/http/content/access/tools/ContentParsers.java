package org.gcube.application.framework.http.content.access.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ContentParsers {

	private static final Logger logger = LoggerFactory.getLogger(ContentParsers.class);
	
	
	public static TreeMap<String, List<String>> parseOAI_Payload (String payload){
		TreeMap<String, List<String>> contentURLs = new TreeMap<String, List<String>>();
		List<String> mainURLs = new ArrayList<String>();
		List<String> altURLs = new ArrayList<String>();
		try {
			Document doc = parseXMLFileToDOM(payload);
			NodeList list = doc.getElementsByTagName("content");
			if (list != null) {
				for(int i=0; i < list.getLength(); i++ ) {
					Element contentNode = (Element)list.item(i);
					String contentType = "";
					String contentURL = "";
					NodeList ctList = contentNode.getElementsByTagName("contentType");
					if(ctList != null && ctList.getLength() > 0) {
						Element el = (Element)ctList.item(0);
						contentType = el.getFirstChild().getNodeValue();
					}
					NodeList urlList = contentNode.getElementsByTagName("url");
					if(urlList != null && urlList.getLength() > 0) {
						Element el = (Element)urlList.item(0);
						contentURL = el.getFirstChild().getNodeValue();
					}
					logger.debug("CONTENT URL: TYPE --- URL -> " + contentType + " --- " + contentURL);
					if (contentType.equalsIgnoreCase("main"))
						mainURLs.add(contentURL);
					else
						altURLs.add(contentURL);
				}
				contentURLs.put(ContentConstants.MAIN_URLs, mainURLs);
				contentURLs.put(ContentConstants.ALTERNATIVE_URLs, altURLs);
			}
			else
				return null;
		} catch (Exception e) {
			return null;
		}
		return contentURLs;
	}
	
	public static TreeMap<String, List<String>> parseFIGIS_Payload(String payload) {
		TreeMap<String, List<String>> contentURLs = new TreeMap<String, List<String>>();
		List<String> mainURLs = new ArrayList<String>();
		try {
			Document doc = parseXMLFileToDOM(payload);
			NodeList list = doc.getElementsByTagName("factsheet_url");
			if (list != null) {
				for(int i=0; i < list.getLength(); i++ ) {
					Element el = (Element)list.item(i);
					String contentURL = el.getFirstChild().getNodeValue();
					mainURLs.add(contentURL);
				}
				contentURLs.put(ContentConstants.MAIN_URLs, mainURLs);
			}
			else
				return null;
		} catch (Exception e) {
			return null;
		}
		return contentURLs;
	}
	
	public static Document parseXMLFileToDOM(String XMLdoc) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(XMLdoc)));
		return doc;
	}
}
