package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TableTemplate {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("headers")
	private List<String> headers;
	
	@JsonProperty("rowKey")
	private String rowKey;
	
	@JsonProperty("columnMapper")
	private ColumnMapper columnMapper;
	
	@JsonProperty("data")
	private List<Object> data;
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public List<String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	
	public String getRowKey() {
		return rowKey;
	}
	
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	
	public ColumnMapper getColumnMapper() {
		return columnMapper;
	}
	
	public void setColumnMapper(ColumnMapper columnMapper) {
		this.columnMapper = columnMapper;
	}
	
	public List<Object> getData() {
		return data;
	}
	
	public void setData(List<Object> data) {
		this.data = data;
	}
}
