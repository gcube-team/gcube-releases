/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.Collection;
import java.util.List;

/**
 * @author vfloros
 *
 */
public class LayerMessengerForAdminPortlet {
	private String id = null;
	private String name = "";
	private String description = "";
	private String creator = "";
	private String created = "";
	private String geocodeSystem = "";
	private String Status = "";
	private short isTemplate;
	private String style = "";
	private int replicationFactor = 0;
	private Collection<String> tags;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getGeocodeSystem() {
		return geocodeSystem;
	}
	public void setGeocodeSystem(String geocodeSystem) {
		this.geocodeSystem = geocodeSystem;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public short getIsTemplate() {
		return isTemplate;
	}
	public void setIsTemplate(short isTemplate) {
		this.isTemplate = isTemplate;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public int getReplicationFactor() {
		return replicationFactor;
	}
	public void setReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
	}
	public Collection<String> getTags() {
		return tags;
	}
	public void setTags(Collection<String> tags) {
		this.tags = tags;
	}
	
}
