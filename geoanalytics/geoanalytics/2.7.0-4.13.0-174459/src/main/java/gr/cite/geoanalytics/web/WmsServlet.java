package gr.cite.geoanalytics.web;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.fasterxml.jackson.core.type.TypeReference;
import gr.cite.geoanalytics.ows.client.WmsClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.parquet.Strings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import gr.cite.clustermanager.exceptions.NoAvailableLayer;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.layeroperations.FeatureInfo;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;
import gr.cite.geoanalytics.dataaccess.entities.auditing.AuditingData;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerVisualization;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerVisualizationData;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerVisualizationData.AttributeLabelAndOrder;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.manager.AuditingManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.security.GeoanalyticsAuthenticatedUser;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import sun.misc.BASE64Encoder;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class WmsServlet extends HttpServlet {

	private static final long serialVersionUID = 8898707256706179803L;

	private TrafficShaper trafficShaper;
	private GeocodeManager taxonomyManager;
	private ConfigurationManager configManager;
	private AuditingManager auditingManager;
	private SecurityContextAccessor securityContextAccessor;
	private PrincipalDao principalDao;
	private LayerManager layerManager = null;
	private WmsClient wmsClient;

	private JAXBContext auditingCtx = null;

	private Logger log = LoggerFactory.getLogger(WmsServlet.class);

	private static Set<String> configuredTaxonomies;

	private static ObjectMapper mapper = new ObjectMapper();

	@Inject
	public void setTrafficShaper(TrafficShaper trafficShaper) {
		this.trafficShaper = trafficShaper;
	}

	@Inject
	public void setLayerManager(LayerManager layerManager) {
		this.layerManager = layerManager;
	}

	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}

	@Inject
	public WmsServlet(GeocodeManager taxonomyManager, ConfigurationManager configManager, GeospatialBackendClustered geospatialBackendClustered, AuditingManager auditingManager,
			SecurityContextAccessor securityContextAccessor) throws Exception {
		this.taxonomyManager = taxonomyManager;
		this.configManager = configManager;
		this.auditingManager = auditingManager;
		this.securityContextAccessor = securityContextAccessor;
		this.wmsClient = new WmsClient();

		this.auditingCtx = JAXBContext.newInstance(AuditingData.class);

		Set<String> configuredTaxonomies = new HashSet<String>();
		List<TaxonomyConfig> tcs = configManager.retrieveTaxonomyConfig(true);
		for (TaxonomyConfig tc : tcs)
			configuredTaxonomies.add(tc.getId());

		WmsServlet.configuredTaxonomies = Collections.unmodifiableSet(configuredTaxonomies);
	}

	@Override
	@RequestMapping(value = { "/wms", "/admin/wms" }, method = RequestMethod.GET)
	public @ResponseBody void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			System.out.println("Finally here is where the magic happens");
			GeoanalyticsAuthenticatedUser authUser = (GeoanalyticsAuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			String req = getCaseInsensitiveParameter("request", request);

			boolean isGetMap = req.equalsIgnoreCase("getMap");
			boolean isGetFeatureInfo = req.equalsIgnoreCase("getFeatureInfo");
			boolean isGetLegendGraphic = req.equalsIgnoreCase("getLegendGraphic");

			if (!isGetMap && !isGetFeatureInfo && !isGetLegendGraphic) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operation not supported");
				return;
			}
			List<String> layers = null;

			if (!isGetLegendGraphic) {
				layers = getLayers(request);
			}

			if (isGetMap && (layers == null || layers.size() != 1)) //MUST BE ONLY ONE. IF MORE THAN ONE, WE MIGHT HAVE THEM IN MORE THAN ONE GOS, AND THEREFORE CANNOT SERVE AS ONE IMAGE 
				throw new ServletException("CANNOT SERVE A WMS REQUEST WITH MORE THAN ONE LAYER");

			if (isGetMap) {

				Layer layer = this.layerManager.findLayerById(UUID.fromString(layers.get(0)));

				try {
					if (layer.isExternal()) {
						relayExternalGetMapRequest(request, response, layer.getId().toString());
					} else {
						relayGetMapRequest(request, response, layers);
					}
				} catch (Exception ex) {
					log.warn("Could not serve a wms call ->  ", ex);
					redirectToUnavailabilityImage(request, response);
				}
			} else if (isGetFeatureInfo) {
				try {
					relayGetFeatureInfoRequest(request, response, layers);
				} catch (Exception ex) {
					log.warn("Could not serve a wms call ->  ", ex);
					redirectToUnavailabilityImage(request, response);
				}
			}
			else if (isGetLegendGraphic){
				Layer layer = null;
//				System.out.println("REQUEST:"+request.getQueryString());

				Map<String, String> queryParameters = new HashMap<>();
				String queryString = request.getQueryString();

				if (StringUtils.isEmpty(queryString)) {
					log.error("An error has ocurred while serving request, no parameters found");
					redirectToUnavailabilityImage(request, response);
				}
				String params = "?";
				String[] parameters = queryString.split("&");

				for (String parameter : parameters) {
					String[] keyValuePair = parameter.split("=");
					if (keyValuePair.length > 1) {
						queryParameters.put(keyValuePair[0], keyValuePair[1]);
						params += parameter +"&";
					}
				}
//				System.out.println("The Layer is :"+ queryParameters.get("layer"));
					String[] layerId = queryParameters.get("layer").split(":");
				try {
					layer = this.layerManager.findLayerById(UUID.fromString(layerId[1]));
				} catch (Exception e) {
					log.error("Could not find selected layer", e);
					throw e;
				}

				String geoserverUrl = this.layerManager.getGeoserverUrlOfLayer(layer);
				geoserverUrl +="/wms" + params;
				Image image = wmsClient.getImageResource(geoserverUrl);
				String imageString = encodeToString(imageToBufferedImage(image),"png");
				response.getWriter().write(imageString);

            }
		} catch (ServletException se) {
			log.error("An error has ocurred while serving request", se);
			throw se;
		} catch (IOException ioe) {
			log.error("An error has ocurred while serving request", ioe);
			throw ioe;
		} catch (Exception e) {
			log.error("An error has ocurred while serving request", e);
			redirectToUnavailabilityImage(request, response);
		}
	}

	private String getCaseInsensitiveParameter(String parameter, HttpServletRequest request) {
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			String paramName = entry.getKey();
			if (parameter.toLowerCase().equals(paramName.toLowerCase()))
				return entry.getValue()[0];
		}
		return null;
	}

	private List<String> getLayers(HttpServletRequest request) throws Exception {
		List<String> layers = new ArrayList<String>();
		String param = getCaseInsensitiveParameter("layers", request);
		String[] ls = param.split(",");

		for (String l : ls) {
			String[] layerParts = l.trim().split(":");
			if (layerParts.length != 2) {
				throw new Exception("Invalid layer");
			}

			String workspace = layerParts[0].trim();
			String layerId = layerParts[1].trim();

			Layer layer = this.layerManager.findLayerById(UUID.fromString(layerId));

			if (!layer.isExternal()) {
				GosDefinition availableGos = trafficShaper.getAppropriateGosForLayer(layerId);

				if (availableGos == null) {
					throw new Exception("No available geoservers to fetch the layer " + layerId + " from");
				}

				if (!workspace.equals(availableGos.getGeoserverWorkspace())) {
					throw new Exception("Invalid layer");
				}
			}

			layers.add(layerId);
		}
		return layers;
	}

	private Set<String> getLayerAttributes(List<AttributeMappingConfig> layerAttributeMappings, boolean featureInfo) throws Exception {
		Set<String> attrs = new HashSet<String>();
		for (AttributeMappingConfig mcfg : layerAttributeMappings) {
			if (mcfg.getAttributeValue() != null)
				continue;
			if (!mcfg.isPresentable())
				continue;
			if (mcfg.getLayerTermId() != null) {
				if (mcfg.getAttributeValue() == null || mcfg.getAttributeValue().equals("")) {
					GeocodeSystem t = taxonomyManager.findGeocodeSystemById(mcfg.getLayerTermId(), false);
					if (featureInfo && t != null && !configuredTaxonomies.contains(t.getName()))
						continue;
					if (t != null)
						attrs.add(t.getName());
				} else if (!featureInfo)
					attrs.add(mcfg.getAttributeName());
			} else if (!featureInfo)
				attrs.add(mcfg.getAttributeName());
		}

		return attrs;
	}

	//use TrafficShaper instead
	private String getCommaSeparatedLayers(List<String> layers) throws NoAvailableLayer {
		Iterator<String> it = layers.iterator();
		StringBuilder value = new StringBuilder();
		while (it.hasNext()) {
			String layerID = it.next();
			value.append(trafficShaper.getAppropriateGosForLayer(layerID).getGeoserverWorkspace() + ":" + layerID);
			if (it.hasNext())
				value.append(",");
		}
		return value.toString();
	}

	private void relayGetFeatureInfoRequest(HttpServletRequest request, HttpServletResponse response, List<String> layers) throws ServletException, IOException {
		try {
			List<String> toRemove = new ArrayList<String>();
			Set<String> externalLayers = new HashSet<String>();

			for (String layerId : layers) {
				Layer layer = this.layerManager.findLayerById(UUID.fromString(layerId));
				if (layer == null) {
					toRemove.add(layerId);
					log.warn("Could not find layer " + layerId);
				} else {
					if (layer.isExternal()) {
						externalLayers.add(layerId);
					}
				}
			}

			for (String layer : toRemove) {
				layers.remove(layer);
			}

			Map<String, String> layerIDToGeoserverURL;

			try {
				layerIDToGeoserverURL = layers.parallelStream().collect(Collectors.toMap(layerId -> layerId, layerId -> {
					try {
						if (externalLayers.contains(layerId)) {
							return layerId;
						} else {
							return trafficShaper.getAppropriateGosForLayer(layerId).getGeoserverEndpoint();
						}
					} catch (NoAvailableLayer e) {
						log.warn("No available geoserver to serve the layer: " + layerId);
					}
					return null;
				}));
			} catch (Exception e) {
				log.error("Could not find geoservers for any layer ");
				return;
			}

			Iterator<Entry<String, String>> itLayers = layerIDToGeoserverURL.entrySet().iterator();

			Map<String, String> layerURLMap = new HashMap<String, String>();

			Set<String> requiredInternalParameters = Stream.of("service", "version", "request", "layers", "styles", "srs", "bbox", "width", "height", "query_layers", "x", "y", "format", "transparent").collect(Collectors.toSet());					
			Set<String> requiredExternalParameters = Stream.of("service", "version", "request", "styles", "srs", "bbox", "width", "height", "x", "y").collect(Collectors.toSet());
			Set<String> optionalParameters = Stream.of("info_format", "feature_count", "exceptions").collect(Collectors.toSet());
			Set<String> requiredParameters;

			while (itLayers.hasNext()) {
				Map.Entry<String, String> pair = itLayers.next();
				String requestedLayer = pair.getKey();
				String pickedGeoserver = pair.getValue();

				StringBuilder urlS = new StringBuilder();

				if (externalLayers.contains(requestedLayer)) {
					Layer layer = this.layerManager.findLayerById(UUID.fromString(requestedLayer));
					String workspace = layer.getWorkspace();
					String name = layer.getName();
					urlS.append(layer.getExternalGeoserverUrl() + "wms?layers=" + workspace + ":" + name + "&query_layers=" + workspace + ":" + name);
					requiredParameters = new HashSet<>(requiredExternalParameters);
				} else {
					if (pickedGeoserver == null) {
						throw new IOException("Could not find any available geoserver to serve the WMS call for layer: " + requestedLayer);
					}

					urlS.append(pickedGeoserver + "/wms");
					requiredParameters = new HashSet<>(requiredInternalParameters);
				}

				Enumeration<String> ps = request.getParameterNames();

				boolean first = true;

				Set<String> requiredParametersFound = new HashSet<>();

				while (ps.hasMoreElements()) {
					String parameter = ps.nextElement();
					
					if (requiredParameters.contains(parameter.toLowerCase())) {
						if (!requiredParametersFound.contains(parameter.toLowerCase())) {
							requiredParametersFound.add(parameter.toLowerCase());
						} else {
							throw new CustomException(HttpStatus.BAD_REQUEST, "WMS request is invalid. Parameter " + parameter + " must be defined once");
						}
					} else if (!optionalParameters.contains(parameter.toLowerCase())) {
						log.debug("Unknown parameter " + parameter);
						continue;
					}

					String value = request.getParameter(parameter);

					if (value == null || value.trim().isEmpty()) {
						if (parameter.toLowerCase().equals("styles")) {
							value = "";
						} else {
							throw new CustomException(HttpStatus.BAD_REQUEST, "WMS request is invalid. Parameter " + parameter + " cannot be empty");
						}
					}
					
					if(parameter.equalsIgnoreCase("layers") || parameter.equalsIgnoreCase("query_layers")){
						value = requestedLayer;
					}
					
					if (first && !externalLayers.contains(requestedLayer)) {
						urlS.append("?");	
						first = false;
					} else {
						urlS.append("&");
					}

					urlS.append(URLEncoder.encode(parameter, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
				}				
						
			
				if (requiredParameters.size() != requiredParametersFound.size()) {
					requiredParameters.removeAll(requiredParametersFound);
					throw new CustomException(HttpStatus.BAD_REQUEST, "WMS request is invalid. Parameters missing: " + requiredParameters);
				}

				layerURLMap.put(requestedLayer, urlS.toString());
			}

			Map<String, InputStream> layerIDToIsStream = connectSimultaneouslyAndReportAllAtEnd(layerURLMap, request, response);
			//connectSequentally(theURLs, request, response);

			ObjectMapper mapper = new ObjectMapper();
			List<FeatureInfo> jsonList = new ArrayList<FeatureInfo>();

			List<byte[]> jsonFiles = new ArrayList<byte[]>();
			layerIDToIsStream.forEach((l, is) -> {
				byte[] byteA = null;
				try {
					byteA = IOUtils.toByteArray(is);
					FeatureInfo jsonMap = mapper.readValue(byteA, FeatureInfo.class);
					jsonMap.getFeatures().forEach(f -> {
						Map<String, String> filteredProperties = null;
						try {
							filteredProperties = this.filterFeaturesByLayerVisualization(f.getProperties(), UUID.fromString(l));
						} catch (JAXBException e) {
							log.error(null, e);
						}
						if(filteredProperties != null && !filteredProperties.isEmpty()) {
							f.getProperties().clear();
							f.getProperties().putAll(filteredProperties);
						}
					});

					jsonList.add(jsonMap);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				jsonFiles.add(byteA);
			});

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			Map<String, List<FeatureInfo>> jsonMapObject = new HashMap<String, List<FeatureInfo>>();
			jsonMapObject.put("layerFeatures", jsonList);

			String json = ow.writeValueAsString(jsonMapObject);

			response.getWriter().println(json);
		} catch (Exception e) {
			log.error("An error has occurred while servicing a WMS getFeatureInfo request", e);
		}
	}

	private Map<String, InputStream> connectSimultaneouslyAndReportAllAtEnd(Map<String, String> URLs, HttpServletRequest request, HttpServletResponse response)
			throws InterruptedException, ExecutionException {
		Collection<Callable<RequestResult>> tasks = new ArrayList<>();
		for (Map.Entry<String, String> layerURL : URLs.entrySet()) {
			tasks.add(new Task(layerURL.getValue(), request, response, layerURL.getKey()));
		}
		int numThreads = URLs.size();
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		List<Future<RequestResult>> results = executor.invokeAll(tasks);
		Map<String, InputStream> layerIDToISMap = new HashMap<String, InputStream>();

		for (Future<RequestResult> result : results) {
			RequestResult pingResult = result.get();
			log.debug("Connecting to: " + pingResult.getUrl());
			log.debug("Successful: " + pingResult.isSuccess());

			layerIDToISMap.put(pingResult.getLayerID(), pingResult.getIs());
		}

		executor.shutdown(); //always reclaim resources

		return layerIDToISMap;
	}

	//	private List<InputStream> connectSequentally(Collection<String> URLs, HttpServletRequest request, HttpServletResponse response) throws InterruptedException, ExecutionException {
	//		List<InputStream> isList = new ArrayList<InputStream>();
	//		for(String url : URLs){
	//			try {
	//				RequestResult pingResult = new Task(url, request, response).resultReportStatus(url, request, response);
	//				isList.add(pingResult.getIs());
	//			} catch (MalformedURLException e) {
	//				e.printStackTrace();
	//			}
	//
	//		}
	//		
	//		return isList;
	//	}

	private void redirectToUnavailabilityImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("resources/img/Unavailable.png");
	}

	private boolean auditIllegalAccess(AuditingType auditingType, Principal principal, HttpServletRequest request) throws Exception {
		Auditing entry = new Auditing();
		entry.setCreator(principalDao.systemPrincipal());
		entry.setDate(new Date());
		entry.setType(AuditingType.LayerIllegalAccessAttempt);
		entry.setPrincipal(principal);
		AuditingData data = new AuditingData();
		data.setType(AuditingType.LayerIllegalAccessAttempt);
		data.setData(request.getRemoteAddr());
		Marshaller m = auditingCtx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(data, sw);
		entry.setData(sw.toString());
		auditingManager.updateAuditing(entry, true);
		return false;
	}

	private List<String> authzPass(List<String> layersStringUUID, Principal principal, HttpServletRequest request) throws Exception {
		List<UUID> accessLayersUUIDs = securityContextAccessor.getLayersIds();
		List<String> accessLayersUUIDsString = accessLayersUUIDs.stream().map(uuid -> uuid.toString()).collect(Collectors.toList());
		List<UUID> layersIdsFromRequest = layersStringUUID.stream().map(stringUUID -> UUID.fromString(stringUUID)).collect(Collectors.toList());
		if (!configManager.isSystemOnline())
			return null;

		for (UUID lId : layersIdsFromRequest) {
			if (!securityContextAccessor.canAccessLayer(lId)) {
				auditIllegalAccess(AuditingType.LayerIllegalAccessAttempt, principal, request);
			}
		}

		layersStringUUID.retainAll(accessLayersUUIDsString);
		List<String> layers = layersIdsFromRequest.stream().map(stringUUID -> {
			try {
				return layerManager.findLayerById(stringUUID).getId().toString();
			} catch (Exception e) {
				log.error(null,e);
				return "@@@xxx111";
			}
		}).collect(Collectors.toList());

		layers.removeAll(Collections.singleton("@@@xxx111"));

		return layers;
	}

	private int processZoomLevels(HttpServletRequest request, String layer) throws Exception {

		//width should be equal to height, but if it is not take the greater
		int width = Integer.parseInt(getCaseInsensitiveParameter("width", request));
		int height = Integer.parseInt(getCaseInsensitiveParameter("height", request));

		return width;
	}

	public Map<String, String> filterFeaturesByLayerVisualization(Map<String, String> properties, UUID layerID) throws JAXBException {
		UUID tenantID = securityContextAccessor.getTenant().getId();
		LayerVisualization lv = layerManager.getLayerVisualizationByLayerIDAndTenant(layerID, tenantID);
		if (lv == null) {
			return null;
		}
		LayerVisualizationData lvd = layerManager.getLayerVisualizationDataFromXMLField(lv.getAttributeVisualization());

		Map<String, LayerVisualizationData.AttributeLabelAndOrder> lvdProps = new LinkedHashMap<String, LayerVisualizationData.AttributeLabelAndOrder>();

		List<Map.Entry<String, AttributeLabelAndOrder>> list = new LinkedList<Map.Entry<String, LayerVisualizationData.AttributeLabelAndOrder>>();
		list.addAll(lvd.getNameToLabel().entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, AttributeLabelAndOrder>>() {

			@Override
			public int compare(Map.Entry<String, AttributeLabelAndOrder> labelOrder1, Map.Entry<String, AttributeLabelAndOrder> labelOrder2) {
				return (labelOrder1.getValue()).compareTo(labelOrder2.getValue());
			}
		});

		list.forEach(sortedMap -> {
			lvdProps.put(sortedMap.getKey(), sortedMap.getValue());
		});

		Map<String, String> responseNegatives = new LinkedHashMap<String, String>();
		Map<String, String> responseZeros = new LinkedHashMap<String, String>();

		Map<String, String> responsePositives = new LinkedHashMap<String, String>();
		lvdProps.forEach((key, value) -> {
			if (properties.containsKey(key) && (value.getLabel() != null)) {
				if (value.getLabel().equals("")) {
					if (value.getOrder() > 0) {
						responsePositives.put(key, properties.get(key));
					}
					if (value.getOrder() == 0) {
						responseZeros.put(key, properties.get(key));
					}
					if (value.getOrder() < 0) {
						responseNegatives.put(key, properties.get(key));
					}
				} else {
					if (value.getOrder() > 0) {
						responsePositives.put("_#"+value.getLabel(), properties.get(key));
					}
					if (value.getOrder() == 0) {
						responseZeros.put("_#"+value.getLabel(), properties.get(key));
					}
					if (value.getOrder() < 0) {
						responseNegatives.put("_#"+value.getLabel(), properties.get(key));
					}
				}
			} else if (properties.containsKey(key) && value.getLabel() == null) {
				if (value.getOrder() > 0) {
					responsePositives.put(key, properties.get(key));
				}
				if (value.getOrder() == 0) {
					responseZeros.put(key, properties.get(key));
				}
				if (value.getOrder() < 0) {
					responseNegatives.put(key, properties.get(key));
				}
			}
		});

		responsePositives.putAll(responseNegatives);

		return responsePositives;
	}

	private void relayGetMapRequest(HttpServletRequest request, HttpServletResponse response, List<String> layerNamesInUUIDForm) throws ServletException, IOException {
		try {
			String requestType = getCaseInsensitiveParameter("request", request);
			List<List<String>> layerAttrs = new ArrayList<List<String>>();
			boolean featureInfo = requestType.equalsIgnoreCase("GetFeatureInfo");

			int processedWidth = 0;
			List<String> toRemove = new ArrayList<String>();
			for (String layerNameAsUUID : layerNamesInUUIDForm) {
				int w = 0;
				if (requestType.equals("GetMap")) {
					w = processZoomLevels(request, layerNameAsUUID);
					if (w <= 0)
						toRemove.add(layerNameAsUUID);
					else if (processedWidth == 0)
						processedWidth = w;
					else if (w < processedWidth)
						processedWidth = w; //TODO is this correct for multiple layers?
				}
				Layer layer = layerManager.findLayerById(UUID.fromString(layerNameAsUUID));

				LayerConfig lc = configManager.getLayerConfig(layer.getId());
				if (lc == null) {} else {
					Set<String> la = getLayerAttributes(configManager.getMappingConfigsForLayer(layer.getId().toString()), featureInfo);
					layerAttrs.add(new ArrayList<String>(la));
				}
			}

			StringBuilder urlS = new StringBuilder();

			Map<String, String> layerIDToGeoserverURL = layerNamesInUUIDForm.parallelStream().collect(Collectors.toMap(layerID -> layerID, layerID -> {
				try {
					return trafficShaper.getAppropriateGosForLayer(layerID).getGeoserverEndpoint();
				} catch (NoAvailableLayer e) {
					log.warn("No available geoserver to serve the layer: " + layerID);
					return null;
				}
			}));

			Entry<String, String> firstEntry = layerIDToGeoserverURL.entrySet().iterator().next();
			String requestedLayer = firstEntry.getKey();
			String pickedGeoserver = firstEntry.getValue();
			if (pickedGeoserver == null)
				throw new IOException("Could not find any available geoserver to serve the WMS call for layer: " + requestedLayer);

			urlS.append(pickedGeoserver + "/wms");
			Enumeration<String> ps = request.getParameterNames();

			boolean first = true;
			while (ps.hasMoreElements()) {
				String p = ps.nextElement();
				if (first) {
					urlS.append("?");
					first = false;
				} else
					urlS.append("&");
				String value = null;
				if (p.equalsIgnoreCase("width") || p.equalsIgnoreCase("height")) {
					if (requestType.equals("GetFeatureInfo"))
						value = getCaseInsensitiveParameter(p, request);
					else
						value = new Integer(processedWidth).toString();

				}
				if (p.equalsIgnoreCase("i") || p.equalsIgnoreCase("j")) {
					continue;
				} else if (p.equalsIgnoreCase("layers") || p.equalsIgnoreCase("query_layers"))
					value = getCommaSeparatedLayers(layerNamesInUUIDForm);
				else if (!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options")) //override propertyName so that the geometry attribute cannot be
																											//requested and format_options so that dpi cannot be increased
					value = request.getParameter(p);

				if (!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options"))
					urlS.append(URLEncoder.encode(p, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			}

			//DEPWARN GeoServer WMS vendor parameter
			StringBuilder propNames = new StringBuilder();
			for (List<String> ls : layerAttrs) {
				if (layerNamesInUUIDForm.size() > 1)
					propNames.append("(");
				Iterator<String> it = ls.iterator();
				while (it.hasNext()) {
					propNames.append(it.next().toLowerCase());
					if (it.hasNext())
						propNames.append(",");
				}
				if (layerNamesInUUIDForm.size() > 1)
					propNames.append(")");
			}

			URL url = new URL(urlS.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);

			addConnectionHeaders(connection, request, response);
			pipeWmsRequest(connection, response);
		} catch (Exception e) {
			log.error("An error has occurred while servicing a WMS request. Redirecting", e);
			try {
				redirectToUnavailabilityImage(request, response);
			} catch (Exception ee) {
				log.error("An error has occurred while trying to redirect request", ee);
			}
		}
	}

	public void relayExternalGetMapRequest(HttpServletRequest request, HttpServletResponse response, String layerId) throws Exception {
		try {
			Layer layer = this.layerManager.findLayerById(UUID.fromString(layerId));

			Set<String> requiredParameters = Stream.of("width", "height", "request", "service", "version", "bbox", "srs", "styles", "format").collect(Collectors.toSet());
			Set<String> optionalParameters = Stream.of("transparent", "bgcolor", "exceptions", "time", "sld", "sld_body").collect(Collectors.toSet());
			Set<String> requiredParametersFound = new HashSet<>();
			
			String workspace = layer.getWorkspace();
			String name = layer.getName();
			StringBuilder urlS = new StringBuilder(layer.getExternalGeoserverUrl() + "wms?layers=" + workspace + ":" + name);
			
			Enumeration<String> ps = request.getParameterNames();

			while (ps.hasMoreElements()) {
				String parameter = ps.nextElement();

				if (requiredParameters.contains(parameter.toLowerCase())) {
					if (!requiredParametersFound.contains(parameter)) {
						requiredParametersFound.add(parameter);
					} else {
						throw new CustomException(HttpStatus.BAD_REQUEST, "WMS request is invalid. Parameter " + parameter + " must be defined once");
					}
				} else if (!optionalParameters.contains(parameter.toLowerCase())) {
					log.debug("Unknown parameter " + parameter);
					continue;
				}

				String value = request.getParameter(parameter);

				if (value == null || value.trim().isEmpty()) {
					if (parameter.toLowerCase().equals("styles")) {
						value = "";
					} else {
						throw new CustomException(HttpStatus.BAD_REQUEST, "WMS request is invalid. Parameter " + parameter + " cannot be empty");
					}
				}

				urlS.append("&");
				urlS.append(URLEncoder.encode(parameter, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			}

			if (requiredParameters.size() != requiredParametersFound.size()) {
				requiredParameters.removeAll(requiredParametersFound);
				throw new CustomException(HttpStatus.BAD_REQUEST, "WMS request is invalid. Parameters missing: " + requiredParameters);
			}
			urlS.append("&");
			urlS.append(URLEncoder.encode("layout", "UTF-8") + ":" + URLEncoder.encode("style-editor-legend", "UTF-8"));
			System.out.println("IM GETTING map and is:"+urlS.toString());
			URL url = new URL(urlS.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);

			addConnectionHeaders(connection, request, response);
			pipeWmsRequest(connection, response);
		} catch (Exception e) {
			log.error("An error has occurred while servicing a WMS request. Redirecting", e);
			try {
				redirectToUnavailabilityImage(request, response);
			} catch (Exception ee) {
				log.error("An error has occurred while trying to redirect request", ee);
			}
		}
	}

	private void addConnectionHeaders(HttpURLConnection connection, HttpServletRequest request, HttpServletResponse response) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			connection.setRequestProperty(headerName, request.getHeader(headerName));
		}

		for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
			List<String> vals = new LinkedList<String>(header.getValue());
			if (header.getKey() != null && !header.getKey().equalsIgnoreCase("Transfer-Encoding")) {
				response.setHeader(header.getKey(), vals.get(0));
			}
			vals.remove(0);
			for (String val : new HashSet<String>(vals)) {
				if (!header.getKey().equalsIgnoreCase("Transfer-Encoding")) {
					response.addHeader(header.getKey(), val);
				}
			}
		}
	}

	private void pipeWmsRequest(HttpURLConnection connection, HttpServletResponse response) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = connection.getInputStream();
			os = response.getOutputStream();
			byte[] buffer = new byte[1024];

			while (true) {
				int bytesRead = is.read(buffer, 0, 1024);
				if (bytesRead < 0) {
					break;
				}
				os.write(buffer, 0, bytesRead);
				os.flush();
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}

	public static String encodeToString(BufferedImage image, String type)
	{
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();

			BASE64Encoder encoder = new BASE64Encoder();
			imageString = encoder.encode(imageBytes);

			bos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return imageString;
	}

	public static BufferedImage imageToBufferedImage(Image im) {
		BufferedImage bi = new BufferedImage
				(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}
}