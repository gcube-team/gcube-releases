package gr.cite.gaap.datatransferobjects;

public class ExternalLayerDTO {

	private String geoserverUrl;
	private String workspace;
	private String name;

	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	public void setGeoserverUrl(String geoserverUrl) {
		this.geoserverUrl = geoserverUrl;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
