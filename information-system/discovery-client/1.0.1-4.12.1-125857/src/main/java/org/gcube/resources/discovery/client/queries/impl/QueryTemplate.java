package org.gcube.resources.discovery.client.queries.impl;

import static javax.xml.stream.XMLStreamConstants.*;
import static org.gcube.resources.discovery.client.queries.impl.Utils.*;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.gcube.resources.discovery.client.queries.api.Query;

/**
 * A {@link Query} that interpolates named parameters inside a template.
 * <p>
 * Templates are strings with empty XML elements, optionally with a {@link #DEFAULT} attribute, e.g.:
 * <p>
 * <code>all results that satisfy &lt;cond1/> or &lt;cond2 def='that'/> &lt;extra/></code>
 * <p>
 * Whenever {@link #expression()} is invoked, the elements in the template are replaced according to the first rule that applies
 * among the following:
 * 
 * <ul>
 * <li>by the value of an equally named parameter, if one exists
 * <li>by the value of the {@link #DEFAULT} attribute, if one exists
 * <li>by the empty string
 * </ul>
 * 
 * For example, given the previous template and the single parameter <code>cond1="this"</code>, {@link #expression()} returns:
 * <p>
 * <code>all results that satisfy this or that</code>
 * <p>
 * 
 */
public class QueryTemplate extends QueryBox implements Query {

	public static final String DEFAULT = "def";
	
	
	private static final XMLInputFactory xmlif = XMLInputFactory.newInstance();
	
	private static final String wrapper = "_template_";
	
	
	private final Map<String, String> parameters;

	/**
	 * Creates an instance with a template.
	 * 
	 * @param template the template
	 */
	public QueryTemplate(String template) {
		super(template);
		this.parameters = new HashMap<String, String>();
	}
	
	/**
	 * Creates an instance with a template and an initial set of parameters.
	 * 
	 * @param template the template
	 */
	public QueryTemplate(String template, Map<String, String> parameters) {
		super(template);
		notNull("parameters", parameters);
		this.parameters = new HashMap<String, String>(parameters);
	}

	public String expression() {
		return interpolate(super.expression(), parameters);
	}

	/**
	 * Adds a parameter to the query, overwriting any value that it may already have.
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 * @throws IllegalStateException if the parameter name or value are <code>null</code>
	 */
	public void addParameter(String name, String value) {
		
		notNull("name",name);
		notNull("value",value);
		
		this.parameters.put(name, value);
	}
	
	/**
	 * Adds a parameter to the query, extending any value that it may already have.
	 * 
	 * @param name the parameter name
	 * @param value the value
	 * @throws IllegalStateException if the parameter name or value are <code>null</code>
	 */
	public void appendParameter(String name, String value) {
		
		notNull("name",name);
		notNull("value",value);
		
		
		if (parameters.containsKey(name))
			value=parameters.get(name)+value;
		
		parameters.put(name, value);
	}
	
	/**
	 * Returns the current value of a parameter.
	 * @param name the parameter name
	 * @return the value
	 * @throws IllegalStateException if the parameter does not exist
	 * @throws IllegalStateException if the parameter name is <code>null</code>
	 */
	public String parameter(String name) throws IllegalStateException {
		
		notNull("name",name);
		
		if (hasParameter(name))
			return parameters.get(name);
		
		throw new IllegalStateException("unknown parameter "+name);
	}
	
	/**
	 * Returns <code>true</code> if the query has a given parameter.
	 * @param name the parameter name
	 * @return <code>true</code> if the query has a given parameter, <code>false</code> otherwise
	 * @throws IllegalStateException if the parameter name is <code>null</code>
	 */
	public boolean hasParameter(String name) {
		
		notNull("name",name);
		
		return parameters.containsKey(name);
	}

	// helper
	private String interpolate(String expression, Map<String, String> parameters) {
		// replace query parameters with their values.
		try {

			StringBuilder builder = new StringBuilder();
			
			XMLStreamReader xmlr = xmlif.createXMLStreamReader(new StringReader("<"+wrapper+">"+expression+"</"+wrapper+">"));
					
			loop: while (true) {

				int tokenType = xmlr.next();

				switch (tokenType) {

				case START_ELEMENT: // replace parameters with values (provided or default)

					String name = xmlr.getLocalName();
					
					if (name.equals(wrapper)) 
							break;
					
					if (parameters.containsKey(name))
						builder.append(parameters.get(name));
					else {
						// is there a default value?
						String def = xmlr.getAttributeValue(null,DEFAULT);
						if (def != null)
							// add default as a parameter
							builder.append(def);
					}
					break;

				case CHARACTERS: // copy text in output
					builder.append(xmlr.getText());
					break;

				case END_DOCUMENT:
					break loop;
				}
			}
			return builder.toString();
		} catch (Exception e) {
			throw new RuntimeException("cannot replace parameters " + parameters + " in query " + expression,e);
		}
	}
	
	
}
