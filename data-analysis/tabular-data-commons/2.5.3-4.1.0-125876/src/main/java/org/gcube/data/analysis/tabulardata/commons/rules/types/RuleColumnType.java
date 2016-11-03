package org.gcube.data.analysis.tabulardata.commons.rules.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({DimensionColumnRuleType.class, BaseColumnRuleType.class})
public abstract class RuleColumnType extends RuleType{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum RuleColumn {
		BaseColumn, 
		DimensionColumn
	}
	
	protected RuleColumnType(){}
	
	public abstract RuleColumn getType();
	
	public abstract Object getInternalType();
	
}
