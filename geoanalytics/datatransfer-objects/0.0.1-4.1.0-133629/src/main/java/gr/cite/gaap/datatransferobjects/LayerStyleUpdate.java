package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.LayerStyle;

public class LayerStyleUpdate {
	private String name = null;
	private String origName = null;
	private String style = null;

	public LayerStyleUpdate() {
	}

	public LayerStyleUpdate(LayerStyle ls) {
		this.name = ls.getName();
		this.style = ls.getStyle();
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
