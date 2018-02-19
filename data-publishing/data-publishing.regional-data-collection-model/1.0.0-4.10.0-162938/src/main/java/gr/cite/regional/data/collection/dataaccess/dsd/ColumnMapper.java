package gr.cite.regional.data.collection.dataaccess.dsd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ColumnMapper {
	@JsonProperty("columns")
	private List<Column> columns;
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
}
