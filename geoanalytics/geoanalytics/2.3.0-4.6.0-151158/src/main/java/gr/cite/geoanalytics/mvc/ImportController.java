package gr.cite.geoanalytics.mvc;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import gr.cite.gaap.datatransferobjects.GeoTiffImportProperties;
import gr.cite.gaap.datatransferobjects.LayerMessenger;
import gr.cite.gaap.datatransferobjects.ServiceResponse;
import gr.cite.gaap.datatransferobjects.ShapefileImportProperties;
import gr.cite.gaap.datatransferobjects.TsvImportProperties;
import gr.cite.gaap.datatransferobjects.WfsRequestLayer;
import gr.cite.gaap.datatransferobjects.WfsRequestMessenger;
import gr.cite.gaap.datatransferobjects.request.ImportMetadata;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.util.http.CustomResponseEntity;

@Controller
@RequestMapping("/")
public class ImportController {

	@Autowired	private ImportManager importManager;
	@Autowired	private LayerManager layerManager;
	@Autowired	private SecurityContextAccessor securityContextAccessor;
	
	@Resource(name="shapefileDefaultEncoding")	
	private String shapefileDefaultEncoding;

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(ImportController.class);

	@RequestMapping(value = "import/getCapabilities", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ServiceResponse getCapabilities(@RequestBody WfsRequestMessenger reqM, HttpServletRequest request) throws Exception {
		logger.debug("Getting capabilities...");
		String tenant = request.getHeader("tenant");

		if (reqM != null && (reqM.getUrl().isEmpty() || reqM.getVersion().isEmpty())) {
			logger.error("Invalid data input");
			return new ServiceResponse(false, null, "Invalid data input");
		}

		List<LayerMessenger> featureTypes = null;
		try {
			featureTypes = importManager.doCapabilities(reqM, tenant, false);
		} catch (Exception e) {
			logger.error("Error while getting capabilities", e);
			return new ServiceResponse(false, null, e.getMessage());
		}
		if (featureTypes == null) {
			logger.error("An error occured with wfs data publisher");
			return new ServiceResponse(false, null, "An error occured with wfs data publisher");
		}
		logger.debug("Getting capabilities has been succeeded");
		return new ServiceResponse(true, featureTypes, "feature types returned");
	}

	@RequestMapping(value = "import/storeShapeFilesForFeatureType", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ServiceResponse storeShapeFilesForFeatureType(@RequestBody WfsRequestMessenger reqM, HttpServletRequest request) throws Exception {
		logger.debug("Import WFS request...");

		Map<String, InputStream> map = null;

		try {
			if (reqM.getLayersInfo() == null || reqM.getLayersInfo().isEmpty()) {
				logger.error("No feature types to insert");
				return new ServiceResponse(false, null, "No feature types to insert");
			}

			for (WfsRequestLayer layerInfo : reqM.getLayersInfo()) {
				String featureType = layerInfo.getFeatureTypes();
				map = importManager.doWfsCall(reqM, featureType);
				if (map == null) {
					logger.error("No valid files from geoserver. Corrupted files maybe?");
					return new ServiceResponse(false, null, "No valid files from geoserver. Corrupted files maybe?");
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

				importManager.importWfsLayer(layerImport, layerInfo, tf.getAbsolutePath(), reqM, featureType);
			}
			logger.info("Wfs import request has been submitted successfully!");
			return new ServiceResponse(true, null, "Import request has been submitted successfully!");
		} catch (PSQLException p) {
			logger.error("Not a valid term name", p);
			return new ServiceResponse(false, null, "Not a valid term name");
		} catch (Exception e) {
			logger.error(null, e);
			return new ServiceResponse(false, null, "Something went wrong with the import. Please try again later");
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

	@RequestMapping(value = { "import/tsv" }, method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public @ResponseBody ResponseEntity<?> importTsv(MultipartHttpServletRequest request, HttpServletResponse response) {
		logger.debug("Import TSV request...");

		try {
			InputStream tsvInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			String tsvData = IOUtils.toString(tsvInputStream, "UTF-8");

			String metadataJson = request.getParameter("tsvImportMetadata");
			String propertiesJson = request.getParameter("tsvImportProperties");

			TsvImportProperties properties = mapper.readValue(propertiesJson, TsvImportProperties.class);
			ImportMetadata metadata = mapper.readValue(metadataJson, ImportMetadata.class);

			properties.validate();
			metadata.validate();

			LayerImport layerImport = importManager.createTsvLayerImport(properties, metadata.getDescription());

			importManager.importTsvLayer(layerImport, metadata, tsvData, properties);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Something went wrong with the import. Please try again later", e);
		}

		logger.info("TSV import request has been submitted successfully!");

		return new CustomResponseEntity<String>(OK, "TSV Import request has been submitted successfully!");
	}

	@RequestMapping(value = "import/shapeFile", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public @ResponseBody ResponseEntity<?> importShapefile(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Import Shapefile request...");

		Map<String, InputStream> map = null;
		String successMessage;
				
		try {
			boolean cpgNotFound = true;

			String metadataJson = request.getParameter("shapefileImportMetadata");
			String propertiesJson = request.getParameter("shapefileImportProperties");

			ShapefileImportProperties properties = mapper.readValue(propertiesJson, ShapefileImportProperties.class);
			ImportMetadata metadata = mapper.readValue(metadataJson, ImportMetadata.class);

			properties.validate();
			metadata.validate();

			InputStream shapefileInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			map = importManager.getShapefilesFromZip(properties.getNewLayerName(), shapefileInputStream);

			String fileName = request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();

			File tf = Files.createTempDir();
			for (Map.Entry<String, InputStream> entry : map.entrySet()) {
				File file = new File(tf.getAbsolutePath() + "/" + entry.getKey());

				System.out.println("FILE: " + tf.getAbsolutePath() + "/" + entry.getKey());

				file.createNewFile();
				
				if (entry.getKey().endsWith(".cpg")) {
					try{
						String encoding = IOUtils.toString(entry.getValue());
						System.out.println(encoding);
						properties.setDbfEncoding(encoding);
						cpgNotFound = false;
					} catch(Exception e){
						throw new CustomException(HttpStatus.BAD_REQUEST, "Corrupted or empty .cpg file");
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
			
			if(cpgNotFound){
				properties.setDbfEncoding(shapefileDefaultEncoding);		
				successMessage = "Shapefile is missing .cpg file. Will use default encoding " + shapefileDefaultEncoding;
			} else {
				successMessage = "Shapefile Import request has been submitted successfully!";
			}
			
			LayerImport layerImport = importManager.createShapeFileLayerImport(properties, fileName, metadata.getDescription());

			importManager.importShapeFileLayer(layerImport, properties, metadata, tf.getAbsolutePath());
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
	public @ResponseBody ResponseEntity<?> importGeoTIFF(MultipartHttpServletRequest request, HttpServletResponse response) {
		logger.info("Import GeoTIFF request");

		try {
			String propertiesJson = request.getParameter("geotiffImportProperties");
			String metadataJson = request.getParameter("geotiffImportMetadata");

			GeoTiffImportProperties properties = mapper.readValue(propertiesJson, GeoTiffImportProperties.class);
			ImportMetadata metadata = mapper.readValue(metadataJson, ImportMetadata.class);

			properties.validate();
			metadata.validate();

			String fileName;
			InputStream inputStream;

			try {
				fileName = request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();
				inputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			} catch (Exception e) {
				throw new CustomException(BAD_REQUEST, "No GeoTIFF file found", e);
			}

			byte[] geotiff;

			try {
				geotiff = IOUtils.toByteArray(inputStream);
				importManager.getLatLongBounds(geotiff);
			} catch (Exception e) {
				throw new CustomException(BAD_REQUEST, "Not valid GeoTIFF file", e);
			}

			LayerImport layerImport = importManager.createGeoTIFFLayerImport(properties.getLayerName(), fileName);

			importManager.importGeoTIFFLayer(layerImport, metadata, geotiff);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(INTERNAL_SERVER_ERROR, "Something went wrong with the import. Please try again", e);
		}

		logger.info("GeoTIFF import request has been submitted successfully!");

		return new CustomResponseEntity<String>(OK, "GeoTIFF Import request has been submitted successfully!");
	}

	@RequestMapping(value = "import/status", method = RequestMethod.GET, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> importStatus() {
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

	@RequestMapping(value = "import/status/clear", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<?> importStatusClear(@RequestBody List<String> layerIds) {
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