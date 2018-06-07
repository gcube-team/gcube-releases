package org.gcube.common.core.informationsystem.client;


/**
 * A model of an XPath equality condition on a result of a {@link ISTemplateQuery}.
 * An atomic condition is comprised of an arbitrary XPath expression which evaluates to
 * a simple element of the result, and a value to match against the text content of the
 * element.
 * 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 */
public class AtomicCondition extends QueryParameter {
	
	/**
	 * Creates an instance from the path and the value of the condition.
	 * @param path the path of the condition.
	 * @param value the value of the condition.
	 */
	public AtomicCondition(String path, String value) {
		super(path,value);
	}
}
