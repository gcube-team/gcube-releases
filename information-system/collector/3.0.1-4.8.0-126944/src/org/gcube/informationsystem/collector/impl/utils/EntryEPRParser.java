package org.gcube.informationsystem.collector.impl.utils;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.utils.EntryEPRParser;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.message.MessageElement;

import org.w3c.dom.*;

import org.xml.sax.InputSource;

import javax.xml.parsers.*;

import java.io.StringReader;
import java.lang.Exception;

/**
 * 
 * Parser for EntryEPR of a WS-ServiceGroup resources
 * 
 * 
 */

public class EntryEPRParser {

    private Document internalDOM;

    private Element serviceGroup;

    // xpath factory to evaluate Xpath expressions
    private XPath path = XPathFactory.newInstance().newXPath();

    private static GCUBELog logger = new GCUBELog(EntryEPRParser.class);

    /**
     * 
     * @param e
     *            the EPR of the service group resource to parse
     * @throws Exception
     *             if the input EPR is no valid
     */
    public EntryEPRParser(EndpointReferenceType e) throws Exception {

	ReferencePropertiesType prop = e.getProperties();
	MessageElement[] any = prop.get_any();

	if (any[0].getName().equalsIgnoreCase("ServiceGroupEntryKey")) {
	    serviceGroup = any[0];
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    StringReader reader = new StringReader(serviceGroup.toString());
	    InputSource source = new InputSource(reader);
	    this.internalDOM = builder.parse(source);
	}
    }

    /**
     * 
     * @return the service group's entry key
     * @throws Exception
     *             if the parsing of the EPR fails
     */
    public String getEntryKey() throws Exception {

	String key = null;
	try {
	    key = path.evaluate("ServiceGroupEntryKey/EntryKey", internalDOM);
	} catch (XPathExpressionException xpee) {
	    logger.error(xpee.getMessage());
	    logger.error(xpee.getStackTrace());
	    throw new Exception("XPath evaluation error");
	}
	return key;
    }

    /**
     * 
     * @return the service group's group key
     * @throws Exception
     *             if the parsing of the EPR fails
     */
    public String getGroupKey() throws Exception {

	String key = null;
	try {
	    key = path.evaluate("ServiceGroupEntryKey/GroupKey", internalDOM);
	} catch (XPathExpressionException xpee) {
	    logger.error(xpee.getMessage());
	    logger.error(xpee.getStackTrace());
	    throw new Exception("XPath evaluation error");
	}
	return key;
    }

}
