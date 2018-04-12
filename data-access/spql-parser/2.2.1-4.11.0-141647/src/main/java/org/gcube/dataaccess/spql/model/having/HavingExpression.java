/**
 * 
 */
package org.gcube.dataaccess.spql.model.having;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class HavingExpression {
	
	protected String expression;

	/**
	 * @param expression
	 */
	public HavingExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HavingExpression [expression=");
		builder.append(expression);
		builder.append("]");
		return builder.toString();
	}


}
