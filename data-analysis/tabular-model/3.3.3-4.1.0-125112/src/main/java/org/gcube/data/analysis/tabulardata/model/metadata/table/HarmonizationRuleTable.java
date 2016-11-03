package org.gcube.data.analysis.tabulardata.model.metadata.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.Table;
@XmlRootElement(name = "HarmonizationRuleTable")
@XmlAccessorType(XmlAccessType.FIELD)
public class HarmonizationRuleTable implements TableMetadata {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2454749761708034378L;
	private Table rulesTable;
	
	
	@SuppressWarnings("unused")
	private HarmonizationRuleTable(){
		
	}
	
	
	public HarmonizationRuleTable(Table rulesTable) {
		super();
		this.rulesTable = rulesTable;
	}

	public Table getRulesTable() {
		return rulesTable;
	}
	public void setRulesTable(Table rulesTable) {
		this.rulesTable = rulesTable;
	}
	
	
	@Override
	public boolean isInheritable() {
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HarmonizationRuleTable [rulesTable=");
		builder.append(rulesTable);
		builder.append("]");
		return builder.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rulesTable == null) ? 0 : rulesTable.hashCode());
		return result;
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
		HarmonizationRuleTable other = (HarmonizationRuleTable) obj;
		if (rulesTable == null) {
			if (other.rulesTable != null)
				return false;
		} else if (!rulesTable.equals(other.rulesTable))
			return false;
		return true;
	}

	
	
}
