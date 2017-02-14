package gr.cite.gaap.datatransferobjects;

public class LayerStyleMessenger {
	private String theme;
	private String termId;
	private String layerName;
	private String style;
	private Integer minScale;
	private Integer maxScale;

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
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

	public Integer getMinScale() {
		return minScale;
	}

	public void setMinScale(Integer minScale) {
		this.minScale = minScale;
	}

	public Integer getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(Integer maxScale) {
		this.maxScale = maxScale;
	}
}
