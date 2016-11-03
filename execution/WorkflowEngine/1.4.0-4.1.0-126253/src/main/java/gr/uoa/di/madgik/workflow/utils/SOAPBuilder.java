package gr.uoa.di.madgik.workflow.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPBuilder {

	public static final String TO = "To";
	public static final String SCOPE = "scope";
	private static final String SERVICECLASS = "serviceClass";
	private static final String OPERATION = "operation";
	private static final String RESOURCEKEY = "ResourceKey";
	private static final String SERVICENAME = "serviceName";
	private static final String MESSAGEID = "MessageID";
	private static final String ACTION = "Action";
	private static final String FROM = "From";
	private static final String OUTPUTLOCATOREXTRACTIONEXPRESSION = "OutputLocatorExtractionExpression";
	
	private String wsdl = "";
	private String factory = "";
	private String bindings = "";
	private Document wsdlXML;
	private Document bindingsXML;
	private Document flattenedXML;
	private String operation;
	private Map<String, String> arguments;
	private String service;
//	private String inputNamespace;

	private static Logger logger = LoggerFactory.getLogger(SOAPBuilder.class);
	
	private class Node {
		private String type;
		private String name;
		private List<Node> list;

		public Node() {
			list = new ArrayList<SOAPBuilder.Node>();
		}

		public String toString() {
			String string = "";
			if (!type.equals("complex")) {
				string += (name + " " + type) + '\n';
			} else {
				string += (name) + '\n';
				for (Node node : list) {
					string += node.toString();
				}
			}
			return string;
		}
	}

	public SOAPBuilder(Map<String, String> arguments, String... inputs) {
		this.arguments = arguments;
		if (this.arguments.containsKey(SOAPBuilder.TO))
			this.service = this.arguments.get(SOAPBuilder.TO) + "?wsdl";
		else
			throw new IllegalArgumentException("No To argument specified");
		if (this.arguments.containsKey(SOAPBuilder.OPERATION))
			this.operation = this.arguments.get(SOAPBuilder.OPERATION);
		else
			throw new IllegalArgumentException("No Operation argument specified");
		this.readWSDLFiles(service);
	}

	public String getWSDL() {
		return this.wsdl;
	}

	public String getFactory() {
		return this.factory;
	}

	public String getBidnings() {
		return this.bindings;
	}

	private void readWSDLFiles(String url) {
		try {
			URL wsdl_url = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(wsdl_url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				this.wsdl += inputLine;
			in.close();
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = f.newDocumentBuilder();
			wsdlXML = builder.parse(new InputSource(new StringReader(this.wsdl)));
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='import']/@location");
			String bindings = (String) expr.evaluate(wsdlXML, XPathConstants.STRING);
			URL bindings_url = new URL(bindings);
			in = new BufferedReader(new InputStreamReader(bindings_url.openStream()));
			while ((inputLine = in.readLine()) != null)
				this.bindings += inputLine;
			in.close();
			bindingsXML = builder.parse(new InputSource(new StringReader(this.bindings)));
			xPathfactory = XPathFactory.newInstance();
			xpath = xPathfactory.newXPath();
			expr = xpath.compile("//*[local-name()='import']/@location");
			String factory = bindings.substring(0, bindings.lastIndexOf('/') + 1) + (String) expr.evaluate(bindingsXML, XPathConstants.STRING);
			URL factory_url = new URL(factory);
			in = new BufferedReader(new InputStreamReader(factory_url.openStream()));
			while ((inputLine = in.readLine()) != null)
				this.factory += inputLine;
			in.close();
			flattenedXML = builder.parse(new InputSource(new StringReader(this.factory)));
		} catch (IOException e) {
			logger.error("Exception",e);
		} catch (ParserConfigurationException e) {
			logger.error("Exception",e);
		} catch (SAXException e) {
			logger.error("Exception",e);
		} catch (XPathExpressionException e) {
			logger.error("Exception",e);
		}
//		this.inputNamespace = this.getServiceNamespace();
	}

	// not working, service name not in wsdl
//	private String getServiceName() {
//		try {
//			XPathFactory xPathfactory = XPathFactory.newInstance();
//			XPath xpath = xPathfactory.newXPath();
//			XPathExpression expr = xpath.compile("//*[local-name()='definitions']/@name");
//			return (String) expr.evaluate(flattenedXML, XPathConstants.STRING);
//		} catch (XPathExpressionException e) {
//		}
//		return null;
//	}

	public String getAction() {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='operation' and @name='" + this.operation + "']//@soapAction");
			return (String) expr.evaluate(bindingsXML, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}

	private String getServiceNamespace() {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/definitions/types/*[local-name()='schema']/@targetNamespace");
			return (String) expr.evaluate(this.flattenedXML, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}
	
	private String getResourceServiceNamespace() {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/definitions/@targetNamespace");
			return (String) expr.evaluate(this.wsdlXML, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private String getServiceNamespace(Document doc) {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/definitions/types/*[local-name()='schema']/@targetNamespace");
			return (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}

	public String getOutputLocatorExtractionExpression() {
		return this.arguments.get(SOAPBuilder.OUTPUTLOCATOREXTRACTIONEXPRESSION);
	}

	@SuppressWarnings("unused")
	private Node getInput() {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='operation' and @name='" + this.operation + "']/input/@message");
			String result = (String) expr.evaluate(flattenedXML, XPathConstants.STRING);

			String namespace = getNamespace(result.split(":")[0]);
			if(namespace.equals(this.getServiceNamespace()))
				return getMessageElement(result.split(":")[1]);
			else
			{
				return getMessageElement(result.split(":")[1], namespace);
			}
				
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}

	private Node getMessageElement(String operationName) {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='message' and @name='" + operationName + "']/part/@element");
			String result = (String) expr.evaluate(flattenedXML, XPathConstants.STRING);
			return getElement(result.split(":")[1]);
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}
	
	private Node getMessageElement(String operationName, String namespace) {
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = f.newDocumentBuilder();
			URL namespace_url = new URL(namespace);
			BufferedReader in = new BufferedReader(new InputStreamReader(namespace_url.openStream()));
			String inputLine, namespace_wsdl="";
			while ((inputLine = in.readLine()) != null)
				namespace_wsdl += inputLine;
			in.close();
			Document namespaceXML = builder.parse(new InputSource(new StringReader(namespace_wsdl)));
			
//			this.inputNamespace = getServiceNamespace(namespaceXML);
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='message' and @name='" + operationName + "']/part/@element");
			String result = (String) expr.evaluate(namespaceXML, XPathConstants.STRING);
			return getElement(result.split(":")[1]);
		} catch (XPathExpressionException e) {
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Node getElement(String operationName) {
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='element' and @name='" + operationName + "']/@type");
			String result = (String) expr.evaluate(flattenedXML, XPathConstants.STRING);
			return createInputNode(operationName, result);
		} catch (XPathExpressionException e) {
			logger.error("Error in xpath expression",e);
		}
		return null;
	}

	private Node createInputNode(String name, String type) {
		Node node = new Node();
		String[] table = type.split(":");
		node.name = name;
		if (table[0].equals("xsd")) {
			node.type = table[1];
			return node;
		} else {
			node.type = "complex";
			try {
				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();
				XPathExpression expr = null;
				if (type.trim().length() == 0) {
					expr = xpath.compile("//*[local-name()='element' and @name='" + name + "']//element");
				} else {
					expr = xpath.compile("//*[local-name()='complexType' and @name='" + type.split(":")[1] + "']//element");
				}
				NodeList result = (NodeList) expr.evaluate(flattenedXML, XPathConstants.NODESET);
				for (int i = 0; i < result.getLength(); i++) {
					Element n = (Element) result.item(i);
					String elName = xpath.evaluate("@name", n);
					String elType = xpath.evaluate("@type", n);
					node.list.add(createInputNode(elName, elType));
				}
			} catch (XPathExpressionException e) {
				logger.error("Erron in xpath expression",e);
			}
		}
		return node;
	}

	private static void addToSOAPEnvelope(SOAPHeader sh, String name, String value, String namespaceURI, String prefix) throws SOAPException {
		QName headerName = new QName(namespaceURI, name, prefix);
		SOAPHeaderElement headerElement = sh.addHeaderElement((headerName));
		QName mU = headerElement.createQName("mustUnderstand", "SOAP-ENV");
		QName actor = headerElement.createQName("actor", "SOAP-ENV");
		headerElement.addAttribute(actor, "http://schemas.xmlsoap.org/soap/actor/next");
		headerElement.addAttribute(mU, "0");
		headerElement.addTextNode(value);
	}

	private static void addToSOAPEnvelope(SOAPHeader sh, String name, QName qn, String value, String namespaceURI, String prefix) throws SOAPException {
		QName headerName = new QName(namespaceURI, name, prefix);
		SOAPHeaderElement headerElement = sh.addHeaderElement((headerName));
		QName mU = headerElement.createQName("mustUnderstand", "SOAP-ENV");
		QName actor = headerElement.createQName("actor", "SOAP-ENV");
		headerElement.addAttribute(actor, "http://schemas.xmlsoap.org/soap/actor/next");
		headerElement.addAttribute(mU, "0");
		SOAPElement quotation = headerElement.addChildElement(qn);
		quotation.addTextNode(value);
	}

	private static void addResourceKeyToSOAPEnvelope(SOAPHeader sh, String name, String value, String namespaceURI, String prefix) throws SOAPException {
		QName headerName = new QName(namespaceURI, name, prefix);
		SOAPHeaderElement headerElement = sh.addHeaderElement((headerName));
		QName mU = headerElement.createQName("mustUnderstand", "SOAP-ENV");
		QName actor = headerElement.createQName("actor", "SOAP-ENV");
		headerElement.addAttribute(actor, "http://schemas.xmlsoap.org/soap/actor/next");
		headerElement.addAttribute(mU, "0");
		headerElement.addTextNode(value);
	}

	@SuppressWarnings("unused")
	private void addToSOAPBody(Node node, SOAPElement parentElement, boolean first) throws SOAPException {
		SOAPElement quotation;
		if (first) {
			quotation = parentElement;
		} else {
			quotation = parentElement.addChildElement(node.name);
		}
		if (node.type.equals("complex")) {
			for (Node n : node.list) {
				addToSOAPBody(n, quotation, false);
			}
		} else {
			if (this.arguments.containsKey(node.name)) {
				String input = this.arguments.get(node.name);
				if (input.contains(",")) {
					String[] inputs = input.trim().split("\\s*,\\s*");
					quotation.addTextNode(inputs[0]);
					for (int i = 1; i < inputs.length; i++) {
						quotation = parentElement.addChildElement(node.name);
						quotation.addTextNode(inputs[i]);
					}
				} else {
					quotation.addTextNode(input);
				}
			} else {
				quotation.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi"), "true");
			}
		}
	}

	public String getSOAP() {

		String scope, serviceClass, to, serviceName, body;
			scope = this.arguments.get(SOAPBuilder.SCOPE);
			serviceClass = this.arguments.get(SOAPBuilder.SERVICECLASS);
			to = this.arguments.get(SOAPBuilder.TO);
			serviceName = this.arguments.get(SOAPBuilder.SERVICENAME);
			body = this.arguments.get("body");
			if(scope==null || serviceClass==null || to==null || serviceName==null)
				throw new IllegalArgumentException("You need to specify scope, serviceClass, body and To arguments");

		try {
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage sm = mf.createMessage();

			SOAPEnvelope envelope = sm.getSOAPPart().getEnvelope();

			envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
			envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			envelope.addNamespaceDeclaration("wsa", "http://schemas.xmlsoap.org/ws/2004/03/addressing");

			SOAPHeader sh = sm.getSOAPHeader();

			addToSOAPEnvelope(sh, SOAPBuilder.SERVICECLASS, serviceClass, "http://gcube-system.org/namespaces/scope", "pref");
			addToSOAPEnvelope(sh, SOAPBuilder.SERVICENAME, serviceName, "http://gcube-system.org/namespaces/scope", "pref");
			addToSOAPEnvelope(sh, SOAPBuilder.SCOPE, scope, "http://gcube-system.org/namespaces/scope", "pref");
			addToSOAPEnvelope(sh, SOAPBuilder.MESSAGEID, "uuid:" + UUID.randomUUID().toString(), "http://schemas.xmlsoap.org/ws/2004/03/addressing", "wsa");
			addToSOAPEnvelope(sh, SOAPBuilder.TO, to, "http://schemas.xmlsoap.org/ws/2004/03/addressing", "wsa");
			addToSOAPEnvelope(sh, SOAPBuilder.ACTION, this.getAction(), "http://schemas.xmlsoap.org/ws/2004/03/addressing", "wsa");

			QName qname = new QName("http://schemas.xmlsoap.org/ws/2004/03/addressing", "Address", "wsa");
			addToSOAPEnvelope(sh, SOAPBuilder.FROM, qname, "http://schemas.xmlsoap.org/ws/2004/03/addressing/role/anonymous",
					"http://schemas.xmlsoap.org/ws/2004/03/addressing", "wsa");
			logger.info("RESOURCESERVICENAMESPACE "+this.getResourceServiceNamespace());
			if (this.arguments.containsKey(SOAPBuilder.RESOURCEKEY))
			{
				addResourceKeyToSOAPEnvelope(sh, SOAPBuilder.RESOURCEKEY, this.arguments.get(SOAPBuilder.RESOURCEKEY), this.getResourceServiceNamespace(), "ns3");
				logger.info("RESOURCESERVICENAMESPACE "+this.getResourceServiceNamespace());
			}

			SOAPBody sb = sm.getSOAPBody();

//			Node n = this.getInput();
//			QName bodyName = new QName(this.getServiceNamespace(), n.name, "pref");
//			SOAPBodyElement bodyElement = sb.addBodyElement(bodyName);
//			addToSOAPBody(n, bodyElement, true);
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();  
            builderFactory.setNamespaceAware(true);  
            InputStream stream  = new ByteArrayInputStream(body.getBytes());  
            Document doc = builderFactory.newDocumentBuilder().parse(stream);  
            sb.addDocument(doc);
            
            insertMissingInputs(sb);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			sm.writeTo(out);
			String strMsg = new String(out.toByteArray());
			return strMsg;
		} catch (Exception ex) {
			logger.error("Exception",ex);
		}
		return null;
	}

	private String getNamespace(String name) {
		NodeList entries = flattenedXML.getElementsByTagName("definitions");
		int num = entries.getLength();
		for (int i = 0; i < num; i++) {
			Element element = (Element) entries.item(i);
			NamedNodeMap attributes = element.getAttributes();
			int numAttrs = attributes.getLength();

			for (int j = 0; j < numAttrs; j++) {
				Attr attr = (Attr) attributes.item(j);

				String attrName = attr.getNodeName();
				String attrValue = attr.getNodeValue();

				if (attrName.contains(":")) {
					if (attrName.split(":")[1].equals(name))
						return attrValue;
				}
				else {
					if (attrName == name)
						return attrValue;
				}
			}
		}
		return null;
	}
	
	private void insertMissingInputs(SOAPBody sb) {
		Iterator<?> iter = sb.getChildElements();
		while(iter.hasNext())
		{
			SOAPBodyElement elem = (SOAPBodyElement) iter.next();
			if(this.arguments.containsKey(elem.getLocalName()))
			{
				elem.setTextContent(this.arguments.get(elem.getLocalName()));
			}
		}
	}

	public static void main(String[] args) throws SOAPException {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(SOAPBuilder.SERVICECLASS, "Index");
		hm.put(SOAPBuilder.SERVICENAME, "FullTextIndexNode");
		hm.put(SOAPBuilder.SCOPE, "gcube/devNext");
		hm.put(SOAPBuilder.TO, "http://ariadni.di.uoa.gr:8000/wsrf/services/gcube/index/FullTextIndexNode");
//		hm.put(SOAPBuilder.TO, "http://ariadni.di.uoa.gr:8000/wsrf/services/gcube/index/FullTextIndexNodeFactory");
		hm.put(SOAPBuilder.RESOURCEKEY, "4b435f90-96c0-11e2-a9ec-c813fb52e4da");
		hm.put(SOAPBuilder.OPERATION, "feedLocator");
//		hm.put(SOAPBuilder.OPERATION, "createResource");
//
		hm.put("body", "<pref:feedLocator "+
					"xmlns:pref=\"http://gcube-system.org/namespaces/index/FullTextIndexNode\">"+
				"</pref:feedLocator>");

//		hm.put("feedLocator", "something");
//		hm.put("body", "<pref:query "+
//				"xmlns:pref=\"http://gcube-system.org/namespaces/indexmanagement/FullTextIndexLookupService\">"+
//				"	<cqlQuery>something</cqlQuery>"+
//				"	<noncomplete>true</noncomplete>"+
//				"	</pref:query>"
//		);
		
		SOAPBuilder sb = new SOAPBuilder(hm);

		logger.info("SOAP: "+sb.getSOAP());
	}

}
