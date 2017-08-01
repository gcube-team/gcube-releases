package org.gcube.rest.commons.helpers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathEvaluator {
	private Node node;
	private XPath xpath;

	public XPathEvaluator(Node node) {
		this.node = node;
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
	}

	public List<String> evaluate(String expression) {
		List<String> list = new ArrayList<String>(); 
		try {
			NodeList nodeList = (NodeList) xpath.evaluate(expression, node, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() == 2 || nodeList.item(i).getNodeType() == 3) {
					list.add(nodeList.item(i).getTextContent());
				} else
					list.add(XMLConverter.nodeToString(nodeList.item(i)));
			}
		} catch (XPathExpressionException e) {
			try {
				Double number = (Double) xpath.evaluate(expression, node, XPathConstants.NUMBER);
				list.add(String.valueOf(number.intValue()));
			} catch (XPathExpressionException e1) {
				return null;
			}
		}
		return list;
	}

	public Node getNode() {
		return node;
	}

	public XPath getXpath() {
		return xpath;
	}
}
