package org.gcube.vremanagement.executor.api.types.adapter;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.thoughtworks.xstream.XStream;

/**
 * @author Lucio Leii (ISTI - CNR)
 */
public class MapAdapter extends XmlAdapter<String, Map<String, Object>>{

	private static XStream xstream = new XStream();
	
	@Override
	public String marshal(Map<String, Object> oi) throws Exception {
		return xstream.toXML(oi);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> unmarshal(String oi) throws Exception {
		return (Map<String, Object>) xstream.fromXML(oi);
	}

}
