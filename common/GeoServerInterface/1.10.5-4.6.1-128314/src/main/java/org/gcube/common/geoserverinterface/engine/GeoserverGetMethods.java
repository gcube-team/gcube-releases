package org.gcube.common.geoserverinterface.engine;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.geoserverinterface.HttpMethodCall;
import org.gcube.common.geoserverinterface.bean.BoundsRest;
import org.gcube.common.geoserverinterface.bean.CoverageStoreRest;
import org.gcube.common.geoserverinterface.bean.CoverageTypeRest;
import org.gcube.common.geoserverinterface.bean.DataStoreRest;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.common.geoserverinterface.bean.WorkspaceRest;
import org.gcube.common.geoserverinterface.geonetwork.utils.ParserXpath;
import org.gcube.common.geoserverinterface.json.JSONArray;
import org.gcube.common.geoserverinterface.json.JSONException;
import org.gcube.common.geoserverinterface.json.JSONObject;

public class GeoserverGetMethods {
	
    /**
	 * @uml.property  name="hMC"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private HttpMethodCall HMC= null;
	
	public GeoserverGetMethods(HttpMethodCall hMC) {
		super();
		HMC = hMC;
	}

	public ArrayList<String> listWorkspaces() throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/workspaces.json");
		
		JSONObject j = new JSONObject(res);
		JSONArray workspaces = j.getJSONObject("workspaces").getJSONArray("workspace");
		
		for (int i = 0; i < workspaces.length(); i++) {
			JSONObject workspace = workspaces.getJSONObject(i);
			results.add(workspace.getString("name"));
		}
		return results;
	}
	
	public WorkspaceRest getWorkspace(String wokspaceName) throws Exception {
		
		WorkspaceRest result = new WorkspaceRest();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+".json");
		
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("workspace").names().length(); i++) {
			if(j.getJSONObject("workspace").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("workspace").getString("name"));
			} else {
				result.setStores(j.getJSONObject("workspace").names().getString(i), 
						getData(wokspaceName, j.getJSONObject("workspace").names().getString(i)));
			}
			
		}
		return result;
	}
	
	private List<String> getData(String wokspaceName, String type) throws Exception {
		
		List<String> results = new ArrayList<String>();

		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/"+type.toLowerCase()+".json");
		
		JSONObject j = new JSONObject(res);
		JSONObject s = j.getJSONObject(type);
		JSONArray attributes = s.getJSONArray(s.names().get(0) + "");

		for (int i = 0; i < attributes.length(); i++) {
			JSONObject n = attributes.getJSONObject(i);
			results.add(n.getString("name"));
		}
		
		return results;
	}
	
	public List<String> listDataStores(String wokspaceName) throws Exception {
		
		return getData(wokspaceName, "dataStores");
	}
	
	public List<String> listCoverageStores(String wokspaceName) throws Exception {
		
		return getData(wokspaceName, "coverageStores");
	}
	
	public CoverageStoreRest getCoverageStore(String wokspaceName, String coverageStore) throws Exception {
		
		CoverageStoreRest result = new CoverageStoreRest();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/coveragestores/" + coverageStore + ".json");
		
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("coverageStore").names().length(); i++) {
			if(j.getJSONObject("coverageStore").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("coverageStore").getString("name"));
			} else if(j.getJSONObject("coverageStore").names().getString(i).contentEquals("type")) {
				result.setType(j.getJSONObject("coverageStore").getString("type"));
			} else if(j.getJSONObject("coverageStore").names().getString(i).contentEquals("url")) {
				result.setType(j.getJSONObject("coverageStore").getString("url"));	
			} else if(j.getJSONObject("coverageStore").names().getString(i).contentEquals("enabled")) {
				result.setEnabled(j.getJSONObject("coverageStore").getString("enabled").contentEquals("true"));
			} else {
				//System.out.println(j.getJSONObject("coverageStore").names().getString(i));
			}
			
		}
		
		result.setCoverages(this.listCoverages(wokspaceName, coverageStore));
		
		return result;
	}

	public DataStoreRest getDataStore(String wokspaceName, String dataStore) throws Exception {
		
		DataStoreRest result = new DataStoreRest();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/datastores/" + dataStore + ".json");
		
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("dataStore").names().length(); i++) {
			if(j.getJSONObject("dataStore").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("dataStore").getString("name"));
			} else if(j.getJSONObject("dataStore").names().getString(i).contentEquals("type")) {
				result.setType(j.getJSONObject("dataStore").getString("type"));
			} else if(j.getJSONObject("dataStore").names().getString(i).contentEquals("enabled")) {
				result.setEnabled(j.getJSONObject("dataStore").getString("enabled").contentEquals("true"));
			} else if(j.getJSONObject("dataStore").names().getString(i).contentEquals("connectionParameters")) {
				JSONArray contents = j.getJSONObject("dataStore").getJSONObject("connectionParameters").getJSONArray("entry");
				for (int x = 0; x < contents.length(); x++) {
					JSONObject n = contents.getJSONObject(x);
					result.setConnectionParameter(n.getString("@key"), n.getString("$"));
				}
			} else {
				//System.out.println(j.getJSONObject("dataStore").names().getString(i));
			}
			
		}
		return result;
	}
	
	public ArrayList<String> listFeaturetypes(String wokspaceName, String dataStore) throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/datastores/"+dataStore+"/featuretypes.json");
		
		JSONObject j = new JSONObject(res);
		JSONArray workspaces = j.getJSONObject("featureTypes").getJSONArray("featureType");
		
		for (int i = 0; i < workspaces.length(); i++) {
			JSONObject workspace = workspaces.getJSONObject(i);
			results.add(workspace.getString("name"));
		}
		return results;
	}
	
	public ArrayList<String> listCoverages(String wokspaceName, String coverageStore) throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/coveragestores/"+coverageStore+"/coverages.json");
		
		JSONObject j = new JSONObject(res);
		JSONArray workspaces = j.getJSONObject("coverages").getJSONArray("coverage");
		
		for (int i = 0; i < workspaces.length(); i++) {
			JSONObject workspace = workspaces.getJSONObject(i);
			results.add(workspace.getString("name"));
		}
		return results;
	}
	
	public FeatureTypeRest getFeatureType(String wokspaceName, String dataStore, String featureType) throws Exception {
		
		FeatureTypeRest result = new FeatureTypeRest();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/datastores/"+dataStore+"/featuretypes/"+featureType+".json");
		
		result.setWorkspace(wokspaceName);
		result.setDatastore(dataStore);
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("featureType").names().length(); i++) {
			if(j.getJSONObject("featureType").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("featureType").getString("name"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("nativeName")) {
				result.setNativeName(j.getJSONObject("featureType").getString("nativeName"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("title")) {
				result.setTitle(j.getJSONObject("featureType").getString("title"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("nativeCRS")) {
				result.setNativeCRS(j.getJSONObject("featureType").getString("nativeCRS"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("srs")) {
				result.setSrs(j.getJSONObject("featureType").getString("srs"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("projectionPolicy")) {
				result.setProjectionPolicy(j.getJSONObject("featureType").getString("projectionPolicy"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("maxFeatures")) {
				result.setMaxFeatures(j.getJSONObject("featureType").getInt("maxFeatures"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("numDecimals")) {
				result.setNumDecimals(j.getJSONObject("featureType").getInt("numDecimals"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("enabled")) {
				result.setEnabled(j.getJSONObject("featureType").getString("enabled").contentEquals("true"));
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("nativeBoundingBox")) {
				JSONObject contents = j.getJSONObject("featureType").getJSONObject("nativeBoundingBox");
				String crs;
				try {
					crs = contents.getString("crs");
				} catch (JSONException e) {
					crs = "EPSG:4326"; // fix for the case which crs doesn't exist
				}
				result.setNativeBoundingBox(new BoundsRest(contents.getDouble("minx"), 
															contents.getDouble("maxx"), 
															contents.getDouble("miny"), 
															contents.getDouble("maxy"), 
															crs));
				
			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("latLonBoundingBox")) {
				JSONObject contents = j.getJSONObject("featureType").getJSONObject("latLonBoundingBox");
				result.setLatLonBoundingBox(new BoundsRest(contents.getDouble("minx"), 
															contents.getDouble("maxx"), 
															contents.getDouble("miny"), 
															contents.getDouble("maxy"), 
															contents.getString("crs")));
				
//			} else if(j.getJSONObject("featureType").names().getString(i).contentEquals("metadata")) {
//				JSONArray contents = j.getJSONObject("featureType").getJSONObject("metadata").getJSONArray("entry");
//				for (int x = 0; x < contents.length(); x++) {
//					JSONObject n = contents.getJSONObject(x);
//					result.setMetadata(n.getString("@key"), n.getString("$"));
//				}
			} else {
				//System.out.println(j.getJSONObject("dataStore").names().getString(i));
			}
			
		}
		return result;
	}
	
	public ArrayList<String> listLayers() throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/layers.json");
		
		
		//System.out.println(res);
		
		
		JSONObject j = new JSONObject(res);
		JSONArray workspaces = j.getJSONObject("layers").getJSONArray("layer");
		
		for (int i = 0; i < workspaces.length(); i++) {
			JSONObject workspace = workspaces.getJSONObject(i);
			results.add(workspace.getString("name"));
		}
		return results;
	}

	public LayerRest getLayer(String nameLayer) throws Exception {
		
		LayerRest result = new LayerRest();
		
		String res = HMC.Call("rest/layers/"+nameLayer+".json");
		
		//System.out.println(res);
		
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("layer").names().length(); i++) {
			if(j.getJSONObject("layer").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("layer").getString("name"));
			} else if(j.getJSONObject("layer").names().getString(i).contentEquals("type")) {
				result.setType(j.getJSONObject("layer").getString("type"));	
			} else if(j.getJSONObject("layer").names().getString(i).contentEquals("enabled")) {
				result.setEnabled(j.getJSONObject("layer").getString("enabled").contentEquals("true"));
			} else if(j.getJSONObject("layer").names().getString(i).contentEquals("defaultStyle")) {
				JSONObject defaultStyle = j.getJSONObject("layer").getJSONObject("defaultStyle");
				result.setDefaultStyle(defaultStyle.getString("name"));
				
			} else if(j.getJSONObject("layer").names().getString(i).contentEquals("resource")) {
				JSONObject contents = j.getJSONObject("layer").getJSONObject("resource");
				
				result.setResource(contents.getString("@class"));
				result.setResourceName(contents.getString("name"));
				result.setFeatureTypeLink(contents.getString("href"));
				
				try {
					String href = contents.getString("href");
					String workspace = "";
					String datastore = "";
					String coveragestore = "";
					//http://geoserver-dev.d4science-ii.research-infrastructures.eu:8080/geoserver/rest/workspaces/aquamaps/coveragestores/TrueMarble/coverages/TrueMarble.16km.2700x1350.json
					
					if (href.indexOf("/datastores/") > href.indexOf("/workspaces/")+12)
						workspace = href.substring(href.indexOf("/workspaces/")+12, href.indexOf("/datastores/"));
					if (href.indexOf("/featuretypes/") > href.indexOf("/datastores/")+12)
						datastore = href.substring(href.indexOf("/datastores/")+12, href.indexOf("/featuretypes/"));
					
					if (workspace.trim().contentEquals("")) {
						if (href.indexOf("/coveragestores/") > href.indexOf("/datastores/")+12)
							workspace = href.substring(href.indexOf("/workspaces/")+12, href.indexOf("/coveragestores/"));
					}
					if (href.indexOf("/coverages/") > href.indexOf("/coveragestores/")+16)
						coveragestore = href.substring(href.indexOf("/coveragestores/")+16, href.indexOf("/coverages/"));
					
					if (result.getDatastore().trim().contentEquals("")) {
						result.setDatastore(datastore);
					}
					if (result.getWorkspace().trim().contentEquals("")) {
						result.setWorkspace(workspace);
					}
					if (result.getCoveragestore().trim().contentEquals("")) {
						result.setCoveragestore(coveragestore);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				

			} else if(j.getJSONObject("layer").names().getString(i).contentEquals("styles")) {
				JSONArray contents = j.getJSONObject("layer").getJSONObject("styles").getJSONArray("style");
				for (int x = 0; x < contents.length(); x++) {
					JSONObject n = contents.getJSONObject(x);
					result.addStyle(n.getString("name"));
				}
			} else {
				//System.out.println(j.getJSONObject("dataStore").names().getString(i));
			}
			
		}
		return result;
	}
	
	public ArrayList<String> listLayerGroups() throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/layergroups.json");

		JSONObject j = new JSONObject(res);
		JSONArray workspaces = j.getJSONObject("layerGroups").getJSONArray("layerGroup");
		
		for (int i = 0; i < workspaces.length(); i++) {
			JSONObject workspace = workspaces.getJSONObject(i);
			results.add(workspace.getString("name"));
		}
		return results;
	}
	
	public GroupRest getLayerGroup(String nameGroup) throws Exception {
		
		GroupRest result = new GroupRest();
		
		String res = HMC.Call("rest/layergroups/"+nameGroup+".json");
		
		if (res == null) return null;
		//System.out.println(res);
		
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("layerGroup").names().length(); i++) {
			if(j.getJSONObject("layerGroup").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("layerGroup").getString("name"));
			
			} else if(j.getJSONObject("layerGroup").names().getString(i).contentEquals("bounds")) {
				JSONObject contents = j.getJSONObject("layerGroup").getJSONObject("bounds");
				result.setBounds(new BoundsRest(contents.getDouble("minx"), 
															contents.getDouble("maxx"), 
															contents.getDouble("miny"), 
															contents.getDouble("maxy"), 
															contents.getString("crs")));
						
			} else if(j.getJSONObject("layerGroup").names().getString(i).contentEquals("layers")) {
				JSONArray contents = j.getJSONObject("layerGroup").getJSONObject("layers").getJSONArray("layer");
				for (int x = 0; x < contents.length(); x++) {
					JSONObject n = contents.getJSONObject(x);
					result.addLayer(n.getString("name"));
				}
			} else {
				//System.out.println(j.getJSONObject("dataStore").names().getString(i));
			}
			
		}
		
		for (int i = 0; i < j.getJSONObject("layerGroup").names().length(); i++) {
			if(j.getJSONObject("layerGroup").names().getString(i).contentEquals("styles")) {
				JSONArray contents = j.getJSONObject("layerGroup").getJSONObject("styles").getJSONArray("style");
				for (int x = 0; x < contents.length(); x++) {
					JSONObject n = contents.getJSONObject(x);
					
					result.addStyle(result.getLayers().get(x), n.getString("name"));
				}
			}
		}
		
		return result;
	}

	public ArrayList<String> listStyles() throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/styles.json");

		JSONObject j = new JSONObject(res);
		JSONArray workspaces = j.getJSONObject("styles").getJSONArray("style");
		
		for (int i = 0; i < workspaces.length(); i++) {
			JSONObject workspace = workspaces.getJSONObject(i);
			results.add(workspace.getString("name"));
		}
		return results;
	}
	
	public ArrayList<String> listStyles(String layerName) throws Exception {
		
		ArrayList<String> results = new ArrayList<String>();
		
		String res = HMC.Call("rest/layers/"+layerName+"/styles.json");
		
		JSONObject j = new JSONObject(res);
		
		if (!j.get("styles").equals("")) {
			JSONArray workspaces = j.getJSONObject("styles").getJSONArray("style");
			
			for (int i = 0; i < workspaces.length(); i++) {
				JSONObject workspace = workspaces.getJSONObject(i);
				results.add(workspace.getString("name"));
			}
		}
		return results;
	}

	public InputStream getStyle(String styleName) throws Exception {
		return HMC.CallAsStream("rest/styles/"+styleName+".sld");
	}

	public CoverageTypeRest getCoverageType(String wokspaceName,
			String coveragestore, String coverageType) throws Exception {
		
		CoverageTypeRest result = new CoverageTypeRest();
		
		String res = HMC.Call("rest/workspaces/"+wokspaceName+"/coveragestores/"+coveragestore+"/coverages/"+coverageType+".json");
		
		result.setWorkspace(wokspaceName);
		result.setCoveragestore(coveragestore);
		JSONObject j = new JSONObject(res);
		for (int i = 0; i < j.getJSONObject("coverage").names().length(); i++) {
			if(j.getJSONObject("coverage").names().getString(i).contentEquals("name")) {
				result.setName(j.getJSONObject("coverage").getString("name"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("nativeName")) {
				result.setNativeName(j.getJSONObject("coverage").getString("nativeName"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("title")) {
				result.setTitle(j.getJSONObject("coverage").getString("title"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("nativeCRS")) {
				result.setNativeCRS(j.getJSONObject("coverage").getString("nativeCRS"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("srs")) {
				result.setSrs(j.getJSONObject("coverage").getString("srs"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("projectionPolicy")) {
				result.setProjectionPolicy(j.getJSONObject("coverage").getString("projectionPolicy"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("nativeFormat")) {
				result.setNativeFormat(j.getJSONObject("coverage").getString("nativeFormat"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("enabled")) {
				result.setEnabled(j.getJSONObject("coverage").getString("enabled").contentEquals("true"));
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("nativeBoundingBox")) {
				JSONObject contents = j.getJSONObject("coverage").getJSONObject("nativeBoundingBox");
				result.setNativeBoundingBox(new BoundsRest(contents.getDouble("minx"), 
															contents.getDouble("maxx"), 
															contents.getDouble("miny"), 
															contents.getDouble("maxy"), 
															contents.getString("crs")));
				
			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("latLonBoundingBox")) {
				JSONObject contents = j.getJSONObject("coverage").getJSONObject("latLonBoundingBox");
				result.setLatLonBoundingBox(new BoundsRest(contents.getDouble("minx"), 
															contents.getDouble("maxx"), 
															contents.getDouble("miny"), 
															contents.getDouble("maxy"), 
															contents.getString("crs")));
				
//			} else if(j.getJSONObject("coverage").names().getString(i).contentEquals("metadata")) {
//				JSONArray contents = j.getJSONObject("coverage").getJSONObject("metadata").getJSONArray("entry");
//				for (int x = 0; x < contents.length(); x++) {
//					JSONObject n = contents.getJSONObject(x);
//					result.setMetadata(n.getString("@key"), n.getString("$"));
//				}
			} else {
				//System.out.println(j.getJSONObject("dataStore").names().getString(i));
			}
			
		}
		return result;
	}
	
	private String getCapabilities() throws Exception{
		String queryWMS = "wms?VERSION=1.1.0&REQUEST=GetCapabilities";
		String res = HMC.Call(queryWMS);
		return res;
	}
	
	public List<String> getLayerTitleByWms (List<String> workspaces, List<String> layerNames) throws Exception{
		List<String> map = new ArrayList<String>();
		String capabilities = getCapabilities();
		int len = layerNames.size();
		for (int i = 0;i<len;i++){
			String title = getLayerTitleByWms(workspaces.get(i), layerNames.get(i), capabilities);
			map.add(title);
		}
		return map;
	}
	
	public String getLayerTitleByWms(String workspace, String layerName) throws Exception {
		return getLayerTitleByWms(workspace, layerName, null);
	}
	public String getLayerTitleByWms(String workspace, String layerName,String capabilitiesXML) throws Exception {

		String res = capabilitiesXML;
		if ((res==null )|| (res.length()==0))
				res = getCapabilities();
		
		ArrayList<String> xmlValues = new ArrayList<String>();

		String queryXPath = "//Layer[Name[contains(.,'"+workspace+":"+layerName+"')]]/Title";

//		System.out.println(queryXPath);

		xmlValues = ParserXpath.getTextFromXPathExpression(res, queryXPath);

		if (xmlValues.size() > 0)
			return xmlValues.get(0);
		else
			return null;

	}
}
