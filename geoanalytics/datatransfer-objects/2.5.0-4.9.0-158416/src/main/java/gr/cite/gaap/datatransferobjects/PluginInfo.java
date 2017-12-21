/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.Date;
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
	private String updateDate = null;
	private String creationDate = null;
	private short pluginType = 0;
	private String widgetName = "";
	private String qualifiedNameOfClass = "";
	private String jsFileName = "";
	private String methodName = "";
	private String configurationClass = "";
	
	
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
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public short getPluginType() {
		return pluginType;
	}
	public void setPluginType(short pluginType) {
		this.pluginType = pluginType;
	}
	public String getQualifiedNameOfClass() {
		return qualifiedNameOfClass;
	}
	public void setQualifiedNameOfClass(String qualifiedNameOfClass) {
		this.qualifiedNameOfClass = qualifiedNameOfClass;
	}
	public String getJsFileName() {
		return jsFileName;
	}
	public void setJsFileName(String jsFileName) {
		this.jsFileName = jsFileName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getConfigurationClass() {
		return configurationClass;
	}
	public void setConfigurationClass(String configurationClass) {
		this.configurationClass = configurationClass;
	}
}
