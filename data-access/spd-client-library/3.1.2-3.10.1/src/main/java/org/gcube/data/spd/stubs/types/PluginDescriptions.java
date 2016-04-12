package org.gcube.data.spd.stubs.types;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class PluginDescriptions {

	@XmlElement(name="pluginDescriptions")
	private List<String> descriptions;

	public List<String> getDescriptions() {
		return descriptions;
	}
	
	

}
