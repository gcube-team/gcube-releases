package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Field {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("order")
	private Integer order;
	
	@JsonProperty("codelist")
	private Codelist codelist;
	
	@JsonProperty("static")
	private boolean staticField = false;
	
	@JsonIgnore
	private boolean mandatory = false;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Codelist getCodelist() {
		return codelist;
	}
	
	public void setCodelist(Codelist codelist) {
		this.codelist = codelist;
	}
	
	public boolean isStaticField() {
		return staticField;
	}
	
	public void setStaticField(boolean staticField) {
		this.staticField = staticField;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
}
