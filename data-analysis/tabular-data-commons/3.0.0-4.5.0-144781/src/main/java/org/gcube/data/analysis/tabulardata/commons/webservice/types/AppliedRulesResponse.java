package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.MapAdapter;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AppliedRulesResponse {
	
	List<RuleDescription> tableRules;
	
	@XmlJavaTypeAdapter(MapAdapter.class)
	Map<String, List<RuleDescription>> columnRuleMapping;
		
	@SuppressWarnings("unused")
	private AppliedRulesResponse(){}
	
	public AppliedRulesResponse(List<RuleDescription> tableRules,
			Map<String, List<RuleDescription>> columnRuleMapping) {
		super();
		this.tableRules = tableRules;
		this.columnRuleMapping = columnRuleMapping;
	}

	public List<RuleDescription> getTableRules() {
		return tableRules;
	}

	public Map<String, List<RuleDescription>> getColumnRuleMapping() {
		return columnRuleMapping;
	}

	@Override
	public String toString() {
		return "AppliedRulesResponse [tableRules=" + tableRules
				+ ", columnRuleMapping=" + columnRuleMapping + "]";
	}
			
}
