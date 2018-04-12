package gr.cite.gaap.datatransferobjects.layeroperations;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vfloros
 *
 */
public class Feature {
	private String type;
	private Map<String, Object> geometry = new HashMap<String, Object>();
	private String geometry_name;
	private String id;
	private Map<String, String> properties = new HashMap<String, String>();

	public Feature() {}

	public Map<String, Object> getGeometry() {
		return geometry;
	}

	public void setGeometry(Map<String, Object> geometry) {
		this.geometry = geometry;
	}

	public String getGeometry_name() {
		return geometry_name;
	}

	public void setGeometry_name(String geometry_name) {
		this.geometry_name = geometry_name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
