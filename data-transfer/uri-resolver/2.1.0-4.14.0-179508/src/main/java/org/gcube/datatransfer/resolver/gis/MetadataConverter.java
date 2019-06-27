package org.gcube.datatransfer.resolver.gis;

import java.util.Collection;

import org.gcube.datatransfer.resolver.gis.entity.GeoserverBaseUri;
import org.gcube.datatransfer.resolver.gis.entity.GisLayerItem;
import org.gcube.datatransfer.resolver.util.HttpRequestUtil;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.identification.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class MetadataConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 15, 2017
 */
public class MetadataConverter {

	private static final String GEOSERVER = "/geoserver";
	protected static final String SERVICE_WMS = "service=wms";
	protected static Logger logger = LoggerFactory.getLogger(MetadataConverter.class);
	public static final String NOT_FOUND = "";

	/**
	 * Gets the geoserver base uri.
	 *
	 * @param uri the uri
	 * @return the input uri without the parameters, (the uri substring from start to index of '?' char (if exists)) if geoserver base url not found,
	 * geoserver url otherwise
	 */
	public static GeoserverBaseUri getGeoserverBaseUri(String uri){

		GeoserverBaseUri geoserverBaseUri = new GeoserverBaseUri();

		if(uri==null)
			return geoserverBaseUri; //uri is empty

//		 Remove each string after "?"
		int end = uri.toLowerCase().lastIndexOf("?");

		if(end==-1){
			logger.trace("char ? not found in geoserver uri, return: "+uri);
			return geoserverBaseUri; //uri is empty
		}

		String geoserverUrl = uri.substring(0, uri.toLowerCase().lastIndexOf("?"));

		int index = geoserverUrl.lastIndexOf(GEOSERVER);

		if(index>-1){ //FOUND the string GEOSERVER into URL
			logger.trace("found geoserver string: "+GEOSERVER+" in "+geoserverUrl);

			//THERE IS SCOPE?
			int lastSlash = geoserverUrl.lastIndexOf("/");
			int includeGeoserverString = index+GEOSERVER.length();
			int endUrl = lastSlash>includeGeoserverString?lastSlash:includeGeoserverString;

			logger.trace("indexs - lastSlash: ["+lastSlash+"],  includeGeoserverString: ["+includeGeoserverString+"], endUrl: ["+endUrl+"]");

			int startScope = includeGeoserverString+1<endUrl?includeGeoserverString+1:endUrl; //INCLUDE SLASH
			String scope = geoserverUrl.substring(startScope, endUrl);

			logger.trace("geoserver url include scope: "+geoserverUrl.substring(includeGeoserverString, endUrl));

			geoserverBaseUri.setBaseUrl(geoserverUrl.substring(0, endUrl));
			geoserverBaseUri.setScope(scope);

			return geoserverBaseUri;
		}
		else{

			logger.trace("the string 'geoserver' not found in "+geoserverUrl);
			// GET LAST INDEX OF '/' AND CONCATENATE GEOSERVER
			String urlConn = geoserverUrl.substring(0, geoserverUrl.lastIndexOf("/"))+GEOSERVER;
			logger.trace("tentative concatenating string 'geoserver' at http url "+urlConn);

			try {

				if(HttpRequestUtil.urlExists(urlConn, false)){
					logger.trace("url: "+urlConn+" - open a connection, return "+urlConn);
					geoserverBaseUri.setBaseUrl(urlConn);
					return geoserverBaseUri;
				}
				else
					logger.trace("url: "+urlConn+" - not open a connection");

			} catch (Exception e) {
				logger.error("url connection is wrong at :"+urlConn);
			}

			String uriWithoutParameters = uri.substring(0, end);
			logger.trace("url connection, returned: "+uriWithoutParameters);
			geoserverBaseUri.setBaseUrl(uriWithoutParameters);
			return geoserverBaseUri;
		}
	}


	/**
	 * Gets the WMS on line resource.
	 *
	 * @param geonetowrkInstance the geonetowrk instance
	 * @param uuid the uuid
	 * @return the WMS on line resource
	 * @throws Exception the exception
	 */
	public static GisLayerItem getWMSOnLineResource(GeonetworkInstance geonetowrkInstance, String uuid) throws Exception{

		String fullWmsPath = "";
		boolean foundGeoserverUrl = false;
		String layerName = "";
		//IT IS LAYER TITLE
		String citationTitle = null;
		try{

			logger.trace("geonetowrkInstance is null? "+(geonetowrkInstance==null));
			Metadata meta = geonetowrkInstance.getGeonetworkPublisher().getById(uuid);

			if(meta.getDistributionInfo()!=null && meta.getDistributionInfo()!=null){

				for (DigitalTransferOptions  item: meta.getDistributionInfo().getTransferOptions()) {
	//				System.out.println(++i +" item DigitalTransferOptions options: "+item);
					if(item.getOnLines()!=null){
						Collection<? extends OnlineResource> onlineResources = item.getOnLines();

						for (OnlineResource onlineResource : onlineResources) {
							String geoserverUrl = onlineResource.getLinkage()!=null? onlineResource.getLinkage().toString():"";

							//FIND ONLINE RESOURCES WITH GEOSERVER WMS PROTOCOL
							if(!geoserverUrl.isEmpty()){

								int indexServiceWMS = geoserverUrl.toLowerCase().lastIndexOf(SERVICE_WMS);
								fullWmsPath = geoserverUrl;

								//IS OWS OR WMS?
								if(indexServiceWMS>-1){
									logger.info("found "+SERVICE_WMS+" url "+geoserverUrl);
//									isOwsService = geoserverUrl.contains("ows");
//									tempBaseUri = getGeoserverBaseUri(geoserverUrl);
									geoserverUrl.contains("ows");
									getGeoserverBaseUri(geoserverUrl);
									if(!geoserverUrl.contains("layers") && !geoserverUrl.contains("LAYERS")){
										logger.info("geoserverUrl does not contain 'layers' param, reading");
										layerName= onlineResource.getName()!=null? onlineResource.getName():"";
										logger.info("found layer name: " +layerName);
										if(!layerName.isEmpty()){
											logger.info("added layers = "+layerName);
											fullWmsPath+="&layers="+layerName;
										}
									}
									break;
								}

								if(!foundGeoserverUrl)
									logger.trace(SERVICE_WMS+" not found for "+uuid);
							}
						}
					}
				}
			}

			if(meta.getIdentificationInfo()!=null){
//				logger.trace("found Identification Info size: "+meta.getIdentificationInfo().size());
				for (Identification info : meta.getIdentificationInfo()) {

					if(info!=null){
						Citation citation = info.getCitation();
						if(citation!=null){
							citationTitle = citation.getTitle() != null? citation.getTitle().toString():"";
//								logger.trace("found citation Title: "+citationTitle);
						}else
							logger.info("Title is null for: "+uuid);
					}
				}
			}
		}catch(Exception e){
			logger.error("getWMSOnLineResource with UUID "+uuid + " has thrown exception: ",e);
			throw new Exception("An error occurred when converting layer with UUID "+uuid);
		}
		GisLayerItem gisLI = new GisLayerItem(uuid, citationTitle, layerName, "", fullWmsPath);
		logger.debug("returning: "+gisLI);

		return gisLI;
	}

	/*
	public static void main(String[] args) throws Exception {

//		String geoserver = "http://www.fao.org/figis/a/wms/?service=WMS&version=1.1.0&request=GetMap&layers=area:FAO_AREAS&styles=Species_prob, puppa&bbox=-180.0,-88.0,180.0,90.0000000694&width=667&height=330&srs=EPSG:4326&format=image%2Fpng";
//		System.out.println(MetadataConverter.getGeoserverBaseUri(geoserver));

		String user ="admin";
		String pwd = "admin";
		boolean authenticate = true;
//		String uuid ="177e1c3c-4a22-4ad9-b015-bfc443d16cb8";
		String uuid ="fao-species-map-bep";
//		String uuid ="fao-species-map-bon"; //FAO
		String geoNetworkUrl ="http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork";
		GeonetworkInstance geonetowrkInstance = new GeonetworkInstance(geoNetworkUrl, user, pwd, authenticate);
		String onLineResource = getWMSOnLineResource(geonetowrkInstance, uuid);
		System.out.println(onLineResource);

	}*/
}
