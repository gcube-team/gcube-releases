package gr.cite.gaap.datatransferobjects;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class WfsRequestLayer {

	private String featureTypes;
	private String layerDescription;
	private String layerName;
	private String style;
	boolean publishOnGeoNetwork;

	public boolean isPublishOnGeoNetwork() {
		return publishOnGeoNetwork;
	}

	public void setPublishOnGeoNetwork(boolean publishOnGeoNetwork) { this.publishOnGeoNetwork = publishOnGeoNetwork; }

	public String getFeatureTypes() { return featureTypes; }

	public void setFeatureTypes(String featureTypes) { this.featureTypes = featureTypes; }

	public String getLayerDescription() { return layerDescription; }

	public void setLayerDescription(String layerDescription) { this.layerDescription = layerDescription; }

	public String getLayerName() { return layerName; }

	public void setLayerName(String layerName) { this.layerName = layerName; }

	public String getStyle() { return style; }

	public void setStyle(String style) { this.style = style; }
	
	public void DecodeToUTF8() throws UnsupportedEncodingException {
		this.setLayerName( URLDecoder.decode( this.getLayerName(), "UTF-8" ) );
		this.setLayerDescription( URLDecoder.decode( this.getLayerDescription(), "UTF-8" ) );
	}

}
