package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataCollectionDto implements Dto {
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("status")
	private Integer status;
	
	@JsonProperty("startDate")
	private Date startDate;
	
	@JsonProperty("endDate")
	private Date endDate;
	
	@JsonProperty("domain")
	private DomainDto domain;
	
	@JsonProperty("dataModel")
	private DataModelDto dataModel;
	
	@JsonProperty("attributes")
	private AttributesDto attributes;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public DomainDto getDomain() {
		return domain;
	}
	
	public void setDomain(DomainDto domain) {
		this.domain = domain;
	}
	
	public DataModelDto getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(DataModelDto dataModel) {
		this.dataModel = dataModel;
	}
	
	public AttributesDto getAttributes() {
		return attributes;
	}
	
	public void setAttributes(AttributesDto attributes) {
		this.attributes = attributes;
	}
	
}
