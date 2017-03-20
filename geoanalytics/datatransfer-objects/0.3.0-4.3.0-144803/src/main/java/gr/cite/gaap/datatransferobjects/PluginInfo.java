/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

/**
 * @author vfloros
 *
 */
public class PluginInfo {

	public PluginInfo() {}

	private UUID pluginId = null;
	private String pluginName = "";
	private String pluginDescription = "";
	private String widgetName = "";
	
	public String getWidgetName() {
		return widgetName;
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
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
