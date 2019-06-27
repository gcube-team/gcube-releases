package org.gcube.data.analysis.tabulardata.commons.rules.types;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({DimensionColumnRuleType.class, BaseColumnRuleType.class, RuleTableType.class})
public abstract class RuleType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract Object getInternalType();
}
