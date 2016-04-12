package org.gcube.portlets.user.geoexplorer.server;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.util.HttpSessionUtil;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class GeoExplorerServiceParameters implements GeoExplorerServiceInterface{
	
	protected static Logger logger = Logger.getLogger(GeoExplorerServiceParameters.class);
	
	protected GeonetworkInstance geonetworkInstance;
	
	protected boolean isValidGeoInstance = true;
	
	protected String geonetworkUrl = null;

	protected String geonetworkUser;

	protected String geonetworkPwd;

	protected String geoserverUrl;

	protected String geoserverUser;

	protected String geoserverPwd;

	protected String scope;
	
	public GeoExplorerServiceParameters()
	{}

	/**
	 * @param geoServerUrl
	 * @param geoServerUser
	 * @param geoServerPwd
	 * @param geoNetworkUrl
	 * @param geoNetworkUser
	 * @param geoNetworkPwd
	 * @param transectUrl
	 */
	public GeoExplorerServiceParameters(GeonetworkInstance geonetworkInstance) {
		logger.trace("Initializing the GeoExplorerServiceParameters...with geonetworkInstance "+geonetworkInstance);
		this.geonetworkInstance = geonetworkInstance;
	}

	/**
	 * @return the geonetworkInstance
	 */
	public GeonetworkInstance getGeonetworkInstance() {
		return geonetworkInstance;
	}
	
	/**
	 * @param geonetworkInstance 
	 * @return
	 */
	public GeonetworkInstance getGeonetworkInstance(HttpSession httpSession, String scope) throws Exception {

		GeonetworkInstance gn = (GeonetworkInstance) HttpSessionUtil.getGeonetworkInstance(httpSession,scope);
		logger.trace("Get GeonetworkInstance in scope : "+scope);
		logger.trace("Currently GeonetworkInstance stored into HttpSession is: "+gn);

		if (gn == null){
			logger.trace("HttpSession GeonetworkInstance is null");
			logger.trace("Object GeonetworkInstance is "+geonetworkInstance);
			if(geonetworkInstance==null){
				logger.trace("Object GeonetworkInstance is null... instancing");
				geonetworkInstance = instanceGeonetwork(httpSession);
			}
			gn = geonetworkInstance;
		}
		
		logger.trace("Updating HttpSession variables...");
		
		if(HttpSessionUtil.getGeonetworkInstance(httpSession, scope)==null){
			logger.trace("Setting the GeonetworkInstance... at http session id: "+httpSession.getId() + " and scope: "+scope);
			HttpSessionUtil.setGeonetorkInstance(httpSession, gn, scope);
		}else
			logger.trace("The GeonetworkInstance at http session id: "+httpSession.getId() +" and scope: "+scope +" is not null");
		
		if(HttpSessionUtil.getScopeInstance(httpSession)==null || HttpSessionUtil.isScopeChanged(httpSession, scope)){
			logger.trace("Setting the ScopeInstance... at http session id: "+httpSession.getId() + "as: "+scope);
			HttpSessionUtil.setScopeInstance(httpSession, scope);
		}else
			logger.trace("The ScopeInstance at http session id: "+httpSession.getId() +" and scope: "+scope +" is not null");
		
		logger.trace("GeonetworkInstance using geonetwork: " +gn.getGeoNetworkUrl());
		
		logger.trace("End Updating HttpSession variables");
		
		return gn;
	}

	private GeonetworkInstance instanceGeonetwork(HttpSession httpSession) throws Exception{
	
		GeonetworkInstance gn;
		logger.trace("geonetworkUrl is null? "+(geonetworkUrl==null));
		if(geonetworkUrl!=null)
			gn = new GeonetworkInstance(scope, geonetworkUrl, geonetworkUser, geonetworkPwd, geoserverUrl, geoserverUser, geoserverPwd, httpSession);
		else
			gn = new GeonetworkInstance(true, httpSession);
		
		return gn;
		
	}

	/**
	 * 
	 */
	public void updateGeonetworkInstance(HttpSession httpSession, String scope) throws Exception {
		logger.trace("updating Geonetwork instance at new http session: "+httpSession.getId());
		GeonetworkInstance gn = instanceGeonetwork(httpSession);
		HttpSessionUtil.setGeonetorkInstance(httpSession, gn, scope);
		isValidGeoInstance = true;
	}

	public boolean isValidGeoInstance() {
		return isValidGeoInstance;
	}

	public void setValidGeoInstance(boolean isValid) {
		this.isValidGeoInstance = isValid;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setGeoNetworkUrl(java.lang.String)
	 */
	@Override
	public void setGeoNetworkUrl(String url) {
		this.geonetworkUrl = url;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setGeoNetworkUser(java.lang.String)
	 */
	@Override
	public void setGeoNetworkUser(String user) {
		this.geonetworkUser = user;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setGeoNetworkPwd(java.lang.String)
	 */
	@Override
	public void setGeoNetworkPwd(String password) {
		this.geonetworkPwd = password;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setGeoServerUrl(java.lang.String)
	 */
	@Override
	public void setGeoServerUrl(String url) {
		this.geoserverUrl = url;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setGeoServerUser(java.lang.String)
	 */
	@Override
	public void setGeoServerUser(String user) {
		this.geoserverUser = user;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setGeoServerPwd(java.lang.String)
	 */
	@Override
	public void setGeoServerPwd(String password) {
		this.geoserverPwd = password;
		
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#setScope(java.lang.String)
	 */
	@Override
	public void setScope(String scope) {
		this.scope = scope;
		
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceInterface#getScope()
	 */
	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoExplorerServiceParameters [geonetworkInstance=");
		builder.append(geonetworkInstance);
		builder.append(", isValidGeoInstance=");
		builder.append(isValidGeoInstance);
		builder.append(", geonetworkUrl=");
		builder.append(geonetworkUrl);
		builder.append(", geonetworkUser=");
		builder.append(geonetworkUser);
		builder.append(", geonetworkPwd=");
		builder.append(geonetworkPwd);
		builder.append(", geoserverUrl=");
		builder.append(geoserverUrl);
		builder.append(", geoserverUser=");
		builder.append(geoserverUser);
		builder.append(", geoserverPwd=");
		builder.append(geoserverPwd);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}
}
