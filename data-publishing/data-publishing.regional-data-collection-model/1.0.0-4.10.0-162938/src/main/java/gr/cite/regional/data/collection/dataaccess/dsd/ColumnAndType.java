package gr.cite.regional.data.collection.dataaccess.dsd;

public class ColumnAndType {
	private String name;
	private String datatype;
	
	public ColumnAndType(String name, String datatype) {
		this.name = name;
		this.datatype = datatype;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDatatype() {
		return datatype;
	}
	
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
}
