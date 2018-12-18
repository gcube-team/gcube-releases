/**
 * 
 */
package gr.cite.geoanalytics.dataaccess.json;

/**
 * @author vfloros
 *
 */
public class FeatureinfoResponse {
	private String type;
	private String totalFeatures;
	private Object[] features;
	private Object crs;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTotalFeatures() {
		return totalFeatures;
	}
	public void setTotalFeatures(String totalFeatures) {
		this.totalFeatures = totalFeatures;
	}
	public Object[] getFeatures() {
		return features;
	}
	public void setFeatures(Object[] features) {
		this.features = features;
	}
	public Object getCrs() {
		return crs;
	}
	public void setCrs(Object crs) {
		this.crs = crs;
	}
}
