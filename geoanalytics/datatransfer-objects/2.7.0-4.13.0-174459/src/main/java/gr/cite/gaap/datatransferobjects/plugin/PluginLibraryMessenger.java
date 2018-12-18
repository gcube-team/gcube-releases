package gr.cite.gaap.datatransferobjects.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class PluginLibraryMessenger {
	private String pluginLibraryName = null;
	
	private List<PluginUploadMessenger> pluginMessengers = new ArrayList<PluginUploadMessenger>();

	public String getPluginLibraryName() {
		return pluginLibraryName.trim();
	}

	public void setPluginLibraryName(String pluginLibraryName) {
		this.pluginLibraryName = pluginLibraryName;
	}

	public List<PluginUploadMessenger> getPluginMessengers() {
		return pluginMessengers;
	}

	public void setPluginMessengers(List<PluginUploadMessenger> pluginMessengers) {
		this.pluginMessengers = pluginMessengers;
	}
	
	public void validate() throws Exception {
		try{

			Assert.notNull(pluginLibraryName, "Plugin library name cannot be empty");
			Assert.hasLength(pluginLibraryName, "Plugin library name cannot be empty");

			for(PluginUploadMessenger pum : pluginMessengers){
				pum.validate();
			}
		} catch(Exception e){
			throw new Exception("Error when validatin plugin entries!!");
		}
	}
}
