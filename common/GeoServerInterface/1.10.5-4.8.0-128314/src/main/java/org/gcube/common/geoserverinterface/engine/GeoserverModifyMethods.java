package org.gcube.common.geoserverinterface.engine;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.geoserverinterface.HttpMethodCall;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.json.JSONArray;
import org.gcube.common.geoserverinterface.json.JSONObject;


public class GeoserverModifyMethods {

	/**
	 * @uml.property  name="hMC"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private HttpMethodCall HMC= null;
	/**
	 * @uml.property  name="geoserverGetMethods"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GeoserverGetMethods geoserverGetMethods;
	
	public GeoserverModifyMethods(HttpMethodCall hMC) {
		super();
		HMC = hMC;
		geoserverGetMethods = new GeoserverGetMethods(hMC);
	}
	
	public boolean modifyLayersGroup(GroupRest group) throws Exception {
		Map<String, Object> m = new HashMap<String, Object>();
		
		GroupRest tmp = geoserverGetMethods.getLayerGroup(group.getName());
		
		if (group.getLayers().size() > 0) {
			JSONArray a = new JSONArray();
			for (String layer: group.getLayers()) {
				JSONObject n = new JSONObject();
				n.put("name",layer);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("layer",a);
			m.put("layers", l);
		} else {
			JSONArray a = new JSONArray();
			for (String layer: tmp.getLayers()) {
				JSONObject n = new JSONObject();
				n.put("name",layer);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("layer",a);
			m.put("layers", l);
		}
		
		if (group.getStyles().size() > 0) {
			JSONArray a = new JSONArray();
			for (String style: group.getStyles()) {
				JSONObject n = new JSONObject();
				n.put("name",style);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("style",a);
			m.put("styles", l);
		} else {
			JSONArray a = new JSONArray();
			for (String style: tmp.getStyles()) {
				JSONObject n = new JSONObject();
				n.put("name",style);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("style",a);
			m.put("styles", l);
		}
		if (group.getBounds() != null) {
			JSONObject bounds = new JSONObject();
			bounds.put("minx", group.getBounds().getMinx());
			bounds.put("maxx", group.getBounds().getMaxx());
			bounds.put("miny", group.getBounds().getMiny());
			bounds.put("maxy", group.getBounds().getMaxy());
			bounds.put("crs", group.getBounds().getCrs());
			
			m.put("bounds", bounds);	
		} else {
			JSONObject bounds = new JSONObject();
			bounds.put("minx", tmp.getBounds().getMinx());
			bounds.put("maxx", tmp.getBounds().getMaxx());
			bounds.put("miny", tmp.getBounds().getMiny());
			bounds.put("maxy", tmp.getBounds().getMaxy());
			bounds.put("crs", tmp.getBounds().getCrs());
			
			m.put("bounds", bounds);	
		}
		
		JSONObject j = new JSONObject();
		j.put("layerGroup", m);
		
		HMC.CallPut("rest/layergroups/" + group.getName(), j.toString(), "application/json");
		
		return true;
	}
	
	public boolean modifyStyleSDL(String schemaName, String xmlSdl) throws Exception {
		if (HMC.CallPut("rest/styles/" + schemaName, xmlSdl, "application/vnd.ogc.sld+xml") == null) return false;
		
		return true;
	}
}
