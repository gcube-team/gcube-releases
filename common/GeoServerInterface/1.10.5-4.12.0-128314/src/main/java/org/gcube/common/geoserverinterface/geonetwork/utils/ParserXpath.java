package org.gcube.common.geoserverinterface.geonetwork.utils;


import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.geoserverinterface.geonetwork.csw.NamespaceCswResolver;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ParserXpath {
	
	public static ArrayList<String> getTextFromXPathExpression(String xml, String xpathExpression){
		
		ArrayList<String> list = new ArrayList<String>();
		try {

			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new NamespaceCswResolver());
			XPathExpression xPathExpression = xpath.compile(xpathExpression);
			InputSource inputSource = new InputSource(InputStreamUtil.stringToInputStream(xml));
			
//			System.out.println(xml);
//			System.out.println(xpathExpression);
//			System.out.println(inputSource.toString());
			
			NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);

			for (int i = 0; i<nodes.getLength(); i++) {
				Node node = nodes.item(i);
				list.add(node.getTextContent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
