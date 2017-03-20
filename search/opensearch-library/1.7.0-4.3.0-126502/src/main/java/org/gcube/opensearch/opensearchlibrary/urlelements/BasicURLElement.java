package org.gcube.opensearch.opensearchlibrary.urlelements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;

import org.gcube.opensearch.opensearchlibrary.query.BasicQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.BasicURLTemplate;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * URL element class implementing the URLElement interface, which provides standard OpenSearch
 * URL element functionality
 * 
 * @author gerasimos.farantatos
 *
 */
public class BasicURLElement implements  URLElement{
	
	private Element url;
	
	private BasicURLTemplate template = null;
	private Map<String, String> nsPrefixes = null;
	private String MIMEType = null;
	protected String rel = null;
	private int indexOffset;
	private int pageOffset;
	
	private boolean init = false;
	private static final List<String> supportedRels = Arrays.asList("results", "suggestions", "self", "collection");
	
	private void extractTemplate() throws Exception {
		Node n = url.getAttributeNode("template");
		if(n == null) {
			throw new Exception("Url element lacks template attribute");
		}
		
		try {
			template = new BasicURLTemplate(n.getNodeValue(), nsPrefixes);
		}catch(Exception e) {
			throw new Exception("Malformed URL template", e);
		}
	}

	private void extractType() throws Exception {
		Node n = url.getAttributeNode("type");
		if(n == null) {
			throw new Exception("URL element lacks type attribute");
		}
//		String tmp = n.getNodeValue();
//		boolean valid = false;
//		if(tmp.compareTo("application/rss+xml") == 0) {
//			MIMEType = new MimeType("applicationMimeType.RSS_XML;
//			valid = true;
//		}
//		else if(tmp.compareTo("text/html") == 0) {
//			MIMEType = MimeType.TEXT_HTML;
//			valid = true;
//		}
//		else if(tmp.compareTo("application/opensearchdescription+xml") == 0) {
//			MIMEType = MimeType.OPENSEARCHDESCRIPTION_XML;
//			valid = true;
//		}
//		if(valid == false)
//			throw new Exception("Invalid or unsupported MIME type");	
		
		try {
			new MimeType(n.getNodeValue().trim());
		}catch(Exception e) {
			throw new Exception("Malformed MIME type", e);	
		}
		MIMEType = n.getNodeValue();
	}
	
	private void extractRel() throws Exception {
		Node n = url.getAttributeNode("rel");
		if(n != null) {
			String tmp = n.getNodeValue().trim();
			if(tmp.compareTo("results") == 0 || tmp.compareTo("") == 0)
				rel = "results";
			rel = tmp;
		}
		else
			rel = "results"; //default
	}
	
	private void extractIndexOffset() throws Exception {
		Node n = url.getAttributeNode("indexOffset");
		if(n != null) {
			try {
				indexOffset = Integer.parseInt(n.getNodeValue().trim());
			}catch(Exception e) {
				throw new Exception("Invalid indexOffset value", e);
			}
		}else
			indexOffset = 1;		
	}
	
	private void extractPageOffset() throws Exception {
		Node n = url.getAttributeNode("pageOffset");
		if(n != null) {
			try {
				pageOffset = Integer.parseInt(n.getNodeValue().trim());
			}catch(Exception e) {
				throw new Exception("Invalid pageOffset value", e);
			}
		}else
			pageOffset = 1;
	}
	
	/**
	 * Creates a new BasicURLElement
	 * 
	 * @param url An Element instance containing the URL element to be processed
	 * @param nsPrefixes The mapping from namespace URIs to namespace prefixes for all namespaces contained in a description document
	 */
	public BasicURLElement(Element url, Map<String, String> nsPrefixes) {
		this.url = url;
		this.nsPrefixes = nsPrefixes;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#parse()
	 */
	public void parse() throws Exception {
		if(init == true)
			return;
		extractTemplate();
		extractType();
		extractIndexOffset();
		extractPageOffset();
		extractRel();
		init = true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getMimeType()
	 */
	public String getMimeType() throws Exception {
		if(init == false)
			throw new Exception("URL element not initialized");
		return MIMEType;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getIndexOffset()
	 */
	public int getIndexOffset() throws Exception {
		if(init == false)
			throw new Exception("URL element not initialized");
		return indexOffset;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getPageOffset()
	 */
	public int getPageOffset() throws Exception {
		if(init == false)
			throw new Exception("URL element not initialized");
		return pageOffset;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getRel()
	 */
	public String getRel() throws Exception {
		if(init == false)
			throw new Exception("URL element not initialized");
		return rel;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#isRelSupported()
	 */
	public boolean isRelSupported() throws Exception {
		if(init == false)
			throw new Exception("URL element not initialized");
		
		if(supportedRels.contains(rel))
			return true;
		return false;	
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.urlelements.URLElement#getQueryBuilder()
	 */
	public QueryBuilder getQueryBuilder() throws Exception {
		//If indexOffset and pageOffset are specified as optional parameters, their default values are
		//those specified in the URL element
		return new BasicQueryBuilder(template, ((Integer)indexOffset).toString(), ((Integer)pageOffset).toString());
	}

}
