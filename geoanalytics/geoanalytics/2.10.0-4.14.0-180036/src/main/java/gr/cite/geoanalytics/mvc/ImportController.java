package gr.cite.geoanalytics.mvc;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gr.cite.gaap.datatransferobjects.*;
import gr.cite.gaap.datatransferobjects.layeroperations.UserWorkspaceLayerPersist;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.geospatialbackend.GeospatialServices;
import gr.cite.geoanalytics.manager.UserWorkspaceLayersmanager;
import org.apache.commons.io.IOUtils;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

import gr.cite.gaap.datatransferobjects.request.GeoNetworkMetadataDTO;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.util.http.CustomResponseEntity;

@Controller
@RequestMapping("/")
public class ImportController extends BaseController {

    @Autowired
    private ImportManager importManager;
    @Autowired
    private LayerManager layerManager;
    @Autowired
    private SecurityContextAccessor securityContextAccessor;
    @Autowired
    private UserWorkspaceLayersmanager userWorkspaceLayersmanager;

    @Resource(name = "shapefileDefaultEncoding")
    private String shapefileDefaultEncoding;

    @Autowired
    private GeospatialServices geospatialServices;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ImportController.class);

    @RequestMapping(value = "import/getCapabilities", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ResponseEntity<?> getCapabilities(WfsRequestMessenger requestMessenger, HttpServletRequest request) {
        logger.debug("Getting capabilities...");

        String tenant = request.getHeader("tenant");
        List<LayerMessenger> featureTypes = null;

        try {
            requestMessenger.validate();

            featureTypes = importManager.doCapabilities(requestMessenger, tenant, false);

            if (featureTypes == null) {
                throw new CustomException(INTERNAL_SERVER_ERROR, "An error occured with WFS data publisher. URL might be invalid");
            }
        } catch (CustomException e) {
            return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Error while Getting Capabilities. URL might be invalid", e);
        }

        logger.debug("Getting capabilities has succeeded");

        return new CustomResponseEntity<List<LayerMessenger>>(OK, featureTypes);
    }

    @RequestMapping(value = "import/storeShapeFilesForFeatureType", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseEntity<?> storeShapeFilesForFeatureType(@RequestBody WfsRequestMessenger reqM, HttpServletRequest request) throws Exception {
        logger.debug("Import WFS request...");

        Map<String, InputStream> map = null;

        try {
            Tenant tenant = this.getSecurityContextAccessor().getTenant();

            if (reqM.getLayersInfo() == null || reqM.getLayersInfo().isEmpty()) {
                throw new CustomException(INTERNAL_SERVER_ERROR, "No feature types to insert");
            }

            for (WfsRequestLayer layerInfo : reqM.getLayersInfo()) {
                layerInfo.DecodeToUTF8();

                String featureType = layerInfo.getFeatureTypes();
                map = importManager.doWfsCall(reqM, featureType);

                if (map == null) {
                    throw new CustomException(INTERNAL_SERVER_ERROR, "No valid files from geoserver. Maybe files are corrupted");
                }

                File tf = Files.createTempDir();
                for (Map.Entry<String, InputStream> entry : map.entrySet()) {
                    File file = new File(tf.getAbsolutePath() + "/" + entry.getKey());
                    file.createNewFile();

                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = entry.getValue().read(bytes)) >= 0) {
                        fos.write(bytes, 0, length);
                    }
                    fos.close();
                }

                LayerImport layerImport = importManager.createWfsLayerImport(layerInfo, reqM.getUrl());

                importManager.importWfsLayer(layerImport, layerInfo, tf.getAbsolutePath(), reqM, featureType, tenant);
            }

            logger.info("Import request has been submitted successfully!");

            return new CustomResponseEntity<String>(OK, "Import request has been submitted successfully!");
        } catch (CustomException e) {
            return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Error while Getting Capabilities. URL might be invalid", e);
        } finally {
            if (map != null && !map.isEmpty()) {
                map.forEach((name, is) -> {
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error(null, e);
                    }
                });
            }
        }
    }

    @RequestMapping(value = {"import/tsv"}, method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces = {"application/json"})
    public @ResponseBody
    ResponseEntity<?> importTsv(MultipartHttpServletRequest request, HttpServletResponse response) {
        logger.debug("Import TSV request...");

        try {
            Tenant tenant = this.getSecurityContextAccessor().getTenant();
//			InputStream tsvInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
            String tsvFileData = request.getParameter("tsvFileData");
            String tsvData = null;
            if(tsvFileData != null) {
                tsvData = URLDecoder.decode(tsvFileData, "UTF-8");
                //IOUtils.toString(tsvInputStream, "UTF-8");
            }

            String userWorkspaceLayerJSON = request.getParameter("userWorkspaceLayerFileDescriptor");
            String propertiesJson = request.getParameter("tsvImportProperties");
            String metadataJson = request.getParameter("tsvImportMetadata");

            TsvImportProperties properties = mapper.readValue(propertiesJson, TsvImportProperties.class);
            properties.validate();
            properties.DecodeToUTF8();

            GeoNetworkMetadataDTO metadata = null;

            if (metadataJson != null) {
                metadata = mapper.readValue(metadataJson, GeoNetworkMetadataDTO.class);
                metadata.validate();
                metadata.DecodeToUTF8();
            }

            LayerImport layerImport = importManager.createTsvLayerImport(properties);

            if(tsvData != null)
                importManager.importTsvLayer(layerImport, metadata, tsvData, properties, tenant);

            if(userWorkspaceLayerJSON != null) {
                String scope = this.getSecurityContextAccessor().getTenant().getName();
                String token = request.getHeader(this.GCUBE_TOKEN_HEADER_KEY);
                logger.info("Token: " + token	+ " scope: " + scope );

                UserWorkspaceLayerPersist uWL = mapper.readValue(userWorkspaceLayerJSON, UserWorkspaceLayerPersist.class);
                InputStream tsvInputStream = userWorkspaceLayersmanager.downloadStreamOfUserWorkspaceFile(scope, token, uWL.getId());
                UserWorkspaceUtil wsUtil = new UserWorkspaceUtil(token, scope, uWL);
                importManager.importTsvLayer(layerImport, metadata, tsvInputStream, properties, tenant, wsUtil);

            }
        } catch (CustomException e) {
            return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Something went wrong with the import. Please try again later", e);
        }

        logger.info("TSV import request has been submitted successfully!");

        return new CustomResponseEntity<String>(OK, "TSV Import request has been submitted successfully!");
    }

    @RequestMapping(value = "import/shapeFile", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public @ResponseBody
    ResponseEntity<?> importShapefile(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Import Shapefile request...");

        Map<String, InputStream> map = null;
        String successMessage;

        try {
            Tenant tenant = this.getSecurityContextAccessor().getTenant();
            boolean cpgNotFound = true;

            String userWorkspaceLayerJSON = request.getParameter("userWorkspaceLayerFileDescriptor");
            String metadataJson = request.getParameter("shapefileImportMetadata");
            String propertiesJson = request.getParameter("shapefileImportProperties");

            ShapefileImportProperties properties = mapper.readValue(propertiesJson, ShapefileImportProperties.class);
            properties.validate();
            properties.DecodeToUTF8();

            GeoNetworkMetadataDTO metadata = null;

            if (metadataJson != null) {
                metadata = mapper.readValue(metadataJson, GeoNetworkMetadataDTO.class);
                metadata.validate();
                metadata.DecodeToUTF8();
            }

            InputStream shapefileInputStream = null;
            UserWorkspaceLayerPersist uWL = null;
            UserWorkspaceUtil wsUtil = null;
            if(userWorkspaceLayerJSON != null) {
                String scope = this.getSecurityContextAccessor().getTenant().getName();
                String token = request.getHeader(this.GCUBE_TOKEN_HEADER_KEY);
                logger.info("Token: " + token	+ " scope: " + scope );

                uWL = mapper.readValue(userWorkspaceLayerJSON, UserWorkspaceLayerPersist.class);
                shapefileInputStream = userWorkspaceLayersmanager.downloadStreamOfUserWorkspaceFile(scope, token, uWL.getId());
                wsUtil = new UserWorkspaceUtil(token, scope, uWL);
            } else {
                shapefileInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
            }

            map = importManager.getShapefilesFromZip(properties.getNewLayerName(), shapefileInputStream);

            String fileName = userWorkspaceLayerJSON != null ? uWL.getName() : request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();

            File tf = Files.createTempDir();
            for (Map.Entry<String, InputStream> entry : map.entrySet()) {
                File file = new File(tf.getAbsolutePath() + "/" + entry.getKey());
                file.createNewFile();

                if (!properties.isDbfUserInput()) {

                    if (entry.getKey().endsWith(".cpg")) {
                        try {
                            String encoding = IOUtils.toString(entry.getValue());
                            properties.setDbfEncoding(encoding);
                            cpgNotFound = false;
                        } catch (Exception e) {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Corrupted or empty .cpg file");
                        }
                    }

                }

                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = entry.getValue().read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                fos.close();
            }

            if (cpgNotFound && !properties.isDbfUserInput()) {
                properties.setDbfEncoding(shapefileDefaultEncoding);
                successMessage = "Shapefile is missing .cpg file. Will use default encoding " + shapefileDefaultEncoding;
            }

            successMessage = "Shapefile Import request has been submitted successfully!";

            LayerImport layerImport = importManager.createShapeFileLayerImport(properties, fileName);

            if(userWorkspaceLayerJSON != null)
                importManager.importShapeFileLayer(layerImport, properties, metadata, tf.getAbsolutePath(), tenant, wsUtil);
            else
                importManager.importShapeFileLayer(layerImport, properties, metadata, tf.getAbsolutePath(), tenant);
        } catch (PSQLException p) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Not a valid term name", p);
        } catch (CustomException e) {
            return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Something went wrong with the import request", e);
        } finally {
            if (map != null && !map.isEmpty()) {
                map.forEach((name, is) -> {
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error(null, e);
                    }
                });
            }
        }

        logger.info(successMessage);

        return new CustomResponseEntity<String>(OK, successMessage);
    }

    @RequestMapping(value = "import/geotiff", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> importGeoTIFF(MultipartHttpServletRequest request, HttpServletResponse response) {
        logger.info("Import GeoTIFF request");

        try {
            Tenant tenant = this.getSecurityContextAccessor().getTenant();

            String propertiesJson = request.getParameter("geotiffImportProperties");
            String metadataJson = request.getParameter("geotiffImportMetadata");
            String userWorkspaceLayerPersistJson = request.getParameter("userWorkspaceLayerFileDescriptor");

            GeoTiffImportProperties properties = mapper.readValue(propertiesJson, GeoTiffImportProperties.class);
            properties.validate();
            properties.DecodeToUTF8();

            GeoNetworkMetadataDTO metadata = null;

            if (metadataJson != null) {
                metadata = mapper.readValue(metadataJson, GeoNetworkMetadataDTO.class);
                metadata.validate();
                metadata.DecodeToUTF8();
            }

            UserWorkspaceLayerPersist uWL = null;
            UserWorkspaceUtil wsUtil = null;
            String scope = this.getSecurityContextAccessor().getTenant().getName();
            String token = request.getHeader(this.GCUBE_TOKEN_HEADER_KEY);
            if(userWorkspaceLayerPersistJson != null) {
                uWL = mapper.readValue(userWorkspaceLayerPersistJson, UserWorkspaceLayerPersist.class);
                wsUtil = new UserWorkspaceUtil(token, scope, uWL);
            }

            String fileName;
            InputStream inputStream;

            try {
                fileName = uWL == null ? request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename() : uWL.getName();
                inputStream = uWL == null ? request.getFiles(request.getFileNames().next()).get(0).getInputStream() : userWorkspaceLayersmanager.downloadStreamOfUserWorkspaceFile(scope, token, uWL.getId());
            } catch (Exception e) {
                throw new CustomException(BAD_REQUEST, "No GeoTIFF file found", e);
            }
            LayerImport layerImport = importManager.createGeoTIFFLayerImport(properties, fileName);

//          importManager.importGeoTIFFLayer(layerImport, properties, metadata, geotiff);
            importManager.importGeoTIFFLayer(layerImport, properties, metadata, inputStream, tenant, false, wsUtil);
        } catch (CustomException e) {
            return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Something went wrong with the import. Please try again", e);
        }

        logger.info("GeoTIFF import request has been submitted successfully!");

        return new CustomResponseEntity<String>(OK, "GeoTIFF Import request has been submitted successfully!");
    }


    @RequestMapping(value = "import/netcdf", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> importNetCDF(MultipartHttpServletRequest request, HttpServletResponse response) {
        logger.info("Import NetCDF request");
        File importFile = null;
        LayerImport layerImport = null;
        UserWorkspaceUtil wsUtil = null;

        try {

            Tenant tenant = this.getSecurityContextAccessor().getTenant();
            String propertiesJson = request.getParameter("NetCDFImportProperties");
            String metadataJson = request.getParameter("NetCDFImportMetadata");
            String userWorkspaceLayerPersistJson = request.getParameter("userWorkspaceLayerFileDescriptor");

            NetCDFImportProperties properties = mapper.readValue(propertiesJson, NetCDFImportProperties.class);
            properties.validate();
            properties.DecodeToUTF8();

            GeoNetworkMetadataDTO metadata = null;

            if (metadataJson != null) {
                metadata = mapper.readValue(metadataJson, GeoNetworkMetadataDTO.class);
                metadata.validate();
                metadata.DecodeToUTF8();
            }

            String fileName;
            InputStream inputStream;

            String scope = this.getSecurityContextAccessor().getTenant().getName();
            String token = request.getHeader(this.GCUBE_TOKEN_HEADER_KEY);
            UserWorkspaceLayerPersist uWL = null;
            if(userWorkspaceLayerPersistJson != null) {
                uWL = mapper.readValue(userWorkspaceLayerPersistJson, UserWorkspaceLayerPersist.class);
                wsUtil = new UserWorkspaceUtil(token, scope, uWL);
            }

            try {
                fileName = uWL == null ? request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename() : uWL.getName();
//                request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();
                inputStream = uWL == null
                        ? request.getFiles(request.getFileNames().next()).get(0).getInputStream()
                        : userWorkspaceLayersmanager.downloadStreamOfUserWorkspaceFile(scope, token, uWL.getId());
//                inputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();

//                importFile = new File( request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename());
                importFile = new File( fileName );
//                request.getFiles(request.getFileNames().next()).get(0).transferTo(importFile);

                if(uWL == null){
                    request.getFiles(request.getFileNames().next()).get(0).transferTo(importFile);
                } else {
                    OutputStream outStream = new FileOutputStream(importFile);
                    org.apache.commons.io.IOUtils.copy(inputStream, outStream);
                    outStream.flush();
                    outStream.close();
                }

                inputStream.close();
            } catch (Exception e) {
                throw new CustomException(BAD_REQUEST, "No NetCDF file found", e);
            }

            layerImport = importManager.createNetCDFLayerImport(properties, fileName);

            importManager.importNetCDFLayer(layerImport, properties, metadata, importFile, tenant, wsUtil);
        } catch(Exception e){
            importManager.updateImportStatusToFailureForNetCDFLayer(layerImport);
        }

        String successMessage = "NetCDF import request has been submitted successfully!";
        logger.info(successMessage);

        return new CustomResponseEntity<String>(OK, successMessage);
    }


    @RequestMapping(value = "import/geojson", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public @ResponseBody
    ResponseEntity<?> importGeojson(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("Import Geojson request...");
        Map<String, InputStream> map = null;
        String successMessage;


        boolean cpgNotFound = true;
        File tf = null;
        try {
            Tenant tenant = this.getSecurityContextAccessor().getTenant();
            String metadataJson = request.getParameter("geojsonImportMetadata");
            String propertiesJson = request.getParameter("geojsonImportProperties");
            String userWorkspaceLayerPersistJson = request.getParameter("userWorkspaceLayerFileDescriptor");

            ShapefileImportProperties properties = mapper.readValue(propertiesJson, ShapefileImportProperties.class);
            properties.validate();
            properties.DecodeToUTF8();

            GeoNetworkMetadataDTO metadata = null;

            if (metadataJson != null) {
                metadata = mapper.readValue(metadataJson, GeoNetworkMetadataDTO.class);
                metadata.validate();
                metadata.DecodeToUTF8();
            }
            String fileName;
            InputStream geojsonInputStream;

            String scope = this.getSecurityContextAccessor().getTenant().getName();
            String token = request.getHeader(this.GCUBE_TOKEN_HEADER_KEY);
            UserWorkspaceLayerPersist uWL = null;
            UserWorkspaceUtil wsUtil = null;
            if(userWorkspaceLayerPersistJson != null) {
                uWL = mapper.readValue(userWorkspaceLayerPersistJson, UserWorkspaceLayerPersist.class);
                wsUtil = new UserWorkspaceUtil(token, scope, uWL);
            }


            fileName = uWL == null ? request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename() : uWL.getName();

            geojsonInputStream = uWL == null
                    ? request.getFiles(request.getFileNames().next()).get(0).getInputStream()
                    : userWorkspaceLayersmanager.downloadStreamOfUserWorkspaceFile(scope, token, uWL.getId());

            InputStream shapefileInputStream = geospatialServices.convertGeojsonToShapefile(geojsonInputStream, fileName);
            map = importManager.getShapefilesFromZip(properties.getNewLayerName(), shapefileInputStream);


            tf = Files.createTempDir();
            for (Map.Entry<String, InputStream> entry : map.entrySet()) {
                File file = new File(tf.getAbsolutePath() + "/" + entry.getKey());
                file.createNewFile();

                if (!properties.isDbfUserInput()) {

                    if (entry.getKey().endsWith(".cpg")) {
                        try {
                            String encoding = IOUtils.toString(entry.getValue());
                            properties.setDbfEncoding(encoding);
                            cpgNotFound = false;
                        } catch (Exception e) {
                            throw new CustomException(HttpStatus.BAD_REQUEST, "Corrupted or empty .cpg file");
                        }
                    }

                }

                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = entry.getValue().read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                fos.close();
            }

            if (cpgNotFound && !properties.isDbfUserInput()) {
                properties.setDbfEncoding(shapefileDefaultEncoding);
                successMessage = "Shapefile is missing .cpg file. Will use default encoding " + shapefileDefaultEncoding;
            }

            successMessage = "Shapefile Import request has been submitted successfully!";

            LayerImport layerImport = importManager.createShapeFileLayerImport(properties, fileName);

//            importManager.importShapeFileLayer(layerImport, properties, metadata, tf.getAbsolutePath(), tenant, wsUtil);

            if(userWorkspaceLayerPersistJson != null)
                importManager.importShapeFileLayer(layerImport, properties, metadata, tf.getAbsolutePath(), tenant, wsUtil);
            else
                importManager.importShapeFileLayer(layerImport, properties, metadata, tf.getAbsolutePath(), tenant);
        } catch (PSQLException p) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Not a valid term name", p);
        } catch (CustomException e) {
            return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Something went wrong with the import request", e);
        } finally {
            if (map != null && !map.isEmpty()) {
                map.forEach((name, is) -> {
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error(null, e);
                    }
                });
            }

            if(tf != null) {
                tf.delete();
            }

        }

        logger.info(successMessage);

        return new CustomResponseEntity<String>(OK, successMessage);


    }


    @RequestMapping(value = "import/status", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody
    ResponseEntity<?> importStatus() {
        logger.debug("Requesting Layer Imports");

        List<LayerImport> layerImports = null;

        try {
            Principal principal = securityContextAccessor.getPrincipal();
            layerImports = layerManager.getLayerImportsOfPrincipal(principal);
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Could not retrieve submitted imports", e);
        }

        return new CustomResponseEntity<List<LayerImport>>(OK, layerImports);
    }

    @RequestMapping(value = "import/status/clear", method = RequestMethod.POST, consumes = "application/json" , produces = {"application/json"})
    public @ResponseBody
    ResponseEntity<?> importStatusClear(@RequestBody List<String> layerIds) {
        logger.debug("Clearing Layer Imports");

        try {
            for (String layerId : layerIds) {
                layerManager.deleteLayerImport(layerId);
            }
        } catch (Exception e) {
            return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Could not clear selected layer imports", e);
        }

        return new CustomResponseEntity<>(OK);
    }
}