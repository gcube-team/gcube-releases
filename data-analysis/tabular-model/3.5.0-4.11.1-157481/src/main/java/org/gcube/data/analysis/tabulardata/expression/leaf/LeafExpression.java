package org.gcube.data.analysis.tabulardata.expression.leaf;

import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.Expression;

public abstract class LeafExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -333867220271834258L;

	@Override
	public List<Expression> getLeavesByType(
			Class<? extends LeafExpression> type) {
		return Collections.emptyList();
	}
	
	
}
