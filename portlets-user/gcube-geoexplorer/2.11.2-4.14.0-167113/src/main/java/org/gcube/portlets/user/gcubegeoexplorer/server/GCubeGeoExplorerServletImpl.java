/**
 *
 */
package org.gcube.portlets.user.gcubegeoexplorer.server;



import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.gcubegeoexplorer.client.ConstantsGcubeGeoExplorer;
import org.gcube.portlets.user.gcubegeoexplorer.client.GeoexplorerMetadataStyle;
import org.gcube.portlets.user.gcubegeoexplorer.server.entity.GeoexplorerDefaultLayer;
import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl;
import org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface;
import org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceParameters;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author Modified By Francesco Mangiacrapa
 *
 */
public class GCubeGeoExplorerServletImpl extends GeoExplorerServiceImpl {

	private static final long serialVersionUID = 804152795418658243L;

	public static final String USERNAME_ATTRIBUTE = "username";

	public static final String GEOSERVER_RESOURCE_NAME = "GeoServer";
	public static final String GEONETWORK_RESOURCE_NAME = "GeoNetwork";

	public static final String GEOCALLER_ATTRIBUTE_NAME = "GEOCALLER";
	private static final String WORKSPACES_PROPERTY_NAME = "workspaces";
	public static final long CACHE_REFRESH_TIME = 10*60*1000;

	public static Logger logger = Logger.getLogger(GCubeGeoExplorerServletImpl.class);

	protected Map<String, GeoExplorerServiceInterface> parametersCache = new HashMap<String, GeoExplorerServiceInterface>();

//	protected Map<String, GeoexplorerApplicationProfileReader> layerCache = new HashMap<String, GeoexplorerApplicationProfileReader>();
	protected Timer timer;

	protected List<String> listUrlInternalGeoserver = new ArrayList<String>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				refreshParametersCache();
			}
		}, 0, CACHE_REFRESH_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		super.destroy();
		timer.cancel();
	}

	protected ASLSession getASLSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.warn("GCubeGeoExplorerServletImpl STARTING IN TEST MODE - NO USER FOUND");

			//for test only
			user = ConstantsGcubeGeoExplorer.TEST_USER;
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);

			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(ConstantsGcubeGeoExplorer.DEFAULT_SCOPE);


			return session;
		} else logger.trace("user found in session "+user);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}


	protected ServerParameters getParameters(ScopeBean scope, boolean isGeoserver) throws Exception
	{

		ServerParameters parameters = new ServerParameters();
		String resourceName = isGeoserver ? GEOSERVER_RESOURCE_NAME : GEONETWORK_RESOURCE_NAME;
		logger.debug("Get Parameters from RR: "+resourceName +", scope: "+scope.toString());
		try{

			ScopeProvider.instance.set(scope.toString());

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+resourceName);

			ServiceEndpoint se = r.get(0);
			if(se.profile()==null)
				throw new Exception("IS profile is null for resource: "+resourceName);

			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource "+resourceName+" not found");

			AccessPoint ap = accessPoints.iterator().next();
			parameters.setUrl(ap.address());
			parameters.setUser(ap.username()); //username

			String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
			logger.debug("resourceName: "+resourceName +", scope: "+scope.toString() +", username: "+ap.username() +", password: "+ap.password());
			parameters.setPassword(decryptedPassword); //password

			if (isGeoserver){
				Group<Property> properties = ap.properties();

				if(properties.size()==0) throw new Exception("Properties in resource "+resourceName+" not found");

				Iterator<Property> iter = properties.iterator();

				while (iter.hasNext()) {

					Property prop = iter.next();

					if(prop.name().compareTo(WORKSPACES_PROPERTY_NAME)==0){
						logger.trace("Property "+WORKSPACES_PROPERTY_NAME+" found, setting value: "+prop.value());
						parameters.setWorkspaces(prop.value());
						break;
					}
				}
			}

		}catch (Exception e) {
			logger.error("Sorry, an error occurred on reading parameters in Runtime Reosurces for scope: "+scope +" resourceName: "+resourceName, e);
			e.printStackTrace();
		}

		return parameters;
	}

	protected GeoExplorerServiceInterface retrieveGisParameters(ScopeBean scope) throws Exception
	{
		logger.debug("Reading GisParameters...");
		GeoExplorerServiceParameters geoExplorerServiceParameters = new GeoExplorerServiceParameters();
		geoExplorerServiceParameters.setScope(scope.toString());

		try {
			ServerParameters geoServerParameters = getParameters(scope, true);
			geoExplorerServiceParameters.setGeoServerUrl(geoServerParameters.getUrl());
			geoExplorerServiceParameters.setGeoServerUser(geoServerParameters.getUser());
			geoExplorerServiceParameters.setGeoServerPwd(geoServerParameters.getPassword());
//			gisViewerServiceParameters.setWorkspaces(geoServerParameters.getWorkspaces());

			listUrlInternalGeoserver.add(geoServerParameters.getUrl());

		} catch (Exception e)
		{
			logger.error("Error retrieving the GeoServer parameters", e);
			throw new Exception("Error retrieving the GeoServer parameters", e);
		}

		try {
			ServerParameters geoNetworkParameters = getParameters(scope, false);
			geoExplorerServiceParameters.setGeoNetworkUrl(geoNetworkParameters.getUrl());
			geoExplorerServiceParameters.setGeoNetworkUser(geoNetworkParameters.getUser());
			geoExplorerServiceParameters.setGeoNetworkPwd(geoNetworkParameters.getPassword());

		} catch (Exception e)
		{
			logger.error("Error retrieving the Gis parameters", e);
			throw new Exception("Error retrieving the GeoNetwork parameters", e);
		}

		logger.info("retrieved parameters done!");

//		System.out.println("retrieved parameters: "+geoExplorerServiceParameters);

		return geoExplorerServiceParameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GeoExplorerServiceInterface getGeoParameters() throws Exception {
		//httpSession = this.getThreadLocalRequest().getSession();
		GeoExplorerServiceInterface parameters = null;
		try{

			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			String scope = session.getScope();

			parameters = parametersCache.get(scope);
			logger.trace("ParametersCache is: "+ parameters);

			if (parameters == null) {
				logger.trace("ParametersCache with scope: "+scope +" is null, creating..");
				parameters = retrieveGisParameters(new ScopeBean(scope));
				parametersCache.put(scope.toString(), parameters);
				logger.trace("ParametersCache filled with scope: "+scope +" and parameter: "+parameters);
			}else
				logger.trace("ParametersCache with scope: "+scope.toString() +" is not null, returning cached parameters: "+parameters);

		}catch (Exception e) {
			logger.error("Error retrieving Geo Parameters", e);
		}

		return parameters;
	}

	protected void refreshParametersCache(){
		logger.info("refreshParametersCache start...");

		for (String scope: parametersCache.keySet()) {
			try{
				logger.info("Refreshing cache with gis parameters for scope:" +scope);
				if(listUrlInternalGeoserver!=null && listUrlInternalGeoserver.size()>0)
					listUrlInternalGeoserver.clear();

				GeoExplorerServiceInterface parameters = retrieveGisParameters(new ScopeBean(scope));
				parametersCache.put(scope.toString(), parameters);

				logger.info("Refreshing completed");
			} catch (Exception e) {
				logger.warn("An error occured retrieving gis parameters for scope "+scope, e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getInternalGeoserver()
	 */
	@Override
	protected List<String> getInternalGeoserver() throws Exception {
		return listUrlInternalGeoserver;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getDefaultLayers()
	 */
	@Override
	public List<String> getDefaultLayersItem() throws Exception {

		try{

			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			String scope = session.getScope();

			// DEBUG CODE
//			String scope = "/gcube/devsec/devVRE";

			List<String> listUUID = new ArrayList<String>();

			List<GeoexplorerDefaultLayer> listLayers = getListGeoxplorerLayersFromGR(scope);

			if(listLayers==null || listLayers.isEmpty())
				return listUUID;

			//GET UUID-S
			for (GeoexplorerDefaultLayer geoexplorerDefaultLayer : listLayers) {

				logger.trace("GeoexplorerDefaultLayer found: " + geoexplorerDefaultLayer);
				logger.trace("isBaseLayer: " + geoexplorerDefaultLayer.isBaseLayer());

				if(!geoexplorerDefaultLayer.isBaseLayer()){
					logger.trace("adding "+geoexplorerDefaultLayer.getUUID()+" as default layer");
					listUUID.add(geoexplorerDefaultLayer.getUUID());
				}
			}

//			return getListLayerItemByUUID(listUUID);

			return listUUID;

		}catch (Exception e) {
			logger.error("An error occurred in getDefaultLayersItem ", e);
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#
	 * getDefaultLayers()
	 */
	@Override
	public List<String> getBaseLayersItem() throws Exception {

		try {

			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			String scope = session.getScope();

			// DEBUG CODE
//			String scope = "/gcube/devsec/devVRE";

			List<String> listUUID = new ArrayList<String>();

			List<GeoexplorerDefaultLayer> listLayers = getListGeoxplorerLayersFromGR(scope);

			if(listLayers==null || listLayers.isEmpty())
				return listUUID;

			// GET UUID-S
			for (GeoexplorerDefaultLayer geoexplorerDefaultLayer :listLayers) {

				logger.trace("GeoexplorerDefaultLayer found: " + geoexplorerDefaultLayer);
				logger.trace("isBaseLayer: " + geoexplorerDefaultLayer.isBaseLayer());
				if (geoexplorerDefaultLayer.isBaseLayer()){
					logger.trace("adding "+geoexplorerDefaultLayer.getUUID()+" as base layer");
					listUUID.add(geoexplorerDefaultLayer.getUUID());
				}
			}
			return listUUID;
			// return getListLayerItemByUUID(listUUID);

		} catch (Exception e) {
			logger.error("An errror occurred in getBaseLayersItem ", e);
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	/**
	 *
	 * @param scope
	 * @return
	 */
	private List<GeoexplorerDefaultLayer> getListGeoxplorerLayersFromGR(String scope){

		try{
			GeoexplorerDefaultLayersApplicationProfileReader reader = new GeoexplorerDefaultLayersApplicationProfileReader(new ScopeBean(scope));
			return reader.getListGeoexplorerDefaultLayer();
		}catch(Exception e){
			logger.error("An error occurred on recovering default layer from Generic Resource, returning empty list. ",e);
			return new ArrayList<GeoexplorerDefaultLayer>();
		}
	}

	/**
	 *
	 * @param onlyIsDisplay if true return only metadata where isDisplay is true
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<? extends GeoexplorerMetadataStyleInterface> getGeoexplorerStylesToShow(boolean onlyIsDisplay) throws Exception {

		try {

			logger.trace("GeoexplorerStylesToShow, onlyIsDisplay: " + onlyIsDisplay);

			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			String scope = session.getScope();

			// DEBUG CODE
//			String scope = "/gcube/devsec/devVRE";

			GeoexplorerMetadataStylesApplicationProfileReader reader = new GeoexplorerMetadataStylesApplicationProfileReader(new ScopeBean(scope));

			if(onlyIsDisplay){
				List<GeoexplorerMetadataStyle> listMetadataStyles = new ArrayList<GeoexplorerMetadataStyle>();

				for (GeoexplorerMetadataStyle geoMetaStyle : reader.getListGeoexplorerMetadataStyles()) {

					logger.trace("GeoexplorerMetadataStyle found: " + geoMetaStyle);

					if (geoMetaStyle.isDisplay())
						listMetadataStyles.add(geoMetaStyle);
				}
				return listMetadataStyles;
			}

			logger.trace("GeoexplorerStylesToShow, returning " + reader.getListGeoexplorerMetadataStyles().size() +" style/s");
			return reader.getListGeoexplorerMetadataStyles();

			// return getListLayerItemByUUID(listUUID);

		} catch (Exception e) {
			logger.error("An errror occurred in getGeoexplorerStylesToShow ", e);
			e.printStackTrace();
			return new ArrayList<GeoexplorerMetadataStyle>(1);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#invalidCache(org.gcube.common.scope.impl.ScopeBean)
	 */
	@Override
	protected void invalidCache(ScopeBean scope) {

		logger.info("Invalidating geo cache for scope: "+scope);

		if(parametersCache==null || scope==null){
			logger.trace("parametersCache or scope is null, returning");
			return;
		}

		GeoExplorerServiceInterface geoConf = parametersCache.get(scope.toString());
		logger.info("Invalidating geo cache.. GeoExplorerServiceInterface for searched scope is: "+geoConf);
		if(geoConf!=null){
			logger.info("removing from geo cache GeoExplorerServiceInterface with scope: "+geoConf.getScope());
			parametersCache.remove(scope.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getGisViewerLinkForUUID(java.lang.String)
	 */
	@Override
	protected String getGisLinkForUUID(String uuid) throws Exception {

		if(uuid == null || uuid.isEmpty())
			throw new Exception("UUID is null");

		String shortLink;

		try {
			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			String scope = session.getScope();
			//SCOPE TO READ UriResolverManager Generic Resource
			ScopeProvider.instance.set(scope);
			UriResolverManager resolver = new UriResolverManager("GIS");
			Map<String, String> params = new HashMap<String, String>();
			params.put("gis-UUID", uuid);
			//SCOPE TO INSTANCE GEONETWORK
			GeoExplorerServiceInterface parameters = getGeoParameters();
			params.put("scope", parameters.getScope());
			shortLink = resolver.getLink(params, true);
			logger.info("returning gisviewer link: "+shortLink);
		} catch (UriResolverMapException e) {
			logger.error("Error on retrieving gisviewer link for uuid: " + uuid, e);
			throw new Exception("An error occurred on generating gisviewer link for uuid: " + uuid);
		} catch (IllegalArgumentException e) {
			logger.error("Error on retrieving gisviewer link for uuid: " + uuid, e);
			throw new Exception("An error occurred on generating gisviewer link for uuid: ", e);
		} catch (Exception e) {
			logger.error("Error on retrieving gisviewer link for uuid: " + uuid, e);
			throw new Exception("An error occurred on generating gisviewer link for uuid: " + uuid);
		}

		return shortLink;
	}



	public static void main(String[] args) throws Exception
	{

		try{
			String DEFAULT_SCOPE = "/gcube/devsec/devVRE";
			DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps/EGIP";
//			DEFAULT_SCOPE = "/gcube/devNext";
//			ScopeProvider.instance.set(DEFAULT_SCOPE);

			GCubeGeoExplorerServletImpl impl = new GCubeGeoExplorerServletImpl();
			GeoExplorerServiceInterface geoServiceInterface = impl.retrieveGisParameters(new ScopeBean(DEFAULT_SCOPE));

			System.out.println(geoServiceInterface);

			List<String> defaults = impl.getDefaultLayersItem();
			for (String layer : defaults) {
				System.out.println(layer);
			}
			List<? extends GeoexplorerMetadataStyleInterface> metadata = impl.getGeoexplorerStylesToShow(true);

			for (GeoexplorerMetadataStyleInterface geoexplorerMetadataStyleInterface : metadata) {
				System.out.println(geoexplorerMetadataStyleInterface);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}

	}
}
