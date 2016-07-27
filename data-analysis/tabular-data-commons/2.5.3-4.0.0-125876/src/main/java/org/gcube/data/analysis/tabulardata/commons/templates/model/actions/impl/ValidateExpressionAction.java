package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.expression.Expression;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateExpressionAction extends TemplateAction<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4671336031540947624L;

	private static final String RULES_PARAMETER = "rules";
	private static final String EXPRESSION_PARAMETER = "expression";
	private static final String NAME_PARAMETER = "name";
	
	private String name;
	private Expression expression;
		
	protected ValidateExpressionAction() {}

	public ValidateExpressionAction(String name, Expression expression) {
		super();
		this.name = name;
		this.expression = expression;
	}

	@Override
	public boolean usesExpression() {
		return true;
	}

	@Override
	public Long getIdentifier() {
		return 5009l;
	}

	@Override
	public List<TemplateColumn<?>> getPostOperationStructure(
			List<TemplateColumn<?>> columns) {
		return columns;
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> rule = new HashMap<String, Object>();
		rule.put(EXPRESSION_PARAMETER, this.expression);
		rule.put(NAME_PARAMETER, this.name);
		return Collections.singletonMap(RULES_PARAMETER, (Object)Collections.singletonList(rule));
	}

	public String getName() {
		return name;
	}

	public Expression getExpression() {
		return expression;
	}
	
	
}
