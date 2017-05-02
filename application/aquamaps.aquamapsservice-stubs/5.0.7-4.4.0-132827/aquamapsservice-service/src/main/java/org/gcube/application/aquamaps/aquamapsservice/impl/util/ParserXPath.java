package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ParserXPath {
	final static Logger logger= LoggerFactory.getLogger(ParserXPath.class);


	public static ArrayList<String> getTextFromXPathExpression(String xml, String xpathExpression)throws Exception{
		try {
			ArrayList<String> list = new ArrayList<String>();

			XPath xpath = XPathFactory.newInstance().newXPath();			
			XPathExpression xPathExpression = xpath.compile(xpathExpression);
			InputSource inputSource = new InputSource(stringToInputStream(xml));

			//			System.out.println(xml);
			//			System.out.println(xpathExpression);
			//			System.out.println(inputSource.toString());

			NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);

			for (int i = 0; i<nodes.getLength(); i++) {
				Node node = nodes.item(i);
				list.add(node.getTextContent());
			}

			return list;
		} catch (Exception e) {
			logger.warn("Unexpected exception while getting "+xpathExpression,e);
			throw e;
		}

	}

	public static InputStream stringToInputStream(String text) throws UnsupportedEncodingException{
		/*
		 * Convert String to InputStream using ByteArrayInputStream 
		 * class. This class constructor takes the string byte array 
		 * which can be done by calling the getBytes() method.
		 */
		try {
			return new ByteArrayInputStream(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.warn("",e);
			throw e;
		}
	}
}
