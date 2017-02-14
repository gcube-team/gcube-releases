package gr.cite.geoanalytics.manager;

import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.LayerStyleMessenger;
import gr.cite.gaap.datatransferobjects.ShapeImportInfo;
import gr.cite.gaap.datatransferobjects.ImportMetadata;
import gr.cite.gaap.datatransferobjects.WfsRequestMessenger;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeImportManager;
import gr.cite.gaap.servicelayer.ShapeManager.GeographyHierarchy;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermShapeDao;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Layer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoNetworkBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.GSManagerGeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaDataForm;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.commons.util.datarepository.DataRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.io.Files;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Service
public class ImportManager {

	private ViewBuilder builder;
	private GeospatialBackend shapeManager;
	private ShapeImportManager shapeImportManager;
	private TaxonomyManager taxonomyManager;
	private ProjectManager projectManager;
	private ConfigurationManager configurationManager;
	private SecurityContextAccessor securityContextAccessor;
	private GeoServerBridge geoServerBridge;
	private Configuration configuration;
	private DataManager dataManager;
	private ImportManager layerManager;

	private TaxonomyTermDao taxonomyTermDao;
	private TaxonomyTermShapeDao taxonomyTermShapeDao;
	private ShapeDao shapeDao;
	private ShapeDocumentDao shapeDocumentDao;

	private DataRepository repository;

	public static Logger log = LoggerFactory.getLogger(ImportManager.class);

	public ImportManager() {
	}

	public ImportManager(GeospatialBackend shapeManager, ShapeImportManager shapeImportManager,
			TaxonomyManager taxonomyManager, ProjectManager projectManager, ConfigurationManager configurationManager) {
		this.shapeManager = shapeManager;
		this.shapeImportManager = shapeImportManager;
		this.taxonomyManager = taxonomyManager;
		this.projectManager = projectManager;
		this.configurationManager = configurationManager;
	}

	@Inject
	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	@Inject
	public void setImportManager(ImportManager layerManager) {
		this.layerManager = layerManager;
	}

	@Inject
	public void setBuilder(ViewBuilder builder) {
		this.builder = builder;
	}

	@Inject
	public void setShapeManager(GeospatialBackend shapeManager) {
		this.shapeManager = shapeManager;
	}

	@Inject
	public void setShapeImportManager(ShapeImportManager shapeImportManager) {
		this.shapeImportManager = shapeImportManager;
	}

	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}

	@Inject
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	@Inject
	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}

	@Inject
	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
		this.geoServerBridge = geoServerBridge;
	}

	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@Inject
	public void setTaxonomyTermDao(TaxonomyTermDao taxonomyTermDao) {
		this.taxonomyTermDao = taxonomyTermDao;
	}

	@Inject
	public void setTaxonomyTermShapeDao(TaxonomyTermShapeDao taxonomyTermShapeDao) {
		this.taxonomyTermShapeDao = taxonomyTermShapeDao;
	}

	@Inject
	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {
		this.shapeDocumentDao = shapeDocumentDao;
	}

	@Inject
	public void setShapeDao(ShapeDao shapeDao) {
		this.shapeDao = shapeDao;
	}

	@Inject
	public void setDataRepository(DataRepository repository) {
		this.repository = repository;
	}

	public ShapeImportManager getShapeImportManager() {
		return this.shapeImportManager;
	}

	@Transactional(readOnly = true)
	public Map<String, String> analyzeAttributes(String filename, String charset) throws Exception {
		return shapeImportManager.analyzeAttributesOfShapeFile(filename, charset);
	}

	@Transactional(readOnly = true)
	public Set<String> getAttributeValues(String filename, String charset, String attribute) throws Exception {
		return shapeImportManager.getAttributeValuesFromShapeFile(filename, charset, attribute);
	}

	/**
	 * @param templateLayerTaxonomyTermId
	 * @param newLayerNameForTaxonomyTerm
	 * @param taxonomyOfLayerTaxonomyTerm
	 * @param tsv
	 * @return
	 * @throws Exception
	 */
	@Transactional(
			propagation = Propagation.REQUIRES_NEW,
			rollbackFor = { GeoServerBridgeException.class, Exception.class })
	public TaxonomyTerm createNewLayerForTaxonomyTermOfTsv(UUID templateLayerTaxonomyTermId,
			String newLayerNameForTaxonomyTerm, UUID taxonomyOfLayerTaxonomyTerm, String tsv) throws Exception {

		Taxonomy taxonomy = this.taxonomyManager.findTaxonomyById(taxonomyOfLayerTaxonomyTerm.toString(), false);
		TaxonomyTerm templateLayerTaxonomyTerm = this.taxonomyManager
				.findTermById(templateLayerTaxonomyTermId.toString(), false);

		TaxonomyTerm taxonomyTerm = null;
		taxonomyTerm = this.taxonomyManager.findTermByName(newLayerNameForTaxonomyTerm, false);
		if (taxonomyTerm == null) {
			taxonomyTerm = new TaxonomyTerm();
			taxonomyTerm.setCreator(securityContextAccessor.getPrincipal());
			taxonomyTerm.setName(newLayerNameForTaxonomyTerm);
			taxonomyTerm.setTaxonomy(taxonomy);
			this.taxonomyManager.updateTerm(taxonomyTerm, null, null, true);
		}

		this.tsvParsing(templateLayerTaxonomyTerm, taxonomyTerm, tsv);

		securityContextAccessor.updateLayers();
		return taxonomyTerm;
	}

	/**
	 * @param templateLayerTaxonomyTermId
	 * @param taxonomyTerm
	 * @throws Exception
	 * @throws GeoServerBridgeException
	 */
	@Transactional(
			propagation = Propagation.REQUIRES_NEW,
			rollbackFor = { GeoServerBridgeException.class, Exception.class })
	public void createLayerInDataBaseAndGeoserver(UUID templateLayerTaxonomyTermId, TaxonomyTerm taxonomyTerm)
			throws Exception, GeoServerBridgeException {

		TaxonomyTerm templateLayerTaxonomyTerm = this.taxonomyManager
				.findTermById(templateLayerTaxonomyTermId.toString(), false);

		boolean newLayer = false;
		Layer l = null;
		LayerConfig lcfg = configurationManager.getLayerConfig(taxonomyTerm);
		String layerDefaultStyle = "line"; // default style for existing layer

		LayerBounds bounds = null;

		if (lcfg != null) {
			// defensive actions
			l = geoServerBridge.getLayer(taxonomyTerm.getName());
			layerDefaultStyle = configurationManager.getDefaultTermStyle(taxonomyTerm.getId().toString());
			if (l != null) {
			}
			removeLayer(taxonomyTerm);
			bounds = lcfg.getBoundingBox();
		} else {
			newLayer = true;
			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(templateLayerTaxonomyTerm);
			bounds = new LayerBounds(templateLayerConfig.getBoundingBox());
		}

		setUpNewOrUpdatedLayer(taxonomyTerm, null, newLayer, lcfg, layerDefaultStyle,
				new Bounds(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), "EPSG:4326"));
	}

	public void publishLayerMetadataToGeonetwork(String scope, String layerName, ImportMetadata importMetadata) throws Exception {		
		MetaDataForm meta = new MetaDataForm(importMetadata.getUser(), importMetadata.getTitle(), new Date());
		meta.setAbstractField(importMetadata.getAbstractField());
		meta.setPurpose(importMetadata.getPurpose());
		meta.setKeywords(importMetadata.getKeywords());
		meta.setUserLimitation(importMetadata.getLimitation());
		//meta.setGraphicOverviewExternal(importMetadata.getGraphicOverview());
		meta.setDistributorOrganisationName(importMetadata.getDistributorOrganisationName());
		meta.setDistributorIndividualName(importMetadata.getDistributorIndividualName());
		meta.setDistributorSite(importMetadata.getDistributorOnlineResource());
		meta.setProviderIndividualName(importMetadata.getProviderIndividualName());
		meta.setProviderSite(importMetadata.getProviderOnlineResource());
		meta.setProviderOrganisationName(importMetadata.getProviderOrganisationName());
		
		String geoServerUrl = configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl();
		Layer layer = geoServerBridge.getLayer(layerName);
		if (layer!=null) {
			FeatureType featureType = geoServerBridge.getFeatureType(layerName);
			featureType.setSrs("EPSG:4326");
			meta.setGraphicOverviewFromLayer(layer, featureType, geoServerUrl);
		}
		try {
			GeoNetworkBridge geo = new GSManagerGeoNetworkBridge();
			geo.publishGeonetwork(scope, meta); 
			log.info("Metadata have been imported successfully!");
		} catch (GeoNetworkBridgeException e) {
			log.error("Error while publishing layer metadata to geonetwork");
			e.printStackTrace();
			throw (e);
		} catch (Exception e) {
			e.printStackTrace();
			throw (e);
		}
	}

	public void tsvParsing(TaxonomyTerm templateLayerTaxonomyTerm, TaxonomyTerm taxonomyTermLayer, String tsv)
			throws Exception {
		if (taxonomyTermLayer == null) {
			log.error("TaxonomyTerm or GeographycalTaxonomy is null");
			throw new RuntimeException();
		}

		try {
			Taxonomy geoTaxonomy = shapeManager.getTermFromLayerTermAndShape(templateLayerTaxonomyTerm,
					this.shapeManager.getShapesOfLayer(templateLayerTaxonomyTerm).get(0)).getTaxonomy();

			List<String> taxonomyHierarchy = this.shapeManager.getGeographyHierarchy(geoTaxonomy).getMainHierarchy()
					.stream().map(t -> t.getName()).collect(Collectors.toList());
			if (taxonomyHierarchy.isEmpty()) {
				log.error("Taxonomy Hierarchy is not available");
				throw new RuntimeException();
			}

			Principal creator = securityContextAccessor.getPrincipal();

			List<Shape> newShapes = new ArrayList<Shape>();
			Map<String, Shape> sourceShapes = shapeManager.getShapesFromLayerTerm(templateLayerTaxonomyTerm);

			CSVParser csvParser = CSVParser.parse(tsv, CSVFormat.TDF.withRecordSeparator('\n'));
			CSVRecord cSVRecordHeader = null;
			int recordIndex = 0;
			int geoPos = -1;

			for (CSVRecord record : csvParser) {
				if (recordIndex != 0) {
					String[] recordsAsArray;
					String attributeTag = null;
					String geoAttributeValue = null;
					String prefixOfAttribute = null;
					String extraTags = "";
					int valueIndex = 0;

					Shape sourceShape = null, targetShape = null;

					for (String value : record) {
						if (valueIndex == 0) {
							recordsAsArray = value.split(",");
							List<String> recordsAsList = new ArrayList<String>(Arrays.asList(recordsAsArray));
							geoAttributeValue = recordsAsList.remove((geoPos));
							geoAttributeValue = geoAttributeValue.substring(0, 1).toUpperCase()
									+ geoAttributeValue.substring(1).toLowerCase();

							if (recordsAsList.isEmpty()) {
								prefixOfAttribute = String.join("_", taxonomyTermLayer.getName());
							} else {
								prefixOfAttribute = String.join("_", recordsAsList);
							}

							sourceShape = sourceShapes.get(geoAttributeValue);

							if (sourceShape == null) {
								break;
							}

							sourceShape = checkIfThisShapeExistInThisShapeListAndReturnShape(newShapes, sourceShape);
							targetShape = this.newShapeBasedOnOld(creator, sourceShape);

							newShapes.add(targetShape);
						} else {
							attributeTag = prefixOfAttribute + "_" + cSVRecordHeader.get(valueIndex);
							extraTags = addExtraTag(extraTags, attributeTag, value);
						}
						valueIndex++;

						if (valueIndex == record.size()) {
							String extraData = addShapeAttributes(targetShape.getExtraData(), extraTags);
							targetShape.setExtraData(extraData);
						}
					}
				} else {
					cSVRecordHeader = record;
					String firstPartOfheader = "";
					try {
						String[] headers = record.get(0).split("\\\\");
						firstPartOfheader = Arrays.asList(headers).get(0);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Error TSV is does not have proper formation", e);
						throw new RuntimeException();
					}
					String[] firstPartOfHeaderArrayCommaSeparated = firstPartOfheader.split(",");
					List<String> firstPartOfHeaderListCommaSeparated = Arrays
							.asList(firstPartOfHeaderArrayCommaSeparated);
					geoPos = firstPartOfHeaderListCommaSeparated.indexOf("geo");
				}
				recordIndex++;
			}

			for (Shape shape : newShapes) {
				this.shapeDao.create(shape);
			}

			this.shapeManager.createShapeAssociationsWithLayerTerm(taxonomyTermLayer, newShapes);
		} catch (Exception e) {
			log.error("Something went wrong during parsing of the Tsv File", e);
			throw new Exception("Something went wrong during parsing of the Tsv File");
		}
	}

	public String addExtraTag(String extraTags, String attribute, String value) {
		String openingTag = "<" + attribute + " type=\"" + ShapeAttributeDataType.STRING.toString().toUpperCase() + "\">";
		String closingTag = "</" + attribute + ">";
		return extraTags + openingTag + value + closingTag;
	}

	public String addShapeAttributes(String extraData, String extraTags) throws Exception {
		int index = extraData.lastIndexOf("</extraData>");
		String prefixOfExtraData = extraData.substring(0, index);
		String suffixOfExtraData = extraData.substring(index, extraData.length());
		return prefixOfExtraData + extraTags + suffixOfExtraData;
	}

	/**
	 * @param shapes
	 * @param sourceShape
	 * @return
	 */
	private Shape checkIfThisShapeExistInThisShapeListAndReturnShape(List<Shape> shapes, Shape sourceShape) {
		for (Shape shp : shapes) {
			if (shp.getGeography().equals(sourceShape)) {
				sourceShape = shp;
				break;
			}
		}
		return sourceShape;
	}

	private Shape newShapeBasedOnOld(Principal principal, Shape sourceShape) throws Exception {
		Shape targetShape = new Shape();
		targetShape.setCode(sourceShape.getCode());
		targetShape.setCreator(principal);
		targetShape.setGeography(sourceShape.getGeography());
		targetShape.setName(sourceShape.getName());
		targetShape.setExtraData(sourceShape.getExtraData());

		return targetShape;
	}

	public ShapeImportInfo importLayerFromShapeFile(String filename, TaxonomyTerm tt, TaxonomyTerm boundaryTerm,
			int srid, String charset, boolean forceLonLat, boolean newLayer, List<AttributeInfo> attrInfo,
			Principal principal, boolean overwriteMappings, String style, GeographyHierarchy hierarchy)
			throws Exception {

		ShapeImportInfo info = importLayerFromShapeFileToDataBase(filename, tt, boundaryTerm, srid, charset,
				forceLonLat, newLayer, attrInfo, principal, hierarchy);

		try {
			this.setUpNewOrUpdatedLayer(tt, boundaryTerm, newLayer, new LayerConfig(), null, info.getBoundingBox());
			securityContextAccessor.updateLayers(); // update accessible layers (only for current user, which is
													// acceptable because layers are added by a single user with
													// administrative rights)
		} catch (Exception e) {
			log.error("Error while importing data from shape file", e);
			this.removeLayer(tt);
			removeDataBaseView(tt);
			e.printStackTrace();
			return null;
		}
		return info;
	}

	/**
	 * @param tt
	 * @throws Exception
	 */
	@Transactional(rollbackFor = { Exception.class })
	void removeDataBaseView(TaxonomyTerm tt) throws Exception {
		this.builder.forIdentity(tt.getId().toString(), tt.getName()).removerViewIfExists();
	}

	/**
	 * @param filename
	 * @param tt
	 * @param boundaryTerm
	 * @param srid
	 * @param charset
	 * @param forceLonLat
	 * @param newLayer
	 * @param attrInfo
	 * @param principal
	 * @param hierarchy
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
	private ShapeImportInfo importLayerFromShapeFileToDataBase(String filename, TaxonomyTerm tt,
			TaxonomyTerm boundaryTerm, int srid, String charset, boolean forceLonLat, boolean newLayer,
			List<AttributeInfo> attrInfo, Principal principal, GeographyHierarchy hierarchy) throws Exception {

		ShapeImportInfo info = null;
		boolean removed = false;
		Layer l = null;
		FeatureType ft = null;
		LayerConfig lcfg = configurationManager.getLayerConfig(tt);
		Map<String, String> layerStyles = null;
		String layerDefaultStyle = null; // default style for existing layer

		try {
			if (lcfg != null && newLayer == true) {
				// defensive actions
				l = geoServerBridge.getLayer(tt.getName()); // TODO could maybe create new l and ft from lcfg (style?)
				layerStyles = configurationManager.getLayerStyles();
				layerDefaultStyle = configurationManager.getDefaultTermStyle(tt.getId().toString());
				if (l != null) {
					ft = geoServerBridge.getFeatureType(tt.getName());
				}
				removeLayer(tt);
				removed = true;
			}

			LayerConfig layerConfig = new LayerConfig();

			info = newLayerFromShapeFile(filename, tt, boundaryTerm, srid, charset, forceLonLat, newLayer, attrInfo,
					principal, true, layerConfig, layerDefaultStyle, hierarchy); // TODO expose overwriteMappings

			// setUpNewOrUpdatedLayer(tt, boundaryTerm, newLayer, layerConfig, style, info.getBoundingBox());

			securityContextAccessor.updateLayers(); // update accessible layers (only for current user, which is
													// acceptable because layers are added by a single user with
													// administrative rights)
			return info;
		} catch (Exception e) {
			handleLayerUpdateException(tt, newLayer, removed, ft, lcfg, layerStyles, e);
			return null;
		}
	}

	private void handleLayerUpdateException(TaxonomyTerm tt, boolean newLayer, boolean removed, FeatureType ft,
			LayerConfig lcfg, Map<String, String> layerStyles, Exception e) throws Exception {

		Layer l;
		log.error("Error while importing layer", e);
		if (newLayer == false && removed == true) {
			log.info("Attempting to recover from layer removal");
			try {
				if ((l = geoServerBridge.getLayer(tt.getName())) == null) {

					geoServerBridge.addLayer(l, ft, layerStyles, lcfg.getMinScale(), lcfg.getMaxScale());
				}
				throw e;
			} catch (GeoServerBridgeException gbe) {
				log.error("Unable to recover from layer removal", gbe);
				throw new Exception("Unable to recover from layer removal", gbe);
			}
		} else {
			throw e;
		}

	}

	@Transactional(rollbackFor = { Exception.class })
	public ShapeImportInfo newLayerFromShapeFile(String filename, TaxonomyTerm tt, TaxonomyTerm boundaryTerm, int srid,
			String charset, boolean forceLonLat, boolean newLayer, List<AttributeInfo> attrInfo, Principal principal,
			boolean overwriteMappings, LayerConfig layerConfig, String style, GeographyHierarchy hierarchy)
			throws Exception {

		Map<String, Map<String, AttributeInfo>> attrInfoM = createAttributeInfoMap(attrInfo);
		ShapeImportInfo info = shapeImportManager.fromShapefile(filename, tt.getId().toString(), srid, charset,
				forceLonLat, attrInfoM, principal, overwriteMappings);
		shapeManager.generateShapesOfImport(tt, attrInfoM, info.getValueMappingValues(), info.getImportId(),
				tt.getId().toString(), hierarchy, principal);
		if (boundaryTerm != null)
			shapeManager.generateShapeBoundary(tt, boundaryTerm, principal);

		String layerName = newLayer ? tt.getName() : layerConfig.getName();
		info.setLayerName(layerName);

		if (!newLayer) {
			info.setBoundingBox(
					new Bounds(layerConfig.getBoundingBox().getMinX(), layerConfig.getBoundingBox().getMinY(),
							layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), null));
		}
		projectManager.updateAllProjectAttributes();

		return info;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void setUpNewOrUpdatedLayer(TaxonomyTerm tt, TaxonomyTerm boundaryTerm, boolean newLayer,
			LayerConfig layerConfig, String style, Bounds boundingBox) throws Exception {

		String layerName = null;

		layerConfig = (layerConfig == null) ? new LayerConfig() : layerConfig;

		if (style == null) {
			style = configurationManager.getDefaultTermStyle(tt.getId().toString());
		}

		if (newLayer) {
			layerName = tt.getName();
			layerConfig.setName(layerName);
			layerConfig.setTermId(tt.getId().toString());
			if (boundaryTerm != null)
				layerConfig.setBoundaryTermId(boundaryTerm.getId().toString());

			LayerBounds b = new LayerBounds();
			b.setMinX(boundingBox.getMinx());
			b.setMinY(boundingBox.getMiny());
			b.setMaxX(boundingBox.getMaxx());
			b.setMaxY(boundingBox.getMaxy());
			layerConfig.setBoundingBox(b);

			this.dataManager.createDataBaseView(tt.getId().toString(), layerConfig.getName());

			layerName = newLayerFromImportedData(tt.getId().toString(), layerConfig, boundingBox.getCrs(), style);

			layerConfig.setName(layerName);

			configurationManager.addLayerConfig(layerConfig);
		} else {
			// TODO modify zoom levels, add style?
			this.dataManager.createDataBaseView(tt.getId().toString(),
					tt.getName());/* re-create view as new attributes could have been merged into the dataset */

			LayerConfig lcfg = configurationManager.getLayerConfig(tt);

			LayerBounds b = new LayerBounds();
			b.setMinX(boundingBox.getMinx());
			b.setMinY(boundingBox.getMiny());
			b.setMaxX(boundingBox.getMaxx());
			b.setMaxY(boundingBox.getMaxy());
			lcfg.getBoundingBox().mergeWith(b);
			if (boundaryTerm != null)
				lcfg.setBoundaryTermId(boundaryTerm.getId().toString());

			configureLayer(tt, SystemPresentationConfig.DEFAULT_THEME, style, lcfg.getMinScale(), lcfg.getMaxScale());
			configurationManager.updateLayerConfig(lcfg);
		}
	}

	private Map<String, Map<String, AttributeInfo>> createAttributeInfoMap(List<AttributeInfo> attrInfo) {
		Map<String, Map<String, AttributeInfo>> attrInfoM = new HashMap<String, Map<String, AttributeInfo>>();

		for (AttributeInfo ai : attrInfo) {
			if (attrInfoM.get(ai.getName()) == null)
				attrInfoM.put(ai.getName(), new HashMap<String, AttributeInfo>());
			if (ai.getValue() == null)
				attrInfoM.get(ai.getName()).put("", ai);
			else
				attrInfoM.get(ai.getName()).put(ai.getValue(), ai);
		}
		return attrInfoM;
	}

	/* if inputStream is enabled, doTheRequest returns InputStream (--needed for outputFormat=shape-zip purposes) */
	public Object doTheRequest(String url, Map<String, String> parameters, boolean inputStream) throws Exception {
		Client client = Client.create();
		WebResource webResource = null;

		MultivaluedMap nameValuePairs = new MultivaluedMapImpl();
		for (Map.Entry<String, String> params : parameters.entrySet()) {
			if (params.getKey() != null && !params.getKey().isEmpty() && params.getValue() != null
					&& !params.getValue().isEmpty())
				nameValuePairs.add(params.getKey(), params.getValue());
		}
		webResource = client.resource(url).queryParams(nameValuePairs);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() == 201 || response.getStatus() == 200) {
			try {
				if (inputStream)
					return response.getEntity(InputStream.class);
				return response.getEntity(String.class);

			} catch (Exception e) {
				System.err.println("Exception occured!");
			}
		}
		return null;
	}

	public List<String> getCapabilities(WfsRequestMessenger reqM, String scope, boolean doPublish) throws Exception {

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");

		// TODO: se epomeno release prepei na pianoume tous diaforetikous tupous. Fia twra karfwta mono 1.0.0 version

		parameters.put("version", "1.0.0");
		// parameters.put("version", reqM.getVersion());

		parameters.put("request", "GetCapabilities");

		String body = null;
		// try {
		body = (String) doTheRequest(reqM.getUrl(), parameters, false);
		// } catch (Exception e) {
		// return null;
		// }
		if (body == null)
			return null;

		parseGetCapabilitiesForService(body, scope, doPublish);

		return parseGetFeatureTypes(body);
	}

	public void parseGetCapabilitiesForService(String body, String scope, boolean doPublish) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(body));

		Document document = db.parse(is);

		XPath xPath = XPathFactory.newInstance().newXPath();

		String name = (String) xPath.compile("/WFS_Capabilities/Service/Name/text()").evaluate(document,
				XPathConstants.STRING);
		String title = (String) xPath.compile("/WFS_Capabilities/Service/Title/text()").evaluate(document,
				XPathConstants.STRING);
		String abstractText = (String) xPath.compile("/WFS_Capabilities/Service/Abstract/text()").evaluate(document,
				XPathConstants.STRING);
		String keywords = (String) xPath.compile("/WFS_Capabilities/Service/Keywords/text()").evaluate(document,
				XPathConstants.STRING);
		String onlineResource = (String) xPath.compile("/WFS_Capabilities/Service/OnlineResource/text()")
				.evaluate(document, XPathConstants.STRING);
		// System.out.println(name);
		// System.out.println(title);
		// System.out.println(abstractText);
		// System.out.println(keywords);
		// System.out.println(onlineResource);

		ImportMetadata wfsImportMetadata = new ImportMetadata();
		wfsImportMetadata.setAbstractField(abstractText);

		List<String> keyWs = new ArrayList<String>();
		String[] pieces = keywords.split(",");
		for (String k : pieces)
			keyWs.add(k);
		wfsImportMetadata.setKeywords(keyWs);
		wfsImportMetadata.setProviderIndividualName(name);
		wfsImportMetadata.setProviderOnlineResource(onlineResource);
		wfsImportMetadata.setProviderOrganisationName(name);
		wfsImportMetadata.setTitle(title);

		wfsImportMetadata.print();

		if (doPublish)
			layerManager.publishLayerMetadataToGeonetwork(scope, name, wfsImportMetadata);
	}

	public List<String> parseGetFeatureTypes(String body) throws Exception {
		NodeList featureTypes = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(body));

		Document document = db.parse(is);
		document.getDocumentElement().normalize();

		XPath xPath = XPathFactory.newInstance().newXPath();
		featureTypes = (NodeList) xPath.compile("/WFS_Capabilities/FeatureTypeList/FeatureType").evaluate(document,
				XPathConstants.NODESET);

		List<String> featureTypesToReturn = new ArrayList<String>();

		/*
		 * Example of a featuretype node
		 * 
		 * <FeatureType><Name>topp:tasmania_cities</Name> <Title>Tasmania cities</Title> <Abstract>Cities in Tasmania (actually, just the capital)</Abstract> <Keywords>cities,
		 * Tasmania</Keywords> <SRS>EPSG:4326</SRS> <LatLongBoundingBox minx="145.19754" miny="-43.423512" maxx="148.27298000000002" maxy="-40.852802"/> </FeatureType>
		 * 
		 */

		for (int i = 0; i < featureTypes.getLength(); i++) {
			Node node = featureTypes.item(i);

			System.out.println(node.getNodeName()); /* FeatureType */

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;

				// String s = elem.getElementsByTagName("firstname").item(0).getTextContent();

				// System.out.println(elem.getElementsByTagName("Title").item(0).getFirstChild().getNodeValue());
				// System.out.println(elem.getElementsByTagName("Abstract").item(0).getFirstChild().getNodeValue());
				// System.out.println(elem.getElementsByTagName("Keywords").item(0).getFirstChild().getNodeValue());
				// System.out.println(elem.getElementsByTagName("SRS").item(0).getFirstChild().getNodeValue());
				//
				// System.out.println(((Element)elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("minx"));
				// System.out.println(((Element)elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("miny"));
				// System.out.println(((Element)elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("maxx"));
				// System.out.println(((Element)elem.getElementsByTagName("LatLongBoundingBox").item(0)).getAttribute("maxy"));

				featureTypesToReturn.add(elem.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue());
			}
		}

		return featureTypesToReturn;
	}

	public List<String> doCapabilities(WfsRequestMessenger reqM, String scope, boolean doPublish) throws Exception {
		return getCapabilities(reqM, scope, doPublish);
	}

	/* get info with getCapabilities and return available feature types */
	public Map<String, InputStream> doWfsCall(WfsRequestMessenger reqM, String featureType) throws Exception {
		Map<String, InputStream> map = new HashMap<String, InputStream>();

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("service", "wfs");
		// TODO: se epomeno release prepei na pianoume tous diaforetikous tupous. Fia twra karfwta mono 1.0.0 version
		parameters.put("version", "1.0.0");
		// parameters.put("version", reqM.getVersion());

		parameters.put("request", "GetFeature");
		parameters.put("typeName", featureType);

		// TODO: hardcoded! in next release need change
		parameters.put("outputFormat", "SHAPE-ZIP");

		try {
			InputStream inputStream = (InputStream) doTheRequest(reqM.getUrl(), parameters, true);
			if (inputStream == null)
				return null;

			File ff = Files.createTempDir();
			File f = new File(ff.getAbsolutePath() + "/" + featureType + ".zip");

			FileOutputStream fos = new FileOutputStream(f);
			int length;
			byte[] bytes = new byte[1024];
			while ((length = inputStream.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			fos.close();

			ZipFile zipFile = new ZipFile(f);

			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				String name = zipEntry.getName();
				long size = zipEntry.getSize();
				long compressedSize = zipEntry.getCompressedSize();
				System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);
				InputStream is = zipFile.getInputStream(zipEntry);
				map.put(name, is);
			}

		} catch (Exception e) {
			return null;
		}
		return map;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void updateLayerStyle(String name, String origName, String style) throws Exception {

		if (origName == null)
			origName = name;
		if (configurationManager.getLayerStyle(origName) != null) {
			// update style in remote geospatial server

			// remove all references to style
			List<LayerStyleMessenger> refs = configurationManager.getLayersReferencingStyle(origName);
			List<LayerStyleMessenger> uniqueStyles = new ArrayList<LayerStyleMessenger>(); // type LayerConfig is used
																							// here solely as a
																							// container for scale
																							// values
			for (LayerStyleMessenger cfg : refs) {
				boolean addUnique = true;
				for (LayerStyleMessenger us : uniqueStyles) {
					int mins = cfg.getMinScale() != null ? cfg.getMinScale() : 0;
					int umins = us.getMinScale() != null ? us.getMinScale() : 0;
					int maxs = cfg.getMaxScale() != null ? cfg.getMaxScale() : 0;
					int umaxs = us.getMaxScale() != null ? us.getMaxScale() : 0;
					if (mins != umins || maxs != umaxs) {
						addUnique = false;
						break;
					}
				}
				if (addUnique)
					uniqueStyles.add(cfg);
				geoServerBridge.removeLayerStyle(cfg.getLayerName(), origName, cfg.getMinScale(), cfg.getMaxScale());
			}

			// remove all references to style as a default style
			List<LayerStyleMessenger> defaultRefs = configurationManager.getLayersReferencingDefaultStyle(origName);
			for (LayerStyleMessenger cfg : defaultRefs) {
				boolean addUnique = true;
				for (LayerStyleMessenger us : uniqueStyles) {
					int mins = cfg.getMinScale() != null ? cfg.getMinScale() : 0;
					int umins = us.getMinScale() != null ? us.getMinScale() : 0;
					int maxs = cfg.getMaxScale() != null ? cfg.getMaxScale() : 0;
					int umaxs = us.getMaxScale() != null ? us.getMaxScale() : 0;
					if (mins != umins || maxs != umaxs) {
						addUnique = false;
						break;
					}
				}
				if (addUnique)
					uniqueStyles.add(cfg);
				geoServerBridge.setDefaultLayerStyle(cfg.getLayerName(), SystemPresentationConfig.DEFAULT_STYLE,
						configurationManager.getLayerStyle(SystemPresentationConfig.DEFAULT_STYLE));
			}

			// remove actual style instances
			for (LayerStyleMessenger us : uniqueStyles)
				geoServerBridge.removeStyle(origName, us.getMinScale(), us.getMaxScale());

			if (!name.equals(origName)) // old style is kept
				configurationManager.addLayerStyle(name, style);

			// add updated style instances (actual style instances are published automatically if needed)
			for (LayerStyleMessenger ref : defaultRefs) {
				geoServerBridge.setDefaultLayerStyle(ref.getLayerName(), name, style, ref.getMinScale(),
						ref.getMaxScale());
				if (!name.equals(origName))
					configurationManager.addDefaultTermStyle(ref.getTermId(), name);
			}
			for (LayerStyleMessenger ref : refs) {
				geoServerBridge.addLayerStyle(ref.getLayerName(), name, style, ref.getMinScale(), ref.getMaxScale());
				if (!name.equals(origName))
					configurationManager.addTermStyle(ref.getTheme(), ref.getTermId(), name);
			}

			if (name.equals(origName))
				configurationManager.updateLayerStyle(name, style);
		} else
			configurationManager.addLayerStyle(name, style);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void removeLayerStyles(List<String> names) throws Exception {

		List<String> removed = new ArrayList<String>();
		for (String name : names) {
			try {
				geoServerBridge.removeStyle(name);
				removed.add(name);
			} catch (GeoServerBridgeException e) {
				for (String r : removed) {
					try {
						geoServerBridge.addStyle(r, configurationManager.getLayerStyle(r));
					} catch (Exception ee) {
						log.warn("Could not recover from failed removal of style: " + r, ee);
					}
				}
				throw e;
			}
		}
		configurationManager.removeLayerStyles(names);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void removeThemes(List<String> themes) throws Exception {
		for (String t : themes) {
			for (LayerConfig cfg : configurationManager.getLayerConfig()) {
				String ts = configurationManager.getTermStyle(t, cfg.getTermId());
				if (ts != null) {
					if (!ts.equals(configurationManager.getDefaultTermStyle(cfg.getTermId()))) {
						try {
							geoServerBridge.removeLayerStyle(cfg.getName(), ts, cfg.getMinScale(), cfg.getMaxScale());
						} catch (GeoServerBridgeException e) {
							log.warn("Could not remove style for theme " + t + " from layer " + cfg.getName()
									+ ", style=" + ts, e);
						}
					}
				}
			}
		}

		configurationManager.removeThemes(themes);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void configureLayer(TaxonomyTerm tt, String theme, String style, Integer minScale, Integer maxScale)
			throws Exception {

		LayerConfig cfg = configurationManager.getLayerConfig(tt);
		if (cfg == null)
			throw new Exception("Layer configuration not found for term " + tt.getId());
		String ts = theme != null && !theme.equals(SystemPresentationConfig.DEFAULT_THEME)
				? configurationManager.getTermStyle(theme, tt.getId().toString())
				: configurationManager.getDefaultTermStyle(tt.getId().toString());
		/*
		 * optimization not enforced because actual styles need to be updated on the remote side so that their proper update can be ensured if(ts != null && ts.equals(style)) {
		 * if((minScale == null && cfg.getMinScale() == null) || (minScale != null && cfg.getMinScale() != null && minScale.equals(cfg.getMinScale()))) { if((maxScale == null &&
		 * cfg.getMaxScale() == null) || (maxScale != null && cfg.getMaxScale() != null && maxScale.equals(cfg.getMaxScale()))) return; //no change } }
		 */
		String sld = configurationManager.getLayerStyle(style);
		if (sld == null)
			throw new Exception("Style " + style + " not found");

		boolean found = false;
		if (ts != null) {
			List<LayerStyleMessenger> refs = configurationManager.getLayersReferencingStyle(ts);
			refs.addAll(configurationManager.getLayersReferencingDefaultStyle(ts));

			for (LayerStyleMessenger ref : refs) {
				if (!ref.getTermId().equals(tt.getId().toString())
						|| ((ref.getMinScale() == null && minScale != null)
								|| (ref.getMinScale() != null && minScale == null)
								|| ref.getMinScale() != null && minScale != null && !ref.getMinScale().equals(minScale))
						|| ((ref.getMaxScale() == null && maxScale != null)
								|| (ref.getMaxScale() == null && maxScale != null) || ref.getMaxScale() != null
										&& maxScale != null && !ref.getMaxScale().equals(maxScale))) {
					found = true;
					break;
				}
			}
		}

		if (theme != null && !theme.equals(SystemPresentationConfig.DEFAULT_THEME)) {
			if (ts != null && !ts.equals(SystemPresentationConfig.DEFAULT_STYLE) && !found) // no other layers are
																							// referencing this
																							// particular style
																							// instance, so sync with
																							// remote server
				geoServerBridge.removeLayerStyle(cfg.getName(), ts, cfg.getMinScale(), cfg.getMaxScale());

			geoServerBridge.addLayerStyle(cfg.getName(), style, sld, minScale, maxScale);
		} else {
			if (ts != null && !ts.equals(SystemPresentationConfig.DEFAULT_STYLE) && !found) // no other layers are
																							// referencing this
																							// particular style
																							// instance, so sync with
																							// remote server
			{
				geoServerBridge.setDefaultLayerStyle(cfg.getName(), SystemPresentationConfig.DEFAULT_STYLE,
						configurationManager.getLayerStyle(SystemPresentationConfig.DEFAULT_STYLE), minScale, maxScale); // remove
																															// remaining
																															// reference
				geoServerBridge.removeStyle(ts, minScale, maxScale);
			}
			geoServerBridge.setDefaultLayerStyle(cfg.getName(), style, sld, minScale, maxScale);
		}
		cfg.setMinScale(minScale);
		cfg.setMaxScale(maxScale);
		configurationManager.updateLayerConfig(cfg);
		configurationManager.updateTermStyle(theme, tt.getId().toString(), style);
	}

	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
	public void removeLayer(TaxonomyTerm tt) throws Exception {
		LayerConfig lc = configurationManager.getLayerConfig(tt);
		try {
			geoServerBridge.deleteLayer(lc != null ? lc.getName() : tt.getName()); // fallback to name of term if for
																					// some exceptional reason lc does
																					// not exist
		} catch (GeoServerBridgeException e) {
			Layer layer = geoServerBridge.getLayer(lc != null ? lc.getName() : tt.getName());
			if (layer != null) // proceed if layer has actually been deleted
				throw e;
		}

		if (lc != null && lc.getBoundaryTermId() != null) {
			TaxonomyTerm btt = taxonomyTermDao.read(UUID.fromString(lc.getBoundaryTermId()));
			TaxonomyTermShape tts = taxonomyTermShapeDao.findUniqueByTerm(btt);
			taxonomyTermShapeDao.delete(tts);
			shapeDao.delete(tts.getShape());
		}

		List<TaxonomyTermShape> termMappings = shapeManager.findTermMappingsOfLayerShapes(tt);
		for (TaxonomyTermShape tts : termMappings) {
			if (tts.getTerm().getExtraData() != null && tts.getTerm().getExtraData().toLowerCase().contains("auto")) {
				TaxonomyTerm ttst = taxonomyManager.findTermById(tts.getTerm().getId().toString(), false);
				if (ttst == null)
					continue; // tts and ttst already deleted (applies only if terms map to multiple shapes)
				taxonomyTermDao.update(tts.getTerm()); // save transient object so that it can be deleted

				taxonomyTermShapeDao.update(tts); // save transient object so that it can be deleted
				taxonomyManager.deleteTerm(tts.getTerm()); // tts will also be deleted as a side-effect
				// taxonomyTermShapeDao.delete(tts);
			} else {
				List<ShapeDocument> sds = shapeDocumentDao.findByTaxonomyTermShape(tts);
				for (ShapeDocument sd : sds)
					shapeDocumentDao.delete(sd);
				taxonomyTermShapeDao.delete(tts);
			}
		}
		shapeManager.deleteShapesOfTerm(tt);
		configurationManager.removeLayerConfig(tt);
		configurationManager.removeMappingConfigForLayer(tt.getId().toString());
		configurationManager.removeTermStyles(tt.getId().toString());
	}

	@Transactional(rollbackFor = { GeoServerBridgeException.class, Exception.class })
	public String newLayerFromImportedData(String identity, LayerConfig layerConfig, String crs, String style)
			throws Exception {
		GeoServerBridgeConfig config = configuration.getGeoServerBridgeConfig();

		Bounds b = new Bounds(layerConfig.getBoundingBox().getMinY(), layerConfig.getBoundingBox().getMinY(),
				layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), crs);

		FeatureType featureType = new FeatureType();
		featureType.setDatastore(config.getDataStoreConfig().getDataStoreName());
		featureType.setWorkspace(config.getGeoServerBridgeWorkspace());
		featureType.setEnabled(true);
		featureType.setName(layerConfig.getName());
		featureType.setTitle(layerConfig.getName());
		featureType.setSrs("EPSG:4326");
		featureType.setNativeCRS("EPSG:4326");
		featureType.setNativeBoundingBox(b);
		featureType.setLatLonBoundingBox(b);

		Layer l = new Layer();
		l.setWorkspace(config.getGeoServerBridgeWorkspace());
		l.setDatastore(config.getDataStoreConfig().getDataStoreName());
		l.setEnabled(true);
		l.setDefaultStyle(style);
		l.setName(layerConfig.getName());
		l.setType("VECTOR");

		geoServerBridge.addLayer(l, featureType, configurationManager.getLayerStyles(), layerConfig.getMinScale(),
				layerConfig.getMaxScale());
		return l.getName();
	}
}
