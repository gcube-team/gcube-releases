/**
 * 
 */
package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.UUID;

/**
 * @author vfloros
 *
 */
public class LayerTagInfo {
	private UUID id = null;
	private String name = "";
	
	public LayerTagInfo(UUID id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
