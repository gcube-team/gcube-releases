package org.gcube.resources.discovery.client.queries.impl;

import java.net.URI;
import java.util.Map;

import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * A {@link SimpleQuery} over an XQuery template.
 * <p>
 * The template is defined as follows (cf. {@link #template}):
 * <p>
 * <code>&lt;ns/> for $resource in &lt;range/>&lt;vars/> where &ltcond def="$result"/> return &lt;result def="$result"/></code>
 * <p>
 * 
 * where:
 * 
 * <ul>
 * <li> {@link #range} stands for the path to the data ranged over by the <code>$result</code> variable. This parameter
 * is typically bound at query-creation time (cf. {@link #XQuery(Map)}.
 * <li> {@link #ns}, {@link #vars}, {@link #cond} and {@link #result} stand for, respectively, the declarations of namespace prefixes, the declarations of auxiliary variables,
 * the conditions, and the result expression of the query. These parameters should be bound through the {@link SimpleQuery} API.
 * </ul>
 * 
 * @author Fabio Simeoni
 * 
 */
public class XQuery extends QueryTemplate implements SimpleQuery {

	public static final String ns = "ns";
	public static final String vars = "vars";
	public static final String range = "range";
	public static final String cond = "cond";
	public static final String result = "result";

	public static final String template = "<ns/> for $resource in <" + range + "/><vars/> where <" + cond + " " + DEFAULT
			+ "='$resource'/> return <" + result + " " + DEFAULT + "='$resource'/>";

	public XQuery(Map<String, String> parameters) {// add static parameters
		
		super(template, parameters);
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * In the condition, <code>$resource</code> ranges over resources.
	 * 
	 */
	public XQuery addCondition(String condition) {

		String newcond = "("+condition+")";
		
		if (hasParameter(cond))
			appendParameter(cond," and "+newcond);
		else
			addParameter(cond,newcond);
		
		
		return this;
	}

	public XQuery addNamespace(String prefix, URI uri) {

		String declaration = "declare namespace " + prefix + " = '" + uri + "';";
		
		appendParameter(ns,declaration);
		
		return this;
	}
	
	public XQuery addVariable(String variable, String range) {

		String declaration = ", "+variable+" in "+range;
		
		appendParameter(vars,declaration);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * In the expression, <code>$resource</code> ranges over resources.
	 */
	public XQuery setResult(String expression) {
		addParameter(result, expression);
		return this;
	}

}
