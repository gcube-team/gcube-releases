package org.gcube.common.geoserverinterface;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.gcube.common.geoserverinterface.bean.CoverageStoreRest;
import org.gcube.common.geoserverinterface.bean.CoverageTypeRest;
import org.gcube.common.geoserverinterface.bean.DataStoreRest;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.common.geoserverinterface.bean.WorkspaceRest;
import org.gcube.common.geoserverinterface.engine.GeoserverDeleteMethods;
import org.gcube.common.geoserverinterface.engine.GeoserverGetMethods;
import org.gcube.common.geoserverinterface.engine.GeoserverModifyMethods;
import org.gcube.common.geoserverinterface.engine.GeoserverPutMethods;

public class GeoserverCaller {

	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private MultiThreadedHttpConnectionManager connectionManager = null;
    /**
	 * @uml.property  name="hMC"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private HttpMethodCall HMC= null;
	/**
	 * @uml.property  name="geoserverGetMethods"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GeoserverGetMethods geoserverGetMethods = null;
	/**
	 * @uml.property  name="geoserverPutMethods"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GeoserverPutMethods geoserverPutMethods = null;
	/**
	 * @uml.property  name="geoserverModifyMethods"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GeoserverModifyMethods geoserverModifyMethods = null;
	/**
	 * @uml.property  name="geoserverDeleteMethods"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GeoserverDeleteMethods geoserverDeleteMethods = null;
	
	private String username;
	private String password;
	private String geoserverUrl;
	
	public GeoserverCaller(String geoserver_url, String user, String password) {
		super();
		this.username = user;
		this.password = password;
		connectionManager = new MultiThreadedHttpConnectionManager();
		HMC = new HttpMethodCall(connectionManager, geoserver_url, user, password);
		this.geoserverGetMethods = new GeoserverGetMethods(HMC);
		this.geoserverPutMethods = new GeoserverPutMethods(HMC);
		this.geoserverModifyMethods = new GeoserverModifyMethods(HMC);
		this.geoserverDeleteMethods = new GeoserverDeleteMethods(HMC);
		this.geoserverUrl = geoserver_url;
	}
	
	/**
	 * @return the geoserverUrl
	 */
	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	//TODO AGGIUNTA DA FRA**************
	
	/**
	 * @return
	 * @uml.property  name="geoserverGetMethods"
	 */
	public GeoserverGetMethods getGeoserverGetMethods() {
		return geoserverGetMethods;
	}

	/**
	 * @return
	 * @uml.property  name="geoserverPutMethods"
	 */
	public GeoserverPutMethods getGeoserverPutMethods() {
		return geoserverPutMethods;
	}


	/**
	 * @return
	 * @uml.property  name="geoserverModifyMethods"
	 */
	public GeoserverModifyMethods getGeoserverModifyMethods() {
		return geoserverModifyMethods;
	}

	/**
	 * @return
	 * @uml.property  name="geoserverDeleteMethods"
	 */
	public GeoserverDeleteMethods getGeoserverDeleteMethods() {
		return geoserverDeleteMethods;
	}
	
	
	public List<String> getLayerTitlesByWms(List<String> workspace, List<String> layerName){
		
		List<String> res = null;
		
		try {
			res = geoserverGetMethods.getLayerTitleByWms(workspace, layerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	public String getLayerTitleByWms(String workspace, String layerName){
		
		String res = null;
		
		try {
			res = geoserverGetMethods.getLayerTitleByWms(workspace, layerName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	public ArrayList<String> listWorkspaces() throws Exception {
		return geoserverGetMethods.listWorkspaces();
	}
	
	public WorkspaceRest getWorkspace(String wokspaceName) throws Exception {
		return geoserverGetMethods.getWorkspace(wokspaceName);
	}
	
	public List<String> listDataStores(String wokspaceName) throws Exception {
		return geoserverGetMethods.listDataStores(wokspaceName);
	}
	
	public DataStoreRest getDataStore(String wokspaceName, String dataStore) throws Exception {
		return geoserverGetMethods.getDataStore(wokspaceName, dataStore);
	}
	
	public ArrayList<String> listFeaturetypes(String wokspaceName, String dataStore) throws Exception {
		return geoserverGetMethods.listFeaturetypes(wokspaceName, dataStore);
	}
	
	public FeatureTypeRest getFeatureType(String wokspaceName, String dataStore, String featureType) throws Exception {
		return geoserverGetMethods.getFeatureType(wokspaceName, dataStore, featureType);
	}
	
	public CoverageTypeRest getCoverageType(String wokspaceName, String coveragestore, String coverageType) throws Exception {
		return geoserverGetMethods.getCoverageType(wokspaceName, coveragestore, coverageType);
	}
	
	public ArrayList<String> listLayers() throws Exception {
		return geoserverGetMethods.listLayers();
	}

	public LayerRest getLayer(String nameLayer) throws Exception {
		return geoserverGetMethods.getLayer(nameLayer);
	}
	
	public ArrayList<String> listLayerGroups() throws Exception {
		return geoserverGetMethods.listLayerGroups();
	}
	
	public GroupRest getLayerGroup(String nameGroup) throws Exception {
		return geoserverGetMethods.getLayerGroup(nameGroup);
	}
	public ArrayList<String> listStyles() throws Exception {
		return geoserverGetMethods.listStyles();
	}
	
	public ArrayList<String> listStyles(String layerName) throws Exception {
		return geoserverGetMethods.listStyles(layerName);
	}
	
	public InputStream getStyle(String styleName) throws Exception {
		return geoserverGetMethods.getStyle(styleName);
	}
	
	public List<String> listCoverageStores(String wokspaceName) throws Exception {
		return geoserverGetMethods.listCoverageStores(wokspaceName);
	}
	
	public CoverageStoreRest getCoverageStore(String wokspaceName, String coverageStore) throws Exception {
		return geoserverGetMethods.getCoverageStore(wokspaceName, coverageStore);
	}
	
	public ArrayList<String> listCoverages(String wokspaceName, String coverageStore) throws Exception{
		return geoserverGetMethods.listCoverages(wokspaceName, coverageStore);
	}
	
	//***************************** create methods
	public boolean addLayersGroup(GroupRest group) throws Exception {
		return geoserverPutMethods.addLayersGroup(group);
	}
	public boolean addFeatureType(FeatureTypeRest featureTypeRest) throws Exception {
		return geoserverPutMethods.addFeatureType(featureTypeRest);
	}
	public boolean addLayer(LayerRest layerRest) throws Exception {
		return geoserverPutMethods.addLayer(layerRest);
	}
	public boolean setLayer(FeatureTypeRest featureTypeRest, String defaultStyle, ArrayList<String> styles) throws Exception {
		return geoserverPutMethods.setLayer(featureTypeRest, defaultStyle, styles);
	}
	public boolean addStyleToLayer(String layer, String style) throws Exception {
		return geoserverPutMethods.addStyleToLayer(layer, style);
	}
	public boolean sendStyleSDL(String xmlSdl) throws Exception {
		return geoserverPutMethods.sendStyleSDL(xmlSdl);
	}
	
	//***************************** modify methods
	public boolean modifyLayersGroup(GroupRest group) throws Exception {
		return geoserverModifyMethods.modifyLayersGroup(group);
	}
	public boolean modifyStyleSDL(String schemaName, String xmlSdl) throws Exception {
		return geoserverModifyMethods.modifyStyleSDL(schemaName, xmlSdl);
	}
	
	//***************************** deleting methods
	public boolean deleteLayersGroup(String name) throws Exception { 
		return geoserverDeleteMethods.deleteLayersGroup(name);
	}
	public boolean deleteStyleSDL(String schemaName, boolean purge) throws Exception {
		return geoserverDeleteMethods.deleteStyleSDL(schemaName, purge);
	}
	public boolean deleteLayer(String LayerName) throws Exception {
		return geoserverDeleteMethods.deleteLayer(LayerName);
	}
	public boolean deleteFeatureTypes(String wokspaceName, String dataStore, String featureTypes) throws Exception {
		return geoserverDeleteMethods.deleteFeatureTypes(wokspaceName, dataStore, featureTypes);
	}
}
