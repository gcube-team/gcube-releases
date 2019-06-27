package gr.cite.bluebridge.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.bluebridge.endpoint.DatabaseCredentials;
import gr.cite.bluebridge.endpoint.DatabaseDiscovery;
import gr.cite.bluebridge.endpoint.ServiceDiscovery;
import gr.cite.bluebridge.endpoint.ServiceProfile;
import gr.cite.bluebridge.endpoint.exceptions.DatabaseDiscoveryException;
import gr.cite.bluebridge.endpoint.exceptions.ServiceDiscoveryException;

public class EndpointManager {

	private static Map<String, Map<DatabaseProfile, DatabaseCredentials>> databaseEndpoints = new HashMap<>();
	private static Map<String, Map<ServiceProfile, Set<String>>> serviceEndpoints = new HashMap<>();

	private static Object databaseLock = new Object();
	private static Object serviceLock = new Object();

	private static long REFRESH_INTERVAL; // Minutes

	private static Date lastUpdated = new Date();

	private static Logger logger = LoggerFactory.getLogger(EndpointManager.class);

	public EndpointManager() {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
			prop.load(input);
			REFRESH_INTERVAL = Long.parseLong((String) prop.get("cache.refresh.interval"));
		} catch (IOException ex) {
			logger.error("Could not load properties file");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("Could not close properties file");
				}
			}
		}

		logger.info("Initiliazing Endpoint Manager with refresh interval " + REFRESH_INTERVAL + " minutes");
	}

	@SuppressWarnings("unused")
	private void refreshDatabaseEndpoints() {
		synchronized (databaseLock) {
			for (String scope : databaseEndpoints.keySet()) {
				logger.info("Refreshing " + scope + " database endpoints");
				for (DatabaseProfile databaseProfile : databaseEndpoints.get(scope).keySet()) {
					try {
						discoverDatabaseEndpoints(scope, databaseProfile);
					} catch (Exception e) {
						logger.error("Cannot database refresh endpoints: " + e);
					}
				}
			}
		}
	}

	private void discoverDatabaseEndpoints(String scope, DatabaseProfile databaseProfile) throws DatabaseDiscoveryException {
		logger.info("Discovering  database crendentials for scope " + scope);

		DatabaseCredentials databaseCredentials = DatabaseDiscovery.fetchDatabaseCredentials(scope, databaseProfile);
		databaseEndpoints.get(scope).put(databaseProfile, databaseCredentials);
	}

	private void discoverServiceEndpoints(String scope, ServiceProfile serviceProfile) throws ServiceDiscoveryException {
		logger.info("Discovering service endpoints for scope " + scope);

		Set<String> endpoints = ServiceDiscovery.discoverServiceEndpoints(scope, serviceProfile);
		serviceEndpoints.get(scope).put(serviceProfile, endpoints);

		logger.info("Discovered " + endpoints.size() + " service endpoints for scope " + scope);
	}

	public DatabaseCredentials getDatabaseEndpoint(String scope, DatabaseProfile databaseProfile) throws DatabaseDiscoveryException {
		DatabaseCredentials asynchronizedDatabaseCredentials;

		synchronized (databaseLock) {
			boolean scopeNotExists = !databaseEndpoints.containsKey(scope);
			boolean databaseNotExists = scopeNotExists ? true : !databaseEndpoints.get(scope).containsKey(databaseProfile);

			if (scopeNotExists) {
				databaseEndpoints.put(scope, new HashMap<DatabaseProfile, DatabaseCredentials>());
			}

			if (databaseNotExists) {
				discoverDatabaseEndpoints(scope, databaseProfile);
			}

			DatabaseCredentials databaseCredentials = databaseEndpoints.get(scope).get(databaseProfile);
			asynchronizedDatabaseCredentials = databaseCredentials.clone();
		}

		return asynchronizedDatabaseCredentials;
	}

	public List<String> getServiceEndpoints(String scope, ServiceProfile serviceProfile) throws ServiceDiscoveryException {
		List<String> asynchronizedEndpoints;

		synchronized (serviceLock) {
			boolean scopeNotExists = !serviceEndpoints.containsKey(scope);
			boolean serviceNotExists = scopeNotExists ? true : !serviceEndpoints.get(scope).containsKey(serviceProfile);
			boolean hasNotEndpoints = serviceNotExists ? true : serviceEndpoints.get(scope).get(serviceProfile).size() == 0;
			boolean needsRefresh = needsRefresh();

			if (scopeNotExists) {
				serviceEndpoints.put(scope, new HashMap<ServiceProfile, Set<String>>());
			}

			if (serviceNotExists || hasNotEndpoints || needsRefresh) {
				discoverServiceEndpoints(scope, serviceProfile);
			}

			Set<String> endpoints = serviceEndpoints.get(scope).get(serviceProfile);
			asynchronizedEndpoints = new ArrayList<String>(endpoints);
		}

		Collections.shuffle(asynchronizedEndpoints);

		logger.info("Returned " + asynchronizedEndpoints.size() + " service endpoint(s)");

		return asynchronizedEndpoints;
	}

	public void removeServiceEndpoint(String scope, ServiceProfile serviceProfile, String endpoint) {
		synchronized (serviceLock) {
			if (serviceEndpoints.get(scope).get(serviceProfile).contains(endpoint)) {
				logger.info("Removing endpoint " + endpoint + " from cache.");
				serviceEndpoints.get(scope).get(serviceProfile).remove(endpoint);
			}
		}
	}

	private boolean needsRefresh() {
		Date now = new Date();

		long duration = now.getTime() - lastUpdated.getTime();
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

		if (diffInMinutes > REFRESH_INTERVAL) {
			return true;
		}

		return false;
	}
}