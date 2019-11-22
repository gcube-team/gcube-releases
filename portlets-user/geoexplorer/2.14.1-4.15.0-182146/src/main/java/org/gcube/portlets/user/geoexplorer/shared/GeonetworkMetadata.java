package org.gcube.portlets.user.geoexplorer.shared;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;

@Entity
public class GeonetworkMetadata implements FetchingElement{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId;

	private String uuid;
	private String schema;


	private Long metadataId;

	public GeonetworkMetadata(){
		
	}
	public GeonetworkMetadata(String uuid, String schema, Long metadataId) {
		this.uuid = uuid;
		this.schema = schema;
	}

	public int getInternalId() {
		return internalId;
	}

	public void setInternalId(int internalId) {
		this.internalId = internalId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public String getId() {
		return getUuid();
	}

	public void setMetadataId(Long metadataId) {
		this.metadataId = metadataId;
	}

	public Long getMetadataId() {
		return metadataId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkMetadata [internalId=");
		builder.append(internalId);
		builder.append(", uuid=");
		builder.append(uuid);
		builder.append(", schema=");
		builder.append(schema);
		builder.append(", metadataId=");
		builder.append(metadataId);
		builder.append("]");
		return builder.toString();
	}

	
}
