package gr.cite.geoanalytics.web;

import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.servicelayer.ShapeManager.GeographyHierarchy;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.manager.AuditingManager;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;
import gr.cite.geoanalytics.dataaccess.entities.auditing.AuditingData;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.logicallayer.LogicalLayerBroker;
import gr.cite.geoanalytics.logicallayer.NodePicker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WmsServlet extends HttpServlet {

	private static final long serialVersionUID = 8898707256706179803L;
	
	private TaxonomyManager taxonomyManager;
	private ConfigurationManager configManager;
	private Configuration configuration;
	private AuditingManager auditingManager;
	private SecurityContextAccessor securityContextAccessor;
	private PrincipalDao principalDao;
	private PrincipalManager principalManager;
	private GeoserverPicker geoserverPicker;
	
	private JAXBContext auditingCtx = null;
	
	private Logger log = LoggerFactory.getLogger(WmsServlet.class);
	
	private static Set<String> configuredTaxonomies;
	
	@Inject
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public WmsServlet(TaxonomyManager taxonomyManager, 
			ConfigurationManager configManager, GeospatialBackend shapeManager, AuditingManager auditingManager,
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
		GeographyHierarchy hier = shapeManager.getDefaultGeographyHierarchy();
		List<List<Taxonomy>> geographyTaxonomies = new ArrayList<List<Taxonomy>>(hier.getAlternativeHierarchies());
		geographyTaxonomies.add(hier.getMainHierarchy());
		for(List<Taxonomy> ts : geographyTaxonomies)
		{
			for(Taxonomy gt : ts)
				configuredTaxonomies.add(gt.getName());
		}
		WmsServlet.configuredTaxonomies = Collections.unmodifiableSet(configuredTaxonomies);
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Inject
	public void setGeoserverPicker(GeoserverPicker geoserverPicker) {
		this.geoserverPicker = geoserverPicker;
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
			if(!layerParts[0].trim().equals(configuration.getGeoServerBridgeConfig().getGeoServerBridgeWorkspace())) throw new Exception("Invalid layer");
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
			if(mcfg.getTermId() != null)
			{
				if(mcfg.getAttributeValue() == null || mcfg.getAttributeValue().equals(""))
				{
					Taxonomy t = taxonomyManager.findTaxonomyById(mcfg.getTermId(), false);
					if(featureInfo && !configuredTaxonomies.contains(t.getName()))
						continue;
					attrs.add(t.getName());
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
	
	private String getCommaSeparatedLayers(List<String> layers) {
		GeoServerBridgeConfig config = configuration.getGeoServerBridgeConfig();
		Iterator<String> it = layers.iterator();
		StringBuilder value = new StringBuilder();
		value.append(config.getGeoServerBridgeWorkspace() + ":" + it.next());
		if(it.hasNext()) value.append(",");
		while(it.hasNext()) {
			value.append(config.getGeoServerBridgeWorkspace() + ":" + it.next());
			if(it.hasNext()) value.append(",");
		}
		return value.toString();
	}
	
	private void relay(HttpServletRequest request, HttpServletResponse response, List<String> layers, Principal principal) throws ServletException, IOException {
		try {
			String requestType = getCaseInsensitiveParameter("request", request);
			List<List<String>> layerAttrs = new ArrayList<List<String>>();
			boolean featureInfo = requestType.equalsIgnoreCase("GetFeatureInfo");
			
			int processedWidth = 0;
			List<String> toRemove = new ArrayList<String>();
			for(String layer : layers) {
				int w = 0;
				if(requestType.equals("GetMap")) {
					w = processZoomLevels(request, layer, principal);
					if(w <= 0)
						toRemove.add(layer);
					else if(processedWidth == 0)
						processedWidth = w;
					else if(w < processedWidth)
						processedWidth = w; //TODO is this correct for multiple layers?
				}
				TaxonomyTerm tt = taxonomyManager.findTermByName(layer.substring(layer.indexOf(":")+1), false);
				LayerConfig lc = configManager.getLayerConfig(tt);
				if(lc == null) {
					toRemove.add(layer);
					log.warn("Could not find layer " + layer);
				}
				else {
					Set<String> la = getLayerAttributes(configManager.getMappingConfigsForLayer(tt.getId().toString()), featureInfo);
//					if(la.isEmpty() && requestType.equalsIgnoreCase("GetFeatureInfo"))
//						toRemove.add(layer);
//					else
						layerAttrs.add(new ArrayList<String>(la));
				}
			}
			for(String l : toRemove)
				layers.remove(l);
			
			StringBuilder urlS = new StringBuilder();
			//TODO the implementation now only supports finding nodes which contain all layers in the WMS request
			//so the first (and only) returned node is fetched by the map.
			//To support merging of responses of multiple nodes, do multiple WMS requests to the nodes in a loop and then
			//process the responses in order to construct a single merged response.
			String pickedGeoserver = geoserverPicker.pickGeoserversByLayerNames(new HashSet<>(layers)).entrySet().iterator().next().getKey();
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
					value = getCommaSeparatedLayers(layers);
				else if(!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options")) //override propertyName so that the geometry attribute cannot be
																									  //requested and format_options so that dpi cannot be increased
					value = request.getParameter(p);
				else
					auditIllegalAccess(AuditingType.IllegalRequestAttempt, principal, request);
				
				if(!p.equalsIgnoreCase("propertyName") && !p.equalsIgnoreCase("format_options"))
					urlS.append(URLEncoder.encode(p, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			}
			
			//DEPWARN GeoServer WMS vendor parameter
			StringBuilder propNames = new StringBuilder();
			for(List<String> ls : layerAttrs) {
				//multiple layers propertyName syntax: propertyName=(nameLayer11,...,nameLayer1N)...(name1LayerN,...,nameNLayerN)
				if(layers.size() > 1) propNames.append("(");
				Iterator<String> it = ls.iterator();
				while(it.hasNext()) {
					propNames.append(it.next().toLowerCase());
					if(it.hasNext()) propNames.append(",");
				}
				if(layers.size() > 1) propNames.append(")");
			} 
//			urlS.append("&" + URLEncoder.encode("propertyName", "UTF-8") + "=" + URLEncoder.encode(propNames.toString(), "UTF-8"));
			
			
			
//			String urlSDirty = "http://localhost:8082/geoserver/geoanalytics/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&FORMAT=image%2Fpng&TRANSPARENT=true";
//			String partialString = "&STYLES";
//			String partialString2 = "&INFO_FORMAT=application%2Fjson&FEATURE_COUNT=50&X=50&Y=50&SRS=EPSG%3A4326&WIDTH=101&HEIGHT=101";
//			log.trace("Requesting: " + urlS);
//			
//			int stringIndex1 = urlS.toString().indexOf("&QUERY_LAYERS");
//			String sub11 = urlS.toString().substring(stringIndex1);
//			int indexOfAmp1 = sub11.indexOf("&",1);
//			String queryLayers = sub11.substring(0, indexOfAmp1);
//			
//			int stringIndex2 = urlS.toString().indexOf("&Layers");
//			String sub12 = urlS.toString().substring(stringIndex2);
//			int indexOfAmp2 = sub12.indexOf("&",1);
//			String theLayers = sub12.substring(0, indexOfAmp2);
//			
//			int stringIndex = urlS.toString().indexOf("&BBOX");
//			String sub1 = urlS.toString().substring(stringIndex);
//			int indexOfAmp = sub1.indexOf("&",1);
//			String bbox = sub1.substring(0, indexOfAmp);
//			String finalURL = urlSDirty + queryLayers + partialString + theLayers + partialString2 + bbox;
//			URL url = new URL(finalURL);
			
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
	
	private List<String> authzPass(List<String> layers, Principal principal, HttpServletRequest request) throws Exception {
		List<String> accessLayers = securityContextAccessor.getLayers();
		if(!configManager.isSystemOnline()) return null;
		
		for(String l : layers) {
			if(!securityContextAccessor.canAccessLayer(l))
				auditIllegalAccess(AuditingType.LayerIllegalAccessAttempt, principal, request);
		}
		
		layers.retainAll(accessLayers);
		
		return layers;
	}
	
	private int processZoomLevels(HttpServletRequest request, String  layer, Principal principal) throws Exception {
//		String[] layerParts = getCaseInsensitiveParameter("layers", request).split(":");
//		if(layerParts.length != 2) throw new Exception("Invalid layer");
//		if(!layerParts[0].trim().equals(Context.getGeoServerBridgeWorkspace())) throw new Exception("Invalid layer");
		
		/*TaxonomyTerm tt = taxonomyManager.findTermByName(layer, false);
		if(tt == null) return -1;
		
		LayerConfig lc = configManager.getLayerConfig(tt);
		String[] coords = getCaseInsensitiveParameter("bbox", request).split(",");
		Bounds bounds = new Bounds(Double.parseDouble(coords[0].trim()), Double.parseDouble(coords[1].trim()), 
				Double.parseDouble(coords[2].trim()), Double.parseDouble(coords[3].trim()), null);*/
		
		//width should be equal to height, but if it is not take the greater
		int width = Integer.parseInt(getCaseInsensitiveParameter("width", request));
		int height = Integer.parseInt(getCaseInsensitiveParameter("height", request));
		
		//TODO remove after scale check is finalized
		//return width <=height ? width : height;
		return width;
		/*
		if(lc.getMaxScale() == null && lc.getMinScale() == null)
			return width <= height ? width : height;
		
		int dim = width >= height ? width : height;
		Double resolution = (bounds.getMaxx() - bounds.getMinx())/dim;
		if((lc.getMaxScale() != null && resolution > lc.getMaxScale())
				|| (lc.getMinScale() != null && resolution < lc.getMinScale()))
		{
			auditIllegalAccess(AuditingType.LayerZoomIllegalAccessAttempt, u, request);
			return (int)(width * (bounds.getMaxx() - bounds.getMaxy())/resolution);
		}
		
		return width <= height ? width : height;*/
	}
	
	@RequestMapping(value = {"/wms", "/admin/wms"}, method=RequestMethod.GET)
	public @ResponseBody void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			org.springframework.security.core.userdetails.User user = 
					(org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Principal principal = principalManager.getActivePrincipalByName(user.getUsername());
			if(principal == null) throw new Exception("User " + user.getUsername() + " not found");
			
			String req = getCaseInsensitiveParameter("request", request);
			if(!req.equalsIgnoreCase("getMap") && !req.equalsIgnoreCase("getFeatureInfo")) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operation not supported");
				auditIllegalAccess(AuditingType.IllegalRequestAttempt, principal, request);
				return;
			}
			
			List<String> layers = getLayers(request);
			layers = authzPass(layers, principal, request);
			if(layers != null) relay(request, response, layers, principal);
			else redirectToUnavailabilityImage(request, response);
		}
		catch(ServletException se) {
			log.error("An error has ocurred while serving request", se);
			throw se;
		}catch(IOException ioe) {
			log.error("An error has ocurred while serving request", ioe);
			throw ioe;
		}catch(Exception e) {
			log.error("An error has ocurred while serving request", e);
		}
	}
}
