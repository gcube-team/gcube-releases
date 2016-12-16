package org.gcube.common.core.informationsystem.client.impl;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.kxml2.io.KXmlParser;

/**
 * Abstract implementation of {@link ISQuery}.
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class AbstractQuery<RESULT> implements ISQuery<RESULT>  {

	/** Query parameters .*/
	protected Map<String,String> parameters = Collections.synchronizedMap(new HashMap<String,String>());
	
	/**The time-to-live of query results.*/
	long ttl = 100000;
	
	/** The textual expression of the query.*/
	protected String expression;
	
	/**{@inheritDoc}*/
	public long getTTL() {return this.ttl;}

	/**{@inheritDoc}*/
	public String getExpression() {

		//replace query parameters with their values.
		try{
			StringBuilder builder = new StringBuilder();
			KXmlParser parser = new KXmlParser();
			parser.setInput(new StringReader(this.expression));
			String value;
			loop: while (true) {
				int tokenType = parser.next();
				switch (tokenType){	
					case KXmlParser.START_TAG :
						String name = parser.getName();
						value = parameters.get(name);
						if (value!=null) builder.append(value);
						else {
							String replacement = parser.getAttributeValue(null,"ISdefault");
							if (replacement!=null) this.parameters.put(name,replacement);
							else builder.append("<"+name+">");
						}
						break;
					case KXmlParser.TEXT:
						builder.append(parser.getText());
						break;
					case KXmlParser.END_TAG :
						name = parser.getName();
						if (parameters.get(name)==null) builder.append("</"+name+">");
						break;
					case KXmlParser.END_DOCUMENT :
						break loop;
				}
			}
			return builder.toString();
		}
		catch (Exception e) {return null;}
	}

	/**{@inheritDoc}*/
	public void setTTL(long ttl) {this.ttl=ttl;}

	/**{@inheritDoc}*/
	public void setExpression(String exp) {this.expression=exp;}
	
	/**
	 * Add one or more parameters to the query.
	 * @param parameters the parameters.
	 */
	public void addParameters(QueryParameter ... parameters) {
		if (parameters==null) return;
		for (QueryParameter parameter : parameters) this.parameters.put(parameter.name,parameter.value);
	}
	
	
	/**
	 * Override to indicate whether the query is well-formed.
	 * By default, it simply checks that the query expression is non-<code>null</code>.
	 * @return <code>true</code> if the query is well-formed, <code>false</code> otherwise.
	 */
	protected boolean isWellFormed() {return this.getExpression()!=null;}
	
	/**
	 * Override to return a query result from its textual serialisation.
	 * @param unparsedResult the textual representation.
	 * @return the result.
	 * @throws ISMalformedResultException if the result cannot be deserialised.
	 */
	protected abstract RESULT parseResult(String unparsedResult) throws ISMalformedResultException;

}
