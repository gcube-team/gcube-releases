package org.gcube.opensearch.opensearchlibrary.responseelements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Class implementing an OpenSearch XML response
 * 
 * @author gerasimos.farantatos
 *
 */
public class XMLResponse extends OpenSearchResponse {
	
	/**
	 * 
	 * @param in The InputStream from which to read the response
	 * @param qElFactory The QueryElement factory that will be used to construct query element implementations
	 * @param qb A QueryBuilder that is to be further populated in case a query contained in a response query element is requested
	 * @param encoding The encoding to expect
	 * @param nsPrefixes The mapping from namespace URIs to namespace prefixes for all namespaces contained in a description document
	 * @throws Exception If an error occurs while parsing the response
	 */
	public XMLResponse(InputStream in, QueryElementFactory qElFactory, QueryBuilder qb, String encoding, Map<String, String> nsPrefixes) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		BufferedReader bIn = new BufferedReader(new InputStreamReader(in));
		InputSource is = new InputSource(bIn);
		if(encoding.compareTo("UTF-8") != 0)
			is.setEncoding(encoding);
		response = builder.parse(is);
		this.nsPrefixes = nsPrefixes;
		Node n = response.getElementsByTagNameNS(OpenSearchConstants.OpenSearchNS, "totalResults").item(0);
		boolean hasTotalResults = true;
		if(n != null)
			totalResults = Integer.parseInt(n.getFirstChild().getNodeValue().trim());
		else {
			isLastPage = true;
			hasTotalResults = false;
		}
		
		n = response.getElementsByTagNameNS(OpenSearchConstants.OpenSearchNS, "startIndex").item(0);
		if(n != null)
			startIndex = Integer.parseInt(n.getFirstChild().getNodeValue().trim());
		else {
			if(hasTotalResults == false)
				containsInfo = false;
			isFirstPage = true;
		}
		
		n = response.getElementsByTagNameNS(OpenSearchConstants.OpenSearchNS, "itemsPerPage").item(0);
		if(n != null)
			itemsPerPage = Integer.parseInt(n.getFirstChild().getNodeValue().trim());
	
		int count = 0;
		while((n = response.getElementsByTagNameNS(OpenSearchConstants.OpenSearchNS, "Query").item(count++)) != null) {
			try {
				createQueryBuilder(n, qElFactory, qb);
			}catch(Exception e) {
				logger.warn("Ignored a Query element contained in a response element. Cause:", e);
				continue;
			}
		}	
		
		if(containsInfo == false) {
			isLastPage = null;
			isFirstPage = null;
		}
	}
	
}
