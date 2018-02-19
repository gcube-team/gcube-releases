package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DsdTemplate {
	
	@JsonProperty("tableTemplate")
	private TableTemplate tableTemplate;
	
	@JsonProperty("codelists")
	private List<Codelist> codelists;
	
	@JsonProperty("rules")
	private List<Rule> rules;
	
	public TableTemplate getTableTemplate() {
		return tableTemplate;
	}
	
	public void setTableTemplate(TableTemplate tableTemplate) {
		this.tableTemplate = tableTemplate;
	}
	
	public List<Codelist> getCodelists() {
		return codelists;
	}
	
	public void setCodelists(List<Codelist> codelists) {
		this.codelists = codelists;
	}
	
	public List<Rule> getRules() {
		return rules;
	}
	
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
}
