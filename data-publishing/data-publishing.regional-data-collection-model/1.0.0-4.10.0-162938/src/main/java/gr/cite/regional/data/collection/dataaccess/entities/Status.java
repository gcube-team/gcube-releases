package gr.cite.regional.data.collection.dataaccess.entities;

public enum Status {
	INACTIVE(0),
	ACTIVE(1);
	
	private final Integer statusCode;
	
	Status(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
	public Integer getStatusCode() {
		return this.statusCode;
	}
}
