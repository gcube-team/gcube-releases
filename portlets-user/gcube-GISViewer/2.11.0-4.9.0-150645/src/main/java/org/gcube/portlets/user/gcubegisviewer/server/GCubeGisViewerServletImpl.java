/**
 *
 */
package org.gcube.portlets.user.gcubegisviewer.server;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portlets.user.gcubegisviewer.client.GCubeGisViewerService;
import org.gcube.portlets.user.gcubegisviewer.client.GCubeGisViewerServiceException;
import org.gcube.portlets.user.gcubegisviewer.server.readers.RuntimeResourceReader;
import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl;
import org.gcube.portlets.user.gisviewer.server.GisViewerServiceParameters;
import org.gcube.portlets.user.gisviewer.server.MapGeneratorUtils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * The Class GCubeGisViewerServletImpl.
 *
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author updated by "Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it"
 * Feb 2, 2016
 */
public class GCubeGisViewerServletImpl extends GisViewerServiceImpl implements GCubeGisViewerService {

	/**
	 *
	 */
	protected static final String SCOPE_SEPARATOR = "/";

	private static final long serialVersionUID = 804152795418658243L;

	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String GEOSERVER_RESOURCE_NAME = "GeoServer";
	public static final String GEONETWORK_RESOURCE_NAME = "GeoNetwork";
	public static final String TRANSECT_RESOURCE_NAME = "Transect";
	public static final String DATAMINER_RESOURCE_NAME = "DataMiner";
	public static final String GEOCALLER_ATTRIBUTE_NAME = "GEOCALLER";
	public static final long CACHE_REFRESH_TIME = 10*60*1000;

	public static Logger log = Logger.getLogger(GCubeGisViewerServletImpl.class);

	protected Map<String, GisViewerServiceParameters> parametersCache = new HashMap<String, GisViewerServiceParameters>();
	protected static Logger logger = Logger.getLogger(GCubeGisViewerServletImpl.class);
	protected Timer timer;

	private final static String DEFAULT_ROLE = "OrganizationMember";

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

	/**
	 * Gets the ASL session.
	 *
	 * @param httpSession the http session
	 * @return the ASL session
	 */
	protected ASLSession getASLSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		if (user == null) {

			log.info("STARTING IN TEST MODE - NO USER FOUND");

			//for test only
			user = "test.user";
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(Constants.defaultScope);
			//session.setScope("/gcube/devsec/devVRE");

			return session;
		} else logger.trace("user found in session "+user);
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}


	/**
	 * Gets the parameters.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @return the parameters
	 * @throws Exception the exception
	 */
	protected ServerParameters getParameters(ScopeBean scope, String resourceName) throws Exception
	{
		logger.trace("setting scope instance: "+scope.toString());
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

		ServerParameters parameters = new ServerParameters();

		AccessPoint ap = accessPoints.iterator().next();
		parameters.setUrl(ap.address());
		parameters.setUser(ap.username()); //username

		String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
		logger.info("resourceName: "+resourceName +", scope: "+scope.toString() +", username: "+ap.username() +", password: "+ap.password() +" decryptedPassword: "+decryptedPassword);
		parameters.setPassword(decryptedPassword); //password

		return parameters;
	}

	/**
	 * Gets the transect url.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @return the transect url
	 * @throws Exception the exception
	 */
	protected String getTransectUrl(ScopeBean scope, String resourceName) throws Exception
	{

		ScopeProvider.instance.set(scope.toString());

		String queryString = "for $resource in collection('/db/Profiles/GenericResource')" +
				"//Resource where ($resource/Profile/Name eq '"+resourceName+"')" +
				" and ($resource/Scopes/Scope eq '"+scope.toString()+"') return $resource";

		Query q = new QueryBox(queryString);

		DiscoveryClient<String> client = client();
	 	List<String> appProfile = client.submit(q);

	 	if (appProfile == null || appProfile.size() == 0)
			throw new Exception("Your generic resource "+resourceName+" is not registered in the infrastructure for scope "+scope.toString());
		else {
			String elem = appProfile.get(0);
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);

			List<String> currValue = helper.evaluate("/Resource/Profile/Body/text()");

			if(currValue==null || currValue.isEmpty()){
				throw new Exception("Your generic resource "+resourceName+" has not got <body>");
			}

			return currValue.get(0);//body value
		}
	}

	/**
	 * Retrieve gis parameters.
	 *
	 * @param scope the scope
	 * @return the gis viewer service parameters
	 * @throws Exception the exception
	 */
	protected GisViewerServiceParameters retrieveGisParameters(ScopeBean scope) throws Exception
	{
		GisViewerServiceParameters gisViewerServiceParameters = new GisViewerServiceParameters();
		gisViewerServiceParameters.setScope(scope.toString());

		try {
			ServerParameters geoServerParameters = getParameters(scope, GEOSERVER_RESOURCE_NAME);
			gisViewerServiceParameters.setGeoServerUrl(geoServerParameters.getUrl());
			gisViewerServiceParameters.setGeoServerUser(geoServerParameters.getUser());
			gisViewerServiceParameters.setGeoServerPwd(geoServerParameters.getPassword());
		} catch (Exception e)
		{
			logger.error("Error retrieving the GeoServer parameters", e);
			throw new Exception("Error retrieving the GeoServer parameters", e);
		}

		try {
			ServerParameters geoNetworkParameters = getParameters(scope, GEONETWORK_RESOURCE_NAME);
			gisViewerServiceParameters.setGeoNetworkUrl(geoNetworkParameters.getUrl());
			gisViewerServiceParameters.setGeoNetworkUser(geoNetworkParameters.getUser());
			gisViewerServiceParameters.setGeoNetworkPwd(geoNetworkParameters.getPassword());
		} catch (Exception e)
		{
			logger.error("Error retrieving the GeoNetwork parameters", e);
			throw new Exception("Error retrieving the GeoNetwork parameters", e);
		}

		try {

			RuntimeResourceReader resolver = new RuntimeResourceReader(scope.toString(), TRANSECT_RESOURCE_NAME);
			gisViewerServiceParameters.setTransectUrl(resolver.getServiceBaseURI());

			//READ FROM GENERIC RESOURCE
//			String transectUrl = getTransectUrl(scope, TRANSECT_RESOURCE_NAME);
//			gisViewerServiceParameters.setTransectUrl(transectUrl);
		} catch (Exception e){
			logger.warn("Error retrieving the resource (by name): "+TRANSECT_RESOURCE_NAME, e);
			//throw new Exception("Error retrieving the Transect url", e);
		}

		try {

			String infra = scope.toString();
			if(!scope.is(Type.INFRASTRUCTURE)){
				String[] split = scope.toString().split(SCOPE_SEPARATOR);
				if(infra.startsWith(SCOPE_SEPARATOR))
					infra = SCOPE_SEPARATOR+split[1];
				else
					infra = SCOPE_SEPARATOR+split[0];

				logger.info("Getting infra from scope "+infra);
			}

			RuntimeResourceReader resolver = new RuntimeResourceReader(infra, DATAMINER_RESOURCE_NAME);
			gisViewerServiceParameters.setDataMinerUrl(resolver.getServiceBaseURI());
		} catch (Exception e){
			logger.warn("Error retrieving the resource (by name): "+DATAMINER_RESOURCE_NAME, e);
		}

		logger.info("retrieved parameters: "+gisViewerServiceParameters);

		return gisViewerServiceParameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GisViewerServiceParameters getParameters() throws Exception {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		String scope = session.getScope();

		GisViewerServiceParameters parameters = parametersCache.get(scope);

		if (parameters == null) {
			parameters = retrieveGisParameters(new ScopeBean(scope));
//			parameters.setScope(scope);
			parametersCache.put(scope, parameters);
		}
		logger.info("returing GisViewerServiceParameters: "+parameters);
		return parameters;
	}

	/**
	 * Refresh parameters cache.
	 */
	protected void refreshParametersCache()
	{
		for (String scope:parametersCache.keySet()) {
			try{
				GisViewerServiceParameters parameters = retrieveGisParameters(new ScopeBean(scope));
				parametersCache.put(scope, parameters);
			} catch (Exception e) {
				logger.warn("An error occured retrieving gis parameters for scope "+scope, e);
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl#getBaseLayersToGisViewer()
	 */
	public List<? extends GisViewerBaseLayerInterface> getBaseLayersToGisViewer(){
		logger.trace("Retrieving BaseLayersToGisViewer...");
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		String scope = session.getScope();
		try {
			GCGisViewerBaseLayersAPR reader = new GCGisViewerBaseLayersAPR(new ScopeBean(scope));
			return reader.getListGisViewerBaseLayer();
		} catch (Exception e) {
			logger.error("An error occured retrieving gis viewer base layers form GR, scope:"+scope, e);
			return new ArrayList<GisViewerBaseLayerInterface>(1);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl#getGeoCaller()
	 */
//	@Override
//	protected GeoCaller getGeoCaller() throws Exception
//	{
//		HttpSession httpSession = this.getThreadLocalRequest().getSession();
//		ASLSession session = getASLSession(httpSession);
//		GeoCaller geoCaller = (GeoCaller) session.getAttribute(GEOCALLER_ATTRIBUTE_NAME);
//		if (geoCaller==null) {
//			try {
//
//				GisViewerServiceParameters parameters = getParameters();
//				String geoserverUrl = parameters.getGeoServerUrl();
//				String geonetworkUrl = parameters.getGeoNetworkUrl();
//				String gnUser = parameters.getGeoNetworkUser();
//				String gnPwd = parameters.getGeoNetworkPwd();
//				String gsUser = parameters.getGeoServerUser();
//				String gsPwd = parameters.getGeoServerPwd();
//
//				geoCaller = new GeoCaller(geonetworkUrl, gnUser, gnPwd, geoserverUrl, gsUser, gsPwd, researchMethod);
//				session.setAttribute(GEOCALLER_ATTRIBUTE_NAME, geoCaller);
//
//			} catch (Exception e) {
//				throw new Exception("Error initializing the GeoCaller", e);
//			}
//		}
//
//		return geoCaller;
//	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveLayerItem(String name, String mimeType, String url, String destinationFolderId) throws Exception {
		logger.trace("saveLayerItem name: "+name+" mimeType: "+mimeType+" url: "+url+" destinationFolderId: "+destinationFolderId);

		try{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);

			ScopeProvider.instance.set(session.getScope());
			Workspace wa= HomeLibrary.getUserWorkspace(session.getUsername());

			WorkspaceFolder destinationFolder = (WorkspaceFolder) wa.getItem(destinationFolderId);

			URL urlObject = new URL(url);
			URLConnection connection = urlObject.openConnection();
			connection.connect();
			name = WorkspaceUtil.getUniqueName(name, destinationFolder);
			InputStream is = connection.getInputStream();

			//workaround for a bug in the HomeLibrary
			if ("image/svg+xml".equals(mimeType)) destinationFolder.createExternalFileItem(name, "", mimeType, is);
			else WorkspaceUtil.createExternalFile(destinationFolder, name, "", mimeType, is);
		} catch (MalformedURLException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to retrieve layer data");
		} catch (IOException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to load layer data");
		} catch (ItemNotFoundException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to save layer data. Layer data not found");
		} catch (InternalErrorException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to save layer data. Unexpected Exception occurred. Try again or notify to administrator.");
		} catch (Exception e){
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to save layer. Unexpected Exception occurred. Try again or notify to administrator.");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gcubegisviewer.client.GCubeGisViewerService#saveMapImageItem(java.lang.String, java.lang.String, java.util.Map, java.lang.String)
	 */
	@Override
	public void saveMapImageItem(String name, String mimeType, Map<String, String> parameters, String destinationFolderId) throws GCubeGisViewerServiceException {

		try{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			ASLSession session = getASLSession(httpSession);
			Workspace wa= HomeLibrary.getUserWorkspace(session.getUsername());
			WorkspaceFolder destinationFolder = (WorkspaceFolder) wa.getItem(destinationFolderId);

			BufferedImage imgRis = MapGeneratorUtils.createMapImage(mimeType, parameters);

			name = WorkspaceUtil.getUniqueName(name, destinationFolder);

			File tmp = File.createTempFile("GCube", ".img");
			ImageIO.write(imgRis, MapGeneratorUtils.getOutputExtension(mimeType), tmp);

			InputStream is = new FileInputStream(tmp);

			//workaround for a bug in the HomeLibrary
			if ("image/svg+xml".equals(mimeType)) destinationFolder.createExternalFileItem(name, "", mimeType, is);
			else WorkspaceUtil.createExternalFile(destinationFolder, name, "", mimeType, is);

//			tmp.delete();
			System.out.println(tmp.getAbsolutePath());

		} catch (MalformedURLException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to retrieve layer data");
		} catch (IOException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to load layer data");
		} catch (ItemNotFoundException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to save layer data. Layer data not found");
		} catch (InternalErrorException e) {
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to save layer data. Unexpected Exception occurred. Try again or notify to administrator.");
		} catch (Exception e){
			logger.error("", e);
			throw new GCubeGisViewerServiceException("Sorry, unable to save layer. Unexpected Exception occurred. Try again or notify to administrator.");
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl#getBaseLayersToAddGisViewer()
	 */
	@Override
	public String getGcubeSecurityToken() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession session = getASLSession(httpSession);
		logger.debug("Get security token return: "+session.getSecurityToken());

		if(session.getSecurityToken()==null || session.getSecurityToken().isEmpty()){
			logger.error("Security token retured from ASL is null or empty!!!");
			return null;
		}

		return session.getSecurityToken();
	}


	/**
	 * Temporary method to set the authorization token
	 *
	 * @param session the new authorization token
	 */
	/*private static void setAuthorizationToken(ASLSession session) {
		String username = session.getUsername();
		String scope = session.getScope();
		ScopeProvider.instance.set(scope);
		logger.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<String>();
		userRoles.add(DEFAULT_ROLE);
		session.setSecurityToken(null);
		String token = authorizationService().build().generate(session.getUsername(), userRoles);
		logger.debug("received token: "+token);
		session.setSecurityToken(token);
		logger.info("Security token set in session for: "+username + " on " + scope);
	}*/


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl#getBaseLayersToAddGisViewer()
	 */
	@Override
	protected List<? extends GisViewerBaseLayerInterface> getBaseLayersToAddGisViewer() throws Exception {
		return getBaseLayersToGisViewer();
	}


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception{

		String DEFAULT_SCOPE = "/gcube/devsec/devVRE";

		GCubeGisViewerServletImpl impl = new GCubeGisViewerServletImpl();
		GisViewerServiceParameters parameters = impl.retrieveGisParameters(new ScopeBean(DEFAULT_SCOPE));
		System.out.println(parameters);

	}
}
