package gr.cite.geoanalytics.context;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeoServerBridgeConfig {

	private static Logger log = LoggerFactory.getLogger(GeoServerBridgeConfig.class);

	private static final String geoServerBridgeWorkspaceDefault = "geoanalytics";

	private String geoServerBridgeUrl = null;
	private String geoServerBridgeUser = null;
	private String geoServerBridgePassword = null;
	private String geoServerBridgeWorkspace = geoServerBridgeWorkspaceDefault;

	private DataStoreConfig postgisDataStoreConfig;
	private DataStoreConfig geotiffDataStoreConfig;

	public GeoServerBridgeConfig(String geoServerBridgeUrl, String geoServerBridgeUser, String geoServerBridgePassword) {
		this.geoServerBridgeUrl = geoServerBridgeUrl;
		this.geoServerBridgeUser = geoServerBridgeUser;
		this.geoServerBridgePassword = geoServerBridgePassword;
	}

	public DataStoreConfig getPostgisDataStoreConfig() {
		return postgisDataStoreConfig;
	}

	@Inject
	public void setPostgisDataStoreConfig(DataStoreConfig postgisDataStoreConfig) {
		this.postgisDataStoreConfig = postgisDataStoreConfig;
	}

	public DataStoreConfig getGeotiffDataStoreConfig() {
		return geotiffDataStoreConfig;
	}

	@Inject
	public void setGeotiffDataStoreConfig(DataStoreConfig geotiffDataStoreConfig) {
		this.geotiffDataStoreConfig = geotiffDataStoreConfig;
	}

	public String getGeoServerBridgeUrl() {
		return geoServerBridgeUrl;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.url}")
	public void setGeoServerBridgeUrl(String geoServerBridgeUrl) {
		log.trace("Setting geoserver url: " + geoServerBridgeUrl);
		this.geoServerBridgeUrl = geoServerBridgeUrl;
	}

	public String getGeoServerBridgeUser() {
		return geoServerBridgeUser;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.user}")
	public void setGeoServerBridgeUser(String geoServerBridgeUser) {
		this.geoServerBridgeUser = geoServerBridgeUser;
	}

	public String getGeoServerBridgePassword() {
		return geoServerBridgePassword;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.pass}")
	public void setGeoServerBridgePass(String geoServerBridgePassword) {
		this.geoServerBridgePassword = geoServerBridgePassword;
	}

	public String getGeoServerBridgeWorkspace() {
		return geoServerBridgeWorkspace;
	}

	@Value("${gr.cite.geoanalytics.dataaccess.geoServerBridge.workspace:" + geoServerBridgeWorkspaceDefault + "}")
	public void setGeoServerBridgeWorkspace(String geoServerBridgeWorkspace) {
		this.geoServerBridgeWorkspace = geoServerBridgeWorkspace;
	}

}
