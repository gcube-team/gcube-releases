/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: XMLUtil.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.shared.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.resourcemanagement.support.shared.types.datamodel.AtomicTreeNode;



import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */

public class XMLUtil {

	public static AtomicTreeNode XMLToTree(final String xml, final String rootName)
	throws Exception {
		AtomicTreeNode root = null;
		Document doc = XMLParser.parse(xml);
		String rootTag = rootName;
		root = elementToNode((com.google.gwt.xml.client.Element) doc.getElementsByTagName(rootTag).item(0));
		return root;
	}

	private static AtomicTreeNode elementToNode(final com.google.gwt.xml.client.Element el) throws Exception {
		if (el == null) {
			throw new Exception("Invalid Profile");
		}
		StringBuffer value = new StringBuffer(XMLUtil.getTextContent(el).trim());

		if (el.hasAttributes()) {
			NamedNodeMap attr = el.getAttributes();
			String attrName = null;
			String attrVal = null;
			for (int i = 0; i < attr.getLength(); i++) {
				attrName = attr.item(i).getNodeName();
				attrVal = attr.getNamedItem(attrName).getNodeValue();
				value.append(attrName + " = " + attrVal + "; ");
			}
		} else {
			if (value != null && value.length() != 0) {
				value = new StringBuffer("value = " + value);
			}
		}

		String tagName = el.getTagName() + ((value.length() != 0) ? " [" + value + "]" : "");
		AtomicTreeNode node = new AtomicTreeNode(tagName);
		Iterator<com.google.gwt.xml.client.Element> childrenIterator = XMLUtil.getElementChildren(el).iterator();
		while (childrenIterator.hasNext()) {
			node.add(elementToNode(childrenIterator.next()));
		}
		return node;
	}


	public static Element getFirstElementChild(final Element parent) {
		if (parent == null) {
			return null;
		}

		Node current = parent.getFirstChild();
		while (current != null) {
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) current;
				return elem;
			}
			current = current.getNextSibling();
		}
		return null;
	}


	public static Element getFirstElementChild(final Element parent, final String tagName) {
		if (parent == null || tagName == null) {
			return null;
		}

		Node current = parent.getFirstChild();
		while (current != null) {
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) current;
				if (tagName.equals(elem.getTagName())) {
					return elem;
				}
			}
			current = current.getNextSibling();
		}
		return null;
	}

	public static List<com.google.gwt.xml.client.Element> getElementChildren(final Element parent) {
		ArrayList<Element> result = new ArrayList<Element>();
		if (parent == null) {
			return result;
		}

		Node current = parent.getFirstChild();
		while (current != null) {
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) current;
				result.add(elem);
			}
			current = current.getNextSibling();
		}
		return result;
	}


	public static List<Element> getElementChildren(final Element parent, final String tagName) {
		ArrayList<Element> result = new ArrayList<Element>();
		if (parent == null || tagName == null) {
			return result;
		}
		Node current = parent.getFirstChild();
		while (current != null) {
			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) current;
				if (tagName.equals(elem.getTagName())) {
					result.add(elem);
				}
			}
			current = current.getNextSibling();
		}
		return result;
	}


	public static String getTextContent(final Element parent) {
		StringBuffer result = new StringBuffer();
		if (parent == null) {
			return result.toString();
		}
		Node current = parent.getFirstChild();
		while (current != null) {
			if (current.getNodeType() == Node.TEXT_NODE) {
				result.append(current.getNodeValue());
			}
			current = current.getNextSibling();

		}

		return result.toString();
	}

	public static String getEntireContent(final Node parent) {
		StringBuilder result = new StringBuilder();
		if (parent != null) {
			NodeList children = parent.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				result.append(node.toString());
				//if(node.hasChildNodes()) result.append(getEntireContent(node));
			}
		}
		return result.toString();
	}

}


