/**
 * 
 */
package gr.cite.gaap.datatransferobjects.layeroperations;

import java.util.UUID;

/**
 * @author vfloros
 *
 */
public class LayerAttributeInfo {
	private String attributeName;
	private String attributeLabel;
	private int attributeAppearanceOrder;
	
	public LayerAttributeInfo(){}
	
	public LayerAttributeInfo(String attributeName, String attributeLabel, int attributeAppearanceOrder) {
		super();
		this.attributeName = attributeName;
		this.attributeLabel = attributeLabel;
		this.attributeAppearanceOrder = attributeAppearanceOrder;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeLabel() {
		return attributeLabel;
	}

	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	public int getAttributeAppearanceOrder() {
		return attributeAppearanceOrder;
	}

	public void setAttributeAppearanceOrder(int attributeAppearanceOrder) {
		this.attributeAppearanceOrder = attributeAppearanceOrder;
	}
}
