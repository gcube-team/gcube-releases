package org.gcube.common.geoserverinterface.engine;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.geoserverinterface.HttpMethodCall;


public class GeoserverDeleteMethods {

	/**
	 * @uml.property  name="hMC"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private HttpMethodCall HMC= null;

	public GeoserverDeleteMethods(HttpMethodCall hMC) {
		super();
		HMC = hMC;
	}

	public boolean deleteLayersGroup(String groupName) throws Exception {

		if (HMC.CallDelete("rest/layergroups/" + groupName) == null) return false;

		return true;
	}

	public boolean deleteStyleSDL(String styleName, boolean purge) throws Exception {

		Map<String, Object> postparameters = new HashMap<String, Object>();
		postparameters.put("purge", purge);
		return HMC.deleteStyle("rest/styles/" + styleName+"?purge="+purge);
		/*		
		if (HMC.CallDelete("rest/styles/" + styleName, postparameters) == null) return false;

		return true;
		 */
	}



	public boolean deleteLayer(String layerName) throws Exception {

		if (HMC.CallDelete("rest/layers/" + layerName) == null) return false;

		return true;
	}

//	
//		public boolean deleteFeatureTypes(String wokspaceName, String dataStore, String featureTypes) throws Exception {
//			
//			if (HMC.CallDelete("workspaces/"+wokspaceName+"/datastores/"+dataStore+"/featuretypes/" + featureTypes) == null) return false;
//			
//			return true;
//		}
//	
//	See ticket https://issue.imarine.research-infrastructures.eu/ticket/1051
//	
	public boolean deleteFeatureTypes(String wokspaceName,String dataStore,String featureTypes) throws Exception {

		this.deleteLayer(featureTypes); // first delete Layer

		//then delete featuretype
		if (HMC.CallDelete("rest/workspaces/"+wokspaceName+"/datastores/"+dataStore+"/featuretypes/" + featureTypes) == null) return false;
		return true;
	}

}
