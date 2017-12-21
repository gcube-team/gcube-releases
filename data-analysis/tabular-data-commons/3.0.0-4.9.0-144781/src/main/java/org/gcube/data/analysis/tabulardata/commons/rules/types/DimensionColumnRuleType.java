package org.gcube.data.analysis.tabulardata.commons.rules.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DimensionColumnRuleType extends RuleColumnType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DimensionReference dimensionReference;
		
	@SuppressWarnings("unused")
	private DimensionColumnRuleType(){}
	
	public DimensionColumnRuleType(DimensionReference dimensionReference) {
		super();
		this.dimensionReference = dimensionReference;
	}

	public RuleColumn getType() {
		return RuleColumn.DimensionColumn;
	}

	public Object getInternalType() {
		return dimensionReference;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DimensionColumnRuleType other = (DimensionColumnRuleType) obj;
		if (dimensionReference == null) {
			if (other.dimensionReference != null)
				return false;
		} else if (!dimensionReference.equals(other.dimensionReference))
			return false;
		return true;
	}

	
	
}
