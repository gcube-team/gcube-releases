package org.gcube.opensearch.opensearchlibrary;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElement;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory;
import org.gcube.opensearch.opensearchlibrary.utils.SyndicationRight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This is the central class of the OpenSearch library, providing functionality related to OpenSearch
 * description documents. A client who intends to use the library for the purpose of submitting queries
 * to OpenSearch-enabled providers should first instantiate this class.
 * On instantiation, a pre-parsed {@link Document} object is checked for validity as an OpenSearch description
 * document. If the document is found valid, various operations such as the retrieval of {@link QueryBuilder} objects
 * ,which can be used in order to submit queries, and the querying of a number of description document related properties 
 * are exposed to the client.
 * 
 * @author gerasimos.farantatos
 *
 */
public class DescriptionDocument {
	
	private Logger logger = LoggerFactory.getLogger(DescriptionDocument.class.getName());
	private Document descriptionDocument = null;
	private Map<String, String> reverseNSMappings = new HashMap<String, String>(); //TODO not needed, remove
	private Map<String, String> NSMappings = new HashMap<String, String>();
	
	private String shortName = null;
	private String longName = null;
	private String description = null;
	private List<URLElement> urlElements = null;
	private List<QueryElement> queryElements = null;
	private String contact = null;
	private List<String> tags = null;
	private String developer = null;
	private String attribution = null;
	private SyndicationRight syndication = null;
	private Boolean adultContent = null;
	private List<String> lang = null;  
	private List<ImageElement> images = null;
	private List<String> inputEncoding = null;
	private List<String> outputEncoding = null;
	
	private final String defaultInputEncoding = "UTF-8";
	private final String defaultOutputEncoding = "UTF-8";
	
	/**
	 * Stores the mappings from namespace prefixes to namespace URIs that are contained
	 * in the root element of the description document
	 */
	private void extractNSMappings() {
		NamedNodeMap nnm = descriptionDocument.getDocumentElement().getAttributes();
		for(int i = 0; i < nnm.getLength(); i++) {
			String NSUrl = nnm.item(i).getNodeValue();
			String prefix = nnm.item(i).getLocalName();
			if(prefix.compareTo("xmlns") != 0) {
				reverseNSMappings.put(NSUrl, prefix);
				NSMappings.put(prefix, NSUrl);
			}
			else
				NSMappings.put("", OpenSearchConstants.OpenSearchNS);
		}
	}
	
	/**
	 * Retrieves the namespace prefix associated with a namespace URI in the description document
	 * 
	 * @param NSUrl The namespace URI
	 * @return The prefix of the namespace. Null is returned if the namespace URI is not present in the description document
	 */
	public String getNSPrefix(String NSUrl) {
		return reverseNSMappings.get(NSUrl);
	}
	
	/**
	 * Retrieves the namespace URI associated with a namespace prefix in the description document
	 * 
	 * @param NSPrefix The prefix of the namespace
	 * @return The URI of the namespace. Null is returned if the namespace prefix is not present in the description document
	 */
	public String getNSUrl(String NSPrefix) {
		return NSMappings.get(NSPrefix);
	}
	
	/**
	 * Creates a new {@link DescriptionDocument} instance
	 * 
	 * @param descriptionDocument A {@link Document} corresponding to the XML document that is to be processed as a description document
	 * @param urlFactory The factory that will be used to construct {@link URLElement}s in order to process URL elements contained in the description document
	 * @param queryFactory The factory that will be used to construct {@link QueryElement}s in order to process Query elements contained in the description document
	 * @throws Exception In case of an error, description document validity related or otherwise.
	 */
	public DescriptionDocument(Document descriptionDocument, URLElementFactory urlFactory, QueryElementFactory queryFactory) throws Exception {
		this.descriptionDocument = descriptionDocument;
		
		if(!descriptionDocument.getDocumentElement().getNodeName().equals("OpenSearchDescription")) {
			logger.error("Bad document element. Throwing exception");
			throw new Exception("Bad document element");
		}
		
		extractNSMappings();
		
		//Required element: ShortName
		Node n = descriptionDocument.getElementsByTagName("ShortName").item(0);
		if(n == null) {
			logger.error("Description Document lacks a ShortName element. Throwing exception");
			throw new Exception("Description Document lacks a ShortName element");
		}
		shortName = n.getFirstChild().getNodeValue();
		
		//Optional element: LongName
		n = descriptionDocument.getElementsByTagName("LongName").item(0);
		if(n != null)
			longName = n.getFirstChild().getNodeValue();
		
		//Required element: Description
		n = descriptionDocument.getElementsByTagName("Description").item(0);
//		if(n == null) {
//			logger.error("Description Document lacks a Description element. Throwing exception");
//			throw new Exception("Description Document lacks a Description element");
//		}
		//TODO the following is a workaround to make out-of-spec providers work
		if(n == null)
			logger.warn("Description Document lacks a Description element.");
		else 
			description = n.getFirstChild().getNodeValue();
		
		//Required element: Url
		//Construct and store URL Element objects describing result queries or other functions
		urlElements = new ArrayList<URLElement>();
		int count = 0;
		int supported = 0;
		while((n = descriptionDocument.getElementsByTagName("Url").item(count)) != null) {
			URLElement urlEl;
			count++;
			try {
				urlEl = urlFactory.newInstance((Element)n, reverseNSMappings);
				urlEl.parse();
			}
			catch(Exception e) {
				logger.warn("Ignored a Url element. Cause:", e);
				continue;
			}
			urlElements.add(urlEl);
			supported++;
		}
		if(count == 0) {
			logger.error("Description Document lacks URL elements. Throwing exception.");
			throw new Exception("Description Document lacks URL elements");
		}
		if(supported == 0) {
			logger.error("Description Document lacks valid or supported URL elements. Throwing exception");
			throw new Exception("Description Document lacks valid or supported Url elements");
		}
		
		//Optional element: Query
		count = 0;
		queryElements = new ArrayList<QueryElement>();
		while((n = descriptionDocument.getElementsByTagName("Query").item(count++)) != null) {
			QueryElement queryEl;
			try {
				queryEl = queryFactory.newInstance((Element)n, reverseNSMappings);
				queryEl.parse();
			}
			catch(Exception e) {
				logger.warn("Ignored a Query element. Cause:", e);
				continue;
			}
			
			if(queryEl.describesExampleQuery() == false) {
				logger.warn("Ignored a Query element. Cause: Query element does not describe an example query");
				continue;
			}
	
			queryElements.add(queryEl);
		}
		
		//Optional element: Contact
		n = descriptionDocument.getElementsByTagName("Contact").item(0);
		if(n != null)
			contact = n.getFirstChild().getNodeValue();
	
		//Optional element: Tags
		n = descriptionDocument.getElementsByTagName("Tags").item(0);
		if(n != null) {
			tags = new ArrayList<String>();
			tags.addAll(Arrays.asList(n.getFirstChild().getNodeValue().split(" ")));
		}
		
		//Optional element: Developer
		n = descriptionDocument.getElementsByTagName("Developer").item(0);
		if(n != null)
			developer = n.getFirstChild().getNodeValue();
		
		//Optional element: Attribution
		n = descriptionDocument.getElementsByTagName("Attribution").item(0);
		if(n != null)
			attribution = n.getFirstChild().getNodeValue();	
		
		//Optional element: SyndicationRight
		n = descriptionDocument.getElementsByTagName("SyndicationRight").item(0);
		if(n != null) {
			boolean valid = false;
			String sr = n.getFirstChild().getNodeValue().trim();
			if(sr.compareToIgnoreCase("open") == 0) {
				syndication = SyndicationRight.OPEN;
				valid = true;
			}else if(sr.compareToIgnoreCase("limited") == 0) {
				syndication = SyndicationRight.LIMITED;
				valid = true;
			}else if(sr.compareToIgnoreCase("private") == 0) {
				syndication = SyndicationRight.PRIVATE;
				valid = true;
			}else if(sr.compareToIgnoreCase("closed") == 0) {
				syndication = SyndicationRight.CLOSED;
				valid = true;
			}
			if(valid == false) {
				logger.error("Invalid value in SyndicationRight element. Throwing exception");
				throw new Exception("Invalid value in SyndicationRight element");
			}
		}
		else
			syndication = SyndicationRight.OPEN; //default
		
		//Optional element: AdultContent
		n = descriptionDocument.getElementsByTagName("AdultContent").item(0);
		if(n != null) {
			String ac = n.getFirstChild().getNodeValue().trim();
			if(ac.compareTo("false") == 0 || ac.compareTo("FALSE") == 0 || ac.compareTo("no") == 0|| ac.compareTo("NO") == 0 || ac.compareTo("0") == 0)
				adultContent = false;
			else
				adultContent = true;
		}
		
		//Optional element: Language
		count = 0;
		while((n = descriptionDocument.getElementsByTagName("Language").item(count)) != null) {
			if(count == 0)
				lang = new ArrayList<String>();
			if(lang != null) {
				String tmp = n.getFirstChild().getNodeValue();
				if(tmp.compareTo("*") != 0)
					lang.add(n.getFirstChild().getNodeValue());
				else
					lang = null;
			}
			count++;
		}
		
		//Optional element: Image
		count = 0;
		if((n = descriptionDocument.getElementsByTagName("Image").item(0)) != null) {
			images = new ArrayList<ImageElement>();
			images.add(new ImageElement((Element)n));
			count++;
		}
		while((n = descriptionDocument.getElementsByTagName("Image").item(count)) != null) {
			images.add(new ImageElement((Element)n));
			count++;
		}
	
		//Optional element: inputEncoding
		inputEncoding = new ArrayList<String>();
		count = 0;
		supported = 0;
		while((n = descriptionDocument.getElementsByTagName("InputEncoding").item(count)) != null) {
			String enc = n.getFirstChild().getNodeValue();
			if(Charset.isSupported(enc) == true) {
				inputEncoding.add(enc);
				supported++;
			}
			count++;
		}
		
		if(count == 0)
			inputEncoding.add(defaultInputEncoding);
		else if(supported == 0) {
			logger.error("Unsupported encoding in InputEncoding element. Throwing exception");
			throw new Exception("Unsupported encoding in InputEncoding element");
		}
		
		//Optional element: outputEncoding
		outputEncoding = new ArrayList<String>();
		count = 0;
		supported = 0;
		while((n = descriptionDocument.getElementsByTagName("OutputEncoding").item(count)) != null) {
			String enc = n.getFirstChild().getNodeValue();
			if(Charset.isSupported(enc) == true) {
				outputEncoding.add(enc);
				supported++;
			}
			count++;
		}
		
		if(count == 0)
			outputEncoding.add(defaultOutputEncoding);
		else if(supported == 0) {
			logger.error("Unsupported encoding in OutputEncoding element. Throwing exception");
			throw new Exception("Unsupported encoding in OutputEncoding element");
		}
	}
	
	/**
	 * Returns the String contained in the ShortName element of the description document
	 * 
	 * @return The ShortName String or null if the description document does not contain such an element
	 */
	public String getShortName() {
		return shortName;
	}
	
	/**
	 * Returns the String contained in the LongName element of the description document
	 * 
	 * @return The LongName String or null if the description document does not contain such an element
	 */
	public String getLongName() {
		return longName;
	}
	
	/**
	 * Returns the String contained in the Description element of the description document
	 * 
	 * @return The Description value
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the String contained in the Contact element of the description document
	 * 
	 * @return The Contact String or null if the description document does not contain such an element
	 */
	public String getContact() {
		return contact;
	}
	
	/**
	 * Returns the String contained in the Developer element of the description document
	 * 
	 * @return The Developer String or null if the description document does not contain such an element
	 */
	public String getDeveloper() {
		return developer;
	}
	
	/**
	 * Returns the String contained in the Attribution element of the description document
	 * 
	 * @return The Attribution String or null if the description document does not contain such an element
	 */
	public String getAttribution() {
		return attribution;
	}
	
	/**
	 * Returns the {@link SyndicationRight} value contained in the SyndicationRight element of the description document
	 * 
	 * @return The {@link SyndicationRight} of the description document or null if such an element is absent
	 */
	public SyndicationRight getSyndicationRight() {
		return syndication;
	}
	
	/**
	 * Determines whether the client may request search results from the OpenSearch provider corresponding to this description document
	 * 
	 * @return true if the client may request search results, false otherwise
	 */
	public boolean canRequest() {
		return syndication != SyndicationRight.CLOSED;
	}
	
	/**
	 * Determines whether the client may display the search results obtained from the OpenSearch provider corresponding to this description document to end users
	 * 
	 * @return true if the client may display the search results to end users, false otherwise
	 */
	public boolean canSendToEndUsers() {
		return syndication == SyndicationRight.OPEN || syndication == SyndicationRight.LIMITED;
	}
	
	/**
	 * Determines whether the client may send the search results obtained from the OpenSearch provider corresponding to this description document to other search clients
	 * 
	 * @return true if the client may send the search results to other search clients, false otherwise
	 */
	public boolean canSendToClients() {
		return syndication == SyndicationRight.OPEN;
	}
	
	/**
	 * Determines whether the search results obtained from the OpenSearch provider corresponding to this description document may contain material intended only for adults
	 * 
	 * @return true if the search results may contain material intended only for adults, false otherwise
	 */
	public Boolean hasAdultContent() {
		return adultContent;
	}
	
	/**
	 * Retrieves a list of all the tags contained in the description document which identify and categorize the search content
	 * 
	 * @return A list containing all the tags contained in the description document. Null is returned if no tags are specified in the description document
	 */
	public List<String> getTags() {
		return tags == null ? null : new ArrayList<String>(tags);
	}
	
	/**
	 * Determines whether the description document limits the search to a set of supported languages
	 * 
	 * @return true if the search must be limited to the languages supported by the provider, false if queries for an arbitrary language are supported
	 */
	public boolean hasLangRestriction() {
		return lang == null ? false : true;
	}
	
	/**
	 * Determines whether a specific language is supported by the OpenSearch provider corresponding to this description document
	 * 
	 * @param lang The language to check support for
	 * @return true if the language is supported, false otherwise
	 */
	public boolean isLanguageSupported(String lang) {
		if(this.hasLangRestriction() == false)
			return true;
		if(this.lang.contains(lang))
			return true;
		return false;
	}
	
	/**
	 * Retrieves a list of all languages supported by the OpenSearch provider corresponding to this description document
	 * 
	 * @return A list of all supported languages
	 */
	public List<String> getSupportedLanguages() {
		return hasLangRestriction() ? new ArrayList<String>(lang) : null;
	}
	
	/**
	 * Determines whether a specific input encoding is supported by the OpenSearch provider corresponding to this description document
	 * 
	 * @param encoding The input encoding to check support for
	 * @return true if the input encoding is supported, false otherwise
	 */
	public boolean isInputEncodingSupported(String encoding) {
		return inputEncoding.contains(encoding);
	}
	
	/**
	 * Returns the default input encoding of the OpenSearch specification
	 * @return the default input encoding
	 */
	public String getDefaultInputEncoding() {
		return defaultInputEncoding;
	}
	
	/**
	 * Retrieves a list of all input encodings supported by e OpenSearch provider corresponding to this description document
	 * 
	 * @return A list of all supported input encodings
	 */
	public List<String> getSupportedInputEncodings() {
		return new ArrayList<String>(inputEncoding);
	}
	
	/**
	 * Determines whether a specific output encoding is supported by the OpenSearch provider corresponding to this description document
	 * 
	 * @param encoding The output encoding to check support for
	 * @return true if the output encoding is supported, false otherwise
	 */
	public boolean isOutputEncodingSupported(String encoding) {
		return outputEncoding.contains(encoding);
	}
	
	/**
	 * Returns the default output encoding of the OpenSearch specification
	 * @return the default output encoding
	 */
	public String getDefaultOutputEncoding() {
		return defaultOutputEncoding;
	}
	/**
	 * Retrieves a list of all output encodings supported by e OpenSearch provider corresponding to this description document
	 * 
	 * @return A list of all supported output encodings
	 */
	public List<String> getSupportedOutputEncodings() {
		return new ArrayList<String>(outputEncoding);
	}
	
	/**
	 * Retrieves the URI of the image that appears in the first Image element of this description document
	 * 
	 * @return The URI of the image
	 */
	public URI getImageURI() {
		if(images == null)
			return null;
		URI uri = null;
		try {
			uri =  new URI(images.get(0).getURI().toString()); //this always returns the first image. According to OpenSearch specs this is actually the image that the client should give preference to
		}catch(Exception e) { 
			logger.warn("Unexpected exception caught while creating a URI copy", e);
		}
		return uri;
	}
	
	/**
	 * Retrieves the URI of the image with a specific MIME type appearing first in an Image element of this description document
	 * 
	 * @param MIMEType The MIME type of the image to be retrieved
	 * @return The URI of the image with the given MIME type 
	 */
	public URI getImageURI(String MIMEType) {
		if(images == null)
			return null;
		URI uri = null;
		int i = 0;
		while(images.get(i++).getMimeType().compareTo(MIMEType) != 0);
		if( i == images.size())
			i = 0;
		
		try {
			uri =  new URI(images.get(i).getURI().toString()); 
		}catch(Exception e) { 
			logger.warn("Unexpected exception caught while creating a URI copy", e);
		}
		return uri;
	}
	
	/**
	 * Retrieves a list of MIME types supported for constructing queries for a specific role
	 * Standard OpenSearch Url rel values are: "results", "suggestion", "self" and "collection"
	 * 
	 * @param rel The role of the resource to be queried
	 * @return A list of all MIME types supported for querying the resource associated with the given role
	 * @throws Exception If an error has occurred
	 */
	public List<String> getSupportedMimeTypes(String rel) throws Exception {
		Iterator<URLElement> it = urlElements.iterator();
		List<String> types = null;
		int count = 0;
		while(it.hasNext()) {
			URLElement n = it.next();
			if(n.getRel().compareTo(rel) == 0) {
				if(count == 0)
					types = new ArrayList<String>();
				types.add(n.getMimeType());
				count++;
			}
		}
		return types;
	}
	
	/**
	 * Returns all parameter-wise unique templates contained in this description document
	 * 
	 * @return An association of the MIME types corresponding to the templates with the unique templates themselves
	 * @throws Exception If the URL elements of the description document are not initialized, or in case of other errors
	 */
	public Map<String, List<String>> getUniqueTemplates() throws Exception {
		Map<String, List<String>> templates = new HashMap<String, List<String>>(); //<MimeType, Template list>
		List<QueryBuilder> qbs = new ArrayList<QueryBuilder>();
		
		for(URLElement urlEl : urlElements) {
			if(urlEl.getRel().compareTo("results") != 0)
				continue;
			
			if(qbs.size() == 0) {
				templates.put(urlEl.getMimeType(), new ArrayList<String>(Arrays.asList(urlEl.getQueryBuilder().getRawTemplate())));
				qbs.add(urlEl.getQueryBuilder());
				continue;
			}
			
		    @SuppressWarnings("unused")
			List<String> reqParams = urlEl.getQueryBuilder().getRequiredParameters();
		    @SuppressWarnings("unused")
			List<String> optParams = urlEl.getQueryBuilder().getOptionalParameters();
		    
			for(QueryBuilder qb: qbs) {
				List<String> reqPs = urlEl.getQueryBuilder().getRequiredParameters();
				List<String> optPs = urlEl.getQueryBuilder().getOptionalParameters();
				reqPs.removeAll(qb.getRequiredParameters());
				optPs.removeAll(qb.getOptionalParameters());
				if(!reqPs.isEmpty() || !optPs.isEmpty()) {
					if(!templates.containsKey(urlEl.getMimeType()))
						templates.put(urlEl.getMimeType(), new ArrayList<String>(Arrays.asList(urlEl.getQueryBuilder().getRawTemplate())));
					else
						templates.get(urlEl.getMimeType()).add(urlEl.getQueryBuilder().getRawTemplate());
					break;
				}
			}
			qbs.add(urlEl.getQueryBuilder());
		}
		return templates;
	}
	
	/**
	 * Retrieves a list of {@link QueryBuilder} objects that can be used to construct queries for resources related to a specific role and which return results of 
	 * a given MIME type
	 * 
	 * @param rel The role of the resource to be queried, e.g. "results"
	 * @param MIMEType The MIME type of the results that the provider should return
	 * @return A list of all {@link QueryBuilder}s that satisfy the criteria passed as arguments. If none are found, an empty list is returned.
	 * @throws Exception If the URL elements contained in the desription document are not initialized or in case of other errors
	 */
	public List<QueryBuilder> getQueryBuilders(String rel, String MIMEType) throws Exception {
		Iterator<URLElement> it = urlElements.iterator();
		List<QueryBuilder> qbs = new ArrayList<QueryBuilder>();
		while(it.hasNext()) {
			URLElement n = it.next();
			if(n.getRel().compareTo(rel) == 0 && n.getMimeType().compareTo(MIMEType) == 0)
				qbs.add(n.getQueryBuilder());
		}
		return qbs;
	}

	/**
	 * Retrieves a list of all {@link QueryBuilder} objects that can be used to send example queries as described in the Query elements of this description document.
	 * The result type of the queries constructed by these {@link QueryBuilder}s will match the MIME type given as argument.
	 * 
	 * @param MIMEType The MIME type which the results obtained from queries issued using the {@link QueryBuilder}s returned should have
	 * @return A list of {@link QueryBuilder} objects that can be used to issue the requested example queries
	 * @throws Exception If the URL elements contained in the desription document are not initialized or in case of other errors
	 */
	public List<QueryBuilder> getExampleQueryBuilders(String MIMEType) throws Exception {
		Iterator<URLElement> urlIt = urlElements.iterator();
		Iterator<QueryElement> queryIt;
		List<QueryBuilder> qbs = new ArrayList<QueryBuilder>();
		Map<String, String> queryParams;
		QueryBuilder urlQb;
		while(urlIt.hasNext()) {
			URLElement urlN = urlIt.next();
			if(urlN.getMimeType().compareTo(MIMEType) != 0)
				continue;
			urlQb = urlN.getQueryBuilder();
			List<String> urlParams = urlQb.getRequiredParameters();
			queryIt = queryElements.iterator();
			while(queryIt.hasNext()) {
				urlQb = urlN.getQueryBuilder();
				QueryElement queryN = queryIt.next();
				if(!queryN.describesExampleQuery())
					continue;
			    queryParams = queryN.getQueryParameters();
				if(!urlParams.containsAll(queryParams.keySet()))
					continue;
				for(Map.Entry<String, String> e : queryParams.entrySet())
					urlQb.setParameter(e.getKey(), e.getValue());
				qbs.add(urlQb);
			}

		}
		return qbs;
	}

	public Map<String, String> getNSPrefixToURIMappings() {
		return new HashMap<String, String>(NSMappings);
	}
	
	public Map<String, String> getURIToPrefixMappings() {
		return new HashMap<String, String>(reverseNSMappings);
	}
}
