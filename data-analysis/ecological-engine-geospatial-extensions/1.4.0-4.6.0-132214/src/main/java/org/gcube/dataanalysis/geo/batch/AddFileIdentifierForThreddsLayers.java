package org.gcube.dataanalysis.geo.batch;

import it.geosolutions.geonetwork.GN210Client;
import it.geosolutions.geonetwork.GN26Client;
import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.protocol.Protocol;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.DefaultConfiguration;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.util.DefaultInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.identification.Identification;

public class AddFileIdentifierForThreddsLayers {

	/* production settings */

	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geonetworkUser = "admin";
	static String geonetworkPwd = "kee9GeeK";
	static String targetScope = "/d4science.research-infrastructures.eu/gCubeApps";

	/* dev settings */
/*	
	 static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/"; static String geonetworkUser = "admin"; static String geonetworkPwd = "Geey6ohz"; static String targetScope = "/gcube/devsec";
	*/ 

	public static void main(String[] args) throws Exception {
		String title = "thredds";
//		String title = "VLIZ";
		changeScope2(title, targetScope, targetScope);
	}


	public static void changeScope2(String title, String startScope, String targetScope) throws Exception {

		ScopeProvider.instance.set(startScope);

		GeoNetworkAdministration reader = GeoNetwork.get();
		reader.login(LoginLevel.SCOPE);
		DefaultConfiguration geonetworkCfg = (DefaultConfiguration) reader.getConfiguration();

		Map<Account.Type, Account> accounts = geonetworkCfg.getScopeConfiguration().getAccounts();
		Account account = accounts.get(Account.Type.SCOPE);

		String geonetworkScopeUser = account.getUser();
		String geonetworkScopePassword = account.getPassword();
		Integer scopePublicGroup = geonetworkCfg.getScopeConfiguration().getPublicGroup();

		System.out.println("GeoNetwork user " + geonetworkScopeUser);
		System.out.println("GeoNetwork password " + geonetworkScopePassword);
		System.out.println("GeoNetwork scope Public Group " + scopePublicGroup);

		System.out.println("GeoNetwork Admin user " + geonetworkCfg.getAdminAccount().getUser());
		System.out.println("GeoNetwork Admin password " + geonetworkCfg.getAdminAccount().getPassword());
		System.out.println("GeoNetwork scope Public Group " + scopePublicGroup);

		reader = GeoNetwork.get();
		reader.login(LoginLevel.ADMIN);

		// Configure search request
		GNSearchRequest req = new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any, title);
		req.addConfig(GNSearchRequest.Config.similarity, "1");
		GNSearchResponse resp = reader.query(req);

		// Iterate through results and access found metadata
		Long id = 0L;
		Metadata meta = null;
		int N = resp.getCount();
		System.out.println("Found N layers: " + N);
		int i = 1;
		for (GNSearchResponse.GNMetadata metadata : resp) {
			System.out.println("Layer " + i + " of " + N);
			i++;
			id = metadata.getId();
			System.out.println("ID " + id);
			System.out.println("Name " + metadata.getInfo().getName());
			meta = reader.getById(id);
			Identification idf = meta.getIdentificationInfo().iterator().next();
			String otitle = idf.getCitation().getTitle().toString();
			String oabstract = idf.getAbstract().toString();

			System.out.println("Title " + otitle);

			System.out.println("Publishing " + id);
			// look for target configuration
			ScopeConfiguration targetConfiguration = null;
			targetScope = targetScope.substring(targetScope.lastIndexOf("/") + 1);
			System.out.println("target scope " + targetScope);
			for (ScopeConfiguration configuration : reader.getConfiguration().getExistingConfigurations())
				if (configuration.getAssignedScope().equals(targetScope))
					targetConfiguration = configuration;

			if (targetConfiguration == null)
				throw new MissingConfigurationException("Scope " + targetScope + " has no configuration");

			int targetUserId = UserUtils.getByName(reader.getUsers(), targetConfiguration.getAccounts().get(Account.Type.SCOPE).getUser()).getId();
			int targetGroup = targetConfiguration.getDefaultGroup();

			System.out.println("ID " + id + " targetUserId " + targetUserId + " targetGroup " + targetGroup + " Public group " + scopePublicGroup);

			String fileIdentifier = ((DefaultMetadata) meta).getFileIdentifier();
			System.out.println("File identifier: " + fileIdentifier);

			GNClient client = null;
			File tmetafile = null;
			try {
				System.out.println("deleting meta " + id);
				client = new GN210Client(geonetworkurl,geonetworkUser,geonetworkPwd);
				
				client.deleteMetadata(id);
				System.out.println("inserting meta");
				client = new GN210Client(geonetworkurl,geonetworkScopeUser,geonetworkScopePassword);
				
				tmetafile = GenericLayerMetadata.meta2File(meta);
				GeoNetworkAdministration readerScope = GeoNetwork.get();
				readerScope.login(LoginLevel.SCOPE);
				GNInsertConfiguration configuration = readerScope.getCurrentUserConfiguration("datasets", "_none_");
				if (fileIdentifier == null) {
					((DefaultMetadata) meta).setFileIdentifier(UUID.randomUUID().toString());
				} else
					System.out.println("File identifier already present! " + fileIdentifier);

				DefaultMetadata fullmeta = (DefaultMetadata) meta;
				DefaultDistribution distributionInfo = (DefaultDistribution) fullmeta.getDistributionInfo();

				// DefaultDigitalTransferOptions transferOptions = (DefaultDigitalTransferOptions) distributionInfo.getTransferOptions();
				for (DigitalTransferOptions options : distributionInfo.getTransferOptions()) {
					DefaultDigitalTransferOptions transferOptions = (DefaultDigitalTransferOptions) options;
					for (OnlineResource resource : transferOptions.getOnLines()) {
						DefaultOnlineResource onlineres = (DefaultOnlineResource) resource;
						String url = onlineres.getLinkage().toURL().toString();
						String estdescription = getDescription(url);
						String estProtocol = getProtocol(url);
						String estName = getName(otitle, oabstract, estProtocol, url);
						System.out.println("*****" + otitle + "*****");
						System.out.println("URL:" + url + "\nDescription:" + estdescription + "\nName:" + estName + "\nProtocol:" + estProtocol);
						System.out.println("*****************");
						onlineres.setDescription(new DefaultInternationalString(estdescription));
						onlineres.setName(estName);
						onlineres.setProtocol(estProtocol);
					}
				}

				tmetafile.delete();
				readerScope.insertMetadata(configuration, meta);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Using standard client");
				long metaid = client.insertMetadata(new GNInsertConfiguration("" + scopePublicGroup, "datasets", "_none_", true), tmetafile);
				System.out.println("Generated Metaid " + metaid);
			}

			System.out.println("Done with " + otitle);
			Thread.sleep(2000);
			// break;
		}

		System.out.println("All done");
	}

	public static String getProtocol(String url) {

		if (url.toLowerCase().contains("service=wfs"))
			return "OGC:WFS-1.1.0-http-get-feature";

		else if (url.toLowerCase().contains("service=wms"))
			return "OGC:WMS-1.3.0-http-get-map";

		else if (url.toLowerCase().contains("service=wcs"))
			return "OGC:WCS-1.0.0-http-get-coverage";

		else if (url.toLowerCase().contains("/dodsC"))
			return "OGC:OPeNDAP-2.0.0";

		else
			return "WWW:LINK-1.0-http--link";
	}

	public static String getDescription(String url) {

		if (url.toLowerCase().contains("service=wfs"))
			return "GIS data (WFS - JSON)";

		else if (url.toLowerCase().contains("service=wms")) {
			if (url.toLowerCase().contains("openlayers"))
				return "GIS data (WMS - OpenLayers)";
			else
				return "GIS data (WMS)";
		} else if (url.toLowerCase().contains("service=wcs"))
			return "GIS data (WCS - Geotiff)";

		else if (url.toLowerCase().contains("/dodsc"))
			return "GIS data (OPenNDAP - NetCDF)";

		else
			return "Direct HTTP link";
	}

	public static String getName(String title, String abstractTitle, String protocol, String url) {

		if (abstractTitle.matches(".+: ")) {
			String variablename = title.substring(title.indexOf(":"));
			System.out.println("detected variable " + variablename);
			return variablename;
		} else {
			if (url.contains("catalog.xml")) {
				return "Catalogue";
			} else if (protocol.equals("WWW:LINK-1.0-http--link") || protocol.equals("OGC:OPeNDAP-2.0.0")) {
				int slash = url.lastIndexOf("/");
				if (slash > 0) {
					String file = url.substring(slash + 1);
					if (file.trim().length() > 0 && file.contains("."))
						return file;
					else
						return "Direct link to resource";
				} else
					return "Direct link to resource";
			} else {
				int check = -1;
				String checker = "";
				if (protocol.equals("OGC:WMS-1.3.0-http-get-map")) {
					checker = "layers=";
				} else if (protocol.equals("OGC:WFS-1.0.0-http-get-feature")) {
					checker = "typename=";
				} else if (protocol.equals("OGC:WCS-1.0.0-http-get-coverage")) {
					checker = "coverage=";
				}

				check = url.toLowerCase().indexOf(checker);
				if (check > 0) {
					String cutUrl = url.substring(check + checker.length());
					int end = cutUrl.indexOf("&");
					String layerName = cutUrl;
					if (end > -1)
						layerName = cutUrl.substring(0, end);

					int column = -1;
					if ((column = layerName.indexOf(":")) > 0)
						layerName = layerName.substring(column + 1);
					System.out.println("Layer Name: " + layerName);
					return layerName;
				} else
					return title;
			}
		}

	}

}
