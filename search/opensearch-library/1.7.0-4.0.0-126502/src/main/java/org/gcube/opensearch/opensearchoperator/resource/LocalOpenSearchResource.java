package org.gcube.opensearch.opensearchoperator.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class implementing the OpenSearchResource interface that processes OpenSearch resources in the absence
 * of an InformationSystem
 * 
 * @author gerasimos.farantatos
 *
 */
public class LocalOpenSearchResource implements OpenSearchResource {

	private Logger logger = LoggerFactory.getLogger(LocalOpenSearchResource.class.getName());
	
	private String DDUrl = null;
	private URL DDURL = null;
	private Document descriptionDocument = null;
	private String name = null;
	private boolean brokeredResults = false;
	private boolean security = false;
	private Map<String, TransformationSpec> transformationSpecs = new LinkedHashMap<String, TransformationSpec>();
	private TransformerFactory tf = TransformerFactory.newInstance();
	private Map<String, String> parameters = new HashMap<String, String>();
	
	private class SimpleErrorHandler implements ErrorHandler {
	    public void warning(SAXParseException e) throws SAXException {
	    	logger.warn(e.getMessage(), e);
	    }
	    
	    public void error(SAXParseException e) throws SAXException {
	        throw new SAXException(e.getMessage());
	    }

	    public void fatalError(SAXParseException e) throws SAXException {
	    	 throw new SAXException(e.getMessage());
	    }
	}

	private class TransformationSpec {
		public final URL XSLTUrl;
		public final XPathExpression splitXPath;
		public final XPathExpression idXPath;
		public final Map<String, String> presentationInfo;
		
		public TransformationSpec(URL XSLTUrl, XPathExpression splitXPath, XPathExpression idXPath, Map<String, String> presentationInfo) {
			this.splitXPath = splitXPath;
			this.idXPath = idXPath;
			this.XSLTUrl = XSLTUrl;
			this.presentationInfo = presentationInfo;
		}
		
		@Override
		public String toString() {
			return "XSLT=" + (XSLTUrl != null ? XSLTUrl.toString() : "identity") + " " + "recordSplit=" + splitXPath + " "
			+ " " + "recordId=" + idXPath + " " + " presentationInfo=" + presentationInfo.toString();
		}
	}
	/**
	 * Processes a OpenSearch resource. The XSLTs contained in the transformation specifications
	 * are retrieved using the local file system or the network.
	 * The XSLTLink element of the XML representation of the LocalOpenSearchResource is expected to contain a URL
	 * identifying the XSLT
	 * 
	 * @param document A Document representing the OpenSearch resource to be processed
	 * @throws Exception If the resource is malformed or in case of other error
	 */
	private void parse(Document document) throws Exception {
		name = document.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		DDUrl = document.getElementsByTagName("descriptionDocumentURI").item(0).getFirstChild().getNodeValue();
		DDURL = new URL(DDUrl);
		
		String tmp = document.getElementsByTagName("brokeredResults").item(0).getFirstChild().getNodeValue();
		if(tmp.compareToIgnoreCase("true") == 0 || tmp.compareTo("1") == 0)
			brokeredResults = true;
		
		Node n = document.getElementsByTagName("security").item(0);
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
		
	//	TransformerFactory tf = TransformerFactory.newInstance();
		XPathFactory xpFactory = XPathFactory.newInstance();
		int count = 0;
		int found = 0;
		NodeList nl = document.getElementsByTagName("transformation");
		
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
				logger.warn("Malformed XPath expression in transformation element for " + MIMEType + 
						" in resource " + name + ". Ignoring element.", e);
				continue;
			}
			
			ch = ((Element)n).getElementsByTagName("XSLTLink").item(0);
			URL xsltUrl = null;
			if(ch != null) {
				try {
					xsltUrl = new URL(ch.getFirstChild().getNodeValue());
				}catch(Exception e) {
					logger.warn("Malformed XSLT URL in transformation element for " + MIMEType + 
							" in resource " + name + ". Ignoring element.", e);
					continue;					
				}
			}

//			try {
//				URLConnection conn = xsltUrl.openConnection();
//				tf.newTransformer(new StreamSource(new BufferedReader(new InputStreamReader(conn.getInputStream())))); //Just check if XSLT is valid
//			}catch(Exception e) {
//				logger.warn("Error while parsing XSLT source for " + MIMEType + 
//			           " for resource " + name + ". Ignoring transformation element.", e);
//				continue;						
//			}
			
			Map<String, String> presentationInfo = new HashMap<String, String>();
			ch = ((Element)n).getElementsByTagName("presentationInfo").item(0);
			NodeList presentables = ((Element)ch).getElementsByTagName("presentable");
			for(int i = 0; i < presentables.getLength(); i++) {
				String presentableName = ((Element)presentables.item(i)).getElementsByTagName("fieldName").item(0).getFirstChild().getNodeValue();
				String presentableXPath = ((Element)presentables.item(i)).getElementsByTagName("expression").item(0).getFirstChild().getNodeValue();
				presentationInfo.put(presentableName, presentableXPath);
			}
		
			transformationSpecs.put(MIMEType, new TransformationSpec(xsltUrl, recordSplitExpr, recordIdExpr, presentationInfo));
			found++;
		}

		if(found == 0) {
			logger.error("Could not retrieve a valid transformation specification for resource " + name + ". Throwing exception.");
			throw new Exception("Could not retrieve a valid transformation specification for resource " + name);
		}

//		n = document.getElementsByTagName("brokeredXSLTUrl").item(0);
//		if(n != null) {
//			try {
//				URL bXSLTUrl = new URL(n.getFirstChild().getNodeValue());
//				URLConnection conn = bXSLTUrl.openConnection();
//				brokeredXSLT = tf.newTransformer(new StreamSource(new BufferedReader(new InputStreamReader(conn.getInputStream()))));
//				brokeredResults = true;
//			}catch(Exception e) {
//				logger.error("Error while processing XSLT for brokered results", e);
//			}
//		}
		
		try {
			URLConnection conn = DDURL.openConnection();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			this.descriptionDocument = builder.parse(new InputSource(in));
			in.close();
		}catch(Exception e) {
			logger.error("Error while processing Description Document of resource " + name);
			throw e;
		}
	
	}
	/**
	 * Creates a new LocalOpenSearchResource by directly providing its attributes
	 * 
	 * @param name The name of the OpenSearch resource to be created
	 * @param DDUrl The URL of the description document of the OpenSearch provider that will be associated with the OpenSearch resource to be created
	 * @param brokered true if the OpenSearch resource to be created provides brokered results, false otherwise
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public LocalOpenSearchResource(String name, URL DDUrl, boolean brokered) throws IOException, ParserConfigurationException, SAXException {
		this.name = name;
		this.DDUrl = DDUrl.toString();
		this.DDURL = new URL(DDUrl.toString());
		this.brokeredResults = brokered;
		
		URLConnection conn = DDUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		this.descriptionDocument = builder.parse(new InputSource(in));	
		in.close();
	}
	
	/**
	 * Creates a new LocalOpenSearchResource whose XML representation is stored in a file
	 * 
	 * @param file The file where the XML representation of the OpenSearch resource is stored
	 * @param schemaFile The schema file describing the schema of the OpenSearch resource to be used for validation. This is an optional parameter. 
	 * If null, no validation will be performed
	 * @throws Exception If the resource is malformed or in case of other error
	 */
	public LocalOpenSearchResource(File file, File schemaFile) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		if(schemaFile != null) {
			factory.setValidating(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaFile.getAbsolutePath());
		}
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());
		Document document = builder.parse(new InputSource(new BufferedReader(new FileReader(file))));
		parse(document);
	}
	
	/**
	 * Creates a new LocalOpenSearchResouce whose XML representation is stored in a String
	 * 
	 * @param xml The XML representation of the OpenSearch resource to be created
	 * @param schemaFile The schema file describing the schema of the OpenSearch resource to be used for validation. This is an optional parameter. 
	 * If null, no validation will be performed
	 * @throws Exception If the resource is malformed or in case of other error
	 */
	public LocalOpenSearchResource(String xml, File schemaFile) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		if(schemaFile != null) {
			factory.setValidating(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaFile.getAbsolutePath());
		}
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		parse(document);
	}
	
	/**
	 * Creates a new LocalOpenSearchResource whose XML representation can be retrieved through a URL
	 * 
	 * @param url The URL that will be used to retrieve the XML representation of the OpenSearch resource
	 * to be created
	 * @param schemaFile The schema file describing the schema of the OpenSearch resource to be used for validation. This is an optional parameter. 
	 * If null, no validation will be performed
	 * @throws Exception If the resource is malformed or in case of other error
	 */
	public LocalOpenSearchResource(URL url, File schemaFile) throws Exception {
		URLConnection conn = url.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		if(schemaFile != null) {
			factory.setValidating(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaFile.getAbsolutePath());
		}
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Document document = builder.parse(new InputSource(in));
		in.close();
		parse(document);
	}
	
	/**
	 * Adds a transformation specification to the OpenSearch resource
	 * 
	 * @param MIMEType The MIME type of the results that will be associated with the transformation specifications
	 * @param recordSplitXPath The Xpath expression to be used to split the results contained in a search result page to individual records
	 * @param recordIdXPath The Xpath expression to be used to extract a record id from a record of the search results
	 * @param XSLTUrl The URL of the XSLT to be used to transform the search result records to the desired form
	 * @param presentationInfo A mapping from presentable field names to XPath expressions that will be used to extract result fields from
	 * the results returned by the provider
	 * @throws Exception In case of error
	 */
	public synchronized void addTransformation(String MIMEType, String recordSplitXPath, String recordIdXPath, 
			URL XSLTUrl, Map<String, String> presentationInfo) throws Exception {

		try {
			new MimeType(MIMEType);
		}catch(Exception e) {
			logger.error("Malformed MIME type.", e);
			throw new Exception("Malformed MIME type.");
		}
		
		XPathFactory xpFactory = XPathFactory.newInstance();
	    XPath xpath = xpFactory.newXPath();
	    XPathExpression recordSplitExpr = xpath.compile(recordSplitXPath);
	    
	    XPathExpression recordIdExpr = null;
	    if(recordIdXPath != null) {
	    	xpath = xpFactory.newXPath();
	    	recordIdExpr = xpath.compile(recordIdXPath);
	    }
		
	    if(XSLTUrl != null) {
			try {
				URLConnection conn = XSLTUrl.openConnection();
				tf.newTransformer(new StreamSource(new BufferedReader(new InputStreamReader(conn.getInputStream())))); //Only check if XSLT is valid
			}catch(Exception e) {
				logger.error("Error while parsing XSLT source.", e);
				throw new Exception("Error while parsing XSLT source.");
			}
	    }
	
		if(transformationSpecs.containsKey(MIMEType))
			logger.warn("An XSLT/XPath pair is already present for type " + MIMEType + " . Previous specification will be replaced");
		transformationSpecs.put(MIMEType, new TransformationSpec(XSLTUrl, recordSplitExpr, recordIdExpr, presentationInfo));
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
	public synchronized Transformer getTransformer(String MIMEType) throws Exception {
		try {
			new MimeType(MIMEType);
		}catch(Exception e) {
			logger.error("Malformed MIME Type.", e);
			throw new Exception("Malformed MIME Type");
		}
		
		TransformationSpec tSpec = transformationSpecs.get(MIMEType);
		if(tSpec == null)
			return null;
		URLConnection conn = null;
		if(tSpec.XSLTUrl != null)
			conn = tSpec.XSLTUrl.openConnection();
// prints out xslt TODO remove
//		System.out.println("Transformer requested, creating one with the following XSLT");
//		BufferedReader test = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		String ln;
//		while((ln = test.readLine()) != null)
//			System.out.println(ln);
//		conn = transformationSpecs.get(MIMEType).XSLTUrl.openConnection();
//		
		TransformerFactory ttf = TransformerFactory.newInstance();
		if(tSpec.XSLTUrl != null)
			return ttf.newTransformer(new StreamSource(new BufferedReader(new InputStreamReader(conn.getInputStream()))));
		else
			return ttf.newTransformer();
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
	 * @see org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource#getTransformer())
	 */
	public synchronized Transformer getTransformer() throws Exception {

		if(transformationSpecs.isEmpty())
			return null;
		URLConnection conn = transformationSpecs.get(transformationSpecs.keySet().iterator()).XSLTUrl.openConnection();
		return tf.newTransformer(new StreamSource(new BufferedReader(new InputStreamReader(conn.getInputStream()))));
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
	
	public static void main(String[] args) throws Exception {
		LocalOpenSearchResource o = new LocalOpenSearchResource(new File(System.getenv("HOME") + "/workspace/OpenSearch_Library/Resources/Google.xml"), new File(System.getenv("HOME") + "/workspace/OpenSearch_Library/Schema/OpenSearchResource.xsd"));
//		String xml = "";
//		String line = null;
//		BufferedReader reader = new BufferedReader(new FileReader("Resources/Google.xml"));
//		while((line = reader.readLine()) != null) {
//			xml += line;
//		}
//		System.out.println(xml);
//		OpenSearchResource o = new OpenSearchResource(xml);
		System.out.println(o.getName());
		System.out.println(o.isBrokered());
		System.out.println(o.isSecure());
	}

}
