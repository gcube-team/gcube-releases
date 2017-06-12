package gr.cite.gaap.datatransferobjects.plugin;

import org.springframework.util.Assert;

public class PluginUploadMessenger {
	
	private String name = null;
	private String description = null;
	private String widgetName = null;
	private String className = null;
	private String methodName = null;
	private String jsFileName = null;
	private String configurationClass = null;
	private int type = 0;
	
	public String getName() {
		return name.trim();
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description.trim();
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWidgetName() {
		return widgetName.trim();
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	public String getClassName() {
		return className.trim();
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName.trim();
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getJsFileName() {
		return jsFileName.trim();
	}
	public void setJsFileName(String jsFileName) {
		this.jsFileName = jsFileName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getConfigurationClass() {
		return configurationClass;
	}
	public void setConfigurationClass(String configurationClass) {
		this.configurationClass = configurationClass;
	}
	public void validate() throws Exception {
		try {
			Assert.notNull(name, "Plugin name cannot be empty");
			Assert.hasLength(name, "Plugin name cannot be empty");
			Assert.notNull(description, "Plugin description cannot be empty");
			Assert.hasLength(description, "Plugin description cannot be empty");
			Assert.notNull(widgetName, "Plugin widget name cannot be empty");
			Assert.hasLength(widgetName, "Plugin widget name cannot be empty");
			Assert.notNull(className, "Plugin class name cannot be empty");
			Assert.hasLength(className, "Plugin class name cannot be empty");
			Assert.notNull(methodName, "Plugin method name cannot be empty");
			Assert.hasLength(methodName, "Plugin method name cannot be empty");
			Assert.notNull(jsFileName, "Plugin javascript file name cannot be empty");
			Assert.hasLength(jsFileName, "Plugin javascript file name cannot be empty");
			Assert.notNull(configurationClass, "Plugin configuration Class cannot be empty");
			Assert.hasLength(configurationClass, "Plugin configuration Class cannot be empty");
		} catch(Exception e){
			throw new Exception();
		}
	}
}
