package gr.cite.geoanalytics.dataaccess.entities.plugin.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="pluginConfigurationMetadata")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
public class PluginConfigurationMetadata {
	private String param1 = "";
	private String param2 = "";
	
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
}
