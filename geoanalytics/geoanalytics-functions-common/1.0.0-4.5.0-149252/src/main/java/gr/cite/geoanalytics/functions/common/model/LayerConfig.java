package gr.cite.geoanalytics.functions.common.model;

public class LayerConfig {

	
	private String requiredLayerName;
	private String layerID;
//	private String layerType;
//	private String layerDataType;
	
	
	public LayerConfig(String requiredLayerName, String layerID) {
		this.requiredLayerName = requiredLayerName;
		this.layerID = layerID;
	}
	public LayerConfig() {	}
	public String getRequiredLayerName() {
		return requiredLayerName;
	}
	public void setRequiredLayerName(String requiredLayerName) {
		this.requiredLayerName = requiredLayerName;
	}
	public String getLayerID() {
		return layerID;
	}
	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}
	
	
	
	
	
	
}
