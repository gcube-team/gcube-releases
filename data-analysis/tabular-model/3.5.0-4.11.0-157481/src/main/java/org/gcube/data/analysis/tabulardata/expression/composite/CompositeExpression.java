package org.gcube.data.analysis.tabulardata.expression.composite;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;

public abstract class CompositeExpression extends Expression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8188471531784080598L;

	public abstract Operator getOperator();
}
