package org.gcube.common.geoserverinterface;

import java.util.ArrayList;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.gcube.common.geoserverinterface.GeoCaller.FILTER_TYPE;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverType;
import org.gcube.common.geoserverinterface.bean.CswLayersResult;
import org.gcube.common.geoserverinterface.bean.CswRecord;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.LayerCsw;
import org.gcube.common.geoserverinterface.bean.MetadataInfo;
import org.gcube.common.geoserverinterface.engine.GeonetworkGetMethods;
import org.gcube.common.geoserverinterface.engine.GeonetworkPutMethods;
import org.gcube.common.geoserverinterface.geonetwork.GeoserverDiscovery;
import org.gcube.common.geoserverinterface.geonetwork.MostUnLoadGeoserver;
import org.gcube.common.geoserverinterface.geonetwork.RandomGeoserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeonetworkCaller {

	private static final Logger logger = LoggerFactory.getLogger(GeonetworkCaller.class);
	
	
	/**
	 * @uml.property name="connectionManager"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private MultiThreadedHttpConnectionManager connectionManager = null;
	/**
	 * @uml.property name="hMC"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private HttpMethodCall HMC = null;
	/**
	 * @uml.property name="geonetworkGetMethods"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private GeonetworkGetMethods geonetworkGetMethods = null;
	/**
	 * @uml.property name="geonetworkPutMethods"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private GeonetworkPutMethods geonetworkPutMethods = null;
	// private GeoserverCaller geoserverCaller = null;
	/**
	 * @uml.property name="username"
	 */
	private String geonetworkUser = null;
	/**
	 * @uml.property name="password"
	 */
	private String geonetworkPassword = null;
	/**
	 * @uml.property name="isLogged"
	 */
	private boolean isLogged = false;
	/**
	 * @uml.property name="geoDiscovery"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private GeoserverDiscovery geoDiscovery = null;
	/**
	 * @uml.property name="suffixGeonetworkServices"
	 */
	private final String suffixGeonetworkServices = "/srv/en";
	private String geonetworkUrl;
	private String geoserverUser;
	private String geoserverPwd;

	// public static enum GeonetworkCategory {APPLICATION, DATASETS, ANY};
	// public static enum GeoserverMethodResearch {RANDOM, UNLOAD};
	// public static enum GeoserverType {WMS, WFS};

	/**
	 * This Constructor use only Geonetwork
	 * 
	 * @param geonetworkUrl
	 * @param geonetworkUser
	 * @param geonetworkPassword
	 */
	public GeonetworkCaller(String geonetworkUrl, String geonetworkUser, String geonetworkPassword, String geoserverUser, String geoserverPwd) {
		//geonetworkUrl += suffixGeonetworkServices;
		this.geonetworkUrl = geonetworkUrl + suffixGeonetworkServices;
		this.geonetworkUser = geonetworkUser;
		this.geonetworkPassword = geonetworkPassword;
		this.geoserverUser = geoserverUser;
		this.geoserverPwd = geoserverPwd;

		connectionManager = new MultiThreadedHttpConnectionManager();
		HMC = new HttpMethodCall(connectionManager, this.geonetworkUrl, "", ""); // TODO // va modificato eventualmente il costruttore di HttpMethodCall
		
		this.instanceCallers();
		this.loginGeonetwork();
		// try {
		// this.geoDiscovery = new GeoserverDiscovery(this.getHarvestings());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public void loadGeoserverDiscovery() {

		try {
			this.geoDiscovery = new GeoserverDiscovery(this.getHarvestings());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void instanceCallers() {

		this.geonetworkGetMethods = new GeonetworkGetMethods(HMC);
		this.geonetworkPutMethods = new GeonetworkPutMethods(HMC);
	}

	/**
	 * @return
	 * @uml.property name="geonetworkGetMethods"
	 */
	public GeonetworkGetMethods getGeonetworkGetMethods() {
		return geonetworkGetMethods;
	}

	/**
	 * @return
	 * @uml.property name="geonetworkPutMethods"
	 */
	public GeonetworkPutMethods getGeonetworkPutMethods() {
		return geonetworkPutMethods;
	}

	private String loginGeonetwork() {
		String res = null;

		logger.info("Login Geonetwork");

		if (isLogged)
			this.logoutGeonetwork();
		
		try {
			//System.out.println("\n\nLOGIN WITH "+this.geonetworkUser+this.geonetworkPassword);
			String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
							"<request>" + 
								"<username>" + this.geonetworkUser + "</username>" + 
								"<password>" + this.geonetworkPassword + "</password>" + 
							"</request>";
			res = HMC.CallPost("xml.user.login", query, "text/xml");

		} catch (Exception e) {
			this.isLogged = false;
			e.printStackTrace();
		}

		this.isLogged = true;

		return res;
	}

	public String logoutGeonetwork() {
		String res = null;
		logger.info("Logout Geonetwork");
		
		if (isLogged) {
			try {
				res = HMC.CallPost("xml.user.logout", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><requests/>", "application/xml");

			} catch (Exception e) {
				e.printStackTrace();
			}

			this.isLogged = false;
		}
		return res;
	}

	/**
	 * @return
	 * @uml.property name="username"
	 */
	public String getUsername() {
		return geonetworkUser;
	}

	/**
	 * @return
	 * @uml.property name="password"
	 */
	public String getPassword() {
		return geonetworkPassword;
	}

	public Boolean getIsLogged() {
		return isLogged;
	}

	/**
	 * Function return a list of Geoserver url ordered through the choice strategy (param method in input)
	 * 
	 * @param method
	 *            (mandatory) type enum GeoserverMethodResearch
	 * @param serverType
	 *            (mandatory) type enum GeoserverType
	 * @return
	 */
	public ArrayList<String> getOrderedListOfGeoserver(GeoserverMethodResearch method, GeoserverType serverType) {

		ArrayList<String> orderList = new ArrayList<String>();

		if (!isLogged)
			loginGeonetwork();

		if (this.geoDiscovery == null)
			loadGeoserverDiscovery();

		try {

			if (method.compareTo(GeoserverMethodResearch.MOSTUNLOAD) == 0)
				this.geoDiscovery.setSorter(new MostUnLoadGeoserver(this.geoserverUser, this.geoserverPwd));
			else
				this.geoDiscovery.setSorter(new RandomGeoserver()); // Default choice

			// else if(method.compareTo(GeoserverMethodResearch.RANDOM)==0)
			// this.geoDiscovery.setSorter(new RandomGeoserver());

			
			if (serverType.compareTo(GeoserverType.WMS) == 0)
				orderList = this.geoDiscovery.sortGeoserver(this.geoDiscovery.getWmsGeoserverList());
			else if (serverType.compareTo(GeoserverType.WFS) == 0)
				orderList = this.geoDiscovery.sortGeoserver(this.geoDiscovery.getWfsGeoserverList());

			/*
			 * System.out.println("\nOrdered List of "+ serverType +" Geoserver:");
			 * 
			 * for(String a : orderList) System.out.println(a);
			 * 
			 * System.out.println("\n");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		return orderList;

	}

	// public void reloadWmsGeoserverList(){
	//
	// loadGeoserverList(GeoserverType.WMS);
	//
	// }

	// public void reloadWfsGeoserverList(){
	//
	// loadGeoserverList(GeoserverType.WFS);
	//
	// }

	// ** GET Methods

	public String getHarvestings() throws Exception {
		if (isLogged)
			return geonetworkGetMethods.getListHarvestings();
		else
			throw new Exception("You must be logged");
	}

	public String getMetadataByFileIdentifier(String uuid) throws Exception {
		if (isLogged)
			return geonetworkGetMethods.getMetadataByFileIdentifier(uuid);
		else
			throw new Exception("You must be logged");
	}

	public String getMetadataById(String id) throws Exception {
		if (isLogged)
			return geonetworkGetMethods.getMetadataById(id);
		else
			throw new Exception("You must be logged");
	}

	public String searchLayerByTitleIsLike(String title) throws Exception {
		if (isLogged)
			return geonetworkGetMethods.searchLayerByTitleIsLike(title);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param anyText
	 * @param maxRecords
	 *            = 0 return all records also return max record as specified in input
	 * @return
	 * @throws Exception
	 */
	public String searchLayerByAnyText(String anyText, int maxRecords) throws Exception {

		if (isLogged)
			return geonetworkGetMethods.searchLayerByAnyText(anyText, maxRecords);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param anyText
	 * @param maxRecords
	 *            = 0 return all records also return max record as specified in input
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CswRecord> getCswRecordsBySearch(String anyText, int maxRecords) throws Exception {

		if (isLogged)
			return geonetworkGetMethods.getCswRecordsBySearch(anyText, maxRecords);
		else
			throw new Exception("You must be logged");
	}

	public String searchLayerByTitleIsEqualTo(String title) throws Exception {
		if (isLogged)
			return geonetworkGetMethods.searchLayerByTitleIsEqualTo(title);
		else
			throw new Exception("You must be logged");
	}

	public String getHarvestingById(String id) throws Exception {
		if (isLogged)
			return geonetworkGetMethods.getHarvestingById(id);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param title
	 *            - (mandatory) string or null
	 * @param category
	 *            - (mandatory) string or null
	 * @param similarity
	 *            - (mandatory) true is precise - false is imprecise
	 * @return
	 * @throws Exception
	 */
	public String getGeonetInfoBySearchService(String title, GeonetworkCategory category, Boolean similarity) throws Exception {
		if (isLogged)
			return geonetworkGetMethods.searchService(title, category, similarity);
		else
			throw new Exception("You must be logged");
	}

	// **PUT Methods
	/**
	 * 
	 * @param fileIdentifier
	 *            - (mandatory) string or null
	 * @param layerTitle
	 *            - (mandatory) string
	 * @param description
	 *            - string
	 * @param category
	 *            - (mandatory) A element of GeonetworkCategory Enum
	 * @param geoServerWmsUrl
	 *            - (mandatory) string
	 * @return
	 * @throws Exception
	 */
	public String insertMetadata(String fileIdentifier, String workspace, String layerTitle, String layerName, String description, GeonetworkCategory category, String geoServerWmsUrl) throws Exception {
		if (isLogged)
			return geonetworkPutMethods.insertMetadata(fileIdentifier, workspace, layerTitle, layerName, description, category, geoServerWmsUrl);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param metadataInfo
	 *            type MetadataInfo
	 * @return
	 * @throws Exception
	 */
	public String insertMetadata(MetadataInfo metadataInfo) throws Exception {
		if (isLogged)
			return geonetworkPutMethods.insertMetadata(metadataInfo);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param featureTypeRest
	 * @param category
	 * @param geoServerWmsUrl
	 * @return
	 * @throws Exception
	 */
	public String insertMetadataByFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category, String geoServerWmsUrl) throws Exception {
		if (isLogged)
			return geonetworkPutMethods.insertMetadataByFeatureType(featureTypeRest, category, geoServerWmsUrl);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param fileIdentifier
	 *            - (mandatory) string or null
	 * @param layerTitle
	 *            - (mandatory) string
	 * @param description
	 *            - string
	 * @param geoServerWmsUrl
	 *            - (mandatory) string
	 * @return
	 * @throws Exception
	 */
	public String insertMetadataByCswTransaction(String fileIdentifier, String workspace, String layerTitle, String layerName, String description, String geoServerWmsUrl) throws Exception {
		if (isLogged)
			return geonetworkPutMethods.insertMetadataByCswTransaction(fileIdentifier, workspace, layerTitle, layerName, description, geoServerWmsUrl);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param id
	 *            - (mandatory) string
	 * @return
	 * @throws Exception
	 */
	public String updateHarvesting(String id) throws Exception {
		if (isLogged)
			return geonetworkPutMethods.updateHarvesting(id);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param id
	 *            - (mandatory) string
	 * @return
	 * @throws Exception
	 */
	public String deleteMetadataById(String id) throws Exception {
		if (isLogged)
			return geonetworkPutMethods.deleteMetadataById(id);
		else
			throw new Exception("You must be logged");
	}

	/* added by ceras */
	/**
	 * @param referredWorkspace
	 * @param startPosition
	 * @param maxRecords
	 * @param sortByTiyle
	 * @param sortAscendent
	 * @param filter
	 * @param textToSearch
	 * @return
	 */
	public CswLayersResult getLayersFromCsw(String referredWorkspace, int startPosition, int maxRecords, boolean sortByTitle, boolean sortAscendent, FILTER_TYPE filter, String textToSearch) {
		if (!isLogged)
			loginGeonetwork();

		return geonetworkGetMethods.getLayersFromCsw(referredWorkspace, startPosition, maxRecords, sortByTitle, sortAscendent, filter, textToSearch);
	}

	public String getLayersUrlInformation(FILTER_TYPE filter, String textToSearch) {

		if (!isLogged)
			loginGeonetwork();

		CswLayersResult layer = geonetworkGetMethods.getLayersFromCsw(null, 1, 1, true, true, filter, textToSearch);
		try {
			return layer.getLayers().get(0).getGeoserverUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public LayerCsw getLayersInformation(FILTER_TYPE filter, String textToSearch) {

		if (!isLogged)
			loginGeonetwork();

		CswLayersResult layer = geonetworkGetMethods.getLayersFromCsw(null, 1, 1, true, true, filter, textToSearch);
		try {
			return layer.getLayers().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
