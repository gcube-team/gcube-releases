package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataSubmissionDataDto {
	
	@JsonProperty("headers")
	private List<String> headers;
	
	@JsonProperty("data")
	private List<CdtDto> data;
	
	public List<String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	
	public List<CdtDto> getData() {
		return data;
	}
	
	public void setData(List<CdtDto> data) {
		this.data = data;
	}
}
