package org.gcube.opensearch.opensearchlibrary.query;


import java.util.List;

import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;

/**
 * Interface of the query builder class that is used to construct OpenSearch queries using
 * a query template
 *
 * @author gerasimos.farantatos
 *
 */
public interface QueryBuilder {

	/**
	 * Determines if a parameter is contained in the parameter set of the query builder
	 *  
	 * @param name The qualified name of the parameter to be checked
	 * @return true if the parameter is present in the parameter set, false otherwise
	 */
	public boolean hasParameter(String name);
	/**
	 * Returns a list containing all required parameters of the query builder
	 * 
	 * @return A list of all required parameters
	 */
	public List<String> getRequiredParameters();
	/**
	 * Returns a list containing all optional parameters of the query builder
	 * 
	 * @return A list of all optional parameters
	 */
	public List<String> getOptionalParameters();
	/**
	 * Returns a list containing all parameters to which no value has been assigned 
	 * 
	 * @return A list of all unset parameters
	 */
	public List<String> getUnsetParameters();
	/**
	 * Retrieves the value assigned to a parameter of the query builder
	 * 
	 * @param name The qualified name of the parameter
	 * @return The value of the parameter. or null if the parameter has no assigned value
	 * @throws NonExistentParameterException If the parameter is not contained in the parameter set of the query builder
	 * @throws Exception In case of other error
	 */
	public String getParameterValue(String name) throws NonExistentParameterException, Exception;
	
	/**
	 * Assigns a string value to a parameter of the query builder
	 * 
	 * @param name The qualified name of the parameter
	 * @param value The value to be assigned to the parameter
	 * @return The query builder with the parameter value assigned to the parameter
	 * @throws NonExistentParameterException If the parameter is not contained in the parameter set of the query builder
	 * @throws Exception In case of other error
	 */
	public QueryBuilder setParameter(String name, String value) throws NonExistentParameterException, Exception;
	/**
	 * Assigns an integral value to a parameter of the query builder
	 * 
	 * @param name The qualified name of the parameter
	 * @param value The value to be assigned to the parameter
	 * @return The query builder with the parameter value assigned to the parameter
	 * @throws NonExistentParameterException If the parameter is not contained in the parameter set of the query builder
	 * @throws Exception In case of other error
	 */
	public QueryBuilder setParameter(String name, Integer value) throws NonExistentParameterException, Exception;
	/**
	 * Assigns to each parameter contained in the list of the first argument the respective value contained in the list of the secord argument
	 * 
	 * @param names A list of parameter qualified names
	 * @param values A list of parameter values to be assigned to the respective parameter
	 * @return The query builder with the values assigned to the parameters
	 * @throws NonExistentParameterException If a parameter of the list is not contained in the parameter of the query builder
	 * @throws Exception In case of other error
	 */
	public QueryBuilder setParameters(List<String> names, List<Object> values) throws NonExistentParameterException, Exception;
	/**
	 * Sets all parameters of the query builder that are also contained in the QueryElement provided with the values contained in the QueryElement
	 * 
	 * @param queryEl The query element which will be used to assign values to the parameters of the query builder
	 * @return The query builder with the QueryElement's values assigned
	 * @throws Exception In case of error
	 */
	public QueryBuilder setParameters(QueryElement queryEl) throws Exception;
	/**
	 * Determines if a parameter has an assigned value.
	 * More specifically, true is returned if and only if the parameter is present and its value is not equal to null and not equal to the empty string.
	 * 
	 * @param name The qualified name of the parameter
	 * @return true if the value of the parameter is set, false otherwise
	 */
	public boolean isParameterSet(String name);
	/**
	 * Returns the default value of the StartIndex OpenSearch parameter associated with this query builder 
	 * 
	 * @return The default value of the StartIndex parameter
	 */
	public Integer getStartIndexDef();
	/**
	 * Returns the default value of the StartPage OpenSearch parameter associated with this query builder 
	 * 
	 * @return The default value of the StartPage parameter
	 */
	public Integer getStartPageDef();
	
	/**
	 * Determines whether the query is complete and therefore ready to be issued, i.e. there are no unset required parameters
	 * 
	 * @return true if the query is complete, false otherwise
	 */
	public boolean isQueryComplete();
	/**
	 * Retrieves the search query corresponding to the current state of the query builder
	 * 
	 * @return The search query
	 * @throws IncompleteQueryException If the query is not complete, i.e. there still exist unset required parameters
	 * @throws MalformedQueryException If the query is malformed, e.g. if a parameter value is not of the correct form
	 * @throws Exception In case of other error
	 */
	public String getQuery() throws IncompleteQueryException, MalformedQueryException, Exception;
	
	/**
	 * Returns the query template associated with this query builder
	 * 
	 * @return The query template
	 */
	public String getRawTemplate();
	
	/**
	 * Returns a copy of this query builder
	 * 
	 * @return A new query builder whose state is the same as this instance
	 */
	public QueryBuilder clone();
	
}
