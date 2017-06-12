package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.UUID;

public class PluginInfoDaoUtil {
	
	public PluginInfoDaoUtil(UUID pluginId, String pluginName, String pluginDescription, String pluginMetadata) {
		super();
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.pluginDescription = pluginDescription;
		this.pluginMetadata = pluginMetadata;
	}
	
	
	
	public PluginInfoDaoUtil(UUID pluginId, String pluginName, String pluginDescription) {
		super();
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.pluginDescription = pluginDescription;
	}

	private UUID pluginId = null;
	private String pluginName = "";
	private String pluginDescription = "";
	private String pluginMetadata = "";
	
	public String getPluginMetadata() {
		return pluginMetadata;
	}
	public void setPluginMetadata(String pluginMetadata) {
		this.pluginMetadata = pluginMetadata;
	}
	public UUID getPluginId() {
		return pluginId;
	}
	public void setPluginId(UUID pluginId) {
		this.pluginId = pluginId;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getPluginDescription() {
		return pluginDescription;
	}
	public void setPluginDescription(String pluginDescription) {
		this.pluginDescription = pluginDescription;
	}

}
