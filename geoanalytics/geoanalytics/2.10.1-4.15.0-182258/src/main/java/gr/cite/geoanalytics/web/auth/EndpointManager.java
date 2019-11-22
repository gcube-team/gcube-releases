package gr.cite.geoanalytics.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EndpointManager {

    private static Map<String, Map<ServiceProfile, Set<String>>> serviceEndpoints = new HashMap<>();

    private static Object serviceLock = new Object();

    private static long REFRESH_INTERVAL; // Minutes

    private static Date lastUpdated = new Date();

    private static Logger logger = LoggerFactory.getLogger(EndpointManager.class);

    public EndpointManager() {

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream("endpoint.manager.configuration.properties");
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

    private void discoverServiceEndpoints(String scope, ServiceProfile serviceProfile) throws ServiceDiscoveryException {
        logger.info("Discovering service endpoints for scope " + scope);

        Set<String> endpoints = ServiceDiscovery.discoverServiceEndpoints(scope, serviceProfile);
        serviceEndpoints.get(scope).put(serviceProfile, endpoints);

        logger.info("Discovered " + endpoints.size() + " service endpoints for scope " + scope);
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