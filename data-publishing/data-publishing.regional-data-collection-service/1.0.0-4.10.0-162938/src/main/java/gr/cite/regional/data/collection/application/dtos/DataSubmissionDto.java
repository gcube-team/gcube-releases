package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataSubmissionDto implements Dto {
	
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("status")
	private Integer status;
	
	@JsonProperty("submissionTimestamp")
	private Date submissionTimestamp;
	
	@JsonProperty("completionTimestamp")
	private Date completionTimestamp;
	
	@JsonProperty("comment")
	private String comment;
	
	@JsonProperty("attributes")
	private AttributesDto attributes;
	
	@JsonProperty("data")
	private List<CdtDto> data;
	
	@JsonProperty("dataCollection")
	private DataCollectionDto dataCollection;
	
	@JsonProperty("domain")
	private DomainDto domain;
	
	@JsonProperty("owner")
	private UserReferenceDto owner;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Date getSubmissionTimestamp() {
		return submissionTimestamp;
	}
	
	public void setSubmissionTimestamp(Date submissionTimestamp) {
		this.submissionTimestamp = submissionTimestamp;
	}
	
	public Date getCompletionTimestamp() {
		return completionTimestamp;
	}
	
	public void setCompletionTimestamp(Date completionTimestamp) {
		this.completionTimestamp = completionTimestamp;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public AttributesDto getAttributes() {
		return attributes;
	}
	
	public void setAttributes(AttributesDto attributes) {
		this.attributes = attributes;
	}
	
	public List<CdtDto> getData() {
		return data;
	}
	
	public void setData(List<CdtDto> data) {
		this.data = data;
	}
	
	public DataCollectionDto getDataCollection() {
		return dataCollection;
	}
	
	public void setDataCollection(DataCollectionDto dataCollection) {
		this.dataCollection = dataCollection;
	}
	
	public DomainDto getDomain() {
		return domain;
	}
	
	public void setDomain(DomainDto domain) {
		this.domain = domain;
	}
	
	public UserReferenceDto getOwner() {
		return owner;
	}
	
	public void setOwner(UserReferenceDto owner) {
		this.owner = owner;
	}
}
