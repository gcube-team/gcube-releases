/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class Resource {

	public static final String BASE_NS = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';\ndeclare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';\n";

	static final String QUERY_CONDITION_PLACEHOLDER = " CONDITION ";

	/** Logger */
	private static Logger logger = Logger.getLogger(Resource.class);

	/** The scope of this resource */
	private String scope;

	private String resourceTypeName;
	private String baseISQuery;
	private HashMap<String, List<String>> attributes;
	private List<String> attrNames;

	/**
	 * Class constructor
	 * @param resourceData the resource data that this object will wrap
	 */
	protected Resource(String scope, String resourceTypeName, String baseISQuery) {
		this.scope = scope;
		this.resourceTypeName = resourceTypeName;
		this.baseISQuery = baseISQuery;
		this.attributes = new HashMap<String, List<String>>();
		this.attrNames = new LinkedList<String>();
	}

	/**
	 * Returns the scope of this resource
	 * @return the resource's scope
	 */
	public String getScope() {
		return this.scope;
	}

	public String getResourceTypeName() {
		return resourceTypeName;
	}

	String getBaseISQuery() {
		return baseISQuery;
	}

	protected void setBaseISQuery(String query) {
		this.baseISQuery = query;
	}

	protected HashMap<String, List<String>> getAttributeMap() {
		return this.attributes;
	}

	public List<String> getAttributeNames() {
		return this.attrNames;
	}

	protected void addAttributeName(String attrName) {
		this.attrNames.add(attrName);
	}

	public List<String> getAttributeValue(String attrName) {
		return this.attributes.get(attrName);
	}

	public void setAttributeValue(String attrName, List<String> attrValues) {
		if (attrNames.contains(attrName))
			this.attributes.put(attrName, attrValues);
	}

	public void setAttributeValue(String attrName, String attrValue) {
		if (attrNames.contains(attrName)) {
			List<String> valList = EntityParsingUtil.attrValueToArrayOfValues(attrValue); //new ArrayList<String>(1);
			//valList.add(attrValue);
			this.attributes.put(attrName, valList);
		}

	}

	static List<String> evaluateExpression(String xml, String expression) {
		//try {
		if (xml != null && !xml.isEmpty()) {
			try {
				logger.debug("!!!XML to evaluate expression --> " + xml);
				logger.debug("!!!Expression to evaluate --> " + expression);

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = dbFactory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(xml)));
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
				if (nList != null && nList.getLength() > 0) {
					List<String> result = new ArrayList<String>();
					for (int i=0; i<nList.getLength(); i++) {
						String value = nList.item(i).getTextContent();
						//logger.debug("Adding -> " + value + " to the returned list");
						result.add(value);
					}
					return result;
				}
			} catch (XPathExpressionException e) {
				logger.error("Failed to evaluate the given expression for the given document", e);
			} catch (DOMException e) {
				logger.error("Failed to evaluate the given expression for the given document", e);
			} catch (ParserConfigurationException e) {
				logger.error("Failed to evaluate the given expression for the given document", e);
			} catch (SAXException e) {
				logger.error("Failed to evaluate the given expression for the given document", e);
			} catch (IOException e) {
				logger.error("Failed to evaluate the given expression for the given document", e);
			}
		}
		//		} catch (Exception e) {
		//			logger.error("Failed to evaluate the given expression for the given document", e);
		//		}
		else
			logger.info("XML to evaluate is null or empty. Avoiding evaluation");
		return null;
	}

	abstract void fromXML(String xmlResult);
}
