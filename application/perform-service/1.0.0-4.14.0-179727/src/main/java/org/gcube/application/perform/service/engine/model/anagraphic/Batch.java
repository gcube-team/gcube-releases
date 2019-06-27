package org.gcube.application.perform.service.engine.model.anagraphic;

import java.util.UUID;

public class Batch {

	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getFarmId() {
		return farmId;
	}
	public void setFarmId(Long farmId) {
		this.farmId = farmId;
	}
	
	public Batch() {
		// TODO Auto-generated constructor stub
	}
	public Batch(Long id, UUID uuid, String name, String type, Long farmId) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.type = type;
		this.farmId = farmId;
	}
	private Long id;
	private UUID uuid;
	private String name;
	private String type;
	private Long farmId;
	
}
