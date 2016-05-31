package org.gcube.contentmanagement.timeseries.geotools.gisconnectors;

public class GISLayerInformation {

	
	private String layerName;
	private String defaultStyle;
	private String layerTitle;
	
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getLayerName() {
		return layerName;
	}
	
	public String getDefaultStyle() {
		return defaultStyle;
	}
	public void setDefaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	public String getLayerTitle() {
		return layerTitle;
	}

	public void setLayerTitle(String layerTitle) {
		this.layerTitle = layerTitle;
	}
	
}
