/**
 * 
 */
package org.gcube.dataaccess.spql.model.error;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class AbstractQueryError implements QueryError {
	
	protected String errorMessage;

	/**
	 * @param errorMessage
	 */
	public AbstractQueryError(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
