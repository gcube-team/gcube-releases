package gr.cite.geoanalytics.functions.discovery;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gr.cite.clustermanager.exceptions.NoAvailableLayer;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;

public class PathFinder implements Serializable {

	private static final long serialVersionUID = -5928146173925525510L;
	
	private static final Logger logger = LoggerFactory.getLogger(PathFinder.class);
	
	@Autowired private TrafficShaper trafficShaper;
	
	
	private Synopsis getSynopsisFor(String layerID) throws NoAvailableLayer{
		GosDefinition gosDefinition = trafficShaper.getAppropriateGosForLayer(layerID);
		String geoserverEndpoint = gosDefinition.getGeoserverEndpoint();
		geoserverEndpoint = geoserverEndpoint.endsWith("/") ? geoserverEndpoint.substring(0, geoserverEndpoint.length()-1) : geoserverEndpoint;
		String capabilitiesUrl = geoserverEndpoint+"/wfs?REQUEST=GetCapabilities&version=1.1.0";
		return new Synopsis(capabilitiesUrl, geoserverEndpoint, gosDefinition.getGeoserverWorkspace(), layerID);
	}
	
	//TODO: REMOVE THIS DUMMY FUNCTION (REPLACE WITH ABOVE)
	private Synopsis getDUMMYSynopsisFor(String layerID) throws NoAvailableLayer{
		String geoserverEndpoint = "http://dl008.madgik.di.uoa.gr:8080/geoserver";
		String capabilitiesUrl = geoserverEndpoint+"/wfs?REQUEST=GetCapabilities&version=1.1.0";
		return new Synopsis(capabilitiesUrl, geoserverEndpoint, "geoanalytics", layerID);
	}
	
	
	
	public FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSourceFor(String layerID) throws NoAvailableLayer, IOException{
		//TODO: replace the getDUMMYSynopsisFor with getSynopsisFor
		Synopsis synopsis = getSynopsisFor(layerID);
		System.out.println("Fetching info from geoserver (synopsis): "+synopsis);
		Map<String, String> connectionParameters = new HashMap<String, String>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", synopsis.getCapabilitiesURL());
		return DataStoreFinder.getDataStore(connectionParameters).getFeatureSource(synopsis.getWorkspace()+":"+synopsis.getLayerID());
	}
	
	

	
	
	
	class Synopsis {
		
		String capabilitiesURL;
		String geoserverURL;
		String workspace;
		String layerID;
		
		public Synopsis(String capabilitiesURL, String geoserverURL, String workspace, String layerID){
			this.capabilitiesURL = capabilitiesURL;
			this.geoserverURL = geoserverURL;
			this.workspace = workspace;
			this.layerID = layerID;
		}
		
		public String getCapabilitiesURL() {
			return capabilitiesURL;
		}
		public String getGeoserverURL() {
			return geoserverURL;
		}
		public String getWorkspace() {
			return workspace;
		}
		public String getLayerID() {
			return layerID;
		}
		
		@Override
		public String toString(){
			return "[capabilitiesURL="+capabilitiesURL+" geoserverURL="+geoserverURL+" workspace="+workspace+" layerID="+layerID+"]";
		}
		
	}
	
}
