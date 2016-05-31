package org.gcube.textextractor.helpers;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathHelper {


	public static String getMultiValuesXPath(XPath xpath, Document doc, String xpathStr) throws XPathExpressionException {
		return getMultiValuesXPath(xpath, doc, xpathStr, null);
	}
	public static String getMultiValuesXPath(XPath xpath, Document doc, String xpathStr, String attr) throws XPathExpressionException {
		StringBuffer strBuf = new StringBuffer();
		
		NodeList nl = (NodeList) xpath.compile(xpathStr).evaluate(doc, XPathConstants.NODESET);
		for (int i = 0 ; i < nl.getLength() ; i++){
			
			if (i > 0)
				strBuf.append(", ");
			
			if (attr == null)
				strBuf.append(nl.item(i).getTextContent());
			else
				strBuf.append(nl.item(i).getAttributes().getNamedItem(attr).getTextContent());
			
		}
		return strBuf.toString();
	}
	
	public static String getValueXPath(XPath xpath, Document doc, String xpathStr) throws XPathExpressionException {
		Node node = (Node) xpath.compile(xpathStr).evaluate(doc, XPathConstants.NODE);
		return node.getTextContent();
	}
	
	public static Boolean checkNodeExists(XPath xpath, Document doc, String xpathStr) throws XPathExpressionException {
		Node node = (Node) xpath.compile(xpathStr).evaluate(doc, XPathConstants.NODE);
		return node != null;
	}
	
	public static String getStringAttribute(XPath xpath, Document doc, String xpathStr) throws XPathExpressionException {
		String value = (String) xpath.compile(xpathStr).evaluate(doc, XPathConstants.STRING);
		return value;
	}
}
