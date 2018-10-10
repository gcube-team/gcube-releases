/**
 * 
 */
package org.gcube.dataaccess.spql;

import java.util.Collections;
import java.util.List;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ParserException extends Exception {

	private static final long serialVersionUID = -8816849855712521070L;
	
	protected List<String> errors;
	
	/**
	 * @param message
	 * @param cause
	 */
	public ParserException(String message, String error) {
		super(message);
		errors = Collections.singletonList(error);
	}

	/**
	 * @param message
	 */
	public ParserException(String message) {
		super(message);
		errors = Collections.emptyList();
	}
	
	/**
	 * @param message
	 * @param errors
	 */
	public ParserException(String message, List<String> errors) {
		super(message);
		this.errors = errors;
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param errors
	 */
	public ParserException(String message, Throwable cause, List<String> errors) {
		super(message, cause);
		this.errors = errors;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParserException [errors=");
		builder.append(errors);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
