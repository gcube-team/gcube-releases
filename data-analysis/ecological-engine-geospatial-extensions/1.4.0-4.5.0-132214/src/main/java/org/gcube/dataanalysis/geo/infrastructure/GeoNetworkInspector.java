package org.gcube.dataanalysis.geo.infrastructure;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.configuration.ConfigurationManager;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.Resolution;

public class GeoNetworkInspector {
	private String geonetworkUrl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/";
	// private String geonetworkUrl = "http://geoserver-last.d4science-ii.research-infrastructures.eu/geonetwork/";
	// private String geonetworkUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork/";
	private String geonetworkUser = "admin";
	private String geonetworkPwd = "admin";
	private String scope = "/gcube/devsec";

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public static double getResolution(Metadata meta) {
		double res = 0;
		try {
			DefaultDataIdentification ddi = (DefaultDataIdentification) meta.getIdentificationInfo().iterator().next();
			// take the lowest resolution
			for (Resolution r : ddi.getSpatialResolutions()) {
				Double rr = r.getDistance();
				if (rr == null)
					rr = r.getEquivalentScale().doubleValue();

				if (rr != null && rr > res) {
					res = rr;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Could not get Data Identification");
		}

		AnalysisLogger.getLogger().debug("Calculated Resolution is:" + res);

		return res;
	}

	public String getGeoserverLink(Metadata meta) {
		String link = null;
		String geoserverString = "/geoserver/";
		String geoserverEndString = "/geoserver?";
		String wmslink = getWMSLink(meta);
		if (wmslink != null) {
			int idx = wmslink.indexOf(geoserverString);
			if (idx < 0)
				idx = wmslink.indexOf(geoserverEndString);
			if (idx > 0) {
				link = wmslink.substring(0, idx + geoserverString.length() - 1);
				return link;
			}
		}
		String wfslink = getWFSLink(meta);
		if (wfslink != null) {
			int idx = wfslink.indexOf(geoserverString);
			if (idx < 0)
				idx = wfslink.indexOf(geoserverEndString);

			if (idx > 0) {
				link = wfslink.substring(0, idx + geoserverString.length() - 1);
				return link;
			}
		}
		String wcslink = getWCSLink(meta);
		if (wcslink != null) {
			int idx = wcslink.indexOf(geoserverString);
			if (idx < 0)
				idx = wcslink.indexOf(geoserverEndString);

			if (idx > 0) {
				link = wcslink.substring(0, idx + geoserverString.length() - 1);
				return link;
			}
		}

		if (link == null)
			AnalysisLogger.getLogger().debug("NO GEOSERVER LINK WAS FOUND ACCORDING TO THE CRITERION");

		return link;
	}

	private String searchInUrl(Metadata meta, String criterion) {
		String link = null;
		for (DigitalTransferOptions option : meta.getDistributionInfo().getTransferOptions()) {
			for (OnlineResource resource : option.getOnLines()) {
				String tlink = "";

				try {
					tlink = resource.getLinkage().toString();

				} catch (Exception e) {
				}
				if (tlink.toLowerCase().contains(criterion.toLowerCase())) {
					link = tlink;
					break;
				}
				// read the http header and check there for the criterion
				
				else if (tlink.toLowerCase().startsWith("http:")) {
					try {
						URL obj = new URL(tlink);
						URLConnection conn = obj.openConnection();

						// get all headers
						Map<String, List<String>> map = conn.getHeaderFields();
						for (Map.Entry<String, List<String>> entry : map.entrySet()) {
							String value = entry.getValue().toString();
							if (value.toLowerCase().contains("filename=")){
								AnalysisLogger.getLogger().debug("Searching in http header: found "+value);
								if (value.toLowerCase().contains(criterion)){
									link = tlink;
									break;
								}
							}
						}
					} catch (Exception e) {
						AnalysisLogger.getLogger().debug("Malformed url for link: "+tlink);
					}
				}
				
			}

		}
		if (link == null)
			AnalysisLogger.getLogger().debug("NO ONLINE LINK WAS FOUND ACCORDING TO THE CRITERION :" + criterion);
		return link;
	}

	private String searchLayerNameInMeta(Metadata meta) {
		String innerlayername = null;
		for (DigitalTransferOptions option : meta.getDistributionInfo().getTransferOptions()) {
			for (OnlineResource resource : option.getOnLines()) {
				String layername = resource.getName();
				String link = "";
				try {
					link = resource.getLinkage().toString().toLowerCase();
				} catch (Exception e) {
				}
				if ((layername != null) && link.contains("wms")) {
					innerlayername = layername;
					break;
				}
			}

		}
		if (innerlayername == null)
			System.out.println("NO LAYER NAME WAS FOUND IN TRANSFER OPTIONS");
		return innerlayername;
	}

	public String getHttpLink(Metadata meta) {
		return searchInUrl(meta, "http");
	}

	public String getWFSLink(Metadata meta) {
		return searchInUrl(meta, "service=wfs");
	}

	// retrieves the wms link
	public String getWMSLink(Metadata meta) {
		return searchInUrl(meta, "service=wms");
	}

	public String getWCSLink(Metadata meta) {
		return searchInUrl(meta, "service=wcs");
	}

	public String getGeoTiffLink(Metadata meta) {
		String url = searchInUrl(meta, ".tiff");
		if (url == null)
			url = searchInUrl(meta, ".geotiff");
		if (url == null)
			url = searchInUrl(meta, ".tif");
		if (url == null)
			url = searchInUrl(meta, ".geotif");
		return url;
	}

	public String getASCLink(Metadata meta) {
		return searchInUrl(meta, ".asc");
	}
	
	public String getOpenDapLink(Metadata meta) {
		String url = searchInUrl(meta, "/dodsC");
//		if (url == null) the netcdf can be accessed through opendap only
//			url = searchInUrl(meta, ".nc");
		return url;
	}

	public String getThreddsLink(Metadata meta) {
		return searchInUrl(meta, "catalog.xml");
	}

	public String getLayerName(Metadata meta) {
		AnalysisLogger.getLogger().debug("Retrieving Layer Name from WMS");
		String wmslink = getWMSLink(meta);
		String layer = null;
		
		if (wmslink != null) {
			String [] finders = {"layers=", "LAYERS=","Layers="};
			AnalysisLogger.getLogger().debug("WMS layer found!");
			int idxfinder = -1;
			for (String finder:finders){
				idxfinder = wmslink.indexOf(finder);
				if (idxfinder>-1)
					break;
			}		
			if (idxfinder > 0) {
				AnalysisLogger.getLogger().debug("Searching for Layer Name inside the WMS Link");
				wmslink = wmslink.substring(idxfinder);
				int andIdx = wmslink.indexOf("&");
				if (andIdx < 0)
					andIdx = wmslink.length();

				layer = wmslink.substring(finders[0].length(), andIdx).trim();
			}
			// if the layer is not inside the wmslink
			else {
				AnalysisLogger.getLogger().debug("Searching for Layer Name inside the file");
				layer = searchLayerNameInMeta(meta);
			}
		}
		else
		{
			AnalysisLogger.getLogger().debug("Trying with WFS link");
			String wfslink = getWFSLink(meta);
			String [] finders = {"typename=", "TYPENAME=","typeName=","TypeName"};
			if (wfslink != null) {
				AnalysisLogger.getLogger().debug("WFS layer found!");
				int idxfinder = -1;
				for (String finder:finders){
					idxfinder = wfslink.indexOf(finder);
					if (idxfinder>-1)
						break;
				}		
				if (idxfinder > 0) {
					AnalysisLogger.getLogger().debug("Searching for Layer Name inside the WMS Link");
					wfslink = wfslink.substring(idxfinder);
					int andIdx = wfslink.indexOf("&");
					if (andIdx < 0)
						andIdx = wfslink.length();

					layer = wfslink.substring(finders[0].length(), andIdx).trim();
				}
				// if the layer is not inside the wfslink
				else {
					AnalysisLogger.getLogger().debug("Searching for Layer Name inside the file");
					layer = searchLayerNameInMeta(meta);
				}
			}
		}
		AnalysisLogger.getLogger().debug("Returning layer "+layer);
		return layer;
	}

	public boolean isNetCDFFile(Metadata meta) {
		return (getOpenDapLink(meta) != null);
	}

	public boolean isAscFile(Metadata meta) {
		String httplink = getASCLink(meta);
		return (httplink!=null);
	}

	public boolean isWFS(Metadata meta) {
		String httplink = getWFSLink(meta);
		return (httplink != null);
	}

	public boolean isWCS(Metadata meta) {
		String httplink = getWCSLink(meta);
		return (httplink != null);
	}

	public boolean isGeoTiff(Metadata meta) {
		String httplink = getGeoTiffLink(meta);
		return (httplink != null);
	}

	Configuration gnconfiguration;

	public GeoNetworkReader initGeoNetworkReader() throws Exception {
		AnalysisLogger.getLogger().debug("Features Manager: configuring GeoNetwork");
/*
		if (scope != null)
			ScopeProvider.instance.set(scope);
*/
		AnalysisLogger.getLogger().debug("Initializing GeoNetwork");

		GeoNetworkReader gn = GeoNetwork.get();

		AnalysisLogger.getLogger().debug("Using the following Geonetwork: " + gn.getConfiguration().getGeoNetworkEndpoint() + " in scope " + scope);

		return gn;
	}

	public String getGeonetworkURLFromScope() throws Exception {
		GeoNetworkReader gn = initGeoNetworkReader();
		return gn.getConfiguration().getGeoNetworkEndpoint();
	}

	public String getGeonetworkUserFromScope() throws Exception {
		GeoNetworkReader gn = initGeoNetworkReader();
		return gn.getConfiguration().getScopeConfiguration().getAccounts().get(Account.Type.SCOPE).getUser();
	}

	public String getGeonetworkPrivateGroup() throws Exception {
		GeoNetworkReader gn = initGeoNetworkReader();
		return ""+gn.getConfiguration().getScopeConfiguration().getPrivateGroup();
	}
	
	public String getGeonetworkPublicGroup() throws Exception {
		GeoNetworkReader gn = initGeoNetworkReader();
		return ""+gn.getConfiguration().getScopeConfiguration().getPublicGroup();
	}
	
	public String getGeonetworkPasswordFromScope() throws Exception {
		GeoNetworkReader gn = initGeoNetworkReader();
		return gn.getConfiguration().getScopeConfiguration().getAccounts().get(Account.Type.SCOPE).getPassword();
	}

	private Metadata getGNInfobyTitle(String info) throws Exception {

		GeoNetworkReader gn = initGeoNetworkReader();
		// Form query object
		gn.login(LoginLevel.ADMIN);
		GNSearchRequest req = new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.title, info);
		// req.addConfig(GNSearchRequest.Config.similarity, "1");
		GNSearchResponse resp = gn.query(req);
		Metadata meta = null;
		if (resp.getCount() != 0)
			for (GNSearchResponse.GNMetadata metadata : resp) {
				try {
					meta = gn.getById(metadata.getUUID());
					break;
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Error retrieving information for some metadata");
				}
			}

		return meta;
	}

	public List<Metadata> getAllGNInfobyTitle(String info, String tolerance) throws Exception {

		GeoNetworkReader gn = initGeoNetworkReader();
		// Form query object
		gn.login(LoginLevel.ADMIN);
		GNSearchRequest req = new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.title, info);
		req.addConfig(GNSearchRequest.Config.similarity, tolerance);
		GNSearchResponse resp = gn.query(req);
		Metadata meta = null;
		List<Metadata> metadatalist = new ArrayList<Metadata>();
		if (resp.getCount() != 0)
			for (GNSearchResponse.GNMetadata metadata : resp) {
				try {
					meta = gn.getById(metadata.getUUID());
					metadatalist.add(meta);
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Error retrieving information for some metadata");
				}
			}

		return metadatalist;
	}

	public List<Metadata> getAllGNInfobyText(String info, String tolerance) throws Exception {

		GeoNetworkReader gn = initGeoNetworkReader();
		// Form query object
		gn.login(LoginLevel.ADMIN);
		GNSearchRequest req = new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any, info);
		req.addConfig(GNSearchRequest.Config.similarity, tolerance);
		GNSearchResponse resp = gn.query(req);
		Metadata meta = null;
		List<Metadata> metadatalist = new ArrayList<Metadata>();
		if (resp.getCount() != 0)
			for (GNSearchResponse.GNMetadata metadata : resp) {
				try {
					meta = gn.getById(metadata.getUUID());
					metadatalist.add(meta);
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Error retrieving information for some metadata");
				}
			}

		return metadatalist;
	}

	private List<Metadata> getFastGNInfobyTitle(String info, String completeTitle, String tolerance) throws Exception {

		GeoNetworkReader gn = initGeoNetworkReader();
		// Form query object
		gn.login(LoginLevel.ADMIN);
		GNSearchRequest req = new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.title, info);
		req.addConfig(GNSearchRequest.Config.similarity, tolerance);
		GNSearchResponse resp = gn.query(req);

		Metadata meta = null;
		List<Metadata> metadatalist = new ArrayList<Metadata>();
		if (resp.getCount() != 0) {
			AnalysisLogger.getLogger().debug("Retrieving information ...");
			for (GNSearchResponse.GNMetadata metadata : resp) {
				try {
					meta = gn.getById(metadata.getUUID());
					Identification id = meta.getIdentificationInfo().iterator().next();
					String title = id.getCitation().getTitle().toString();
					if (title.equalsIgnoreCase(completeTitle)) {
						AnalysisLogger.getLogger().debug("Found UUID:" + metadata.getUUID());
						metadatalist.add(meta);
						break;
					}
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Error retrieving information for some metadata");
				}
			}
			AnalysisLogger.getLogger().debug("Information Successfully Retrieved");
		}
		return metadatalist;
	}

	private Metadata getGNInfobyUUID(String UUID) throws Exception {

		AnalysisLogger.getLogger().debug("Initializing GeoNetwork");
		GeoNetworkReader gn = initGeoNetworkReader();
		AnalysisLogger.getLogger().debug("Initialized GeoNetwork!");
		// Form query object
		gn.login(LoginLevel.ADMIN);
		Metadata meta = gn.getById(UUID);
		AnalysisLogger.getLogger().debug("Layer with UUID: " + UUID + " successfully Retrieved!");

		return meta;
	}

	public Metadata getGNInfobyUUIDorName(String layerUUIDorTitle) throws Exception {

		AnalysisLogger.getLogger().debug("MapsComparator: Getting layer with UUID..." + layerUUIDorTitle);
		Metadata meta = null;
		try {
			meta = getGNInfobyUUID(layerUUIDorTitle);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("MapsComparator: Impossible to get layer as UUID");
		}

		if (meta == null) {
			AnalysisLogger.getLogger().debug("MapsComparator: NO UUID Available - Trying with NAME..." + layerUUIDorTitle);
			try {
				meta = checkForMetadatabyTitle(GeoNetworkInspector.treatTitleForGN(layerUUIDorTitle), layerUUIDorTitle);
			} catch (Exception e) {
//				e.printStackTrace();
				AnalysisLogger.getLogger().debug("Layer does not exist: "+e.getLocalizedMessage());
			}
		}

		return meta;
	}

	private Metadata checkForMetadatabyTitle(String searchString, String completetitle) throws Exception {
		return checkForMetadatabyTitle(searchString, completetitle, "");
	}

	private Metadata checkForMetadatabyTitle(String searchString, String completetitle, String filename) throws Exception {
		AnalysisLogger.getLogger().debug("Searching for: " + searchString);
		List<Metadata> mlist = getFastGNInfobyTitle(searchString, completetitle, "1");
		AnalysisLogger.getLogger().debug("Found:" + mlist.size() + " results");
		Metadata mfound = null;
		// DefaultInternationalString intfilename = new DefaultInternationalString(filename);
		for (Metadata m : mlist) {
			Identification id = m.getIdentificationInfo().iterator().next();
			String title = id.getCitation().getTitle().toString();
			if (completetitle.equalsIgnoreCase(title)) {
				/*
				 * Iterator<? extends Keywords> it = id.getDescriptiveKeywords().iterator(); while (it.hasNext()){ Keywords keys = (Keywords)it.next(); for (InternationalString is :keys.getKeywords()){ // System.out.println(is); if (is.toString().equals(filename)){ mfound = m; break; } } if (mfound!=null) break; } } if (mfound!=null) break;
				 */
				mfound = m;
				break;
			}
		}

		return mfound;
	}

	public String getGeonetworkUrl() {
		return geonetworkUrl;
	}

	public void setGeonetworkUrl(String geonetworkUrl) {
		this.geonetworkUrl = geonetworkUrl;
	}

	public String getGeonetworkUser() {
		return geonetworkUser;
	}

	public void setGeonetworkUser(String geonetworkUser) {
		this.geonetworkUser = geonetworkUser;
	}

	public String getGeonetworkPwd() {
		return geonetworkPwd;
	}

	public void setGeonetworkPwd(String geonetworkPwd) {
		this.geonetworkPwd = geonetworkPwd;
	}

	public static String treatTitleForGN(String origLayerTitle) {
		String layerTitle = origLayerTitle.toLowerCase();
		int idx = layerTitle.indexOf(" from [");
		// int idx = layerTitle.indexOf(" "); //let's search among many layers
		String layerTitle2 = layerTitle;
		if (idx > 0)
			layerTitle2 = layerTitle.toLowerCase().substring(0, idx).trim();
		else {
			idx = layerTitle.indexOf(" in [");
			if (idx > 0)
				layerTitle2 = layerTitle.toLowerCase().substring(0, idx).trim();
			else {
				idx = layerTitle.indexOf("(");
				if (idx > 0)
					layerTitle2 = layerTitle.toLowerCase().substring(0, idx).trim();
			}
		}
		layerTitle2 = layerTitle2.replaceAll("(\\(.*\\))", " ");

		layerTitle2 = layerTitle2.replace("_", " ").replace("-", " ").replace("(", " ").replace(")", " ");
		String punct = "[!\"#$%&'*+,./:;<=>?@\\^_`{|}~-]";

		layerTitle2 = layerTitle2.replaceAll("( |^)+[^A-Za-z]+(" + punct + ")*[^A-Za-z]*", " ").trim();

		return layerTitle2.replaceAll(punct, " ").replaceAll("( )+", " ");
	}

	public static void main(String args[]) throws Exception {
		// String title = "temperature (04091217ruc.nc)";
		// String title = "Bathymetry";
		// String title = "FAO aquatic species distribution map of Melanogrammus aeglefinus";
		// String title = "geopotential height from [12/09/2004 19:00] to [12/09/2004 22:00] (04091217_ruc.nc)";
		String title = "geopotential height";
		GeoNetworkInspector fm = new GeoNetworkInspector();
//		Metadata meta = fm.getGNInfobyTitle(title);
//		fm.setScope("/gcube/devsec/devVRE");
//		fm.setScope("/gcube/devsec/devVRE");
		fm.setScope("/gcube/devsec");
//		Metadata meta = fm.getGNInfobyUUID("ffd86c4b-e624-493b-b279-2cd20d6b267f");
//		Metadata meta = fm.getGNInfobyUUID("0815e357-ebd7-4c02-8dc8-f945eceb870c"); //public
//		Metadata meta = fm.getGNInfobyUUID("d57e7f27-9763-4216-a72f-48289f35779f"); //private
		Metadata meta = fm.getGNInfobyUUID("c286077b-53e9-4389-aaf8-85fb3cb480a2"); //public
		
		System.out.println("is file? " + fm.isNetCDFFile(meta));
		System.out.println("opendap: " + fm.getOpenDapLink(meta));
		System.out.println("wcs:" + fm.getWCSLink(meta));
		System.out.println("wms:" + fm.getWMSLink(meta));
		System.out.println("thredds:" + fm.getThreddsLink(meta));
	}

	public static void main2(String args[]) throws Exception {
		System.out.println(treatTitleForGN("sea/land/lake/ice field composite mask from"));
	}
}
