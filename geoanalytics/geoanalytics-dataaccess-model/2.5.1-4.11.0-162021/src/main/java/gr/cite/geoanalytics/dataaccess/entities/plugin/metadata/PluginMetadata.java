package gr.cite.geoanalytics.dataaccess.entities.plugin.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="pluginMetadata")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
public class PluginMetadata {
	private String jsFileName = "";
	private String widgetName = "";
	private String qualifiedNameOfClass = "";
	private String methodName = "";
	private String configurationClass = "";
	
	@XmlElement(required = false)
	public String getJsFileName() {
		return jsFileName;
	}
	public void setJsFileName(String jsFileName) {
		this.jsFileName = jsFileName;
	}
	
	@XmlElement(required = false)
	public String getWidgetName() {
		return widgetName;
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	
	@XmlElement(required = false)
	public String getQualifiedNameOfClass() {
		return qualifiedNameOfClass;
	}
	public void setQualifiedNameOfClass(String qualifiedNameOfClass) {
		this.qualifiedNameOfClass = qualifiedNameOfClass;
	}

	@XmlElement(required = false)
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	@XmlElement(required = false)
	public String getConfigurationClass() {
		return configurationClass;
	}
	public void setConfigurationClass(String configurationClass) {
		this.configurationClass = configurationClass;
	}
	
	
}
