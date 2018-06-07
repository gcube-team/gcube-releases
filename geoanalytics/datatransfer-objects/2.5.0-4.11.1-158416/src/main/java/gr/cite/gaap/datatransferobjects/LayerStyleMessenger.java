package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayerStyleMessenger {
	private static Logger logger = LoggerFactory.getLogger(LayerStyleMessenger.class);
	private String theme;
	private String termId;
	private String layerName;
	private String style;
	private Integer minScale;
	private Integer maxScale;
	
	

	public LayerStyleMessenger() {
		super();
		logger.trace("Initialized default contructor for LayerStyleMessenger");
	}

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
