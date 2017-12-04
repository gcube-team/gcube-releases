package org.gcube.common.geoserverinterface;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import java.io.File;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverType;
import org.gcube.common.geoserverinterface.bean.BoundsRest;
import org.gcube.common.geoserverinterface.bean.CoverageStoreRest;
import org.gcube.common.geoserverinterface.bean.CoverageTypeRest;
import org.gcube.common.geoserverinterface.bean.CswLayersResult;
import org.gcube.common.geoserverinterface.bean.CswRecord;
import org.gcube.common.geoserverinterface.bean.DataStoreRest;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.common.geoserverinterface.bean.MetadataInfo;
import org.gcube.common.geoserverinterface.bean.WorkspaceRest;
import org.gcube.common.geoserverinterface.engine.GeonetworkGetMethods;
import org.opengis.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoCaller extends GeoCallerConfigurationInterface {

	private static final Logger logger = LoggerFactory.getLogger(GeoCaller.class);
	
	private static final String GEOTIFF_TYPE = "GeoTIFF";

	/**
	 * @uml.property name="geoserverCaller"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private GeoserverCaller geoserverCaller = null;
	/**
	 * @uml.property name="geonetworkCaller"
	 * @uml.associationEnd
	 */
	private GeonetworkCaller geonetworkCaller = null;
	/**
	 * @uml.property name="wmsGeoserverList"
	 * @uml.associationEnd multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private ArrayList<String> wmsGeoserverList = null;
	/**
	 * @uml.property name="wfsGeoserverList"
	 * @uml.associationEnd multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private ArrayList<String> wfsGeoserverList = null;
	/**
	 * @uml.property name="isAvailableGeonetwork"
	 */
	private boolean isAvailableGeonetwork = false;
	/**
	 * @uml.property name="gEONETWORKERROR"
	 */
	private final String GEONETWORKERROR = "Geonetwork is not instantiated";
	/**
	 * @uml.property name="iNSTANCEERROR"
	 */
	private final String INSTANCEERROR = "Geonetwork is not available";
	/**
	 * @uml.property name="httpRC"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private HttpResourceControl httpRC = new HttpResourceControl();
	private String geonetworkUrl;
	private String geonetworkUser;
	private String geoserverWmsUrl;
	private String geoserverUser;
	private String geoserverPwd;
	private GeoserverMethodResearch geoserverResearchMethod;
	private String geonetworkPwd;

	private String currentWmsGeoserver;

	/**
	 * This constructor instance Geonetwork and load (with sorting criteria passed in input) the lists of Geoserver from Geonetwork (or specific GeoserverCaller with params in input)
	 * 
	 * @param geonetworkUrl
	 *            (mandatory) - if null instance only GeoserverCaller (with params in input) if available
	 * @param geonetworkUser
	 *            (mandatory)
	 * @param geonetworkPwd
	 *            (mandatory)
	 * @param geoserverWmsUrl
	 *            (mandatory) - if null instance only GeonetworkCaller if available
	 * @param geoserverUser
	 *            (mandatory)
	 * @param geoserverPwd
	 *            (mandatory)
	 * @param geoserverResearchMethod
	 * @throws Exception
	 */
	public GeoCaller(String geonetworkUrl, String geonetworkUser, String geonetworkPwd, String geoserverWmsUrl, String geoserverUser, String geoserverPwd, GeoserverMethodResearch geoserverResearchMethod) throws Exception {

		this.geonetworkUrl = geonetworkUrl;
		this.geonetworkUser = geonetworkUser;
		this.geonetworkPwd = geonetworkPwd;
		this.geoserverWmsUrl = geoserverWmsUrl;
		this.geoserverUser = geoserverUser;
		this.geoserverPwd = geoserverPwd;
		this.geoserverResearchMethod = geoserverResearchMethod;
		
		// Control if Geonetwork is available
		if (geonetworkUrl != null && httpRC.isAvailableNetworkResource(geonetworkUrl, TRYSLEEPTIME, MAXTRY, null, null)) {
			this.isAvailableGeonetwork = true;
			// Instance GeoserverCaller
			this.geonetworkCaller = new GeonetworkCaller(this.geonetworkUrl, this.geonetworkUser, this.geonetworkPwd, this.geoserverUser, this.geoserverPwd);
			this.wmsGeoserverList = new ArrayList<String>();
			this.wfsGeoserverList = new ArrayList<String>();

			// //Control if Geosever Url exists
			// if(geoserverWmsUrl != null){
			//
			// try{
			// //Instance specific GeoseverCaller
			// this.setWmsGeoserver(geoserverWmsUrl,geoserverUsername,geoserverPassword);
			//
			// }catch (Exception e) {
			// //If instance of Geoserver is not available
			// e.printStackTrace();
			// this.instanceGeoserverCaller(geoserverResearchMethod);
			// }
			// }
			// else{

			this.instanceGeoserverCaller(geoserverResearchMethod, geoserverUser, geoserverPwd);
			// }
		}

		else
		// Control if Geosever Url exists
		if (geoserverWmsUrl != null) {
			// Instance specific GeoseverCaller
			this.setWmsGeoserver(geoserverWmsUrl, geoserverUser, geoserverPwd);
		} else {

			throw new Exception(INSTANCEERROR);
		}
	}

	/**
	 * This constructor instance only GeoserverCaller
	 * 
	 * @param geoserverWmsUrl
	 * @param geoserverUsername
	 * @param geoserverPassword
	 * @throws Exception
	 */
	public GeoCaller(String geoserverWmsUrl, String geoserverUsername, String geoserverPassword) throws Exception {

		this.setWmsGeoserver(geoserverWmsUrl, geoserverUsername, geoserverPassword);
	}

	private void instanceGeoserverCaller(GeoserverMethodResearch geoserverResearchMethod, String geoserverUsername, String geoserverPassword) {

		this.loadGeoserverListsFromGeonetwork(geoserverResearchMethod);
		
		
		// Instance GeoserverCaller on first element of list
		try{
			currentWmsGeoserver = this.wmsGeoserverList.get(0);		
			this.geoserverCaller = new GeoserverCaller(currentWmsGeoserver, geoserverUsername, geoserverPassword);
		}catch(Exception e){			
			try {
				setWmsGeoserver(this.geoserverWmsUrl, geoserverUsername, geoserverPassword);
			} catch (Exception e1) {
				throw new RuntimeException(e);
			}
		}
	}

	private void loadGeoserverListsFromGeonetwork(GeoserverMethodResearch geoserverResearchMethod) {

		// Control if GeoserverMethodResearch exists
		if (geoserverResearchMethod != null) {
			// Set Geoserver method research passed in input and load geoserver list from Geonetwork
			this.wmsGeoserverList = this.geonetworkCaller.getOrderedListOfGeoserver(geoserverResearchMethod, GeoserverType.WMS);
			this.wfsGeoserverList = this.geonetworkCaller.getOrderedListOfGeoserver(geoserverResearchMethod, GeoserverType.WFS);
		} else {
			// Set Geoserver method research as default and load geoserver list from Geonetwork
			this.wmsGeoserverList = this.geonetworkCaller.getOrderedListOfGeoserver(DEFAULTMETHODRESEARCH, GeoserverType.WMS);
			this.wfsGeoserverList = this.geonetworkCaller.getOrderedListOfGeoserver(DEFAULTMETHODRESEARCH, GeoserverType.WFS);
		}
	}

	public void setWmsGeoserver(String geoserverUrl, String geoserverUsername, String geoserverPassword) throws Exception {

		if (httpRC.isAvailableNetworkResource(geoserverUrl, TRYSLEEPTIME, MAXTRY, null, null)) {
			this.wmsGeoserverList = new ArrayList<String>();
			this.wmsGeoserverList.add(geoserverUrl);
			this.geoserverCaller = new GeoserverCaller(geoserverUrl, geoserverUsername, geoserverPassword);
			this.currentWmsGeoserver=geoserverUrl;
		} else
			throw new Exception("Geoserver " + geoserverUrl + " not found");
	}

	public void setWfsGeoserver(String geoserverUrl, String geoserverUsername, String geoserverPassword) throws Exception {

		if (httpRC.isAvailableNetworkResource(geoserverUrl, TRYSLEEPTIME, MAXTRY, null, null)) {
			this.wfsGeoserverList = new ArrayList<String>();
			this.wfsGeoserverList.add(geoserverUrl);
			this.geoserverCaller = new GeoserverCaller(geoserverUrl, geoserverUsername, geoserverPassword);
		} else
			throw new Exception("Geoserver " + geoserverUrl + " not found");
	}

	public String getCurrentWmsGeoserver() {
		return this.currentWmsGeoserver;
//		return this.wmsGeoserverList.get(0);
	}

	public String getCurrentWfsGeoserver() {
		return this.wfsGeoserverList.get(0);
	}

	public ArrayList<String> getWmsGeoserverList() {
		return wmsGeoserverList;
	}

	public ArrayList<String> getWfsGeoserverList() {
		return wfsGeoserverList;
	}

	public void setWmsGeoserverResearchStrategy(GeoserverMethodResearch strategy, String user, String pwd) throws Exception {
		if (isAvailableGeonetwork) {
			this.wmsGeoserverList = new ArrayList<String>();
			this.wmsGeoserverList = this.geonetworkCaller.getOrderedListOfGeoserver(strategy, GeoserverType.WMS);
			this.geoserverCaller = new GeoserverCaller(this.wmsGeoserverList.get(0), user, pwd);
		} else
			throw new Exception(GEONETWORKERROR);
	}

	public void setWfsGeoserverResearchStrategy(GeoserverMethodResearch strategy, String user, String pwd) throws Exception {
		if (isAvailableGeonetwork) {
			this.wfsGeoserverList = new ArrayList<String>();
			this.wfsGeoserverList = this.geonetworkCaller.getOrderedListOfGeoserver(strategy, GeoserverType.WFS);
			this.geoserverCaller = new GeoserverCaller(this.wfsGeoserverList.get(0), user, pwd);
		} else
			throw new Exception(GEONETWORKERROR);
	}

	/*
	 * GEONETWORK METHODS
	 */

	// Geonetwork get methods
	public String getHarvestings() throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().getListHarvestings();
		else
			throw new Exception(GEONETWORKERROR);
	}

	public String getMetadataByFileIdentifier(String uuid) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().getMetadataByFileIdentifier(uuid);
		else
			throw new Exception(GEONETWORKERROR);
	}

	public String getMetadataById(String id) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().getMetadataById(id);
		else
			throw new Exception(GEONETWORKERROR);
	}

	public String searchLayerByTitleIsLike(String title) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().searchLayerByTitleIsLike(title);
		else
			throw new Exception(GEONETWORKERROR);
	}

	public String searchLayerByTitleIsEqualTo(String title) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().searchLayerByTitleIsEqualTo(title);
		else
			throw new Exception(GEONETWORKERROR);
	}

	public String getHarvestingById(String id) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().getHarvestingById(id);
		else
			throw new Exception(GEONETWORKERROR);
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
	public String getGeonetworkLayerInfoBySearchService(String title, GeonetworkCategory category, Boolean similarity) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().searchService(title, category, similarity);
		else
			throw new Exception(GEONETWORKERROR);
	}

	/**
	 * 
	 * @param anyText
	 * @param maxRecords
	 *            = 0 return all records also return max record as specified in input
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CswRecord> getGeonetworkCswRecordsBySearch(String anyText, int maxRecords) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkGetMethods().getCswRecordsBySearch(anyText, maxRecords);
		else
			throw new Exception(GEONETWORKERROR);
	}

	/**
	 * 
	 * @param fileIdentifier
	 * @param workspace
	 * @param layerTitle
	 * @param layerName
	 * @param description
	 * @param category
	 * @param geoServerWmsUrl
	 * @return
	 * @throws Exception
	 */
	public String insertMetadata(String fileIdentifier, String workspace, String layerTitle, String layerName, String description, GeonetworkCategory category, String geoServerWmsUrl) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkPutMethods().insertMetadata(fileIdentifier, workspace, layerTitle, layerName, description, category, geoServerWmsUrl);
		else
			throw new Exception(GEONETWORKERROR);
	}

	/**
	 * 
	 * @param metadataInfo
	 *            type MetadataInfo
	 * @return
	 * @throws Exception
	 */
	public String insertMetadata(MetadataInfo metadataInfo) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkPutMethods().insertMetadata(metadataInfo);
		else
			throw new Exception("You must be logged");
	}

	/**
	 * 
	 * @param fileIdentifier
	 * @param workspace
	 * @param layerTitle
	 * @param layerName
	 * @param description
	 * @param geoServerWmsUrl
	 * @return
	 * @throws Exception
	 */
	public String insertMetadataByCswTransaction(String fileIdentifier, String workspace, String layerTitle, String layerName, String description, String geoServerWmsUrl) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkPutMethods().insertMetadataByCswTransaction(fileIdentifier, workspace, layerTitle, layerName, description, geoServerWmsUrl);
		else
			throw new Exception(GEONETWORKERROR);

	}

	/**
	 * 
	 * @param id
	 *            - (mandatory) string
	 * @return
	 * @throws Exception
	 */
	public String updateHarvesting(String id) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkPutMethods().updateHarvesting(id);
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
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getGeonetworkPutMethods().deleteMetadataById(id);
		else
			throw new Exception("You must be logged");
	}

	/*
	 * GEOSERVER METHODS
	 */

	public ArrayList<String> listWorkspaces() throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listWorkspaces();
	}

	public WorkspaceRest getWorkspace(String wokspaceName) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getWorkspace(wokspaceName);
	}

	public List<String> listDataStores(String wokspaceName) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listDataStores(wokspaceName);
	}

	public DataStoreRest getDataStore(String wokspaceName, String dataStore) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getDataStore(wokspaceName, dataStore);
	}

	public ArrayList<String> listFeaturetypes(String wokspaceName, String dataStore) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listFeaturetypes(wokspaceName, dataStore);
	}

	public FeatureTypeRest getFeatureType(String wokspaceName, String dataStore, String featureType) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getFeatureType(wokspaceName, dataStore, featureType);
	}

	public CoverageTypeRest getCoverageType(String wokspaceName, String coveragestore, String coverageType) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getCoverageType(wokspaceName, coveragestore, coverageType);
	}

	public ArrayList<String> listLayers() throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listLayers();
	}

	public LayerRest getLayer(String nameLayer) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getLayer(nameLayer);
	}

	public ArrayList<String> listLayerGroups() throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listLayerGroups();
	}

	public GroupRest getLayerGroup(String nameGroup) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getLayerGroup(nameGroup);
	}

	public ArrayList<String> listStyles() throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listStyles();
	}

	public ArrayList<String> listStyles(String layerName) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listStyles(layerName);
	}

	public InputStream getStyle(String styleName) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getStyle(styleName);
	}

	public List<String> listCoverageStores(String wokspaceName) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listCoverageStores(wokspaceName);
	}

	public CoverageStoreRest getCoverageStore(String wokspaceName, String coverageStore) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().getCoverageStore(wokspaceName, coverageStore);
	}

	public ArrayList<String> listCoverages(String wokspaceName, String coverageStore) throws Exception {
		return geoserverCaller.getGeoserverGetMethods().listCoverages(wokspaceName, coverageStore);
	}

	// ***************************** create methods
	// method integrate with geonetwork
	// public boolean addLayersGroup(GroupRest group) throws Exception {
	// return geoserverCaller.getGeoserverPutMethods().addLayersGroup(group);
	// }

	public boolean addLayersGroup(GroupRest group, GeonetworkCategory category) throws Exception {
		boolean insertGeoserverStatus = geoserverCaller.getGeoserverPutMethods().addLayersGroup(group);

		if (insertGeoserverStatus == true && isAvailableGeonetwork) {
			if (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByGroupRest(group, category, this.getCurrentWmsGeoserver()) != null)
				return true;
		}

		return insertGeoserverStatus;

	}

	// method integrate with geonetwork
	// public boolean addFeatureType(FeatureTypeRest featureTypeRest) throws Exception {
	// //if(isAvailableGeonetwork)
	// return geoserverCaller.getGeoserverPutMethods().addFeatureType(featureTypeRest);
	// }
	
//	public boolean addFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category) throws Exception {
//
//		boolean insertGeoserverStatus = geoserverCaller.getGeoserverPutMethods().addFeatureType(featureTypeRest);
//
//		if (insertGeoserverStatus == true && isAvailableGeonetwork) {
//			if (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest, category, this.getCurrentWmsGeoserver()) != null)
//				return true;
//		}
//
//		return insertGeoserverStatus;
//
//	}

//	public boolean addGeoLayer(String fileUrl, String layerName, String layerTitle, String workspace, GeonetworkCategory category, String description, String abstr, String scope) throws Exception {
//		String destinationUrl = geoserverCaller.getGeoserverUrl();
//		
//		logger.info("1 - CALL DATA TRANSFER...");
//		final String origFileName = DataTransferUtl.transferFromUrl(fileUrl, destinationUrl, workspace, scope, true);
//		
//		if (origFileName==null) {
//			logger.error("Data transfer error, url not found: "+fileUrl);
//			return false;
//		}
//		String fileName = origFileName.substring(0, origFileName.lastIndexOf(".")) + ".tiff";
//		
////		System.out.println("Orig file name: "+origFileName);
////		System.out.println("File name: "+fileName);
////		boolean b = true;
//		logger.info("    CALL DATA TRANSFER - OK");
//
//		logger.info("2 - CREATE COVERAGE STORE AND COVERAGE...");
//		
//		boolean b = addPreExistentGeoTiff(fileName, layerName, layerTitle, workspace, category, description, abstr);
//
//		if (b)
//			logger.info("    CREATE COVERAGE STORE AND COVERAGE - OK");		
//		
//		return b;
//	}
	

	
	
//	public boolean addGeoTiff(String geoTiffUrl, final String layerName, String layerTitle, final String workspace, 
//			GeonetworkCategory category, String description, String abstr, String scope) throws Exception {
//		
//		String destinationUrl = geoserverCaller.getGeoserverUrl();
//		
//		logger.info("1 - CALL DATA TRANSFER...");
//		final String fileName = DataTransferUtl.transferFromUrl(geoTiffUrl, destinationUrl, workspace, scope, false);
//		
//		if (fileName==null) {
//			logger.error("Data transfer error, url not found.");
//			return false;
//		}
////		String fileName = "p_edulis_map2.tiff";
//		logger.info("    CALL DATA TRANSFER - OK");
//
//		logger.info("2 - CREATE COVERAGE STORE AND COVERAGE...");
//		
//		boolean b = addPreExistentGeoTiff(fileName, layerName, layerTitle, workspace, category, description, abstr);
////		geoserverCaller.getGeoserverPutMethods().addCoverage(layerName, layerTitle, description, workspace, storeName);
////		geoserverCaller.getGeoserverPutMethods().addCoverage("p_edulis5_map", "p_edulis5_map title", "descr", "aquamaps", "p_edulis5_cs_P");
////		geoserverCaller.getGeoserverPutMethods().addCoverageStore("p_edulis5_cs_P", GEOTIFF_TYPE, true, workspace, urlFile);
////		geoserverCaller.getGeoserverPutMethods().addCoverageStore("store_"+layerName, GEOTIFF_TYPE, true, workspace, urlFile);
//		if (b)
//			logger.info("    CREATE COVERAGE STORE AND COVERAGE - OK");		
//		
//		return b;
//	}

	public boolean addPreExistentGeoTiff(String fileName, final String layerName, String layerTitle, final String workspace, GeonetworkCategory category, String description, String abstr) throws Exception {
		
		boolean insertGeoserverStatus = true;
		
		String storeName = "store_"+layerName;
		File geotiff = new File(Constants.getGeoserverDataAbsolutePath() + workspace + "/" + fileName);
		
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(this.currentWmsGeoserver, this.geoserverUser, this.geoserverPwd);
		insertGeoserverStatus = publisher.publishExternalGeoTIFF(workspace, storeName, geotiff, layerName, "EPSG:4326", ProjectionPolicy.REPROJECT_TO_DECLARED, "raster");
		
		if (insertGeoserverStatus && isAvailableGeonetwork) {
			FeatureTypeRest featureTypeRest = new FeatureTypeRest();
			featureTypeRest.setName(layerName);
			featureTypeRest.setTitle(layerTitle);
			featureTypeRest.setWorkspace(workspace);
			featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
			featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
//			featureTypeRest.setNativeBoundingBox();
			boolean insertGeonetworkSataus = (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest, category, this.getCurrentWmsGeoserver(), description, abstr) != null);
			return insertGeonetworkSataus;
		} else
			return insertGeoserverStatus;
	}
	

//	public boolean addCoverageStore(String fileName, final String layerName, String layerTitle, final String workspace, 
//			String description, String abstr, String scope) throws Exception {
//
//		System.out.println("2 - CREATE COVERAGE STORE...");
//		String urlFile = "file:data/" + workspace + "/" + fileName;
//		geoserverCaller.getGeoserverPutMethods().addCoverageStore("store_"+layerName, GEOTIFF_TYPE, true, workspace, urlFile);
//		System.out.println("    CREATE COVERAGE STORE - OK");
//		
//		return false;
//	}
	

	/* modified by ceras */
	public boolean addFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category, String description, String abstr) throws Exception {

		boolean insertGeoserverStatus = true;
		
		if (!layerExists(featureTypeRest.getName()))
			insertGeoserverStatus = geoserverCaller.getGeoserverPutMethods().addFeatureType(featureTypeRest);  

		if (insertGeoserverStatus && isAvailableGeonetwork)
			return (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest, category, this.getCurrentWmsGeoserver(), description, abstr) != null);
		else
			return insertGeoserverStatus;
	}


	
	
	
	public boolean addFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category) throws Exception {

		boolean insertGeoserverStatus = true;
		
		if (!layerExists(featureTypeRest.getName()))
			insertGeoserverStatus = geoserverCaller.getGeoserverPutMethods().addFeatureType(featureTypeRest);  

		if (insertGeoserverStatus && isAvailableGeonetwork)
			return (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest, category, this.getCurrentWmsGeoserver(), null, null) != null);
		else
			return insertGeoserverStatus;

	}
	
	//TODO Method to publish ISO Metadata
//	public boolean addFeatureType(FeatureTypeRest featureTypeRest, GeonetworkCategory category, String description, String abstr) throws Exception {
//
//		boolean insertGeoserverStatus = true;
//		
//		if (!layerExists(featureTypeRest.getName()))
//			insertGeoserverStatus = geoserverCaller.getGeoserverPutMethods().addFeatureType(featureTypeRest);  
//
//		if (insertGeoserverStatus && isAvailableGeonetwork)
//			return (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest, category, this.getCurrentWmsGeoserver(), description, abstr) != null);
//		else
//			return insertGeoserverStatus;
//	}
	
	/* added by ceras */
	
	// check if a layer exists
	public boolean layerExists(String layerName) throws Exception {
		return layerExists(layerName, 1);
	}

	// check if a group exists
	public boolean groupExists(String groupName) throws Exception {
		return groupExists(groupName, 1);
	}

	public boolean layerExists(String layerName, int maxtries) throws Exception {
		String geoserverUrl = this.getGeoServerForLayer(layerName);
		String urlToCheck = geoserverUrl + "/rest/layers/" + layerName + ".json";
		boolean found = false;
		
		for (int i=0; i<maxtries && !found; i++){
			logger.debug("layerExists->Checking Layer on geoserver " + geoserverUrl);
			int result = checkUrl(urlToCheck, this.geoserverUser, this.geoserverPwd);
			logger.debug("layerExists->Cached Layer Checking " + result);
			if (result == 200)
				found = true;
		}
		
		logger.debug("layerExists->Layer " +layerName + (found ? "" : " not") + " present in " + geoserverUrl);
		
		return found;
	}

	public boolean groupExists(String groupName, int maxtries) throws Exception {
		String geoserverUrl = this.getGeoServerForGroup(groupName);
		String urlToCheck = geoserverUrl + "/rest/layergroups/" + groupName + ".json";
		boolean found = false;
		
		for (int i=0; i<maxtries && !found; i++){
			logger.debug("groupExists->Checking Group on geoserver " + geoserverUrl);
			int result = checkUrl(urlToCheck, this.geoserverUser, this.geoserverPwd);
			logger.debug("groupExists->Cached Group Checking " + result);
			if (result == 200)
				found = true;
		}
		
		logger.debug("layerExists->Group " +groupName + (found ? "" : " not") + " present in " + geoserverUrl);
		
		return found;
	}

	public int checkUrl(String url, final String username, final String password) {
		int checkConn = -1;
		try {
			if ((username != null) && (password != null)) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password.toCharArray());
					}
				});
			}
 
			URL checkurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) checkurl.openConnection();
			checkConn = conn.getResponseCode();
			conn.disconnect();
		} catch (Exception e) {
			logger.error("ERROR in URL " + e.getMessage());
		}
		return checkConn;
	}
	
	/* end added by ceras */
	
	

	public boolean addLayer(LayerRest layerRest) throws Exception {
		return geoserverCaller.getGeoserverPutMethods().addLayer(layerRest);
	}

	public boolean setLayer(FeatureTypeRest featureTypeRest, String defaultStyle, ArrayList<String> styles) throws Exception {
		return geoserverCaller.getGeoserverPutMethods().setLayer(featureTypeRest, defaultStyle, styles);
	}

	public boolean addStyleToLayer(String layer, String style) throws Exception {
		return geoserverCaller.getGeoserverPutMethods().addStyleToLayer(layer, style);
	}

	public boolean sendStyleSDL(String xmlSdl) throws Exception {
		return geoserverCaller.getGeoserverPutMethods().sendStyleSDL(xmlSdl);
	}

	// ***************************** modify methods
	public boolean modifyLayersGroup(GroupRest group) throws Exception {
		return geoserverCaller.getGeoserverModifyMethods().modifyLayersGroup(group);
	}

	public boolean modifyStyleSDL(String schemaName, String xmlSdl) throws Exception {
		return geoserverCaller.getGeoserverModifyMethods().modifyStyleSDL(schemaName, xmlSdl);
	}

	// ***************************** deleting methods
	public boolean deleteLayersGroup(String name) throws Exception {
		return geoserverCaller.getGeoserverDeleteMethods().deleteLayersGroup(name);
	}

	public boolean deleteStyleSDL(String schemaName, boolean purge) throws Exception {
		return geoserverCaller.getGeoserverDeleteMethods().deleteStyleSDL(schemaName, purge);
	}

	public boolean deleteLayer(String LayerName) throws Exception {
		return geoserverCaller.getGeoserverDeleteMethods().deleteLayer(LayerName);
	}

	public boolean deleteFeatureTypes(String wokspaceName, String dataStore, String featureTypes) throws Exception {
		return geoserverCaller.getGeoserverDeleteMethods().deleteFeatureTypes(wokspaceName, dataStore, featureTypes);
	}

	/**
	 * Returns the first geoserver containing the group name
	 * 
	 * @param groupName
	 * @return
	 */
	// gets geoserverName
	public String getGeoServerForName(String name, boolean searchgroup) throws Exception {

		ArrayList<CswRecord> cswarray = null;

		if (isAvailableGeonetwork)
			cswarray = getGeonetworkCswRecordsBySearch(name, 0);

		String geourl = "";

		if ((cswarray != null) && (cswarray.size() > 0)) {
			int s = cswarray.size();
			for (int i = 0; i < s; i++) {
				boolean isgroup = cswarray.get(i).getAbstractProperty().startsWith(GeonetworkGetMethods.GROUP);
				if ((searchgroup && isgroup) || (!searchgroup && !isgroup) ){
					geourl = cswarray.get(i).getURI().get(0);
					break;
				}
			}

		} else
			geourl = wmsGeoserverList.get(0);

		if (geourl.length() > 0) {
			int interr = geourl.indexOf("?");
			if (interr > 0)
				geourl = geourl.substring(0, interr);

			if (geourl.endsWith("/wms") || geourl.endsWith("/gwc") || geourl.endsWith("/wfs"))
				geourl = geourl.substring(0, geourl.length() - 4);
		}
		return geourl;
	}

	public String getGeoServerForGroup(String groupName) throws Exception {
		return getGeoServerForName(groupName, true);
	}

	public String getGeoServerForLayer(String layerName) throws Exception {
		return getGeoServerForName(layerName, false);
	}

	// added by gianpaolo coro in 02/02/12
	public List<String> getLayerTitlesByWms(List<String> workspaces, List<String> layerNames) throws Exception {

		List<String> res = new ArrayList<String>();
		int len = layerNames.size();

		try {
			for (int i = 0; i < len; i++) {
				res.add(getLayerTitleByWms(workspaces.get(i), layerNames.get(i)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public String getLayerTitleByWms(String workspace, String layerName) {

		String res = null;

		try {
			String geoserverUrl = getGeoServerForName(layerName,false);
			GeoserverCaller geoserver = new GeoserverCaller(geoserverUrl, this.geoserverUser, this.geoserverPwd);
			res = geoserver.getLayerTitleByWms(workspace, layerName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public enum FILTER_TYPE {NO_FILTER, TITLE, ANY_TEXT};
	
	/* added by ceras */
	public CswLayersResult getLayersFromCsw() throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getLayersFromCsw(null, 1, 0, false, false, FILTER_TYPE.NO_FILTER, null);
		else
			throw new Exception(GEONETWORKERROR); // TODO manage this case
	}

	/* added by ceras */
	public CswLayersResult getLayersFromCsw(String referredWorkspace) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getLayersFromCsw(referredWorkspace, 1, 0, false, false, FILTER_TYPE.NO_FILTER, null);
		else
			throw new Exception(GEONETWORKERROR);
	}

	/* added by ceras */
	public CswLayersResult getLayersFromCsw(String referredWorkspace, int startPosition, int maxRecords) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getLayersFromCsw(referredWorkspace, startPosition, maxRecords, false, false, FILTER_TYPE.NO_FILTER, null);
		else
			throw new Exception(GEONETWORKERROR);
	}

	/* added by ceras */
	public CswLayersResult getLayersFromCsw(String referredWorkspace, int startPosition, int maxRecords, boolean sortByTiyle, boolean sortAscendent) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getLayersFromCsw(referredWorkspace, startPosition, maxRecords, sortByTiyle, sortAscendent, FILTER_TYPE.NO_FILTER, null);
		else
			throw new Exception(GEONETWORKERROR);
	}

	/* added by ceras */
	public CswLayersResult getLayersFromCsw(String referredWorkspace, int startPosition, int maxRecords, boolean sortByTitle, boolean sortAscendent, FILTER_TYPE filter, String textToSearch) throws Exception {
		if (isAvailableGeonetwork)
			return this.geonetworkCaller.getLayersFromCsw(referredWorkspace, startPosition, maxRecords, sortByTitle, sortAscendent, filter, textToSearch);
		else
			throw new Exception(GEONETWORKERROR);
	}

//	/* added by ceras */
//	public void setLogger(org.apache.log4j.logger logger) {
//		logger.setLogger(logger);
//	}
	
	public void close() {
		if (isAvailableGeonetwork && geonetworkCaller!=null)
			this.geonetworkCaller.logoutGeonetwork();
	}
	
	// ****************************** Custom ISO Metadata publishing ******************************* //
	
//	public boolean addGeoLayer(String fileUrl, String layerName, String layerTitle, String workspace, GeonetworkCategory category, Metadata toPublishMeta, String scope, String defaultStyle) throws Exception {
//		String destinationUrl = geoserverCaller.getGeoserverUrl();
//		
//		logger.info("1 - CALL DATA TRANSFER...");
//		final String origFileName = DataTransferUtl.transferFromUrl(fileUrl, destinationUrl, workspace, scope, true);
//		
//		if (origFileName==null) {
//			logger.error("Data transfer error, url not found: "+fileUrl);
//			return false;
//		}
//		String fileName = origFileName.substring(0, origFileName.lastIndexOf(".")) + ".tiff";
//		
////		System.out.println("Orig file name: "+origFileName);
////		System.out.println("File name: "+fileName);
////		boolean b = true;
//		logger.info("    CALL DATA TRANSFER - OK");
//
//		logger.info("2 - CREATE COVERAGE STORE AND COVERAGE...");
//		
//		boolean b = addPreExistentGeoTiff(fileName, layerName, layerTitle, workspace, category, toPublishMeta,defaultStyle);
//
//		if (b)
//			logger.info("    CREATE COVERAGE STORE AND COVERAGE - OK");		
//		
//		return b;
//	}
	
	public boolean addFeatureType(FeatureTypeRest featureTypeRest,String defaultStyle, GeonetworkCategory category, Metadata toPublishMeta) throws Exception {

		boolean insertGeoserverStatus = true;
		
		if (!layerExists(featureTypeRest.getName()))
			insertGeoserverStatus = geoserverCaller.getGeoserverPutMethods().addFeatureType(featureTypeRest);  

		if (insertGeoserverStatus && isAvailableGeonetwork)
			return (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest,defaultStyle, category, this.getCurrentWmsGeoserver(), toPublishMeta) != null);
		else
			return insertGeoserverStatus;
	}
	
//	public boolean addGeoTiff(String geoTiffUrl, final String layerName, String layerTitle, final String workspace, 
//			GeonetworkCategory category, Metadata toPublishMeta, String scope, String defaultStyle) throws Exception {
//		
//		String destinationUrl = geoserverCaller.getGeoserverUrl();
//		
//		logger.info("1 - CALL DATA TRANSFER...");
//		final String fileName = DataTransferUtl.transferFromUrl(geoTiffUrl, destinationUrl, workspace, scope, false);
//		
//		if (fileName==null) {
//			logger.error("Data transfer error, url not found.");
//			return false;
//		}
////		String fileName = "p_edulis_map2.tiff";
//		logger.info("    CALL DATA TRANSFER - OK");
//
//		logger.info("2 - CREATE COVERAGE STORE AND COVERAGE...");
//		
//		boolean b = addPreExistentGeoTiff(fileName, layerName, layerTitle, workspace, category, toPublishMeta,defaultStyle);
////		geoserverCaller.getGeoserverPutMethods().addCoverage(layerName, layerTitle, description, workspace, storeName);
////		geoserverCaller.getGeoserverPutMethods().addCoverage("p_edulis5_map", "p_edulis5_map title", "descr", "aquamaps", "p_edulis5_cs_P");
////		geoserverCaller.getGeoserverPutMethods().addCoverageStore("p_edulis5_cs_P", GEOTIFF_TYPE, true, workspace, urlFile);
////		geoserverCaller.getGeoserverPutMethods().addCoverageStore("store_"+layerName, GEOTIFF_TYPE, true, workspace, urlFile);
//		if (b)
//			logger.info("    CREATE COVERAGE STORE AND COVERAGE - OK");		
//		
//		return b;
//	}

	public boolean addPreExistentGeoTiff(String fileName, final String layerName, String layerTitle, final String workspace, GeonetworkCategory category, Metadata toPublishMeta,String defaultStyle) throws Exception {
		
		boolean insertGeoserverStatus = true;
		
		String storeName = "store_"+layerName;
		File geotiff = new File(Constants.getGeoserverDataAbsolutePath() + workspace + "/" + fileName);
		
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(this.currentWmsGeoserver, this.geoserverUser, this.geoserverPwd);
		insertGeoserverStatus = publisher.publishExternalGeoTIFF(workspace, storeName, geotiff, layerName, "EPSG:4326", ProjectionPolicy.REPROJECT_TO_DECLARED, "raster");
		
		if (insertGeoserverStatus && isAvailableGeonetwork) {
			FeatureTypeRest featureTypeRest = new FeatureTypeRest();
			featureTypeRest.setName(layerName);
			featureTypeRest.setTitle(layerTitle);
			featureTypeRest.setWorkspace(workspace);
			featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
			featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
//			featureTypeRest.setNativeBoundingBox();
			boolean insertGeonetworkSataus = (geonetworkCaller.getGeonetworkPutMethods().insertMetadataByFeatureType(featureTypeRest,defaultStyle, category, this.getCurrentWmsGeoserver(), toPublishMeta) != null);
			return insertGeonetworkSataus;
		} else
			return insertGeoserverStatus;
	}

}
