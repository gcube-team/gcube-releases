package org.gcube.data.analysis.tabulardata.commons.rules;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.PlaceholderReplacer;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColumnRule extends Rule {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Expression preparedExpression;

	
	@SuppressWarnings("unused")
	private ColumnRule() {
		super();
	}

	public ColumnRule(Expression preparedExpression) {
		super();
		this.preparedExpression = preparedExpression;
	}
		
	/**
	 * @return the preparedExpression
	 */
	public Expression getExpressionWithPlaceholder() {
		return preparedExpression;
	}

	
	/**
	 * @return the ruleType
	 */
	public RuleScope getScope() {
		return RuleScope.COLUMN;
	}

	@Override
	public Expression getExpression(TableId tableId, Map<String, Column> placeholderColumnMapping) {
		if (placeholderColumnMapping.size()!=1) 
			throw new IllegalArgumentException("a ColumnRule can be applied only on one column");

		Column selectedColumn = (Column)placeholderColumnMapping.values().toArray()[0];
		
		ColumnReference reference = new ColumnReference(tableId, selectedColumn.getLocalId());
		PlaceholderReplacer replacer = null;
		try {
			replacer = new PlaceholderReplacer(getExpressionWithPlaceholder());
			replacer.replaceAll(reference);
		} catch (MalformedExpressionException e) {
			throw new RuntimeException(e);
		}

		return replacer.getExpression();
	}

	

}
