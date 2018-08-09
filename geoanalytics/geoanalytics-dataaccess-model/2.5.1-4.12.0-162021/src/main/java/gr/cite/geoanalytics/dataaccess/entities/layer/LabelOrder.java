package gr.cite.geoanalytics.dataaccess.entities.layer;

public class LabelOrder {
	
	private String label;
	private int order;
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}