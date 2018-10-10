package org.gcube.data.spd.model.service.types;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.util.Capabilities;

public class MapElements
{
	@XmlElement public Capabilities  key;
	@XmlElement public List<Conditions> value;

	@SuppressWarnings("unused")
	private MapElements() {} //Required by JAXB

	public MapElements(Capabilities key, List<Conditions> value)
	{
		this.key   = key;
		this.value = value;
	}
}