package org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.thoughtworks.xstream.XStream;

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
