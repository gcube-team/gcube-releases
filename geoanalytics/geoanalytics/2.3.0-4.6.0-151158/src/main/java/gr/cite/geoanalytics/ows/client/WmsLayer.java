package gr.cite.geoanalytics.ows.client;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;

public class WmsLayer {

	private String name;
	private String workspace;
	private String geoserverUrl;
	private String title;
	private String description;
	private String style;
	private String url;

	private List<String> keywordList;
	private LayerBounds latLongBoundingBox;

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	public void setGeoserverUrl(String geoserverUrl) {
		this.geoserverUrl = geoserverUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}

	public LayerBounds getLatLongBoundingBox() {
		return latLongBoundingBox;
	}

	public void setLatLongBoundingBox(LayerBounds latLongBoundingBox) {
		this.latLongBoundingBox = latLongBoundingBox;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String toString() {
		return "WmsLayer[name=" + name + ", title=" + title + ", description=" + description + ", style=" + style + ", latLongBoundingBox=" + latLongBoundingBox + "]";
	}

	public String getWmsBaseUrl() {
		return geoserverUrl + "wms?layers=" + workspace + ":" + name + "&query_layers=" + workspace + ":" + name;
	}
}
