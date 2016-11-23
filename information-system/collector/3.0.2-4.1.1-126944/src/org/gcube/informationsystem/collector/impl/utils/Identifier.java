package org.gcube.informationsystem.collector.impl.utils;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * 
 * Build instance state identifiers
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Identifier {

    /**
     * Builds an instance state identifier
     * @param parser the parser of the source entry
     * @return the identifier
     */
    public static String buildInstanceStateID(EntryParser parser) {		
	return buildInstanceStateID(parser.getSourceURI(),parser.getSourceKey());		
    }

    /**
     * Builds an instance state identifier
     * @param source the URI of the publisher
     * @param id the id related to the piece of state to identify
     * @return the identifier
     */
    public static String buildInstanceStateID(String source, String id) {	
	return source.replace("http://", "").replace(":", "").replace("/", "-") + "-" + id.replace("http://", "").replace(":", "").replace("/", "-");		
    }
    
    /**
     * Builds a profile ID starting from the message (a string serialization of the profile) 
     * @param message the message 
     * @return the ID of the profile
     * @throws Exception if the profile is not well-formed
     */
    public static String buildProfileID(String message) throws Exception {
	Document internalDOM;
	try {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(message);
		InputSource source = new InputSource(reader);
		internalDOM = builder.parse(source);
		XPath path = XPathFactory.newInstance().newXPath();		
		// uses the GCUBEResource ID as local resource ID
		return path.evaluate("/Resource/ID", internalDOM);
	    } catch (Exception e) {		
		throw new Exception("Unable to extract the ID from the resource " + e.getMessage());
	    } finally {
		internalDOM = null;
	    }
    }
}
