package gr.cite.geoanalytics.dataaccess.entities.layer;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import gr.cite.geoanalytics.dataaccess.entities.layer.LayerVisualizationData.AttributeLabelAndOrder;

/**
 * @author vfloros
 *
 */
@XmlRootElement
public class LayerVisualizationData {
	private Map<String, LayerVisualizationData.AttributeLabelAndOrder> nameToLabel = new HashMap<String, LayerVisualizationData.AttributeLabelAndOrder>();

	public Map<String, LayerVisualizationData.AttributeLabelAndOrder> getNameToLabel() {
		return nameToLabel;
	}

	public void setNameToLabel(Map<String, LayerVisualizationData.AttributeLabelAndOrder> nameToLabel) {
		this.nameToLabel = nameToLabel;
	}
	
	public static class AttributeLabelAndOrder {
		private String label;
		
		private int order;
		
		public AttributeLabelAndOrder() {
			super();
		}
		public AttributeLabelAndOrder(String label, int order) {
			super();
			this.label = label;
			this.order = order;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		
		public int compareTo(AttributeLabelAndOrder value) {
			return this.getOrder()-value.getOrder();
		}
	}

	public LayerVisualizationData() {
		super();
	}
}