package org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.thoughtworks.xstream.XStream;

public class MapAdapter extends XmlAdapter<MapElements[], Map<String,Object>> {

	private static XStream xstream = new XStream();
	
	public Map<String, Object> unmarshal(MapElements[] v)
			throws Exception {
		Map<String, Object> r = new HashMap<String,Object>();
		for (MapElements mapelement : v){
			r.put(mapelement.key, xstream.fromXML(mapelement.value));
		}
		return r;
	}

	public MapElements[] marshal(Map<String, Object> v)
			throws Exception {
		MapElements[] mapElements = new MapElements[v.size()];
		int i = 0;
		
		for (Map.Entry<String, Object> entry : v.entrySet())
			mapElements[i++] = new MapElements(entry.getKey(), xstream.toXML(entry.getValue()));
		return mapElements;
	}
}