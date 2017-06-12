package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.LayerStyle;

public class LayerStyleUpdate {
	private static Logger logger = LoggerFactory.getLogger(LayerStyleUpdate.class);
	private String name = null;
	private String origName = null;
	private String style = null;

	public LayerStyleUpdate() {
		logger.trace("Initialized default contructor for LayerStyleUpdate");
	}

	public LayerStyleUpdate(LayerStyle ls) {
		logger.trace("Initializing LayerStyleUpdate...");
		this.name = ls.getName();
		this.style = ls.getStyle();
		logger.trace("Initialized LayerStyleUpdate");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrigName() {
		return origName;
	}

	public void setOrigName(String origName) {
		this.origName = origName;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
