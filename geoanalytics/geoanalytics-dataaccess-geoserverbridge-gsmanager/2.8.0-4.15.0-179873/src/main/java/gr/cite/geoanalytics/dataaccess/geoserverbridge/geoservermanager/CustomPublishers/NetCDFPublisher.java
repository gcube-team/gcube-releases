package gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.CustomPublishers;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import it.geosolutions.geoserver.rest.encoder.*;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStructuredGridCoverageReaderManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStyleManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.zip.ZipFile;

public class NetCDFPublisher {
    public static final String DEFAULT_CRS = "EPSG:4326";
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTPublisher.class);
    private final String restURL;
    private final String gsuser;
    private final String gspass;
    private final GeoServerRESTStyleManager styleManager;

    public NetCDFPublisher(String restURL, String username, String password) {
        this.restURL = HTTPUtils.decurtSlash(restURL);
        this.gsuser = username;
        this.gspass = password;
        URL url = null;

        try {
            url = new URL(restURL);
        } catch (MalformedURLException var6) {
            LOGGER.error("Bad URL: Calls to GeoServer are going to fail", var6);
        }

        this.styleManager = new GeoServerRESTStyleManager(url, username, password);
    }

    public String backup(String backupDir) throws IllegalArgumentException {
        return this.backup(backupDir, false, false, false);
    }

    public String backup(String backupDir, boolean includedata, boolean includegwc, boolean includelog) throws IllegalArgumentException {
        if (backupDir != null && !backupDir.isEmpty()) {
            StringBuilder bkpUrl = new StringBuilder(this.restURL);
            bkpUrl.append("/rest/bkprst/backup");
            GSBackupEncoder bkpenc = new GSBackupEncoder(backupDir);
            bkpenc.setIncludeData(includedata);
            bkpenc.setIncludeGwc(includegwc);
            bkpenc.setIncludeLog(includelog);
            String result = HTTPUtils.post(bkpUrl.toString(), bkpenc.toString(), "text/xml", this.gsuser, this.gspass);
            return result;
        } else {
            throw new IllegalArgumentException("The backup_dir must not be null or empty");
        }
    }

    public String restore(String backupDir) throws IllegalArgumentException {
        if (backupDir != null && !backupDir.isEmpty()) {
            StringBuilder bkpUrl = new StringBuilder(this.restURL);
            bkpUrl.append("/rest/bkprst/restore");
            GSBackupEncoder bkpenc = new GSBackupEncoder(backupDir);
            String result = HTTPUtils.post(bkpUrl.toString(), bkpenc.toString(), "text/xml", this.gsuser, this.gspass);
            return result;
        } else {
            throw new IllegalArgumentException("The backup_dir must not be null or empty");
        }
    }

    public boolean createWorkspace(String workspace) {
        String sUrl = this.restURL + "/rest/workspaces";
        GSWorkspaceEncoder wsenc = new GSWorkspaceEncoder(workspace);
        String wsxml = wsenc.toString();
        String result = HTTPUtils.postXml(sUrl, wsxml, this.gsuser, this.gspass);
        return result != null;
    }

    public boolean createWorkspace(String name, URI uri) {
        return this.createNamespace(name, uri);
    }

    public boolean createNamespace(String prefix, URI uri) {
        String sUrl = this.restURL + "/rest/namespaces";
        GSNamespaceEncoder nsenc = new GSNamespaceEncoder(prefix, uri);
        String nsxml = nsenc.toString();
        String result = HTTPUtils.postXml(sUrl, nsxml, this.gsuser, this.gspass);
        return result != null;
    }

    public boolean updateNamespace(String prefix, URI uri) {
        String sUrl = this.restURL + "/rest/namespaces/" + this.encode(prefix);
        GSNamespaceEncoder nsenc = new GSNamespaceEncoder(prefix, uri);
        String nsxml = nsenc.toString();
        String result = HTTPUtils.put(sUrl, nsxml, "application/xml", this.gsuser, this.gspass);
        return result != null;
    }

    public boolean removeNamespace(String prefix, boolean recurse) {
        return this.removeWorkspace(prefix, recurse);
    }

    public boolean publishStyle(String sldBody) {
        return this.styleManager.publishStyle(sldBody);
    }

    public boolean publishStyle(String sldBody, String name) throws IllegalArgumentException {
        return this.styleManager.publishStyle(sldBody, name);
    }

    public boolean publishStyle(File sldFile) {
        return this.styleManager.publishStyle(sldFile);
    }

    public boolean publishStyle(File sldFile, String name) {
        return this.styleManager.publishStyle(sldFile, name);
    }

    public boolean publishStyle(String sldBody, String name, boolean raw) {
        return this.styleManager.publishStyle(sldBody, name, raw);
    }

    public boolean publishStyle(File sldFile, String name, boolean raw) {
        return this.styleManager.publishStyle(sldFile, name, raw);
    }

    public boolean updateStyle(File sldFile, String name, boolean raw) throws IllegalArgumentException {
        return this.styleManager.updateStyle(sldFile, name, raw);
    }

    public boolean updateStyle(String sldBody, String name, boolean raw) throws IllegalArgumentException {
        return this.styleManager.updateStyle(sldBody, name, raw);
    }

    public boolean updateStyle(String sldBody, String name) throws IllegalArgumentException {
        return this.styleManager.updateStyle(sldBody, name);
    }

    public boolean updateStyle(File sldFile, String name) throws IllegalArgumentException {
        return this.styleManager.updateStyle(sldFile, name);
    }

    public boolean removeStyle(String styleName, boolean purge) throws IllegalArgumentException {
        return this.styleManager.removeStyle(styleName, purge);
    }

    public boolean removeStyle(String styleName) {
        return this.styleManager.removeStyle(styleName);
    }

    public boolean publishStyleInWorkspace(String workspace, String sldBody) {
        return this.styleManager.publishStyleInWorkspace(workspace, sldBody);
    }

    public boolean publishStyleInWorkspace(String workspace, String sldBody, String name) throws IllegalArgumentException {
        return this.styleManager.publishStyleInWorkspace(workspace, sldBody, name);
    }

    public boolean publishStyleInWorkspace(String workspace, File sldFile) {
        return this.styleManager.publishStyleInWorkspace(workspace, sldFile);
    }

    public boolean publishStyleInWorkspace(String workspace, File sldFile, String name) {
        return this.styleManager.publishStyleInWorkspace(workspace, sldFile, name);
    }

    public boolean updateStyleInWorkspace(String workspace, String sldBody, String name) throws IllegalArgumentException {
        return this.styleManager.updateStyleInWorkspace(workspace, sldBody, name);
    }

    public boolean updateStyleInWorkspace(String workspace, File sldFile, String name) throws IllegalArgumentException {
        return this.styleManager.updateStyleInWorkspace(workspace, sldFile, name);
    }

    public boolean removeStyleInWorkspace(String workspace, String styleName, boolean purge) throws IllegalArgumentException {
        return this.styleManager.removeStyleInWorkspace(workspace, styleName, purge);
    }

    public boolean removeStyleInWorkspace(String workspace, String styleName) {
        return this.styleManager.removeStyleInWorkspace(workspace, styleName);
    }

    private boolean createStore(String workspace, GeoServerRESTPublisher.StoreType dsType, String storeName,
                                GeoServerRESTPublisher.UploadMethod method, Enum extension, String mimeType, URI uri,
                                GeoServerRESTPublisher.ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException, IllegalArgumentException {

        if (workspace != null && dsType != null && storeName != null && method != null && extension != null && mimeType != null && uri != null) {
            StringBuilder sbUrl = (new StringBuilder(this.restURL)).append("/rest/workspaces/").append(workspace).append("/").append(dsType).append("/").append(storeName).append("/").append(method).append(".").append("netcdf");
            String sentResult;
            if (configure != null) {
                sbUrl.append("?configure=").append(configure);
                if (params != (NameValuePair[])null) {
                    sentResult = this.appendParameters(params);
                    if (!sentResult.isEmpty()) {
                        sbUrl.append("&").append(sentResult);
                    }
                }
            }

            sentResult = null;
            if (method.equals(GeoServerRESTPublisher.UploadMethod.FILE)) {
                File file = new File(uri);
                if (!file.exists()) {
                    throw new FileNotFoundException("unable to locate file: " + file);
                }

                sentResult = HTTPUtils.put(sbUrl.toString(), file, mimeType, this.gsuser, this.gspass);
            } else if (method.equals(GeoServerRESTPublisher.UploadMethod.EXTERNAL)) {
                sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(), mimeType, this.gsuser, this.gspass);
            } else if (method.equals(GeoServerRESTPublisher.UploadMethod.URL)) {
                sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(), mimeType, this.gsuser, this.gspass);
            }

            if (sentResult != null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Store successfully created using ( " + uri + " )");
                }

                return true;
            } else {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Error in creating store using: " + uri);
                }

                return false;
            }
        } else {
            throw new IllegalArgumentException("Null argument");
        }
    }

    public boolean createNetCDFStore(String workspace, String storeName,
                                GeoServerRESTPublisher.UploadMethod method, Enum extension, String mimeType, URI uri,
                                GeoServerRESTPublisher.ParameterConfigure configure,
                                      String content, NameValuePair... params) throws FileNotFoundException, IllegalArgumentException {
        if(configure == null)
            configure = GeoServerRESTPublisher.ParameterConfigure.ALL;


        if (workspace != null && storeName != null && method != null && extension != null && mimeType != null && uri != null) {
            StringBuilder sbUrl = (new StringBuilder(this.restURL)).append("/rest/workspaces/").append(workspace).append("/").append("coveragestores");
            String sentResult;
            if (configure != null) {
                sbUrl.append("?configure=").append(configure);
            }

            sentResult = null;
            if (method.equals(GeoServerRESTPublisher.UploadMethod.FILE)) {
                File file = new File(uri);
                if (!file.exists()) {
                    throw new FileNotFoundException("unable to locate file: " + file);
                }

                sentResult = HTTPUtils.put(sbUrl.toString(), file, mimeType, this.gsuser, this.gspass);
            } else if (method.equals(GeoServerRESTPublisher.UploadMethod.EXTERNAL)) {
                sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(), mimeType, this.gsuser, this.gspass);
            } else if (method.equals(GeoServerRESTPublisher.UploadMethod.URL)) {
                sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(), mimeType, this.gsuser, this.gspass);
            }

            if(content == null)
                content = "<coverageStore><name>NetCDFStore</name><workspace>NetCDFWorkspace</workspace><enabled>true</enabled><type>NetCDF</type><url>file:NetCDFStore\\/sresa1b_ncar_ccsm3-example.nc</url></coverageStore>";

            sentResult = HTTPUtils.putXml(sbUrl.toString(), content, this.gsuser, this.gspass);

            if (sentResult != null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Store successfully created using ( " + uri + " )");
                }

                return true;
            } else {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Error in creating store using: " + uri);
                }

                return false;
            }
        } else {
            throw new IllegalArgumentException("Null argument");
        }
    }

    private boolean createDataStore(String workspace, String storeName, GeoServerRESTPublisher.UploadMethod method, GeoServerRESTPublisher.DataStoreExtension extension, String mimeType, URI uri, GeoServerRESTPublisher.ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException, IllegalArgumentException {
        return this.createStore(workspace, GeoServerRESTPublisher.StoreType.DATASTORES, storeName, method, extension, mimeType, uri, configure, params);
    }

    private boolean createCoverageStore(String workspace, String storeName, GeoServerRESTPublisher.UploadMethod method,
                                        GeoServerRESTPublisher.CoverageStoreExtension extension, String mimeType, URI uri,
                                        GeoServerRESTPublisher.ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException, IllegalArgumentException {
        return this.createStore(workspace, GeoServerRESTPublisher.StoreType.COVERAGESTORES, storeName, method, extension, mimeType, uri, configure, params);
    }

    /** @deprecated */
    public boolean createPostGISDatastore(String workspace, GSPostGISDatastoreEncoder datastoreEncoder) {
        String sUrl = this.restURL + "/rest/workspaces/" + workspace + "/datastores/";
        String xml = datastoreEncoder.toString();
        String result = HTTPUtils.postXml(sUrl, xml, this.gsuser, this.gspass);
        return result != null;
    }

    /** @deprecated */
    public boolean publishDBLayer(String workspace, String storename, String layername, String srs, String defaultStyle) {
        GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
        fte.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED);
        fte.addKeyword("KEYWORD");
        fte.setTitle(layername);
        fte.setName(layername);
        fte.setSRS(srs);
        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setDefaultStyle(defaultStyle);
        return this.publishDBLayer(workspace, storename, fte, layerEncoder);
    }

    public boolean publishDBLayer(String workspace, String storename, GSFeatureTypeEncoder fte, GSLayerEncoder layerEncoder) {
        String ftypeXml = fte.toString();
        StringBuilder postUrl = (new StringBuilder(this.restURL)).append("/rest/workspaces/").append(workspace).append("/datastores/").append(storename).append("/featuretypes");
        String layername = fte.getName();
        if (layername != null && !layername.isEmpty()) {
            String configuredResult = HTTPUtils.postXml(postUrl.toString(), ftypeXml, this.gsuser, this.gspass);
            boolean published = configuredResult != null;
            boolean configured = false;
            if (!published) {
                LOGGER.warn("Error in publishing (" + configuredResult + ") " + workspace + ":" + storename + "/" + layername);
            } else {
                LOGGER.info("DB layer successfully added (layer:" + layername + ")");
                if (layerEncoder == null) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("GSLayerEncoder is null: Unable to find the defaultStyle for this layer");
                    }

                    return false;
                }

                configured = this.configureLayer(workspace, layername, layerEncoder);
                if (!configured) {
                    LOGGER.warn("Error in configuring (" + configuredResult + ") " + workspace + ":" + storename + "/" + layername);
                } else {
                    LOGGER.info("DB layer successfully configured (layer:" + layername + ")");
                }
            }

            return published && configured;
        } else {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("GSFeatureTypeEncoder has no valid name associated, try using GSFeatureTypeEncoder.setName(String)");
            }

            return false;
        }
    }

    public boolean publishShp(String workspace, String storename, String datasetname, File zipFile) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, storename, new NameValuePair[0], datasetname, GeoServerRESTPublisher.UploadMethod.FILE, zipFile.toURI(), "EPSG:4326", (String)null);
    }

    public boolean publishShp(String workspace, String storeName, NameValuePair[] storeParams, String datasetName, GeoServerRESTPublisher.UploadMethod method, URI shapefile, String srs, String nativeCRS, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && storeName != null && shapefile != null && datasetName != null && policy != null) {
            boolean srsNull = srs == null || srs.length() == 0;
            boolean nativeSrsNull = nativeCRS == null || nativeCRS.length() == 0;
            if (policy != GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED || !nativeSrsNull && !srsNull) {
                if (policy == GSResourceEncoder.ProjectionPolicy.NONE && nativeSrsNull) {
                    throw new IllegalArgumentException("Unable to run: you can't ask GeoServer to use a native srs which is null");
                } else if (policy == GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED && srsNull) {
                    throw new IllegalArgumentException("Unable to run: you can't force GeoServer to use an srs which is null");
                } else {
                    String mimeType;
                    switch(method) {
                        case EXTERNAL:
                        case external:
                            mimeType = "text/plain";
                            break;
                        case URL:
                        case FILE:
                        case file:
                        case url:
                            mimeType = "application/zip";
                            break;
                        default:
                            mimeType = null;
                    }

                    if (!this.createDataStore(workspace, storeName != null ? storeName : FilenameUtils.getBaseName(shapefile.toString()), method, GeoServerRESTPublisher.DataStoreExtension.SHP, mimeType, shapefile, GeoServerRESTPublisher.ParameterConfigure.NONE, storeParams)) {
                        LOGGER.error("Unable to create data store for shapefile: " + shapefile);
                        return false;
                    } else {
                        GSFeatureTypeEncoder featureTypeEncoder = new GSFeatureTypeEncoder();
                        featureTypeEncoder.setName(datasetName);
                        featureTypeEncoder.setTitle(datasetName);
                        if (!srsNull) {
                            featureTypeEncoder.setSRS(srs);
                        } else {
                            featureTypeEncoder.setSRS(nativeCRS);
                        }

                        if (!nativeSrsNull) {
                            featureTypeEncoder.setNativeCRS(nativeCRS);
                        }

                        featureTypeEncoder.setProjectionPolicy(policy);
                        if (!this.createResource(workspace, GeoServerRESTPublisher.StoreType.DATASTORES, storeName, featureTypeEncoder)) {
                            LOGGER.error("Unable to create a coverage store for coverage: " + shapefile);
                            return false;
                        } else {
                            GSLayerEncoder layerEncoder = this.configureDefaultStyle(defaultStyle);
                            return this.configureLayer(workspace, datasetName, layerEncoder);
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Unable to run: you can't ask GeoServer to reproject while not specifying a native CRS");
            }
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    private GSLayerEncoder configureDefaultStyle(String defaultStyle) {
        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        if (defaultStyle != null && !defaultStyle.isEmpty()) {
            if (defaultStyle.indexOf(":") != -1) {
                String[] wsAndName = defaultStyle.split(":");
                layerEncoder.setDefaultStyle(wsAndName[0], wsAndName[1]);
            } else {
                layerEncoder.setDefaultStyle(defaultStyle);
            }
        }

        return layerEncoder;
    }

    /** @deprecated */
    public boolean publishShp(String workspace, String storeName, NameValuePair[] storeParams, String datasetName, GeoServerRESTPublisher.UploadMethod method, URI shapefile, String srs, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, storeName, storeParams, datasetName, method, shapefile, srs, (String)null, policy, defaultStyle);
    }

    public boolean publishShp(String workspace, String storeName, NameValuePair[] storeParams, String datasetName, GeoServerRESTPublisher.UploadMethod method, URI shapefile, String srs, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, storeName, storeParams, datasetName, method, shapefile, srs, (String)null, GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED, defaultStyle);
    }

    public boolean publishShp(String workspace, String storename, String layerName, File zipFile, String srs, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, storename, (NameValuePair[])null, layerName, GeoServerRESTPublisher.UploadMethod.FILE, zipFile.toURI(), srs, defaultStyle);
    }

    public boolean publishShp(String workspace, String storename, String layername, File zipFile, String srs) throws FileNotFoundException {
        return this.publishShp(workspace, storename, (NameValuePair[])null, layername, GeoServerRESTPublisher.UploadMethod.FILE, zipFile.toURI(), srs, (String)null);
    }

    public boolean publishShp(String workspace, String storename, String layername, File zipFile, String srs, NameValuePair... params) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, storename, params, layername, GeoServerRESTPublisher.UploadMethod.FILE, zipFile.toURI(), srs, (String)null);
    }

    public boolean publishShpCollection(String workspace, String storeName, URI resource) throws FileNotFoundException {
        GeoServerRESTPublisher.UploadMethod method = null;
        String mime = null;
        if (!resource.getScheme().equals("file") && resource.isAbsolute()) {
            try {
                if (resource.toURL() != null) {
                    method = GeoServerRESTPublisher.UploadMethod.URL;
                    mime = "text/plain";
                }
            } catch (MalformedURLException var7) {
                throw new IllegalArgumentException("Resource is not recognized as a zip file, or a directory, or a valid URL", var7);
            }
        } else {
            File f = new File(resource);
            if (f.exists() && f.isFile() && f.toString().endsWith(".zip")) {
                method = GeoServerRESTPublisher.UploadMethod.FILE;
                mime = "application/zip";
            } else if (f.isDirectory()) {
                method = GeoServerRESTPublisher.UploadMethod.EXTERNAL;
                mime = "text/plain";
            }
        }

        return this.createStore(workspace, GeoServerRESTPublisher.StoreType.DATASTORES, storeName, method, GeoServerRESTPublisher.DataStoreExtension.SHP, mime, resource, GeoServerRESTPublisher.ParameterConfigure.ALL);
    }

    private boolean publishCoverage(String workspace, String coveragestore, GeoServerRESTPublisher.CoverageStoreExtension extension, String mimeType, File file, GeoServerRESTPublisher.ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException {
        return this.createCoverageStore(workspace, coveragestore, GeoServerRESTPublisher.UploadMethod.FILE, extension, mimeType, file.toURI(), configure, params);
    }

    private boolean publishExternalCoverage(String workspace, String coveragestore, GeoServerRESTPublisher.CoverageStoreExtension extension, String mimeType, File file, GeoServerRESTPublisher.ParameterConfigure configure, GeoServerRESTPublisher.ParameterUpdate update) throws FileNotFoundException, IllegalArgumentException {
        return this.createCoverageStore(workspace, coveragestore, GeoServerRESTPublisher.UploadMethod.EXTERNAL, extension, mimeType, file.toURI(), configure, update != null ? new NameValuePair[]{new NameValuePair("update", update.toString())} : (NameValuePair[])null);
    }

    public boolean publishArcGrid(String workspace, String storeName, File arcgrid) throws FileNotFoundException {
        return this.publishCoverage(workspace, storeName, GeoServerRESTPublisher.CoverageStoreExtension.ARCGRID, "image/arcgrid", arcgrid, GeoServerRESTPublisher.ParameterConfigure.FIRST, (NameValuePair[])null);
    }

    public boolean publishArcGrid(String workspace, String storeName, String coverageName, File arcgrid) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && arcgrid != null) {
            return this.publishCoverage(workspace, storeName != null ? storeName : FilenameUtils.getBaseName(arcgrid.getAbsolutePath()), GeoServerRESTPublisher.CoverageStoreExtension.ARCGRID, "image/arcgrid", arcgrid, GeoServerRESTPublisher.ParameterConfigure.FIRST, coverageName != null ? new NameValuePair[]{new NameValuePair("coverageName", coverageName)} : (NameValuePair[])null);
        } else {
            throw new IllegalArgumentException("Unable to proceed, some arguments are null");
        }
    }

    public boolean publishArcGrid(String workspace, String storeName, String coverageName, File arcgrid, String srs, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle, double[] bbox) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && storeName != null && arcgrid != null && coverageName != null && srs != null && policy != null && defaultStyle != null) {
            if (!this.createCoverageStore(workspace, storeName != null ? storeName : FilenameUtils.getBaseName(arcgrid.getAbsolutePath()), GeoServerRESTPublisher.UploadMethod.FILE, GeoServerRESTPublisher.CoverageStoreExtension.ARCGRID, "image/arcgrid", arcgrid.toURI(), GeoServerRESTPublisher.ParameterConfigure.NONE, (NameValuePair[])null)) {
                LOGGER.error("Unable to create coverage store for coverage: " + arcgrid);
                return false;
            } else {
                GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
                coverageEncoder.setName(coverageName);
                coverageEncoder.setTitle(coverageName);
                coverageEncoder.setSRS(srs);
                coverageEncoder.setNativeFormat("ArcGrid");
                coverageEncoder.addSupportedFormats("ARCGRID");
                coverageEncoder.addKeyword("arcGrid");
                coverageEncoder.addKeyword("WCS");
                coverageEncoder.setNativeCRS(srs);
                coverageEncoder.setProjectionPolicy(policy);
                coverageEncoder.setRequestSRS(srs);
                coverageEncoder.setResponseSRS(srs);
                if (bbox != null && bbox.length == 4) {
                    coverageEncoder.setLatLonBoundingBox(bbox[0], bbox[1], bbox[2], bbox[3], "EPSG:4326");
                }

                if (!this.createCoverage(workspace, storeName, coverageEncoder)) {
                    LOGGER.error("Unable to create a coverage store for coverage: " + arcgrid);
                    return false;
                } else {
                    GSLayerEncoder layerEncoder = this.configureDefaultStyle(defaultStyle);
                    return this.configureLayer(workspace, coverageName, layerEncoder);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    public boolean publishExternalArcGrid(String workspace, String storeName, File arcgrid, String coverageName, String srs, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && storeName != null && arcgrid != null && coverageName != null && srs != null && policy != null && defaultStyle != null) {
            GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
            coverageEncoder.setName(coverageName);
            coverageEncoder.setTitle(coverageName);
            coverageEncoder.setSRS(srs);
            coverageEncoder.setProjectionPolicy(policy);
            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.setDefaultStyle(defaultStyle);
            return this.publishExternalArcGrid(workspace, storeName, arcgrid, coverageEncoder, layerEncoder) != null;
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    public RESTCoverageStore publishExternalArcGrid(String workspace, String storeName, File arcgrid, GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder) throws IllegalArgumentException, FileNotFoundException {
        if (workspace != null && arcgrid != null && storeName != null && layerEncoder != null && coverageEncoder != null) {
            String coverageName = coverageEncoder.getName();
            if (coverageName.isEmpty()) {
                throw new IllegalArgumentException("Unable to run: empty coverage store name");
            } else {
                boolean store = this.publishExternalCoverage(workspace, storeName, GeoServerRESTPublisher.CoverageStoreExtension.ARCGRID, "text/plain", arcgrid, GeoServerRESTPublisher.ParameterConfigure.NONE, GeoServerRESTPublisher.ParameterUpdate.OVERWRITE);
                if (!store) {
                    return null;
                } else if (!this.createCoverage(workspace, storeName, coverageEncoder)) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Unable to create a coverage for the store:" + coverageName);
                    }

                    return null;
                } else {
                    if (this.configureLayer(workspace, coverageName, layerEncoder)) {
                        try {
                            GeoServerRESTReader reader = new GeoServerRESTReader(this.restURL, this.gsuser, this.gspass);
                            return reader.getCoverageStore(workspace, storeName);
                        } catch (MalformedURLException var10) {
                            LOGGER.error(var10.getMessage(), var10);
                        }
                    }

                    return null;
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    public boolean publishGeoTIFF(String workspace, String storeName, File geotiff) throws FileNotFoundException {
        return this.publishCoverage(workspace, storeName, GeoServerRESTPublisher.CoverageStoreExtension.GEOTIFF, "image/geotiff", geotiff, GeoServerRESTPublisher.ParameterConfigure.FIRST, (NameValuePair[])null);
    }

    public boolean publishGeoTIFF(String workspace, String storeName, String coverageName, File geotiff) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && geotiff != null) {
            return this.publishCoverage(workspace, storeName != null ? storeName : FilenameUtils.getBaseName(geotiff.getAbsolutePath()), GeoServerRESTPublisher.CoverageStoreExtension.GEOTIFF, "image/geotiff", geotiff, GeoServerRESTPublisher.ParameterConfigure.FIRST, coverageName != null ? new NameValuePair[]{new NameValuePair("coverageName", coverageName)} : (NameValuePair[])null);
        } else {
            throw new IllegalArgumentException("Unable to proceed, some arguments are null");
        }
    }

    /** @deprecated */
    public boolean publishGeoTIFF(String workspace, String storeName, String resourceName, File geotiff, String srs, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        return this.publishGeoTIFF(workspace, storeName, resourceName, geotiff, srs, policy, defaultStyle, (double[])null);
    }

    public boolean publishGeoTIFF(String workspace, String storeName, String coverageName, File geotiff, String srs, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle, double[] bbox) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && storeName != null && geotiff != null && coverageName != null && srs != null && policy != null && defaultStyle != null) {
            if (!this.createCoverageStore(workspace, storeName != null ? storeName : FilenameUtils.getBaseName(geotiff.getAbsolutePath()), GeoServerRESTPublisher.UploadMethod.FILE, GeoServerRESTPublisher.CoverageStoreExtension.GEOTIFF, "image/geotiff", geotiff.toURI(), GeoServerRESTPublisher.ParameterConfigure.NONE, (NameValuePair[])null)) {
                LOGGER.error("Unable to create coverage store for coverage: " + geotiff);
                return false;
            } else {
                GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
                coverageEncoder.setName(coverageName);
                coverageEncoder.setTitle(coverageName);
                coverageEncoder.setSRS(srs);
                coverageEncoder.setNativeFormat("GeoTIFF");
                coverageEncoder.addSupportedFormats("GEOTIFF");
                coverageEncoder.addKeyword("geoTiff");
                coverageEncoder.addKeyword("WCS");
                coverageEncoder.setNativeCRS(srs);
                coverageEncoder.setProjectionPolicy(policy);
                coverageEncoder.setRequestSRS(srs);
                coverageEncoder.setResponseSRS(srs);
                if (bbox != null && bbox.length == 4) {
                    coverageEncoder.setLatLonBoundingBox(bbox[0], bbox[1], bbox[2], bbox[3], "EPSG:4326");
                }

                if (!this.createCoverage(workspace, storeName, coverageEncoder)) {
                    LOGGER.error("Unable to create a coverage store for coverage: " + geotiff);
                    return false;
                } else {
                    GSLayerEncoder layerEncoder = this.configureDefaultStyle(defaultStyle);
                    return this.configureLayer(workspace, coverageName, layerEncoder);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    public boolean publishNetCDF(String workspace, String storeName,
                                 String coverageName, File netcdfFile, String srs,
                                 GSResourceEncoder.ProjectionPolicy policy, String defaultStyle, double[] bbox) throws FileNotFoundException, IllegalArgumentException {

        LOGGER.info("I have managed to reach public boolean publishNetCDF");

        if (workspace != null && storeName != null && netcdfFile != null && coverageName != null && srs != null && policy != null && defaultStyle != null) {
            if (!this.createCoverageStore(workspace, storeName != null ? storeName : FilenameUtils.getBaseName(netcdfFile.getAbsolutePath()),
                    GeoServerRESTPublisher.UploadMethod.FILE, GeoServerRESTPublisher.CoverageStoreExtension.GEOTIFF, "application/zip",
                    netcdfFile.toURI(), GeoServerRESTPublisher.ParameterConfigure.NONE, (NameValuePair[])null)) {

                LOGGER.error("Unable to create coverage store for coverage: " + netcdfFile);
                return false;

            } else {

                GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
                coverageEncoder.setName(coverageName);
                coverageEncoder.setTitle(coverageName);
                coverageEncoder.setSRS(srs);
                coverageEncoder.setNativeFormat("NetCDF");
                coverageEncoder.addSupportedFormats("NetCDF");
                coverageEncoder.addKeyword("NetCDF");
                coverageEncoder.addKeyword("WCS");
                coverageEncoder.setNativeCRS(srs);
                coverageEncoder.setProjectionPolicy(policy);
                coverageEncoder.setRequestSRS(srs);
                coverageEncoder.setResponseSRS(srs);
                if (bbox != null && bbox.length == 4) {
                    coverageEncoder.setLatLonBoundingBox(bbox[0], bbox[1], bbox[2], bbox[3], "EPSG:4326");
                }

                if (!this.createCoverage(workspace, storeName, coverageEncoder)) {
                    LOGGER.error("Unable to create a coverage store for coverage: " + netcdfFile);
                    return false;
                } else {
                    GSLayerEncoder layerEncoder = this.configureDefaultStyle(defaultStyle);
                    return this.configureLayer(workspace, coverageName, layerEncoder);
                }

            }
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }

    }

    public boolean publishExternalGeoTIFF(String workspace, String storeName, File geotiff, String coverageName, String srs, GSResourceEncoder.ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        if (workspace != null && storeName != null && geotiff != null && coverageName != null && srs != null && policy != null && defaultStyle != null) {
            GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
            coverageEncoder.setName(coverageName);
            coverageEncoder.setTitle(coverageName);
            coverageEncoder.setSRS(srs);
            coverageEncoder.setProjectionPolicy(policy);
            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.setDefaultStyle(defaultStyle);
            return this.publishExternalGeoTIFF(workspace, storeName, geotiff, coverageEncoder, layerEncoder) != null;
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    public RESTCoverageStore publishExternalGeoTIFF(String workspace, String storeName, File geotiff, GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder) throws IllegalArgumentException, FileNotFoundException {
        if (workspace != null && geotiff != null && storeName != null && layerEncoder != null && coverageEncoder != null) {
            String coverageName = coverageEncoder.getName();
            if (coverageName.isEmpty()) {
                throw new IllegalArgumentException("Unable to run: empty coverage store name");
            } else {
                boolean store = this.publishExternalCoverage(workspace, storeName, GeoServerRESTPublisher.CoverageStoreExtension.GEOTIFF, "text/plain", geotiff, GeoServerRESTPublisher.ParameterConfigure.NONE, GeoServerRESTPublisher.ParameterUpdate.OVERWRITE);
                if (!store) {
                    return null;
                } else if (!this.createCoverage(workspace, storeName, coverageEncoder)) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Unable to create a coverage for the store:" + coverageName);
                    }

                    return null;
                } else {
                    if (this.configureLayer(workspace, coverageName, layerEncoder)) {
                        try {
                            GeoServerRESTReader reader = new GeoServerRESTReader(this.restURL, this.gsuser, this.gspass);
                            return reader.getCoverageStore(workspace, storeName);
                        } catch (MalformedURLException var10) {
                            LOGGER.error(var10.getMessage(), var10);
                        }
                    }

                    return null;
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }
    }

    public boolean publishWorldImage(String workspace, String coveragestore, File zipFile) throws FileNotFoundException {
        return this.publishWorldImage(workspace, coveragestore, zipFile, GeoServerRESTPublisher.ParameterConfigure.FIRST, (NameValuePair)null);
    }

    public boolean publishWorldImage(String workspace, String coveragestore, File zipFile, GeoServerRESTPublisher.ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException {
        return this.publishCoverage(workspace, coveragestore, GeoServerRESTPublisher.CoverageStoreExtension.WORLDIMAGE, "application/zip", zipFile, configure, params);
    }

    public boolean publishImageMosaic(String workspace, String storeName, File zipFile) throws FileNotFoundException {
        return this.publishCoverage(workspace, storeName, GeoServerRESTPublisher.CoverageStoreExtension.IMAGEMOSAIC, "application/zip", zipFile, GeoServerRESTPublisher.ParameterConfigure.FIRST, (NameValuePair[])null);
    }

    public boolean publishImageMosaic(String workspace, String storeName, File zipFile, GeoServerRESTPublisher.ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException {
        return this.publishCoverage(workspace, storeName, GeoServerRESTPublisher.CoverageStoreExtension.IMAGEMOSAIC, "application/zip", zipFile, configure, params);
    }

    public RESTCoverageStore createExternaMosaicDatastore(String workspace, String storeName, File mosaicDir, GeoServerRESTPublisher.ParameterConfigure configure, GeoServerRESTPublisher.ParameterUpdate update) throws FileNotFoundException {
        if (!mosaicDir.isDirectory() && LOGGER.isWarnEnabled()) {
            LOGGER.warn("Directory '" + mosaicDir + "' not exists locally. Continue: please check existance on the remote server.");
        }

        String sUrl = this.restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storeName + "/external.imagemosaic?configure=" + configure.toString() + "&update=" + update.toString();
        String sendResult = HTTPUtils.put(sUrl, mosaicDir.toURI().toString(), "text/plain", this.gsuser, this.gspass);
        return RESTCoverageStore.build(sendResult);
    }

    public boolean publishExternalMosaic(String workspace, String storeName, File mosaicDir, String srs, String defaultStyle) throws FileNotFoundException {
        GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setSRS(srs);
        String name = FilenameUtils.getBaseName(mosaicDir.getName());
        coverageEncoder.setName(name);
        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setDefaultStyle(defaultStyle);
        return this.publishExternalMosaic(workspace, storeName, mosaicDir, coverageEncoder, layerEncoder);
    }

    /** @deprecated */
    public boolean createExternalMosaic(String workspace, String storeName, File mosaicDir, GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder) throws FileNotFoundException {
        return this.publishExternalMosaic(workspace, storeName, mosaicDir, coverageEncoder, layerEncoder);
    }

    public boolean publishExternalMosaic(String workspace, String storeName, File mosaicDir, GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder) throws FileNotFoundException, IllegalArgumentException {
        if (coverageEncoder == null) {
            throw new IllegalArgumentException("no coverageEncoder provided for mosaic " + mosaicDir);
        } else if (layerEncoder == null) {
            throw new IllegalArgumentException("no layerEncoder provided for " + mosaicDir);
        } else {
            RESTCoverageStore store = this.createExternaMosaicDatastore(workspace, storeName, mosaicDir, GeoServerRESTPublisher.ParameterConfigure.NONE, GeoServerRESTPublisher.ParameterUpdate.OVERWRITE);
            if (store == null) {
                return false;
            } else {
                String coverageName = coverageEncoder.getName();
                if (coverageName == null) {
                    coverageName = mosaicDir.getName();
                    coverageEncoder.setName(coverageName);
                }

                if (!this.createCoverage(workspace, storeName, coverageEncoder)) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Unable to create a coverage for the store:" + coverageName);
                    }

                    return false;
                } else if (!this.configureLayer(workspace, coverageName, layerEncoder)) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Unable to configure the Layer for the coverage:" + coverageName);
                    }

                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public boolean unpublishCoverage(String workspace, String storename, String layerName) {
        try {
            String fqLayerName;
            if (workspace == null) {
                fqLayerName = layerName;
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Null workspace while configuring layer : " + layerName + " -- This behavior is deprecated.");
                }
            } else {
                fqLayerName = workspace + ":" + layerName;
            }

            URL deleteLayerUrl = new URL(this.restURL + "/rest/layers/" + fqLayerName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Going to delete /rest/layers/" + fqLayerName);
            }

            boolean layerDeleted = HTTPUtils.delete(deleteLayerUrl.toExternalForm(), this.gsuser, this.gspass);
            if (!layerDeleted) {
                LOGGER.warn("Could not delete layer '" + fqLayerName + "'");
                return false;
            } else {
                URL deleteCovUrl = new URL(this.restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storename + "/coverages/" + layerName);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Going to delete /rest/workspaces/" + workspace + "/coveragestores/" + storename + "/coverages/" + layerName);
                }

                boolean covDeleted = HTTPUtils.delete(deleteCovUrl.toExternalForm(), this.gsuser, this.gspass);
                if (!covDeleted) {
                    LOGGER.warn("Could not delete coverage " + workspace + ":" + storename + "/" + layerName + ", but layer was deleted.");
                } else {
                    LOGGER.info("Coverage successfully deleted " + workspace + ":" + storename + "/" + layerName);
                }

                return covDeleted;
            }
        } catch (MalformedURLException var9) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(var9.getLocalizedMessage(), var9);
            }

            return false;
        }
    }

    public boolean unpublishFeatureType(String workspace, String storename, String layerName) {
        try {
            String fqLayerName;
            if (workspace == null) {
                fqLayerName = layerName;
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Null workspace while configuring layer : " + layerName + " -- This behavior is deprecated.");
                }
            } else {
                fqLayerName = workspace + ":" + layerName;
            }

            URL deleteLayerUrl = new URL(this.restURL + "/rest/layers/" + fqLayerName);
            boolean layerDeleted = HTTPUtils.delete(deleteLayerUrl.toExternalForm(), this.gsuser, this.gspass);
            if (!layerDeleted) {
                LOGGER.warn("Could not delete layer '" + fqLayerName + "'");
                return false;
            } else {
                URL deleteFtUrl = new URL(this.restURL + "/rest/workspaces/" + workspace + "/datastores/" + storename + "/featuretypes/" + layerName);
                boolean ftDeleted = HTTPUtils.delete(deleteFtUrl.toExternalForm(), this.gsuser, this.gspass);
                if (!ftDeleted) {
                    LOGGER.warn("Could not delete featuretype " + workspace + ":" + storename + "/" + layerName + ", but layer was deleted.");
                } else {
                    LOGGER.info("FeatureType successfully deleted " + workspace + ":" + storename + "/" + layerName);
                }

                return ftDeleted;
            }
        } catch (MalformedURLException var9) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(var9.getLocalizedMessage(), var9);
            }

            return false;
        }
    }

    /** @deprecated */
    public boolean removeDatastore(String workspace, String storename) {
        try {
            return this.removeDatastore(workspace, storename, true);
        } catch (IllegalArgumentException var4) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Arguments may not be null or empty!", var4);
            }

            return false;
        }
    }

    public boolean removeDatastore(String workspace, String storename, boolean recurse) throws IllegalArgumentException {
        return this.removeStore(workspace, storename, GeoServerRESTPublisher.StoreType.DATASTORES, recurse, GeoServerRESTPublisher.Purge.NONE);
    }

    public boolean removeDatastore(String workspace, String storename, boolean recurse, GeoServerRESTPublisher.Purge purge) throws IllegalArgumentException {
        return this.removeStore(workspace, storename, GeoServerRESTPublisher.StoreType.DATASTORES, recurse, purge);
    }

    /** @deprecated */
    public boolean removeCoverageStore(String workspace, String storename) {
        return this.removeCoverageStore(workspace, storename, true);
    }

    public boolean removeCoverageStore(String workspace, String storename, boolean recurse) throws IllegalArgumentException {
        return this.removeStore(workspace, storename, GeoServerRESTPublisher.StoreType.COVERAGESTORES, recurse, GeoServerRESTPublisher.Purge.NONE);
    }

    public boolean removeCoverageStore(String workspace, String storename, boolean recurse, GeoServerRESTPublisher.Purge purge) throws IllegalArgumentException {
        return this.removeStore(workspace, storename, GeoServerRESTPublisher.StoreType.COVERAGESTORES, recurse, purge);
    }

    private boolean removeStore(String workspace, String storename, GeoServerRESTPublisher.StoreType type, boolean recurse, GeoServerRESTPublisher.Purge purge) throws IllegalArgumentException {
        try {
            if (workspace != null && storename != null) {
                if (!workspace.isEmpty() && !storename.isEmpty()) {
                    StringBuilder url = new StringBuilder(this.restURL);
                    url.append("/rest/workspaces/").append(workspace).append("/").append(type).append("/").append(storename);
                    url.append("?recurse=").append(recurse);
                    if (purge != null) {
                        url.append("&purge=").append(purge);
                    }

                    URL deleteStore = new URL(url.toString());
                    boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(), this.gsuser, this.gspass);
                    if (!deleted) {
                        LOGGER.warn("Could not delete store " + workspace + ":" + storename);
                    } else {
                        LOGGER.info("Store successfully deleted " + workspace + ":" + storename);
                    }

                    return deleted;
                } else {
                    throw new IllegalArgumentException("Arguments may not be empty!");
                }
            } else {
                throw new IllegalArgumentException("Arguments may not be null!");
            }
        } catch (MalformedURLException var9) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(var9.getLocalizedMessage(), var9);
            }

            return false;
        }
    }

    /** @deprecated */
    public boolean removeWorkspace(String workspace) {
        return this.removeWorkspace(workspace, false);
    }

    public boolean removeWorkspace(String workspace, boolean recurse) throws IllegalArgumentException {
        workspace = this.sanitize(workspace);

        try {
            if (workspace == null) {
                throw new IllegalArgumentException("Arguments may not be null!");
            } else if (workspace.isEmpty()) {
                throw new IllegalArgumentException("Arguments may not be empty!");
            } else {
                StringBuffer url = (new StringBuffer(this.restURL)).append("/rest/workspaces/").append(workspace);
                if (recurse) {
                    url.append("?recurse=true");
                }

                this.deleteStylesForWorkspace(workspace);
                URL deleteUrl = new URL(url.toString());
                boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(), this.gsuser, this.gspass);
                if (!deleted) {
                    LOGGER.warn("Could not delete Workspace " + workspace);
                } else {
                    LOGGER.info("Workspace successfully deleted " + workspace);
                }

                return deleted;
            }
        } catch (MalformedURLException var6) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(var6.getLocalizedMessage(), var6);
            }

            return false;
        }
    }

    private void deleteStylesForWorkspace(String workspace) {
        RESTStyleList styles = this.styleManager.getStyles(workspace);
        if (styles != null) {
            Iterator i$ = styles.iterator();

            while(i$.hasNext()) {
                NameLinkElem nameLinkElem = (NameLinkElem)i$.next();
                this.removeStyleInWorkspace(workspace, nameLinkElem.getName(), true);
            }

        }
    }

    public boolean removeLayerGroup(String workspace, String name) {
        String url = this.restURL + "/rest";
        if (workspace == null) {
            url = url + "/layergroups/" + name;
        } else {
            url = url + "/workspaces/" + workspace + "/layergroups/" + name;
        }

        try {
            URL deleteUrl = new URL(url);
            boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(), this.gsuser, this.gspass);
            if (!deleted) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Could not delete layergroup " + name);
                }
            } else if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Layergroup successfully deleted: " + name);
            }

            return deleted;
        } catch (MalformedURLException var6) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(var6.getLocalizedMessage(), var6);
            }

            return false;
        }
    }

    public boolean removeLayerGroup(String name) {
        return this.removeLayerGroup((String)null, name);
    }

    public boolean removeLayer(String workspace, String layerName) {
        String fqLayerName;
        if (workspace == null) {
            fqLayerName = layerName;
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Null workspace while removing layer : " + layerName + " -- This behavior is deprecated.");
            }
        } else {
            fqLayerName = workspace + ":" + layerName;
        }

        if (layerName == null) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Null layerName : " + layerName);
            }

            return false;
        } else {
            String url = this.restURL + "/rest/layers/" + fqLayerName;
            boolean result = HTTPUtils.delete(url, this.gsuser, this.gspass);
            if (result) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Layer successfully removed: " + fqLayerName);
                }
            } else if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error removing layer " + fqLayerName);
            }

            return result;
        }
    }

    public boolean reloadStore(String workspace, String storeName, GeoServerRESTPublisher.StoreType storeType) throws IllegalArgumentException, MalformedURLException {
        String url = HTTPUtils.append(new String[]{this.restURL, "/rest/workspaces/", workspace, "/", storeType.toString(), "/", storeName, ".xml"}).toString();
        String store = HTTPUtils.get(url, this.gsuser, this.gspass);
        if (store != null) {
            String storeTag = storeType.getTypeName();
            String startTag = "<" + storeTag + ">";
            int start = store.indexOf(startTag);
            String endTag = "</" + storeTag + ">";
            int stop = store.indexOf(endTag) + endTag.length();
            return HTTPUtils.putXml(url, store.subSequence(0, start) + store.substring(stop), this.gsuser, this.gspass) != null;
        } else {
            return false;
        }
    }

    public boolean reload() {
        String sUrl = this.restURL + "/rest/reload";
        String result = HTTPUtils.post(sUrl, "", "text/plain", this.gsuser, this.gspass);
        return result != null;
    }

    public boolean reset() {
        String sUrl = this.restURL + "/rest/reset";
        String result = HTTPUtils.post(sUrl, "", "text/plain", this.gsuser, this.gspass);
        return result != null;
    }

    public boolean configureLayer(String workspace, String resourceName, GSLayerEncoder layer) throws IllegalArgumentException {
        if (workspace != null && resourceName != null && layer != null) {
            if (!workspace.isEmpty() && !resourceName.isEmpty() && !layer.isEmpty()) {
                String fqLayerName = workspace + ":" + resourceName;
                String url = this.restURL + "/rest/layers/" + fqLayerName;
                String layerXml = layer.toString();
                String sendResult = HTTPUtils.putXml(url, layerXml, this.gsuser, this.gspass);
                if (sendResult != null) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Layer successfully configured: " + fqLayerName);
                    }
                } else if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error configuring layer " + fqLayerName + " (" + sendResult + ")");
                }

                return sendResult != null;
            } else {
                throw new IllegalArgumentException("Empty argument");
            }
        } else {
            throw new IllegalArgumentException("Null argument");
        }
    }

    public boolean createLayerGroup(String name, GSLayerGroupEncoder group) {
        return this.createLayerGroup((String)null, name, group);
    }

    public boolean createLayerGroup(String workspace, String name, GSLayerGroupEncoder group) {
        String url = this.restURL + "/rest";
        if (workspace == null) {
            url = url + "/layergroups/";
        } else {
            group.setWorkspace(workspace);
            url = url + "/workspaces/" + workspace + "/layergroups/";
        }

        group.setName(name);
        String sendResult = HTTPUtils.postXml(url, group.toString(), this.gsuser, this.gspass);
        if (sendResult != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("LayerGroup successfully configured: " + name);
            }
        } else if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Error configuring LayerGroup " + name + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    public boolean configureLayerGroup(String name, GSLayerGroupEncoder group) {
        return this.configureLayerGroup((String)null, name, group);
    }

    public boolean configureLayerGroup(String workspace, String name, GSLayerGroupEncoder group) {
        String url = this.restURL + "/rest";
        if (workspace == null) {
            url = url + "/layergroups/" + name;
        } else {
            url = url + "/workspaces/" + workspace + "/layergroups/" + name;
        }

        String sendResult = HTTPUtils.putXml(url, group.toString(), this.gsuser, this.gspass);
        if (sendResult != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("LayerGroup successfully configured: " + name);
            }
        } else if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Error configuring LayerGroup " + name + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    public boolean configureCoverage(GSCoverageEncoder ce, String wsname, String csname) {
        return this.configureCoverage(ce, wsname, csname, ce.getName());
    }

    public boolean configureCoverage(GSCoverageEncoder ce, String wsname, String csname, String coverageName) {
        if (coverageName == null) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unable to configure a coverage with no name try using GSCoverageEncoder.setName(String)");
            }

            return false;
        } else {
            GeoServerRESTReader reader;
            try {
                reader = new GeoServerRESTReader(this.restURL, this.gsuser, this.gspass);
            } catch (MalformedURLException var10) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(var10.getLocalizedMessage(), var10);
                }

                return false;
            }

            RESTCoverage coverage = reader.getCoverage(wsname, csname, coverageName);
            if (coverage == null) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("No coverages found in new coveragestore " + csname + " called " + coverageName);
                }

                return false;
            } else {
                String url = this.restURL + "/rest/workspaces/" + wsname + "/coveragestores/" + csname + "/coverages/" + coverageName + ".xml";
                String xmlBody = ce.toString();
                String sendResult = HTTPUtils.putXml(url, xmlBody, this.gsuser, this.gspass);
                if (sendResult != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Coverage successfully configured " + wsname + ":" + csname + ":" + coverageName);
                    }
                } else if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error configuring coverage " + wsname + ":" + csname + ":" + coverageName + " (" + sendResult + ")");
                }

                return sendResult != null;
            }
        }
    }

    /** @deprecated */
    public boolean createCoverage(GSCoverageEncoder ce, String wsname, String csname) {
        return this.createCoverage(wsname, csname, ce);
    }

    public boolean createCoverage(String wsname, String storeName, GSCoverageEncoder ce) throws IllegalArgumentException {
        return this.createResource(wsname, GeoServerRESTPublisher.StoreType.COVERAGESTORES, storeName, ce);
    }

    private boolean createResource(String workspace, GeoServerRESTPublisher.StoreType dsType, String storeName, GSResourceEncoder re) throws IllegalArgumentException {
        if (workspace != null && dsType != null && storeName != null && re != null) {
            StringBuilder sbUrl = (new StringBuilder(this.restURL)).append("/rest/workspaces/").append(workspace).append("/").append(dsType).append("/").append(storeName).append("/").append(dsType.getTypeNameWithFormat(GeoServerRESTPublisher.Format.XML));
            String resourceName = re.getName();
            if (resourceName == null) {
                throw new IllegalArgumentException("Unable to configure a coverage using unnamed coverage encoder");
            } else {
                String xmlBody = re.toString();
                String sendResult = HTTPUtils.postXml(sbUrl.toString(), xmlBody, this.gsuser, this.gspass);
                if (sendResult != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(dsType + " successfully created " + workspace + ":" + storeName + ":" + resourceName);
                    }
                } else if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Error creating coverage " + workspace + ":" + storeName + ":" + resourceName + " (" + sendResult + ")");
                }

                return sendResult != null;
            }
        } else {
            throw new IllegalArgumentException("Null argument");
        }
    }

    public boolean createNetCDFCoverageStore(String workspace, String storeName, String xmlBody){
        if(workspace == null || xmlBody == null || storeName == null)
            throw new IllegalArgumentException("Null argument");

        StringBuilder sbUrl = new StringBuilder(this.restURL).append("/rest/workspaces/").append(workspace).append("/coveragestores?configure=all");
        LOGGER.info("Submitting request to geoserver at URL: " + sbUrl.toString());
        String sendResult = HTTPUtils.postXml(sbUrl.toString(), xmlBody, this.gsuser, this.gspass);
        LOGGER.info("Result from request: " + sendResult);

        if (sendResult != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Coverage successfully created " + workspace + ":" + storeName);
            }
        } else if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Error creating coverage " + workspace + ":" + storeName + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    public boolean createNetCDFLayerFromFile(String workspace, String storeName, File netCDFFile){
        if(workspace == null || storeName == null)
            throw new IllegalArgumentException("Null argument");

        StringBuilder sbUrl = new StringBuilder(this.restURL).append("/rest/workspaces/").append(workspace).append("/coveragestores/").append(storeName).append("/file.netcdf");
        LOGGER.info("Submitting request to geoserver at: " + sbUrl.toString());

        String sendResult = HTTPUtils.put(sbUrl.toString(), netCDFFile, "application/zip", this.gsuser, this.gspass);
        LOGGER.info("Result from request: " + sendResult);

        if (sendResult != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Coverage successfully created " + workspace + ":" + storeName);
            }
        } else if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Error creating coverage " + workspace + ":" + storeName + " (" + sendResult + ")");
        }

        return sendResult != null;

    }

    protected String sanitize(String s) {
        return s.indexOf(".") != -1 ? s + ".DUMMY" : s;
    }

    private String appendParameters(NameValuePair... params) {
        StringBuilder sbUrl = new StringBuilder();
        if (params != null) {
            int paramsSize = params.length;
            if (paramsSize > 0) {
                int i = 0;
                NameValuePair param = params[i];

                while(true) {
                    String name;
                    String value;
                    while(param != null && i++ < paramsSize) {
                        name = param.getName();
                        value = param.getValue();
                        if (name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
                            sbUrl.append(name).append("=").append(value);
                            param = null;
                        } else {
                            param = params[i];
                        }
                    }

                    for(; i < paramsSize; ++i) {
                        param = params[i];
                        if (param != null) {
                            name = param.getName();
                            value = param.getValue();
                            sbUrl.append(name).append("=").append(value);
                            if (name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
                                sbUrl.append("&").append(name).append("=").append(value);
                            }
                        }
                    }
                    break;
                }
            }
        }

        return sbUrl.toString();
    }

    protected String encode(String s) {
        return URLEncoder.encode(s);
    }

    public boolean harvestExternal(String workspace, String coverageStore, String format, String path) {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = new GeoServerRESTStructuredGridCoverageReaderManager(new URL(this.restURL), this.gsuser, this.gspass);
            return manager.harvestExternal(workspace, coverageStore, format, path);
        } catch (IllegalArgumentException var6) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(var6.getLocalizedMessage(), var6);
            }
        } catch (MalformedURLException var7) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(var7.getLocalizedMessage(), var7);
            }
        }

        return false;
    }

    public boolean createImageMosaic(String workspace, String coverageStore, String path) {
        return this.createImageMosaic(workspace, coverageStore, path, GeoServerRESTStructuredGridCoverageReaderManager.ConfigureCoveragesOption.ALL);
    }

    public boolean createImageMosaic(String workspace, String coverageStore, String path, GeoServerRESTStructuredGridCoverageReaderManager.ConfigureCoveragesOption configureOpt) {
        checkString(workspace);
        checkString(coverageStore);
        checkString(path);
        File zipFile = new File(path);
        if (zipFile.exists() && zipFile.isFile() && zipFile.canRead()) {
            ZipFile zip = null;

            try {
                zip = new ZipFile(zipFile);
                zip.getName();
            } catch (Exception var15) {
                LOGGER.trace(var15.getLocalizedMessage(), var15.getStackTrace());
                throw new IllegalArgumentException("The provided pathname does not point to a valide zip file: " + path);
            } finally {
                if (zip != null) {
                    try {
                        zip.close();
                    } catch (IOException var14) {
                        LOGGER.trace(var14.getLocalizedMessage(), var14.getStackTrace());
                    }
                }

            }

            StringBuilder ss = HTTPUtils.append(new String[]{this.restURL, "/rest/workspaces/", workspace, "/coveragestores/", coverageStore, "/", GeoServerRESTPublisher.UploadMethod.EXTERNAL.toString(), ".imagemosaic"});
            switch(configureOpt) {
                case NONE:
                    ss.append("?configure=none");
                case ALL:
                    String sUrl = ss.toString();
                    String result = HTTPUtils.put(sUrl, zipFile, "application/zip", this.gsuser, this.gspass);
                    return result != null;
                default:
                    throw new IllegalArgumentException("Unrecognized COnfigureOption: " + configureOpt);
            }
        } else {
            throw new IllegalArgumentException("The provided pathname does not point to a valide zip file: " + path);
        }
    }

    public boolean removeGranuleById(String workspace, String coverageStore, String coverage, String granuleId) {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = new GeoServerRESTStructuredGridCoverageReaderManager(new URL(this.restURL), this.gsuser, this.gspass);
            return manager.removeGranuleById(workspace, coverageStore, coverage, granuleId);
        } catch (IllegalArgumentException var6) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(var6.getLocalizedMessage(), var6);
            }
        } catch (MalformedURLException var7) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(var7.getLocalizedMessage(), var7);
            }
        }

        return false;
    }

    public boolean removeGranulesByCQL(String workspace, String coverageStore, String coverage, String filter) throws UnsupportedEncodingException {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = new GeoServerRESTStructuredGridCoverageReaderManager(new URL(this.restURL), this.gsuser, this.gspass);
            return manager.removeGranulesByCQL(workspace, coverageStore, coverage, filter);
        } catch (IllegalArgumentException var6) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(var6.getLocalizedMessage(), var6);
            }
        } catch (MalformedURLException var7) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(var7.getLocalizedMessage(), var7);
            }
        }

        return false;
    }

    private static void checkString(String string) {
        if (string == null) {
            throw new NullPointerException("Provided string is is null!");
        } else if (string.length() <= 0) {
            throw new IllegalArgumentException("Provided string is is empty!");
        }
    }

    public static enum Purge {
        NONE,
        METADATA,
        ALL;

        private Purge() {
        }
    }

    public static enum Format {
        XML,
        JSON,
        HTML,
        SLD,
        SLD_1_1_0;

        private Format() {
        }

        public static String getContentType(GeoServerRESTPublisher.Format f) {
            switch(f) {
                case XML:
                    return "application/xml";
                case HTML:
                    return "application/html";
                case JSON:
                    return "application/json";
                case SLD:
                    return "application/vnd.ogc.sld+xml";
                case SLD_1_1_0:
                    return "application/vnd.ogc.se+xml";
                default:
                    return null;
            }
        }

//        public String getContentType() {
//            return getContentType(this);
//        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static enum ParameterUpdate {
        APPEND,
        OVERWRITE;

        private ParameterUpdate() {
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static enum ParameterConfigure {
        FIRST,
        NONE,
        ALL;

        private ParameterConfigure() {
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static enum CoverageStoreExtension {
        GEOTIFF,
        IMAGEMOSAIC,
        WORLDIMAGE,
        ARCGRID;

        private CoverageStoreExtension() {
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static enum DataStoreExtension {
        SHP,
        PROPERTIES,
        H2,
        SPATIALITE;

        private DataStoreExtension() {
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static enum UploadMethod {
        FILE,
        /** @deprecated */
        @Deprecated
        file,
        URL,
        /** @deprecated */
        @Deprecated
        url,
        EXTERNAL,
        /** @deprecated */
        @Deprecated
        external;

        private UploadMethod() {
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static enum StoreType {
        COVERAGESTORES,
        DATASTORES;

        private StoreType() {
        }

        public static String getTypeNameWithFormat(GeoServerRESTPublisher.StoreType type, GeoServerRESTPublisher.Format format) {
            return getTypeName(type) + "." + format;
        }

//        public String getTypeNameWithFormat(GeoServerRESTPublisher.Format format) {
//            return getTypeName(this).toLowerCase() + "." + format;
//        }

        public static String getTypeName(GeoServerRESTPublisher.StoreType type) {
            switch(type) {
                case COVERAGESTORES:
                    return "coverages";
                case DATASTORES:
                    return "featureTypes";
                default:
                    return "coverages";
            }
        }

        public static String getType(GeoServerRESTPublisher.StoreType type) {
            switch(type) {
                case COVERAGESTORES:
                    return "coverageStore";
                case DATASTORES:
                    return "dataStore";
                default:
                    return "coverageStore";
            }
        }

//        public String getTypeName() {
//            return getTypeName(this);
//        }

//        public String getType() {
//            return getType(this);
//        }

//        public String toString() {
//            return this.name().toLowerCase();
//        }
    }

    /** @deprecated */
//    public static enum DataStoreType {
//        COVERAGESTORES,
//        DATASTORES;
//
//        private DataStoreType() {
//        }
//
//        /** @deprecated */
//        public static String getTypeName(GeoServerRESTPublisher.StoreType type) {
//            return GeoServerRESTPublisher.StoreType.getTypeNameWithFormat(type, GeoServerRESTPublisher.Format.XML);
//        }
//
//        /** @deprecated */
//        public String toString() {
//            return this.name().toLowerCase();
//        }
//    }
}
