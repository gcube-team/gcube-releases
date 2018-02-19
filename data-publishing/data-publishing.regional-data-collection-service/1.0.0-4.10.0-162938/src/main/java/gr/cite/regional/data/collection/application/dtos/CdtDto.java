package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CdtDto implements Dto {
	@JsonProperty("id")
	private UUID id;
	
	@JsonProperty("ordinal")
	private Integer ordinal;
	
	@JsonProperty("status")
	private Integer status;
	
	@JsonProperty("dataSubmission")
	private Integer dataSubmission;
	
	@JsonProperty("data")
	private Map<String, Object> data;
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public Integer getOrdinal() {
		return ordinal;
	}
	
	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getDataSubmission() {
		return dataSubmission;
	}
	
	public void setDataSubmission(Integer dataSubmission) {
		this.dataSubmission = dataSubmission;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
