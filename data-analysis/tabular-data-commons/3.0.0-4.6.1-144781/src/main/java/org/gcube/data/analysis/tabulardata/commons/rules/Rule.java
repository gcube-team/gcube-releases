package org.gcube.data.analysis.tabulardata.commons.rules;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ColumnRule.class, TableRule.class})
public abstract class Rule implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Rule(){}
	
	public abstract Expression getExpressionWithPlaceholder();
	
	public abstract Expression getExpression(TableId tableId,  Map<String, Column> placeholderColumnMapping);
	
	public abstract RuleScope getScope();
}
