package gr.cite.gaap.datatransferobjects.layeroperations;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author vfloros
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureInfo {
	private CRS crs;
	private List<Feature> features;
	private String totalFeatures;
	private String type;

	public FeatureInfo() {}
	
	public FeatureInfo(CRS crs, List<Feature> features, String totalFeatures, String type) {
		super();
		this.crs = crs;
		this.features = features;
		this.totalFeatures = totalFeatures;
		this.type = type;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public CRS getCrs() {
		return crs;
	}

	public void setCrs(CRS crs) {
		this.crs = crs;
	}

	public String getTotalFeatures() {
		return totalFeatures;
	}

	public void setTotalFeatures(String totalFeatures) {
		this.totalFeatures = totalFeatures;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
