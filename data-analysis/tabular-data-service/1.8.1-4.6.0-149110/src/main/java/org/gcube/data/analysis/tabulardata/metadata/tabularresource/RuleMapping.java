package org.gcube.data.analysis.tabulardata.metadata.tabularresource;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.gcube.data.analysis.tabulardata.metadata.StorableRule;

@Entity
public class RuleMapping {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	
	//PlaceHolderId, columnId
	@ElementCollection()
	private Map<String, String> placeholderColumnMapping;
	
	@ManyToOne
	private StorableRule rule;
	
	@ManyToOne
	private StorableTabularResource tabularResource;
	
	@SuppressWarnings("unused")
	private RuleMapping(){}
	
	@Column(nullable=true)
	private String columnLocalId;
	
	public RuleMapping(StorableRule rule, Map<String, String> placeholderColumnMapping ){
		this.rule = rule;
		this.placeholderColumnMapping = placeholderColumnMapping;
	}
		
	public RuleMapping(StorableRule rule, String columnLocalId ){
		this.rule = rule;
		this.columnLocalId = columnLocalId;
	}	
	
	/**
	 * @return the rule
	 */
	public StorableRule getStorableRule() {
		return rule;
	}

	/**
	 * @return the placeholderColumnMapping
	 */
	public Map<String, String> getPlaceholderColumnMapping() {
		return placeholderColumnMapping;
	}

	public long getId() {
		return id;
	}

	public String getColumnLocalId() {
		return columnLocalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnLocalId == null) ? 0 : columnLocalId.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime
				* result
				+ ((placeholderColumnMapping == null) ? 0
						: placeholderColumnMapping.hashCode());
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleMapping other = (RuleMapping) obj;
		if (rule.getId()!= other.getStorableRule().getId()) return false;
		
		if (columnLocalId != null) 
			return columnLocalId.equals(other.columnLocalId);
		
		return true;
	}

	
	
}
