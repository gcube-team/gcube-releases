package gr.cite.geoanalytics.web;

import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.clustermanager.exceptions.NoAvailableLayer;
import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.manager.AuditingManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.security.GeoanalyticsAuthenticatedUser;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.gos.client.GeoserverManagement;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.auditing.AuditingData;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;

import gr.cite.geoanalytics.manager.LayerManager;

@Controller
public class WmsServlet extends HttpServlet {

	private static final long serialVersionUID = 8898707256706179803L;
	
	
	private TrafficShaper trafficShaper;
	private GeocodeManager taxonomyManager;
	private ConfigurationManager configManager;
	private Configuration configuration;
	private AuditingManager auditingManager;
	private SecurityContextAccessor securityContextAccessor;
	private PrincipalDao principalDao;
	private PrincipalManager principalManager;
	private LayerManager layerManager = null;
	
	private DataMonitor dataMonitor;
//	private GeoserverManagement geoserverManagement;
	
	private JAXBContext auditingCtx = null;
	
	private Logger log = LoggerFactory.getLogger(WmsServlet.class);
	
	private static Set<String> configuredTaxonomies;

	
//	@Inject
//	public void setGeoserverManagement(GeoserverManagement geoserverManagement){
//		this.geoserverManagement = geoserverManagement;
//	}
	
	
	@Inject
	public void setDataMonitor(DataMonitor dataMonitor){
		this.dataMonitor = dataMonitor;
	}
	
	@Inject
	public void setTrafficShaper(TrafficShaper trafficShaper){
		this.trafficShaper = trafficShaper;
	}
	
	@Inject
	public void setLayerManager(LayerManager layerManager) {
		this.layerManager = layerManager;
	}
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public WmsServlet(GeocodeManager taxonomyManager, 
			ConfigurationManager configManager, GeospatialBackendClustered geospatialBackendClustered, AuditingManager auditingManager,
			SecurityContextAccessor securityContextAccessor) throws Exception {
		this.taxonomyManager = taxonomyManager;
		this.configManager = configManager;
		this.auditingManager = auditingManager;
		this.securityContextAccessor = securityContextAccessor;
		
		this.auditingCtx = JAXBContext.newInstance(AuditingData.class);
		
		Set<String> configuredTaxonomies = new HashSet<String>();
		List<TaxonomyConfig> tcs =  configManager.retrieveTaxonomyConfig(true);
		for(TaxonomyConfig tc : tcs)
			configuredTaxonomies.add(tc.getId());
		
		//GeographyHierarchy hier = shapeManager.getDefaultGeographyHierarchy();
		
		//List<List<GeocodeSystem>> geographyTaxonomies = new ArrayList<List<GeocodeSystem>>(hier.getAlternativeHierarchies());
		//geographyTaxonomies.add(hier.getMainHierarchy());
		
//		for(List<GeocodeSystem> ts : geographyTaxonomies)
//		{
//			for(GeocodeSystem gt : ts)
//				configuredTaxonomies.add(gt.getName());
//		}
		WmsServlet.configuredTaxonomies = Collections.unmodifiableSet(configuredTaxonomies);
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
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
		
		for(String l : ls)
		{
			String[] layerParts = l.trim().split(":");
			if(layerParts.length != 2) throw new Exception("Invalid layer");
			GosDefinition availableGos = trafficShaper.getAppropriateGosForLayer(layerParts[1].trim());
			if(availableGos==null) throw new Exception("No available geoservers to fetch the layer "+layerParts[1].trim()+" from");
			if(!layerParts[0].trim().equals(availableGos.getGeoserverWorkspace())) throw new Exception("Invalid layer");
			layers.add(layerParts[1].trim());
		}
		return layers;
	}
	
	private Set<String> getLayerAttributes(List<AttributeMappingConfig> layerAttributeMappings, boolean featureInfo) throws Exception {
		Set<String> attrs = new HashSet<String>();
		for(AttributeMappingConfig mcfg : layerAttributeMappings) {
			if(mcfg.getAttributeValue() != null)
				continue;
			if(!mcfg.isPresentable())
				continue;
			if(mcfg.getLayerTermId() != null)
			{
				if(mcfg.getAttributeValue() == null || mcfg.getAttributeValue().equals(""))
				{
					GeocodeSystem t = taxonomyManager.findGeocodeSystemById(mcfg.getLayerTermId(), false);
					if(featureInfo && t!=null && !configuredTaxonomies.contains(t.getName()))
						continue;
					if(t != null)
						attrs.add(t.getName());
//					attrs.add(t.getId().toString());
				}
				else if(!featureInfo)
					attrs.add(mcfg.getAttributeName());
			}
			else if(!featureInfo)
				attrs.add(mcfg.getAttributeName());
		}
		//attrs.add(Context.getShapeIdColumnName());
		
		
		return attrs;
	}
	
	
	
	//use TrafficShaper instead

	private String getCommaSeparatedLayers(List<String> layers) throws NoAvailableLayer {
//		GeoServerBridgeConfig config = configuration.getGeoServerBridgeConfig();
		Iterator<String> it = layers.iterator();
		StringBuilder value = new StringBuilder();
		while(it.hasNext()) {
			String layerID = it.next();
			value.append(trafficShaper.getAppropriateGosForLayer(layerID).getGeoserverWorkspace() + ":" + layerID);
			if(it.hasNext()) value.append(",");
		}
		return value.toString();
	}
	
	private void relayGetMapRequest(HttpServletRequest request, HttpServletResponse response, List<String> layerNamesInUUIDForm)
			throws ServletException, IOException {
		try {
			String requestType = getCaseInsensitiveParameter("request", request);
			List<List<String>> layerAttrs = new ArrayList<List<String>>();
			boolean featureInfo = requestType.equalsIgnoreCase("GetFeatureInfo");
			
			int processedWidth = 0;
			List<String> toRemove = new ArrayList<String>();
			for(String layerNameAsUUID : layerNamesInUUIDForm) {
				int w = 0;
				if(requestType.equals("GetMap")) {
					w = processZoomLevels(request, layerNameAsUUID);
					if(w <= 0)
						toRemove.add(layerNameAsUUID);
					else if(processedWidth == 0)
						processedWidth = w;
					else if(w < processedWidth)
						processedWidth = w; //TODO is this correct for multiple layers?
				}
//				TaxonomyTerm tt = taxonomyManager.findTermByName(layer.substring(layer.indexOf(":")+1), false);
//				Layer layer = layerManager.findLayerByName(layerNameAsUUID.substring(layerNameAsUUID.indexOf(":")+1));
				Layer layer = layerManager.findLayerById(UUID.fromString(layerNameAsUUID));
				
				LayerConfig lc = configManager.getLayerConfig(layer.getId());
				if(lc == null) {
//					toRemove.add(layerNameAsUUID);
//					log.warn("Could not find layer config for layerID" + layerNameAsUUID);
				}
				else {
					Set<String> la = getLayerAttributes(configManager.getMappingConfigsForLayer(layer.getId().toString()), featureInfo);
//					if(la.isEmpty() && requestType.equalsIgnoreCase("GetFeatureInfo"))
//						toRemove.add(layer);
//					else
						layerAttrs.add(new ArrayList<String>(la));
				}
			}
//			for(String l : toRemove)
//				layerNamesInUUIDForm.remove(l);
			
			StringBuilder urlS = new StringBuilder();
			//TODO the implementation now only supports finding nodes which contain all layers in the WMS request
			//so the first (and only) returned node is fetched by the map.
			//To support merging of responses of multiple nodes, do multiple WMS requests to the nodes in a loop and then
			//process the responses in order to construct a single merged response.
			
			Map<String, String> layerIDToGeoserverURL = 
					layerNamesInUUIDForm.parallelStream()
						.collect(Collectors.toMap(
								layerID->layerID, 
								layerID->{
									try {
										return trafficShaper.getAppropriateGosForLayer(layerID).getGeoserverEndpoint();
									} catch (NoAvailableLayer e) {
										log.warn("No available geoserver to serve the layer: "+layerID);
										return null;
									}
								}
							));
			
			//TODO: THIS MUST ME RECONSTRUCTED. LAYERS WILL NOT RESIDE WITHIN THE SAME GEOSERVER ALWAYS - WAS WRONG AND I JUST TRANSFORM KEEPEING THE WRONG DESIGN - PLEASE FIX
			//TODO: HERE WE JUST PICK ARBITRARILY ONE 
			
			Entry<String,String> firstEntry = layerIDToGeoserverURL.entrySet().iterator().next();
			String requestedLayer = firstEntry.getKey();
			String pickedGeoserver = firstEntry.getValue();
			if(pickedGeoserver==null) throw new IOException("Could not find any available geoserver to serve the WMS call for layer: "+requestedLayer);
			
			urlS.append(pickedGeoserver+"/wms");
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
				if(p.equalsIgnoreCase("width") || p.equalsIgnoreCase("height")) {
					if(requestType.equals("GetFeatureInfo"))
						value = getCaseInsensitiveParameter(p, request);
					else
						value = new Integer(processedWidth).toString();
					
				}
				if(p.equalsIgnoreCase("i") || p.equalsIgnoreCase("j")) {
					continue;
				}
				else if(p.equalsIgnoreCase("layers") || p.equalsIgnoreCase("query_layers"))
					value = getCommaSeparatedLayers(layerNamesInUUIDForm);
				else if(!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options")) //override propertyName so that the geometry attribute cannot be
																									  //requested and format_options so that dpi cannot be increased
					value = request.getParameter(p);
//				else
//					auditIllegalAccess(AuditingType.IllegalRequestAttempt, principal, request);
				
				if(!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options"))
					urlS.append(URLEncoder.encode(p, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			}
			
			//DEPWARN GeoServer WMS vendor parameter
			StringBuilder propNames = new StringBuilder();
			for(List<String> ls : layerAttrs) {
				//multiple layers propertyName syntax: propertyName=(nameLayer11,...,nameLayer1N)...(name1LayerN,...,nameNLayerN)
				if(layerNamesInUUIDForm.size() > 1) propNames.append("(");
				Iterator<String> it = ls.iterator();
				while(it.hasNext()) {
					propNames.append(it.next().toLowerCase());
					if(it.hasNext()) propNames.append(",");
				}
				if(layerNamesInUUIDForm.size() > 1) propNames.append(")");
			} 
//			urlS.append("&" + URLEncoder.encode("propertyName", "UTF-8") + "=" + URLEncoder.encode(propNames.toString(), "UTF-8"));
			
			URL url = new URL(urlS.toString());

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);

			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				connection.setRequestProperty(headerName, request.getHeader(headerName));
				//System.out.println("Request header: " + headerName + ":" + request.getHeader(headerName));
			}

			for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
				//System.out.println("Response header: " + header.getKey() + " : " + header.getValue());
				List<String> vals = new LinkedList<String>(header.getValue());
				if (header.getKey() != null && !header.getKey().equalsIgnoreCase("Transfer-Encoding")) 
					response.setHeader(header.getKey(), vals.get(0));
				vals.remove(0);
				for (String val : new HashSet<String>(vals))
					if (!header.getKey().equalsIgnoreCase("Transfer-Encoding"))
						response.addHeader(header.getKey(), val);
			}

			InputStream is = null;
			OutputStream os = null;
			try {
				is = connection.getInputStream();
				os = response.getOutputStream();
				byte[] buffer = new byte[1024];

				while (true) {
					int bytesRead = is.read(buffer, 0, 1024);
					if (bytesRead < 0)
						break;
					os.write(buffer, 0, bytesRead);
					os.flush();
				}
			} finally {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			}

			//System.out.println("end");
		} catch (Exception e) {
			log.error("An error has occurred while servicing a WMS request. Redirecting", e);
			try
			{
				redirectToUnavailabilityImage(request, response);
			}catch(Exception ee)
			{
				log.error("An error has occurred while trying to redirect request", ee);
			}
		}	
	}
	
	private void relayGetFeatureInfoRequest(HttpServletRequest request, HttpServletResponse response, List<String> layerNamesInUUIDForm)
			throws ServletException, IOException {
		try {
			String requestType = getCaseInsensitiveParameter("request", request);
			List<List<String>> layerAttrs = new ArrayList<List<String>>();
			
			int processedWidth = 0;
			List<String> toRemove = new ArrayList<String>();
			for(String layerNameAsUUID : layerNamesInUUIDForm) {
				LayerConfig lc = configManager.getLayerConfig(UUID.fromString(layerNameAsUUID));
				if(lc == null) {
					toRemove.add(layerNameAsUUID);
					log.warn("Could not find layer " + layerNameAsUUID);
				}
				else {
					Set<String> la = getLayerAttributes(configManager.getMappingConfigsForLayer(layerNameAsUUID), true);
						layerAttrs.add(new ArrayList<String>(la));
				}
			}
			for(String l : toRemove)
				layerNamesInUUIDForm.remove(l);
			
			Map<String, String> layerIDToGeoserverURL = 
					layerNamesInUUIDForm.parallelStream()
						.collect(Collectors.toMap(
								layerID->layerID, 
								layerID->{
									try {
										return trafficShaper.getAppropriateGosForLayer(layerID).getGeoserverEndpoint();
									} catch (NoAvailableLayer e) {
										log.warn("No available geoserver to serve the layer: "+layerID);
										return null;
									}
								}
							));
			
			Iterator<Entry<String, String>> itLayers = layerIDToGeoserverURL.entrySet().iterator();
			
			List<String> theURLs = new ArrayList<String>();
			
			while(itLayers.hasNext()) {
				Map.Entry<String, String> pair = itLayers.next();
				String requestedLayer = pair.getKey();//firstEntry.getKey();
				String pickedGeoserver = pair.getValue();//firstEntry.getValue();

				if(pickedGeoserver==null) throw new IOException("Could not find any available geoserver to serve the WMS call for layer: "+requestedLayer);
				StringBuilder urlS = new StringBuilder();
				urlS.append(pickedGeoserver+"/wms");
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
					if(p.equalsIgnoreCase("width") || p.equalsIgnoreCase("height")) {
						if(requestType.equals("GetFeatureInfo"))
							value = getCaseInsensitiveParameter(p, request);
						else
							value = new Integer(processedWidth).toString();

					}
					if(p.equalsIgnoreCase("i") || p.equalsIgnoreCase("j")) {
						continue;
					}
					else if(p.equalsIgnoreCase("layers") || p.equalsIgnoreCase("query_layers"))
						value = requestedLayer;//getCommaSeparatedLayers(layerNamesInUUIDForm);
					else if(!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options")) //override propertyName so that the geometry attribute cannot be
						//requested and format_options so that dpi cannot be increased
						value = request.getParameter(p);

					if(!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options"))
						urlS.append(URLEncoder.encode(p, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
				}

				//DEPWARN GeoServer WMS vendor parameter
				StringBuilder propNames = new StringBuilder();
				for(List<String> ls : layerAttrs) {
					//multiple layers propertyName syntax: propertyName=(nameLayer11,...,nameLayer1N)...(name1LayerN,...,nameNLayerN)
					if(layerNamesInUUIDForm.size() > 1) propNames.append("(");
					Iterator<String> it = ls.iterator();
					while(it.hasNext()) {
						propNames.append(it.next().toLowerCase());
						if(it.hasNext()) propNames.append(",");
					}
					if(layerNamesInUUIDForm.size() > 1) propNames.append(")");
				} 

				URL url = new URL(urlS.toString());
				theURLs.add(urlS.toString());
			}
			
			List<InputStream> isStream = connectSimultaneouslyAndReportAllAtEnd(theURLs, request, response);
			//connectSequentally(theURLs, request, response);
			
			ObjectMapper mapper = new ObjectMapper();
			List<HashMap<String, Object>> jsonList = new ArrayList<HashMap<String, Object>>();

			List<byte[]> jsonFiles = isStream.stream().map(t -> {
				byte[] byteA = null;
				try {
					byteA = IOUtils.toByteArray(t);
					HashMap<String,Object> jsonMap = new HashMap<String, Object>();
					jsonMap = mapper.readValue(byteA, HashMap.class);
					jsonList.add(jsonMap);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return byteA;
			}).collect(Collectors.toList());
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			Map<String, List<HashMap<String, Object>>> jsonMapObject = new HashMap<String, List<HashMap<String, Object>>>();
			jsonMapObject.put("layerFeatures", jsonList);
			
			String json = ow.writeValueAsString(jsonMapObject);

			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("An error has occurred while servicing a WMS getFeatureInfo request", e);
		}	
	}
	
	private List<InputStream> connectSimultaneouslyAndReportAllAtEnd(Collection<String> URLs, HttpServletRequest request, HttpServletResponse response) throws InterruptedException, ExecutionException {
		Collection<Callable<RequestResult>> tasks = new ArrayList<>();
		for(String url : URLs){
			tasks.add(new Task(url, request, response));
		}
		int numThreads = URLs.size();
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		List<Future<RequestResult>> results = executor.invokeAll(tasks);
		List<InputStream> isList = new ArrayList<InputStream>();
		
		for(Future<RequestResult> result : results) {
			RequestResult pingResult = result.get();
			log.info("Connecting to: " + pingResult.getUrl());
			log.info("Successful: " + pingResult.isSuccess());
			
			isList.add(pingResult.getIs());
		}
		
		executor.shutdown(); //always reclaim resources
		
		return isList;
	}
	
	private List<InputStream> connectSequentally(Collection<String> URLs, HttpServletRequest request, HttpServletResponse response) throws InterruptedException, ExecutionException {
		List<InputStream> isList = new ArrayList<InputStream>();
		for(String url : URLs){
			try {
				RequestResult pingResult = new Task(url, request, response).resultReportStatus(url, request, response);
				isList.add(pingResult.getIs());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}
		
		return isList;
	}
	
	
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
	
	private List<String> authzPass(/*List<String> layers,*/List<String> layersStringUUID, Principal principal, HttpServletRequest request) throws Exception {
//		List<String> accessLayers = new ArrayList<String>();
				//securityContextAccessor.getLayers();
//		layersStringUUID
		List<UUID> accessLayersUUIDs = securityContextAccessor.getLayersIds();
		List<String> accessLayersUUIDsString = accessLayersUUIDs.stream().map(uuid -> uuid.toString()).collect(Collectors.toList());
		List<UUID> layersIdsFromRequest = layersStringUUID.stream().map(stringUUID -> UUID.fromString(stringUUID)).collect(Collectors.toList());
		if(!configManager.isSystemOnline()) return null;
		
//		for(String l : layers) {
//			if(!securityContextAccessor.canAccessLayer(l))
//				auditIllegalAccess(AuditingType.LayerIllegalAccessAttempt, principal, request);
//		}
		
		for(UUID lId : layersIdsFromRequest){
			if(!securityContextAccessor.canAccessLayer(lId)) {
				auditIllegalAccess(AuditingType.LayerIllegalAccessAttempt, principal, request);
			}
		}
		
		layersStringUUID.retainAll(accessLayersUUIDsString);
		List<String> layers = layersIdsFromRequest.stream().map(stringUUID -> {
			try{
//				return layerManager.findLayerById(stringUUID).getName();
				return layerManager.findLayerById(stringUUID).getId().toString();
			} catch(Exception e){
				e.printStackTrace();
				return "@@@xxx111";
			}
		}).collect(Collectors.toList());
		
		layers.removeAll(Collections.singleton("@@@xxx111"));
		
		return layers;
	}
	
	private int processZoomLevels(HttpServletRequest request, String  layer) throws Exception {
		
		//width should be equal to height, but if it is not take the greater
		int width = Integer.parseInt(getCaseInsensitiveParameter("width", request));
		int height = Integer.parseInt(getCaseInsensitiveParameter("height", request));
		
		return width;
	}
	
	@RequestMapping(value = {"/wms", "/admin/wms"}, method=RequestMethod.GET)
	public @ResponseBody void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			GeoanalyticsAuthenticatedUser authUser = 
					(GeoanalyticsAuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			String req = getCaseInsensitiveParameter("request", request);
			
			boolean isGetMap = req.equalsIgnoreCase("getMap");
			boolean isGetFeatureInfo = req.equalsIgnoreCase("getFeatureInfo");
			
			
			if(!isGetMap && !isGetFeatureInfo) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operation not supported");
//				auditIllegalAccess(AuditingType.IllegalRequestAttempt, principal, request);
				return;
			}
			
			List<String> layers = getLayers(request);
//			layers = authzPass(layers, principal, request);
			
			if(isGetMap && (layers==null || layers.size()!=1)) //MUST BE ONLY ONE. IF MORE THAN ONE, WE MIGHT HAVE THEM IN MORE THAN ONE GOS, AND THEREFORE CANNOT SERVE AS ONE IMAGE 
				throw new ServletException("CANNOT SERVE A WMS REQUEST WITH MORE THAN ONE LAYER");
			
			if(isGetMap){
				try{
					relayGetMapRequest(request, response, layers);
				}
				catch(Exception ex){
					log.warn("Could not serve a wms call ->  ",ex);
					redirectToUnavailabilityImage(request, response);
				}			
			} else if(isGetFeatureInfo) {
				try{
					relayGetFeatureInfoRequest(request, response, layers);
				}
				catch(Exception ex){
					log.warn("Could not serve a wms call ->  ",ex);
					redirectToUnavailabilityImage(request, response);
				}
			}
		}
		catch(ServletException se) {
			log.error("An error has ocurred while serving request", se);
			throw se;
		}catch(IOException ioe) {
			log.error("An error has ocurred while serving request", ioe);
			throw ioe;
		}catch(Exception e) {
			log.error("An error has ocurred while serving request", e);
			e.printStackTrace();
			redirectToUnavailabilityImage(request, response);
		}
	}
}