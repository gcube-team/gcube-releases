package org.gcube.portlets.user.speciesdiscovery.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.speciesdiscovery.client.rpc.GISInfoService;
import org.gcube.portlets.user.speciesdiscovery.server.asl.SessionUtil;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchRequest.Config;
import it.geosolutions.geonetwork.util.GNSearchRequest.Param;

public class GisInfoServiceImpl extends RemoteServiceServlet implements GISInfoService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1137730151475571288L;
	
	protected static Logger logger = Logger.getLogger(GisInfoServiceImpl.class);
	
	@Override
	public String getGisLinkByLayerName(String layername) throws Exception {
		try{
			logger.debug("Received get gis ilnk for layer name : "+layername);
		SessionUtil.getAslSession(this.getThreadLocalRequest().getSession());
		logger.debug("Loaded ASL, looking for layer thorugh geonetwork..");
		String uuid=getUUIDbyGSId(layername);
		logger.debug("UUID is "+uuid);
		String publicLink=getPublicLink(uuid);
		logger.debug("public link is "+publicLink);
		return publicLink;
		}catch (Exception e){
			throw e;
		}
	}

	
	private static String getPublicLink(String uuid) throws UriResolverMapException, IllegalArgumentException{
		UriResolverManager resolver = new UriResolverManager("GIS");
		
		
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("gis-UUID", uuid);
		params.put("scope", ScopeProvider.instance.get());
		return resolver.getLink(params, true);
	}
	
	
	private static String getUUIDbyGSId(String gsID) throws Exception{
		GeoNetworkReader reader=GeoNetwork.get();
		reader.login(LoginLevel.ADMIN);


		GNSearchRequest req=new GNSearchRequest();
		req.addParam(Param.any, gsID);
		req.addConfig(Config.similarity, "1");
		GNSearchResponse resp=reader.query(req);		
		return resp.getMetadata(0).getUUID();
	}
	
}
