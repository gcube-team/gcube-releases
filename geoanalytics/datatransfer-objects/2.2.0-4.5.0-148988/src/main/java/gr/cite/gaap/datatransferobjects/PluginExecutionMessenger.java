package gr.cite.gaap.datatransferobjects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PluginExecutionMessenger {

	public PluginExecutionMessenger() {}
	
	private UserinfoObject userInfoObject = null;
	
	private UUID pluginId = null;
	
	private UUID projectId = null;
	
	private Map<String, Object> parameters = new HashMap<String, Object>();

	public UserinfoObject getUserInfoObject() {
		return userInfoObject;
	}

	public void setUserInfoObject(UserinfoObject userInfoObject) {
		this.userInfoObject = userInfoObject;
	}

	public UUID getPluginId() {
		return pluginId;
	}

	public void setPluginId(UUID pluginId) {
		this.pluginId = pluginId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
