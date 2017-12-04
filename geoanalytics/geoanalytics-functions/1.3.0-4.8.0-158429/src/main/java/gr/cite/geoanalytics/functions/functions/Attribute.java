package gr.cite.geoanalytics.functions.functions;

public class Attribute {
	private String name = null;
	private Object value = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Attribute() {
	
	}
	
	public Attribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}
