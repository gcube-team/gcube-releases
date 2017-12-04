package org.gcube.portlets.user.gisviewer.server.gisconfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.portlets.user.gisviewer.server.exception.PropertyFileNotFoundException;

/**
 * The Class GisConfigurationPropertyReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 28, 2015
 */
public class GisConfigurationPropertyReader {

	public static final String GIS_CONFIGURATION_FILE = "gisconfiguration.properties";

	public static final String GEONETWORK_URL = "GEONETWORK_URL";
	public static final String GEONETWORK_USER = "GEONETWORK_USER";
	public static final String GEONETWORK_PWD = "GEONETWORK_PWD";

	public static final String GEOSERVER_URL = "GEOSERVER_URL";
	public static final String GEOSERVER_USER = "GEOSERVER_USER";
	public static final String GEOSERVER_PWD = "GEOSERVER_PWD";

	public static final String TRANSECT_URL = "TRANSECT_URL"; // ex:
																// "http://portal.d4science.research-infrastructures.eu/chartview/";
	public static final String SCOPE = "SCOPE";

	public static final String DATAMINER_URL = "DATAMINER_URL";

	private String geoNetworkUrl = "http://geonetwork.d4science.org/geonetwork";
	private String geoNetworkUser = "admin";
	private String geoNetworkPwd = "6ibwdpJ1IkPHPYMfxSKZg==";

	private String geoServerUrl = "http://geoserver.d4science.org/geoserver";
	private String geoServerUser = "admin";
	private String geoServerPwd = "HA2kz7mReOnq9pT3bGNMsQ==";

	private String transectUrl = "http://monitor.d4science.research-infrastructures.eu/transect/";
	private String scope = "/d4science.research-infrastructures.eu/gCubeApps";

	private boolean readPropertiesFromFile;

	private String dataMinerUrl;

	public static Logger logger = Logger.getLogger(GisConfigurationPropertyReader.class);

	/**
	 * Instantiates a new gis configuration property reader.
	 *
	 * @throws PropertyFileNotFoundException
	 *             the property file not found exception
	 */
	public GisConfigurationPropertyReader(boolean readPropertyFromFile) throws PropertyFileNotFoundException {
		this.readPropertiesFromFile = readPropertyFromFile;

		if(readPropertyFromFile)
			readProperties();
	}

	public void readProperties() throws PropertyFileNotFoundException{

		Properties prop = new Properties();

		try {

			InputStream in = GisConfigurationPropertyReader.class.getResourceAsStream(GIS_CONFIGURATION_FILE);

			// load a properties file
			prop.load(in);

			this.geoNetworkUrl = prop.getProperty(GEONETWORK_URL);
			this.geoNetworkUser = prop.getProperty(GEONETWORK_USER);
			this.geoNetworkPwd = prop.getProperty(GEONETWORK_PWD);
			logger.info("geoNetworkPwd: "+geoNetworkPwd +" tentative to descypt...");
			String decryptedPassword;

			this.scope = prop.getProperty(SCOPE);

			this.dataMinerUrl = prop.getProperty(DATAMINER_URL);

//			ScopeProvider.instance.set(scope.toString());
			try {
//				decryptedPassword = StringEncrypter.getEncrypter().decrypt(geoNetworkPwd);
				decryptedPassword = StringEncrypter.getEncrypter().decrypt(geoNetworkPwd);
				logger.info("geoNetworkPwd decrypted pwd: "+decryptedPassword);
				this.geoNetworkPwd = decryptedPassword;

			} catch (Exception e) {
				logger.warn("An error occurred during decripting geoNetworkPwd  " + e, e);
			}

			this.geoServerUrl = prop.getProperty(GEOSERVER_URL);
			this.geoServerUser = prop.getProperty(GEOSERVER_USER);
			this.geoServerPwd = prop.getProperty(GEOSERVER_PWD);
			logger.info("geoServerPwd: "+geoServerPwd +" tentative to descypt...");

			try {
				decryptedPassword = StringEncrypter.getEncrypter().decrypt(geoServerPwd);
				logger.info("geoServerPwd decrypted pwd: "+decryptedPassword);
				this.geoServerPwd = decryptedPassword;

			} catch (Exception e) {
				logger.warn("An error occurred during decripting geoServerPwd  " + e, e);
			}

			this.transectUrl = prop.getProperty(TRANSECT_URL);

			logger.info("Read parameters: "+this.toString());

		} catch (IOException e) {
			logger.error("An error occurred on read property file " + e, e);
			throw new PropertyFileNotFoundException(
					"An error occurred on read property file " + e);
		}
	}




	/**
	 * @param dataMinerUrl the dataMinerUrl to set
	 */
	public void setDataMinerUrl(String dataMinerUrl) {

		this.dataMinerUrl = dataMinerUrl;
	}

	/**
	 * @return the dataMinerUrl
	 */
	public String getDataMinerUrl() {

		return dataMinerUrl;
	}

	/**
	 * @return the readPropertiesFromFile
	 */
	public boolean isReadPropertiesFromFile() {
		return readPropertiesFromFile;
	}
	/**
	 * @return the geoNetworkUrl
	 */
	public String getGeoNetworkUrl() {
		return geoNetworkUrl;
	}

	/**
	 * @return the geoNetworkUser
	 */
	public String getGeoNetworkUser() {
		return geoNetworkUser;
	}

	/**
	 * @return the geoNetworkPwd
	 */
	public String getGeoNetworkPwd() {
		return geoNetworkPwd;
	}

	/**
	 * @return the geoServerUrl
	 */
	public String getGeoServerUrl() {
		return geoServerUrl;
	}

	/**
	 * @return the geoServerUser
	 */
	public String getGeoServerUser() {
		return geoServerUser;
	}

	/**
	 * @return the geoServerPwd
	 */
	public String getGeoServerPwd() {
		return geoServerPwd;
	}

	/**
	 * @return the transectUrl
	 */
	public String getTransectUrl() {
		return transectUrl;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param geoNetworkUrl
	 *            the geoNetworkUrl to set
	 */
	public void setGeoNetworkUrl(String geoNetworkUrl) {
		this.geoNetworkUrl = geoNetworkUrl;
	}

	/**
	 * @param geoNetworkUser
	 *            the geoNetworkUser to set
	 */
	public void setGeoNetworkUser(String geoNetworkUser) {
		this.geoNetworkUser = geoNetworkUser;
	}

	/**
	 * @param geoNetworkPwd
	 *            the geoNetworkPwd to set
	 */
	public void setGeoNetworkPwd(String geoNetworkPwd) {
		this.geoNetworkPwd = geoNetworkPwd;
	}

	/**
	 * @param geoServerUrl
	 *            the geoServerUrl to set
	 */
	public void setGeoServerUrl(String geoServerUrl) {
		this.geoServerUrl = geoServerUrl;
	}

	/**
	 * @param geoServerUser
	 *            the geoServerUser to set
	 */
	public void setGeoServerUser(String geoServerUser) {
		this.geoServerUser = geoServerUser;
	}

	/**
	 * @param geoServerPwd
	 *            the geoServerPwd to set
	 */
	public void setGeoServerPwd(String geoServerPwd) {
		this.geoServerPwd = geoServerPwd;
	}

	/**
	 * @param transectUrl
	 *            the transectUrl to set
	 */
	public void setTransectUrl(String transectUrl) {
		this.transectUrl = transectUrl;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GisConfigurationPropertyReader [geoNetworkUrl=");
		builder.append(geoNetworkUrl);
		builder.append(", geoNetworkUser=");
		builder.append(geoNetworkUser);
		builder.append(", geoNetworkPwd=");
		builder.append(geoNetworkPwd);
		builder.append(", geoServerUrl=");
		builder.append(geoServerUrl);
		builder.append(", geoServerUser=");
		builder.append(geoServerUser);
		builder.append(", geoServerPwd=");
		builder.append(geoServerPwd);
		builder.append(", transectUrl=");
		builder.append(transectUrl);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", readPropertiesFromFile=");
		builder.append(readPropertiesFromFile);
		builder.append(", dataMinerUrl=");
		builder.append(dataMinerUrl);
		builder.append("]");
		return builder.toString();
	}

	//TEST
	public static void main(String[] args) {
		GisConfigurationPropertyReader gr;
		try {
//			ScopeProvider.instance.set(DEFAULT_SCOPE);
			gr = new GisConfigurationPropertyReader(true);
			System.out.println(gr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
