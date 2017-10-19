package org.gcube.data.analysis.tabulardata.commons.rules;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.MapAdapter;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.PlaceholderReplacer;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TableRule extends Rule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Expression preparedExpression;

	@XmlJavaTypeAdapter(value=MapAdapter.class)
	private Map<String, Class< ? extends DataType>> mappingPlaceholderType;
	
	@SuppressWarnings("unused")
	private TableRule() {
		super();
	}

	public TableRule(Expression preparedExpression, Map<String, Class< ? extends DataType>> mappingPlaceholderType) {
		super();
		this.preparedExpression = preparedExpression;
		this.mappingPlaceholderType = mappingPlaceholderType;
	}
	
	@Override
	public Expression getExpressionWithPlaceholder() {
		return preparedExpression;
	}

	@Override
	public Expression getExpression(TableId tableId,
			Map<String, Column> placeholderColumnMapping) {
		PlaceholderReplacer replacer = null;
		try {
			replacer = new PlaceholderReplacer(getExpressionWithPlaceholder());
			for (Entry<String, Column> entry : placeholderColumnMapping.entrySet())
				replacer.replaceById(new ColumnReference(tableId, entry.getValue().getLocalId(), entry.getValue().getDataType()), entry.getKey());
			return replacer.getExpression();
		} catch (MalformedExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RuleScope getScope() {
		return RuleScope.TABLE;
	}

}
