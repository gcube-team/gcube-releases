package org.gcube.common.core.informationsystem.client;


/**
 * A (name,value) pair used as a query parameter.
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 */
public class QueryParameter {
	/** The parameter name. */
	public String name;
	/** The parameter value.*/
	public String value;	
	/**
	 * Creates a parameter from its name and value.
	 * @param name the name.
	 * @param value the value.
	 */
	public QueryParameter(String name, String value) {
		this.name=name;
		this.value=value;
	}
}
