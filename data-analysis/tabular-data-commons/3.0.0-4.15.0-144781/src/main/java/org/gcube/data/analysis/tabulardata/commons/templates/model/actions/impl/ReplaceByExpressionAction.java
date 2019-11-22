package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.TemplateColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ReplaceByExpressionAction extends TemplateAction<Long> implements TemplateColumnAction {

	private static final Logger logger = LoggerFactory.getLogger(ReplaceByExpressionAction.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final long OPERATION_ID = 3101;
	
	private static final ExpressionParameter CONDITION_PARAMETER = new ExpressionParameter("condition", "Condition",
			"Boolean condition that identifies to modify rows", Cardinality.ONE);
	
	private static final ExpressionParameter VALUE_PARAMETER = new ExpressionParameter("value", "Value",
			"Expression that returns the value to be set", Cardinality.ONE);
	
	private Expression condition;
	
	private Expression valueToSet;
		
	private TemplateColumn<?> column;
	
	protected ReplaceByExpressionAction(){}
	
	public ReplaceByExpressionAction(Expression condition,
			Expression valueToSet, TemplateColumn<?> column) {
		super();
		try {
			if (!(condition.getReturnedDataType() instanceof BooleanType))
				throw new IllegalArgumentException("condition expression must return boolean");	
		} catch (NotEvaluableDataTypeException e) {
			logger.warn("condition expression is not evaluable");
		}
		try {
			if (!(valueToSet.getReturnedDataType().getClass().equals(column.getValueType())))
				throw new IllegalArgumentException("value expression must have the same type of the column");	
		} catch (NotEvaluableDataTypeException e) {
			logger.warn("condition expression is not evaluable");
		}
		this.condition = condition;
		this.valueToSet = valueToSet;
		this.column = column;
	}

	@Override
	public Long getIdentifier() {
		return OPERATION_ID;
	}

	@Override
	public Map<String, Object> getParameters() {
		HashMap<String, Object> parameters = new HashMap<>(2);
		parameters.put(CONDITION_PARAMETER.getIdentifier(), condition);
		parameters.put(VALUE_PARAMETER.getIdentifier(), valueToSet);
		return parameters;
	}

	public Expression getCondition() {
		return condition;
	}

	public Expression getValueToSet() {
		return valueToSet;
	}

	@Override
	public String getColumnId() {
		return column.getId();
	}

	@Override
	public boolean usesExpression() {
		return true;
	}

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		return columns;
	}
		
}
