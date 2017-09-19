package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.Date;
import java.util.UUID;

public class PluginInfoDaoUtil {
	
	public PluginInfoDaoUtil(UUID pluginId, String pluginName, String pluginDescription, String pluginMetadata) {
		super();
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.pluginDescription = pluginDescription;
		this.pluginMetadata = pluginMetadata;
	}
	
	public PluginInfoDaoUtil(
			UUID pluginId, String pluginName,
			String pluginDescription, Date updateDate, 
			Date creationDate, short pluginType) {
		super();
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.pluginDescription = pluginDescription;
		this.updateDate = updateDate;
		this.creationDate = creationDate;
		this.pluginType = pluginType;
	}

	private UUID pluginId = null;
	private String pluginName = "";
	private String pluginDescription = "";
	private String pluginMetadata = "";
	private Date updateDate = null;
	private Date creationDate = null;
	private short pluginType = 0;
	
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
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public short getPluginType() {
		return pluginType;
	}
	public void setPluginType(short pluginType) {
		this.pluginType = pluginType;
	}
}
