package org.gcube.data.spd.model.service.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.util.Capabilities;

public class MapAdapter extends XmlAdapter<MapElements[], Map<Capabilities, List<Conditions>>> {

	
	public Map<Capabilities, List<Conditions>> unmarshal(MapElements[] v)
			throws Exception {
		Map<Capabilities, List<Conditions>> r = new HashMap<Capabilities, List<Conditions>>();
		for (MapElements mapelement : v){
			if (mapelement.value==null)
				r.put(mapelement.key, new ArrayList<Conditions>());
			else r.put(mapelement.key, mapelement.value);
		}
		return r;
	}

	public MapElements[] marshal(Map<Capabilities, List<Conditions>> v)
			throws Exception {
		MapElements[] mapElements = new MapElements[v.size()];
		int i = 0;
		for (Map.Entry<Capabilities, List<Conditions>> entry : v.entrySet())
			mapElements[i++] = new MapElements(entry.getKey(), entry.getValue());
		return mapElements;
	}
}