package org.gcube.data.analysis.tabulardata.commons.rules.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseColumnRuleType extends RuleColumnType{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataType dataType;
	
	@SuppressWarnings("unused")
	private BaseColumnRuleType(){}
	
	public BaseColumnRuleType(DataType dataType) {
		super();
		this.dataType = dataType;
	}

	public RuleColumn getType() {
		return RuleColumn.BaseColumn;
	}

	public DataType getInternalType() {
		return dataType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseColumnRuleType other = (BaseColumnRuleType) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BaseColumnRuleType [dataType=" + dataType + "]";
	}
	
	
	
}
