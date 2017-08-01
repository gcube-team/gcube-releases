package gr.cite.geoanalytics.ows.client;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathUtils {

	private static Logger logger = LoggerFactory.getLogger(XPathUtils.class);

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;
	private XPath xPath;

	public XPathUtils(String xml) throws Exception {
		try {
			this.factory = DocumentBuilderFactory.newInstance();
			this.builder = factory.newDocumentBuilder();
			this.document = this.builder.parse(new InputSource(new StringReader(xml)));
			this.xPath = XPathFactory.newInstance().newXPath();
		} catch (Exception e) {
			throw new Exception("Failed to initialize DocumentBuilder", e);
		}
	}

	public Node evaluateNode(String expression) {
		return (Node) evaluate(null, expression, XPathConstants.NODE);
	}

	public String evaluateString(String expression) {
		return (String) evaluate(null, expression, XPathConstants.STRING);
	}

	public Double evaluateNumber(String expression) {
		return (Double) evaluate(null, expression, XPathConstants.NUMBER);
	}

	public Boolean evaluateBoolean(String expression) {
		return (Boolean) evaluate(null, expression, XPathConstants.BOOLEAN);
	}

	public NodeList evaluateNodeSet(String expression) {
		return (NodeList) evaluate(null, expression, XPathConstants.NODESET);
	}

	public Node evaluateNodeOf(Object rootElement, String expression) {
		return (Node) evaluate(rootElement, expression, XPathConstants.NODE);
	}

	public String evaluateStringOf(Object rootElement, String expression) {
		return (String) evaluate(rootElement, expression, XPathConstants.STRING);
	}

	public Double evaluateNumberOf(Object rootElement, String expression) {
		return (Double) evaluate(rootElement, expression, XPathConstants.NUMBER);
	}

	public Boolean evaluateBooleanOf(Object rootElement, String expression) {
		return (Boolean) evaluate(rootElement, expression, XPathConstants.BOOLEAN);
	}

	public NodeList evaluateNodeSetOf(Object rootElement, String expression) {
		return (NodeList) evaluate(rootElement, expression, XPathConstants.NODESET);
	}

	public Object evaluate(Object rootElement, String expression, QName type) {
		Object value = null;

		if (rootElement == null) {
			rootElement = this.document;
		}

		try {
			value = xPath.compile(expression).evaluate(rootElement, type);
		} catch (XPathExpressionException e) {
			logger.error("Cannot evaluate String of " + expression, e);
		}

		return value;
	}
}
