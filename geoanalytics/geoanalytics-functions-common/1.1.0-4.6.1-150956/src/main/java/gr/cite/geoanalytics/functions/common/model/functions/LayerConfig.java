package gr.cite.geoanalytics.functions.common.model.functions;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class LayerConfig implements Serializable {

	private static final long serialVersionUID = 1468207451843929707L;
	
	
	private final String objectID;   	   	//THIS SHOULD BE FINAL - DO NOT CHANGE
	private final String captionForUser;   //THIS SHOULD BE FINAL - DO NOT CHANGE
	private String layerID;
	private String layerDescription;
//	private String layerType;
//	private String layerDataType;
	
	

	/**
	 * DO NOT MAKE PUBLIC
	 * No others apart from the Functions which extend the FunctionLayerConfigBase should be able to create LayerConfigs
	 */
	protected LayerConfig(String objectID, String captionForUser) {
		this(objectID, captionForUser, null);
	}
	
	
	/**
	 * DO NOT MAKE PUBLIC
	 * No others apart from the Functions which extend the FunctionLayerConfigBase should be able to create LayerConfigs
	 */
	protected LayerConfig(String objectID, String captionForUser, String layerDescription) {
		this.objectID = objectID;
		this.captionForUser = captionForUser;
		this.layerDescription = layerDescription;
	}
	
	public String getObjectID(){
		return objectID;
	}
	
	public String getCaptionForUser() {
		return captionForUser;
	}
	
	public String getLayerID() {
		return layerID;
	}
	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}
	public String getLayerDescription() {
		return layerDescription;
	}
	public void setLayerDescription(String layerDescription) {
		this.layerDescription = layerDescription;
	}
	
	
	@Override
    public int hashCode() {
        return Objects.hash(objectID);
    }
	
	
	@Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof LayerConfig)) return false;
        LayerConfig lc = (LayerConfig) o;
        return Objects.equals(objectID, lc.objectID);
    }
	
	
	@Override
	public String toString(){
		return "[objectID: "+objectID+" captionForUser: "+captionForUser+" layerID: "+layerID+" layerDescription: "+layerDescription+"]";
	}
	
}
