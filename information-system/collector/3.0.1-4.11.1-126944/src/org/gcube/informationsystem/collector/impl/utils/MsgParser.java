package org.gcube.informationsystem.collector.impl.utils;

import org.apache.axis.encoding.AnyContentType;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.utils.MsgParser;
import org.globus.wsrf.utils.AnyHelper;

import org.w3c.dom.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import java.io.StringWriter;
import java.io.StringReader;
import java.lang.Exception;

/**
 * 
 * 
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class MsgParser {

    private Document internalDOM;

    private String originalMsgString;

    private String type; // the entry type (profile or generic resource
			 // properties)

    private XPath path = XPathFactory.newInstance().newXPath(); // object to
								// evaluate
								// Xpath
								// expressions

    private final String rootElement = "Data";
    
    private final String typeElement = "Type";

    private static GCUBELog logger = new GCUBELog(MsgParser.class);

    public MsgParser(AnyContentType deliveredMsg) throws Exception {

	try {
	    this.originalMsgString = AnyHelper.toSingleString(deliveredMsg);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    StringReader reader = new StringReader("<" + rootElement + ">"
		    + this.originalMsgString + "</" + rootElement + ">");
	    InputSource source = new InputSource(reader);
	    this.internalDOM = builder.parse(source);
	    // check if the message has been sent in push or pull mode
	    this.checkMsgType();
	    // gets the entry type
	    this.type = path.evaluate("//" + rootElement
		    + "/child::*[local-name() = '" + typeElement + "']", internalDOM);
	} catch (ParserConfigurationException pce) {
	    logger.error("", pce);
	    throw new Exception("Parser configuration error");
	} catch (SAXParseException spe) {
	    logger.error("", spe);
	    throw new Exception(" SAX parser error");
	} catch (XPathExpressionException xpee) {
	    logger.error("", xpee);
	    throw new Exception("XPath evaluation error");
	}
    }

    /**
     * Returns the entry type. It can be "Properties" or "Profile"
     * 
     * @return the entry type
     */
    public String getEntryType() {
	return this.type;
    }
    
    /**
     * Returns the Scope Name included in the entry
     * 
     * @throws Exception
     * 
     * @return the Scope name
     */
    public String getScopeName() throws Exception {

	return this.getGCUBEProperty("Scope");
    }

    /**
     * Returns the Service Name included in the entry
     * 
     * @throws Exception
     * 
     * @return the service name
     */
    public String getServiceName() throws Exception {

	return this.getGCUBEProperty("ServiceName");
    }

    /**
     * Returns the Service Class included in the entry
     * 
     * @throws Exception
     * 
     * @return the service class
     */
    public String getServiceClass() throws Exception {

	return this.getGCUBEProperty("ServiceClass");
    }

    /**
     * Returns the Running Instance ID included in the entry
     * 
     * @throws Exception
     * 
     * @return the ID
     */
    public String getRunningInstanceID() throws Exception {

	return this.getGCUBEProperty("RunningInstanceID");
    }

    /**
     * Returns the Service ID included in the entry
     * 
     * @throws Exception
     * 
     * @return the service ID
     */
    public String getServiceID() throws Exception {

	return this.getGCUBEProperty("ServiceID");
    }

    /**
     * Returns the DHN ID included in the entry
     * 
     * @throws Exception
     * 
     * @return the DHN ID
     */
    public String getGHNID() throws Exception {

	return this.getGCUBEProperty("GHN");
    }

    /**
     * 
     * @return the profile, it any
     * @throws Exception
     */
    public String getProfile() throws Exception {
	try {
	    TransformerFactory transFactory = TransformerFactory.newInstance();
	    Transformer transformer = transFactory.newTransformer();
	    StringWriter buffer = new StringWriter();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    transformer.transform(new DOMSource(this.internalDOM.getElementsByTagName("Resource").item(0)), new StreamResult(buffer));
	    return buffer.toString();
	} catch (Exception e) {
	    throw new Exception("Unable to deserialise content data");	    
	} 

    }

    
    /**
     * Reads a GCUBE Property
     * 
     * @return the property value
     * @throws Exception if the reading fails
     * 
     */
    private String getGCUBEProperty(String propName) throws Exception {

	try {
	    return path.evaluate("//" + rootElement
		    + "/child::*[local-name() = '" + propName + "']",
		    internalDOM);
	} catch (XPathExpressionException xpee) {
	    logger.error("", xpee);
	    throw new Exception("XPath evaluation error");
	}
    }

    /**
     * Returns a XML representation of the Entry
     * 
     * @return the XML string
     */
    public String getEntryAsString() {
	return this.originalMsgString ;
    }

    /**
     * Releases the allocated resources
     * 
     */

    public void dispose() {
	// remove the allocated DOM
	internalDOM = null;
    }

    /**
     * Checks if the message has been sent by a WS-Notification In that case,
     * the NewValue is extracted by the msg
     * 
     */
    private void checkMsgType() throws Exception {

	Boolean isPush = false;
	String notificationXpath = "/child::*[local-name() = 'value']";
	notificationXpath += "/child::*[local-name() = 'ResourcePropertyValueChangeNotification']";
	notificationXpath += "/child::*[local-name() = 'NewValue']";

	try {
	    isPush = (Boolean) path.evaluate("//" + rootElement + notificationXpath, this.internalDOM,
		    XPathConstants.BOOLEAN);
	} catch (NullPointerException n) {
	    logger.trace("The delivered message has been sent using the pull mode");
	    return;
	} catch (XPathExpressionException xpee) {
	    logger.error("", xpee);
	}

	if (isPush) {
	    // replace the originalMsgString and the internalDOM with the
	    // newValue section of the delivered message
	    logger.trace("The delivered message has been sent using the push mode");
	    // String nodeString = (String) path.evaluate(notificationXpath,
	    // this.internalDOM, XPathConstants.STRING);

	    Node node = (Node) path.evaluate("//" + rootElement
		    + notificationXpath, this.internalDOM, XPathConstants.NODE);

	    Transformer serializer = TransformerFactory.newInstance().newTransformer();
	    StringWriter sw = new StringWriter();

	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder domBuilder = factory.newDocumentBuilder();

	    // create the nre data string
	    NodeList children = node.getChildNodes();
	    this.originalMsgString = "";
	    if (children != null) {
		for (int i = 0; i < children.getLength(); i++) {
		    serializer.transform(new DOMSource(children.item(i)),
			    new StreamResult(sw));
		    this.originalMsgString += sw.toString();
		}
	    }
	    logger.trace("Data string " + this.originalMsgString);
	    // trim the directive <?xml... ?>
	    this.originalMsgString = this.originalMsgString.substring(this.originalMsgString.indexOf("?>", 1) + 1);
	    logger.trace("Trimmed data string " + this.originalMsgString);
	    // create the new internalDOM
	    StringReader reader = new StringReader("<" + rootElement + ">"
		    + this.originalMsgString + "</" + rootElement + ">");
	    InputSource source = new InputSource(reader);
	    this.internalDOM = domBuilder.parse(source);
	} else {
	    logger.info("The delivered message has been sent using the pull mode");
	}
    }

}
