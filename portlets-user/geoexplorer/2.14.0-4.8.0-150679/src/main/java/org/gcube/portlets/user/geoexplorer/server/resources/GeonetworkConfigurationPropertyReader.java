package org.gcube.portlets.user.geoexplorer.server.resources;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.beans.PropertyFileNotFoundException;
import org.gcube.portlets.user.geoexplorer.server.service.DatabaseServiceException;
import org.gcube.portlets.user.geoexplorer.server.service.dao.DaoManager;
import org.gcube.portlets.user.geoexplorer.server.service.dao.GeoParametersPersistence;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters.RESOURCETYPE;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class GeonetworkConfigurationPropertyReader {

	protected static final String GEOSERVER = "GEOSERVER";
	protected static final String GEONETWORK = "GEONETWORK";
	
	private String geoserverUrl = "";
	private String geoserverUser = "";
	private String geoserverPwd = "";
	
	private String geoNetworkUrl = "";
	private String geoNetworkUser = "";
	private String geoNetworkPwd = "";

	protected static Logger logger = Logger.getLogger(GeonetworkConfigurationPropertyReader.class);
	
	private GeoParametersPersistence geoPersistence;
	private String geoNetworkScope;
	private String geoserverScope;
	
	public GeonetworkConfigurationPropertyReader(String scope, HttpSession httpSession) throws PropertyFileNotFoundException {

		EntityManagerFactory factory = DaoManager.getEntityManagerFactoryForGeoParameters(scope, httpSession);
		geoPersistence = new GeoParametersPersistence(factory);
		loadProperties();
	}
	
	public void loadProperties() throws PropertyFileNotFoundException{

		try {
			if(geoPersistence==null)
				throw new Exception("Geo Persistence is null");
			
			GeoResourceParameters server;
			try {
				
				server = geoPersistence.getLastResourceType(GeoResourceParameters.RESOURCETYPE.GEONETWORK);
				
				this.geoNetworkUrl = server.getUrl();
				this.geoNetworkUser = server.getUser();
				this.geoNetworkPwd = server.getPassword();
				this.geoNetworkScope = server.getScope();


			} catch (DatabaseServiceException e) {
				logger.warn("GEONETWORK resources not found in DB GeoResourceParameters");
			}
			
			try {
				
				server = geoPersistence.getLastResourceType(GeoResourceParameters.RESOURCETYPE.GEOSERVER);
				
				this.geoserverUrl = server.getUrl();
				this.geoserverUser = server.getUser();
				this.geoserverPwd = server.getPassword();
				this.geoserverScope = server.getScope();


			} catch (DatabaseServiceException e) {
				logger.warn("GEOSERVER resources not found in DB GeoResourceParameters");
			}
				
		} catch (Exception e) {
			logger.error("An error occurred on reading property file",e);
			throw new PropertyFileNotFoundException("An error occurred on read property file "+e);
		}
		
	}
	
	
	/**
	 * Save Geonetwork properties
	 * @param geoNetworkUrl
	 * @param geoNetworkUser
	 * @param geoNetworkPwd
	 */
	public void saveGeonetworkProperties(String scope, String geoNetworkUrl, String geoNetworkUser, String geoNetworkPwd){
		logger.info("Saving GeonetworkProperties...");
		this.geoNetworkUrl = geoNetworkUrl;
		this.geoNetworkUser = geoNetworkUser;
		this.geoNetworkPwd = geoNetworkPwd;
		this.geoNetworkScope = scope;
		
		GeoResourceParameters server = new GeoResourceParameters(geoNetworkScope, geoNetworkUrl, geoNetworkUser, geoNetworkPwd, RESOURCETYPE.GEONETWORK);
		logger.info("Saving GeoResourceParameters: "+server);
		
		try {
			geoPersistence.insert(server);
		} catch (DatabaseServiceException e) {
			logger.error("An error occurred on writing "+server+ "on db",e);
		}

	}
	

	/**
	 * Save Geoserver properties
	 * @param geoNetworkUrl
	 * @param geoNetworkUser
	 * @param geoNetworkPwd
	 */
	public void saveGeoserverProperties(String scope, String geoserverUrl, String geoserverUser, String geoserverPwd){
		logger.info("Saving GeoserverProperties...");
		this.geoserverUrl = geoserverUrl;
		this.geoserverUser = geoserverUser;
		this.geoserverPwd = geoserverPwd;
		this.geoserverScope = scope;
		
		GeoResourceParameters server = new GeoResourceParameters(geoserverScope, geoserverUrl, geoserverUser, geoserverPwd, RESOURCETYPE.GEOSERVER);
		logger.info("Saving GeoResourceParameters: "+server);
		try {
			geoPersistence.insert(server);
		} catch (DatabaseServiceException e) {
			logger.trace("An error occurred on writing "+server+ "on db",e);
		}
		
	}
	

	public static void main(String[] args) {
		
		try {
			
			String testScope = Constants.defaultScope;
			
			GeonetworkConfigurationPropertyReader g = new GeonetworkConfigurationPropertyReader(testScope, null);

			System.out.println(g);

//			g.saveGeonetworkProperties();
//
//			System.out.println(g);
			
		} catch (PropertyFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getGeoserverUser() {
		return geoserverUser;
	}

	public void setGeoserverUser(String geoserverUser) {
		this.geoserverUser = geoserverUser;
	}

	public String getGeoserverPwd() {
		return geoserverPwd;
	}

	public void setGeoserverPwd(String geoserverPwd) {
		this.geoserverPwd = geoserverPwd;
	}

	public String getGeoNetworkUrl() {
		return geoNetworkUrl;
	}

	public void setGeoNetworkUrl(String geoNetworkUrl) {
		this.geoNetworkUrl = geoNetworkUrl;
	}

	public String getGeoNetworkUser() {
		return geoNetworkUser;
	}

	public void setGeoNetworkUser(String geoNetworkUser) {
		this.geoNetworkUser = geoNetworkUser;
	}

	public String getGeoNetworkPwd() {
		return geoNetworkPwd;
	}

	public void setGeoNetworkPwd(String geoNetworkPwd) {
		this.geoNetworkPwd = geoNetworkPwd;
	}

	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	public void setGeoserverUrl(String geoserverUrl) {
		this.geoserverUrl = geoserverUrl;
	}

	public String getGeonetworkScope() {
		return geoNetworkScope;
	}

	public void setGeonetworkScope(String geonetworkScope) {
		this.geoNetworkScope = geonetworkScope;
	}

	public String getGeoserverScope() {
		return geoserverScope;
	}

	public void setGeoserverScope(String geoserverScope) {
		this.geoserverScope = geoserverScope;
	}

	public GeoParametersPersistence getGeoPersistence() {
		return geoPersistence;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkConfigurationPropertyReader [geoserverUrl=");
		builder.append(geoserverUrl);
		builder.append(", geoserverUser=");
		builder.append(geoserverUser);
		builder.append(", geoserverPwd=");
		builder.append(geoserverPwd);
		builder.append(", geoNetworkUrl=");
		builder.append(geoNetworkUrl);
		builder.append(", geoNetworkUser=");
		builder.append(geoNetworkUser);
		builder.append(", geoNetworkPwd=");
		builder.append(geoNetworkPwd);
		builder.append(", geoPersistence=");
		builder.append(geoPersistence);
		builder.append(", geonetworkScope=");
		builder.append(geoNetworkScope);
		builder.append(", geoserverScope=");
		builder.append(geoserverScope);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
