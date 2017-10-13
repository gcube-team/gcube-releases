package gr.cite.geoanalytics.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.sld.SLDConfiguration;
import org.geotools.xml.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.manager.StyleManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.util.http.CustomResponseEntity;

@Controller
@RequestMapping("/")
public class StyleController {

	@Autowired	private LayerManager layerManager;
	@Autowired	private StyleManager styleManager;
	@Autowired	private SecurityContextAccessor securityContextAccessor;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	private static final Logger logger = LoggerFactory.getLogger(StyleController.class);

	@RequestMapping(value = "styles/listStyles", method = RequestMethod.GET, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> listStyles(HttpServletRequest request) throws Exception {
		logger.info("Retrieving all Styles");

		List<Map<String, String>> nodes = new ArrayList<>();

		try {
			List<Style> allStyles = styleManager.listAllStyles();

			for (Style style : allStyles) {
				Map<String, String> node = new HashMap<>();
				node.put("id", style.getId().toString());
				node.put("name", style.getName());
				node.put("description", style.getDescription());
				nodes.add(node);
			}
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve styles. Please try again later.", e);
		}

		logger.info("Styles have been retrieved successfully!");

		return new CustomResponseEntity<List<Map<String, String>>>(HttpStatus.OK, nodes);
	}

	@RequestMapping(value = "styles/createStyle", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public @ResponseBody ResponseEntity<?> createStyle(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {

		String name = "";
		Style style = null;

		try {

			String propertiesJson = request.getParameter("styleImportProperties");
			MultipartFile filename = request.getFiles(request.getFileNames().next()).get(0);
			InputStream styleInputStreamValidate = filename.getInputStream();
			InputStream styleInputStream = filename.getInputStream();
			StyleMessenger styleInfo = mapper.readValue(propertiesJson, StyleMessenger.class);

			if (validate(styleInputStreamValidate).size() != 0) {
				return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed. Validation of xml failed");
			}

			logger.info("Creating Style with info : " + styleInfo);

			name = styleInfo.getName();
			String description = styleInfo.getDescription();
			String content = getStringFromXmlFile(styleInputStream);
			Principal creator = securityContextAccessor.getPrincipal();

			Assert.notNull(name, "Style name cannot be empty.");
			Assert.hasLength(name, "Style name cannot be empty.");

			styleManager.checkStyleNotExists(name);
			style = new Style().withName(name).withDescription(description).withContent(content).withCreator(creator);
			styleManager.createStyle(style);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), "Creation failed. " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			return new CustomResponseEntity<String>(HttpStatus.BAD_REQUEST, "Creation failed. Style name cannot be empty.", e);
		} catch (SAXException | ParserConfigurationException e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Creation failed. Validation of xml failed");
		} catch (Exception e) {
			styleManager.deleteStyle(style);
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create style with name" + name);
		}

		logger.info("Style has been created successfully");

		return new CustomResponseEntity<String>(HttpStatus.OK, style.getId().toString());
	}

	@RequestMapping(value = "styles/getAllStyles", method = RequestMethod.GET, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> getAllStyles(HttpServletRequest request) throws Exception {
		logger.info("Retrieving all Styles from Geoservers");

		List<String> styles;

		try {
			styles = styleManager.getAllStyles();
			
			Collections.sort(styles, String.CASE_INSENSITIVE_ORDER);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve styles from geoservers. Please try again later.", e);
		}

		logger.info("Styles have been retrieved successfully!");

		return new CustomResponseEntity<List<String>>(HttpStatus.OK, styles);
	}

	@RequestMapping(value = "styles/deleteStyle", method = RequestMethod.POST, consumes = { "application/json" })
	public @ResponseBody ResponseEntity<?> deleteStyle(@RequestBody String styleId, HttpServletRequest request) throws Exception {
		logger.info("Deleting Style with id : " + styleId);

		try {
			Style style = styleManager.findStyleById(styleId);
			styleManager.deleteStyle(style);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete style", e);
		}

		logger.info("Style has been deleted successfully!");

		return new CustomResponseEntity<String>(HttpStatus.OK, "Style has been deleted successfully!");
	}

	@RequestMapping(value = "styles/editStyle", method = RequestMethod.POST, consumes = { "application/json" })
	public @ResponseBody ResponseEntity<?> editStyle(@RequestBody Map<String, String> styleInfo, HttpServletRequest request) throws Exception {
		logger.info("Editing Style with info : " + styleInfo);

		UUID id = UUID.fromString(styleInfo.get("id"));
		String name = styleInfo.get("name");
		String description = styleInfo.get("description");

		try {
			Assert.notNull(name, "Style name cannot be empty.");
			Assert.hasLength(name, "Style name cannot be empty.");

			Style style = styleManager.findStyleById(id);

			if (Objects.equals(style.getName(), name) && Objects.equals(style.getDescription(), description)) {
				throw new CustomException(HttpStatus.BAD_REQUEST, "No changes were submitted.");
			}

			if (!style.getName().equals(name)) {
				styleManager.checkStyleNotExists(name);
			}

			styleManager.editStyle(style, name, description);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), "Update failed. " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			return new CustomResponseEntity<String>(HttpStatus.BAD_REQUEST, "Update failed. Style name cannot be empty.", e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update style information.", e);
		}

		logger.info("Style information has been updated successfully!");

		return new CustomResponseEntity<String>(HttpStatus.OK, "Style information has been updated successfully!");
	}

	private List<Exception> validate(InputStream location) throws IOException, SAXException, ParserConfigurationException {
		logger.debug("Style filename : " + location + " will be parsed");
		SLDConfiguration sld = new SLDConfiguration();
		Parser p = new Parser(sld);
		p.validate(location);
		return p.getValidationErrors();
	}

	private String getStringFromXmlFile(InputStream input) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

		String fileToString = "";
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			fileToString = fileToString.concat(line);
		}

		return fileToString;

	}

}
