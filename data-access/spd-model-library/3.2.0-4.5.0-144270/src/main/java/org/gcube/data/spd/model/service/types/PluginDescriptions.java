package org.gcube.data.spd.model.service.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.spd.model.PluginDescription;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PluginDescriptions {

	@XmlElement(name="pluginDescriptions")
	private List<PluginDescription> descriptions = new ArrayList<PluginDescription>();
	
	public PluginDescriptions(List<PluginDescription> descriptions) {
		super();
		this.descriptions = descriptions;
	}

	protected PluginDescriptions() {
	}

	public List<PluginDescription> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<PluginDescription> descriptions) {
		this.descriptions = descriptions;
	}
	
}
