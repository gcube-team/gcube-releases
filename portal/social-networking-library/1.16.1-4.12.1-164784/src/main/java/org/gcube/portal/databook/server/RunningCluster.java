package org.gcube.portal.databook.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.ex.TooManyRunningClustersException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 0.1 Dec 2012
 *
 */
@SuppressWarnings("serial")
public class RunningCluster implements Serializable {
	/**
	 * logger
	 */
	private static final Logger _log = LoggerFactory.getLogger(RunningCluster.class);

	/**
	 * properties to read
	 */
	private static final String HOST_PROPERTY = "host";	
	private static final String HOST_PORT_PROPERTY = "port";
	private static final String CLUSTER_NAME_PROPERTY = "cluster";	
	private static final String KEY_SPACE_NAME_PROPERTY = "keyspace";
	/**
	 * other constants
	 */
	private final static String RUNTIME_RESOURCE_NAME = "SocialPortalDataStore";
	private final static String PLATFORM_NAME = "Cassandra";

	private static final String DEFAULT_CONFIGURATION = "/org/gcube/portal/databook/server/resources/databook.properties";

	private static RunningCluster singleton;
	/**
	 * Host
	 */
	private String host;
	/**
	 * Cluster Name 
	 */
	private String clusterName;
	/**
	 * Keyspace Name
	 */
	private String keyspaceName;

	/**
	 * @param infrastructureName could be null
	 * @return an instance of the RunningCluster
	 */
	public static synchronized RunningCluster getInstance(String infrastructureName) {
		if (singleton == null) {
			singleton = new RunningCluster(infrastructureName);
		}
		return singleton;
	}	
	/**
	 * private constructor
	 */
	private RunningCluster(String infrastructureName) {
		try {
			List<ServiceEndpoint> resources = getConfigurationFromIS(infrastructureName);
			if (resources.size() > 1) {
				_log.error("Too many Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" in this scope ");
				throw new TooManyRunningClustersException("There exist more than 1 Runtime Resource in this scope having name " 
						+ RUNTIME_RESOURCE_NAME + " and Platform " + PLATFORM_NAME + ". Only one allowed per infrasrtucture.");
			}
			else if (resources.size() == 0){
				_log.error("There is no Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" and Platform " + PLATFORM_NAME + " in this scope. Using default configuration properties: " + DEFAULT_CONFIGURATION);
				loadDefaultConfiguration();
			}
			else {
				for (ServiceEndpoint res : resources) {
					AccessPoint found = res.profile().accessPoints().iterator().next();
					host = found.address();
					clusterName = found.description();
					keyspaceName = found.name();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return the
	 * @throws Exception
	 */
	private List<ServiceEndpoint> getConfigurationFromIS(String infrastructureName) throws Exception  {
		_log.debug("getConfigurationFromIS infrastructureName="+infrastructureName );
		String scope = "/";
		if(infrastructureName != null && !infrastructureName.isEmpty())
			scope += infrastructureName;
		else {
			scope += readInfrastructureName();
			_log.debug("infrastrucute name is null, setting root scope=" + scope);
		}
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ PLATFORM_NAME +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}		

	private String readInfrastructureName() {
		Properties props = new Properties();
		try {
			StringBuilder sb = new StringBuilder(getCatalinaHome());
			sb.append(File.separator)
			.append(PortalContext.CONFIGURATION_FOLDER)
			.append(File.separator)
			.append(PortalContext.INFRA_PROPERTY_FILENAME);
			String propertyfile = sb.toString();
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			return props.getProperty(GCubePortalConstants.INFRASTRUCTURE_NAME);
		}
		catch(IOException e) {
			_log.error("infrastructure.properties file not found under $CATALINA_HOME/conf/ dir, setting default infrastructure Name " + "gcube");
			return "gcube";
		}		
	}


	/**
	 * 
	 */
	private void loadDefaultConfiguration() {
		Properties props = new Properties();
		try {
			props.load(CassandraClusterConnection.class.getResourceAsStream(DEFAULT_CONFIGURATION));
			host = props.getProperty(HOST_PROPERTY) + ":" + props.getProperty(HOST_PORT_PROPERTY);
			clusterName = props.getProperty(CLUSTER_NAME_PROPERTY);
			keyspaceName = props.getProperty(KEY_SPACE_NAME_PROPERTY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getKeyspaceName() {
		return keyspaceName;
	}

	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}

	@Override
	public String toString() {
		return "RunningCluster [host=" + host + ", clusterName=" + clusterName
				+ ", keyspaceName=" + keyspaceName + "]";
	}
	/**
	 * 
	 * @return $CATALINA_HOME
	 */
	private static String getCatalinaHome() {
		return (System.getenv("CATALINA_HOME").endsWith("/") ? System.getenv("CATALINA_HOME") : System.getenv("CATALINA_HOME")+"/");
	}
}
