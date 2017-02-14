package gr.cite.gaap.datatransferobjects.user;

import java.util.UUID;

public class RoleMessenger {

	private String name = null;
	private UUID id = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
}