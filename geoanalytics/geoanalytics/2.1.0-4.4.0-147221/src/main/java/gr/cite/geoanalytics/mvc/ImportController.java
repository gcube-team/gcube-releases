package gr.cite.geoanalytics.mvc;

import gr.cite.gaap.datatransferobjects.AnalyzeResponse;
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
import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

@Controller
@RequestMapping("/")
public class ImportController {

	@Autowired	private ImportManager importManager;
	@Autowired	private LayerManager layerManager;
	@Autowired	private DataRepository repository;
	@Autowired	private SecurityContextAccessor securityContextAccessor;

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(ImportController.class);

	@RequestMapping(method = RequestMethod.POST, value = { "admin/import/analyze" })
	public @ResponseBody AnalyzeResponse analyze(
			@RequestParam(value = "dbfCharset") String dbfCharset, 
			@RequestParam(value = "shpFile") MultipartFile shapeFile,
			@RequestParam(value = "shxFile") MultipartFile shxFile, 
			@RequestParam(value = "dbfFile") MultipartFile dbfFile,
			@RequestParam(value = "prjFile", required = false) MultipartFile prjFile, 
			@RequestParam(value = "sbnFile", required = false) MultipartFile sbnFile,
			@RequestParam(value = "sbxFile", required = false) MultipartFile sbxFile, 
			@RequestParam(value = "spgFile", required = false) MultipartFile spgFile,
			@RequestParam(value = "xmlFile", required = false) MultipartFile xmlFile, 
			HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response)	throws Exception {

		logger.debug("Analyzing shapefile...");
		try {
			Iterator<String> files = request.getFileNames();
			List<RepositoryFile> rfs = new ArrayList<RepositoryFile>();
			while (files.hasNext()) {
				MultipartFile f = request.getFile(files.next());
				RepositoryFile rf = new RepositoryFile();
				rf.setOriginalName(f.getOriginalFilename());
				rf.setDataType(f.getContentType());
				rf.setSize(f.getSize());
				rf.setPermanent(false);
				rf.setInputStream(f.getInputStream());
				rfs.add(rf);
			}

			String folderId = repository.persistToFolder(rfs);
			File shapeFileDir = repository.retrieveFolder(folderId);
			Map<String, String> attrs = importManager.analyzeAttributes(shapeFileDir.getAbsolutePath(), dbfCharset);

			logger.debug("Analyzing shapefile has been suceeded");
			return new AnalyzeResponse(true, attrs, folderId);
		} catch (Exception e) {
			logger.error("Error while analyzing shapefile", e);
			return new AnalyzeResponse(false, null, e.getMessage());
		}
	}

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
			featureTypes = (List<LayerMessenger>) importManager.doCapabilities(reqM, tenant, false);
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
			String tenant = request.getHeader("tenant");

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

				importManager.importWfsLayer(layerImport, layerInfo, tenant, tf.getAbsolutePath(), reqM);
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

		String tenant = request.getHeader("tenant");
		String metadataJson = request.getParameter("tsvImportMetadata");
		String propertiesJson = request.getParameter("tsvImportProperties");

		try {
			InputStream tsvInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			String tsvData = IOUtils.toString(tsvInputStream, "UTF-8");

			TsvImportProperties properties = mapper.readValue(propertiesJson, TsvImportProperties.class);
			properties.validate();

			ImportMetadata metadata = mapper.readValue(metadataJson, ImportMetadata.class);
			metadata.validate();

			LayerImport layerImport = importManager.createTsvLayerImport(properties, metadata.getDescription());

			importManager.importTsvLayer(layerImport, metadata, tsvData, tenant);

			logger.info("Tsv import request has been submitted successfully!");

			return new CustomResponseEntity<String>(HttpStatus.OK, "TSV Import request has been submitted successfully!");
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with the import. Please try again later", e);
		}
	}

	@RequestMapping(value = "importShapefileToGeoanalytics", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public @ResponseBody ResponseEntity<?> importShapefile(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Import Shapefile request...");

		String tenant = request.getHeader("tenant");
		String metadataJson = request.getParameter("shapefileImportMetadata");
		String propertiesJson = request.getParameter("shapefileImportProperties");

		Map<String, InputStream> map = null;

		try {
			ShapefileImportProperties properties = mapper.readValue(propertiesJson, ShapefileImportProperties.class);
			properties.validate();
			System.out.println(properties);

			ImportMetadata metadata = mapper.readValue(metadataJson, ImportMetadata.class);
			metadata.validate();
			
			InputStream shapefileInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			map = importManager.getShapefilesFromZip(properties.getNewLayerName(), shapefileInputStream);

			String fileName = request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();

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

			LayerImport layerImport = importManager.createShapeFileLayerImport(properties, fileName);

			importManager.importShapeFileLayer(layerImport, properties, metadata, tenant, tf.getAbsolutePath());

			logger.info("Shapefile import request has been submitted successfully!");

			return new CustomResponseEntity<String>(HttpStatus.OK, "ShapeFile Import request has been submitted successfully!");
		} catch (PSQLException p) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Not a valid term name", p);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), e.getMessage(), e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong with the import request", e);
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

	@RequestMapping(value = "import/status", method = RequestMethod.GET, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> importStatus(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Requesting Layer Imports");

		List<LayerImport> layerImports = null;

		try {
			Principal principal = securityContextAccessor.getPrincipal();
			layerImports = layerManager.getLayerImportsOfPrincipal(principal);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Could not retrieve submitted imports", e);
		}

		return new CustomResponseEntity<List<LayerImport>>(HttpStatus.OK, layerImports);
	}
}