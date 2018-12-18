/**
 * 
 */
package org.gcube.dataaccess.spql.model.error;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SyntaxError extends RuntimeException implements QueryError {

	private static final long serialVersionUID = 5091576971399127351L;

	public SyntaxError(String errorMessage) {
		super(errorMessage);
	}

	@Override
	public String getErrorMessage() {
		return getMessage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SyntaxError [getErrorMessage()=");
		builder.append(getErrorMessage());
		builder.append("]");
		return builder.toString();
	}
}
