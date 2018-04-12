package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.MapAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MapObject<K, V> {

	@XmlJavaTypeAdapter(MapAdapter.class)	
	private Map<K, V> map;

	
	protected MapObject() {}


	public MapObject(Map<K, V> map) {
		super();
		this.map = map;
	}

	public Map<K, V> getMap() {
		return map;
	}

	
}
