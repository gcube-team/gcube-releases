package org.gcube.common.geoserverinterface.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.geoserverinterface.HttpMethodCall;
import org.gcube.common.geoserverinterface.HttpResourceControl;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.common.geoserverinterface.json.JSONArray;
import org.gcube.common.geoserverinterface.json.JSONException;
import org.gcube.common.geoserverinterface.json.JSONObject;


public class GeoserverPutMethods {
	
	/**
	 * @uml.property  name="hMC"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private HttpMethodCall HMC= null;
	
	public GeoserverPutMethods(HttpMethodCall hMC) {
		super();
		HMC = hMC;
	}
	
	public boolean addLayersGroup(GroupRest group) throws Exception {
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", group.getName());
		
		{
			JSONArray a = new JSONArray();
			for (String layer: group.getLayers()) {
				JSONObject n = new JSONObject();
				n.put("name",layer);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("layer",a);
			m.put("layers", l);
		}
		{
			JSONArray a = new JSONArray();
			for (String style: group.getStyles()) {
				JSONObject n = new JSONObject();
				n.put("name",style);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("style",a);
			m.put("styles", l);
		}
		{
			JSONObject bounds = new JSONObject();
			bounds.put("minx", group.getBounds().getMinx());
			bounds.put("maxx", group.getBounds().getMaxx());
			bounds.put("miny", group.getBounds().getMiny());
			bounds.put("maxy", group.getBounds().getMaxy());
			bounds.put("crs", group.getBounds().getCrs());
			
			m.put("bounds", bounds);
			
		}
		
		JSONObject j = new JSONObject();
		j.put("layerGroup", m);
		
		HMC.CallPost("rest/layergroups", j.toString(), "application/json");
		
		return true;
	}
	
	public boolean addFeatureType(FeatureTypeRest featureTypeRest) throws Exception {
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", featureTypeRest.getName());
		m.put("nativeName", featureTypeRest.getNativeName());
		m.put("title", featureTypeRest.getTitle());
		//m.put("nativeCRS", featureTypeRest.getNativeCRS());
		m.put("srs", featureTypeRest.getSrs());
		m.put("projectionPolicy", featureTypeRest.getProjectionPolicy());
		m.put("enabled", featureTypeRest.isEnabled());
		
		{
			JSONObject bounds = new JSONObject();
			bounds.put("minx", featureTypeRest.getNativeBoundingBox().getMinx());
			bounds.put("maxx", featureTypeRest.getNativeBoundingBox().getMaxx());
			bounds.put("miny", featureTypeRest.getNativeBoundingBox().getMiny());
			bounds.put("maxy", featureTypeRest.getNativeBoundingBox().getMaxy());
			bounds.put("crs", featureTypeRest.getNativeBoundingBox().getCrs());
			
			m.put("nativeBoundingBox", bounds);
			
		}
		
		{
			JSONObject bounds = new JSONObject();
			bounds.put("minx", featureTypeRest.getLatLonBoundingBox().getMinx());
			bounds.put("maxx", featureTypeRest.getLatLonBoundingBox().getMaxx());
			bounds.put("miny", featureTypeRest.getLatLonBoundingBox().getMiny());
			bounds.put("maxy", featureTypeRest.getLatLonBoundingBox().getMaxy());
			bounds.put("crs", featureTypeRest.getLatLonBoundingBox().getCrs());
			
			m.put("latLonBoundingBox", bounds);
			
		}
		/*
		{"featureType":
			{"name":"world",
				"nativeName":"world",
				"namespace":{	"name":"aquamaps",
								"href":"http:\/\/geoserver.d4science-ii.research-infrastructures.eu:8080\/geoserver\/rest\/namespaces\/aquamaps.json"
							},
				"title":"world",
				"nativeCRS":"GEOGCS[\"WGS 84\", \n  DATUM[\"World Geodetic System 1984\", \n    SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], \n    AUTHORITY[\"EPSG\",\"6326\"]], \n  PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], \n  UNIT[\"degree\", 0.017453292519943295], \n  AXIS[\"Geodetic longitude\", EAST], \n  AXIS[\"Geodetic latitude\", NORTH], \n  AUTHORITY[\"EPSG\",\"4326\"]]",
				"srs":"EPSG:4326",
				"nativeBoundingBox":{"minx":-180,"maxx":180,"miny":-85.5,"maxy":90,"crs":"EPSG:4326"},
				"latLonBoundingBox":{"minx":-180,"maxx":180,"miny":-85.5,"maxy":90,"crs":"EPSG:4326"},
				"projectionPolicy":"FORCE_DECLARED",
				"enabled":true,
				"store":{"@class":"dataStore",
						"name":"aquamapsdb",
						"href":"http:\/\/geoserver.d4science-ii.research-infrastructures.eu:8080\/geoserver\/rest\/workspaces\/aquamaps\/datastores\/aquamapsdb.json"},"maxFeatures":0,"numDecimals":0}}

		*/
		JSONObject j = new JSONObject();
		j.put("featureType", m);
		HMC.CallPost("rest/workspaces/"+featureTypeRest.getWorkspace()+"/datastores/"+featureTypeRest.getDatastore()+"/featuretypes", j.toString(), "application/json");
		
		/**
		 * Add by Francesco
		 */
		
		//Polling on feature type  
		if(HMC.isAvailableFeatureType("rest/workspaces/"+featureTypeRest.getWorkspace()+"/datastores/"+featureTypeRest.getDatastore()+"/featuretypes/" + featureTypeRest.getName() + ".json", 1, 8))
			return true;
		else
			return false; //feature type not found
				
	}
	
	public boolean addLayer(LayerRest layerRest) throws Exception {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", layerRest.getName());
		m.put("type", layerRest.getName());
		m.put("enabled", layerRest.isEnabled());

		{
			JSONObject defaultStyle = new JSONObject();
			defaultStyle.put("name", layerRest.getDefaultStyle());
			m.put("defaultStyle", defaultStyle);
		}
		
		if (layerRest.getStyles().size() > 0) {
			JSONArray a = new JSONArray();
			for (String layer: layerRest.getStyles()) {
				JSONObject n = new JSONObject();
				n.put("name",layer);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("style",a);
			m.put("styles", l);
		}
		
		JSONObject j = new JSONObject();
		j.put("layer", m);
		HMC.CallPut("rest/layers/"+ layerRest.getName(), j.toString(), "application/json");
		return true;
	}
	
	/**
	 * @param string
	 * @param geotiffType
	 * @param b
	 * @param workspace
	 * @param urlFile
	 */
	public void addCoverageStore(final String storeName, final String type, final boolean enabled,
			final String workspace, final String urlFile) throws Exception {
		
		JSONObject jsObj = new JSONObject();
		jsObj.put("coverageStore", new HashMap<String, Object>(){{
			put("name", storeName);
			put("type", type);
			put("enabled", enabled);
			put("workspace", new HashMap<String, Object>(){{
				put("name", workspace);
				put("href", HMC.getUrlservice() + "/rest/workspaces/" + workspace + ".json");
			}});
			put("url", urlFile);
		}});

		System.out.println("JSOBJ:\n"+jsObj.toString());
		String ris = HMC.CallPost("rest/workspaces/"+workspace+"/coveragestores", jsObj.toString(), "application/json");
		System.out.println("RIS: \n"+ris);
	}
	
//	public void addCoverage(final String name, final String title, final String description, final String workspace, final String coverageStore) throws Exception {		
//		JSONObject jsCoverage = new JSONObject();
//		jsCoverage.put("coverage", new HashMap<String, Object>(){{
//			put("name", name);
//			put("nativeName", name);
//			put("namespace", new HashMap<String, Object>(){{
//				put("name", workspace);
//				put("href", HMC.getUrlservice() + "/rest/namespaces/" + workspace + ".json");
//			}});
//			put("title", title);
//			put("description", description);
//			put("keywords", new HashMap<String, Object>(){{
//				put("string", new JSONArray(){{
//					put("WCS");
//					put("GeoTIFF");
//					put(name);
//				}});
//			}});
//			put("nativeCRS", "GEOGCS[\"WGS 84\", \n  DATUM[\"World Geodetic System 1984\", \n    SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], \n    AUTHORITY[\"EPSG\",\"6326\"]], \n  PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], \n  UNIT[\"degree\", 0.017453292519943295], \n  AXIS[\"Geodetic longitude\", EAST], \n  AXIS[\"Geodetic latitude\", NORTH], \n  AUTHORITY[\"EPSG\",\"4326\"]]");
//			put("srs", "EPSG:4326");
//			put("nativeBoundingBox", new HashMap<String, Object>(){{
//		        put("minx", -180);
//		        put("maxx", 180);
//		        put("miny", -60);
//		        put("maxy", 90);
//		        put("crs","EPSG:4326");
//			}});
//			put("latLonBoundingBox", new HashMap<String, Object>(){{
//		        put("minx", -180);
//		        put("maxx", 180);
//		        put("miny", -60);
//		        put("maxy", 90);
//		        put("crs","EPSG:4326");
//			}});
//			put("projectionPolicy", "REPROJECT_TO_DECLARED");
//			put("enabled", true);
//			put("metadata", new HashMap<String, Object>(){{
//				put("entry", new JSONArray(){{
//					put(new HashMap<String, String>(){{
//						put("@key", "cachingEnabled");
//						put("$", "false");
//					}});
//					put(new HashMap<String, String>(){{
//						put("@key", "dirName");
//						put("$", coverageStore+"_"+name); // TODO check layer name or filename 
//					}});
//				}});
//			}});
//			put("store", new HashMap<String, Object>(){{
//				put("@class", "coverageStore");
//				put("name", coverageStore);
//				put("href", HMC.getUrlservice() + "/rest/workspaces/"+workspace+"/coveragestores/"+coverageStore+".json");
//			}});
//			put("nativeFormat", "GeoTIFF");
////			put("grid", new HashMap<String, Object>(){{
////				put("@dimension", "2");
////				put("range", new HashMap<String, Object>(){{
////					put("low", "0 0");
////					put("high", "43200 18000");
////				}});
////				put("transform", new HashMap<String, Object>(){{
////					put("scaleX", 0.0083333333333333);
////			        put("scaleY", -0.0083333333333333);
////	                put("shearX", 0);
////	                put("shearY", 0);
////	                put("translateX", -179.99583333333334);
////	                put("translateY", 89.99583333333274);
////				}});
////				put("crs", "EPSG:4326");
////			}});
////			put("supportedFormats", new HashMap<String, Object>(){{
////				put("string", new String[]{"GEOTIFF", "GIF", "PNG", "JPEG", "TIFF"});
////			}});
////			put("interpolationMethods", new HashMap<String, Object>(){{
////				put("string", new String[]{"bilinear", "bicubic"});
////			}});
//			put("dimensions", new HashMap<String, Object>(){{
//				put("coverageDimension", new JSONArray(){{
//					put(new HashMap<String, Object>(){{
//						put("name", "GRAY_INDEX");
//						put("description", "GridSampleDimension[101.0,101.0]");
//						put("range", new HashMap<String, Object>(){{
//							put("min", 101);
//							put("max", 101);
//						}});
//						put("nullValues", new HashMap<String, Object>(){{
//							put("double", new double[]{101});
//						}});
//					}});
//				}}); 
//			}});
////			put("requestSRS", new HashMap<String, Object>(){{
////				put("string", new String[]{"EPSG:4326"});
////			}});
////			put("responseSRS", new HashMap<String, Object>(){{
////				put("string", new String[]{"EPSG:4326"});
////			}});
////			put("parameters", new HashMap<String, Object>(){{
////				put("entry", new JSONArray(){{
////					put(new HashMap<String, Object>(){{
////						put("string", new String[]{"InputTransparentColor", ""});
////					}});
////					put(new HashMap<String, Object>(){{
////						put("string", new String[]{"SUGGESTED_TILE_SIZE", "512,512"});
////					}});
////				}});
////			}});
//		}});
////		put("", );
////		put("", );
////		put("", );
////		put("", );
////		put("", );
////		put("", new HashMap<String, Object>(){{
////			put("", );
////			put("", );
////		}});
//		System.out.println("JSOBJ:\n"+jsCoverage.toString());
//		String ris = HMC.CallPost("rest/workspaces/"+workspace+"/coverages", jsCoverage.toString(), "application/json");
//		System.out.println("RIS: \n"+ris);
//	}
//
	public boolean setLayer(FeatureTypeRest featureTypeRest, String defaultStyle, ArrayList<String> styles) throws Exception {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", featureTypeRest.getName());
		m.put("enabled", featureTypeRest.isEnabled());

		{
			JSONObject defStyle = new JSONObject();
			defStyle.put("name", defaultStyle);
			m.put("defaultStyle", defStyle);
		}
		
		if (styles.size() > 0) {
			JSONArray a = new JSONArray();
			for (String layer: styles) {
				JSONObject n = new JSONObject();
				n.put("name",layer);
				a.put(n);
			}
			JSONObject l = new JSONObject();
			l.put("style",a);
			m.put("styles", l);
		}
		
		JSONObject j = new JSONObject();
		j.put("layer", m);
		HMC.CallPut("rest/layers/"+ featureTypeRest.getName(), j.toString(), "application/json");
		return true;
	}
	
	public boolean addStyleToLayer(String layer, String style) throws Exception {
		JSONObject n = new JSONObject();
		n.put("name",style);
		JSONObject j2 = new JSONObject();
		j2.put("style", n);
		
		HMC.CallPost("rest/layers/"+ layer + "/styles", j2.toString(), "application/json");
		return true;
	}
	
	public boolean sendStyleSDL(String xmlSdl) throws Exception {
		
		if (HMC.CallPost("rest/styles", xmlSdl, "application/vnd.ogc.sld+xml") == null) return false;
        return true;
	}

}
