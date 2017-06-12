package gr.cite.gaap.datatransferobjects;


public class WfsRequestLayer {
	private String featureTypes;
	private String layerDescription;
	private String layerName;
	private String style;
	
	
	public String getFeatureTypes() {
		return featureTypes;
	}
	public void setFeatureTypes(String featureTypes) {
		this.featureTypes = featureTypes;
	}
	public String getLayerDescription() {
		return layerDescription;
	}
	public void setLayerDescription(String layerDescription) {
		this.layerDescription = layerDescription;
	}
	public String getLayerName() {
		return layerName;
	}
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	
	
}
