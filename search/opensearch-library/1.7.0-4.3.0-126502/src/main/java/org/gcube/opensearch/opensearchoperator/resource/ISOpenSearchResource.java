package org.gcube.opensearch.opensearchoperator.resource;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.is.InformationSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Class implementing the OpenSearchResource interface that processes OpenSearch resources when an
 * InformationSystem is available
 * 
 * @author gerasimos.farantatos
 *
 */
public class ISOpenSearchResource implements OpenSearchResource {

	private Logger logger = LoggerFactory.getLogger(ISOpenSearchResource.class.getName());
	
	private EnvHintCollection envHints = null;
	private String DDUrl = null;
	private Document descriptionDocument = null;
	private String name = null;
	private boolean brokeredResults = false;
	private boolean security = false;
	private Map<String, TransformationSpec> transformationSpecs = new LinkedHashMap<String, TransformationSpec>();
	private Map<String, String> parameters = new HashMap<String, String>();
	
	private class TransformationSpec {
		public final Transformer transformer;
		public final XPathExpression idXPath;
		public final XPathExpression splitXPath;
		public final Map<String, String> presentationInfo;
		
		public TransformationSpec(Transformer transformer, XPathExpression splitXPath, XPathExpression idXPath, Map<String, String> presentationInfo) {
			this.splitXPath = splitXPath;
			this.transformer = transformer;
			this.idXPath = idXPath;
			this.presentationInfo = presentationInfo;
		}
	}
	
	/**
	 * Checks if a Body element is present as a root element in the XML representation to be processed and if it is
	 * found, it removes it
	 * 
	 * @param xml The xml representation of the OpenSearch resource
	 * @return The xml representation of the OpenSearch resource with the Body element removed
	 * @throws Exception
	 */
	private String stripBodyElement(String xml) throws Exception {
		String result = xml;
		int indexStart = xml.indexOf("<Body>");
		int indexEnd;
		
		if(indexStart != 1) {
			int bodyLength = "<Body>".length();
			if(indexStart != 0)
				throw new Exception("Found Body start tag at a position different than 0");
			indexEnd = xml.lastIndexOf("</Body>");
			if(indexEnd == -1)
				throw new Exception("Could not find Body end tag");
			if(indexEnd + bodyLength != xml.length()-1)
				throw new Exception("Extra characters after Body element end tag");
			result = xml.substring(indexStart + bodyLength, indexEnd).trim();
		}
		return result;
	}
	
	/**
	 * Processes a OpenSearch resource in XML form. The XSLTs contained in the transformation specifications
	 * are retrieved using the InformationSystem, if they are not already present in the XSLT cache.
	 * The XSLTLink element of the XML representation of the ISOpenSearchResource is expected to contain the name
	 * of the generic resource corresponding to the XSLT
	 * 
	 * @param xml The XML representation of the OpenSearch resource
	 * @param descriptionDocs A cache containing description documents in XML form, identified by their URLs. If null, no description document cache will be used
	 * @param resourcesXML A cache containing OpenSearch resources in XML form, identified by their description document URLs. If null, no resource cache will be used
	 * @param XSLTs A cache containing XSLTs in XML form, identified by the names of the generic resources associated with them. If null, no XSLT cache will be used
	 * @throws Exception
	 */
	private void parse(String xml, Map<String, String> descriptionDocs, Map<String, String> resourcesXML, Map<String, String> XSLTs) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
	//	factory.setValidating(true);
	//	factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
	//	factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "Schema/OpenSearchResource.xsd");
	//	builder.setErrorHandler(new SimpleErrorHandler());
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
	
		Element rootNode = (Element)document.getDocumentElement();
		if(xml.indexOf("<Body>") == 0)
			rootNode = (Element)document.getElementsByTagName("Body").item(0);
		name = rootNode.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		DDUrl = rootNode.getElementsByTagName("descriptionDocumentURI").item(0).getFirstChild().getNodeValue();
		@SuppressWarnings("unused")
		URL DDURL = new URL(DDUrl);
		
		//cache xml representation of the resource
		if(resourcesXML != null)
			resourcesXML.put(DDUrl, xml);
		
		String tmp = rootNode.getElementsByTagName("brokeredResults").item(0).getFirstChild().getNodeValue();
		if(tmp.compareToIgnoreCase("true") == 0 || tmp.compareTo("1") == 0)
			brokeredResults = true;
		
		Node n = rootNode.getElementsByTagName("security").item(0);
		if(n != null) {
			security = true;
		//	tmp = ((Element)n).getElementsByTagName("spec").item(0).getFirstChild().getNodeValue(); //TODO: fix this when security specs are clarified
		}
		else
			security = false;
		
		NodeList paramsNl = ((Element)document.getElementsByTagName("parameters").item(0)).getElementsByTagName("parameter");
		for(int i = 0; i < paramsNl.getLength(); i++) {
			Element param = (Element)paramsNl.item(i);
			parameters.put(param.getElementsByTagName("fieldName").item(0).getFirstChild().getNodeValue(),
					param.getElementsByTagName("qName").item(0).getFirstChild().getNodeValue());
		}
		
		TransformerFactory tf = TransformerFactory.newInstance();
		XPathFactory xpFactory = XPathFactory.newInstance();
		int count = 0;
		int found = 0;
		NodeList nl = rootNode.getElementsByTagName("transformation");
		
		while((n = nl.item(count)) != null) {
			count++;
			String MIMEType = null;

			Node ch = ((Element)n).getElementsByTagName("MIMEType").item(0);
			MIMEType = ch.getFirstChild().getNodeValue();
			
			try {
				new MimeType(MIMEType);
			}catch(Exception e) {
				logger.warn("Malformed MIME type in transformation element in resource " + name + ". Ignoring element.", e);
				continue;
			}
			
			ch = ((Element)n).getElementsByTagName("recordIdXPath").item(0);
			XPathExpression recordIdExpr = null;
			if(ch != null) {
				try {
					XPath xpath = xpFactory.newXPath();
				    recordIdExpr = xpath.compile(ch.getFirstChild().getNodeValue());
				}catch(Exception e) {
					logger.warn("Malformed record id XPath expression in transformation element for " + MIMEType +
							" in resource " + name + ". Ignoring element.", e);
					continue;
				}
			}
			ch = ((Element)n).getElementsByTagName("recordSplitXPath").item(0);
			XPathExpression recordSplitExpr;
			try {
				XPath xpath = xpFactory.newXPath();
			    recordSplitExpr = xpath.compile(ch.getFirstChild().getNodeValue());
			}catch(Exception e) {
				logger.warn("Malformed record split XPath expression in transformation element for " + MIMEType + 
						" in resource " + name + ". Ignoring element.", e);
				continue;
			}
			
			ch = ((Element)n).getElementsByTagName("XSLTLink").item(0);
			String xsltLink = null;
			if(ch != null)
				xsltLink = ch.getFirstChild().getNodeValue();

//			try {
//				URLConnection conn = xsltUrl.openConnection();
//				tf.newTransformer(new StreamSource(new BufferedReader(new InputStreamReader(conn.getInputStream())))); //Just check if XSLT is valid
//			}catch(Exception e) {
//				logger.warn("Error while parsing XSLT source. Ignoring transformation element.", e);
//				continue;						
//			}
		
			Transformer transformer = null;
			/*
			 * retrieve the XSLT generic resource
			 */
			String xmlRes = null;
			try {
				if(xsltLink != null) {
					if(XSLTs != null)
						xmlRes = XSLTs.get(xsltLink);
					if(xmlRes == null) {
						
						List<String> xmlRess = InformationSystem.GetGenericByName(xsltLink, envHints);
						if(xmlRess.size() > 1)
							logger.warn("Found more than one generic resources with name " + xsltLink + ". Keeping first");
						xmlRes = xmlRess.get(0).trim();
						xmlRes = stripBodyElement(xmlRes);
						if(XSLTs != null) {
						//	logger.info("Resource: " + name + ": Did not find XSLT in the cache"); //TODO remove
							XSLTs.put(xsltLink, xmlRes); //cache XSLT
						}
					}
				}
				//else
				//	logger.info("Resource: " + name + ": Found XSLT in the cache"); //TODO remove
			}catch(Exception e) {
				logger.warn("Error while retrieving and processing XSLT source. Ignoring transformation element", e);
				continue;
			}

			try {
				if(xsltLink != null)
					transformer = tf.newTransformer(new StreamSource(new BufferedReader(new StringReader(xmlRes))));
				else 
					transformer = tf.newTransformer();
			}catch(Exception e) {
				logger.warn("Error while parsing XSLT source for " + MIMEType + 
						" for resource " + name + ". Ignoring transformation element.", e);
				continue;						
			}
			
			Map<String, String> presentationInfo = new HashMap<String, String>();
			ch = ((Element)n).getElementsByTagName("presentationInfo").item(0);
			NodeList presentables = ((Element)ch).getElementsByTagName("presentable");
			for(int i = 0; i < presentables.getLength(); i++) {
				String presentableName = ((Element)presentables.item(i)).getElementsByTagName("fieldName").item(0).getFirstChild().getNodeValue();
				String presentableXPath = ((Element)presentables.item(i)).getElementsByTagName("expression").item(0).getFirstChild().getNodeValue();
				presentationInfo.put(presentableName, presentableXPath);
			}
			
			transformationSpecs.put(MIMEType, new TransformationSpec(transformer, recordSplitExpr, recordIdExpr, presentationInfo));
			found++;
		}

		if(found == 0) {
			logger.error("Could not retrieve a valid transformation specification for resource " + name + ". Throwing exception.");
			throw new Exception("Could not retrieve a valid transformation specification for resource " + name);
		}
		
		try {
			String ddXML = null;
			if(descriptionDocs != null)
				ddXML = descriptionDocs.get(DDUrl);
			if(ddXML != null) {
				this.descriptionDocument = builder.parse(new InputSource(new StringReader(ddXML)));
				//logger.info("Resource: " + name + ": Found description document in cache"); //TODO remove
			}
			else {
				URLConnection conn = (new URL(DDUrl)).openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line; ddXML = new String();
				while((line = in.readLine()) != null)
					ddXML += line;
				in.close();
				this.descriptionDocument = builder.parse(new InputSource(new StringReader(ddXML)));
				if(descriptionDocs != null) {
					descriptionDocs.put(DDUrl, ddXML); //cache description document
				//	logger.info("Resource: " + name + ": Did not find description document in cache"); //TODO remove
				
				}
			}
				
		}catch(Exception e) {
			logger.error("Error while processing Description Document of resource " + name);
			throw e;
		}
	}
	
	/**
	 * Creates a new ISOpenSearchResource with a cache that will be used and further populated
	 * 
	 * @param xml The XML representation of the OpenSearch resource
	 * @param cachedDescriptionDocs A cache containing description documents in XML form, identified by their URLs
	 * @param cachedResourcesXML A cache containing OpenSearch resources in XML form, identified by their description document URLs
	 * @param cachedXSLTs A cache containing XSLTs in XML form, identified by the names of the generic resources associated with them
	 * @param envHints The environment hints that to be passed to the InformationSystem
	 * @throws Exception If the resource is malformed or in case of other error
	 */
	public ISOpenSearchResource(String xml, Map<String, String> cachedDescriptionDocs, Map<String, String> cachedResourcesXML, Map<String, String> cachedXSLTs, EnvHintCollection envHints) throws Exception {
		this.envHints = envHints;
		
		parse(xml.trim(), cachedDescriptionDocs, cachedResourcesXML, cachedXSLTs);
	}
	
	/**
	 * Creates a new ISOpenSearchResource that will not use a cache
	 * 
	 * @param xml The XML representation of the OpenSearch resource
	 * @param envHints The environment hints that to be passed to the InformationSystem
	 * @throws Exception If the resource is malformed or in case of other error
	 */
	public ISOpenSearchResource(String xml, EnvHintCollection envHints) throws Exception {
		this(xml, null, null, null, envHints);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#isBrokered()
	 */
	public boolean isBrokered() {
		return brokeredResults;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#isSecure()
	 */
	public boolean isSecure() {
		return security;
	}
	
	//TODO: fix this when specs are clarified
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getSecuritySpecs()
	 */
	public String getSecuritySpecs() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getDescriptionDocument()
	 */
	public Document getDescriptionDocument() {
		return descriptionDocument;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getDescriptionDocURL()
	 */
	public String getDescriptionDocURL() {
		return DDUrl;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getTransformer(String)
	 */
	public Transformer getTransformer(String MIMEType) throws Exception {
		try {
			new MimeType(MIMEType);
		}catch(Exception e) {
			logger.error("Malformed MIME Type.", e);
			throw new Exception("Malformed MIME Type");
		}
		
		return transformationSpecs.get(MIMEType).transformer;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getRecordSplitXPath(String)
	 */
	public XPathExpression getRecordSplitXPath(String MIMEType) throws Exception {
		try {
			new MimeType(MIMEType);
		}catch(Exception e) {
			logger.error("Malformed MIME Type.", e);
			throw new Exception("Malformed MIME Type");
		}
		
		return transformationSpecs.get(MIMEType).splitXPath;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getRecordIdXPath(String)
	 */
	public XPathExpression getRecordIdXPath(String MIMEType) throws Exception {
		try {
			new MimeType(MIMEType);
		}catch(Exception e) {
			logger.error("Malformed MIME Type.", e);
			throw new Exception("Malformed MIME Type");
		}
		
		return transformationSpecs.get(MIMEType).idXPath;
	}
	

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getTransformer()
	 */
	public Transformer getTransformer() throws Exception {
		return transformationSpecs.get(transformationSpecs.keySet().iterator()).transformer;
	}
	

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getPresentationInformation(String)
	 */
	@Override
	public Map<String, String> getPresentationInformation(String MIMEType) {
		return transformationSpecs.get(MIMEType).presentationInfo;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getTransformationTypes()
	 */
	public List<String> getTransformationTypes() {
		return new ArrayList<String>(transformationSpecs.keySet());
	}
	
	public String getParameterQName(String fieldName) {
		return parameters.get(fieldName);
	}
	
	public Map<String, String> getParameters() {
		return new HashMap<String, String>(parameters);
	}
}
