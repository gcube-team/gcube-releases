package org.gcube.opensearch.opensearchoperator.resource;

import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;

/**
 * Interface of the OpenSearch resource class that contains information pertaining to an OpenSearch provider
 * 
 * @author gerasimos.farantatos
 *
 */
public interface OpenSearchResource {

	/**
	 * Returns the name of the OpenSearch provider
	 * 
	 * @return The name of the OpenSearch provider
	 */
	public String getName();
	
	/**
	 * Determines whether the provider described by this resource returns brokered results, i.e
	 * the results it returns describe other OpenSearch providers
	 * 
	 * @return true if the provider returns brokered results, false otherwise
	 */
	public boolean isBrokered();
	
	/**
	 * Determines if the provider described by this resource uses a security scheme
	 * 
	 * @return true if the provider uses a security scheme, false otherwise
	 */
	public boolean isSecure();
	
	/**
	 * Retrieves the security specifications of this OpenSearch provider
	 * 
	 * @return The security specifications of the provider
	 */
	//TODO: fix this when specs are clarified
	public String getSecuritySpecs();
	
	/**
	 * Retrieves the description document of the OpenSearch provider described by this resource
	 * 
	 * @return A Document representing the description document of the provider
	 */
	public Document getDescriptionDocument();
	
	/**
	 * Retrieves the URL of the description document of the OpenSearch provider described by this resource
	 * 
	 * @return A String containing the URL of the description document of the provider
	 */
	public String getDescriptionDocURL();
	
	/**
	 * Retrieves a transformer that can be used to transform the results of a given MIME type that are
	 * returned by the provider described by this resource
	 * 
	 * @param MIMEType The MIME type of the results that will be transformed
	 * @return The transformer that can be used to transform the results of the given MIME type
	 * @throws Exception In case of error
	 */
	public Transformer getTransformer(String MIMEType) throws Exception;
	
	/**
	 * Retrieves an XPathExpression that can be used to split a search result page of a given MIME type to individual records
	 * 
	 * @param MIMEType The MIME type of the results returned by the provider described by this resource
	 * @return The Xpath expression that can be used to split search result pages to individual records
	 * @throws Exception In case of error
	 */
	public XPathExpression getRecordSplitXPath(String MIMEType) throws Exception;
	
	/**
	 * Retrieves an XPathExpression that can be used to extract a record id a from a search result record
	 * 
	 * @param MIMEType The MIME type of the results returned by the provider described by this resource
	 * @return The Xpath expression that can be used extract a record id from a search result record
	 * @throws Exception In case of error
	 */
	public XPathExpression getRecordIdXPath(String MIMEType) throws Exception;
	
	/**
	 * Retrieves a transformer that can be used to transform the results of the first MIME type appearing in this OpenSearch resource
	 * that are returned by the provider described by this resource
	 * 
	 * @return The transformer that can be used to transform the results of the first MIME type appearing in this OpenSearch resource
	 * @throws Exception In case of error
	 */
	public Transformer getTransformer() throws Exception;
	
	/**
	 * Retrieves a list of all MIME types for which there exists a transformation specification
	 * 
	 * @return All MIME types for which there exists a transformation specification
	 */
	public List<String> getTransformationTypes();
	
	/**
	 * Retrieves the presentation information, that is, a mapping from field names to XPath expressions
	 * @return
	 */
	public Map<String, String> getPresentationInformation(String MIMEType);
	
	/**
	 * Retrieves the fully qualified name of an OpenSearch parameter which corresponds to a field
	 * @param fieldName The name of the field corresponding to the requested OpenSearch parameter
	 * @return The fully qualified name of the OpenSearch parameter
	 */
	public String getParameterQName(String fieldName);
	
	/**
	 * Retrieved the full mapping from field names to fully ns-qualified OpenSearch parameters as contained
	 * in the OpenSearch Resource
	 * @return All field name to fully ns-qualified mappings 
	 */
	public Map<String, String> getParameters();
	
}
