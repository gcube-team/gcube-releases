/**
 * 
 */
package gr.cite.gaap.datatransferobjects.layeroperations;

import java.util.Map;

/**
 * @author vfloros
 *
 */
public class CRS {
	private String type;
	private Map<String, String> properties;
	
	public CRS() {}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	
}
