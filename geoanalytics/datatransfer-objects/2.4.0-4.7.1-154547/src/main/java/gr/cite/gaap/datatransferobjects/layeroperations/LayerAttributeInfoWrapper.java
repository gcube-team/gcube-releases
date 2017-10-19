package gr.cite.gaap.datatransferobjects.layeroperations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author vfloros
 *
 */
public class LayerAttributeInfoWrapper {
	private UUID layerID = null;
	
	private LayerAttributeInfo[] layerAttrs;

	public UUID getLayerID() {
		return layerID;
	}

	public void setLayerID(UUID layerID) {
		this.layerID = layerID;
	}

	public LayerAttributeInfo[] getLayerAttrs() {
		return layerAttrs;
	}

	public void setLayerAttrs(LayerAttributeInfo[] layerAttrs) {
		this.layerAttrs = layerAttrs;
	}
	
}
