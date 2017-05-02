/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

/**
 * @author vfloros
 *
 */
public class PluginMessenger {
	private UserinfoObject userinfoObject = null;
	private String pluginName = "";

	public PluginMessenger() {}

	public PluginMessenger(UserinfoObject userinfoObject, String pluginName) {
		super();
		this.userinfoObject = userinfoObject;
		this.pluginName = pluginName;
	}

	public UserinfoObject getUserinfoObject() {
		return userinfoObject;
	}

	public void setUserinfoObject(UserinfoObject userinfoObject) {
		this.userinfoObject = userinfoObject;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
}
