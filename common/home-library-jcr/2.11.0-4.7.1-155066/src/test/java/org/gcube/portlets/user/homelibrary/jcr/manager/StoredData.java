package org.gcube.portlets.user.homelibrary.jcr.manager;

public class StoredData {

	public StoredData(String name, String description, String id, DataProvenance provenance, String creationDate, String operator, String computationId, String type, String payload, String vre) {
		super();
		this.name = name;
		this.id = id;
		this.description = description;
		this.provenance = provenance;
		this.creationDate = creationDate;
		this.operator = operator;
		this.computationId = computationId;
		this.type = type;
		this.vre = vre;
		this.payload=payload;
	}
	String name;
	String description;
	String id;
	DataProvenance provenance;
	String creationDate;
	String operator;
	String computationId;
	String vre;
	String type;
	String payload;
}

