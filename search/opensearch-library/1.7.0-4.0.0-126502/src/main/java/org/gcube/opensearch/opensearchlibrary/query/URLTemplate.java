package org.gcube.opensearch.opensearchlibrary.query;


import java.util.List;

/**
 * Interface of the URL template class that is used to construct parse OpenSearch query templates
 * and expose their parameters
 * 
 * @author gerasimos.farantatos
 *
 */
public interface URLTemplate {
	
	/**
	 * Returns a list containing the qualified names of all required parameters contained in the template
	 * 
	 * @return A list of all required parameters of the template
	 */
	public List<String> getRequiredParameters();
	/**
	 * Returns a list containing the qualified names of all optional parameters contained in the template
	 * 
	 * @return A list of all optional parameters of the template
	 */
	public List<String> getOptionalParameters();
	/**
	 * Determines if a parameter with a given qualified name is a required parameter
	 * 
	 * @param name The qualified name of the parameter
	 * @return true if the parameter is required, false otherwise
	 * @throws NonExistentParameterException If the parameter is not found among the parameters contained in the template
	 * @throws Exception In case of other error
	 */
	public boolean isParameterRequired(String name) throws NonExistentParameterException, Exception;

	/**
	 * Determines if the template contains a parameter with a given qualified name
	 * 
	 * @param name The qualified name of the parameter
	 * @return true if the parameter is contained in the template, false otherwise
	 */
	public boolean hasParameter(String name);
	/**
	 * Returns the query template
	 * 
	 * @return The query template
	 */
	public String getTemplate();
}
